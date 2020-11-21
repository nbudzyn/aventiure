package de.nb.aventiure2.scaction.impl;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.common.truth.Correspondence;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.gameobject.World.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RastenActionTest extends AndroidTestBase {
    @Test
    public void zauberinUnbekannt_ManKannNichtAufSieWarten() {
        // GIVEN
        // Zauberin unbekannt

        // WHEN
        final List<RastenAction> actions = RastenAction.buildActions(
                db, n, world,
                (ILocationGO) world.load(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME));
        // THEN
        assertThat(actions)
                .comparingElementsUsing(
                        Correspondence.<RastenAction, String>from(
                                (action, expectedName) -> action.getName().equals(expectedName),
                                "has action name")
                )
                .doesNotContain("Auf die magere Frau warten");
    }
}
