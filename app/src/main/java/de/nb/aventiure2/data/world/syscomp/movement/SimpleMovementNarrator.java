package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.data.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.NO_WAY;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Grundlegende Implementierung, um dem Spieler die Bewegung
 * eines {@link de.nb.aventiure2.data.world.syscomp.movement.IMovingGO} zu beschreiben
 */
public class SimpleMovementNarrator implements IMovementNarrator {
    protected final GameObjectId gameObjectId;
    protected final Narrator n;
    protected final World world;

    /**
     * Ob das Wesen, das sich bewegt, eher groß ist (z.B. ein Mensch, kein Frosch)
     */
    private final boolean eherGross;

    public SimpleMovementNarrator(
            final GameObjectId gameObjectId,
            final Narrator n,
            final World world,
            final boolean eherGross) {
        this.gameObjectId = gameObjectId;
        this.n = n;
        this.world = world;
        this.eherGross = eherGross;
    }

    @Override
    public void narrateScTrifftStehendesMovingGO(final ILocationGO locationMovingGO) {
        final Nominalphrase desc = getDescription();
        final Nominalphrase descShort = getDescription(true);

        final String wo = locationMovingGO.storingPlaceComp().getLocationMode().getWo(eherGross);

        n.narrateAlt(noTime(),
                neuerSatz(wo +
                        " steht " +
                        desc.nomStr())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(wo +
                        " siehst du " +
                        descShort.nomStr() +
                        " stehen")
                        .phorikKandidat(descShort, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(wo +
                        " begegnest du " +
                        desc.datStr())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(wo +
                        " scheint " +
                        desc.nomStr() +
                        " geradezu auf dich zu warten")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateScTrifftMovingGOImDazwischen(
            @Nullable final ILocationGO scFrom,
            final ILocationGO to,
            final FROM movingGOFrom) {
        if (scFrom != null) {
            if (world.isOrHasRecursiveLocation(scFrom, movingGOFrom)) {
                // IMovingGO und SC gehen denselben Weg, das IMovingGO ist noch nicht
                // angekommen
                narrateScUeberholtMovingGO();
                return;
            }

            narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich();
            return;
        }

        narrateScTrifftMovingGOImDazwischen_scHatKeinenVorigenOrt();
    }

    @Override
    public void narrateScUeberholtMovingGO() {
        final Nominalphrase desc = getDescription();

        n.narrateAlt(noTime(),
                du("gehst",
                        "dabei an " +
                                desc.datStr() +
                                " vorbei",
                        "dabei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(SENTENCE),
                du("gehst",
                        "dabei schnellen Schrittes an " +
                                desc.datStr() +
                                " vorüber",
                        "dabei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(SENTENCE),
                du("gehst",
                        "dabei mit schnellen Schritten an " +
                                desc.datStr() +
                                " vorüber",
                        "dabei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(SENTENCE)
        );
    }

    private void narrateScTrifftMovingGOImDazwischen_scHatKeinenVorigenOrt() {
        final Nominalphrase desc = getDescription();

        n.narrateAlt(noTime(),
                neuerSatz(PARAGRAPH,
                        "Dir begegnet " + desc.nomStr())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                du("begegnest ",
                        desc.datStr())
                        .phorikKandidat(desc, gameObjectId)
        );
    }

    @Override
    public void narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich() {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(neuerSatz(anaphOderDesc.nomStr() +
                " kommt dir entgegen und geht an dir vorbei")
                .phorikKandidat(desc, gameObjectId));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " kommt auf dich zu und geht an dir vorbei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " kommt auf dich zu und läuft vorbei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nomStr() +
                                    " entgegen und geht an dir vorbei")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nomStr() +
                                    " entgegen und geht hinter dir davon")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
        }

        n.narrateAlt(alt, noTime());
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoLeaves(
            final FROM from,
            final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        narrateLeaves(from, to, spatialConnection, numberOfWaysOut);

        world.loadSC().memoryComp().upgradeKnown(gameObjectId);
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateLeaves(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();

        if (spatialConnection != null &&
                world.isOrHasRecursiveLocation(scLastLocation, spatialConnection.getTo())) {
            narrateMovingGOKommtSCEntgegenUndGehtAnSCVorbei(
                    from, to, spatialConnection);
            return;
        }

        narrateGehtWeg(from, to, spatialConnection, numberOfWaysOut);
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateMovingGOKommtSCEntgegenUndGehtAnSCVorbei(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(neuerSatz(PARAGRAPH,
                anaphOderDesc.nomStr() +
                        " kommt dir entgegen und geht an dir vorbei")
                .phorikKandidat(desc, gameObjectId));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " kommt dir entgegen und geht an dir vorbei")
                        .phorikKandidat(desc, gameObjectId));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " kommt auf dich zu und geht an dir vorbei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " kommt auf dich zu und läuft vorbei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nomStr() +
                                    " entgegen und geht an dir vorbei")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
            // IDEA "Dir kommt ... entgegen und geht hinter dir seiner / ihrer Wege (Genitiv!...)
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nomStr() +
                                    " entgegen und geht hinter dir davon")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
        }

        n.narrateAlt(alt, noTime());
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateGehtWeg(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysOut);

        n.narrateAlt(noTime(),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " geht "
                                + wo // "auf dem Weg "
                                + "davon")
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " geht "
                                + wo // "auf dem Weg "
                                + "weiter")
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " geht weg")
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                // IDEA: "X geht seines / ihres Wegs" - Possessivartikel vor Genitiv!
                // IDEA: "X geht seiner / ihrer Wege" - Possessivartikel vor Genitiv!
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " geht fort")
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " läuft vorbei")
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " läuft weiter")
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoEnters(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        narrateEnters(from, to, spatialConnection, numberOfWaysIn);

        world.loadSC().memoryComp().upgradeKnown(gameObjectId);
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateEnters(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        if (loadSC().memoryComp().getLastAction().is(BEWEGEN) &&
                // Der SC hat sich nicht nur im selben outermost-Raum bewegt
                !world.hasSameOuterMostLocationAsSC(
                        loadSC().locationComp().getLastLocation()
                )
        ) {
            @Nullable final ILocationGO scLastLocation =
                    loadSC().locationComp().getLastLocation();

            if (world.isOrHasRecursiveLocation(scLastLocation, from)) {
                narrateMovingGOKommtSCNach(
                        from,
                        to,
                        spatialConnection, numberOfWaysIn);
                return;
            }

            narrateMovingGOUndSCKommenEinanderEntgegen(
                    scLastLocation,
                    to,
                    from,
                    spatialConnection,
                    numberOfWaysIn);
            return;
        }

        // Default
        narrateKommtGegangen(from, to, spatialConnection, numberOfWaysIn);
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateMovingGOKommtSCNach(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysIn);

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(neuerSatz(PARAGRAPH,
                anaphOderDesc.nomStr() +
                        " kommt dir "
                        + wo
                        + "hinterher")
                .phorikKandidat(desc, gameObjectId)
                .beendet(PARAGRAPH));
        alt.add(neuerSatz(PARAGRAPH,
                anaphOderDesc.nomStr() +
                        " kommt dir hinterher")
                .phorikKandidat(desc, gameObjectId)
                .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " kommt hinter dir her")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " kommt dir hinterhergegangen")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nomStr() +
                                " ist dir nachgekommen")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Hinter dir kommt " +
                                    desc.nomStr() +
                                    " gegangen")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
        }

        n.narrateAlt(alt, noTime());
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateMovingGOUndSCKommenEinanderEntgegen(
            @Nullable final ILocationGO scFrom,
            final ILocationGO to,
            final FROM movingGOFrom,
            @Nullable final SpatialConnection spatialConnectionMovingGO,
            final NumberOfWays numberOfWaysIn) {
        if (numberOfWaysIn == ONE_IN_ONE_OUT) {
            narrateMovingGOKommtScEntgegen_esVerstehtSichVonSelbstVonWo(
                    scFrom, to, movingGOFrom,
                    spatialConnectionMovingGO);
            return;
        }

        narrateMovingGOKommtScEntgegen_esVerstehtSichNichtVonSelbstVonWo(
                scFrom, to, movingGOFrom, spatialConnectionMovingGO);
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateMovingGOKommtScEntgegen_esVerstehtSichVonSelbstVonWo(
            @Nullable final ILocationGO scFrom,
            final ILocationGO to,
            final FROM movingGOFrom,
            @Nullable final SpatialConnection spatialConnectionMovingGO) {
        final Nominalphrase desc = getDescription();

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();


        alt.add(neuerSatz(desc.nomStr() + " kommt dir entgegen")
                .phorikKandidat(desc, gameObjectId)
                .beendet(PARAGRAPH));

        n.narrateAlt(alt, noTime());
    }

    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateMovingGOKommtScEntgegen_esVerstehtSichNichtVonSelbstVonWo(
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

        n.narrateAlt(alt, noTime());
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateKommtGegangen(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        final Nominalphrase desc = getDescription();

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysIn);

        n.narrateAlt(noTime(),
                neuerSatz(PARAGRAPH,
                        wo // "auf dem Weg "
                                + " kommt " +
                                desc.nomStr())
                        .beendet(PARAGRAPH)
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        desc.nomStr()
                                + " kommt "
                                + wo // "auf dem Weg "
                                + "daher")
                        .beendet(PARAGRAPH)
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        desc.nomStr()
                                + " kommt "
                                + wo // "auf dem Weg "
                                + "gegangen")
                        .beendet(PARAGRAPH)
                        .phorikKandidat(desc, gameObjectId)
        );
    }

    @NonNull
    private static String calcWoIfNecessary(
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWays) {
        if (spatialConnection != null &&
                (numberOfWays == NO_WAY ||
                        numberOfWays == NumberOfWays.SEVERAL_WAYS)) {
            return spatialConnection.getWo() + " ";
        }

        return "";
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
    protected final SubstantivischePhrase getAnaphPersPronWennMglSonstShortDescription() {
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
    protected final SubstantivischePhrase getAnaphPersPronWennMglSonstDescription(
            final boolean descShortIfKnown) {

        final IDescribableGO describableGO = (IDescribableGO) world.load(getGameObjectId());

        @Nullable final Personalpronomen anaphPersPron =
                n.getAnaphPersPronWennMgl(describableGO);
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
    protected final Nominalphrase getDescription() {
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
    protected final Nominalphrase getDescription(final boolean shortIfKnown) {
        return world.getDescription(gameObjectId, shortIfKnown);
    }

    @NonNull
    protected final SpielerCharakter loadSC() {
        return world.loadSC();
    }

    protected final GameObjectId getGameObjectId() {
        return gameObjectId;
    }
}
