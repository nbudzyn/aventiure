package de.nb.aventiure2.scaction.action.invisible.reaction;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.data.world.time.Tageszeit;

import static de.nb.aventiure2.data.storystate.StoryStateBuilder.t;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.TAGESZEIT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

public class TageszeitReactions
        extends AbstractInvisibleReactions<IGameObject> {
    TageszeitReactions(final AvDatabase db) {
        super(db, TAGESZEIT);
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

                n.add(
                        alt(
                                t(SENTENCE, "Die Sonne ist über die Zeit untergegangen"),
                                t(SENTENCE, "Jetzt ist es dunkel"),
                                t(SENTENCE, "Jetzt ist es Nacht und man sieht nur noch wenig"),
                                t(SENTENCE, "Die Sonne ist jetzt untergegangen"),
                                t(PARAGRAPH, "Darüber ist es vollständig dunkel geworden. Nur noch "
                                        + "die Sterne und der Mond spenden ein wenig Licht"),
                                t(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                                t(PARAGRAPH, "Es ist Nacht geworden und man sieht nicht mehr "
                                        + "so gut")));
                return noTime();
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
                n.add(
                        alt(
                                t(PARAGRAPH, "Unterdessen hat der neue Tag begonnen")
                                        .beendet(PARAGRAPH),
                                t(PARAGRAPH, "Es ist schon der nächste Morgen"),
                                t(PARAGRAPH, "Die Nacht ist vorbei und es wird schon "
                                        + "wieder hell"),
                                t(PARAGRAPH, "Unterdessen ist es schon wieder hell geworden"),
                                t(SENTENCE, "Die Sonne geht gerade auf")
                        )
                );
                return noTime();
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private AvTimeSpan onTimePassedFromAbendsTo(@NonNull final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case NACHTS:
                // TODO Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                // Schlafen wieder wach / NEUTRAL)

                n.add(
                        alt(
                                t(SENTENCE, "Die Sonne ist jetzt untergegangen"),
                                t(PARAGRAPH,
                                        "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                                + "die Sterne und der Mond spenden ein wenig Licht"),
                                t(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                                t(PARAGRAPH,
                                        "Es ist Nacht geworden und man sieht nicht mehr so gut")
                        )
                );
                return noTime();
            case MORGENS:
                n.add(
                        alt(
                                t(SENTENCE, "Unterdessen hat der neue Tag begonnen")
                                        .beendet(PARAGRAPH),
                                t(SENTENCE, "Es ist schon der nächste Morgen"),
                                t(SENTENCE, "Die Nacht ist vorbei und es wird schon "
                                        + "wieder hell"),
                                t(PARAGRAPH, "Unterdessen ist es schon wieder hell geworden"),
                                t(SENTENCE, "Die Sonne geht gerade auf")
                        )
                );
                return noTime();
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
