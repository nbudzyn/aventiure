package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobject.World.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * Der Spielercharakter legt sich schlafen.
 */
public class SchlafenAction extends AbstractScAction {
    public static Collection<SchlafenAction> buildActions(
            final AvDatabase db,
            final World world,
            @Nullable final IGameObject location) {
        final ImmutableList.Builder<SchlafenAction> res = ImmutableList.builder();
        if (location != null && location.is(BETT_IN_DER_HUETTE_IM_WALD)) {
            // STORY Schlafen könnte man auch im Schaukelstuhl
            res.add(new SchlafenAction(db, world));
        }

        return res.build();
    }

    private SchlafenAction(final AvDatabase db,
                           final World world) {
        super(db, world);
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
    public AvTimeSpan narrateAndDo() {
        // STORY Nachts ist man immer müde oder nachts wird man leichter müde oder
        //  nachts kann man immer einschlafen...
        // STORY "Vor Hunger kannst du nicht einschlafen"
        if (sc.feelingsComp().hasMood(ERSCHOEPFT)) {
            return narrateAndDoSchlaeftEin();
        }

        return narrateAndDoSchlaeftNichtEin();
    }

    @NonNull
    private AvTimeSpan narrateAndDoSchlaeftNichtEin() {
        sc.memoryComp().setLastAction(buildMemorizedAction());

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();
        if (!isDefinitivWiederholung()) {
            alt.add(du("schließt", "kurz die Augen. Die Aufregung der letzten Stunden "
                            + "steckt dir noch in den Knochen – an Einschlafen ist "
                            + "nicht zu denken",
                    "kurz",
                    mins(1)));
        }

        alt.add(du("bist", "noch nicht müde",
                "müde", mins(1))
                .dann());

        alt.add(neuerSatz("Gibt es hier eigentlich Spinnen?", mins(1)));

        alt.add(du("drehst", "dich von einer Seite auf die andere",
                "von einer Seite", mins(1)));

        return n.addAlt(alt);
    }

    private AvTimeSpan narrateAndDoSchlaeftEin() {
        final AvTimeSpan timeElapsed = n.addAlt(
                // TODO Irgendwie wird immer nur dieser Text gewählt, nicht die anderen
                //  beiden...
                du(SENTENCE,
                        "schließt", "nur kurz die Augen. Die Erlebnisse der letzten Stunden "
                                + "gehen dir durch den Kopf. Was wäre wohl passiert, wenn du…\n"
                                + "Kaum hast du die Augen geschlossen, bist du auch schon "
                                + "eingeschlafen",
                        "nur kurz",
                        schlafen())
                        .beendet(CHAPTER),
                neuerSatz("Jetzt, da du liegst, fällt dir erst auf, wir erschöpft du "
                                + "eigentlich bist. Nur ganz kurz die Augen schließen…",
                        schlafen())
                        .beendet(CHAPTER),
                du("fühlst", "dich auf einmal warm und schwer. Du kuschelst dich an "
                                + "das harte Holz und schon bist du eingeschlafen",
                        "warm und schwer",
                        schlafen())
                        .beendet(CHAPTER));

        sc.memoryComp().setLastAction(buildMemorizedAction());

        // STORY Der Frosch läuft während des Schlafens davon - nicht beim Aufwachen.

        // TODO Zeit in kleinen Einheiten vergehen lassen:erst 5 Minuten, dann den verbliebenen Rest.

        // STORY Man kann durch Ereignisse aufgeweckt werden! Der Status des spielers wäre dann auf wach gesetzt und würde immer wieder geprüft.
        sc.feelingsComp().setMood(NEUTRAL);

        return timeElapsed.plus(n.addAlt(
                du(CHAPTER,
                        "wachst", "nach einem langen Schlaf gut erholt wieder auf",
                        "nach einem langen Schlaf", noTime()),
                du(CHAPTER,
                        "schläfst", "tief und fest und wachst erst nach einigen "
                                + "Stunden wieder auf",
                        "tief",
                        noTime())));
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


    private AvTimeSpan schlafen() {
        final AvDateTime now = db.nowDao().now();

        if (now.getTime().isBefore(oClock(16, 30))) {
            return hours(8);
        } else {
            return now.timeSpanUntil(oClock(7));
        }
    }
}
