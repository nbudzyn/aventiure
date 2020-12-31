package de.nb.aventiure2.data.world.syscomp.waiting;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;

/**
 * Component for a {@link GameObject}: The game object
 * can wait for something.
 */
public class WaitingComp extends AbstractStatefulComponent<WaitingPCD> {
    @NonNull
    private final TimeTaker timeTaker;

    @NonNull
    private final AvDateTime initialEndTime;

    public WaitingComp(final GameObjectId gameObjectId,
                       final AvDatabase db, final TimeTaker timeTaker) {
        this(gameObjectId, db,
                // Warten ist schon vorbei
                timeTaker, new AvDateTime(0, 0, 0));
    }

    private WaitingComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final TimeTaker timeTaker,
                        final AvDateTime initialEndTime) {
        super(gameObjectId, db.waitingDao());
        this.timeTaker = timeTaker;
        this.initialEndTime = initialEndTime;
    }

    @Override
    protected WaitingPCD createInitialState() {
        return new WaitingPCD(getGameObjectId(), initialEndTime);
    }

    public void startWaiting(final AvDateTime endTime) {
        getPcd().setEndTime(endTime);
    }

    public void stopWaiting() {
        getPcd().setEndTime(timeTaker.now().minus(secs(1)));
    }

    public AvDateTime getEndTime() {
        return getPcd().getEndTime();
    }
}
