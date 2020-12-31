package de.nb.aventiure2.data.time;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.noTime;

/**
 * Keeps track of the current time in the world.
 */
public class TimeTaker {
    private static volatile TimeTaker INSTANCE;

    private final AvNowDao dao;

    @Nullable
    private AvDateTime cachedNow;

    public static TimeTaker getInstance(final AvDatabase db) {
        if (INSTANCE == null) {
            synchronized (TimeTaker.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TimeTaker(db);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    @WorkerThread
    public static void reset() {
        INSTANCE = null;
    }

    /**
     * Constructor for a {@link TimeTaker}.
     */
    private TimeTaker(final AvDatabase db) {
        dao = db.nowDao();
    }

    public void saveInitialState() {
        cachedNow =
                new AvDateTime(
                        1, oClock(14, 30));
        save();
    }

    /**
     * Sets the current date and time in the world, adding this passed time.
     * Do NOT use this within an action implementation or a reaction.
     * {@link AbstractScAction#doAndPassTime()} deals with this.
     */
    public void passTime(final AvTimeSpan timePassed) {
        if (timePassed.equals(noTime())) {
            return;
        }

        setNow(now().plus(timePassed));
    }

    /**
     * Richtig in die Datenbank gespeichert wird erst bei {@link #save()}.
     */
    public void setNow(final AvDateTime dateTime) {
        cachedNow = dateTime;
    }

    public AvDateTime now() {
        if (cachedNow == null) {
            cachedNow = dao.now().getNow();
        }

        return cachedNow;
    }

    public void save() {
        if (cachedNow != null) {
            dao.setNow(cachedNow);
        }

        cachedNow = null;
    }
}
