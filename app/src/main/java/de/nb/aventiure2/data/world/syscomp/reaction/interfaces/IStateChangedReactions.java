package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;

/**
 * Reactions to an {@link de.nb.aventiure2.data.world.syscomp.state.IHasStateGO}
 * changing his state.
 */
public interface IStateChangedReactions extends IReactions {
    <S extends Enum<S>> void onStateChanged(final IHasStateGO<S> gameObject, final S oldState,
                                            final S newState);
}
