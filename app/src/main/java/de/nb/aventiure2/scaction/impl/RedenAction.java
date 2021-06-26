package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.IHasMentalModelGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.Praedikat;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * Der Spieler(charakter) redet mit einem Wesen.
 */
public class RedenAction<TALKER extends IDescribableGO & ILocatableGO & ITalkerGO<?>>
        extends AbstractScAction {
    @NonNull
    private final TALKER talker;

    private final SCTalkAction conversationStep;
    private final String name;

    public static <TALKER extends IDescribableGO & ILocatableGO & ITalkerGO<?>>
    Collection<RedenAction<TALKER>> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker, final Narrator n, final World world,
            final TALKER talker) {
        if (world.isOrHasRecursiveLocation(talker, SPIELER_CHARAKTER)) {
            return ImmutableList.of();
        }

        if (!talker.locationComp().hasRecursiveLocation(
                world.loadSC().locationComp().getLocation())) {
            return ImmutableList.of();
        }

        final List<SCTalkAction> talkSteps = talker.talkingComp().getSCConversationSteps();
        return buildActions(scActionStepCountDao, timeTaker, n, world,
                talker,
                talkSteps);
    }

    private static <TALKER extends IDescribableGO & ILocatableGO & ITalkerGO<?>>
    Collection<RedenAction<TALKER>> buildActions(final SCActionStepCountDao scActionStepCountDao,
                                                 final TimeTaker timeTaker,
                                                 final Narrator n, final World world,
                                                 final TALKER talker,
                                                 final List<SCTalkAction> talkSteps) {
        final ImmutableList.Builder<RedenAction<TALKER>> res =
                ImmutableList.builder();

        for (final SCTalkAction talkStep : talkSteps) {
            if (stepTypeFits(n, world, talker, talkStep.getStepType())) {
                res.add(buildAction(scActionStepCountDao, timeTaker, n, world,
                        talker,
                        // "Mit ... reden" /  "Den ... ignorieren" / "Das Gespräch beenden"
                        talkStep));
            }
        }

        return res.build();
    }

    private static boolean stepTypeFits(final Narrator n,
                                        final World world,
                                        final ITalkerGO<?> talker,
                                        final SCTalkAction.Type stepType) {
        if (talker.talkingComp().isTalkingTo(SPIELER_CHARAKTER)) {
            // Der SC befindet sich gerade im Gespräch mit dem Talker.
            return stepType == SCTalkAction.Type.NORMAL ||
                    stepType == SCTalkAction.Type.EXIT;
        }

        if (scHatGeradeGespraechMitTalkerBeendet(n, world.loadSC(), talker)) {
            return stepType == SCTalkAction.Type.IMMEDIATE_RE_ENTRY_SC_HATTE_GESPRAECH_BEENDET;
        }

        if (talkerHatGeradeGespraechMitSCBeendet(n, world.loadSC(), talker)) {
            return stepType == SCTalkAction.Type.IMMEDIATE_RE_ENTRY_NSC_HATTE_GESPRAECH_BEENDET;
        }

        // Der SC befindet sich gerade nicht im Gespräch mit dem Talker
        // (es wurde auch nicht GERADE EBEN so ein Gespräch beendet).
        return stepType == SCTalkAction.Type.ENTRY_RE_ENTRY;
    }

    /**
     * Erzeugt eine <code>RedenAction</code> für dieses {@link ILivingBeingGO}.
     */
    @NonNull
    private static <TALKER extends IDescribableGO & ILocatableGO & ITalkerGO<?>>
    RedenAction<TALKER> buildAction(final SCActionStepCountDao scActionStepCountDao,
                                    final TimeTaker timeTaker,
                                    final Narrator n, final World world,
                                    final TALKER talker,
                                    final SCTalkAction talkStep) {
        final PraedikatOhneLeerstellen praedikatOhneLeerstellen =
                fuelleGgfPraedikatLeerstelleMitCreature(world, talkStep.getName(), talker);

        return buildAction(scActionStepCountDao, timeTaker, n, world, talker,
                talkStep,
                praedikatOhneLeerstellen);
    }

    @SuppressWarnings("ChainOfInstanceofChecks")
    private static PraedikatOhneLeerstellen fuelleGgfPraedikatLeerstelleMitCreature(
            final World worldervice,
            final Praedikat praedikat,
            final IDescribableGO talker) {
        if (praedikat instanceof PraedikatOhneLeerstellen) {
            return (PraedikatOhneLeerstellen) praedikat;
        }

        if (praedikat instanceof PraedikatMitEinerObjektleerstelle) {
            final EinzelneSubstantivischePhrase creatureDesc =
                    worldervice.getPOVDescription(SPIELER_CHARAKTER, talker, true);

            return ((PraedikatMitEinerObjektleerstelle) praedikat).mit(creatureDesc);
        }

        throw new IllegalArgumentException("Unexpected type of Prädikat: " + praedikat);
    }

    /**
     * Erzeugt eine <code>RedenAction</code>
     * mit diesem {@link PraedikatOhneLeerstellen}.
     */
    @NonNull
    private static <TALKER extends IDescribableGO & ILocatableGO & ITalkerGO<?>>
    RedenAction<TALKER> buildAction(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            final TALKER talker,
            final SCTalkAction talkStep,
            final PraedikatOhneLeerstellen praedikatOhneLeerstellen) {
        return new RedenAction<>(scActionStepCountDao, timeTaker, n, world, talker,
                talkStep,
                // "Dem Frosch Angebote machen"
                // "Das Angebot von *dir* weisen"
                joinToKonstituentenfolge(
                        SENTENCE,
                        praedikatOhneLeerstellen.getInfinitiv(P2, SG))
                        .joinToString());
    }

    private RedenAction(final SCActionStepCountDao scActionStepCountDao,
                        final TimeTaker timeTaker,
                        final Narrator n,
                        final World world,
                        @NonNull final TALKER talker,
                        final SCTalkAction conversationStep,
                        @NonNull final String name) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.talker = talker;
        this.conversationStep = conversationStep;
        this.name = name;
    }

    @Override
    public String getType() {
        return "actionRedenRufen";
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @Override
    public void narrateAndDo() {
        sc.memoryComp().setLastAction(buildMemorizedAction());

        updateMentalModels();

        conversationStep.narrateAndDo();
    }

    private void updateMentalModels() {
        if (!world.hasSameOuterMostLocationAsSC(talker)) {
            return;
        }

        // Dies ist zumindest der Regelfall: Wenn der SC mit
        // X spricht, weiß X danach auch, wo der SC sich befindet -
        // zumindest, wenn SC und X im selben Raum sind.
        // Es könnte Ausnahmen geben.
        world.loadSC().mentalModelComp().setAssumedLocationToActual(talker);

        if (!(talker instanceof IHasMentalModelGO)) {
            return;
        }

        // Dies ist zumindest der Regelfall: Wenn der SC mit
        // X spricht, weiß X danach auch, wo der SC sich befindet -
        // zumindest, wenn SC und X im selben Raum sind.
        // Es könnte Ausnahmen geben.
        ((IHasMentalModelGO) talker).mentalModelComp()
                .setAssumptionsToActual(SPIELER_CHARAKTER);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        // Man sagt ja jedes Mal etwas anderes, kommt von einem
        // Verhandlungsschritt zum nächsten etc.
        return false;
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        // Man sagt ja jedes Mal etwas anderes, kommt von einem
        // Verhandlungsschritt zum nächsten etc.
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        // Der SC hat das Gespräch mit der Creature GERADE EBEN beendet
        // und hat es sich ganz offenbar anders überlegt.
        return scHatGeradeGespraechMitTalkerBeendet(n, world.loadSC(), talker);
    }

    private static boolean scHatGeradeGespraechMitTalkerBeendet(
            final Narrator n, final SpielerCharakter sc, final ITalkerGO<?> talker) {
        return gespraechZwischenTalkerUndSCWurdeGeradeBeendet(n, sc, talker)
                && !talker.talkingComp().isTalkerHatLetztesGespraechSelbstBeendet();
    }

    private static boolean talkerHatGeradeGespraechMitSCBeendet(
            final Narrator n, final SpielerCharakter sc, final ITalkerGO<?> talker) {
        return gespraechZwischenTalkerUndSCWurdeGeradeBeendet(n, sc, talker)
                && talker.talkingComp().isTalkerHatLetztesGespraechSelbstBeendet();
    }

    private static boolean gespraechZwischenTalkerUndSCWurdeGeradeBeendet(
            final Narrator n, final SpielerCharakter sc, final ITalkerGO<?> talker) {
        return !n.lastNarrationWasFromReaction()
                && sc.memoryComp().lastActionWas(Action.Type.REDEN, talker)
                && !talker.talkingComp().isTalkingTo(SPIELER_CHARAKTER);
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.REDEN, talker);
    }
}
