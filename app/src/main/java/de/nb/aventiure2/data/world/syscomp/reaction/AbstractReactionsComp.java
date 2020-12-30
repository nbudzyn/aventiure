package de.nb.aventiure2.data.world.syscomp.reaction;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;

/**
 * Component für ein {@link GameObject}: The game object might
 * react to certain events.
 */
public abstract class AbstractReactionsComp extends AbstractStatelessComponent
        implements IReactions {
    protected final AvDatabase db;
    protected final World world;

    protected final Narrator n;

    // IDEA Jeder Creature (jedem NPC) ein Ziel geben - oder in jedem NPC
    //  ein "Potenzial" anlegen (durch seine Werte oder Möglichkeiten),
    //  dass er im Rahmen seiner KI ausschöpfen möchte.

    // IDEA Zu jedem Schritt einen Konflikt / ein Hindernis vorsehen: Gegenspieler, Rivale o.Ä.

    // IDEA Player should care about their character / stuff / achievements / reputation.

    public AbstractReactionsComp(final GameObjectId id,
                                 final AvDatabase db,
                                 final Narrator n,
                                 final World world) {
        super(id);
        this.db = db;
        this.n = n;
        this.world = world;
    }

    @NonNull
    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }
}
