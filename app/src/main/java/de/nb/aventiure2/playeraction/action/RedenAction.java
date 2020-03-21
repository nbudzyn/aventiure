package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.praedikat.Praedikat;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;
import de.nb.aventiure2.playeraction.action.reden.CreatureTalkStep;
import de.nb.aventiure2.playeraction.action.reden.CreatureTalkSteps;

/**
 * Der Spieler(charakter) redet mit einem Wesen.
 */
public class RedenAction extends AbstractPlayerAction {
    private final AvRoom room;
    private final Map<AvObject.Key, ObjectData> allObjectsByKey;

    @NonNull
    private final CreatureData creatureData;

    private final CreatureTalkStep creatureTalkStep;
    private final String name;

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final StoryState initialStoryState, final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final CreatureData creatureData) {
        final List<CreatureTalkStep> talkSteps =
                CreatureTalkSteps.getPossibleSteps(
                        db, initialStoryState, RedenAction.class, room,
                        allObjectsByKey, creatureData);

        return buildActions(db, initialStoryState, room, allObjectsByKey,
                creatureData,
                talkSteps);
    }

    private static Collection<AbstractPlayerAction> buildActions(final AvDatabase db,
                                                                 final StoryState initialStoryState,
                                                                 final AvRoom room,
                                                                 final Map<AvObject.Key, ObjectData> allObjectsByKey,
                                                                 final CreatureData creatureData,
                                                                 final List<CreatureTalkStep> talkSteps) {
        final ImmutableList.Builder<AbstractPlayerAction> res =
                ImmutableList.builder();

        for (final CreatureTalkStep talkStep : talkSteps) {
            if (!talkStep.isExitStep() ||
                    initialStoryState.talkingTo(creatureData.getCreature().getKey())) {
                res.add(buildAction(db, initialStoryState, room, allObjectsByKey,
                        creatureData,
                        // "Mit ... reden" /  "Den ... ignorieren" / "Das Gespräch beenden"
                        talkStep));
            }
        }

        return res.build();
    }

    /**
     * Erzeugt eine <code>RedenAction</code> für dieses {@link CreatureData}.
     */
    @NonNull
    private static RedenAction buildAction(final AvDatabase db,
                                           final StoryState initialStoryState,
                                           final AvRoom room,
                                           final Map<AvObject.Key, ObjectData> allObjectsByKey,
                                           final CreatureData creatureData,
                                           final CreatureTalkStep talkStep) {
        final PraedikatOhneLeerstellen praedikatOhneLeerstellen =
                fuelleGgfPraedikatLeerstelleMitCreature(talkStep.getName(), creatureData);

        return buildAction(db, initialStoryState, room, allObjectsByKey, creatureData,
                talkStep,
                praedikatOhneLeerstellen);
    }

    private static PraedikatOhneLeerstellen fuelleGgfPraedikatLeerstelleMitCreature(
            final Praedikat praedikat,
            final CreatureData creatureData) {
        if (praedikat instanceof PraedikatOhneLeerstellen) {
            return (PraedikatOhneLeerstellen) praedikat;
        }

        if (praedikat instanceof PraedikatMitEinerObjektleerstelle) {
            return ((PraedikatMitEinerObjektleerstelle) praedikat).mitObj(creatureData);
        }

        throw new IllegalArgumentException("Unexpected type of Prädikat: "
                + praedikat);
    }

    /**
     * Erzeugt eine <code>RedenAction</code>
     * mit diesem {@link PraedikatOhneLeerstellen}.
     */
    @NonNull
    private static RedenAction buildAction(final AvDatabase db, final StoryState initialStoryState,
                                           final AvRoom room,
                                           final Map<AvObject.Key, ObjectData> allObjectsByKey,
                                           final CreatureData creatureData,
                                           final CreatureTalkStep talkStep,
                                           final PraedikatOhneLeerstellen praedikatOhneLeerstellen) {
        return new RedenAction(db, initialStoryState, creatureData, room, allObjectsByKey,
                talkStep,
                // "Dem Frosch Angebote machen"
                praedikatOhneLeerstellen.getDescriptionInfinitiv());
    }

    private RedenAction(final AvDatabase db,
                        final StoryState initialStoryState,
                        @NonNull final CreatureData creatureData, final AvRoom room,
                        final Map<AvObject.Key, ObjectData> allObjectsByKey,
                        final CreatureTalkStep creatureTalkStep,
                        @NonNull final String name) {
        super(db, initialStoryState);
        this.creatureData = creatureData;
        this.room = room;
        this.allObjectsByKey = allObjectsByKey;
        this.creatureTalkStep = creatureTalkStep;
        this.name = name;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public void narrateAndDo() {
        creatureTalkStep.narrateAndDo();
    }

    @Override
    protected StoryStateBuilder t(
            @NonNull final StoryState.StartsNew startsNew,
            @NonNull final String text) {
        return super.t(startsNew, text)
                .imGespraechMit(creatureData.getCreature());
    }
}
