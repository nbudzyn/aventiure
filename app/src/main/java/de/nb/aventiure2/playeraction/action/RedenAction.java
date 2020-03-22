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
import de.nb.aventiure2.playeraction.action.conversation.CreatureConversationStep;
import de.nb.aventiure2.playeraction.action.conversation.CreatureConversationSteps;

/**
 * Der Spieler(charakter) redet mit einem Wesen.
 */
public class RedenAction extends AbstractPlayerAction {
    private final AvRoom room;
    private final Map<AvObject.Key, ObjectData> allObjectsByKey;

    @NonNull
    private final CreatureData creatureData;

    private final CreatureConversationStep creatureConversationStep;
    private final String name;

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final StoryState initialStoryState, final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final CreatureData creatureData) {
        final List<CreatureConversationStep> talkSteps =
                CreatureConversationSteps.getPossibleSteps(
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
                                                                 final List<CreatureConversationStep> talkSteps) {
        final ImmutableList.Builder<AbstractPlayerAction> res =
                ImmutableList.builder();

        for (final CreatureConversationStep talkStep : talkSteps) {
            if (stepTypeFits(initialStoryState, creatureData, talkStep.getStepType())) {
                res.add(buildAction(db, initialStoryState, room, allObjectsByKey,
                        creatureData,
                        // "Mit ... reden" /  "Den ... ignorieren" / "Das Gespräch beenden"
                        talkStep));
            }
        }

        return res.build();
    }

    private static boolean stepTypeFits(final StoryState initialStoryState,
                                        final CreatureData creatureData,
                                        final CreatureConversationStep.Type stepType) {
        if (initialStoryState.talkingTo(creatureData.getCreature().getKey())) {
            // Der SC befindet sich gerade im Gespräch mit der Creature-
            return stepType == CreatureConversationStep.Type.NORMAL ||
                    stepType == CreatureConversationStep.Type.EXIT;
        }

        if (initialStoryState.lastActionWas(RedenAction.class)
            // TODO Hier müsste man noch prüfen, dass der SC auch gerade MIT DIESER CREATURE
            // geredet hat! initialStoryState.hatGeradeGeredetMit(creatureData) oder so
        ) {
            // Der SC hat das Gespräch mit der Creature GERADE EBEN beendet
            // und hat es sich ganz offenbar anders überlegt
            // (oder die Creature hat das Gespräch beendet, und der Benutzer möchte
            // sofort wieder ein Gespräch anknüpfen).
            return stepType == CreatureConversationStep.Type.IMMEDIATE_RE_ENTRY;
        }

        // Der SC befindet sich gerade nicht im Gespräch mit der Creature
        // (und auch nicht GERADE EBEN so ein Gespräch beendet).
        return stepType == CreatureConversationStep.Type.ENTRY_RE_ENTRY;
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
                                           final CreatureConversationStep talkStep) {
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
                                           final CreatureConversationStep talkStep,
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
                        final CreatureConversationStep creatureConversationStep,
                        @NonNull final String name) {
        super(db, initialStoryState);
        this.creatureData = creatureData;
        this.room = room;
        this.allObjectsByKey = allObjectsByKey;
        this.creatureConversationStep = creatureConversationStep;
        this.name = name;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public void narrateAndDo() {
        creatureConversationStep.narrateAndDo();
    }

    @Override
    protected StoryStateBuilder t(
            @NonNull final StoryState.StartsNew startsNew,
            @NonNull final String text) {
        return super.t(startsNew, text)
                .imGespraechMit(creatureData.getCreature());
    }
}
