package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static java.util.Arrays.asList;

/**
 * Managet die Stories, d.h. die kleinen
 * Geschichten / Märchen, die der Spieler erlebt. Beginnt neue Stories, speichert den
 * Stand, erzeugt Tipps, wenn der Benutzer in einer Story nicht weiterkommt etc.
 */
public class StoryWebComp extends AbstractStatefulComponent<StoryWebPCD> {
    // TODO Es könnte einen extra Knopf geben, unter dem der benutzer in einem Baum (oder
    //  einem Graphen) sieht,
    //  welche StoryNodes er bisher erreicht hat. Die baumstruktur ordnet sich nach den
    //  Voraussetzungen der StoryNodes voneinander.

    private final AvDatabase db;
    private final World world;
    protected final Narrator n;

    private final LocationSystem locationSystem;
    private final SpatialConnectionSystem spatialConnectionSystem;

    private final SCActionStepCountDao scActionStepCountDao;

    @NonNull
    private final Map<Story, StoryData> initialStoryDataMap;

    public StoryWebComp(final AvDatabase db,
                        final Narrator n,
                        final World world,
                        final LocationSystem locationSystem,
                        final SpatialConnectionSystem spatialConnectionSystem,
                        final Story... initialAktiveStories) {
        this(db, n, world, locationSystem, spatialConnectionSystem, asList(initialAktiveStories));
    }

    public StoryWebComp(final AvDatabase db,
                        final Narrator n, final World world,
                        final LocationSystem locationSystem,
                        final SpatialConnectionSystem spatialConnectionSystem,
                        final Collection<Story> initialAktiveStories) {
        this(db, n, world, locationSystem, spatialConnectionSystem,
                toEmptyStoryDataMap(initialAktiveStories));
    }

    private static ImmutableMap<Story, StoryData> toEmptyStoryDataMap(
            final Collection<Story> stories) {
        final ImmutableMap.Builder<Story, StoryData> res =
                ImmutableMap.builder();

        for (final Story story : stories) {
            res.put(story, new StoryData(story, StoryData.State.AKTIV));
        }

        return res.build();
    }

    private StoryWebComp(final AvDatabase db,
                         final Narrator n, final World world,
                         final LocationSystem locationSystem,
                         final SpatialConnectionSystem spatialConnectionSystem,
                         final Map<Story, StoryData> initialStoryDataMap) {
        super(STORY_WEB, db.storyWebDao());
        this.db = db;

        this.n = n;

        scActionStepCountDao = db.scActionStepCountDao();
        this.world = world;
        this.locationSystem = locationSystem;
        this.spatialConnectionSystem = spatialConnectionSystem;
        this.initialStoryDataMap = initialStoryDataMap;
    }

    @Override
    @NonNull
    protected StoryWebPCD createInitialState() {
        return new StoryWebPCD(getGameObjectId(), initialStoryDataMap);
    }

    public void reachStoryNode(final IStoryNode storyNode) {
        getPcd().reachStoryNode(storyNode, scActionStepCountDao.stepCount());
    }

    public void narrateAndDoHintActionIfAny() {
        // FIXME Tipps: ab dem zb 3. Mal deutlichere Hinweise!

        // FIXME Screenshots vom ca. 11.10.2020 durchlesen
        //  - welche tipps kommen zu früh?
        //  - welche tipps brazöuchen mehr Abwechslung?
        //  - wo braucht es deutlichere stufe-2-Tipps?

        @Nullable final IStoryNode storyNode = getStoryNodeForHintAction();

        if (storyNode == null) {
            return;
        }

        // Nicht alle Geschichten sind von Anfang an "verfügbar", und manchmal
        // kann der Spieler sie auch nur bis zu einem bestimmten Punkt spielen.
        // Wenn aber häufig Tipps notwendig waren, der Spieler also trotz Tipps
        // nicht oder nur langsam weiterkommt, versuchen wir, eine solche Geschichte
        // "weiterzusetzen" (z.B. zu starten).
        // (Das wird wohl eher selten der Fall sein.)
        if (!Story.checkAndAdvanceAStoryIfAppropriate(db, n, world)) {
            // Das hier ist der Regelfall!
            storyNode.narrateAndDoHintAction(db, n, world);
        }

        getPcd().setLastHintActionStepCount(scActionStepCountDao.stepCount());
    }

    public int getScore() {
        return getPcd().getScore();
    }

    @Nullable
    private IStoryNode getStoryNodeForHintAction() {
        final ImmutableSet<IStoryNode> storyNodesForHintAction =
                getPcd().getStoryNodesForHintAction(scActionStepCountDao.stepCount());

        if (storyNodesForHintAction.isEmpty()) {
            return null;
        }

        return chooseBestForHintAction(storyNodesForHintAction);
    }

    @NonNull
    private IStoryNode chooseBestForHintAction(final ImmutableSet<IStoryNode> storyNodes) {
        checkArgument(!storyNodes.isEmpty(), "No story nodes");

        if (storyNodes.size() == 1) {
            return storyNodes.iterator().next();
        }

        // Wenn mehrere Story Nodes zur Auswahl stehen, dann
        // ist eine Story Node relevanter, bei dem der SC gerade in der Nähe steht
        // als ein Story Node, zu dem der Spieler erst hinlaufen muss.

        @Nullable IStoryNode res = null;
        AvTimeSpan minMovementTime = null;

        for (final IStoryNode storyNode : storyNodes) {
            @Nullable final AvTimeSpan movementTimeFromSCToNodeLocation =
                    movementTimeFromSCToNodeLocation(storyNode);
            if (movementTimeFromSCToNodeLocation != null &&
                    (res == null ||
                            minMovementTime.longerThan(movementTimeFromSCToNodeLocation))) {
                res = storyNode;
                minMovementTime = movementTimeFromSCToNodeLocation;
            }
        }

        if (res == null) {
            // Gar keine der Story Nodes ist gerade zu erreichen! Nehmen wir halt die erste.
            return storyNodes.iterator().next();
        }

        return res;
    }

    @Nullable
    private AvTimeSpan movementTimeFromSCToNodeLocation(final IStoryNode storyNode) {
        @Nullable final GameObjectId storyNodeLocationId = storyNode.getLocationId();
        if (storyNodeLocationId == null) {
            // Das Ereignis kann an verschiedenen Orten oder "praktisch überall"
            // auftreten.
            return noTime();
        }

        final ILocationGO storyNodeLocation = (ILocationGO) world.load(storyNodeLocationId);

        final ILocationGO upperMostStoryNodeLocation =
                locationSystem.getUpperMostLocation(storyNodeLocation);
        @Nullable final ILocationGO upperMostSCLocation =
                world.loadSC().locationComp().getUpperMostLocation();

        if (upperMostStoryNodeLocation.is(upperMostSCLocation)) {
            return noTime();
        }

        if (!(upperMostSCLocation instanceof ISpatiallyConnectedGO)) {
            // Der Benutzer ist sonstwo - wie soll er hinkommen?!
            return null;
        }

        return spatialConnectionSystem.findDistance(
                (ISpatiallyConnectedGO) upperMostSCLocation,
                upperMostStoryNodeLocation);
    }

}
