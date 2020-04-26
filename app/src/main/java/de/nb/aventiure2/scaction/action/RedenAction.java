package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.Praedikat;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.action.creature.conversation.CreatureConversationStep;
import de.nb.aventiure2.scaction.action.creature.conversation.CreatureConversationSteps;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;

/**
 * Der Spieler(charakter) redet mit einem Wesen.
 */
public class RedenAction<L extends ILivingBeingGO & IDescribableGO>
        extends AbstractScAction {
    @NonNull
    private final L creature;

    private final CreatureConversationStep creatureConversationStep;
    private final String name;

    public static <L extends ILivingBeingGO & IDescribableGO>
    Collection<RedenAction<L>> buildActions(
            final AvDatabase db, final StoryState initialStoryState, final IHasStoringPlaceGO room,
            final L creature) {
        final List<CreatureConversationStep> talkSteps =
                CreatureConversationSteps.getPossibleSteps(
                        db, initialStoryState, room, creature);

        return buildActions(db, initialStoryState,
                creature,
                talkSteps);
    }

    private static <L extends ILivingBeingGO & IDescribableGO>
    Collection<RedenAction<L>> buildActions(final AvDatabase db,
                                            final StoryState initialStoryState,
                                            final L creature,
                                            final List<CreatureConversationStep> talkSteps) {
        final ImmutableList.Builder<RedenAction<L>> res =
                ImmutableList.builder();

        for (final CreatureConversationStep talkStep : talkSteps) {
            if (stepTypeFits(db, initialStoryState, creature, talkStep.getStepType())) {
                res.add(buildAction(db, initialStoryState,
                        creature,
                        // "Mit ... reden" /  "Den ... ignorieren" / "Das Gespräch beenden"
                        talkStep));
            }
        }

        return res.build();
    }

    private static boolean stepTypeFits(final AvDatabase db,
                                        final StoryState initialStoryState,
                                        final ILivingBeingGO creature,
                                        final CreatureConversationStep.Type stepType) {
        if (initialStoryState.talkingTo(creature)) {
            // Der SC befindet sich gerade im Gespräch mit der Creature-
            return stepType == CreatureConversationStep.Type.NORMAL ||
                    stepType == CreatureConversationStep.Type.EXIT;
        }

        if (loadSC(db).memoryComp().lastActionWas(
                Action.Type.REDEN, creature)) {
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
     * Erzeugt eine <code>RedenAction</code> für dieses {@link ILivingBeingGO}.
     */
    @NonNull
    private static <L extends ILivingBeingGO & IDescribableGO>
    RedenAction buildAction(final AvDatabase db,
                            final StoryState initialStoryState,
                            final L creature,
                            final CreatureConversationStep talkStep) {
        final PraedikatOhneLeerstellen praedikatOhneLeerstellen =
                fuelleGgfPraedikatLeerstelleMitCreature(db, talkStep.getName(), creature);

        return buildAction(db, initialStoryState, creature,
                talkStep,
                praedikatOhneLeerstellen);
    }

    private static PraedikatOhneLeerstellen fuelleGgfPraedikatLeerstelleMitCreature(
            final AvDatabase db,
            final Praedikat praedikat,
            final IDescribableGO creature) {
        if (praedikat instanceof PraedikatOhneLeerstellen) {
            return (PraedikatOhneLeerstellen) praedikat;
        }

        if (praedikat instanceof PraedikatMitEinerObjektleerstelle) {
            final Nominalphrase creatureDesc =
                    GameObjects.getPOVDescription(db, SPIELER_CHARAKTER, creature, true);


            return ((PraedikatMitEinerObjektleerstelle) praedikat).mitObj(
                    creatureDesc);
        }

        throw new IllegalArgumentException("Unexpected type of Prädikat: "
                + praedikat);
    }

    /**
     * Erzeugt eine <code>RedenAction</code>
     * mit diesem {@link PraedikatOhneLeerstellen}.
     */
    @NonNull
    private static <L extends ILivingBeingGO & IDescribableGO>
    RedenAction<L> buildAction(final AvDatabase db, final StoryState initialStoryState,
                               final L creatureData,
                               final CreatureConversationStep talkStep,
                               final PraedikatOhneLeerstellen praedikatOhneLeerstellen) {
        return new RedenAction<>(db, initialStoryState, creatureData,
                talkStep,
                // "Dem Frosch Angebote machen"
                praedikatOhneLeerstellen.getDescriptionInfinitiv());
    }

    private RedenAction(final AvDatabase db,
                        final StoryState initialStoryState,
                        @NonNull final L creature,
                        final CreatureConversationStep creatureConversationStep,
                        @NonNull final String name) {
        super(db, initialStoryState);
        this.creature = creature;
        this.creatureConversationStep = creatureConversationStep;
        this.name = name;
    }

    @Override
    public String getType() {
        return "actionReden";
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        final CreatureConversationStep timeSpan = creatureConversationStep;
        sc.memoryComp().setLastAction(buildMemorizedAction());

        return timeSpan.narrateAndDo();
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        // Man sagt ja jedes Mal etwas anderes, kommt von einem
        // Verhandlungsschritt zum nächsten etc.
        return false;
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.REDEN, creature);
    }

    @Override
    protected StoryStateBuilder t(
            @NonNull final StoryState.StructuralElement startsNew,
            @NonNull final String text) {
        return super.t(startsNew, text)
                .imGespraechMit(creature);
    }
}
