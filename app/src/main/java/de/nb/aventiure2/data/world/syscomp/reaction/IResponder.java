package de.nb.aventiure2.data.world.syscomp.reaction;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * A game object that reacts to certain events.
 */
@ParametersAreNonnullByDefault
public interface IResponder extends IGameObject {
    @Nonnull
    AbstractReactionsComp reactionsComp();

    static <R extends IReactions> boolean reactsTo(
            final IGameObject gameObject,
            final Class<R> reactionsInterface) {
        return reactionsInterface.isInstance(((IResponder) gameObject).reactionsComp());
    }
}
