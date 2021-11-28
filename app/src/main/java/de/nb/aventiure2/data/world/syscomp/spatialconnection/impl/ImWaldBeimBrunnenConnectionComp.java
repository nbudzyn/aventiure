package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.EAST;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.WEST;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link World#SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST}
 * room.
 */
@ParametersAreNonnullByDefault
public class ImWaldBeimBrunnenConnectionComp extends AbstractSpatialConnectionComp {
    private final StoringPlaceComp storingPlaceComp;

    public ImWaldBeimBrunnenConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n, final World world,
            final StoringPlaceComp storingPlaceComp) {
        super(IM_WALD_BEIM_BRUNNEN, db, timeTaker, n, world);
        this.storingPlaceComp = storingPlaceComp;
    }

    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return true;
    }

    @Override
    @NonNull
    public List<SpatialConnection> getConnections() {
        final ImmutableList.Builder<SpatialConnection> res = ImmutableList.builder();

        res.add(con(ABZWEIG_IM_WALD,
                "auf dem Weg",
                WEST, "Den Weg Richtung Schloss gehen",
                mins(3),
                du(SENTENCE, "verlässt", "den Brunnen und erreichst bald "
                        + "die Stelle, wo der überwachsene Weg "
                        + "abzweigt")
                        .komma()));

        if (storingPlaceComp.getLichtverhaeltnisse() == HELL ||
                loadSC().memoryComp().isKnown(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            res.add(con(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                    "im Wald",
                    EAST,
                    this::getActionNameTo_WildnisHinterDemBrunnen,
                    mins(4),
                    du(SENTENCE, "verlässt", "den Brunnen und schlägst dich in die "
                            + "Wildnis "
                            + "hinter dem "
                            + "Brunnen. Umgestürzte Bäume, abgefallene "
                            + "Äste, modriger Grund – es ist schwer, durch "
                            + "diese Wildnis voranzukommen. "
                            + "Nicht weit in den Wald, und dir fällt ein "
                            + "Strauch mit kleinen, "
                            + "purpurnen Früchten auf, wie zu klein geratene "
                            + "Äpfel")
                            .komma()
                            .dann(),
                    du("kämpfst", "dich noch einmal durch den wilden "
                            + "Wald hinter dem Brunnen, bis du den Strauch mit den "
                            + "kleinen violetten Früchten erreichst")
                            .mitVorfeldSatzglied("noch einmal")
                            .timed(mins(4))
                            .komma()
                            .undWartest()
                            .dann()));
        }

        return res.build();
    }

    private String getActionNameTo_WildnisHinterDemBrunnen() {
        if (loadSC().memoryComp()
                // Etwas unklar, ob die Früchte bei Nacht zu sehen sind...
                // bleiben wir konservativ!
                .isKnownFromLight(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            return "In die Wildnis schlagen, wo die Früchte wachsen";
        }

        return "Hinter dem Brunnen in die Wildnis schlagen";
    }
}
