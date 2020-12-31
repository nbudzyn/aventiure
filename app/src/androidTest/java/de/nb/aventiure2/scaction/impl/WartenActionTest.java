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
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.movement.IMovingGO;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState;
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
        final List<WartenAction<LIVGO>> actions = buildWartenActionsImSchattenDerBaeume();
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
        final List<WartenAction<LIVGO>> actions = buildWartenActionsImSchattenDerBaeume();
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
        final List<WartenAction<LIVGO>> actions = buildWartenActionsImSchattenDerBaeume();
        // THEN
        assertThat(actions)
                .comparingElementsUsing(actionNameContains())
                .contains("Auf die magere Frau warten");
    }

    @Test
    public void zauberinKommtNicht_tagsueber_WartenFuerMindestensZweiStunden() {
        // GIVEN
        world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN);

        // WHEN
        final AvDateTime timeBefore = timeTaker.now();

        doAction(buildWartenActionAufZauberinImSchattenDerBaume());

        final AvDateTime timeAfter = timeTaker.now();
        // THEN

        assertThat(timeAfter.minus(timeBefore).getAsHours()).isAtLeast(3);
    }

    @Test
    public <Z extends ILocatableGO & IHasStateGO<RapunzelsZauberinState> & IMovingGO>
    void zauberinKommt_WartenWirdAbgebrochen() {
        // GIVEN
        // Verhindern, dass Rapunzel die Zauberin noch oben in den Turm hilft!
        ((ILocatableGO) world.load(RAPUNZEL)).locationComp().setLocation(HUETTE_IM_WALD);

        world.loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN);
        Z zauberin = (Z) world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(IM_WALD_NAHE_DEM_SCHLOSS);
        zauberin.stateComp().setState(RapunzelsZauberinState.AUF_DEM_WEG_ZU_RAPUNZEL);
        zauberin.movementComp().startMovement(timeTaker.now(), VOR_DEM_ALTEN_TURM);

        // WHEN
        final AvDateTime timeBefore = timeTaker.now();

        doAction(buildWartenActionAufZauberinImSchattenDerBaume());

        final AvDateTime timeAfter = timeTaker.now();

        // THEN
        zauberin = (Z) world.load(RAPUNZELS_ZAUBERIN);
        assertThat(timeAfter.minus(timeBefore).getAsHours()).isLessThan(3);
        assertThat(zauberin.locationComp().getLocationId()).isEqualTo(VOR_DEM_ALTEN_TURM);
    }

    @NonNull
    private <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO> WartenAction<LIVGO>
    buildWartenActionAufZauberinImSchattenDerBaume() {
        return new WartenAction<>(db.scActionStepCountDao(), timeTaker, n, world,
                ((LIVGO) world.load(RAPUNZELS_ZAUBERIN)),
                ((ILocationGO) world.load(VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME)));
    }

    private <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    List<WartenAction<LIVGO>> buildWartenActionsImSchattenDerBaeume() {
        return WartenAction.buildActions(
                db.scActionStepCountDao(), timeTaker, n, world,
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
