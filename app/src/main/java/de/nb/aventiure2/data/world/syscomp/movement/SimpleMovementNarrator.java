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

import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.NO_WAY;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
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

        n.narrateAlt(
                neuerSatz(wo +
                        " steht " +
                        desc.nom(), noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(wo +
                                " siehst du " +
                                descShort.nom() +
                                " stehen",
                        noTime())
                        .phorikKandidat(descShort, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(wo +
                                " begegnest du " +
                                desc.dat(),
                        noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(wo +
                                " scheint " +
                                desc.nom() +
                                " geradezu auf dich zu warten",
                        noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateScTrifftEnteringMovingGO(
            @Nullable final ILocationGO scFrom,
            final ILocationGO to,
            final FROM movingGOFrom) {
        if (scFrom != null) {
            if (world.isOrHasRecursiveLocation(scFrom, movingGOFrom)) {
                // IMovingGO und SC sind denselben Weg gegangen, das IMovingGO ist noch nicht
                // im "Zentrum" angekommen
                narrateScUeberholtMovingGO();
                return;
            }

            @Nullable final SpatialConnection spatialConnectionMovingGO =
                    movingGOFrom.spatialConnectionComp().getConnection(to.getId());

            final NumberOfWays numberOfWaysIn =
                    to instanceof ISpatiallyConnectedGO ?
                            ((ISpatiallyConnectedGO) to).spatialConnectionComp()
                                    .getNumberOfWaysOut() :
                            NumberOfWays.NO_WAY;

            if (spatialConnectionMovingGO != null) {
                narrateMovingGOUndSCKommenEinanderEntgegen(
                        scFrom,
                        to,
                        movingGOFrom,
                        spatialConnectionMovingGO,
                        numberOfWaysIn);
                return;
            }
        }

        narrateScTrifftEnteringMovingGO_scHatKeinenVorigenOrt();
    }

    @Override
    public void narrateScUeberholtMovingGO() {
        final Nominalphrase desc = getDescription();

        n.narrateAlt(
                du("gehst",
                        "dabei an " +
                                desc.dat() +
                                " vorbei",
                        "dabei",
                        noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(SENTENCE),
                du("gehst",
                        "dabei schnellen Schrittes an " +
                                desc.dat() +
                                " vorüber",
                        "dabei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(SENTENCE),
                du("gehst",
                        "dabei mit schnellen Schritten an " +
                                desc.dat() +
                                " vorüber",
                        "dabei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(SENTENCE)
        );
    }

    public void narrateScTrifftEnteringMovingGO_scHatKeinenVorigenOrt() {
        final Nominalphrase desc = getDescription();

        n.narrateAlt(
                neuerSatz(PARAGRAPH,
                        "Dir begegnet " + desc.nom(), noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                du("begegnest ",
                        desc.dat(), noTime())
                        .phorikKandidat(desc, gameObjectId)
        );
    }

    @Override
    public <SC_FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateScTrifftLeavingMovingGO(@Nullable final ILocationGO scFrom,
                                        final SC_FROM scToAndMovingGoFrom,
                                        final ILocationGO movingGOTo) {
        if (world.isOrHasRecursiveLocation(scFrom, movingGOTo)) {
            narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich();
            return;
        }

        @Nullable final SpatialConnection spatialConnectionMovingGO =
                scToAndMovingGoFrom.spatialConnectionComp().getConnection(movingGOTo.getId());

        final NumberOfWays numberOfWaysOut =
                scToAndMovingGoFrom.spatialConnectionComp().getNumberOfWaysOut();

        narrateScSiehtMovingGOFortgehen(spatialConnectionMovingGO, numberOfWaysOut);
    }

    @Override
    public void narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich() {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(neuerSatz(anaphOderDesc.nom() +
                " kommt dir entgegen und geht an dir vorbei")
                .phorikKandidat(desc, gameObjectId));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt auf dich zu und geht an dir vorbei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt auf dich zu und läuft vorbei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nom() +
                                    " entgegen und geht an dir vorbei")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nom() +
                                    " entgegen und geht hinter dir davon")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
        }

        n.narrateAlt(alt, noTime());
    }

    public void narrateScSiehtMovingGOFortgehen(
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        final String wo = numberOfWaysOut != ONE_IN_ONE_OUT && spatialConnection != null ?
                spatialConnection.getWo() + " " :
                "";

        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(
                neuerSatz(anaphOderDesc.nom() +
                        " ist gerade dabei, "
                        + wo
                        + "davonzugehen")
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(
                        anaphOderDesc.nom() +
                                " geht gerade "
                                + wo
                                + "fort")
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(du(SENTENCE, "siehst",
                    ", wie " +
                            anaphOderDesc.nom() +
                            " " +
                            wo +
                            "davongeht")
                    .komma()
                    .phorikKandidat(anaphOderDesc, gameObjectId));
            alt.add(
                    du(SENTENCE, "siehst",
                            anaphOderDesc.akk() +
                                    " " +
                                    wo +
                                    "davongehen")
                            .komma()
                            .phorikKandidat(anaphOderDesc, gameObjectId));
            alt.add(
                    neuerSatz("Vor dir " +
                            wo
                            + "geht " +
                            desc.nom())
                            .phorikKandidat(anaphOderDesc, gameObjectId));
            alt.add(
                    neuerSatz("Ein Stück vor dir "
                            + wo
                            + "geht " +
                            desc.nom())
                            .phorikKandidat(anaphOderDesc, gameObjectId));
        }

        n.narrateAlt(alt, noTime());
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoStartsLeaving(
            final FROM from,
            final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        narrateStartsLeaving(from, to, spatialConnection, numberOfWaysOut);

        world.loadSC().memoryComp().upgradeKnown(gameObjectId);
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateStartsLeaving(
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

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateMovingGOKommtSCEntgegenUndGehtAnSCVorbei(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        alt.add(neuerSatz(PARAGRAPH,
                anaphOderDesc.nom() +
                        " kommt dir entgegen und geht an dir vorbei")
                .phorikKandidat(desc, gameObjectId));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt dir entgegen und geht an dir vorbei")
                        .phorikKandidat(desc, gameObjectId));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt auf dich zu und geht an dir vorbei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt auf dich zu und läuft vorbei")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nom() +
                                    " entgegen und geht an dir vorbei")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
            // STORY "Dir kommt ... entgegen und geht hinter dir seiner / ihrer Wege"
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nom() +
                                    " entgegen und geht hinter dir davon")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
        }

        n.narrateAlt(alt, noTime());
    }

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateGehtWeg(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysOut);

        n.narrateAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht "
                                + wo // "auf dem Weg "
                                + "davon", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht "
                                + wo // "auf dem Weg "
                                + "weiter", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht weg", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                // STORY: "X geht seines / ihres Wegs" - Possessivartikel vor Genitiv!
                // STORY: "X geht seiner / ihrer Wege" - Possessivartikel vor Genitiv!
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht fort", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " läuft vorbei", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " läuft weiter", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoStartsEntering(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        narrateStartsEntering(from, to, spatialConnection, numberOfWaysIn);

        world.loadSC().memoryComp().upgradeKnown(gameObjectId);
    }

    final protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateStartsEntering(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        if (loadSC().memoryComp().getLastAction().is(BEWEGEN)) {
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

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
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
                anaphOderDesc.nom() +
                        " kommt dir "
                        + wo
                        + "nach")
                // FIXME Komisches Deutsch? Statt "nach": "hinterher"?
                .phorikKandidat(desc, gameObjectId)
                .beendet(PARAGRAPH));
        alt.add(neuerSatz(PARAGRAPH,
                anaphOderDesc.nom() +
                        " kommt dir hinterher")
                .phorikKandidat(desc, gameObjectId)
                .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt hinter dir her")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt dir hinterhergegangen")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " ist dir nachgekommen")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Hinter dir kommt " +
                                    desc.nom() +
                                    " gegangen")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
        }

        n.narrateAlt(alt, noTime());
    }

    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
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

    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateMovingGOKommtScEntgegen_esVerstehtSichVonSelbstVonWo(
            @Nullable final ILocationGO scFrom,
            final ILocationGO to,
            final FROM movingGOFrom,
            @Nullable final SpatialConnection spatialConnectionMovingGO) {
        final Nominalphrase desc = getDescription();

        final ImmutableCollection.Builder<AbstractDescription<?>> alt = ImmutableList.builder();


        alt.add(neuerSatz(desc.nom() + " kommt dir entgegen")
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

        alt.add(neuerSatz(anaphOderDesc.nom()
                + " kommt daher")
                .phorikKandidat(desc, gameObjectId)
                .beendet(PARAGRAPH));

        if (spatialConnectionMovingGO != null) {
            alt.add(neuerSatz(anaphOderDesc.nom()
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
                        desc.nom() +
                        " gegangen")
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH));
            }

            alt.add(
                    neuerSatz("Es kommt dir " +
                            desc.nom() +
                            " entgegen")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt " +
                                    desc.nom() +
                                    " entgegen")
                            .phorikKandidat(desc, gameObjectId)
                            .beendet(PARAGRAPH));
        }

        n.narrateAlt(alt, noTime());
    }

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateKommtGegangen(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysIn);

        n.narrateAlt(
                neuerSatz(PARAGRAPH,
                        wo // "auf dem Weg "
                                + " kommt " +
                                desc.nom(), noTime())
                        .beendet(PARAGRAPH)
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt "
                                + wo // "auf dem Weg "
                                + "daher", noTime())
                        .beendet(PARAGRAPH)
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt "
                                + wo // "auf dem Weg "
                                + "gegangen", noTime())
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
