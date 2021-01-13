package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

public class SimpleConnectionCompFactory {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final World world;

    public SimpleConnectionCompFactory(final AvDatabase db, final TimeTaker timeTaker,
                                       final Narrator n,
                                       final World world) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.n = n;
        this.world = world;
    }

    @NonNull
    public SimpleConnectionComp createHuetteImWald() {
        return new SimpleConnectionComp(HUETTE_IM_WALD,
                db,
                timeTaker, n,
                world,
                con(VOR_DER_HUETTE_IM_WALD,
                        "in der Tür",
                        "Die Hütte verlassen",
                        secs(15),
                        du("zwängst", "dich wieder durch die Tür nach "
                                + "draußen")
                                .undWartest()
                                .dann()
                ));
    }

    @NonNull
    public SimpleConnectionComp createHinterDerHuette() {
        return new SimpleConnectionComp(HINTER_DER_HUETTE,
                db,
                timeTaker, n,
                world,
                con(VOR_DER_HUETTE_IM_WALD,
                        "auf dem Weg",
                        "Zur Vorderseite der Hütte gehen",
                        secs(30),
                        du("kehrst", "zurück zur Vorderseite der "
                                + "Hütte")
                                .undWartest()
                                .dann()
                )
        );
    }

    @NonNull
    @CheckReturnValue
    public WaldwildnisHinterDemBrunnenConnectionComp createWaldwildnisHinterDemBrunnen() {
        return new WaldwildnisHinterDemBrunnenConnectionComp(db, timeTaker, n, world);
    }

    public SimpleConnectionComp createNoConnections(final GameObjectId gameObjectId) {
        return new SimpleConnectionComp(gameObjectId,
                db, timeTaker, n, world);
    }
}
