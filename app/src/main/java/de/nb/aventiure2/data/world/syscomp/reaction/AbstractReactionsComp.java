package de.nb.aventiure2.data.world.syscomp.reaction;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;

/**
 * Component für ein {@link GameObject}: The game object might
 * react to certain events.
 */
public abstract class AbstractReactionsComp extends AbstractStatelessComponent
        implements IReactions {
    protected final AvDatabase db;
    protected final World world;

    protected final NarrationDao n;

    // STORY Jeder Creature (jedem NPC) ein Ziel geben - oder in jedem NPC
    //  ein "Potenzial" anlegen (durch seine Werte oder Möglichkeiten),
    //  dass er im Rahmen seiner KI ausschöpfen möchte.

    // STORY Zu jedem Schritt einen Konflikt / ein Hindernis vorsehen: Gegenspieler, Rivale o.Ä.

    // STORY Der Wald kämpft (nachts) gegen den Spieler. Als wäre er böse.

    // STORY Player should care about their character / stuff / achievements / reputation.

    public AbstractReactionsComp(final GameObjectId id,
                                 final AvDatabase db,
                                 final World world) {
        super(id);
        this.db = db;
        this.world = world;

        n = db.narrationDao();
    }

    @NonNull
    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }
}
