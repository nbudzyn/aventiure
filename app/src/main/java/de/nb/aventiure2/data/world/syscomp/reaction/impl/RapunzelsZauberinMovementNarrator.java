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
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.gameobject.World.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * Beschreibt dem Spieler die Bewegung der Zauberin
 */
public class RapunzelsZauberinMovementNarrator extends SimpleMovementNarrator {
    public RapunzelsZauberinMovementNarrator(
            final StoryStateDao storyStateDao,
            final World world) {
        super(RAPUNZELS_ZAUBERIN, storyStateDao, world, true);
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO> AvTimeSpan
    narrateMovingGOKommtScEntgegen_esVerstehtSichNichtVonSelbstVonWo(
            @Nullable final ILocationGO scFrom,
            final ILocationGO to,
            final FROM movingGOFrom,
            @Nullable final SpatialConnection spatialConnectionMovingGO) {
        if (world.isOrHasRecursiveLocation(movingGOFrom, IM_WALD_NAHE_DEM_SCHLOSS) &&
                to.is(VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription();

            return n.addAlt(
                    neuerSatz(SENTENCE,
                            spatialConnectionMovingGO.getWo() // "auf dem Pfad "
                                    + " kommt " +
                                    desc.nom() +
                                    " gegangen", noTime())
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH), //
                    neuerSatz("Den Pfad herauf kommt " +
                                    desc.nom(),
                            noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(SENTENCE));
        }

        if (world.isOrHasRecursiveLocation(movingGOFrom, VOR_DEM_ALTEN_TURM) &&
                world.isOrHasRecursiveLocation(scFrom, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            final Nominalphrase desc = getDescription();

            return n.addAlt(
                    neuerSatz(SENTENCE,
                            spatialConnectionMovingGO.getWo() // "auf dem Pfad "
                                    + " kommt " +
                                    desc.nom() +
                                    " gegangen", noTime())
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH), //
                    neuerSatz(PARAGRAPH,
                            "Von dem Pfad her kommt dir " +
                                    desc.nom() +
                                    " entgegen", noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(SENTENCE));
        }

        if (world.isOrHasRecursiveLocation(movingGOFrom, VOR_DEM_ALTEN_TURM) &&
                world.isOrHasRecursiveLocation(scFrom, ABZWEIG_IM_WALD) &&
                to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            final Nominalphrase desc = getDescription();

            return n.addAlt(
                    neuerSatz(SENTENCE,
                            spatialConnectionMovingGO.getWo() // "auf dem Pfad "
                                    + " kommt " +
                                    desc.nom() +
                                    " gegangen", noTime())
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH), //
                    neuerSatz(PARAGRAPH,
                            "Von dem Pfad her kommt " +
                                    desc.nom(), noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(SENTENCE));
        }

        return super.narrateMovingGOKommtScEntgegen_esVerstehtSichNichtVonSelbstVonWo(
                scFrom, to, movingGOFrom, spatialConnectionMovingGO);
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
