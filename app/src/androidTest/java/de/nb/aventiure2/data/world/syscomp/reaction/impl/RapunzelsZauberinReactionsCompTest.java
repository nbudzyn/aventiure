package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.movement.IMovingGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.*;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RapunzelsZauberinReactionsCompTest extends AndroidTestBase {
    @Test
    public <Z extends IResponder & ILocatableGO>
    void zauberinUnbewegt_scBewegtSichWoanders__keineErwaehnung() {
        // GIVEN
        final Z zauberin = (Z) world.load(RAPUNZELS_ZAUBERIN);
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
        final Z zauberin = (Z) world.load(RAPUNZELS_ZAUBERIN);
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
        final Z zauberin = (Z) world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(VOR_DEM_ALTEN_TURM);
        zauberin.movementComp().startMovement(
                db.nowDao().now(), IM_WALD_NAHE_DEM_SCHLOSS
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
        final Z zauberin = (Z) world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(VOR_DEM_ALTEN_TURM);
        zauberin.movementComp().startMovement(
                db.nowDao().now(), IM_WALD_NAHE_DEM_SCHLOSS
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
        final Z zauberin = (Z) world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);
        world.loadSC().locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);

        // WHEN
        final AvDateTime now = db.nowDao().now();
        zauberin.movementComp().onTimePassed(now.plus(mins(10)));

        // THEN
        assertThat(n.getNarrationText()).doesNotContain("Frau");
    }

    @Test
    public <Z extends IResponder & ILocatableGO & IMovingGO>
    void scUnbewegt_zauberinKommt__Narration() {
        // GIVEN
        world.loadSC().locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);

        final Z zauberin = (Z) world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(ABZWEIG_IM_WALD);

        // WHEN
        final AvDateTime now = db.nowDao().now();
        zauberin.movementComp().startMovement(
                db.nowDao().now(), VOR_DER_HUETTE_IM_WALD
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
        world.loadSC().locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);

        final Z zauberin = (Z) world.load(RAPUNZELS_ZAUBERIN);
        zauberin.locationComp().setLocation(VOR_DER_HUETTE_IM_WALD);

        // WHEN
        final AvDateTime now = db.nowDao().now();
        zauberin.movementComp().startMovement(
                db.nowDao().now(), ABZWEIG_IM_WALD
        );
        zauberin.movementComp().onTimePassed(now.plus(mins(10)));

        // THEN
        final String narration = n.getNarrationText();
        assertThat(narration).contains("Frau");
        assertThat(narration).containsMatch("geht");
    }

    private <Z extends IResponder & ILocatableGO> void reactToScMovement(
            final Z responder, final GameObjectId fromId, final GameObjectId toId) {
        world.loadSC().locationComp().setLocation(fromId);

        final ILocationGO from = (ILocationGO) world.load(fromId);
        final ILocationGO to = (ILocationGO) world.load(toId);
        ((RapunzelsZauberinReactionsComp) responder.reactionsComp())
                .onLeave(world.loadSC(), from, to);

        world.loadSC().locationComp().setLocation(toId);

        ((RapunzelsZauberinReactionsComp) responder.reactionsComp())
                .onEnter(world.loadSC(), from, to);
    }
}
