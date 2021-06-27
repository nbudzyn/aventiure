package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.SchlossfestReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * A factory for special {@link GameObject}s: Invisible concepts, ideas, event or the like, that
 * have a state
 */
public class InvisibleFactory extends AbstractNarratorGameObjectFactory {
    InvisibleFactory(final AvDatabase db,
                     final TimeTaker timeTaker,
                     final Narrator n,
                     final World world) {
        super(db, timeTaker, n, world);
    }

    GameObject createSchlossfest() {
        return new Schlossfest(db, timeTaker, n, world);
    }

    @NonNull
    public static GameObject create(final GameObjectId id) {
        return new GameObject(id);
    }

    private static class Schlossfest extends GameObject
            implements IHasStateGO<SchlossfestState>, IResponder {
        private final AbstractStateComp<SchlossfestState> stateComp;
        private final SchlossfestReactionsComp reactionsComp;

        Schlossfest(final AvDatabase db, final TimeTaker timeTaker,
                    final Narrator n, final World world) {
            super(SCHLOSSFEST);

            stateComp = addComponent(new SchlossfestStateComp(db, timeTaker, world));
            reactionsComp = addComponent(
                    new SchlossfestReactionsComp(n, world, stateComp));
        }

        @Nonnull
        @Override
        public AbstractStateComp<SchlossfestState> stateComp() {
            return stateComp;
        }

        @Nonnull
        @Override
        public SchlossfestReactionsComp reactionsComp() {
            return reactionsComp;
        }
    }
}
