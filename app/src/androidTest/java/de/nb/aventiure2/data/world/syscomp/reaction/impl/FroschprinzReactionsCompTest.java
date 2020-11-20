package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.WARTET_AUF_SC_BEIM_SCHLOSSFEST;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FroschprinzReactionsCompTest extends AndroidTestBase {
    @Test
    public <F extends IResponder & IHasStateGO<FroschprinzState> & ILocatableGO>
    void froschprinzLaeuftLosZumSchloss_FroschprinzIstSofortDa() {
        // GIVEN
        final F froschprinz = (F) world.load(FROSCHPRINZ);

        // WHEN
        ((FroschprinzReactionsComp) froschprinz.reactionsComp())
                .froschprinzLaueftZumSchlossfestLos();

        // THEN
        assertThat(froschprinz.stateComp().getState())
                .isEqualTo(WARTET_AUF_SC_BEIM_SCHLOSSFEST);
        assertThat(froschprinz.locationComp().getLocationId())
                .isEqualTo(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST);
    }
}
