package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.movement.SimpleMovementNarrator;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Beschreibt dem Spieler die Bewegung der Zauberin
 */
public class RapunzelsZauberinMovementNarrator extends SimpleMovementNarrator {
    public RapunzelsZauberinMovementNarrator(
            final Narrator n,
            final World world) {
        super(RAPUNZELS_ZAUBERIN, n, world, true);
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

        alt.add(neuerSatz(anaphOderDesc.nomStr()
                + " kommt daher")
                .phorikKandidat(desc, gameObjectId)
                .beendet(PARAGRAPH));

        if (spatialConnectionMovingGO != null) {
            alt.add(neuerSatz(anaphOderDesc.nomStr()
                    + " kommt "
                    + spatialConnectionMovingGO.getWo() // "auf dem Pfad "
                    + " daher")
                    .phorikKandidat(desc, gameObjectId)
                    .beendet(PARAGRAPH));
        }

        if (!n.isThema(gameObjectId)) {
            if (spatialConnectionMovingGO != null) {
                alt.add(neuerSatz(spatialConnectionMovingGO.getWo() // "auf dem Pfad "
                        + " kommt " +
                        desc.nomStr() +
                        " gegangen")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
            }

            alt.add(
                    neuerSatz("Es kommt dir " +
                            desc.nomStr() +
                            " entgegen")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nomStr() +
                                    " entgegen")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
        }


        if (world.isOrHasRecursiveLocation(movingGOFrom, IM_WALD_NAHE_DEM_SCHLOSS) &&
                to.is(VOR_DEM_ALTEN_TURM)) {
            if (!n.isThema(gameObjectId)) {
                alt.add(neuerSatz("Den Pfad herauf kommt " +
                        desc.nomStr())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(SENTENCE));
            }

            n.narrateAlt(alt, NO_TIME);
            return;
        }

        if (world.isOrHasRecursiveLocation(movingGOFrom, VOR_DEM_ALTEN_TURM) &&
                world.isOrHasRecursiveLocation(scFrom, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            if (!n.isThema(gameObjectId)) {
                alt.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt dir " +
                                desc.nomStr() +
                                " entgegen")
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(SENTENCE));
            }

            n.narrateAlt(alt, NO_TIME);
            return;
        }

        if (world.isOrHasRecursiveLocation(movingGOFrom, VOR_DEM_ALTEN_TURM) &&
                world.isOrHasRecursiveLocation(scFrom, ABZWEIG_IM_WALD) &&
                to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {

            if (!n.isThema(gameObjectId)) {
                alt.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt " +
                                desc.nomStr())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(SENTENCE));
            }

            n.narrateAlt(alt, NO_TIME);
            return;
        }

        super.narrateMovingGOKommtScEntgegen_esVerstehtSichNichtVonSelbstVonWo(
                scFrom, to, movingGOFrom, spatialConnectionMovingGO);
    }

    // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
    //  den SC mit bösen und giftigen Blicken an? Vielleicht aus den Emotionen generieren
    //  lassen?

    // IDEA Nicht so schön: "Vor dem Turm siehst du die Frau stehen. Sie geht den
    //  Pfad hinab." Besser wäre "Dann geht sie den Pfad hinab."
    //  - Denkbar wäre, .dann() optional mit einem Akteur zu qualifizieren:
    //    .dann(RAPUNZELS_ZAUBERIN). Ein "Dann" würde nur dann
    //    erzeugt, wenn der Folgesatz denselben Akteur hat.
}
