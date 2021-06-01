package de.nb.aventiure2.data.world.gameobject;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.impl.HolzFuerStrickleiterDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterStateComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Factor, erzeugt das Holz, das der Sturm von den Bäumen bricht und aus dem
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

        // FIXME "Hier hat der Sturm hat viele Äste von den Bäumen gebrochen / gefegt. Überall
        //  liegen (kleine und große Äste) herum.
        //  (Als WetterReactions?)
        // FIXME Du klaubst Holz auf / "du sammelst Holz"
        // FIXME Holz in handliche Stücke brechen

        // Location
        final LocationComp locationComp =
                new LocationComp(HOLZ_FUER_STRICKLEITER,
                        db, world,
                        // Erst der Sturm bricht das Holz von den Bäumen
                        null, null, true);

        return new StateObject<>(id,
                descriptionComp, locationComp, stateComp);
    }
}
