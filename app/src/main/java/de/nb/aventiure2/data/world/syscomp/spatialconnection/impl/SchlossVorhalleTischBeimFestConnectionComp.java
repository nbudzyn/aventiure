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
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.DuDescription.du;

/**
 * An implementation of {@link AbstractSpatialConnectionComp}
 * for the {@link GameObjects#SCHLOSS_VORHALLE_TISCH_BEIM_FEST}
 * room.
 */
@ParametersAreNonnullByDefault
public class SchlossVorhalleTischBeimFestConnectionComp extends AbstractSpatialConnectionComp {
    public SchlossVorhalleTischBeimFestConnectionComp(
            final AvDatabase db) {
        super(SCHLOSS_VORHALLE_TISCH_BEIM_FEST, db);
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
        return ImmutableList.of(
                SpatialConnection.con(SCHLOSS_VORHALLE,
                        "Vom Tisch aufstehen",
                        SchlossVorhalleTischBeimFestConnectionComp::getDescTo_SchlossVorhalle));
    }

    private static AbstractDescription getDescTo_SchlossVorhalle(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        return du("stehst", "vom Tisch auf", mins(3))
                .undWartest()
                .dann();
    }
}
