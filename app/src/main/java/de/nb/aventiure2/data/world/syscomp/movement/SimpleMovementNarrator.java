package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.NO_WAY;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * Grundlegende Implementierung, um dem Spieler die Bewegung
 * eines {@link de.nb.aventiure2.data.world.syscomp.movement.IMovingGO} zu beschreiben
 */
public class SimpleMovementNarrator implements IMovementNarrator {
    protected final GameObjectId gameObjectId;
    protected final StoryStateDao n;
    protected final World world;

    /**
     * Ob das Wesen, das sich bewegt, eher groß ist (z.B. ein Mensch, kein Frosch)
     */
    private final boolean eherGross;

    public SimpleMovementNarrator(
            final GameObjectId gameObjectId,
            final StoryStateDao storyStateDao,
            final World world,
            final boolean eherGross) {
        this.gameObjectId = gameObjectId;
        n = storyStateDao;
        this.world = world;
        this.eherGross = eherGross;
    }

    public AvTimeSpan narrateScTrifftStehendesMovingGO(final ILocationGO location) {
        final Nominalphrase desc = getDescription();
        final Nominalphrase descShort = getDescription(true);

        final String wo = location.storingPlaceComp().getLocationMode().getWo(eherGross);

        return n.addAlt(
                neuerSatz(SENTENCE,
                        "Hier steht " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(SENTENCE,
                        wo +
                                " steht " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(SENTENCE,
                        wo +
                                " siehst du " +
                                descShort.nom() +
                                " stehen",
                        noTime())
                        .phorikKandidat(descShort, gameObjectId)
                        .beendet(PARAGRAPH),
                du("triffst",
                        desc.akk(),
                        noTime())
                        .undWartest()
                        .phorikKandidat(desc, gameObjectId),
                du("triffst",
                        desc.akk() + " " +
                                wo,
                        wo,
                        noTime())
                        .undWartest()
                        .phorikKandidat(desc, gameObjectId),
                du("triffst",
                        wo +
                                " " +
                                "auf " +
                                desc.akk(),
                        wo,
                        noTime())
                        .undWartest()
                        .phorikKandidat(desc, gameObjectId),
                du("triffst",
                        "auf " +
                                desc.akk(), noTime())
                        .undWartest()
                        .phorikKandidat(desc, gameObjectId),
                du(SENTENCE,
                        "begegnest",
                        wo + " " + desc.dat(),
                        wo,
                        noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                du(SENTENCE,
                        "begegnest",
                        desc.dat(), noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    public <FROM extends ILocationGO & ISpatiallyConnectedGO> AvTimeSpan
    narrateScTrifftEnteringMovingGO(
            @Nullable final ILocationGO scFrom, final FROM movingGOFrom) {
        if (scFrom != null && movingGOFrom != null) {
            if (world.isOrHasRecursiveLocation(scFrom, movingGOFrom)) {
                // IMovingGO und SC sind denselben Weg gegangen, das IMovingGO ist noch nicht
                // im "Zentrum" angekommen
                return narrateScUeberholtMovingGO();
            }

            @Nullable final SpatialConnection spatialConnection =
                    movingGOFrom.spatialConnectionComp().getConnection(scFrom.getId());

            final NumberOfWays numberOfWaysIn =
                    scFrom instanceof ISpatiallyConnectedGO ?
                            ((ISpatiallyConnectedGO) scFrom).spatialConnectionComp()
                                    .getNumberOfWaysOut() :
                            NumberOfWays.NO_WAY;

            if (spatialConnection != null) {
                return narrateMovingGOKommtScEntgegen(
                        movingGOFrom,
                        scFrom,
                        spatialConnection,
                        numberOfWaysIn);
            }
        }

        return narrateScTrifftEnteringMovingGO_ScOderMovingGOHabenKeinenVorigenOrt();
    }

    public AvTimeSpan narrateScUeberholtMovingGO() {
        final Nominalphrase desc = getDescription();

        return n.addAlt(
                du("gehst",
                        "an " +
                                desc.dat() +
                                " vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(SENTENCE),
                du("gehst schnellen Schrittes an " +
                        desc.dat() +
                        " vorüber", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(SENTENCE),
                du("gehst mit schnellen Schritten an " +
                        desc.dat() +
                        " vorüber", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(SENTENCE)
        );
    }

    public AvTimeSpan narrateScTrifftEnteringMovingGO_ScOderMovingGOHabenKeinenVorigenOrt() {
        final Nominalphrase desc = getDescription();

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        "Dir begegnet " + desc.nom(), noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                du("begegnest ",
                        desc.dat(), noTime())
                        .phorikKandidat(desc, gameObjectId)
        );
    }

    public AvTimeSpan narrateScTrifftLeavingMovingGO(final ILocationGO scTo,
                                                     final ILocationGO movingGOTo) {
        if (world.isOrHasRecursiveLocation(scTo, movingGOTo)) {
            return narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich();
        }

        return narrateScSiehtMovingGOFortgehen();
    }

    public AvTimeSpan narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich() {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        return n.addAlt(
                du(SENTENCE,
                        anaphOderDesc.nom() +
                                " kommt dir entgegen und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht hinter dir davon", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom() +
                                " kommt auf dich zu und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom() +
                                " kommt auf dich zu und läuft vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    public AvTimeSpan narrateScSiehtMovingGOFortgehen() {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        return n.addAlt(
                du(SENTENCE, "siehst",
                        ", wie " +
                                anaphOderDesc.nom() +
                                " von dannen geht", noTime())
                        .komma()
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                du(SENTENCE, "siehst",
                        anaphOderDesc.akk() +
                                " davongehen", noTime())
                        .komma()
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                neuerSatz(SENTENCE,
                        anaphOderDesc.nom() +
                                " ist gerade dabei, davonzugehen", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(SENTENCE,
                        "Vor dir geht " +
                                desc.nom(), noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                neuerSatz(SENTENCE,
                        "Ein Stück vor dir geht " +
                                desc.nom(), noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                neuerSatz(SENTENCE,
                        desc.nom() +
                                " geht gerade fort", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    @Override
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateAndDoStartsLeaving(
            final FROM from,
            final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        final AvTimeSpan extraTime =
                narrateStartsLeaving(from, to, spatialConnection, numberOfWaysOut);

        world.upgradeKnownToSC(gameObjectId);

        return extraTime;
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateStartsLeaving(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        @Nullable final ILocationGO scLastLocation = loadSC().locationComp().getLastLocation();

        if (world.isOrHasRecursiveLocation(scLastLocation, spatialConnection.getTo())) {
            return narrateMovingGOKommtSCEntgegenUndGehtAnSCVorbei(
                    from, to, spatialConnection);
        }

        return narrateGehtWeg(from, to, spatialConnection, numberOfWaysOut);
    }

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateMovingGOKommtSCEntgegenUndGehtAnSCVorbei(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt dir entgegen und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " kommt dir entgegen und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht hinter dir von dannen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " entgegen und geht hinter dir davon", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom() +
                                " kommt auf dich zu und geht an dir vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom() +
                                " kommt auf dich zu und läuft vorbei", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateGehtWeg(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysOut) {
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysOut);

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht "
                                + wo // "auf dem Weg "
                                + "von dannen", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId)
                        .beendet(PARAGRAPH),
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
    AvTimeSpan narrateAndDoStartsEntering(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        final AvTimeSpan extraTime =
                narrateStartsEntering(from, to, spatialConnection, numberOfWaysIn);

        world.upgradeKnownToSC(gameObjectId);

        return extraTime;
    }

    final protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateStartsEntering(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        @Nullable final ILocationGO scLastLocation =
                loadSC().locationComp().getLastLocation();
        if (loadSC().memoryComp().getLastAction().is(BEWEGEN)) {
            if (world.isOrHasRecursiveLocation(scLastLocation, from)) {
                return narrateMovingGOKommtSCNach(from, to, spatialConnection, numberOfWaysIn);
            }

            if (!world.isOrHasRecursiveLocation(scLastLocation, from)) {
                return narrateMovingGOKommtScEntgegen(from, to, spatialConnection, numberOfWaysIn);
            }
        }

        // Default
        return narrateKommtGegangen(from, to, spatialConnection, numberOfWaysIn);
    }

    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateMovingGOKommtSCNach(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysIn);

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " nach", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Hinter dir geht " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() +
                                " geht hinter dir her", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Hinter dir kommt " +
                                desc.nom() +
                                " gegangen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateMovingGOKommtScEntgegen(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        final Nominalphrase desc = getDescription();

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysIn);

        return n.addAlt(
                neuerSatz(SENTENCE,
                        (wo.equals("") ?
                                "Es " :
                                wo) // "auf dem Pfad "
                                + "kommt dir " +
                                desc.nom() +
                                " entgegen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " "
                                + wo // "auf dem Weg "
                                + "entgegen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Dir kommt " +
                                desc.nom() +
                                " "
                                + wo // "auf dem Weg "
                                + "entgegengegangen", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH)
        );
    }

    @NonNull
    protected <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateKommtGegangen(
            final FROM from, final ILocationGO to,
            @Nullable final SpatialConnection spatialConnection,
            final NumberOfWays numberOfWaysIn) {
        final Nominalphrase desc = getDescription();
        final SubstantivischePhrase anaphOderDesc =
                getAnaphPersPronWennMglSonstDescription(false);

        final String wo = calcWoIfNecessary(spatialConnection, numberOfWaysIn);

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt herzu", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt "
                                + wo // "auf dem Weg "
                                + "heran", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        anaphOderDesc.nom() + " kommt "
                                + wo // "auf dem Weg "
                                + "gegangen", noTime())
                        .phorikKandidat(anaphOderDesc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        "Es kommt " +
                                desc.nom(), noTime())
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt "
                                + wo // "auf dem Weg "
                                + "daher", noTime())
                        .phorikKandidat(desc, gameObjectId)
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt "
                                + wo // "auf dem Weg "
                                + "gegangen", noTime())
                        .phorikKandidat(desc, gameObjectId),
                neuerSatz(PARAGRAPH,
                        desc.nom()
                                + " kommt", noTime())
                        .phorikKandidat(desc, gameObjectId)
        );
    }

    @NonNull
    private static String calcWoIfNecessary(@Nullable final SpatialConnection spatialConnection,
                                            final NumberOfWays numberOfWaysOut) {
        if (spatialConnection != null &&
                (numberOfWaysOut == NO_WAY ||
                        numberOfWaysOut == NumberOfWays.SEVERAL_WAYS)) {
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
