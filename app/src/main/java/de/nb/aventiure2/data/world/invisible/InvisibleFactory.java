package de.nb.aventiure2.data.world.invisible;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjectstate.GameObjectStateList;
import de.nb.aventiure2.data.world.gameobjectstate.IHasStateGO;
import de.nb.aventiure2.data.world.gameobjectstate.StateComp;

/**
 * A factory for special {@link GameObject}s: Invisible concepts, ideas, event or the like, that
 * have a state
 */
public class InvisibleFactory {
    private final AvDatabase db;

    public InvisibleFactory(final AvDatabase db) {
        this.db = db;
    }

    public GameObject create(final GameObjectId id, final GameObjectStateList states) {
        return new InvisibleFactory.Invisible(id, new StateComp(id, db, states));
    }

    private static class Invisible extends GameObject implements IHasStateGO {
        private final StateComp stateComp;

        public Invisible(final GameObjectId id, final StateComp stateComp) {
            super(id);
            // Jede Komponente muss registiert werden!
            this.stateComp = addComponent(stateComp);
        }

        @Nonnull
        @Override
        public StateComp stateComp() {
            return stateComp;
        }
    }

}
