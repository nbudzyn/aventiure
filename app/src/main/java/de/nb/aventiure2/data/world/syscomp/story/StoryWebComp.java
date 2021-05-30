package de.nb.aventiure2.data.world.syscomp.story;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.wetter.*;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.story.impl.FroschkoenigStoryNode;
import de.nb.aventiure2.data.world.syscomp.story.impl.RapunzelStoryNode;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterData;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung;
import de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonner;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static java.util.Arrays.asList;

/**
 * Managet die Stories, d.h. die kleinen
 * Geschichten / Märchen, die der Spieler erlebt. Beginnt neue Stories, speichert den
 * Stand, erzeugt Tipps, wenn der Benutzer in einer Story nicht weiterkommt etc.
 */
public class StoryWebComp extends AbstractStatefulComponent<StoryWebPCD> {
    private final AvDatabase db;
    private final World world;
    private final TimeTaker timeTaker;
    private final Narrator n;

    private final SpatialConnectionSystem spatialConnectionSystem;

    private final SCActionStepCountDao scActionStepCountDao;

    @NonNull
    private final Map<Story, StoryData> initialStoryDataMap;

    public StoryWebComp(final AvDatabase db, final TimeTaker timeTaker,
                        final Narrator n,
                        final World world,
                        final SpatialConnectionSystem spatialConnectionSystem,
                        final Story... initialAktiveStories) {
        this(db, timeTaker, n, world, spatialConnectionSystem,
                asList(initialAktiveStories));
    }

    private StoryWebComp(final AvDatabase db,
                         final TimeTaker timeTaker, final Narrator n,
                         final World world,
                         final SpatialConnectionSystem spatialConnectionSystem,
                         final Collection<Story> initialAktiveStories) {
        this(db, timeTaker, n, world, spatialConnectionSystem,
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

    private StoryWebComp(final AvDatabase db, final TimeTaker timeTaker,
                         final Narrator n, final World world,
                         final SpatialConnectionSystem spatialConnectionSystem,
                         final Map<Story, StoryData> initialStoryDataMap) {
        super(STORY_WEB, db.storyWebDao());
        this.db = db;
        this.timeTaker = timeTaker;

        this.n = n;

        scActionStepCountDao = db.scActionStepCountDao();
        this.world = world;
        this.spatialConnectionSystem = spatialConnectionSystem;
        this.initialStoryDataMap = initialStoryDataMap;
    }

    @Override
    @NonNull
    protected StoryWebPCD createInitialState() {
        return new StoryWebPCD(getGameObjectId(), initialStoryDataMap);
    }

    public void reachStoryNode(final IStoryNode storyNode) {
        requirePcd().reachStoryNode(storyNode, scActionStepCountDao.stepCount());

        onStoryNodeReached(storyNode);
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    private void onStoryNodeReached(final IStoryNode storyNode) {
        updatePlanWetter();

        // IDEA Wenn eine Story beendet wurde, könnte der Narrator eine neue
        //  (möglichst abstrakte) Überschrift setzen und damit ein neues Kapitel beginnen.
        //  Die Überschrift bezöge sich lose auf eine der
        //  jetzt noch verbleibenden und offenen Storys (sofern es überhaupt solche
        //  gibt). Für jede Story stehen mehrere Überschriften
        //  bereit, die in einer Reihenfolge gewählt werden.
    }

    /**
     * Aktualisert (d.h. setzt, überschreibt oder löscht) das Plan-Wetter - auf Basis
     * des {@link IStoryNode}s, den der SC - insgesamt über alle
     * {@link Story}s - erreicht hat.
     */
    private void updatePlanWetter() {
        // Diese Bedingungen hier sind Story-übergreifend nach Priorität geordnet!

        final Wetter wetterGO = world.loadWetter();

        if (requirePcd().isReachedOrStoryBeendet(FroschkoenigStoryNode.KUGEL_GENOMMEN)
                && !requirePcd().isReachedOrStoryBeendet(
                FroschkoenigStoryNode.ETWAS_IM_BRUNNEN_VERLOREN)) {
            // Es wird schnell recht heiß, so dass der SC motiviert ist, zum
            // kühlen Brunnen zu gehen und dort mit der Kugel zu spielen.

            wetterGO.wetterComp().setPlanwetter(
                    new WetterData(
                            Temperatur.RECHT_HEISS, Temperatur.KUEHL,
                            Windstaerke.WINDSTILL,
                            Bewoelkung.WOLKENLOS,
                            BlitzUndDonner.KEIN_BLITZ_ODER_DONNER),
                    true, // Sofort mit der Änderung anfangen
                    mins(30)); // Und maximal 30 Minuten dafür brauchen
            return;
        }

        if (requirePcd().isReachedOrStoryBeendet(
                RapunzelStoryNode.TURMZIMMER_VERLASSEN_UM_RAPUNZEL_ZU_BEFREIEN)
                && !requirePcd().isReachedOrStoryBeendet(
                RapunzelStoryNode.STURM_HAT_AESTE_VON_BAEUMEN_GEBROCHEN)) {
            // Es kommt ein Sturm auf, der letzlich Äste von den Bäumen bricht.
            final AvTimeSpan duration = requirePcd().getReachableStoryNodes().size() > 1 ?
                    // Der Spieler hat auch andere Dinge zu tun - die Wetteränderung
                    // darf länger dauern
                    hours(4) :
                    // Der Spieler hat alle anderen Ziele erreicht - die Wetteränderung
                    // sollte schnell gehen
                    mins(30);

            wetterGO.wetterComp().setPlanwetter(
                    new WetterData(
                            Temperatur.KUEHL, Temperatur.KUEHL,
                            Windstaerke.SCHWERER_STURM,
                            Bewoelkung.BEDECKT,
                            BlitzUndDonner.BLITZ_UND_DONNER_NICHT_DIREKT_UEBER_EINEM),
                    true, // Sofort mit der Änderung anfangen
                    duration);
            return;
        }

        if (requirePcd().isReachedOrStoryBeendet(
                RapunzelStoryNode.RAPUNZEL_SINGEN_GEHOERT)
                && !requirePcd().isReachedOrStoryBeendet(
                RapunzelStoryNode.TURMZIMMER_VERLASSEN_UM_RAPUNZEL_ZU_BEFREIEN)) {
            // Das Wetter wird langsam schlechter
            wetterGO.wetterComp().setPlanwetter(new WetterData(
                    Temperatur.KUEHL, Temperatur.KUEHL,
                    Windstaerke.WINDIG,
                    Bewoelkung.BEDECKT,
                    BlitzUndDonner.KEIN_BLITZ_ODER_DONNER));
            return;
        }

        if (requirePcd().isReachedOrStoryBeendet(
                FroschkoenigStoryNode.ETWAS_IM_BRUNNEN_VERLOREN)
                && !requirePcd().isReachedOrStoryBeendet(
                FroschkoenigStoryNode.ZUM_SCHLOSSFEST_GEGANGEN)) {
            // Die Hitze lässt langsam nach
            wetterGO.wetterComp().setPlanwetter(new WetterData(
                    Temperatur.WARM, Temperatur.KUEHL,
                    Windstaerke.LUEFTCHEN,
                    Bewoelkung.LEICHT_BEWOELKT,
                    BlitzUndDonner.KEIN_BLITZ_ODER_DONNER));
            return;
        }

        // In allen anderen Fällen ändert sich das Wetter langsam wieder zum
        // Default-Wetter
        wetterGO.wetterComp().setPlanwetter(WetterData.getDefault());
    }

    public void narrateAndDoHintActionIfAny() {
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
        if (!Story.checkAndAdvanceAStoryIfAppropriate(db, timeTaker, n, world)) {
            // Das hier ist der Regelfall!
            storyNode.narrateAndDoHintAction(db, timeTaker, n, world);
        }

        requirePcd().setLastHintActionStepCount(scActionStepCountDao.stepCount());
    }

    public int getScore() {
        return requirePcd().getScore();
    }

    @Nullable
    private IStoryNode getStoryNodeForHintAction() {
        final ImmutableSet<IStoryNode> storyNodesForHintAction =
                requirePcd().getStoryNodesForHintAction(scActionStepCountDao.stepCount());

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
            return NO_TIME;
        }

        final ILocationGO storyNodeLocation = world.load(storyNodeLocationId);

        final ILocationGO outerMostStoryNodeLocation =
                LocationSystem.getOuterMostLocation(storyNodeLocation);
        @Nullable final ILocationGO outerMostSCLocation =
                world.loadSC().locationComp().getOuterMostLocation();

        if (outerMostStoryNodeLocation.is(outerMostSCLocation)) {
            return NO_TIME;
        }

        if (!(outerMostSCLocation instanceof ISpatiallyConnectedGO)) {
            // Der Benutzer ist sonstwo - wie soll er hinkommen?!
            return null;
        }

        return spatialConnectionSystem.findDistance(
                (ISpatiallyConnectedGO) outerMostSCLocation,
                outerMostStoryNodeLocation);
    }

}
