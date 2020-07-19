package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.movement.SimpleMovementNarrator;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobject.World.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

/**
 * Beschreibt dem Spieler die Bewegung der Zauberin
 */
class RapunzelsZauberinMovementNarrator extends SimpleMovementNarrator {
    RapunzelsZauberinMovementNarrator(
            final StoryStateDao storyStateDao,
            final World world) {
        super(RAPUNZELS_ZAUBERIN, storyStateDao, world);
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoStartsLeaving(
            final FROM from, final ILocationGO to) {
        if (from.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        return super.narrateAndDoStartsLeaving(from, to);
    }

    // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
    //  den SC mit bösen und giftigen Blicken an?

    @Override
    protected <FROM extends ILocationGO & ISpatiallyConnectedGO> AvTimeSpan narrateGehtWeg(
            final FROM from, final ILocationGO to) {
        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();

        if (!world.isOrHasRecursiveLocation(scLastLocation, IM_WALD_NAHE_DEM_SCHLOSS) &&
                from.is(VOR_DEM_ALTEN_TURM) && to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            final SubstantivischePhrase anaphOderDesc =
                    getAnaphPersPronWennMglSonstDescription(false);

            // TODO Movement-Componente
            //  - Wenn X noch in Bewegung ist und die Zeit für den Schritt noch nicht
            //    abegelaufen ist, kann SC mit X interagieren (z.B. mit X reden), aber
            //    es wird die Restzeit noch abgewartet. Vielleicht Zusatztext in der Art
            //    "Du wartest, bis ... herangekommen ist und"...
            //    Außerdem wird möglicherweise die Bewegung "ausgesetzt" und (zumindest von der
            //    Zeitmessung her) erst nach der Aktion forgesetzt. Z.B. auch erst
            //    nach einem Dialog (sofern X auf den Dialog eingeht und ihn nicht von sich
            //    aus beendet)

            // TODO Nicht schön: "Vor dem Turm siehst du die Frau stehen. Sie geht den
            //  Pfad hinab." Besser wäre "Dann geht sie den Pfad hinab."
            //  - Denkbar wäre, .dann() optional mit einem Akteur zu qualifizieren:
            //    .dann(RAPUNZELS_ZAUBERIN). Ein "Dann" würde nur dann
            //    erzeugt, wenn der Folgesatz denselben Akteur hat.
            return n.add(neuerSatz(PARAGRAPH,
                    // TODO Nicht schön: "Vor dem Turm siehst du die Frau stehen. Sie geht den
                    //  Pfad hinab." Besser wäre "Dann geht sie den Pfad hinab."
                    //  - Denkbar wäre, .dann() optional mit einem Akteur zu qualifizieren:
                    //    .dann(RAPUNZELS_ZAUBERIN). Ein "Dann" würde nur dann
                    //    erzeugt, wenn der Folgesatz denselben Akteur hat.
                    anaphOderDesc.nom() +
                            " geht den Pfad hinab", noTime())
                    .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        } else {
            // Default
            return super.narrateGehtWeg(from, to);
        }
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoStartsEntering(
            final FROM from, final ILocationGO to) {
        if (to.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        return super.narrateAndDoStartsEntering(from, to);
    }

    @Override
    protected <FROM extends ILocationGO & ISpatiallyConnectedGO> AvTimeSpan narrateMovingGOKommtSCNach(
            final FROM from, final ILocationGO to) {
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        if (to.is(VOR_DEM_ALTEN_TURM)) {
            return n.add(neuerSatz(PARAGRAPH,
                    anaphOderDesc.nom() +
                            " kommt hinter dir den Pfad herauf", noTime())
                    .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                    .beendet(PARAGRAPH));
        }

        return super.narrateMovingGOKommtSCNach(from, to);
    }

    @Override
    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateMovingGOKommtScEntgegen(
            final FROM from, final ILocationGO to) {
        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();
        final Nominalphrase desc = getDescription();

        // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
        //  den SC mit bösen und giftigen Blicken an?

        if (to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            if (world.isOrHasRecursiveLocation(scLastLocation, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    from.is(VOR_DEM_ALTEN_TURM)) {
                return n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt dir " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            }
        }

        // if (to.is(VOR_DEM_ALTEN_TURM)) {
        // STORY Spieler sieht von unten, wie die Zauberin heruntersteigt?

        // STORY Zauberin überrascht den Spieler vor dem Turm
//                // Die Zauberin hat den Spieler so verzaubert, dass er sich nicht
//                //  an sie erinnern kann.
//                loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN, UNKNOWN);
//                return noTime();

        //}

        return super.narrateMovingGOKommtScEntgegen(from, to);
    }

    @NonNull
    @Override
    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateKommtGegangen(
            final FROM from, final ILocationGO to) {
        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        if (to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
            //  den SC mit bösen und giftigen Blicken an?
            if (world.isOrHasRecursiveLocation(scLastLocation, ABZWEIG_IM_WALD) &&
                    from.is(VOR_DEM_ALTEN_TURM)) {
                return n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            }
        }

        if (to.is(VOR_DEM_ALTEN_TURM)) {
            // STORY Spieler sieht von unten, wie die Zauberin heruntersteigt?

            // STORY Zauberin überrascht den Spieler vor dem Turm
            //                // Die Zauberin hat den Spieler so verzaubert, dass er sich nicht
            //                //  an sie erinnern kann.
            //                loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN, UNKNOWN);
            //                return noTime();

            if (from.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
                return n.add(neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt den Pfad herauf", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            }
        }

        return super.narrateKommtGegangen(from, to);
    }
}
