package de.nb.aventiure2.playeraction.action.invisible.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.data.world.time.Tageszeit;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.world.invisible.Invisible.Key.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.invisible.InvisibleState.BEGONNEN;
import static de.nb.aventiure2.data.world.invisible.Invisibles.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.room.AvRoom.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public class TageszeitReactions extends AbstractInvisibleReactions {
    TageszeitReactions(final AvDatabase db,
                       final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        if (now.minus(lastTime).longerThan(AvTimeSpan.ONE_DAY)) {
            // Die Action hat ohnehin erzählt, was passiert ist.
            return noTime();
        }

        // Es gab also einen (oder mehrere) Tageszeitenwechsel während einer Zeit von
        // weniger als einem Tag
        onTimePassed(lastTime.getTageszeit(), now.getTageszeit());

        // Der Tageszeitenwechsel (wenn es überhaupt einen gab) ist parallel passiert.
        return noTime();
    }

    private void onTimePassed(final Tageszeit lastTageszeit,
                              final Tageszeit currentTageszeit) {
        if (lastTageszeit == currentTageszeit) {
            // Entweder ist nur wenig Zeit vergangen - oder die Action hat ohnehin erzählt,
            // was passiert ist.
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

    private void onTimePassedFromNachtsTo(final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case MORGENS:
                n.add(
                        alt(
                                t(SENTENCE, "Allmählich ist es Morgen geworden"),
                                t(SENTENCE, "Der nächste Tag ist angebrochen"),
                                t(SENTENCE, "Langsam graut der Morgen"),
                                t(SENTENCE, "Langsam wird es hell"),
                                t(SENTENCE, "Unterdessen ist es hell geworden"),
                                t(SENTENCE, "Die Sonne geht auf")
                                // TODO So etwas ermöglichen, wenn der Spieler sich
                                // DRAUSSEN aufhält
                                // t(SENTENCE, "Im Osten kündigt sich der neue Tag an")
                        )
                );
                return;
            case TAGSUEBER:
                n.add(
                        alt(
                                t(SENTENCE, "Inzwischen ist es hellichter Tag"),
                                t(SENTENCE, "Der andere Tag hat begonnen"),
                                t(SENTENCE, "Zwischenzeitlich ist die Sonne aufgegangen")
                        )
                );
                return;
            case ABENDS:
                n.add(
                        alt(
                                t(SENTENCE, "Inzwischen ist beinahe der ganze Tag vergangen"),
                                t(SENTENCE, "Inzwischen wird es schon wieder dunkel"),
                                t(SENTENCE, "Der Tag ist schon fast vorüber"),
                                t(SENTENCE, "Die Sonne ist schon wieder am Untergehen")
                        )
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromMorgensTo(final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case TAGSUEBER:
                // Das ist irrelevant
                return;
            case ABENDS:
                n.add(
                        alt(
                                t(SENTENCE,
                                        "\"Währenddessen ist der Tag vergangen und die Sonne steht schon tief \"\n"
                                                + "                        + \"am Himmel\""),
                                t(SENTENCE, "Inzwischen ist beinahe der ganze Tag vergangen"),
                                t(SENTENCE, "Inzwischen wird es schon wieder dunkel"),
                                t(SENTENCE, "Der Tag ist schon fast vorüber"),
                                t(SENTENCE, "Die Sonne ist schon wieder am Untergehen")
                        )
                );
                return;
            case NACHTS:
                // TODO Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                // Schlafen wieder wach / NEUTRAL)

                n.add(
                        alt(
                                t(SENTENCE, "Die Sonne ist über die Zeit untergegangen"),
                                t(SENTENCE, "Jetzt ist es dunkel"),
                                t(SENTENCE, "Jetzt ist es Nacht und man sieht nur noch wenig"),
                                t(SENTENCE, "Die Sonne ist jetzt untergegangen"),
                                t(PARAGRAPH,
                                        "Darüber ist es vollständig dunkel geworden. Nur noch "
                                                + "die Sterne und der "
                                                + "Mond spenden ein wenig Licht"),
                                t(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                                t(PARAGRAPH,
                                        "Es ist Nacht geworden und man sieht nicht mehr "
                                                + "so gut")
                        )
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromTagsueberTo(final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case ABENDS:
                n.add(
                        alt(
                                t(PARAGRAPH, "Allmählich wird es "
                                        + "abendlich dunkel"),
                                t(PARAGRAPH, "Langsam wird es dunkel"),
                                t(PARAGRAPH, "Der Tag neigt sich und es wird dunkler"),
                                t(PARAGRAPH, "Inzwischen steht die Sonne schon tiefer")
                                // TODO WENN DER SPIELER DRAUSSEN IST "Heute ist ein schönes Abendrot zu sehen"
                        )
                );
                return;

            case NACHTS:
                // TODO Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                // Schlafen wieder wach / NEUTRAL)
                n.add(
                        alt(
                                t(SENTENCE, "Die Sonne ist jetzt untergegangen"),
                                t(PARAGRAPH, "Es ist dunkel geworden"),
                                t(SENTENCE, "Die Sonne ist untergegangen"),
                                // TODO Bei Beleuchtung sieht man auch nachts noch gut!
                                //  Z.B. Im Schloss
                                t(SENTENCE,
                                        "Inzwischen ist es Nacht und man sieht nicht "
                                                + "mehr so gut"),
                                t(PARAGRAPH,
                                        "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                                + "die Sterne und der "
                                                + "Mond geben etwas Licht"),
                                t(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                                t(PARAGRAPH,
                                        "Es ist Nacht geworden und man sieht nicht mehr "
                                                + "so gut")
                        )
                );
                return;
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
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private void onTimePassedFromAbendsTo(final Tageszeit currentTageszeit) {
        switch (currentTageszeit) {
            case NACHTS:
                // TODO Der Spieler könnte abends MÜDE werden (und morgens oder nach dem
                // Schlafen wieder wach / NEUTRAL)

                n.add(
                        alt(
                                t(SENTENCE, "Die Sonne ist jetzt untergegangen"),
                                t(PARAGRAPH,
                                        "Es ist jetzt vollständig dunkel geworden. Nur noch "
                                                + "die Sterne und der "
                                                + "Mond spenden ein wenig Licht"),
                                t(PARAGRAPH, "Inzwischen ist es dunkel geworden"),
                                t(PARAGRAPH,
                                        "Es ist Nacht geworden und man sieht nicht mehr so gut")
                        )
                );
                return;
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
                return;
            case TAGSUEBER:
                n.add(
                        alt(
                                t(SENTENCE, "Es ist schon wieder heller Tag"),
                                t(SENTENCE, "Die Sonne ist schon wieder aufgegangen")
                        )
                );
                return;
            default:
                throw new IllegalStateException("Unerwartete Tageszeit: " + currentTageszeit);
        }
    }

    private AvTimeSpan schlossfestBeginnt() {
        final AvRoom currentRoom = db.playerLocationDao().getPlayerLocation().getRoom();

        final AvTimeSpan timeElapsed;
        if (currentRoom == DRAUSSEN_VOR_DEM_SCHLOSS) {
            n.add(t(PARAGRAPH, "Dir fällt auf, dass Handwerker dabei sind, überall "
                    + "im Schlossgarten kleine bunte Pagoden aufzubauen. Du schaust eine Weile "
                    + "zu, und wie es scheint, beginnen von überallher Menschen zu "
                    + "strömen. Aus dem Schloss weht dich der Geruch von Gebratenem an."));

            // Der Spieler weiß jetzt, dass das Schlossfest läuft
            db.counterDao().inc(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN);

            timeElapsed = mins(30);
        } else {
            timeElapsed = noTime();  // Passiert nebenher und braucht KEINE zusätzliche Zeit
        }

        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.NEUTRAL);
        db.invisibleDataDao().setState(SCHLOSSFEST, BEGONNEN);
        return timeElapsed;
    }
}
