package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;

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
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractFlexibleDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.DescriptionUmformulierer;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.NO_WAY;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
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
    public void narrateScFolgtMovingGO(final TimedDescription<?> normalDesc) {
        final SubstantivischePhrase anaph = anaph(true);
        final AltDescriptionsBuilder alt = alt();

        // Wir verwenden hier bewusst kein du(FOLGEN.mit(anaph)). Das Problem ist:
        // Wir wollen keine Ergebnisse wie "Der Frau gefolgt nimmst du den Pfad...".
        // Das Problem daran ist offenbar, dass "Der Frau gefolgt" (oder
        // "Der Frau hinterhergegangen" etc.) einen Nachzustand beschreibt, der für
        // den folgenden Text ("nimmst du den Pfad" o.Ä.) noch nicht erreicht ist.
        // Letztlich beschreiben "Der Frau folgen" und "Du nimmst den Pfad..."
        // ja dasselbe.
        // Möglich wäre etwas wie "Der Frau gefolgt bis da schnell auf dem Hügel" o.Ä. -
        // aber wir können hier ja nicht die normalDesc inhaltlich ändern.
        alt.add(du("folgst", anaph.datK()).undWartest(),
                du("folgst", anaph.datK(), "nach"),
                du("gehst", anaph.datK(), "hinterher").undWartest(),
                du("gehst", anaph.datK(), "hinterher:"),
                du("läufst", anaph.datK(), "hinterher"),
                du("gehst", anaph.datK(), "nach"),
                du("machst", "es wie", anaph.nomK()).undWartest(),
                du("willst", "da besser schnell hinterher")
                        .mitVorfeldSatzglied("da")
        );

        alt.addAll(DescriptionUmformulierer.mitPraefixCap(
                GermanUtil.joinToString("Schnell", anaph.datStr(), "hinterher!"),
                normalDesc.getDescription()));
        alt.addAll(DescriptionUmformulierer.mitPraefixCap(
                GermanUtil.joinToString("Schnell", anaph.datStr(), "gefolgt!"),
                normalDesc.getDescription()));

        if (normalDesc.getDescription() instanceof AbstractFlexibleDescription<?>) {
            final AbstractFlexibleDescription<?> fDesc =
                    (AbstractFlexibleDescription<?>) normalDesc.getDescription();
            if (fDesc.hasSubjektDu()) {
                alt.add(
                        fDesc.toTextDescriptionMitVorfeld(anaph.datStr() + " hinterher")
                                .beginntZumindestSentence(),
                        fDesc.toTextDescriptionMitVorfeld(anaph.datStr() + " folgend")
                                .beginntZumindestSentence()
                );

                if (normalDesc.getDescription() instanceof StructuredDescription) {
                    final StructuredDescription sDesc =
                            (StructuredDescription) normalDesc.getDescription();
                    alt.add(
                            // "auch du..."
                            sDesc.getSatz().mitSubjektFokuspartikel("auch")
                    );
                }
            }
        }

        n.narrateAlt(alt, normalDesc.getTimeElapsed());
    }

    @Override
    public void narrateScTrifftStehendesMovingGO(final ILocationGO locationMovingGO) {
        final Nominalphrase desc = getDescription();
        final Nominalphrase descShort = getDescription(true);

        final String wo = locationMovingGO.storingPlaceComp().getLocationMode().getWo(eherGross);

        n.narrateAlt(NO_TIME,
                neuerSatz(wo,
                        "steht",
                        desc.nomK(), PARAGRAPH),
                neuerSatz(wo,
                        "siehst du",
                        descShort.nomK(),
                        "stehen", PARAGRAPH),
                neuerSatz(wo,
                        "begegnest du",
                        desc.datK(), PARAGRAPH),
                neuerSatz(wo,
                        "scheint",
                        desc.nomK(),
                        "geradezu auf dich zu warten", PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateScTrifftMovingGOImDazwischen(
            @Nullable final ILocationGO scFrom,
            final ILocationGO to,
            final FROM movingGOFrom) {
        if (scFrom != null) {
            if (World.isOrHasRecursiveLocation(scFrom, movingGOFrom)) {
                @Nullable SpatialConnection conn = null;
                if (scFrom instanceof ISpatiallyConnectedGO) {
                    final ISpatiallyConnectedGO scFromSpatiallyConnected =
                            (ISpatiallyConnectedGO) scFrom;

                    conn = scFromSpatiallyConnected.spatialConnectionComp()
                            .getConnection(to.getId());
                }

                // IMovingGO und SC gehen denselben Weg, das IMovingGO ist noch nicht
                // angekommen
                narrateScUeberholtMovingGO(conn);
                return;
            }

            narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich();
            return;
        }

        narrateScTrifftMovingGOImDazwischen_scHatKeinenVorigenOrt();
    }

    @Override
    public void narrateScUeberholtMovingGO(@Nullable final SpatialConnection conn) {
        final Nominalphrase desc = getDescription();
        final String woDabei = conn != null ? conn.getWo() : "dabei";

        n.narrateAlt(NO_TIME,
                du("gehst",
                        woDabei,
                        "an", desc.datK(), "vorbei"),
                du("gehst",
                        woDabei,
                        "schnellen Schrittes an", desc.datK(), "vorüber")
                        .mitVorfeldSatzglied(woDabei),
                du("gehst",
                        woDabei,
                        "mit schnellen Schritten an", desc.datK(), "vorüber")
                        .mitVorfeldSatzglied(woDabei)
        );
    }

    private void narrateScTrifftMovingGOImDazwischen_scHatKeinenVorigenOrt() {
        final Nominalphrase desc = getDescription();

        n.narrateAlt(NO_TIME,
                neuerSatz(PARAGRAPH, "Dir begegnet", desc.nomK(), PARAGRAPH),
                du("begegnest", desc.datK()));
    }

    @Override
    public void narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich() {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaph = anaph(false);

        final AltDescriptionsBuilder alt = alt();

        alt.add(neuerSatz(anaph.nomK(),
                "kommt dir entgegen und geht an dir vorbei"));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaph.nomK(),
                        "kommt auf dich zu und geht an dir vorbei", PARAGRAPH));
        alt.add(
                neuerSatz(PARAGRAPH,
                        anaph.nomK(),
                        "kommt auf dich zu und läuft vorbei", PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt",
                            desc.nomK(),
                            "entgegen und geht an dir vorbei", PARAGRAPH));
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt",
                            desc.nomK(),
                            "entgegen und geht hinter dir davon", PARAGRAPH));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoLeaves(
            final FROM from,
            final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        narrateLeaves(spatialConnection, numberOfWaysOut);

        world.loadSC().memoryComp().narrateAndUpgradeKnown(gameObjectId);
    }

    private void narrateLeaves(
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();

        if (spatialConnection != null &&
                world.isOrHasRecursiveLocation(scLastLocation, spatialConnection.getTo())) {
            narrateMovingGOKommtSCEntgegenUndGehtAnSCVorbei();
            return;
        }

        narrateGehtWeg(spatialConnection, numberOfWaysOut);
    }

    private void narrateMovingGOKommtSCEntgegenUndGehtAnSCVorbei() {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaph = anaph(false);

        final AltDescriptionsBuilder alt = alt();

        alt.add(neuerSatz(PARAGRAPH,
                anaph.nomK(),
                "kommt dir entgegen und geht an dir vorbei"));
        alt.add(neuerSatz(PARAGRAPH,
                anaph.nomK(),
                "kommt dir entgegen und geht an dir vorbei"));
        alt.add(neuerSatz(PARAGRAPH,
                anaph.nomK(),
                "kommt auf dich zu und geht an dir vorbei", PARAGRAPH));
        alt.add(neuerSatz(PARAGRAPH,
                anaph.nomK(),
                "kommt auf dich zu und läuft vorbei", PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(neuerSatz(PARAGRAPH,
                    "Dir kommt",
                    desc.nomK(),
                    "entgegen und geht an dir vorbei", PARAGRAPH));
            // IDEA "Dir kommt ... entgegen und geht hinter dir seiner / ihrer Wege (Genitiv!...)
            alt.add(neuerSatz(PARAGRAPH,
                    "Dir kommt",
                    desc.nomK(),
                    "entgegen und geht hinter dir davon", PARAGRAPH));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private void narrateGehtWeg(
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        final SubstantivischePhrase anaph = anaph(false);

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysOut);

        n.narrateAlt(NO_TIME,
                neuerSatz(PARAGRAPH,
                        anaph.nomK(),
                        "geht",
                        wo, // "auf dem Weg "
                        "davon", PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaph.nomK(),
                        "geht",
                        wo, // "auf dem Weg "
                        "weiter", PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaph.nomK(),
                        "geht weg", PARAGRAPH),
                // IDEA: "X geht seines / ihres Wegs" - Possessivartikel vor Genitiv!
                // IDEA: "X geht seiner / ihrer Wege" - Possessivartikel vor Genitiv!
                neuerSatz(PARAGRAPH,
                        anaph.nomK(),
                        "geht fort", PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaph.nomK(),
                        "läuft vorbei", PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaph.nomK(),
                        "läuft weiter", PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoEnters(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        narrateEnters(from, to, spatialConnection, numberOfWaysIn);

        world.loadSC().memoryComp().narrateAndUpgradeKnown(gameObjectId);
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

            if (World.isOrHasRecursiveLocation(scLastLocation, from)) {
                narrateMovingGOKommtSCNach(
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
        narrateKommtGegangen(spatialConnection, numberOfWaysIn);
    }

    private void narrateMovingGOKommtSCNach(
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaph = anaph(false);

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysIn);

        final AltDescriptionsBuilder alt = alt();

        alt.add(neuerSatz(PARAGRAPH,
                anaph.nomK(),
                "kommt dir",
                wo,
                "hinterher", PARAGRAPH));
        alt.add(neuerSatz(PARAGRAPH,
                anaph.nomK(),
                "kommt dir hinterher", PARAGRAPH));
        alt.add(neuerSatz(PARAGRAPH,
                anaph.nomK(),
                "kommt hinter dir her", PARAGRAPH));
        alt.add(neuerSatz(PARAGRAPH,
                anaph.nomK(),
                "kommt dir hinterhergegangen", PARAGRAPH));
        alt.add(neuerSatz(PARAGRAPH,
                anaph.nomK(),
                "ist dir nachgekommen", PARAGRAPH));

        if (!n.isThema(gameObjectId)) {
            alt.add(neuerSatz(PARAGRAPH,
                    "Hinter dir kommt",
                    desc.nomK(),
                    "gegangen", PARAGRAPH));
        }

        n.narrateAlt(alt, NO_TIME);
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
            );
            return;
        }

        narrateMovingGOKommtScEntgegen_esVerstehtSichNichtVonSelbstVonWo(
                scFrom, to, movingGOFrom, spatialConnectionMovingGO);
    }

    private void narrateMovingGOKommtScEntgegen_esVerstehtSichVonSelbstVonWo() {
        final Nominalphrase desc = getDescription();

        final AltDescriptionsBuilder alt = alt();


        alt.add(neuerSatz(desc.nomK(), "kommt dir entgegen", PARAGRAPH));

        n.narrateAlt(alt, NO_TIME);
    }

    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateMovingGOKommtScEntgegen_esVerstehtSichNichtVonSelbstVonWo(
            @Nullable final ILocationGO scFrom,
            final ILocationGO to,
            final FROM movingGOFrom,
            @Nullable final SpatialConnection spatialConnectionMovingGO) {
        final Nominalphrase desc = getDescription();
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
                        "kommt",
                        desc.nomK(),
                        "gegangen", PARAGRAPH));
            }

            alt.add(
                    neuerSatz("Es kommt dir",
                            desc.nomK(),
                            "entgegen", PARAGRAPH));
            alt.add(
                    neuerSatz(PARAGRAPH,
                            "Dir kommt",
                            desc.nomK(),
                            "entgegen", PARAGRAPH));
        }

        n.narrateAlt(alt, NO_TIME);
    }

    private void narrateKommtGegangen(
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        final Nominalphrase desc = getDescription();

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysIn);

        n.narrateAlt(NO_TIME,
                neuerSatz(PARAGRAPH,
                        wo, // "auf dem Weg "
                        "kommt",
                        desc.nomK(), PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nomK(),
                        "kommt",
                        wo, // "auf dem Weg "
                        "daher", PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nomK(),
                        "kommt",
                        wo, // "auf dem Weg "
                        "gegangen", PARAGRAPH)
        );
    }

    @Nullable
    private static String calcWoIfNecessary(
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWays) {
        if (spatialConnection != null &&
                (numberOfWays == NO_WAY ||
                        numberOfWays == NumberOfWays.SEVERAL_WAYS)) {
            return spatialConnection.getWo();
        }

        return null;
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
    protected final SubstantivischePhrase anaph(final boolean descShortIfKnown) {
        final IDescribableGO describableGO = (IDescribableGO) world.load(getGameObjectId());

        return world.anaph(describableGO, descShortIfKnown);
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
