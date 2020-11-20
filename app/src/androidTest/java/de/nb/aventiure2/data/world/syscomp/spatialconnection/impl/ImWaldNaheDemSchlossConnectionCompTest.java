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
public class ImWaldNaheDemSchlossConnectionCompTest extends AndroidTestBase {
    @Test
    public void gartenUnbekannt_nichtInActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(VOR_DER_HUETTE_IM_WALD, HINTER_DER_HUETTE);
        // Turm unbekannt

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).doesNotContain("Garten");
    }

    @Test
    public void gartenBekannt_inActionName() {
        // GIVEN
        final SpatialConnection con = loadCon(VOR_DER_HUETTE_IM_WALD, HINTER_DER_HUETTE);
        world.loadSC().memoryComp().upgradeKnown(HINTER_DER_HUETTE, KNOWN_FROM_LIGHT);

        // WHEN
        final String actionName = con.getActionName();

        // THEN
        assertThat(actionName).isEqualTo("In den Garten hinter der Hütte gehen");
    }
}