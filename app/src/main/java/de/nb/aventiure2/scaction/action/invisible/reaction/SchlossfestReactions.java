package de.nb.aventiure2.scaction.action.invisible.reaction;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.feelings.Mood;
import de.nb.aventiure2.data.world.gameobjectstate.IHasStateGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.gameobjectstate.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvDateTime.isWithin;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

class SchlossfestReactions
        extends AbstractInvisibleReactions<IHasStateGO> {
    SchlossfestReactions(final AvDatabase db) {
        super(db, SCHLOSSFEST);
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
        final @Nullable IGameObject currentRoom = sc.locationComp().getLocation();

        if (currentRoom == null) {
            return noTime();
        }

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

        sc.feelingsComp().setMood(Mood.NEUTRAL);
        getReactor().stateComp().setState(BEGONNEN);
        return timeElapsed;
    }
}
