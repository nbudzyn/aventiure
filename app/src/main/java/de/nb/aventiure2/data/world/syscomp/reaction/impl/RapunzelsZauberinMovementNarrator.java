package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.movement.IMovementNarrator;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobject.World.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

/**
 * Beschreibt dem Spieler die Bewegung der Zauberin
 */
class RapunzelsZauberinMovementNarrator implements IMovementNarrator {
    private final StoryStateDao n;
    private final World world;
    private final AbstractDescriptionComp descriptionComp;

    RapunzelsZauberinMovementNarrator(
            final StoryStateDao storyStateDao,
            final World world,
            final AbstractDescriptionComp descriptionComp) {
        n = storyStateDao;
        this.world = world;
        this.descriptionComp = descriptionComp;
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoMovementAsExperiencedBySC_StartsLeaving(
            final FROM from, final ILocationGO to) {
        if (from.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        final AvTimeSpan extraTime;

        // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
        //  den SC mit bösen und giftigen Blicken an?

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

            extraTime = n.add(neuerSatz(PARAGRAPH,
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
            extraTime = narrateAndDoMovementAsExperiencedBySC_StartsLeaving_Default(from, to);
        }

        world.upgradeKnownToSC(RAPUNZELS_ZAUBERIN);

        return extraTime;
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoMovementAsExperiencedBySC_StartsLeaving_Default(
            final FROM from, final ILocationGO to) {
        @Nullable final ILocationGO scLastLocation =
                loadSC().locationComp().getLastLocation();

        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        if (world.isOrHasRecursiveLocation(scLastLocation, to)) {
            return n.addAlt(
                    neuerSatz(PARAGRAPH,
                            anaphOderDesc.nom() +
                                    " kommt dir entgegen und geht an dir vorbei", noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN),
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nom() +
                                    " entgegen und geht an dir vorbei", noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH),
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nom() +
                                    " entgegen und geht hinter dir von dannen", noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH),
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nom() +
                                    " entgegen und geht hinter dir davon", noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH),
                    neuerSatz(PARAGRAPH,
                            desc.nom() +
                                    " kommt auf dich zu und geht an dir vorbei", noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH),
                    neuerSatz(PARAGRAPH,
                            desc.nom() +
                                    " kommt auf dich zu und läuft vorbei", noTime())
                            .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                            .beendet(PARAGRAPH)
            );
        }

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht von dannen", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht davon", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht weiter", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht weg", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                // STORY: "Die Zauberin geht ihres Wegs" - Possessivartikel vor Genitiv!
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht fort", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " läuft vorbei", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " läuft weiter", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoMovementAsExperiencedBySC_StartsEntering(
            final FROM from, final ILocationGO to) {
        if (to.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            // Hier bemerkt der SC die Zauberin nicht
            return noTime();
        }

        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final AvTimeSpan extraTime;
        if (to.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            // STORY Wenn die Zauberin WEISS_DASS_RAPUNZEL_BEFREIT_WURDE, sieht sie
            //  den SC mit bösen und giftigen Blicken an?
            if (world.isOrHasRecursiveLocation(scLastLocation, DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    from.is(VOR_DEM_ALTEN_TURM)) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt dir " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else if (world.isOrHasRecursiveLocation(scLastLocation, ABZWEIG_IM_WALD) &&
                    from.is(VOR_DEM_ALTEN_TURM)) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        "Von dem Pfad her kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else {
                extraTime = narrateMovementAsExperiencedBySC_StartsEntering_Default(from, to);
            }
        } else if (to.is(VOR_DEM_ALTEN_TURM)) {
            // STORY Spieler sieht von unten, wie die Zauberin heruntersteigt?

            // STORY Zauberin überrascht den Spieler vor dem Turm
//                // Die Zauberin hat den Spieler so verzaubert, dass er sich nicht
//                //  an sie erinnern kann.
//                loadSC().memoryComp().upgradeKnown(RAPUNZELS_ZAUBERIN, UNKNOWN);
//                return noTime();

            if (loadSC().memoryComp().getLastAction().is(BEWEGEN) &&
                    loadSC().locationComp().lastLocationWas(IM_WALD_NAHE_DEM_SCHLOSS) &&
                    from.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt hinter dir den Pfad herauf", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else if (from.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
                extraTime = n.add(neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt den Pfad herauf", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH));
            } else {
                extraTime = narrateMovementAsExperiencedBySC_StartsEntering_Default(from, to);
            }
        } else {
            extraTime = narrateMovementAsExperiencedBySC_StartsEntering_Default(from, to);
        }

        world.upgradeKnownToSC(RAPUNZELS_ZAUBERIN);

        return extraTime;
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateMovementAsExperiencedBySC_StartsEntering_Default(
            final FROM from, final ILocationGO to) {
        @Nullable final ILocationGO scLastLocation =
                loadSC().locationComp().getLastLocation();
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);


        // TODO Wenn der SC vom Wald den Pfad hinaufgeht und die Zauberin
        //  gleichzeitig dabei ist, den Pfad hinunterzukommen, müsste der SC die Zauberin treffen! -
        //  Funktioniert das?

        if (loadSC().memoryComp().getLastAction().is(BEWEGEN)) {
            if (world.isOrHasRecursiveLocation(scLastLocation, from)) {
                return n.addAlt(
                        neuerSatz(PARAGRAPH,
                                "Dir kommt " +
                                        desc.nom() +
                                        " nach", noTime())
                                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH,
                                "Hinter dir geht " +
                                        desc.nom(), noTime())
                                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH,
                                anaphOderDesc.nom() +
                                        " geht hinter dir her", noTime())
                                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH,
                                "Hinter dir kommt " +
                                        desc.nom() +
                                        " gegangen", noTime())
                                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH,
                                "Dir kommt " +
                                        desc.nom() +
                                        " heran", noTime())
                                .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                                .beendet(PARAGRAPH)
                );
            }

            if (!world.isOrHasRecursiveLocation(scLastLocation, from)) {
                return narrateZauberinKommtScEntgegen();
            }
        }

        // Default
        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt herzu", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt heran", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt gegangen", noTime())
                        .phorikKandidat(anaphOderDesc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        "Es kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt daher", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt gegangen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
        );
    }

    AvTimeSpan narrateZauberinKommtScEntgegen() {
        final Nominalphrase desc = getDescription();

        // STORY Wenn to mehr als zwei Zugänge hat ist auch bei "entgegen" nicht klar,
        //  woher die Zauberin kommt. Aus der SpatialConnection Details erfragen, z.B.
        //  "den kleinen Pfad herab", "auf dem Weg geht X",
        //  "Von dem Pfad her kommt X gegangen"

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegengegangen", noTime())
                        .phorikKandidat(desc, RAPUNZELS_ZAUBERIN)
                        .beendet(PARAGRAPH)
        );
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine kurze
     * Beschreibung des Game Objects.
     * <br/>
     * Es muss sich um eine {@link IDescribableGO} handeln!
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die Lampe" zurück.
     */
    protected SubstantivischePhrase getAnaphPersPronWennMglSonstShortDescription() {
        return getAnaphPersPronWennMglSonstDescription(true);
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine
     * Beschreibung des Game Objects.
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die mysteriöse Lampe" zurück.
     */
    protected SubstantivischePhrase getAnaphPersPronWennMglSonstDescription(
            final boolean descShortIfKnown) {

        final IDescribableGO describableGO = (IDescribableGO) world.load(getGameObjectId());

        @androidx.annotation.Nullable final Personalpronomen anaphPersPron =
                n.getStoryState().getAnaphPersPronWennMgl(describableGO);
        if (anaphPersPron != null) {
            return anaphPersPron;
        }

        return world.getDescription(describableGO, descShortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    protected Nominalphrase getDescription() {
        return getDescription(false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    protected Nominalphrase getDescription(final boolean shortIfKnown) {
        return descriptionComp.getDescription(
                loadSC().memoryComp().isKnown(getGameObjectId()), shortIfKnown);
    }

    @NonNull
    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }

    private static GameObjectId getGameObjectId() {
        return RAPUNZELS_ZAUBERIN;
    }
}
