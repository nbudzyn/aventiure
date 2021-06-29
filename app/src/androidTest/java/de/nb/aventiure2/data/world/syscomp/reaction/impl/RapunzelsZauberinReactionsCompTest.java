package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.movement.IMovingGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinState.AUF_DEM_WEG_ZU_RAPUNZEL;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RapunzelsZauberinReactionsCompTest extends AndroidTestBase {
    // ---------------------------------------------
    // Relative Bewegungen von Zauberin und SC
    // ---------------------------------------------

    @Test
    public <Z extends IResponder & ILocatableGO>
    void zauberinUnbewegt_scBewegtSichWoanders__keineErwaehnung() {
        // GIVEN
        final Z zauberin = world.load(RAPUNZELS_ZAUBERIN);
        // Zauberin unbewegt
        zauberin.locationComp().setLocation(VOR_DEM_ALTEN_TURM);

        // WHEN
        reactToScMovement(zauberin, VOR_DER_HUETTE_IM_WALD, HUETTE_IM_WALD);

        // THEN
        assertThat(n.getNarrationText()).doesNotContain("Frau");
    }

    @Test
    public <Z extends IResponder & ILocatableGO>
    void zauberinUnbewegt_scBewegtSichZumMovingBeing__Narration() {
        // GIVEN
        final Z zauberin = world.load(RAPUNZELS_ZAUBERIN);
        // Zauberin unbewegt
        zauberin.locationComp().setLocation(VOR_DEM_ALTEN_TURM);

        // WHEN
        reactToScMovement(zauberin, IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM);

        // THEN
        final String narration = n.getNarrationText();
        assertThat(narration).contains("Frau");
        assertThat(narration).containsMatch("(steh)|(wart)");
    }

    @Test
    public <Z extends IResponder & ILocatableGO & IMovingGO>
    void zauberinInBewegung_scKommtIhrEntgegen__Narration() {
        // GIVEN
        final Z zauberin = world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(VOR_DEM_ALTEN_TURM);
        zauberin.movementComp().startMovement(
                timeTaker.now(), IM_WALD_NAHE_DEM_SCHLOSS
        );

        // WHEN
        reactToScMovement(zauberin, IM_WALD_NAHE_DEM_SCHLOSS, VOR_DEM_ALTEN_TURM);

        // THEN
        final String narration = n.getNarrationText();
        assertThat(narration).contains("Frau");
        assertThat(narration).containsMatch("(entgegen)|(auf dich zu)");
    }

    @Test
    public <Z extends IResponder & ILocatableGO & IMovingGO>
    void zauberinInBewegung_scUeberholtZauberin__Narration() {
        // GIVEN
        final Z zauberin = world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(VOR_DEM_ALTEN_TURM);
        zauberin.movementComp().startMovement(
                timeTaker.now(), IM_WALD_NAHE_DEM_SCHLOSS
        );

        // WHEN
        reactToScMovement(zauberin, VOR_DEM_ALTEN_TURM, IM_WALD_NAHE_DEM_SCHLOSS);

        // THEN
        final String narration = n.getNarrationText();
        assertThat(narration).contains("Frau");
        assertThat(narration).containsMatch("(vorbei)|(vorüber)");
    }

    @Test
    public <Z extends IResponder & ILocatableGO & IMovingGO>
    void scUnbewegt_zauberinUnbewegt__KeineNarration() {
        // GIVEN
        // Zauberin unbewegt
        final Z zauberin = world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);
        loadSC().locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);

        // WHEN
        final AvDateTime now = timeTaker.now();
        zauberin.movementComp().onTimePassed(now.plus(mins(10)));

        // THEN
        assertThat(n.getNarrationText()).doesNotContain("Frau");
    }

    @Test
    public <Z extends IResponder & ILocatableGO & IMovingGO>
    void scUnbewegt_zauberinKommt__Narration() {
        // GIVEN
        loadSC().locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);

        final Z zauberin = world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(ABZWEIG_IM_WALD);

        // WHEN
        final AvDateTime now = timeTaker.now();
        zauberin.movementComp().startMovement(
                timeTaker.now(), VOR_DER_HUETTE_IM_WALD
        );
        zauberin.movementComp().onTimePassed(now.plus(mins(10)));

        // THEN
        final String narration = n.getNarrationText();
        assertThat(narration).contains("Frau");
        assertThat(narration).containsMatch("kommt");
    }

    @Test
    public <Z extends IResponder & ILocatableGO & IMovingGO>
    void scUnbewegt_zauberinGeht__Narration() {
        // GIVEN
        loadSC().locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);

        final Z zauberin = world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);

        // WHEN
        final AvDateTime now = timeTaker.now();
        zauberin.movementComp().startMovement(
                timeTaker.now(), ABZWEIG_IM_WALD
        );
        zauberin.movementComp().onTimePassed(now.plus(mins(10)));

        // THEN
        final String narration = n.getNarrationText();
        assertThat(narration).contains("Frau");
        assertThat(narration).containsMatch("geht");
    }

    // ---------------------------------------------
    // Zauberin kehrt von Turm wieder zurück
    // ---------------------------------------------

    @Test
    public <Z extends IResponder & ILocatableGO & IMovingGO & IHasStateGO<RapunzelsZauberinState>>
    void zauberinWartetVorTurm_SCAuchVorTurm__ZauberinGehtBaldWieder() {
        // GIVEN
        timeTaker.setNow(new AvDateTime(1, oClock(16)));
        loadSC().locationComp().setLocation(VOR_DEM_ALTEN_TURM);

        final Z zauberin = world.load(RAPUNZELS_ZAUBERIN);
        zauberin.stateComp().setState(AUF_DEM_WEG_ZU_RAPUNZEL);
        zauberin.locationComp().setLocation(VOR_DEM_ALTEN_TURM);

        // WHEN
        final AvDateTime now = timeTaker.now();
        ((ITimePassedReactions) zauberin.reactionsComp()).onTimePassed(
                new Change<>(now, now.plus(mins(45))));

        // THEN
        // Ist die Zauberin schon wieder losgegangen
        assertThat(zauberin.movementComp().isMoving()).isTrue();
        assertThat(zauberin.locationComp().hasNoLocation()).isTrue();
        assertThat(zauberin.movementComp().requireCurrentStepFromId())
                .isEqualTo(VOR_DEM_ALTEN_TURM);
        assertThat(zauberin.movementComp().requireCurrentStepToId())
                .isEqualTo(IM_WALD_NAHE_DEM_SCHLOSS);
    }

    private <Z extends IResponder & ILocatableGO> void reactToScMovement(
            final Z responder, final GameObjectId fromId, final GameObjectId toId) {
        loadSC().locationComp().setLocation(fromId);

        final ILocationGO from = world.load(fromId);
        final ILocationGO to = world.load(toId);
        ((RapunzelsZauberinReactionsComp) responder.reactionsComp())
                .onLeave(loadSC(), from, to);

        loadSC().locationComp().setLocation(toId);

        ((RapunzelsZauberinReactionsComp) responder.reactionsComp())
                .onEnter(loadSC(), from, to);
    }
}
