package de.nb.aventiure2.playeraction.action.invisible.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.invisible.Invisible.Key.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.invisible.InvisibleState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

class SchlossfestReactions extends AbstractInvisibleReactions {
    private static final AvDateTime SCHLOSSFEST_BEGINN_DATE_TIME =
            new AvDateTime(2,
                    oClock(5, 30));

    SchlossfestReactions(final AvDatabase db,
                         final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan timeElapsed = noTime();

        if (lastTime.isBefore(SCHLOSSFEST_BEGINN_DATE_TIME) &&
                !now.isBefore(SCHLOSSFEST_BEGINN_DATE_TIME)) {
            timeElapsed = timeElapsed.plus(schlossfestBeginnt());
        }

        return timeElapsed;
    }

    private AvTimeSpan schlossfestBeginnt() {
        db.invisibleDataDao().setState(SCHLOSSFEST, BEGONNEN);

        return noTime(); // Passiert nebenher und braucht KEINE zusätzliche Zeit
    }
}
