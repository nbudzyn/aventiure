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
public abstract class AbstractStateComp<S extends Enum<S>>
        extends AbstractStatefulComponent<StatePCD> {
    private final Class<S> stateEnumClass;
    private final S initialState;

    private final AvNowDao nowDao;

    /**
     * Constructor for a {@link AbstractStateComp}.
     */
    protected AbstractStateComp(final GameObjectId gameObjectId,
                                final AvDatabase db,
                                final Class<S> stateEnumClass,
                                final S initialState) {
        super(gameObjectId, db.stateDao());
        this.stateEnumClass = stateEnumClass;
        this.initialState = initialState;

        nowDao = db.nowDao();
    }

    @Override
    @NonNull
    protected StatePCD createInitialState() {
        return new StatePCD(
                getGameObjectId(),
                toString(initialState),
                nowDao.now());
    }

    public boolean hasState(final S... alternatives) {
        for (final S test : alternatives) {
            if (getState().equals(test)) {
                return true;
            }
        }

        return false;
    }

    public S getState() {
        return fromString(getPcd().getState());
    }

    public void setState(final S state) {
        if (state.equals(getState())) {
            return;
        }

        getPcd().setState(toString(state));
        getPcd().setStateDateTime(nowDao.now());
    }

    public AvDateTime getStateDateTime() {
        return getPcd().getStateDateTime();
    }

    private S fromString(@NonNull final String stateString) {
        return Enum.valueOf(stateEnumClass, stateString);
    }

    private static <S extends Enum<S>> String toString(@NonNull final S state) {
        return state.name();
    }
}