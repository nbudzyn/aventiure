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

    // IDEA Reaktionen aller IResponders auf alle Aktionen, die sie wahrnehmen.
    //  Aus dem Reaktionen der IResponders neue kleine
    //  plotlines bauen, die bald danach wieder in einen StoryStep einmünden

    // IDEA Jede eigentlich nicht vergesehene Interaktion mit einem
    //  NPC / Creature soll die Welt spürbar verändern.
    static <R extends IReactions> boolean reactsTo(
            final IGameObject gameObject,
            final Class<R> reactionsInterface) {
        return reactionsInterface.isInstance(((IResponder) gameObject).reactionsComp());
    }
}
