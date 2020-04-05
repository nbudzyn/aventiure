package de.nb.aventiure2.scaction.action.scautomaticreaction;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.world.player.stats.ScHunger;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.action.base.reaction.AbstractReactions;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.world.player.stats.ScHunger.HUNGRIG;
import static de.nb.aventiure2.data.world.player.stats.ScStats.ZEITSPANNE_NACH_ESSEN_BIS_WIEDER_HUNGRIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * "Automatische" Reaktionen des Spielercharakters, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Spielercharakter wird hungrig.)
 */
public class ScAutomaticReactions extends AbstractReactions {
    public ScAutomaticReactions(final AvDatabase db,
                                final Class<? extends IPlayerAction> scActionClass) {
        super(db, scActionClass);
    }

    public AvTimeSpan onWirdMitEssenKonfrontiert() {
        final ScHunger hunger = db.playerStatsDao().getPlayerStats().getHunger();
        switch (hunger) {
            case SATT:
                return noTime();
            case HUNGRIG:
                n.add(alt(
                        t(SENTENCE, "Dir läuft das Wasser im Munde zusammen")
                                .undWartest(),
                        t(SENTENCE, "Du hast Hunger")
                                .undWartest(),
                        t(SENTENCE, "Du bist hungrig")
                                .undWartest(),
                        t(SENTENCE, "Dir fällt auf, wie hungrig du bist")
                                .komma()
                                .undWartest()
                ));
                return noTime();
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan timeElapsed = noTime();

        final AvDateTime wiederHungrigAb = db.playerStatsDao().getPlayerStats().getZuletztGegessen()
                .plus(ZEITSPANNE_NACH_ESSEN_BIS_WIEDER_HUNGRIG);
        if (!now.isBefore(wiederHungrigAb)) {
            db.playerStatsDao().setHunger(HUNGRIG);

            if (lastTime.isBefore(wiederHungrigAb)) {
                timeElapsed = timeElapsed.plus(scWirdHungrig());
            }
        }

        return timeElapsed;
    }

    @NonNull
    private AvTimeSpan scWirdHungrig() {
        n.add(
                alt(
                        t(PARAGRAPH, "Du fühlst dich allmählich etwas hungrig")
                                .undWartest(),
                        t(SENTENCE, "Wann hast du eigentlich zuletzt etwas gegessen? Das "
                                + "muss schon eine Weile her sein."),
                        t(PARAGRAPH, "So langsam bekommst du Hunger"),
                        t(PARAGRAPH, "Allmählich überkommt dich der Hunger"),
                        t(PARAGRAPH, "Allmählich regt sich wieder der Hunger")
                                .undWartest(),
                        t(SENTENCE, "Dir fällt auf, dass du Hunger hast")
                                .komma(),
                        t(SENTENCE, "Du empfindest wieder leichter Hunger")
                                .undWartest()
                )
        );

        return noTime();
    }
}
