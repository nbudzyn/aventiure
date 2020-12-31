package de.nb.aventiure2.scaction.impl;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.time.AvTimeSpan.hours;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BewegenActionTest extends AndroidTestBase {
    @Test
    public void getStandardDescription_scMuede__scGehtLangsamer() {
        // GIVEN

        // WHEN
        world.loadSC().feelingsComp().ausgeschlafen(hours(8));

        final BewegenAction<?> hellwachBewegenAction = bewegenAction();
        final AvTimeSpan zeitHellwach = getTimeElapsed(hellwachBewegenAction);

        resetDatabase();

        world.loadSC().feelingsComp().narrateAndUpgradeTemporaereMinimalmuedigkeit(
                FeelingIntensity.STARK, hours(100)
        );

        final BewegenAction<?> muedeBewegenAction = bewegenAction();
        final AvTimeSpan zeitMuede = getTimeElapsed(muedeBewegenAction);

        // THEN
        assertThat(zeitMuede.longerThan(zeitHellwach)).isTrue();
    }

    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    BewegenAction<LOC_DESC> bewegenAction() {
        final GameObject oldLocation = world.load(World.IM_WALD_NAHE_DEM_SCHLOSS);
        return new BewegenAction<>(
                db.scActionStepCountDao(), timeTaker,
                db.counterDao(), n, world,
                (ILocationGO) oldLocation,
                ((ISpatiallyConnectedGO) oldLocation).spatialConnectionComp()
                        .getConnections().iterator().next(),
                ((ISpatiallyConnectedGO) oldLocation).spatialConnectionComp()
                        .getNumberOfWaysOut()
        );
    }

    private static AvTimeSpan getTimeElapsed(final BewegenAction<?> bewegenAction) {
        return bewegenAction.getStandardDescription(
                Known.UNKNOWN, Lichtverhaeltnisse.HELL
        ).getTimeElapsed();
    }
}
