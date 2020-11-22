package de.nb.aventiure2.data.world.syscomp.waiting;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;

/**
 * Component for a {@link GameObject}: The game object
 * can wait for something.
 */
public class WaitingComp extends AbstractStatefulComponent<WaitingPCD> {
    @NonNull
    private final AvDateTime initialEndTime;

    public WaitingComp(final GameObjectId gameObjectId,
                       final AvDatabase db) {
        this(gameObjectId, db,
                // Warten ist schon vorbei
                new AvDateTime(0, 0, 0));
    }

    private WaitingComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final AvDateTime initialEndTime) {
        super(gameObjectId, db.waitingDao());
        this.initialEndTime = initialEndTime;
    }

    @Override
    protected WaitingPCD createInitialState() {
        return new WaitingPCD(getGameObjectId(), initialEndTime);
    }

    public void startWaiting(final AvDateTime endTime) {
        getPcd().setEndTime(endTime);
    }

    public AvDateTime getEndTime() {
        return getPcd().getEndTime();
    }
}
