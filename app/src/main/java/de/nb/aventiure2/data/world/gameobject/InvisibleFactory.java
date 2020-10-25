package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.SchlossfestReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.TageszeitReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * A factory for special {@link GameObject}s: Invisible concepts, ideas, event or the like, that
 * have a state
 */
public class InvisibleFactory {
    private final AvDatabase db;
    private final Narrator n;
    private final World world;

    public InvisibleFactory(final AvDatabase db,
                            final Narrator n,
                            final World world) {
        this.db = db;
        this.n = n;
        this.world = world;
    }

    public GameObject createTageszeit() {
        return new Tageszeit(db, n, world);
    }

    public GameObject createSchlossfest() {
        return new Schlossfest(db, n, world);
    }

    @NonNull
    public static GameObject create(final GameObjectId id) {
        return new GameObject(id);
    }

    private static class Tageszeit extends GameObject implements IResponder {
        private final TageszeitReactionsComp reactionsComp;

        public Tageszeit(final AvDatabase db, final Narrator n, final World world) {
            super(TAGESZEIT);
            // Jede Komponente muss registiert werden!
            reactionsComp = addComponent(new TageszeitReactionsComp(db, n, world));
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

        public Schlossfest(final AvDatabase db, final Narrator n, final World world) {
            super(SCHLOSSFEST);

            stateComp = addComponent(new SchlossfestStateComp(db, n, world));
            reactionsComp = addComponent(
                    new SchlossfestReactionsComp(db, n, world, stateComp));
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
