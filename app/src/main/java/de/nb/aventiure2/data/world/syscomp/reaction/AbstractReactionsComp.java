package de.nb.aventiure2.data.world.syscomp.reaction;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;

/**
 * Component für ein {@link GameObject}: The game object might
 * react to certain events.
 */
public abstract class AbstractReactionsComp extends AbstractStatelessComponent
        implements IReactions,
        // Mixins
        IWorldLoaderMixin, IWorldDescriptionMixin {
    protected final World world;

    protected final Narrator n;

    // IDEA Jeder Creature (jedem NPC) ein Ziel geben - oder in jedem NPC
    //  ein "Potenzial" anlegen (durch seine Werte oder Möglichkeiten),
    //  dass er im Rahmen seiner KI ausschöpfen möchte.

    // IDEA Zu jedem Schritt einen Konflikt / ein Hindernis vorsehen: Gegenspieler, Rivale o.Ä.

    // IDEA Player should care about their character / stuff / achievements / reputation.

    protected AbstractReactionsComp(final GameObjectId id,
                                    final Narrator n,
                                    final World world) {
        super(id);
        this.n = n;
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }
}
