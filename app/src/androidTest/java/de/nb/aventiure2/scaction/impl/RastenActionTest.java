package de.nb.aventiure2.scaction.impl;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;

import de.nb.aventiure2.androidtest.AndroidTestBase;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.gameobject.World.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RastenActionTest extends AndroidTestBase {
    @Test
    public void spielerUnterBett_kannStillDaliegen() {
        // GIVEN
        loadSC().locationComp().setLocation(BETT_OBEN_IM_ALTEN_TURM);

        // WHEN
        final List<RastenAction> rastenActions =
                RastenAction.buildActions(db.counterDao(), db.scActionStepCountDao(),
                        timeTaker, n, world, loadSC().locationComp().getLocation());

        // THEN
        assertThat(rastenActions).hasSize(1);
        assertThat(rastenActions.get(0).getName()).isEqualTo("Still daliegen");
    }
}
