package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Sorgt dafür, dass Sätze erzählt werden wie "Allmählich wird es Abend".
 */
public class TageszeitReactionsComp
        extends AbstractReactionsComp
        implements ITimePassedReactions {
    public TageszeitReactionsComp(final Narrator n,
                                  final World world) {
        super(TAGESZEIT, n, world);
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, @NonNull final AvDateTime endTime) {
        if (endTime.minus(startTime).longerThan(AvTimeSpan.ONE_DAY)) {
            // Die Action hat ohnehin erzählt, was passiert ist.
            return;
        }

        // FIXME WetterData berücksichtigen (verschieben?), insbesondere
        //  Bewölkung

        // FIXME Über den Tag verteilen: die Sonne steht schon hoch.. weit nach Mittag....
        //  Generell nicht nur an den "Tageszeitengrenzen" Texte erzeugen, sondern abhängig von
        //  der Uhrzeit?
        //  - Hinweise, dass die Nacht allmählich naht.

        // FIXME Je nach Ort unterscheiden:
        //  - Dunkelheit ist abhängig von Tageszeit (Raum nicht beleuchtet)
        //  - SC ist draußen / drinnen (z.B. im Wald)
        //  - SC ist unter offener Himmel (z.B. vor dem Schloss)
        //  Merken, wann der Benutzer den jeweiligen Status schon aktualisiert bekommen
        //  hat („Du trittst aus dem Wald hinaus. Rotes Abendrot erstreckt sich über den
        //  Horizont....“)

        // Es gab also einen (oder mehrere) Tageszeitenwechsel während einer Zeit von
        // weniger als einem Tag
        onTimePassed(startTime.getTageszeit(), endTime.getTageszeit());
    }

    private void onTimePassed(final Tageszeit lastTageszeit,
                              final Tageszeit currentTageszeit) {
        if (lastTageszeit == currentTageszeit) {
            // Entweder ist nur wenig Zeit vergangen - oder mehr als ein Tag, dann hat die Action
            // sicher ohnehin erzählt, was passiert ist.
            return;
        }

        switch (lastTageszeit) {
            case NACHTS:
                onTimePassedFromNachtsTo(currentTageszeit);
                return;
            case MORGENS:
                onTimePassedFromMorgensTo(currentTageszeit);
                return;
            case TAGSUEBER:
                onTimePassedFromTagsueberTo(currentTageszeit);
                return;
            case ABENDS:
                onTimePassedFromAbendsTo(currentTageszeit);
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + lastTageszeit);
        }
    }

    private void onTimePassedFromNachtsTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case MORGENS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Allmählich ist es Morgen geworden"
                                // Der Tageszeitenwechsel ist parallel passiert.
                        ),
                        neuerSatz("Der nächste Tag ist angebrochen"),
                        neuerSatz("Langsam graut der Morgen"),
                        neuerSatz("Langsam wird es hell"),
                        neuerSatz("Unterdessen ist es hell geworden"),
                        neuerSatz("Die Sonne geht auf")

                        // FIXME So etwas ermöglichen, wenn der Spieler sich
                        //  DRAUSSEN aufhält
                        //  allg("Im Osten kündigt sich der neue Tag an")
                        //  "Die Sterne verblassen und die Sonne ist am Horizont zu sehen"
                );
                return;
            case TAGSUEBER:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Inzwischen ist es hellichter Tag"),
                        neuerSatz("Der andere Tag hat begonnen"),
                        neuerSatz("Zwischenzeitlich ist die Sonne aufgegangen")
                );
                return;
            case ABENDS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                        neuerSatz("Inzwischen wird es schon wieder dunkel"),
                        neuerSatz("Der Tag ist schon fast vorüber"),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen")
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromMorgensTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case TAGSUEBER:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Die Sonne steigt langsam am Firmament "
                                + "empor"),
                        neuerSatz("Die Sonne ist aufgegangen und beginnt ihren Lauf")
                );
                return;
            case ABENDS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Währenddessen ist der Tag vergangen und die Sonne steht "
                                + "schon tief am Himmel"),
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen"),
                        neuerSatz("Inzwischen wird es schon wieder dunkel"),
                        neuerSatz("Der Tag ist schon fast vorüber"),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen")
                );
                return;
            case NACHTS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Die Sonne ist über die Zeit untergegangen"),
                        neuerSatz("Jetzt ist es dunkel"),
                        neuerSatz("Jetzt ist es Nacht und man sieht nur noch wenig"),
                        neuerSatz("Die Sonne ist jetzt untergegangen"),
                        neuerSatz(PARAGRAPH, "Darüber ist es vollständig dunkel "
                                + "geworden. Nur noch "
                                + "die Sterne und der Mond spenden ein wenig Licht"),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                        neuerSatz(PARAGRAPH, "Es ist Nacht geworden und man sieht nicht mehr "
                                + "so gut"));
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromTagsueberTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case ABENDS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz(PARAGRAPH, "Allmählich wird es abendlich dunkel"),
                        neuerSatz(PARAGRAPH, "Der Tag neigt sich und langsam beginnt der Abend"),
                        neuerSatz(PARAGRAPH, "Der Tag neigt sich und allmählich bricht "
                                + "der Abend an"),
                        neuerSatz(PARAGRAPH, "Die Abenddämmerung beginnt"),
                        neuerSatz(PARAGRAPH, "Inzwischen steht die Sonne schon tief")
                        // STORY Noch ein paar Texte frü den Beginn des Abends
                        // TODO WENN DER SPIELER DRAUSSEN IST "Heute ist ein schönes Abendrot zu
                        //  sehen"
                );
                return;
            case NACHTS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz(PARAGRAPH, "Die Sonne ist jetzt untergegangen"),
                        neuerSatz(PARAGRAPH, "Es ist dunkel geworden"),
                        neuerSatz(PARAGRAPH, "Die Sonne ist untergegangen"),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es Nacht und man sieht nicht "
                                + "mehr so gut"),
                        neuerSatz(PARAGRAPH, "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                + "die Sterne und der Mond geben etwas Licht"),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                        neuerSatz(PARAGRAPH, "Es ist Nacht geworden und man sieht nicht mehr "
                                + "so gut")
                );
                return;
            case MORGENS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz(PARAGRAPH, "Unterdessen hat der neue Tag begonnen",
                                PARAGRAPH),
                        neuerSatz(PARAGRAPH, "Es ist schon der nächste Morgen"),
                        neuerSatz(PARAGRAPH, "Die Nacht ist vorbei und es wird schon "
                                + "wieder hell"),
                        neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder hell geworden"),
                        neuerSatz("Die Sonne geht gerade auf")
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromAbendsTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case NACHTS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Die Sonne ist jetzt untergegangen"),
                        neuerSatz(PARAGRAPH,
                                "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                        + "die Sterne und der Mond spenden ein wenig Licht"),
                        neuerSatz(PARAGRAPH, "Inzwischen ist die Nacht hereingebrochen"),
                        neuerSatz(PARAGRAPH,
                                "Es ist Nacht geworden und man sieht nicht "
                                        + "mehr so gut")
                        // FIXME wenn der SC draußen ist:
                        //  "Jetzt sind am Himmel die Sterne zu sehen. Es ist dunkel und in der
                        //  Ferne "
                        //  + "ruft ein Käuzchen"
                );
                return;
            case MORGENS:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Unterdessen hat der neue Tag begonnen", PARAGRAPH),
                        neuerSatz("Es ist schon der nächste Morgen"),
                        neuerSatz("Die Nacht ist vorbei und es wird schon "
                                + "wieder hell"),
                        neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder "
                                + "hell geworden"),
                        neuerSatz("Die Sonne geht gerade auf")
                );
                return;
            case TAGSUEBER:
                n.narrateAlt(AvTimeSpan.NO_TIME,
                        neuerSatz("Es ist schon wieder heller Tag"),
                        neuerSatz("Die Sonne ist schon wieder aufgegangen")
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }
}
