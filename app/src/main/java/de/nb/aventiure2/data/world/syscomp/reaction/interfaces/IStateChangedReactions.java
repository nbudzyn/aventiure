package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Reactions to an {@link de.nb.aventiure2.data.world.syscomp.state.IHasStateGO}
 * changing his state.
 */
public interface IStateChangedReactions extends IReactions {
    AvTimeSpan onStateChanged(IHasStateGO<?> gameObject,
                              Enum<?> oldState,
                              Enum<?> newState);
}
