package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.world.base.SpatialConnection;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.gameobject.World.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AbzweigImWaldConnectionCompTest extends AndroidTestBase {
    @Test
    public void huetteUnbekannt_nichtInActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(ABZWEIG_IM_WALD, VOR_DER_HUETTE_IM_WALD);
        // Hütte unbekannt

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).doesNotContain("Hütte");
    }

    @Test
    public void huetteBekannt_inActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(ABZWEIG_IM_WALD, VOR_DER_HUETTE_IM_WALD);
        world.loadSC().memoryComp().upgradeKnown(VOR_DER_HUETTE_IM_WALD, KNOWN_FROM_DARKNESS);

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).isEqualTo("Den überwachsenen Abzweig zur Hütte nehmen");
    }

    @Test
    public void brunnenUnbekannt_nichtInActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(ABZWEIG_IM_WALD, IM_WALD_BEIM_BRUNNEN);
        // Hütte unbekannt

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).doesNotContain("Brunnen");
    }

    @Test
    public void brunnenBekannt_inActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(ABZWEIG_IM_WALD, IM_WALD_BEIM_BRUNNEN);
        world.loadSC().memoryComp().upgradeKnown(IM_WALD_BEIM_BRUNNEN, KNOWN_FROM_DARKNESS);

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).isEqualTo("Zum Brunnen gehen");
    }

}