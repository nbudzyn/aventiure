package de.nb.aventiure2.data.world.syscomp.state;

import static java.util.Arrays.asList;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.function.Predicate;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;

/**
 * Component für ein {@link GameObject}: Das Game Object hat einen Zustand (der sich
 * über die Zeit ändern kann). Möglicherweise kann auch der SC den Zustand des Objekts
 * direkt ändern.
 */
public abstract class AbstractStateComp<S extends Enum<S>>
        extends AbstractStatefulComponent<StatePCD>
        implements IWorldLoaderMixin, IWorldDescriptionMixin {
    private final TimeTaker timeTaker;

    protected final World world;

    private final Class<S> stateEnumClass;
    private final S initialState;

    /**
     * Constructor for a {@link AbstractStateComp}.
     */
    @SuppressWarnings("unchecked")
    protected AbstractStateComp(final GameObjectId gameObjectId,
                                final AvDatabase db,
                                final TimeTaker timeTaker,
                                final World world,
                                final S initialState) {
        super(gameObjectId, db.stateDao());
        this.timeTaker = timeTaker;
        this.world = world;
        stateEnumClass = (Class<S>) initialState.getClass();
        this.initialState = initialState;
    }

    @Override
    @NonNull
    protected StatePCD createInitialState() {
        return new StatePCD(
                getGameObjectId(),
                toString(initialState),
                timeTaker.now());
    }

    @SafeVarargs
    public final boolean hasState(final S... alternatives) {
        return hasState(asList(alternatives)::contains);
    }

    public final boolean hasState(final Predicate<S> predicate) {
        return predicate.test(getState());
    }

    public S getState() {
        return fromString(requirePcd().getState());
    }

    public void narrateAndSetState(final S state) {
        if (state == getState()) {
            return;
        }

        final S oldState = getState();

        setState(state);

        world.narrateAndDoReactions().onStateChanged(getGameObjectId(), oldState, state);
    }

    public void setState(final S state) {
        if (state.equals(getState())) {
            return;
        }

        requirePcd().setState(toString(state));
        requirePcd().setStateDateTime(timeTaker.now());
    }

    public AvDateTime getStateDateTime() {
        return requirePcd().getStateDateTime();
    }

    private S fromString(@NonNull final String stateString) {
        return Enum.valueOf(stateEnumClass, stateString);
    }

    private static <S extends Enum<S>> String toString(@NonNull final S state) {
        return state.name();
    }

    public Class<S> getStateEnumClass() {
        return stateEnumClass;
    }

    public abstract ImmutableList<StateModification<S>> getScStateModificationData();

    @Override
    public World getWorld() {
        return world;
    }
}
