package de.nb.aventiure2.scaction.impl;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.List;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.base.SpatialConnectionData;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractFlexibleDescription;
import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONLY_WAY;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.german.base.GermanUtil.buildAufzaehlung;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAusTimed;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.DISKONTINUITAET;
import static java.util.stream.Collectors.toList;

/**
 * Der Spielercharakter bewegt sich in einen anderen Raum oder in ein Objekt, das im
 * aktuellen Raum (oder aktuellen Objekt) enthalten ist - oder aus einem solchen
 * Objekt heraus.
 */
public class BewegenAction<LOC_DESC extends ILocatableGO & IDescribableGO>
        extends AbstractScAction {

    private final ILocationGO oldLocation;

    private final SpatialConnection spatialConnection;

    private final CounterDao counterDao;
    /**
     * Hier werden "Wege" in untergeordnete Objekte (der Weg "auf den Tisch") oder aus
     * untergeordneten Objekten heraus nicht mitgezählt.
     */
    private final NumberOfWays numberOfWays;

    public static ImmutableList<AbstractScAction> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final CounterDao counterDao,
            final Narrator n,
            final World world,
            @NonNull final ILocationGO location) {
        final ImmutableList.Builder<AbstractScAction> res = builder();

        //  FIXME Man könnte "Der mageren Frau heimlich folgen", wenn sie gerade in eine
        //   Richtung gegangen ist? Dann würde man sie heimlich beobachten (nicht überholen!),
        //   wie sie den Turm hinaufsteigt.
        if (location instanceof ISpatiallyConnectedGO) {
            res.addAll(buildSpatiallyConnectedActions(scActionStepCountDao, timeTaker, counterDao,
                    n, world,
                    (ILocationGO & ISpatiallyConnectedGO) location));
        }

        for (final ILocationGO inventoryGO :
                world.loadDescribableNonLivingLocationInventory(location)) {
            @Nullable final SpatialConnectionData inData =
                    inventoryGO.storingPlaceComp().getSpatialConnectionInData();
            if (inData != null) {
                res.add(new BewegenAction<>(
                        scActionStepCountDao, timeTaker,
                        counterDao, n, world, location,
                        con(inventoryGO.getId(), inData),
                        NumberOfWays.NO_WAY));
            }
        }

        @Nullable final SpatialConnectionData outData =
                location.storingPlaceComp().getSpatialConnectionOutData();
        if (outData != null && location instanceof ILocatableGO) {
            @Nullable final ILocationGO outerLocation =
                    ((ILocatableGO) location).locationComp().getLocation();
            if (outerLocation != null) {
                res.add(new BewegenAction<>(scActionStepCountDao, timeTaker,
                        counterDao, n, world, location,
                        con(outerLocation.getId(), outData),
                        NumberOfWays.NO_WAY));
            }
        }

        return res.build();
    }

    private static <LOC extends ILocationGO & ISpatiallyConnectedGO>
    ImmutableList<AbstractScAction> buildSpatiallyConnectedActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker nowDao,
            final CounterDao counterDao,
            final Narrator n,
            final World world,
            @NonNull final LOC location) {
        final ImmutableList.Builder<AbstractScAction> res = builder();

        final List<SpatialConnection> spatialConnections =
                location.spatialConnectionComp().getConnections();

        final NumberOfWays numberOfWays = NumberOfWays.get(spatialConnections.size());

        for (final SpatialConnection spatialConnection : spatialConnections) {
            res.add(new BewegenAction<>(scActionStepCountDao, nowDao,
                    counterDao, n, world, location,
                    spatialConnection, numberOfWays));
        }

        return res.build();
    }

    /**
     * Creates a new {@link BewegenAction}.
     */
    @VisibleForTesting
    BewegenAction(final SCActionStepCountDao scActionStepCountDao,
                  final TimeTaker timeTaker,
                  final CounterDao counterDao, final Narrator n,
                  final World world,
                  @NonNull final ILocationGO oldLocation,
                  @NonNull final SpatialConnection spatialConnection,
                  final NumberOfWays numberOfWays) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.counterDao = counterDao;
        this.numberOfWays = numberOfWays;
        this.oldLocation = oldLocation;
        this.spatialConnection = spatialConnection;
    }

    @Override
    public String getType() {
        return "actionBewegen";
    }

    @Override
    @NonNull
    public String getName() {
        return spatialConnection.getActionName();
    }

    @Override
    public void narrateAndDo() {
        narrateLocationOnly(loadTo());

        world.loadSC().memoryComp().upgradeKnown(spatialConnection.getTo());

        // Die nicht-movable Objekte sollten in der Location-beschreibung
        // alle enthalten gewesen sein (mindestens implizit), auch rekursiv.
        // Sie müssen auf Known gesetzt werden, damit gleich etwas wie
        // "Auf dem Tisch liegt eine Kugel" geschrieben werden kann.
        // Dabei gehen wir mal davon aus, dass alle diese nicht movablen-
        // Objekte auch bei Dunkelheit erkennbar sind - andererseits
        // hätten wir zumindest bei den nicht-movablen Locations das Problem,
        // dass man z.B. den Tisch bei Dunkelheit nicht sieht, aber
        // trotzdem die Kugel, die drauf liegt?!

        // TODO "Der Frosch ist nicht mehr da" o.Ä !! Wenn man ihn irgendwo hingesetzt hat.
        //  Aber nur, wenn Frosch (noch) bekannt ist und sein letzter Ort bekannt war
        //  und es keine guten Gründe gibt, dass er nicht mehr da sein sollte. Und nur einmal,
        //  dann assumeNoLocation() aufrufen!

        // Lebende Dinge, sind hier ausgeschlossen, sie müssen sich ggf. in einer
        // ReactionsComp selbst beschreiben.
        upgradeNonLivingNonMovableRecursiveInventoryKnown(loadTo());

        if (scWirdMitEssenKonfrontiert()) {
            sc.feelingsComp().narrateAndDoSCMitEssenKonfrontiert();
        } else if (scWirdMitSchlafgelegenheitKonfrontiert()) {
            sc.feelingsComp().narrateAndDoSCMitSchlafgelegenheitKonfrontiert();
        }

        narrateAndUpdateFeelings();

        sc.locationComp().narrateAndSetLocation(spatialConnection.getTo(),
                this::narrateNonLivingMovableObjectsAndUpgradeKnownAndSetLastAction);
    }

    private void upgradeNonLivingNonMovableRecursiveInventoryKnown(
            @NonNull final ILocationGO location) {
        final ImmutableList<? extends IGameObject> directlyContainedNonLivingNonMovables =
                world.loadDescribableNonLivingNonMovableInventory(location.getId());
        sc.memoryComp().upgradeKnown(directlyContainedNonLivingNonMovables);

        for (final IGameObject directlyContainedNonLivingNonMovable : directlyContainedNonLivingNonMovables) {
            if (directlyContainedNonLivingNonMovable instanceof ILocationGO) {
                upgradeNonLivingNonMovableRecursiveInventoryKnown(
                        (ILocationGO) directlyContainedNonLivingNonMovable);
            }
        }
    }

    private void narrateNonLivingMovableObjectsAndUpgradeKnownAndSetLastAction() {
        // Unbewegliche Objekte sollen bereits in der Location-Beschreibung mitgenannt werden,
        // nicht hier! (Das betrifft auch indirekt enthaltene unbewegliche Objekte.)

        if (!world.isOrHasRecursiveLocation(spatialConnection.getTo(), oldLocation)) {
            // Wenn man z.B. in einem Zimmer auf einen Tisch steigt: Nicht noch einmal
            // beschreiben, was sonst noch auf dem Tisch steht!

            narrateNonLivingMovableObjectsAndUpgradeKnown(
                    // Wenn man z.B. von einem Tisch heruntersteigt oder
                    // einmal um einen Turm herumgeht, dann noch noch einmal
                    // beschreiben, was sich auf dem Tisch oder vor dem Turm
                    // befindet
                    oldLocation);
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateNonLivingMovableObjectsAndUpgradeKnown(
            @Nullable final ILocationGO excludedLocation) {
        final ImmutableList.Builder<String> descriptionsPerLocation = builder();
        int numMovableObjectsInLocation = 0;
        @Nullable IDescribableGO lastObjectInLocation = null;
        for (final Pair<ILocationGO, ? extends List<? extends IDescribableGO>> locationAndDescribables :
                buildRecursiveLocationsAndDescribables(loadTo())) {
            if (excludedLocation == null ||
                    !world.isOrHasRecursiveLocation(
                            locationAndDescribables.first, excludedLocation)) {
                descriptionsPerLocation.add(
                        //  "auf dem Boden liegen A und B"
                        buildObjectsInLocationDescription(locationAndDescribables));

                numMovableObjectsInLocation += locationAndDescribables.second.size();
                lastObjectInLocation = locationAndDescribables.second
                        .get(locationAndDescribables.second.size() - 1);

                upgradeKnown(locationAndDescribables);
            }
        }

        if (numMovableObjectsInLocation == 0) {
            return;
        }

        //  "Auf dem Boden liegen A und B und auf dem Tisch liegt C"
        final String movableObjectsInLocationDescription =
                buildMovableObjectsInLocationDescription(descriptionsPerLocation.build());

        narrateObjects(movableObjectsInLocationDescription,
                numMovableObjectsInLocation,
                lastObjectInLocation);
    }

    private void narrateObjects(final String objectsDescription, final int numObjects,
                                final IDescribableGO lastObject) {
        n.narrate(
                neuerSatz(objectsDescription, secs(numObjects * 2))
                        .phorikKandidat(
                                world.getDescription(lastObject, false),
                                lastObject.getId()));
    }

    private void upgradeKnown(
            @NonNull
            final Pair<ILocationGO, ? extends List<? extends IDescribableGO>> locationAndDescribables) {
        sc.memoryComp().upgradeKnown(locationAndDescribables.second);

        for (final IGameObject gameObject : locationAndDescribables.second) {
            if (gameObject instanceof ILocationGO) {
                // Die untergeordnet enthaltenen nicht-movable Objekte sollten in der Beschreibung
                // des gameObject alle enthalten gewesen sein (mindestens implizit), auch rekursiv.
                // Sie müssen auf Known gesetzt werden, damit gleich etwas wie
                // "Hier steht ein Tisch mit einer schweren Vase darauf. In der schweren Vase
                // liegt eine Kugel" geschrieben werden kann.
                // Dabei gehen wir mal davon aus, dass alle diese nicht movablen-
                // Objekte auch bei Dunkelheit erkennbar sind - andererseits
                // hätten wir zumindest bei den nicht-movablen Locations das Problem,
                // dass man z.B. die schwere Vase bei Dunkelheit nicht sieht, aber
                // trotzdem die Kugel, die drauf liegt?!
                upgradeNonLivingNonMovableRecursiveInventoryKnown((ILocationGO) gameObject);
            }
        }
    }

    @NonNull
    private ILocationGO loadTo() {
        return (ILocationGO) world.load(spatialConnection.getTo());
    }

    @NonNull
    private String buildObjectsInLocationDescription(
            @NonNull
            final Pair<ILocationGO, ? extends List<? extends IDescribableGO>> locationAndDescribables) {
        return buildObjectsInLocationDescription(
                locationAndDescribables.first,
                locationAndDescribables.second);
    }

    @NonNull
    private static String buildMovableObjectsInLocationDescription(
            final ImmutableList<String> descriptionsPerLocation) {
        return capitalize(buildAufzaehlung(descriptionsPerLocation));
    }

    private ImmutableList<Pair<ILocationGO, ? extends List<LOC_DESC>>> buildRecursiveLocationsAndDescribables(
            @NonNull final ILocationGO location) {
        final ImmutableList.Builder<
                Pair<ILocationGO, ? extends List<LOC_DESC>>>
                res = builder();

        final ImmutableList<LOC_DESC> movableObjectsInLocation =
                world.loadDescribableNonLivingMovableInventory(location.getId());

        if (!movableObjectsInLocation.isEmpty()) {
            res.add(new Pair<ILocationGO, List<LOC_DESC>>(
                    location, movableObjectsInLocation));
        }

        for (final ILocationGO directContainedLocations :
                world.loadDescribableNonLivingLocationInventory(location)) {
            res.addAll(buildRecursiveLocationsAndDescribables(directContainedLocations));
        }

        return res.build();
    }

    @NonNull
    private String buildObjectsInLocationDescription(
            final ILocationGO location,
            @NonNull final List<? extends IDescribableGO> movableObjectsInLocation) {
        return buildObjectInLocationDescriptionPrefix(location,
                movableObjectsInLocation.size())
                + " "
                + buildDescriptionAufzaehlung(movableObjectsInLocation);
    }

    private boolean scWirdMitEssenKonfrontiert() {
        final GameObject newLocation = world.load(spatialConnection.getTo());

        if (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                .hasState(BEGONNEN)) {
            if (oldLocation.is(DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    newLocation.is(SCHLOSS_VORHALLE)) {
                return true;
            }
            if (newLocation.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
                return true;
            }
        }

        if (newLocation.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            // FIXME Im Dunkeln kann man keine Früchte sehen? Oder doch?!
            return true;
        }

        return false;
    }

    private boolean scWirdMitSchlafgelegenheitKonfrontiert() {
        final GameObject newLocation = world.load(spatialConnection.getTo());
        if (oldLocation.is(VOR_DER_HUETTE_IM_WALD) && newLocation.is(HUETTE_IM_WALD)) {
            return true;
        }

        return false;
    }

    /**
     * Aktualisiert den Gemütszustand des Spielercharakters. "Zeit heilt alle Wunden" - oder so
     * ähnlich.
     */
    private void narrateAndUpdateFeelings() {
        if (oldLocation.is(SCHLOSS_VORHALLE)
                && spatialConnection.getTo().equals(DRAUSSEN_VOR_DEM_SCHLOSS)
                && sc.feelingsComp().hasMood(Mood.ANGESPANNT)) {
            sc.feelingsComp().requestMood(Mood.NEUTRAL);
        } else if (spatialConnection.getTo().equals(BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD) &&
                counterDao.get(BaumFactory.HOCHKLETTERN) > 2) {
            sc.feelingsComp().narrateAndUpgradeTemporaereMinimalmuedigkeit(
                    FeelingIntensity.NUR_LEICHT, mins(15)
            );
        } else if (oldLocation.is(BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD) &&
                counterDao.get(BaumFactory.HINABKLETTERN) != 2) {
            sc.feelingsComp().narrateAndUpgradeTemporaereMinimalmuedigkeit(
                    FeelingIntensity.MERKLICH, mins(45)
            );
        } else if (sc.feelingsComp().hasMood(Mood.ETWAS_GEKNICKT)) {
            sc.feelingsComp().requestMood(Mood.NEUTRAL);
        }
    }

    private void narrateLocationOnly(@NonNull final ILocationGO to) {
        // FIXME Verkürzungen automatisch erzeugen, z.B. erzeugen eines Nachfelds nach einem
        //  Prädikat: "und weiter in Richtung Schloss".

        // STORY Wenn Bewegung Wiederholung ist (z.B. Rund um den Turm): Zur Sicherheit...
        //  noch einmal. Um sicher zu
        //  gehen... noch einmal. Du gehst SOGAR noch einmal...

        final TimedDescription<?> description = getNormalDescription(
                to.storingPlaceComp().getLichtverhaeltnisse());

        if (description.getDescription() instanceof AbstractFlexibleDescription &&
                ((AbstractFlexibleDescription<?>) description.getDescription()).hasSubjektDu() &&
                n.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                isDefinitivDiskontinuitaet()) {
            final ImmutableList.Builder<TimedDescription<?>> alt = builder();

            final TextDescription descriptionSatzanschlussOhneSubjekt =
                    ((AbstractFlexibleDescription<?>) description.getDescription())
                            .toTextDescriptionSatzanschlussOhneSubjekt();
            alt.add(new TimedDescription<>(
                    descriptionSatzanschlussOhneSubjekt
                            .mitPraefix(", besinnst dich aber und "),
                    description.getTimeElapsed()));

            alt.addAll(drueckeAusTimed(DISKONTINUITAET, description));
            n.narrateAlt(alt);
            return;
        }

        if (description.getStartsNew() == WORD &&
                n.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                description.getDescription() instanceof AbstractFlexibleDescription) {
            n.narrate(description);
            return;
        }

        if (isDefinitivDiskontinuitaet()) {
            final ImmutableList.Builder<TimedDescription<?>> alt = builder();

            final TextDescription descriptionHauptsatz =
                    description.getDescription().toTextDescription();
            if (numberOfWays == ONLY_WAY) {
                alt.add(
                        new TimedDescription<>(
                                du("schaust",
                                        GermanUtil.joinToString(
                                                "dich nur kurz um, dann",
                                                descriptionHauptsatz.toWortfolge()))
                                        .woertlicheRedeNochOffen(
                                                descriptionHauptsatz.isWoertlicheRedeNochOffen())
                                        .komma(descriptionHauptsatz.isKommaStehtAus()),
                                description.getTimeElapsed())
                                .undWartest(
                                        description
                                                .isAllowsAdditionalDuSatzreihengliedOhneSubjekt()));
            } else {
                alt.add(new TimedDescription<>(
                        descriptionHauptsatz
                                .mitPraefixCapitalize("Was willst du hier eigentlich? ")
                        , description.getTimeElapsed()));
                if (description.getDescription() instanceof AbstractFlexibleDescription<?> &&
                        ((AbstractFlexibleDescription<?>) description.getDescription())
                                .hasSubjektDu()) {
                    alt.add(new TimedDescription<>(
                            ((AbstractFlexibleDescription<?>) description.getDescription())
                                    .toTextDescriptionMitSpeziellemVorfeld()
                                    .mitPraefixCapitalize("Was willst du hier eigentlich? "),
                            description.getTimeElapsed()));
                }

                alt.addAll(drueckeAusTimed(DISKONTINUITAET, description));
            }
            n.narrateAlt(alt);
            return;
        }

        if (sc.memoryComp().getLastAction().is(BEWEGEN)) {
            if (n.endsThisIsExactly(StructuralElement.WORD) && n.dann()) {
                // "Du stehst wieder vor dem Schloss. Dann gehst du wieder hinein in das Schloss."
                final TextDescription satzEvtlMitDann = description.getDescription()
                        .toTextDescriptionMitKonjunktionaladverbWennNoetig("dann")
                        .beginntZumindestSentence();
                if (satzEvtlMitDann.getText().startsWith("Dann")) {
                    satzEvtlMitDann.dann(false);
                }

                n.narrate(description.withDescription(satzEvtlMitDann));
                return;
            } else {
                n.narrate(description);
                return;
            }
        } else {
            if (n.dann()) {
                n.narrate(description.withDescription(
                        description.getDescription()
                                .toTextDescriptionMitKonjunktionaladverbWennNoetig(
                                        "danach")
                                .beginntZumindestParagraph()));
                return;
            }

            n.narrate(description);
            return;
        }
    }

    private TimedDescription<?> getNormalDescription(final Lichtverhaeltnisse
                                                             lichtverhaeltnisseInNewLocation) {
        final Known newLocationKnown = sc.memoryComp().getKnown(spatialConnection.getTo());

        boolean alternativeDescriptionAllowed = false;
        if (oldLocation instanceof ISpatiallyConnectedGO &&
                world.load(spatialConnection.getTo()) instanceof ISpatiallyConnectedGO) {
            alternativeDescriptionAllowed =
                    ((ISpatiallyConnectedGO) oldLocation).spatialConnectionComp()
                            .isAlternativeMovementDescriptionAllowed(
                                    spatialConnection.getTo(),
                                    newLocationKnown, lichtverhaeltnisseInNewLocation);
        }

        final TimedDescription<?> standardDescription =
                getStandardDescription(newLocationKnown, lichtverhaeltnisseInNewLocation);

        if (!alternativeDescriptionAllowed ||
                oldLocation.is(spatialConnection.getTo()) ||
                // Immer, wenn ein Counter hochgezählt werden soll, wird es sinnvoll sein,
                // die standardDescription anzuzeigen!
                standardDescription.getCounterIdIncrementedIfTextIsNarrated() != null) {
            return standardDescription;
        }

        if (newLocationKnown == Known.KNOWN_FROM_LIGHT) {
            if (numberOfWays == ONLY_WAY) {
                if (sc.feelingsComp().hasMood(Mood.VOLLER_FREUDE) &&
                        lichtverhaeltnisseInNewLocation == HELL &&
                        n.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                        sc.memoryComp().getLastAction().is(Action.Type.NEHMEN)) {
                    return du("springst", "damit fort", "damit",
                            standardDescription.getTimeElapsed().times(0.8))
                            .undWartest()
                            .dann();
                }

                if (sc.feelingsComp().hasMood(Mood.UNTROESTLICH)) {
                    return du("trottest", "tieftraurig von dannen", "tieftraurig",
                            standardDescription.getTimeElapsed().times(2))
                            .undWartest();
                }
            } else if (numberOfWays == ONE_IN_ONE_OUT
                    && sc.memoryComp().getLastAction().is(BEWEGEN) &&
                    !sc.locationComp().lastLocationWas(spatialConnection.getTo()) &&
                    sc.feelingsComp().hasMood(Mood.VOLLER_FREUDE) &&
                    lichtverhaeltnisseInNewLocation ==
                            HELL &&
                    n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                return du("eilst", "weiter", standardDescription.getTimeElapsed().times(0.8))
                        .undWartest();
            }
        }

        return standardDescription;
    }

    @VisibleForTesting
    TimedDescription<?> getStandardDescription(final Known newLocationKnown,
                                               final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {

        return spatialConnection.getSCMoveDescriptionProvider()
                .getSCMoveTimedDescription(newLocationKnown, lichtverhaeltnisseInNewLocation)
                .multiplyTimeElapsedWith(calcSpeedFactor());
    }

    private double calcSpeedFactor() {
        return world.loadSC().feelingsComp().getMovementSpeedFactor();
    }

    @NonNull
    private static String buildObjectInLocationDescriptionPrefix(
            @NonNull final ILocationGO location,
            final int numberOfObjects) {
        final String res = location.storingPlaceComp().getLocationMode().getWo(false);

        if (numberOfObjects == 1) {
            return res + " liegt";
        }

        return res + " liegen";
    }

    /**
     * Gibt eine Aufzählung zurück wie "der hässliche Frosch",
     * "die goldene Kugel und der hässliche Frosch" oder "das schöne Glas, die goldene Kugel und
     * der hässliche Frosch".
     */
    @NonNull
    private String buildDescriptionAufzaehlung(
            @NonNull final List<? extends IDescribableGO> describables) {
        return buildAufzaehlung(
                describables.stream()
                        .map(this::getNom)
                        .collect(toList())
        );
    }

    private String getNom(final IDescribableGO d) {
        return world.getDescription(d, false).nom();
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction())
                && !isDefinitivDiskontinuitaet();
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return sc.memoryComp().getLastAction().is(BEWEGEN)
                && !isDefinitivDiskontinuitaet();
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return
                // Es ist oft keine Diskontinuität, wenn
                // zwischen zwei Aktionen eine Reaction liegt. Z.B.: Spieler verlässt das Schloss,
                // sieht den Prinzen wegfahren und geht danach wieder in das Schloss.
                // Anderes fiktives Beispiel: Spieler nimmt der Frosch, der Frosch quakt hässlich,
                //  der Spieler setzt den Frosch wieder ab. Oder: Der Spieler geht
                // irgendwo hin, die Zauberin kommt ihm entgegen, er kehrt um und geht ihr nach.
                !n.lastNarrationWasFromReaction() &&
                        sc.memoryComp().getLastAction().is(BEWEGEN) &&
                        !oldLocation.is(spatialConnection.getTo()) &&
                        sc.locationComp().lastLocationWas(spatialConnection.getTo()) &&
                        // Wenn man aus einem Objekt zurückkehrt, ist es keine Diskontinuität,
                        // wenn man aber aus einem Objekt gekommen ist und dann wieder in
                        // das Objekt zurückkehrt, ist das eine Diskontinuität.
                        oldLocation instanceof ISpatiallyConnectedGO;
    }

    @Contract(" -> new")
    @NonNull
    private Action buildMemorizedAction() {
        return new Action(BEWEGEN, spatialConnection.getTo());
    }
}