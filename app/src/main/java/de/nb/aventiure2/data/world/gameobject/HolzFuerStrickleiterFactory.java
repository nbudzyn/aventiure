package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.impl.HolzFuerStrickleiterDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.HolzFuerStrickleiterReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState;
import de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Factory, erzeugt das Holz, das der Sturm von den Bäumen bricht und aus dem
 * der SC dann eine Strickleiter baut.
 */
class HolzFuerStrickleiterFactory extends AbstractNarratorGameObjectFactory {
    HolzFuerStrickleiterFactory(final AvDatabase db,
                                final TimeTaker timeTaker,
                                final Narrator n,
                                final World world) {
        super(db, timeTaker, n, world);
    }

    GameObject createDraussenVorDemSchloss() {
        return create(HOLZ_FUER_STRICKLEITER);
    }

    private GameObject create(final GameObjectId id) {
        // State
        final HolzFuerStrickleiterStateComp stateComp =
                new HolzFuerStrickleiterStateComp(db, timeTaker, n, world);

        // MultiDescription
        final HolzFuerStrickleiterDescriptionComp descriptionComp =
                new HolzFuerStrickleiterDescriptionComp(stateComp);

        // FIXME Holz in handliche Stücke brechen

        // Location
        final LocationComp locationComp =
                new LocationComp(HOLZ_FUER_STRICKLEITER,
                        db, world,
                        // Erst der Sturm bricht das Holz von den Bäumen
                        null, null, true, true);

        final HolzFuerStrickleiterReactionsComp reactionsComp =
                new HolzFuerStrickleiterReactionsComp(n, world, stateComp, locationComp);

        return new HolzFuerStrickleiter(id,
                descriptionComp, locationComp, stateComp, reactionsComp);
    }

    private static class HolzFuerStrickleiter
            extends StateObject<HolzFuerStrickleiterState>
            implements IResponder {
        protected final AbstractReactionsComp reactionsComp;

        HolzFuerStrickleiter(final GameObjectId id,
                             final AbstractDescriptionComp descriptionComp,
                             final LocationComp locationComp,
                             final AbstractStateComp<HolzFuerStrickleiterState> stateComp,
                             final HolzFuerStrickleiterReactionsComp reactionsComp) {
            super(id, descriptionComp, locationComp, stateComp);
            this.reactionsComp = addComponent(reactionsComp);
        }

        @NonNull
        @Override
        public AbstractReactionsComp reactionsComp() {
            return reactionsComp;
        }
    }
}
