package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection.con;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link GameObjects#SCHLOSS_VORHALLE_TISCH_BEIM_FEST}
 * room.
 */
@ParametersAreNonnullByDefault
public class ImWaldBeimBrunnenConnectionComp extends AbstractSpatialConnectionComp {
    private final StoringPlaceComp storingPlaceComp;

    public ImWaldBeimBrunnenConnectionComp(
            final AvDatabase db,
            final StoringPlaceComp storingPlaceComp) {
        super(IM_WALD_BEIM_BRUNNEN, db);
        this.storingPlaceComp = storingPlaceComp;
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newRoomKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        return true;
    }

    @Override
    @NonNull
    public List<SpatialConnection> getConnections() {
        final ImmutableList.Builder<SpatialConnection> resImWaldBeimBrunnnen =
                ImmutableList.builder();

        resImWaldBeimBrunnnen.add(con(ABZWEIG_IM_WALD,
                "Den Weg Richtung Schloss gehen",
                du(SENTENCE, "verlässt", "den Brunnen und erreichst bald "
                        + "die Stelle, wo der überwachsene Weg "
                        + "abzweigt", mins(3))
                        .komma()));

        if (storingPlaceComp.getLichtverhaeltnisseInside() == HELL ||
                loadSC(db).memoryComp().isKnown(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            resImWaldBeimBrunnnen.add(con(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                    "Hinter dem Brunnen in die Wildnis schlagen",
                    du(SENTENCE, "verlässt", "den Brunnen und schlägst dich in die "
                            + "Wildnis "
                            + "hinter dem "
                            + "Brunnen. Umgestürzte Bäume, abgefallene "
                            + "Äste, modriger Grund – es ist schwer, durch "
                            + "diese Wildnis voranzukommen. "
                            + "Nicht weit in den Wald, und dir fällt ein "
                            + "Strauch mit kleinen, "
                            + "purpurnen Früchten auf, wie zu klein geratene "
                            + "Äpfel", mins(5))
                            .komma()
                            .dann(),
                    du("kämpfst", "dich noch einmal durch den wilden "
                                    + "Wald hinter dem Brunnen, bis du den Strauch mit den "
                                    + "kleinen, violetten Früchten erreichst",
                            "noch einmal",
                            mins(4))
                            .komma()
                            .undWartest()
                            .dann()));
        }

        return resImWaldBeimBrunnnen.build();
    }
}
