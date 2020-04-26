package de.nb.aventiure2.scaction.action.invisible.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.action.base.reaction.AbstractReactions;

abstract class AbstractInvisibleReactions<REACTOR extends IGameObject> extends AbstractReactions {
    protected final REACTOR reactor;

    public AbstractInvisibleReactions(final AvDatabase db,
                                      final GameObjectId reactorId) {
        super(db);
        reactor = (REACTOR) GameObjects.load(db, reactorId);
    }

    public REACTOR getReactor() {
        return reactor;
    }

    public abstract AvTimeSpan onTimePassed(AvDateTime lastTime, AvDateTime now);
}
