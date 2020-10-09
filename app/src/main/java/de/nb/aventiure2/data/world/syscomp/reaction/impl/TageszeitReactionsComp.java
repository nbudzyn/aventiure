package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.time.*;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;

/**
 * Sorgt dafür, dass Sätze erzählt werden wie "Allmählich wird es Abend".
 */
public class TageszeitReactionsComp
        extends AbstractReactionsComp
        implements ITimePassedReactions {
    public TageszeitReactionsComp(final AvDatabase db, final World world) {
        super(TAGESZEIT, db, world);
    }

    @Override
    public void onTimePassed(final AvDateTime lastTime, @NonNull final AvDateTime now) {
        if (now.minus(lastTime).longerThan(AvTimeSpan.ONE_DAY)) {
            // Die Action hat ohnehin erzählt, was passiert ist.
            return;
        }

        // Es gab also einen (oder mehrere) Tageszeitenwechsel während einer Zeit von
        // weniger als einem Tag
        onTimePassed(lastTime.getTageszeit(), now.getTageszeit());
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
                n.narrateAlt(
                        neuerSatz("Allmählich ist es Morgen geworden",
                                // Der Tageszeitenwechsel ist parallel passiert.
                                noTime()),
                        neuerSatz("Der nächste Tag ist angebrochen", noTime()),
                        neuerSatz("Langsam graut der Morgen", noTime()),
                        neuerSatz("Langsam wird es hell", noTime()),
                        neuerSatz("Unterdessen ist es hell geworden", noTime()),
                        neuerSatz("Die Sonne geht auf", noTime())

                        // STORY So etwas ermöglichen, wenn der Spieler sich
                        //  DRAUSSEN aufhält
                        //  allg("Im Osten kündigt sich der neue Tag an", noTime())
                        //  "Die Sterne verblassen und die Sonne ist am Horizont zu sehen"
                );
                return;
            case TAGSUEBER:
                n.narrateAlt(
                        neuerSatz("Inzwischen ist es hellichter Tag", noTime()),
                        neuerSatz("Der andere Tag hat begonnen", noTime()),
                        neuerSatz("Zwischenzeitlich ist die Sonne aufgegangen", noTime())
                );
                return;
            case ABENDS:
                n.narrateAlt(
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen", noTime()),
                        neuerSatz("Inzwischen wird es schon wieder dunkel", noTime()),
                        neuerSatz("Der Tag ist schon fast vorüber", noTime()),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen", noTime())
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromMorgensTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case TAGSUEBER:
                n.narrateAlt(
                        neuerSatz("Die Sonne steigt langsam am Firmament "
                                + "empor", noTime()),
                        neuerSatz("Die Sonne ist aufgegangen und beginnt ihren Lauf",
                                noTime())
                );
                return;
            case ABENDS:
                n.narrateAlt(
                        neuerSatz("Währenddessen ist der Tag vergangen und die Sonne steht "
                                + "schon tief am Himmel", noTime()),
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen", noTime()),
                        neuerSatz("Inzwischen wird es schon wieder dunkel", noTime()),
                        neuerSatz("Der Tag ist schon fast vorüber", noTime()),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen", noTime())
                );
                return;
            case NACHTS:
                // TODO Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                // Schlafen wieder wach / NEUTRAL)

                n.narrateAlt(
                        neuerSatz("Die Sonne ist über die Zeit untergegangen",
                                noTime()),
                        neuerSatz("Jetzt ist es dunkel",
                                noTime()),
                        neuerSatz("Jetzt ist es Nacht und man sieht nur noch wenig",
                                noTime()),
                        neuerSatz("Die Sonne ist jetzt untergegangen",
                                noTime()),
                        neuerSatz(PARAGRAPH, "Darüber ist es vollständig dunkel geworden. Nur noch "
                                        + "die Sterne und der Mond spenden ein wenig Licht",
                                noTime()),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden",
                                noTime()),
                        neuerSatz(PARAGRAPH, "Es ist Nacht geworden und man sieht nicht mehr "
                                        + "so gut",
                                noTime()));
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromTagsueberTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case ABENDS:
                n.narrateAlt(
                        neuerSatz(PARAGRAPH, "Allmählich wird es "
                                + "abendlich dunkel", noTime()),
                        neuerSatz(PARAGRAPH, "Der Tag neigt sich und der Abend beginnt", noTime()),
                        neuerSatz(PARAGRAPH, "Der Tag neigt sich und der Abend bricht an",
                                noTime()),
                        neuerSatz(PARAGRAPH, "Die Abenddämmerung beginnt", noTime()),
                        neuerSatz(PARAGRAPH, "Inzwischen steht die Sonne schon tief", noTime())
                        // STORY Noch ein paar Texte!
                        // STORY WENN DER SPIELER DRAUSSEN IST "Heute ist ein schönes Abendrot zu sehen"
                );
                return;
            case NACHTS:
                // TODO Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                // Schlafen wieder wach / NEUTRAL)
                n.narrateAlt(
                        neuerSatz(PARAGRAPH, "Die Sonne ist jetzt untergegangen", noTime()),
                        neuerSatz(PARAGRAPH, "Es ist dunkel geworden", noTime()),
                        neuerSatz(PARAGRAPH, "Die Sonne ist untergegangen", noTime()),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es Nacht und man sieht nicht "
                                + "mehr so gut", noTime()),
                        neuerSatz(PARAGRAPH, "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                + "die Sterne und der Mond geben etwas Licht", noTime()),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden", noTime()),
                        neuerSatz(PARAGRAPH, "Es ist Nacht geworden und man sieht nicht mehr "
                                + "so gut", noTime())
                );
                return;
            case MORGENS:
                n.narrateAlt(
                        neuerSatz(PARAGRAPH, "Unterdessen hat der neue Tag begonnen",
                                noTime())
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH, "Es ist schon der nächste Morgen",
                                noTime()),
                        neuerSatz(PARAGRAPH, "Die Nacht ist vorbei und es wird schon "
                                        + "wieder hell",
                                noTime()),
                        neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder hell geworden",
                                noTime()),
                        neuerSatz("Die Sonne geht gerade auf", noTime())
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromAbendsTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case NACHTS:
                // STORY Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                //  Schlafen wieder wach / NEUTRAL)

                n.narrateAlt(
                        neuerSatz("Die Sonne ist jetzt untergegangen",
                                noTime()),
                        neuerSatz(PARAGRAPH,
                                "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                        + "die Sterne und der Mond spenden ein wenig Licht",
                                noTime()),
                        neuerSatz(PARAGRAPH, "Inzwischen ist die Nacht hereingebrochen",
                                noTime()),
                        neuerSatz(PARAGRAPH,
                                "Es ist Nacht geworden und man sieht nicht "
                                        + "mehr so gut",
                                noTime())
                        // STORY, wenn der SC draußen ist:
                        //  "Jetzt sind am Himmel die Sterne zu sehen. Es ist dunkel und in der Ferne "
                        //  + "ruft ein Käuzchen"
                );
                return;
            case MORGENS:
                n.narrateAlt(
                        neuerSatz("Unterdessen hat der neue Tag begonnen",
                                noTime())
                                .beendet(PARAGRAPH),
                        neuerSatz("Es ist schon der nächste Morgen",
                                noTime()),
                        neuerSatz("Die Nacht ist vorbei und es wird schon "
                                + "wieder hell", noTime()),
                        neuerSatz(PARAGRAPH, "Unterdessen ist es schon wieder "
                                + "hell geworden", noTime()),
                        neuerSatz("Die Sonne geht gerade auf", noTime())
                );
                return;
            case TAGSUEBER:
                n.narrateAlt(
                        neuerSatz("Es ist schon wieder heller Tag", noTime()),
                        neuerSatz("Die Sonne ist schon wieder aufgegangen", noTime())
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }
}
