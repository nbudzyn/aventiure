package de.nb.aventiure2.data.world.gameobject;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.SchlossfestReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.TageszeitReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobject.World.TAGESZEIT;

/**
 * A factory for special {@link GameObject}s: Invisible concepts, ideas, event or the like, that
 * have a state
 */
public class InvisibleFactory {
    private final AvDatabase db;
    private final World world;

    public InvisibleFactory(final AvDatabase db,
                            final World world) {
        this.db = db;
        this.world = world;
    }

    public GameObject createTageszeit() {
        return new Tageszeit(db, world);
    }

    public GameObject createSchlossfest() {
        return new Schlossfest(db, world);
    }

    private static class Tageszeit extends GameObject implements IResponder {
        private final TageszeitReactionsComp reactionsComp;

        public Tageszeit(final AvDatabase db, final World world) {
            super(TAGESZEIT);
            // Jede Komponente muss registiert werden!
            reactionsComp = addComponent(new TageszeitReactionsComp(db, world));
        }

        @Nonnull
        @Override
        public TageszeitReactionsComp reactionsComp() {
            return reactionsComp;
        }
    }

    private static class Schlossfest extends GameObject
            implements IHasStateGO<SchlossfestState>, IResponder {
        private final AbstractStateComp<SchlossfestState> stateComp;
        private final SchlossfestReactionsComp reactionsComp;

        public Schlossfest(final AvDatabase db, final World world) {
            super(SCHLOSSFEST);

            stateComp = addComponent(new SchlossfestStateComp(TAGESZEIT, db, world));
            reactionsComp = addComponent(
                    new SchlossfestReactionsComp(db, world, stateComp));
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
