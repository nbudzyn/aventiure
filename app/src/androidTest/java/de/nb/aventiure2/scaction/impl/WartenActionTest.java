package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.common.truth.Correspondence;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.List;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.gameobject.World.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("unchecked")
public class WartenActionTest extends AndroidTestBase {
    @Test
    public <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    void zauberinUnbekannt_ManKannNichtAufSieWarten() {
        // GIVEN
        // Zauberin unbekannt

        // WHEN
        final List<WartenAction<LIVGO>> actions = buildWartenActionImSchattenDerBaeume();
        // THEN
        assertThat(actions)
                .comparingElementsUsing(actionNameContains())
                .doesNotContain("Frau");
    }

    @Test
    public <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    void zauberinSchonDa_ManKannNichtAufSieWarten() {
        // GIVEN
        world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN);
        ((ILocatableGO) world.load(RAPUNZELS_ZAUBERIN)).locationComp()
                .setLocation(VOR_DEM_ALTEN_TURM);

        // WHEN
        final List<WartenAction<LIVGO>> actions = buildWartenActionImSchattenDerBaeume();
        // THEN
        assertThat(actions)
                .comparingElementsUsing(actionNameContains())
                .doesNotContain("Frau");
    }

    @Test
    public <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    void zauberinBekanntUndNichtDa_WartenMoeglich() {
        // GIVEN
        world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN);
        ((ILocatableGO) world.load(RAPUNZELS_ZAUBERIN)).locationComp()
                .setLocation(IM_WALD_NAHE_DEM_SCHLOSS);

        // WHEN
        final List<WartenAction<LIVGO>> actions = buildWartenActionImSchattenDerBaeume();
        // THEN
        assertThat(actions)
                .comparingElementsUsing(actionNameContains())
                .contains("Auf die magere Frau warten");
    }

    private <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    List<WartenAction<LIVGO>> buildWartenActionImSchattenDerBaeume() {
        return WartenAction.buildActions(
                db, n, world,
                (LIVGO) world.load(RAPUNZELS_ZAUBERIN),
                (ILocationGO) world.load(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME));
    }

    @NonNull
    private static Correspondence<AbstractScAction, String> actionNameContains() {
        return Correspondence.from(
                (action, expectedName) -> action.getName().contains(expectedName),
                "action name contains");
    }
}
