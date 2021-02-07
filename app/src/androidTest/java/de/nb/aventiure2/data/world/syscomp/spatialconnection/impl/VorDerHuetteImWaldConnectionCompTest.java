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

@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VorDerHuetteImWaldConnectionCompTest extends AndroidTestBase {
    @Test
    public void turmUnbekannt_nichtInActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM);
        // Turm unbekannt

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).doesNotContain("Turm");
    }

    @Test
    public void turmBekannt_inActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM);
        world.loadSC().memoryComp().narrateAndUpgradeKnown(VOR_DEM_ALTEN_TURM, KNOWN_FROM_DARKNESS);

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).isEqualTo("Den langen schmalen Pfad "
                + "zum Turm aufwärtsgehen");
    }
}