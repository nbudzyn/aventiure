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
public class SchlossVorhalleConnectionCompTest extends AndroidTestBase {
    @Test
    public void gartenUnbekannt_nichtInActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS);
        // Turm unbekannt

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).doesNotContain("arten");
    }

    @Test
    public void gartenBekannt_inActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS);
        world.loadSC().memoryComp()
                .narrateAndUpgradeKnown(DRAUSSEN_VOR_DEM_SCHLOSS, KNOWN_FROM_DARKNESS);

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).isEqualTo(
                "Das Schloss verlassen und in den Schlossgarten gehen");
    }
}