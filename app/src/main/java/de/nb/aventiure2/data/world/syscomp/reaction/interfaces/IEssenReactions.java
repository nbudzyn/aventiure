package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;

/**
 * Reactions to a game object eating something.
 */
public interface IEssenReactions extends IReactions {
    /**
     * The game object eats something.
     */
    void onEssen(IGameObject gameObject);
}
