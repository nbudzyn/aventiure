package de.nb.aventiure2.data.world.gameobjects;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.SchlossfestReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.TageszeitReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.StateComp;

import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.TAGESZEIT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.NOCH_NICHT_BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectStateList.sl;

/**
 * A factory for special {@link GameObject}s: Invisible concepts, ideas, event or the like, that
 * have a state
 */
public class InvisibleFactory {
    private final AvDatabase db;
    private final GameObjectService gos;

    public InvisibleFactory(final AvDatabase db,
                            final GameObjectService gos) {
        this.db = db;
        this.gos = gos;
    }

    public GameObject createTageszeit() {
        return new Tageszeit(db, gos);
    }

    public GameObject createSchlossfest() {
        return new Schlossfest(db, gos);
    }

    private static class Tageszeit extends GameObject implements IResponder {
        private final TageszeitReactionsComp reactionsComp;

        public Tageszeit(final AvDatabase db, final GameObjectService gos) {
            super(TAGESZEIT);
            // Jede Komponente muss registiert werden!
            reactionsComp = addComponent(new TageszeitReactionsComp(db, gos));
        }

        @Nonnull
        @Override
        public TageszeitReactionsComp reactionsComp() {
            return reactionsComp;
        }
    }

    private static class Schlossfest extends GameObject
            implements IHasStateGO, IResponder {
        private final StateComp stateComp;
        private final SchlossfestReactionsComp reactionsComp;

        public Schlossfest(final AvDatabase db, final GameObjectService gos) {
            super(SCHLOSSFEST);

            stateComp = addComponent(new StateComp(TAGESZEIT, db,
                    sl(NOCH_NICHT_BEGONNEN, BEGONNEN)));
            reactionsComp = addComponent(
                    new SchlossfestReactionsComp(db, gos, stateComp));
        }

        @Nonnull
        @Override
        public StateComp stateComp() {
            return stateComp;
        }

        @Nonnull
        @Override
        public SchlossfestReactionsComp reactionsComp() {
            return reactionsComp;
        }
    }

}
