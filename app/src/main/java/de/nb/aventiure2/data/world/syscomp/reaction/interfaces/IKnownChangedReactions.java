package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;

/**
 * Reactions to an {@link de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO}
 * changing, what they know.
 */
public interface IKnownChangedReactions extends IReactions {
    void onKnownChanged(final IHasMemoryGO knower,
                        final GameObjectId knowee,
                        final Known oldKnown,
                        final Known newKnown);
}
