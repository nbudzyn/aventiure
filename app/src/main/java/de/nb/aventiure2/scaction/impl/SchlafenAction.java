package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Der Spielercharakter legt sich schlafen.
 */
@SuppressWarnings("ALL")
public class SchlafenAction extends AbstractScAction {
    public static Collection<SchlafenAction> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            @Nullable final IGameObject location) {
        final ImmutableList.Builder<SchlafenAction> res = ImmutableList.builder();
        if (location != null && location.is(BETT_IN_DER_HUETTE_IM_WALD)) {
            res.add(new SchlafenAction(scActionStepCountDao, timeTaker, n, world));
        }

        return res.build();
    }

    private SchlafenAction(final SCActionStepCountDao scActionStepCountDao,
                           final TimeTaker timeTaker, final Narrator n,
                           final World world) {
        super(scActionStepCountDao, timeTaker, n, world);
    }

    @Override
    public String getType() {
        return "actionSchlafen";
    }

    @Override
    @NonNull
    public String getName() {
        return "Ein Nickerchen machen";
    }

    @Override
    public void narrateAndDo() {
        // IDEA "Vor Hunger kannst du nicht einschlafen"
        if (sc.feelingsComp().getMuedigkeit() >=
                // Es ist nicht besonders gemütlich. NUR_LEICHT müde genügt nicht
                FeelingIntensity.MERKLICH) {
            narrateAndDoSchlafen();
            return;
        }

        narrateAndDoSchlaeftNichtEin();
    }

    private void narrateAndDoSchlaeftNichtEin() {
        sc.memoryComp().setLastAction(buildMemorizedAction());

        final AltTimedDescriptionsBuilder alt = altTimed();
        if (!isDefinitivWiederholung()) {
            alt.add(du("schließt",
                    "kurz die Augen. Die Aufregung der letzten Stunden "
                            + "steckt dir noch in den Knochen – an Einschlafen ist "
                            + "nicht zu denken").mitVorfeldSatzglied("kurz")
                    .timed(mins(1)));
        }

        alt.add(du(SENTENCE, "bist", "noch nicht müde")
                .mitVorfeldSatzglied("müde")
                .timed(mins(1))
                .dann());

        alt.add(neuerSatz("Gibt es hier eigentlich Spinnen?")
                .timed(mins(1)));

        alt.add(du("drehst", "dich von einer Seite auf die andere")
                .mitVorfeldSatzglied("von einer Seite")
                .timed(mins(1)));

        n.narrateAlt(alt);
    }

    private void narrateAndDoSchlafen() {
        final AvTimeSpan schlafdauer = sc.feelingsComp().calcSchlafdauerMensch();

        narrateAndDoEinschlafen(schlafdauer);
        sc.feelingsComp().narrateAndDoAufwachenSC(schlafdauer, true);

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateAndDoEinschlafen(final AvTimeSpan schlafdauer) {
        final AltDescriptionsBuilder alt = alt();

        alt.add(du(SENTENCE, "schließt",
                "nur kurz die Augen. Die Erlebnisse der letzten Stunden "
                        + "gehen dir durch den Kopf. Was wäre wohl passiert, wenn du…\n"
                        + "Kaum hast du die Augen geschlossen, bist du auch schon "
                        + "eingeschlafen")
                        .mitVorfeldSatzglied("nur kurz")
                        .beendet(CHAPTER),
                du("fühlst",
                        "dich auf einmal warm und schwer. Du kuschelst dich an",
                        "das harte Holz und schon bist du eingeschlafen")
                        .mitVorfeldSatzglied("warm und schwer")
                        .beendet(CHAPTER),
                du("brauchst", "keines Einwiegens, sondern schläfst sogleich",
                        "ein")
                        .beendet(CHAPTER)
        );

        if (world.loadSC().feelingsComp().getMuedigkeit() < FeelingIntensity.STARK) {
            alt.add(neuerSatz("Jetzt, da du liegst, fällt dir erst auf, wir erschöpft du "
                    + "eigentlich bist. Nur ganz kurz die Augen schließen…")
                    .beendet(CHAPTER));
        }

        n.narrateAlt(alt, schlafdauer);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        // Gilt auch als Wiederholung, wenn der Spielercharakter nicht
        // einschläft - schließlich kann man es, wenn die Entscheidung, ob
        // es sich um eine Wiederholung handelt, relevant ist, noch gar nicht
        // wissen.
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        if (n.lastNarrationWasFromReaction()) {
            return false;
        }

        return false;
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.SCHLAFEN_ODER_VERGEBLICHER_EINSCHLAF_VERSUCH);
    }

}
