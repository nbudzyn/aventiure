package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.Praedikat;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;

/**
 * Der Spieler(charakter) redet mit einem Wesen.
 */
public class RedenAction<TALKER extends IDescribableGO & ITalkerGO>
        extends AbstractScAction {
    @NonNull
    private final TALKER talker;

    private final SCTalkAction conversationStep;
    private final String name;

    public static <TALKER extends IDescribableGO & ITalkerGO>
    Collection<RedenAction<TALKER>> buildActions(
            final AvDatabase db, final World world,
            final ILocationGO room,
            final TALKER talker) {
        final List<SCTalkAction> talkSteps =
                talker.talkingComp().getSCConversationSteps();

        return buildActions(db, world,
                talker,
                talkSteps);
    }

    private static <TALKER extends IDescribableGO & ITalkerGO>
    Collection<RedenAction<TALKER>> buildActions(final AvDatabase db,
                                                 final World world,
                                                 final TALKER talker,
                                                 final List<SCTalkAction> talkSteps) {
        final ImmutableList.Builder<RedenAction<TALKER>> res =
                ImmutableList.builder();

        for (final SCTalkAction talkStep : talkSteps) {
            if (stepTypeFits(world, talker, talkStep.getStepType())) {
                res.add(buildAction(db, world,
                        talker,
                        // "Mit ... reden" /  "Den ... ignorieren" / "Das Gespräch beenden"
                        talkStep));
            }
        }

        return res.build();
    }

    private static boolean stepTypeFits(final World world,
                                        final ITalkerGO talker,
                                        final SCTalkAction.Type stepType) {
        if (talker.talkingComp().isTalkingTo(SPIELER_CHARAKTER)) {
            // Der SC befindet sich gerade im Gespräch mit der Creature-
            return stepType == SCTalkAction.Type.NORMAL ||
                    stepType == SCTalkAction.Type.EXIT;
        }

        if (talker.talkingComp().isDefinitivDiskontinuitaet()) {
            return stepType == SCTalkAction.Type.IMMEDIATE_RE_ENTRY;
        }

        // Der SC befindet sich gerade nicht im Gespräch mit der Creature
        // (und auch nicht GERADE EBEN so ein Gespräch beendet).
        return stepType == SCTalkAction.Type.ENTRY_RE_ENTRY;
    }

    /**
     * Erzeugt eine <code>RedenAction</code> für dieses {@link ILivingBeingGO}.
     */
    @NonNull
    private static <TALKER extends IDescribableGO & ITalkerGO>
    RedenAction<TALKER> buildAction(final AvDatabase db,
                                    final World world,
                                    final TALKER talker,
                                    final SCTalkAction talkStep) {
        final PraedikatOhneLeerstellen praedikatOhneLeerstellen =
                fuelleGgfPraedikatLeerstelleMitCreature(world, talkStep.getName(), talker);

        return buildAction(db, world, talker,
                talkStep,
                praedikatOhneLeerstellen);
    }

    private static PraedikatOhneLeerstellen fuelleGgfPraedikatLeerstelleMitCreature(
            final World worldervice,
            final Praedikat praedikat,
            final IDescribableGO talker) {
        if (praedikat instanceof PraedikatOhneLeerstellen) {
            return (PraedikatOhneLeerstellen) praedikat;
        }

        if (praedikat instanceof PraedikatMitEinerObjektleerstelle) {
            final Nominalphrase creatureDesc =
                    worldervice.getPOVDescription(SPIELER_CHARAKTER, talker, true);


            return ((PraedikatMitEinerObjektleerstelle) praedikat).mitObj(creatureDesc);
        }

        throw new IllegalArgumentException("Unexpected type of Prädikat: " + praedikat);
    }

    /**
     * Erzeugt eine <code>RedenAction</code>
     * mit diesem {@link PraedikatOhneLeerstellen}.
     */
    @NonNull
    private static <TALKER extends IDescribableGO & ITalkerGO>
    RedenAction<TALKER> buildAction(final AvDatabase db,
                                    final World world,
                                    final TALKER talker,
                                    final SCTalkAction talkStep,
                                    final PraedikatOhneLeerstellen praedikatOhneLeerstellen) {
        return new RedenAction<>(db, world, talker,
                talkStep,
                // "Dem Frosch Angebote machen"
                // "Das Angebot von *mir* weisen"
                capitalize(praedikatOhneLeerstellen.getDescriptionInfinitiv(P1, SG)));
    }

    private RedenAction(final AvDatabase db,
                        final World world,
                        @NonNull final TALKER talker,
                        final SCTalkAction conversationStep,
                        @NonNull final String name) {
        super(db, world);
        this.talker = talker;
        this.conversationStep = conversationStep;
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
        sc.memoryComp().setLastAction(buildMemorizedAction());

        return conversationStep.narrateAndDo();
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        // Man sagt ja jedes Mal etwas anderes, kommt von einem
        // Verhandlungsschritt zum nächsten etc.
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return talker.talkingComp().isDefinitivDiskontinuitaet();
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.REDEN, talker);
    }
}
