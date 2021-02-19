package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.AbstractSpatialConnectionComp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.TRAURIG;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection.WEST;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;

public class WaldwildnisHinterDemBrunnenConnectionComp extends AbstractSpatialConnectionComp {
    WaldwildnisHinterDemBrunnenConnectionComp(
            final AvDatabase db, final TimeTaker timeTaker,
            final Narrator n, final World world) {
        super(WALDWILDNIS_HINTER_DEM_BRUNNEN, db, timeTaker, n, world);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean isAlternativeMovementDescriptionAllowed(final GameObjectId to,
                                                           final Known newLocationKnown,
                                                           final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return !to.equals(IM_WALD_BEIM_BRUNNEN) ||
                getObjectsInDenBrunnenGefallen().isEmpty() ||
                !((IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ))
                        .stateComp().hasState(FroschprinzState.UNAUFFAELLIG) ||
                !world.loadSC().feelingsComp().isFroehlicherAls(TRAURIG);
    }

    @Override
    @NonNull
    @CheckReturnValue
    public List<SpatialConnection> getConnections() {
        return ImmutableList.of(
                con(IM_WALD_BEIM_BRUNNEN, "mitten im wilden Wald",
                        WEST, "Zum Brunnen gehen",
                        mins(3),
                        this::getDescTo_ImWaldBeimBrunnen));
    }

    @SuppressWarnings("unchecked")
    private TimedDescription<?> getDescTo_ImWaldBeimBrunnen(final Known newLocationKnown,
                                                            final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        if (!getObjectsInDenBrunnenGefallen().isEmpty() &&
                ((IHasStateGO<FroschprinzState>) world.load(FROSCHPRINZ))
                        .stateComp().hasState(FroschprinzState.UNAUFFAELLIG) &&
                world.loadSC().feelingsComp().isFroehlicherAls(TRAURIG)) {
            return getDescTo_ImWaldBeimBrunnenWirdTraurig();
        }

        return du("suchst", "dir einen Weg "
                + "durch den wilden Wald zurück zum Brunnen")
                .mitVorfeldSatzglied("durch den wilden Wald")
                .undWartest()
                .dann()
                .timed(mins(3));
    }

    @NonNull
    private TimedDescription<?> getDescTo_ImWaldBeimBrunnenWirdTraurig() {
        world.loadSC().feelingsComp().requestMoodMax(TRAURIG);

        return du("suchst",
                "dir einen Weg zurück. Kaum am Brunnen, musst du "
                        + "wieder an den Verlust denken. Du wirst traurig").timed(mins(3))
                .dann();
    }

    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> getObjectsInDenBrunnenGefallen() {
        return world.loadDescribableNonLivingMovableKnownToSCRecursiveInventory(UNTEN_IM_BRUNNEN);
    }
}
