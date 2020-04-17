package de.nb.aventiure2.scaction.action.invisible.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.player.stats.ScStateOfMind;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.world.invisible.InvisibleState.BEGONNEN;
import static de.nb.aventiure2.data.world.invisible.Invisibles.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.invisible.Invisibles.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.invisible.Invisibles.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.room.Rooms.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.time.AvDateTime.isWithin;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

class SchlossfestReactions extends AbstractInvisibleReactions {
    SchlossfestReactions(final AvDatabase db,
                         final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan timeElapsed = noTime();

        if (isWithin(SCHLOSSFEST_BEGINN_DATE_TIME, lastTime, now)) {
            timeElapsed = timeElapsed.plus(schlossfestBeginnt());
        }

        return timeElapsed;
    }

    private AvTimeSpan schlossfestBeginnt() {
        final GameObject currentRoom = db.playerLocationDao().getPlayerLocation().getRoom();

        final AvTimeSpan timeElapsed;
        if (currentRoom.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
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

        db.playerStatsDao().setStateOfMind(ScStateOfMind.NEUTRAL);
        db.invisibleDataDao().setState(SCHLOSSFEST, BEGONNEN);
        return timeElapsed;
    }
}
