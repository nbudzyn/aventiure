package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.movement.SimpleMovementNarrator;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;

/**
 * Beschreibt dem Spieler die Bewegung der Zauberin
 */
public class RapunzelsZauberinMovementNarrator extends SimpleMovementNarrator {
    public RapunzelsZauberinMovementNarrator(
            final NarrationDao narrationDao,
            final World world) {
        super(RAPUNZELS_ZAUBERIN, narrationDao, world, true);
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void
    narrateMovingGOKommtScEntgegen_esVerstehtSichNichtVonSelbstVonWo(
            @Nullable final ILocationGO scFrom,
            final ILocationGO to,
            final FROM movingGOFrom,
            @Nullable final SpatialConnection spatialConnectionMovingGO) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(neuerSatz(anaphOderDesc.nom()
                + " kommt daher", noTime())
                .phorikKandidat(desc, gameObjectId)
                .beendet(PARAGRAPH));

        if (spatialConnectionMovingGO != null) {
            alt.add(neuerSatz(anaphOderDesc.nom()
                    + " kommt "
                    + spatialConnectionMovingGO.getWo() // "auf dem Pfad "
                    + " daher", noTime())
                    .phorikKandidat(desc, gameObjectId)
                    .beendet(PARAGRAPH));
        }

        if (!n.requireNarration().isThema(gameObjectId)) {
            if (spatialConnectionMovingGO != null) {
                alt.add(neuerSatz(spatialConnectionMovingGO.getWo() // "auf dem Pfad "
                        + " kommt " +
                        desc.nom() +
                        " gegangen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
            }

            alt.add(
                    neuerSatz("Es kommt dir " +
                            desc.nom() +
                            " entgegen", noTime())
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nom() +
                                    " entgegen", noTime())
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
        }


        if (world.isOrHasRecursiveLocation(movingGOFrom, IM_WALD_NAHE_DEM_SCHLOSS) &&
                to.is(VOR_DEM_ALTEN_TURM)) {
            if (!n.requireNarration().isThema(gameObjectId)) {
                alt.add(neuerSatz("Den Pfad herauf kommt " +
                                desc.nom(),
                        noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(SENTENCE));
            }

            n.narrateAlt(alt);
            return;
        }

        if (world.isOrHasRecursiveLocation(movingGOFrom, VOR_DEM_ALTEN_TURM) &&
                world.isOrHasRecursiveLocation(scFrom, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            if (!n.requireNarration().isThema(gameObjectId)) {
                alt.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt dir " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(SENTENCE));
            }

            n.narrateAlt(alt);
            return;
        }

        if (world.isOrHasRecursiveLocation(movingGOFrom, VOR_DEM_ALTEN_TURM) &&
                world.isOrHasRecursiveLocation(scFrom, ABZWEIG_IM_WALD) &&
                to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {

            if (!n.requireNarration().isThema(gameObjectId)) {
                alt.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(SENTENCE));
            }

            n.narrateAlt(alt);
            return;
        }

        super.narrateMovingGOKommtScEntgegen_esVerstehtSichNichtVonSelbstVonWo(
                scFrom, to, movingGOFrom, spatialConnectionMovingGO);
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoStartsLeaving(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        if (from.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return;
        }

        super.narrateAndDoStartsLeaving(from, to, spatialConnection, numberOfWaysOut);
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
    void narrateAndDoStartsEntering(final FROM from, final ILocationGO to,
                                    @Nullable final SpatialConnection spatialConnection,
                                    final NumberOfWays numberOfWaysIn) {
        if (to.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return;
        }

        super.narrateAndDoStartsEntering(from, to, spatialConnection, numberOfWaysIn);
    }

    // STORY Zauberin überrascht den Spieler vor dem Turm:
    //  Die Zauberin verzaubert hat den Spieler, so dass er sich nicht
    //  an sie erinnern kann:
    //  loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN, UNKNOWN);
    //  (Ist das nicht schon implementiert?)

    // FIXME Es gibt einen Bug, wo die Zauberin vom Turm aus den SPieler verzaubert,
    //  dann - für den Spieler erlebbar hinuntersteigt und ihn noch einmal verzaubert.
}
