package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BEWEGT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.time.AvTime.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Der Spielercharakter legt sich schlafen.
 */
public class SchlafenAction extends AbstractScAction {
    public static Collection<SchlafenAction> buildActions(
            final AvDatabase db,
            final Narrator n, final World world,
            @Nullable final IGameObject location) {
        final ImmutableList.Builder<SchlafenAction> res = ImmutableList.builder();
        if (location != null && location.is(BETT_IN_DER_HUETTE_IM_WALD)) {
            res.add(new SchlafenAction(db, n, world));
        }

        return res.build();
    }

    private SchlafenAction(final AvDatabase db, final Narrator n,
                           final World world) {
        super(db, n, world);
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
        // FIXME "Vor Hunger kannst du nicht einschlafen"
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

        final ImmutableList.Builder<TimedDescription<?>> alt = ImmutableList.builder();
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

        n.narrateAlt(alt);
    }

    private void narrateAndDoSchlafen() {
        final AvTimeSpan schlafdauer = schlafen();

        narrateAndDoEinschlafen(schlafdauer);

        // IDEA  Idee: Ein LivingBeing oder FeelingBeing kann schlafen - wenn der
        //  SC schläft, bekommt er nichts mit.
        //  Das _könnte_ der Narrator zentral regeln

        // IDEA Frosch läuft während des Schlafs weg. Oder kommt ggf. Auch wieder. Oder läuft
        //  weg und kommt wieder.
        //  Es sollte in der Zeit keine narrations geben (der Spieler bekommt ja nichts mit, es sei
        //  denn man lässt ihn dann aufwachen...). Nach dem (regulären) Aufwachen sollte etwas
        //  kommen wie... ist verschwunden.
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

        // IDEA Konzept entwickeln, dass diese "Statusübergänge" realisiert:
        //  - Benutzer schläft ein, während Rapunzel singt, aufhört und wieder anfängt
        //  - Benutzer schläft ein, während Rapunzel singt und wacht auf und Rapunzel hat
        //    zwischenzeitlich aufgehört zu singen

        // IDEA Idee: Jede Reaktion speichert den letzten Zustand (PCD), auf Basis dessen sie einen
        //  Text gerendert hat sowie den Zeitpunkt dazu. Wenn wieder Gelegenheit ist, ein Text zu
        //  rendern, wird geprüft, ob sich der Status gegenüber dem Zeitpunkt geändert hat,
        //  außerdem wird geprüft, ob der Zeitpunkt Benutzer etwas versäumt hat oder die ganze
        //  Zeit anwesend und aufnahmefähig war - entsprechend etwas wie "Plötzlich endet der Gesang"
        //  oder "Es ist kein Gesang mehr zu hören" gerendert.

        // IDEA Zum Beispiel wäre der Benutzer über alle Statusänderungen zu unterrichten,
        //  Die zwischenzeitlich passiert sind ("der Frosch ist verschwunden").

        // FIXME Man könnte auch, wenn der Benutzer erstmals wieder nach draußen kommt, etwas
        //  schreiben wie "Inzwischen ist es dunkel geworden". Dazu müsste der "Tageszeit-Status"
        //  (oder zumindest der Zeitpunkt) gespeichert werden, wenn der Benutzer REIN GEHT
        //  und später beim RAUSTRETEN dieser Status mit dem aktuellen Tageszeitstatus verglichen
        //  werden.
        //  Man müsste also die Möglichkeit anbieten, jederzeit den Status eines bestimmten
        //  Game Objects unter einem "Label" zu persistieren (inkl. Zeitpunkt), so dass
        //  man ihn später wieder laden kann. Alternativ auch mehrere Game Objects,
        //  denn nur so kann man prüfen, was sich nach dem Schlafen an einem Ort verändert hat.

        // IDEA Der Benutzer (oder auch andere Game Objects) könnte auch ein "mental model" habe, wo
        //  der Stand der Welt, wie der Benutzer ihn sich vorstellt, gespeichert ist
        //  (z.B. wie ein Raum war als der Benutzer ihn verlassen hat...)
        //  Dann könnte man beim Erzählen vergleich...

        // IDEA Der Frosch läuft während des Schlafens davon - nicht beim Aufwachen.
        //  Alternativ könnte der Spieler durch das Weglaufen aufgeweckt werden
        //  (so ähnlich, wie das Warten unterbrochen wird).

        // IDEA Konzept dafür entwickeln, dass der Benutzer einen  Ort verlässt, während XYZ
        //  passiert und zurückkehrt, wenn XYZ nicht mehr passiert

        // IDEA Konzept entwickeln, dass diese "Statusübergänge" realisiert:
        //  - Benutzer schläft an einem Ort, Rapunzel beginnt dort zu singen und hört wieder auf
        //     (Benutzer merkt nichts)
        //  - Benutzer schläft ein, während Rapunzel nicht singt und wacht auf und Rapunzel hat
        //    zwischenzeitlich angefangen zu singen

        narrateAndDoAufwachen(schlafdauer);

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateAndDoEinschlafen(final AvTimeSpan schlafdauer) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(
                du(SENTENCE,
                        "schließt", "nur kurz die Augen. Die Erlebnisse der letzten Stunden "
                                + "gehen dir durch den Kopf. Was wäre wohl passiert, wenn du…\n"
                                + "Kaum hast du die Augen geschlossen, bist du auch schon "
                                + "eingeschlafen",
                        "nur kurz"
                )
                        .beendet(CHAPTER),
                du("fühlst", "dich auf einmal warm und schwer. Du kuschelst dich an "
                                + "das harte Holz und schon bist du eingeschlafen",
                        "warm und schwer")
                        .beendet(CHAPTER)
        );

        if (world.loadSC().feelingsComp().getMuedigkeit() < FeelingIntensity.STARK) {
            alt.add(neuerSatz("Jetzt, da du liegst, fällt dir erst auf, wir erschöpft du "
                    + "eigentlich bist. Nur ganz kurz die Augen schließen…")
                    .beendet(CHAPTER));
        }

        n.narrateAlt(alt, schlafdauer);
    }

    private void narrateAndDoAufwachen(final AvTimeSpan schlafdauer) {
        sc.feelingsComp().menschAusgeschlafen(schlafdauer);
        sc.feelingsComp().requestMoodMin(NEUTRAL);
        sc.feelingsComp().requestMoodMax(BEWEGT);

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        if (schlafdauer.longerThanOrEqual(hours(7))) {
            alt.add(du(CHAPTER,
                    "wachst", "nach einem langen Schlaf gut erholt wieder auf",
                    "nach einem langen Schlaf"));
        }

        if (schlafdauer.longerThanOrEqual(hours(4))) {
            alt.add(du(CHAPTER,
                    "schläfst", "tief und fest und wachst erst nach einigen "
                            + "Stunden wieder auf",
                    "tief"));
        }

        if (schlafdauer.isBetween(hours(3), hours(6))) {
            alt.add(neuerSatz(CHAPTER,
                    "Als du die Augen wieder aufschlägst, sind einige Stunden vergangen"));
        }


        if (schlafdauer.shorterThanOrEqual(hours(1))) {
            alt.add(du(CHAPTER,
                    "schläfst", "vielleicht eine Stunde und wachst "
                            + "gekräftigt wieder auf"));
        }

        if (schlafdauer.shorterThan(hours(1))) {
            alt.add(neuerSatz(CHAPTER,
                    "Keine Stunde und du erwachst wieder"),
                    du(CHAPTER,
                            "bist", "nach einem kurzen Nickerchen wieder wach",
                            "nach einem kurzen Nickerchen"),
                    du(CHAPTER,
                            "bist", "nach knapp einer Stunde wieder wach",
                            "nach knapp einer Stunde"));
        }

        if (schlafdauer.shorterThanOrEqual(mins(20))) {
            alt.add(neuerSatz(CHAPTER,
                    "Als du wieder aufwachst, hast du den Eindruck, dich gerade erst "
                            + "hingelegt zu haben"));
        }

        n.narrateAlt(alt, noTime());
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

    private AvTimeSpan schlafen() {
        final AvDateTime now = db.nowDao().now();

        if (sc.feelingsComp().getMuedigkeit() < FeelingIntensity.DEUTLICH) {
            return mins(59);
        }

        if (now.getTime().isBefore(oClock(16, 30))) {
            return hours(8);
        } else {
            return now.timeSpanUntil(oClock(7));
        }
    }
}
