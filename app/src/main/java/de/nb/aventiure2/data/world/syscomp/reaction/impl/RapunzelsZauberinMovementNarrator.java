package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.movement.SimpleMovementNarrator;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Beschreibt dem Spieler die Bewegung der Zauberin
 */
class RapunzelsZauberinMovementNarrator extends SimpleMovementNarrator {
    RapunzelsZauberinMovementNarrator(
            final StoryStateDao storyStateDao,
            final World world) {
        super(RAPUNZELS_ZAUBERIN, storyStateDao, world, true);
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoStartsLeaving(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        if (from.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        return super.narrateAndDoStartsLeaving(from, to, spatialConnection, numberOfWaysOut);
    }

    // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
    //  den SC mit bösen und giftigen Blicken an?

    // STORY Nicht so schön: "Vor dem Turm siehst du die Frau stehen. Sie geht den
    //  Pfad hinab." Besser wäre "Dann geht sie den Pfad hinab."
    //  - Denkbar wäre, .dann() optional mit einem Akteur zu qualifizieren:
    //    .dann(RAPUNZELS_ZAUBERIN). Ein "Dann" würde nur dann
    //    erzeugt, wenn der Folgesatz denselben Akteur hat.

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoStartsEntering(final FROM from, final ILocationGO to,
                                          @Nullable final SpatialConnection spatialConnection,
                                          final NumberOfWays numberOfWaysIn) {
        if (to.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        return super.narrateAndDoStartsEntering(from, to, spatialConnection, numberOfWaysIn);
    }

    // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
    //  den SC mit bösen und giftigen Blicken an?

    // STORY Spieler sieht von unten, wie die Zauberin heruntersteigt?
    //  if (to.is(VOR_DEM_ALTEN_TURM)) {

    // STORY Zauberin überrascht den Spieler vor dem Turm
    //                // Die Zauberin hat den Spieler so verzaubert, dass er sich nicht
    //                //  an sie erinnern kann.
    //                loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN, UNKNOWN);
    //                return noTime();

}
