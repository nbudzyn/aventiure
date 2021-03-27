package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.movement.SimpleMovementNarrator;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
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
        final EinzelneSubstantivischePhrase desc = getDescription();
        final SubstantivischePhrase anaph = anaph(false);

        final AltDescriptionsBuilder alt = alt();

        alt.add(neuerSatz(anaph.nomK(),
                "kommt daher", PARAGRAPH));

        if (spatialConnectionMovingGO != null) {
            alt.add(neuerSatz(anaph.nomK(),
                    "kommt",
                    spatialConnectionMovingGO.getWo(), // "auf dem Pfad "
                    "daher", PARAGRAPH));
        }

        if (!n.isThema(gameObjectId)) {
            if (spatialConnectionMovingGO != null) {
                alt.add(neuerSatz(spatialConnectionMovingGO.getWo(), // "auf dem Pfad "
                        "kommt", desc.nomK(), "gegangen", PARAGRAPH));
            }

            alt.add(neuerSatz("Es kommt dir", desc.nomK(), "entgegen", PARAGRAPH));
            alt.add(neuerSatz(PARAGRAPH, "Dir kommt", desc.nomK(), "entgegen", PARAGRAPH));
        }

        if (world.isOrHasRecursiveLocation(movingGOFrom, IM_WALD_NAHE_DEM_SCHLOSS) &&
                to.is(VOR_DEM_ALTEN_TURM)) {
            if (!n.isThema(gameObjectId)) {
                alt.add(neuerSatz("Den Pfad herauf kommt",
                        desc.nomK()));
            }

            n.narrateAlt(alt, NO_TIME);
            return;
        }

        if (world.isOrHasRecursiveLocation(movingGOFrom, VOR_DEM_ALTEN_TURM) &&
                world.isOrHasRecursiveLocation(scFrom, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            if (!n.isThema(gameObjectId)) {
                alt.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt dir", desc.nomK(), "entgegen")
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN));
            }

            n.narrateAlt(alt, NO_TIME);
            return;
        }

        if (world.isOrHasRecursiveLocation(movingGOFrom, VOR_DEM_ALTEN_TURM) &&
                world.isOrHasRecursiveLocation(scFrom, ABZWEIG_IM_WALD) &&
                to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {

            if (!n.isThema(gameObjectId)) {
                alt.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt", desc.nomK())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN));
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
