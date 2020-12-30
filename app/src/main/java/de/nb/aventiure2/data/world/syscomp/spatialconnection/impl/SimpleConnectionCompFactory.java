package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;

import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

public class SimpleConnectionCompFactory {
    private final AvDatabase db;
    private final Narrator n;
    private final World world;

    public SimpleConnectionCompFactory(final AvDatabase db,
                                       final Narrator n,
                                       final World world) {
        this.db = db;
        this.n = n;
        this.world = world;
    }

    @NonNull
    public SimpleConnectionComp createHuetteImWald() {
        return new SimpleConnectionComp(HUETTE_IM_WALD,
                db,
                n,
                world,
                con(VOR_DER_HUETTE_IM_WALD,
                        "in der Tür",
                        "Die Hütte verlassen",
                        secs(15),
                        du("zwängst", "dich wieder durch die Tür nach "
                                + "draußen", secs(15))
                                .undWartest()
                                .dann()
                ));
    }

    @NonNull
    public SimpleConnectionComp createHinterDerHuette() {
        return new SimpleConnectionComp(HINTER_DER_HUETTE,
                db,
                n,
                world,
                con(VOR_DER_HUETTE_IM_WALD,
                        "auf dem Weg",
                        "Zur Vorderseite der Hütte gehen",
                        secs(30),
                        du("kehrst", "zurück zur Vorderseite der "
                                + "Hütte", secs(30))
                                .undWartest()
                                .dann()
                )
        );
    }

    @NonNull
    @CheckReturnValue
    public SimpleConnectionComp createWaldwildnisHinterDemBrunnen() {
        return new SimpleConnectionComp(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                db,
                n, world,
                con(IM_WALD_BEIM_BRUNNEN,
                        "mitten im wilden Wald",
                        "Zum Brunnen gehen",
                        mins(3),
                        du("suchst", "dir einen Weg "
                                        + "durch den wilden Wald zurück zum Brunnen",
                                "durch den wilden Wald",
                                mins(3))
                                .undWartest()
                                .dann()
                ));
    }

    public SimpleConnectionComp createNoConnections(final GameObjectId gameObjectId) {
        return new SimpleConnectionComp(gameObjectId,
                db, n, world);
    }
}
