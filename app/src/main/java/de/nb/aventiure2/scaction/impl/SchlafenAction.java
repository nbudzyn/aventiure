package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.time.AvTime.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;

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
    public void narrateAndDo() {
        // STORY Wenn der Benutzer längere Zeit nicht geschlafen hat, wird er automatisch müde
        //  und bleibt müde (analog Hunger!). Abends wird man eher müde oder ist von allein müde.
        //  Oder Nachts ist man immer müde.

        // STORY "Vor Hunger kannst du nicht einschlafen"
        if (sc.feelingsComp().hasMood(ERSCHOEPFT)) {
            narrateAndDoSchlaeftEin();
            return;
        }

        narrateAndDoSchlaeftNichtEin();
    }

    private void narrateAndDoSchlaeftNichtEin() {
        sc.memoryComp().setLastAction(buildMemorizedAction());

        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();
        if (!isDefinitivWiederholung()) {
            alt.add(du("schließt", "kurz die Augen. Die Aufregung der letzten Stunden "
                            + "steckt dir noch in den Knochen – an Einschlafen ist "
                            + "nicht zu denken",
                    "kurz",
                    mins(1)));
        }

        alt.add(du(SENTENCE, "bist", "noch nicht müde",
                "müde", mins(1))
                .dann());

        alt.add(neuerSatz("Gibt es hier eigentlich Spinnen?", mins(1)));

        alt.add(du("drehst", "dich von einer Seite auf die andere",
                "von einer Seite", mins(1)));

        n.addAlt(alt);
    }

    private void narrateAndDoSchlaeftEin() {
        n.addAlt(
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

        // TODO Frosch läuft während des Schlafs weg. Oder kommt ggf. Auch wieder. Oder läuft
        //  weg und kommt wieder.
        //  Es sollte in der Zeit keine narrations geben (der Spieler bekommt ja nichts mit, es sei
        //  denn er wacht auf). Aber danach sollte etwas kommen wie... ist verschwunden.
        //  Wie macht man das sinnvoll?
        //  Für die Tageszeit haben wir ein gutes Konzept.
        //  Für andere Dinge (Frosch weg, Kugel weg) scheint es nicht zu funktionieren? Wenn
        //  man die Zeit mittendrin weiterlaufen lässt, funktioniert das mit den Tageszeiten
        //  nicht mehr!
        //  Man könnte sagen: Schlafen ist wie Bewegen: Es gibt eine neue Beschreibung der
        //  äußeren Umstände, zumindest soweit sie sich verändert haben. Dazu muss der Unterschied
        //  (vorher / nachher) ermittelt werden. Und die Zeit muss zwischendrin vergehen -
        //  allerdings ohne narration.
        //  Andere Idee könnte sein: Beim Vergehen von Zeit gibt es DREI Parameter:
        //  letzter Zeitpunkt, letzter WACHER Zeitpunkt und aktueller Zeitpunkt
        //  Entsprechend kann dann der Text gestaltet werden, z.B. "Der Frosch ist verschwunden."

        // STORY Konzept entwickeln, dass diese "Statusübergänge" realisiert:
        //  - Benutzer rastet für längere Zeit (wach) und Rapunzel beginnt mehrfach
        //    zu singen und hört wieder auf, letztlich hat Rapunzel aufgehört
        //  - Benutzer schläft ein, während Rapunzel singt, aufhört und wieder anfängt
        //  - Benutzer schläft ein, während Rapunzel singt und wacht auf und Rapunzel hat
        //    zwischenzeitlich aufgehört zu singen

        // TODO Idee: Jede Reaktion speichert den letzten Zustand (PCD), auf Basis dessen sie einen
        //  Text gerendert hat sowie den Zeitpunkt dazu. Wenn wieder Gelegenheit ist, ein Text zu
        //  rendern, wird geprüft, ob sich der Status gegenüber dem Zeitpunkt geändert hat,
        //  außerdem wird geprüft, ob der Zeitpunkt Benutzer etwas versäumt hat oder die ganze
        //  Zeit anwesend und aufnahmefähig war - entsprechend etwas wie "Plötzlich endet der Gesang"
        //  oder "Es ist kein Gesang mehr zu hören" gerendert.

        // STORY Zum Beispiel wäre der Benutzer über alle Statusänderungen zu unterrichten,
        //  Die zwischenzeitlich passiert sind ("der Frosch ist verschwunden").

        // STORY Man könnte auch, wenn der Benutzer erstmals wieder nach draußen kommt, etwas
        //  schreiben wie "Inzwischen ist es dunkel geworden". Dazu müsste der "Tageszeit-Status"
        //  (oder zumindest der Zeitpunkt) gespeichert werden, wenn der Benutzer REIN GEHT
        //  und später beim RAUSTRETEN dieser Status mit dem aktuellen Tageszeitstatus verglichen
        //  werden.
        //  Man müsste also die Möglichkeit anbieten, jederzeit den Status eines bestimmten
        //  Game Objects unter einem "Label" zu persistieren (inkl. Zeitpunkt), so dass
        //  man ihn später wieder laden kann. Alternativ auch mehrere Game Objects,
        //  denn nur so kann man prüfen, was sich nach dem Schlafen an einem Ort verändert hat.

        // TODO Der Benutzer (oder auch andere Game Objects) könnte auch ein "mental Model" habe, wo
        //  der Stand der Welt, wie der Benutzer ihn sich vorstellt, gespeichert ist
        //  (z.B. wie ein Raum war als der Benutzer ihn verlassen hat...)
        //  Dann könnte man beim Erzählen vergleich...

        // STORY Der Frosch läuft während des Schlafens davon - nicht beim Aufwachen.

        // STORY Man kann durch Ereignisse aufgeweckt werden! Der Status des spielers wäre dann auf wach gesetzt und würde immer wieder geprüft.

        // STORY Konzept dafür entwickeln, dass der Benutzer einen  Ort verlässt, während XYZ
        //  passiert und zurückkehrt, wenn XYZ nicht mehr passiert

        // STORY Konzept entwickeln, dass diese "Statusübergänge" realisiert:
        //  - Benutzer schläft an einem Ort, Rapunzel beginnt dort zu singen und hört wieder auf
        //     (Benutzer merkt nichts)
        //  - Benutzer schläft ein, während Rapunzel nicht singt und wacht auf und Rapunzel hat
        //    zwischenzeitlich angefangen zu singen

        sc.feelingsComp().setMood(NEUTRAL);

        n.addAlt(
                du(CHAPTER,
                        "wachst", "nach einem langen Schlaf gut erholt wieder auf",
                        "nach einem langen Schlaf", noTime()),
                du(CHAPTER,
                        "schläfst", "tief und fest und wachst erst nach einigen "
                                + "Stunden wieder auf",
                        "tief",
                        noTime()));
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
