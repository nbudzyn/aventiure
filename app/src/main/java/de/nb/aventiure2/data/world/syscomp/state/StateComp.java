package de.nb.aventiure2.data.world.syscomp.state;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvNowDao;

/**
 * Component für ein {@link GameObject}: Das Game Object hat einen Zustand (der sich
 * über die Zeit ändern kann).
 */
public class StateComp extends AbstractStatefulComponent<StatePCD> {
    private final GameObjectStateList states;

    private final AvNowDao nowDao;

    /**
     * Constructor for a {@link StateComp}.
     *
     * @param states The first state is the initial state.
     */
    public StateComp(final GameObjectId gameObjectId,
                     final AvDatabase db,
                     final GameObjectStateList states) {
        super(gameObjectId, db.stateDao());
        this.states = states;

        nowDao = db.nowDao();
    }

    @Override
    @NonNull
    protected StatePCD createInitialState() {
        return new StatePCD(getGameObjectId(), states.getInitial(),
                nowDao.now());
    }

    public boolean hasState(final GameObjectState... alternatives) {
        return getPcd().hasState(alternatives);
    }

    public GameObjectState getState() {
        return getPcd().getState();
    }

    public void setState(final GameObjectState state) {
        if (!isStateAllowed(state)) {
            throw new IllegalArgumentException("State not allowed: " + state);
        }

        if (state.equals(getState())) {
            return;
        }

        getPcd().setState(state);
        getPcd().setStateDateTime(nowDao.now());
    }

    public AvDateTime getStateDateTime() {
        return getPcd().getStateDateTime();
    }

    private boolean isStateAllowed(final GameObjectState state) {
        return states.contains(state);
    }
}
