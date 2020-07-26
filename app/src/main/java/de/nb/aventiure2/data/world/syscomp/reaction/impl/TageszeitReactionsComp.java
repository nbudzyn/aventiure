package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.data.world.time.Tageszeit;

import static de.nb.aventiure2.data.world.gameobject.World.TAGESZEIT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

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
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, @NonNull final AvDateTime now) {
        if (now.minus(lastTime).longerThan(AvTimeSpan.ONE_DAY)) {
            // Die Action hat ohnehin erzählt, was passiert ist.
            return noTime();
        }

        // Es gab also einen (oder mehrere) Tageszeitenwechsel während einer Zeit von
        // weniger als einem Tag
        return onTimePassed(lastTime.getTageszeit(), now.getTageszeit());
    }

    private AvTimeSpan onTimePassed(final Tageszeit lastTageszeit,
                                    final Tageszeit currentTageszeit) {
        if (lastTageszeit == currentTageszeit) {
            // Entweder ist nur wenig Zeit vergangen - oder mehr als ein Tag, dann hat die Action
            // sicher ohnehin erzählt, was passiert ist.
            return noTime();
        }

        switch (lastTageszeit) {
            case NACHTS:
                return onTimePassedFromNachtsTo(currentTageszeit);
            case MORGENS:
                return onTimePassedFromMorgensTo(currentTageszeit);
            case TAGSUEBER:
                return onTimePassedFromTagsueberTo(currentTageszeit);
            case ABENDS:
                return onTimePassedFromAbendsTo(currentTageszeit);
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + lastTageszeit);
        }
    }

    private AvTimeSpan onTimePassedFromNachtsTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case MORGENS:
                return n.addAlt(
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
                );
            case TAGSUEBER:
                return n.addAlt(
                        neuerSatz("Inzwischen ist es hellichter Tag", noTime()),
                        neuerSatz("Der andere Tag hat begonnen", noTime()),
                        neuerSatz("Zwischenzeitlich ist die Sonne aufgegangen", noTime())
                );
            case ABENDS:
                return n.addAlt(
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen", noTime()),
                        neuerSatz("Inzwischen wird es schon wieder dunkel", noTime()),
                        neuerSatz("Der Tag ist schon fast vorüber", noTime()),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen", noTime())
                );
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private AvTimeSpan onTimePassedFromMorgensTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case TAGSUEBER:
                // Das ist irrelevant
                return noTime();
            case ABENDS:
                return n.addAlt(
                        neuerSatz("Währenddessen ist der Tag vergangen und die Sonne steht "
                                + "schon tief am Himmel", noTime()),
                        neuerSatz("Inzwischen ist beinahe der ganze Tag vergangen", noTime()),
                        neuerSatz("Inzwischen wird es schon wieder dunkel", noTime()),
                        neuerSatz("Der Tag ist schon fast vorüber", noTime()),
                        neuerSatz("Die Sonne ist schon wieder am Untergehen", noTime())
                );
            case NACHTS:
                // TODO Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                // Schlafen wieder wach / NEUTRAL)

                return n.addAlt(
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
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private AvTimeSpan onTimePassedFromTagsueberTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case ABENDS:
                return n.addAlt(
                        neuerSatz(PARAGRAPH, "Allmählich wird es "
                                + "abendlich dunkel", noTime()),
                        neuerSatz(PARAGRAPH, "Langsam wird es dunkel", noTime()),
                        neuerSatz(PARAGRAPH, "Der Tag neigt sich und es wird dunkler", noTime()),
                        neuerSatz(PARAGRAPH, "Inzwischen steht die Sonne schon tiefer", noTime())
                        // TODO WENN DER SPIELER DRAUSSEN IST "Heute ist ein schönes Abendrot zu sehen"
                );

            case NACHTS:
                // TODO Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                // Schlafen wieder wach / NEUTRAL)
                return n.addAlt(
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
            case MORGENS:
                return n.addAlt(
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
                        neuerSatz("Die Sonne geht gerade auf",
                                noTime())
                );
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private AvTimeSpan onTimePassedFromAbendsTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case NACHTS:
                // TODO Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                // Schlafen wieder wach / NEUTRAL)

                return n.addAlt(
                        neuerSatz("Die Sonne ist jetzt untergegangen",
                                noTime()),
                        neuerSatz(PARAGRAPH,
                                "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                        + "die Sterne und der Mond spenden ein wenig Licht",
                                noTime()),
                        neuerSatz(PARAGRAPH, "Inzwischen ist es dunkel geworden",
                                noTime()),
                        neuerSatz(PARAGRAPH,
                                "Es ist Nacht geworden und man sieht nicht "
                                        + "mehr so gut",
                                noTime())
                );
            case MORGENS:
                return n.addAlt(
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
            case TAGSUEBER:
                return n.addAlt(
                        neuerSatz("Es ist schon wieder heller Tag", noTime()),
                        neuerSatz("Die Sonne ist schon wieder aufgegangen", noTime())
                );
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }
}
