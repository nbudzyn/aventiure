package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.world.base.SpatialConnection;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_LIGHT;
import static de.nb.aventiure2.data.world.gameobject.World.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("ConstantConditions")
public class ImWaldBeimBrunnenConnectionCompTest extends AndroidTestBase {
    @Test
    public void fruechteUnbekannt_nichtInActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(IM_WALD_BEIM_BRUNNEN, WALDWILDNIS_HINTER_DEM_BRUNNEN);
        // Turm unbekannt

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).doesNotContain("Früchte");
    }

    @Test
    public void fruechteBekannt_inActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(IM_WALD_BEIM_BRUNNEN, WALDWILDNIS_HINTER_DEM_BRUNNEN);
        world.loadSC().memoryComp().upgradeKnown(WALDWILDNIS_HINTER_DEM_BRUNNEN, KNOWN_FROM_LIGHT);

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).isEqualTo(
                "In die Wildnis schlagen, wo die Früchte wachsen");
    }
}