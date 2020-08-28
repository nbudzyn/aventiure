package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.STORY_WEB;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
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

    // TODO Das Programm kann oben in der leiste jederzeit einen prozentsatz anzeigen, wie
    //  viele StoryNodes der benutzer schon erreicht hat.

    private final World world;
    protected final NarrationDao n;

    private final LocationSystem locationSystem;
    private final SpatialConnectionSystem spatialConnectionSystem;

    private final SCActionStepCountDao scActionStepCountDao;

    @NonNull
    private final Map<Story, StoryData> initialStoryDataMap;

    public StoryWebComp(final AvDatabase db,
                        final World world,
                        final LocationSystem locationSystem,
                        final SpatialConnectionSystem spatialConnectionSystem,
                        final Story... initialAktiveStories) {
        this(db, world, locationSystem, spatialConnectionSystem, asList(initialAktiveStories));
    }

    public StoryWebComp(final AvDatabase db,
                        final World world,
                        final LocationSystem locationSystem,
                        final SpatialConnectionSystem spatialConnectionSystem,
                        final Collection<Story> initialAktiveStories) {
        this(db, world, locationSystem, spatialConnectionSystem,
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
                         final World world,
                         final LocationSystem locationSystem,
                         final SpatialConnectionSystem spatialConnectionSystem,
                         final Map<Story, StoryData> initialStoryDataMap) {
        super(STORY_WEB, db.storyWebDao());
        n = db.narrationDao();

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

    public AvTimeSpan narrateAndDoHintActionIfAny() {
        @Nullable final IStoryNode storyNode = getStoryNodeForHintAction();

        if (storyNode == null) {
            return noTime();
        }

        final AvTimeSpan extraTimeElapsed = narrateAndDoHintAction(storyNode);

        getPcd().setLastHintActionStepCount(scActionStepCountDao.stepCount());
        // STORY Speichern, dass eine Hint Action durchgeführt wurde (lastHintActionStepCount)

        return extraTimeElapsed;
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

    private AvTimeSpan narrateAndDoHintAction(final IStoryNode storyNode) {
        // STORY Sinnvollen Tipp generieren o.Ä.

        // STORY Als Tipp werden Sätze erzeugt wie
        //  "Wann soll eigentlich das Schlossfest sein?",
        //  "Vielleicht hättest du doch die Kugel mitnehmen sollen?" o.Ä.
        //  Als Tipp für den Froschprinzen z.B. durch einen NSC
        //  ankündigen lassen: Im Königreich
        //  nebenan ist der Prinz
        //  verschwunden.
        //  Tipp für Rapunzel: Mutter sammelt im Wald Holz und klagt ihr Leid.
        //  Tipps könnten von der storyNode generiert werden, wenn ihre
        //  Voraussetzungen bereits gegeben sind.

        // STORY Jede IStoryNode könnte mehrere Hinweise erzeugen, aus denen
        //  der Narrator auswählen könnte, ganz normal mit addAlt().

        // STORY Statt eines Tipps könnte die storyNode auch eine neue Story starten
        //  oder die Welt anderweitig modifizieren, dass es für den Spieler
        //  wieder mehr zu erleben gibt.
        return n.addAlt(neuerSatz(PARAGRAPH,
                "Du hast das Gefühl, es gibt noch viel zu erleben",
                noTime())
                .beendet(PARAGRAPH));
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

