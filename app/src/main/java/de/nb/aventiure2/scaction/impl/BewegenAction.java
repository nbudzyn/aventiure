package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.base.SpatialConnectionData;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.movement.IMovingGO;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractFlexibleDescription;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;
import de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder;
import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONLY_WAY;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.VERWUESTET;
import static de.nb.aventiure2.german.base.GermanUtil.buildAufzaehlung;
import static de.nb.aventiure2.german.base.GermanUtil.joinToString;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.AltTimedDescriptionsBuilder.altTimed;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAusTimed;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.DISKONTINUITAET;
import static de.nb.aventiure2.german.praedikat.SeinUtil.istSind;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LIEGEN;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalize;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.Objects.requireNonNull;

/**
 * Der Spielercharakter bewegt sich in einen anderen Raum oder in ein Objekt, das im
 * aktuellen Raum (oder aktuellen Objekt) enthalten ist - oder aus einem solchen
 * Objekt heraus.
 */
public class BewegenAction<LOC_DESC extends ILocatableGO & IDescribableGO>
        extends AbstractScAction {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        GEHEN_BEI_STURM
    }

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

        if (location instanceof ISpatiallyConnectedGO) {
            res.addAll(buildSpatiallyConnectedActions(scActionStepCountDao, timeTaker, counterDao,
                    n, world, (ILocationGO & ISpatiallyConnectedGO) location));
        }

        res.addAll(buildInActions(scActionStepCountDao, timeTaker, counterDao, n, world, location));

        final BewegenAction<?> outAction =
                buildOutAction(scActionStepCountDao, timeTaker, counterDao, n, world, location);
        if (outAction != null) {
            res.add(outAction);
        }

        return res.build();
    }

    private static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<BewegenAction<LOC_DESC>> buildInActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker, final CounterDao counterDao, final Narrator n,
            final World world,
            @NonNull final ILocationGO location) {
        final ImmutableList.Builder<BewegenAction<LOC_DESC>> res = builder();

        for (final ILocationGO inventoryGO :
                world.loadDescribableNonLivingLocationInventory(location)) {
            @Nullable final SpatialConnectionData inData =
                    inventoryGO.storingPlaceComp().getSpatialConnectionInData();
            if (inData != null && inData.getActionName() != null) {
                res.add(new BewegenAction<>(
                        scActionStepCountDao, timeTaker,
                        counterDao, n, world, location,
                        con(inventoryGO.getId(), inData),
                        NumberOfWays.NO_WAY));
            }
        }

        return res.build();
    }

    private static <LOC_DESC extends ILocatableGO & IDescribableGO>
    BewegenAction<LOC_DESC> buildOutAction(final SCActionStepCountDao scActionStepCountDao,
                                           final TimeTaker timeTaker, final CounterDao counterDao,
                                           final Narrator n,
                                           final World world,
                                           @NonNull final ILocationGO location) {
        @Nullable final SpatialConnectionData outData =
                location.storingPlaceComp().getSpatialConnectionOutData();
        if (outData != null && outData.getActionName() != null
                && location instanceof ILocatableGO) {
            @Nullable final ILocationGO outerLocation =
                    ((ILocatableGO) location).locationComp().getLocation();
            if (outerLocation != null) {
                return new BewegenAction<>(scActionStepCountDao, timeTaker,
                        counterDao, n, world, location,
                        con(outerLocation.getId(), outData),
                        NumberOfWays.NO_WAY);
            }
        }

        return null;
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
            if (spatialConnection.getActionName() != null) {
                res.add(new BewegenAction<>(scActionStepCountDao, nowDao,
                        counterDao, n, world, location,
                        spatialConnection, numberOfWays));
            }
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

    /**
     * Die Himmelsrichtung der Bewegung.
     */
    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return spatialConnection.getCardinalDirection();
    }

    @Override
    @NonNull
    public String getName() {
        return requireNonNull(spatialConnection.getActionName());
    }

    @Override
    public void narrateAndDo() {
        narrateLocationOnly(loadTo());

        world.narrateAndUpgradeScKnownAndAssumedState(spatialConnection.getTo());

        // Die nicht-movable Objekte sollten in der Location-beschreibung
        // alle enthalten gewesen sein (mindestens implizit), auch rekursiv.
        // Sie müssen auf Known gesetzt werden, damit gleich etwas wie
        // "Auf dem Tisch liegt eine Kugel" geschrieben werden kann.
        // Dabei gehen wir mal davon aus, dass alle diese nicht movablen-
        // Objekte auch bei Dunkelheit erkennbar sind - andererseits
        // hätten wir zumindest bei den nicht-movablen Locations das Problem,
        // dass man z.B. den Tisch bei Dunkelheit nicht sieht, aber
        // trotzdem die Kugel, die drauf liegt?!

        // Lebende Dinge, sind hier ausgeschlossen, sie müssen sich ggf. in einer
        // ReactionsComp selbst beschreiben.
        upgradeNonLivingNonMovableVisiblyRecursiveInventoryKnownMentalModel(loadTo());

        if (scWirdMitEssenKonfrontiert()) {
            sc.feelingsComp().narrateAndDoSCMitEssenKonfrontiert();
        } else if (scWirdMitSchlafgelegenheitKonfrontiert()) {
            sc.feelingsComp().narrateAndDoSCMitSchlafgelegenheitKonfrontiert();
        }

        narrateAndUpdateFeelings();

        sc.locationComp().narrateAndSetLocation(spatialConnection.getTo(),
                this::narrateNonLivingMovableObjectsOrMissingObjectsAndUpgradeKnownAndSetLastAction);
    }

    private void upgradeNonLivingNonMovableVisiblyRecursiveInventoryKnownMentalModel(
            @NonNull final ILocationGO location) {
        final ImmutableList<LOC_DESC> directlyContainedNonLivingNonMovables =
                world.loadDescribableNonLivingNonMovableInventory(location.getId());

        world.narrateAndUpgradeScKnownAndAssumedState(directlyContainedNonLivingNonMovables);
        sc.mentalModelComp().setAssumedLocations(
                directlyContainedNonLivingNonMovables, location);

        for (final IGameObject directlyContainedNonLivingNonMovable :
                directlyContainedNonLivingNonMovables) {
            if (directlyContainedNonLivingNonMovable instanceof ILocationGO
                    && ((ILocationGO) directlyContainedNonLivingNonMovable).storingPlaceComp()
                    .manKannHineinsehenUndLichtScheintHineinUndHinaus()) {
                upgradeNonLivingNonMovableVisiblyRecursiveInventoryKnownMentalModel(
                        (ILocationGO) directlyContainedNonLivingNonMovable);
            }
        }
    }

    private void narrateNonLivingMovableObjectsOrMissingObjectsAndUpgradeKnownAndSetLastAction() {
        // Unbewegliche Objekte sollen bereits in der Location-Beschreibung mitgenannt werden,
        // nicht hier! (Das betrifft auch indirekt enthaltene unbewegliche Objekte.)

        final GameObjectId toId = spatialConnection.getTo();
        final GameObject to = world.load(toId);

        final boolean toIsEqualOrInsideOldLocation =
                isOrHasRecursiveLocation(to, oldLocation);

        final boolean inSublocationInDieManNichtHineinsehenKonnte =
                toIsEqualOrInsideOldLocation
                        && !oldLocation.is(to)
                        && (
                        !(to instanceof ILocationGO)
                                || !((ILocationGO) to).storingPlaceComp()
                                .manKannHineinsehenUndLichtScheintHineinUndHinaus());
        if (// Wenn man z.B. in einem Zimmer auf einen Tisch steigt: Nicht noch einmal
            // beschreiben, was sonst noch auf dem Tisch steht!
                !toIsEqualOrInsideOldLocation
                        // Ausnahme: Man kriecht unter das Bett, unter das man bisher nicht hat
                        //  sehen können:
                        || inSublocationInDieManNichtHineinsehenKonnte
        ) {
            narrateNonLivingMovableObjectsAndUpgradeKnownMentalModel(
                    // Wenn man z.B. von einem Tisch heruntersteigt oder
                    // einmal um einen Turm herumgeht, dann noch noch einmal
                    // beschreiben, was sich auf dem Tisch oder vor dem Turm
                    // befindet
                    inSublocationInDieManNichtHineinsehenKonnte ? null : oldLocation);
            narrateAndDoMissingObjects(toId);
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private void narrateNonLivingMovableObjectsAndUpgradeKnownMentalModel(
            @Nullable final ILocationGO excludedLocation) {
        final ImmutableList.Builder<String> descriptionsPerLocation = builder();
        int numMovableObjectsInLocation = 0;
        @Nullable IDescribableGO lastObjectInLocation = null;
        for (final Pair<ILocationGO, ? extends List<? extends LOC_DESC>> locationAndDescribables :
                buildVisibleRecursiveLocationsAndDescribables(loadTo())) {
            requireNonNull(locationAndDescribables.second, "locationAndDescribables.second");

            if (excludedLocation == null ||
                    !isOrHasRecursiveLocation(
                            locationAndDescribables.first, excludedLocation)) {
                descriptionsPerLocation.add(
                        //  "auf dem Boden liegen A und B"
                        buildObjectsInLocationDescription(locationAndDescribables));

                numMovableObjectsInLocation += locationAndDescribables.second.size();
                lastObjectInLocation = locationAndDescribables.second
                        .get(locationAndDescribables.second.size() - 1);

                upgradeKnownMentalModel(locationAndDescribables);
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
                neuerSatz(objectsDescription)
                        .timed(secs(numObjects * 2))
                        .phorikKandidat(
                                world.getDescription(lastObject, false),
                                lastObject.getId()));
    }

    /**
     * Erzeugt eine Beschreibung wie "Die magere Frau ist nicht mehr da"
     */
    private void narrateAndDoMissingObjects(final GameObjectId locationId) {
        narrateAndDoMissingObjects(getMissingObjects(locationId));
    }

    private ImmutableList<LOC_DESC> getMissingObjects(final ILocationGO location) {
        return getMissingObjects(location.getId());
    }

    private ImmutableList<LOC_DESC> getMissingObjects(final GameObjectId locationId) {
        final ImmutableList<LOC_DESC> expectedDescribableInventory =
                loadAssumedDescribableVisiblyRecursiveInventory(sc, locationId);

        final ImmutableList<LOC_DESC> actualVisibleDescribableInventory =
                world.loadDescribableVisiblyRecursiveInventory(locationId);

        final ImmutableList.Builder<LOC_DESC> missing = ImmutableList.builder();

        for (final LOC_DESC expected : expectedDescribableInventory) {
            if (!actualVisibleDescribableInventory.contains(expected)
                    // Der Frosch oder die Schlosswache ist auch missing, wenn
                    //  der SC sie erwartet hätte, aber nicht bemerkt.
                    || (
                    expected instanceof ILivingBeingGO
                            && !IMovementReactions.scBemerkt(expected))) {
                missing.add(expected);
            }
        }

        // Man muss zusätzlich noch alle Gegenstände prüfen, die
        // sich im actual inventory befinden und nicht erwartet waren!
        // (Dies ist eher zur Sicherheit, vgl.
        // upgradeNonLivingNonMovableVisiblyRecursiveInventoryKnownMentalModel())
        for (final IDescribableGO actual : actualVisibleDescribableInventory) {
            // Den Frosch oder die Schlosswache bemerkt der SC vielleicht gar nicht.
            if ((!(actual instanceof ILivingBeingGO)
                    || IMovementReactions.scBemerkt((IDescribableGO & ILivingBeingGO) actual))
                    && !expectedDescribableInventory.contains(actual)) {
                // Actual ("der Käfig") war nicht erwartet!
                if (actual instanceof ILocationGO
                        && ((ILocationGO) actual).storingPlaceComp()
                        .manKannHineinsehenUndLichtScheintHineinUndHinaus()) {
                    // Aber wenn schon "der Käfig" da ist, dann wäre vielleicht
                    // auch "der Vogel" erwartet, den der SC zuletzt im Käfig
                    // gesehen hat!
                    missing.addAll(getMissingObjects((ILocationGO) actual));
                }
            }
        }

        return missing.build();
    }

    private void narrateAndDoMissingObjects(final List<LOC_DESC> missingObjects) {
        if (missingObjects.isEmpty()) {
            return;
        }

        n.narrateAlt(altMissingObjectsDescriptions(missingObjects), NO_TIME);

        sc.mentalModelComp().unsetAssumedLocations(missingObjects);
    }

    private AltDescriptionsBuilder altMissingObjectsDescriptions(
            final List<? extends IDescribableGO> missingObjects) {
        final SubstantivischePhrase aufzaehlung =
                world.getDescriptionSingleOrReihung(missingObjects);
        final String istSind = istSind(missingObjects);

        final AltDescriptionsBuilder alt = alt();
        alt.add(neuerSatz(aufzaehlung.nomK(), istSind, "nicht mehr da"),
                neuerSatz(aufzaehlung.nomK(), istSind, "nirgendwo zu sehen"),
                neuerSatz(aufzaehlung.nomK(), istSind, "verschwunden"),
                neuerSatz("wo", istSind, "denn", aufzaehlung.nomK(), "geblieben?"),
                neuerSatz("wo", istSind, "denn", aufzaehlung.nomK(), "abgeblieben?"),
                du("kannst", aufzaehlung.akkK(), "nirgendwo entdecken")
                        .schonLaenger()
                ,
                neuerSatz("von", aufzaehlung.datK(), "keine Spur!")
        );

        if (missingObjects.size() == 2) {
            alt.add(neuerSatz("weder",
                    world.getDescription(missingObjects.get(0)).nomK(),
                    "noch",
                    world.getDescription(missingObjects.get(1)).nomK(),
                    "ist irgendwo zu sehen"));
        }

        return alt;
    }

    private void upgradeKnownMentalModel(
            @NonNull
            final Pair<ILocationGO, ? extends List<? extends LOC_DESC>> locationAndDescribables) {
        requireNonNull(locationAndDescribables.second, "locationAndDescribables.second");

        world.narrateAndUpgradeScKnownAndAssumedState(locationAndDescribables.second);
        sc.mentalModelComp().setAssumedLocations(
                locationAndDescribables.second, locationAndDescribables.first);

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
                upgradeNonLivingNonMovableVisiblyRecursiveInventoryKnownMentalModel(
                        (ILocationGO) gameObject);
            }
        }
    }

    @NonNull
    private ILocationGO loadTo() {
        return world.load(spatialConnection.getTo());
    }

    @NonNull
    private String buildObjectsInLocationDescription(
            @NonNull
            final Pair<ILocationGO, ? extends List<? extends IDescribableGO>> locationAndDescribables) {
        requireNonNull(locationAndDescribables.second, "locationAndDescribables.second");

        return buildObjectsInLocationDescription(
                requireNonNull(locationAndDescribables.first),
                locationAndDescribables.second);
    }

    @NonNull
    private static String buildMovableObjectsInLocationDescription(
            final ImmutableList<String> descriptionsPerLocation) {
        return capitalize(buildAufzaehlung(descriptionsPerLocation));
    }

    private ImmutableList<Pair<ILocationGO, ? extends List<LOC_DESC>>> buildVisibleRecursiveLocationsAndDescribables(
            @NonNull final ILocationGO location) {
        final ImmutableList.Builder<Pair<ILocationGO, ? extends List<LOC_DESC>>> res = builder();

        final ImmutableList<LOC_DESC> movableObjectsInLocation =
                world.loadDescribableNonLivingMovableInventory(location.getId());

        if (!movableObjectsInLocation.isEmpty()) {
            res.add(new Pair<ILocationGO, List<LOC_DESC>>(
                    location, movableObjectsInLocation));
        }

        for (final ILocationGO directContainedLocation :
                world.loadDescribableNonLivingLocationInventory(location)) {
            if (directContainedLocation.storingPlaceComp()
                    .manKannHineinsehenUndLichtScheintHineinUndHinaus()) {
                res.addAll(buildVisibleRecursiveLocationsAndDescribables(directContainedLocation));
            }
        }

        return res.build();
    }

    @NonNull
    private String buildObjectsInLocationDescription(
            final ILocationGO location,
            @NonNull final List<? extends IDescribableGO> movableObjectsInLocation) {
        final SubstantivischePhrase descriptionSingleOrReihung =
                world.getDescriptionSingleOrReihung(movableObjectsInLocation);
        return buildObjectInLocationDescriptionPrefix(location,
                descriptionSingleOrReihung)
                + " "
                + joinToString(
                descriptionSingleOrReihung.nomK());
    }

    @SuppressWarnings({"RedundantIfStatement"})
    private boolean scWirdMitEssenKonfrontiert() {
        final GameObject newLocation = world.load(spatialConnection.getTo());

        if (world.<IHasStateGO<SchlossfestState>>load(SCHLOSSFEST).stateComp()
                .hasState(BEGONNEN, VERWUESTET)) {
            if (oldLocation.is(DRAUSSEN_VOR_DEM_SCHLOSS)
                    && newLocation.is(SCHLOSS_VORHALLE)) {
                return true;
            }
            if (newLocation.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
                return true;
            }
        }

        if (newLocation.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("RedundantIfStatement")
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
        }

        if (spatialConnection.getTo().equals(BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD) &&
                counterDao.get(BaumFactory.Counter.HOCHKLETTERN) > 2) {
            sc.feelingsComp().narrateAndUpgradeTemporaereMinimalmuedigkeit(
                    FeelingIntensity.NUR_LEICHT, mins(15)
            );
        }

        if (oldLocation.is(BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD) &&
                counterDao.get(BaumFactory.Counter.HINABKLETTERN) != 2) {
            sc.feelingsComp().narrateAndUpgradeTemporaereMinimalmuedigkeit(
                    FeelingIntensity.MERKLICH, mins(45));
        }

        if (loadWetter().wetterComp().getLokaleWindstaerke(loadTo())
                .compareTo(Windstaerke.STURM) >= 0) {
            if (counterDao.incAndGet(Counter.GEHEN_BEI_STURM) % 3 == 0) {
                sc.feelingsComp().narrateAndUpgradeTemporaereMinimalmuedigkeit(
                        FeelingIntensity.MERKLICH, mins(90));
            }
        }

        if (sc.feelingsComp().hasMood(Mood.ETWAS_GEKNICKT)) {
            sc.feelingsComp().requestMood(Mood.NEUTRAL);
        }
    }

    private void narrateLocationOnly(@NonNull final ILocationGO to) {
        // STORY Wenn Bewegung Wiederholung ist (z.B. Rund um den Turm): Zur Sicherheit...
        //  noch einmal. Um sicher zu
        //  gehen... noch einmal. Du gehst SOGAR noch einmal...

        final Collection<TimedDescription<?>> timedDescriptions = altNormalDescriptions(
                to.storingPlaceComp().getLichtverhaeltnisse());

        @Nullable final IMovingGO wemDerSCFolgt = getWemDerSCFolgt(to);
        if (wemDerSCFolgt != null) {
            wemDerSCFolgt.movementComp().narrateScFolgtMovingGO(timedDescriptions);
            return;
        }

        final AltTimedDescriptionsBuilder alt = altTimed();

        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            if (isDefinitivDiskontinuitaet()) {
                alt.addAll(timedDescriptions.stream()
                        .filter(td -> td.getDescription() instanceof AbstractFlexibleDescription
                                && ((AbstractFlexibleDescription<?>) td.getDescription())
                                .hasSubjektDu()
                                && isDefinitivDiskontinuitaet())
                        .flatMap(td ->
                                toDiskontinuitaetDuSatzanschluss(td,
                                        (AbstractFlexibleDescription<?>) td.getDescription())
                                        .stream()));
            }

            alt.addAllIfOtherwiseEmtpy(timedDescriptions.stream()
                    .filter(td -> td.getStartsNew() == WORD
                            && td.getDescription() instanceof AbstractFlexibleDescription));
        }

        if (isDefinitivDiskontinuitaet()) {
            alt.addAllIfOtherwiseEmtpy(timedDescriptions.stream()
                    .flatMap(td -> toDiskontinuitaet(td).stream()));
        }

        alt.addAllIfOtherwiseEmtpy(timedDescriptions.stream().map(this::tweakForLastActionBewegen));

        n.narrateAlt(alt);
    }

    @Nullable
    private IMovingGO getWemDerSCFolgt(final ILocationGO to) {
        @Nullable final IMovingGO hatDenSCGeradeInDieRichtungVerlassen =
                world.loadWerDenSCGeradeVerlassenHat(to.getId());
        if (hatDenSCGeradeInDieRichtungVerlassen == null
                || !sc.memoryComp().isKnown(hatDenSCGeradeInDieRichtungVerlassen)) {
            return null;
        }

        return hatDenSCGeradeInDieRichtungVerlassen;
    }

    private static ImmutableList<TimedDescription<? extends AbstractDescription<?>>>
    toDiskontinuitaetDuSatzanschluss(
            final TimedDescription<?> description,
            final AbstractFlexibleDescription<?> fDesc) {
        final TextDescription descriptionSatzanschlussOhneSubjekt =
                fDesc.toTextDescriptionSatzanschlussOhneSubjektOhneAnschlusswort();

        final AltTimedDescriptionsBuilder alt = altTimed();
        alt.add(description.withDescription(
                descriptionSatzanschlussOhneSubjekt.mitPraefix(
                        joinToKonstituentenfolge(
                                ", besinnst dich aber ",
                                fDesc.toTextDescriptionSatzanschlussMitAnschlusswortOderVorkomma()))));
        alt.addAll(drueckeAusTimed(DISKONTINUITAET, description));
        return alt.build();
    }

    private ImmutableList<TimedDescription<? extends AbstractDescription<?>>>
    toDiskontinuitaet(final TimedDescription<?> timedDescription) {
        final AltTimedDescriptionsBuilder alt = altTimed();

        if (numberOfWays == ONLY_WAY
                && timedDescription.getDescription() instanceof AbstractFlexibleDescription
                && ((AbstractFlexibleDescription<?>) timedDescription.getDescription())
                .hasSubjektDu()) {

            alt.add(timedDescription.withDescription(
                    ((AbstractFlexibleDescription<?>) timedDescription.getDescription())
                            .toTextDescriptionMitVorfeld("dann")
                            .mitPraefix(joinToKonstituentenfolge(
                                    SENTENCE,
                                    "Du siehst dich nur kurz um"))));
        } else {
            alt.addAll(timedDescription.altMitPraefix(joinToKonstituentenfolge(
                    PARAGRAPH,
                    "Was willst du hier eigentlich?",
                    SENTENCE)));
            alt.addAll(drueckeAusTimed(DISKONTINUITAET, timedDescription));
        }
        return alt.build();
    }

    private TimedDescription<?>
    tweakForLastActionBewegen(final TimedDescription<?> timedDescription) {
        if (sc.memoryComp().getLastAction().is(BEWEGEN)) {
            if (n.endsThisIsExactly(StructuralElement.WORD) && n.dann()
                    && !timedDescription.isSchonLaenger()) {
                // "Du stehst wieder vor dem Schloss. Dann gehst du wieder hinein in das Schloss."
                final TextDescription satzEvtlMitDann = timedDescription.getDescription()
                        .toTextDescriptionMitKonjunktionaladverbWennNoetig("dann")
                        .beginntZumindest(SENTENCE);
                if (satzEvtlMitDann.getTextOhneKontext().startsWith("Dann")) {
                    satzEvtlMitDann.dann(false);
                }
                return timedDescription.withDescription(satzEvtlMitDann);
            }

            return timedDescription;
        }

        if (n.dann()) {
            return timedDescription.withDescription(
                    timedDescription.getDescription()
                            .toTextDescriptionMitKonjunktionaladverbWennNoetig(
                                    "danach").beginntZumindest(PARAGRAPH));
        }

        return timedDescription;
    }

    private ImmutableCollection<TimedDescription<?>> altNormalDescriptions(
            final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
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

        final ImmutableCollection<TimedDescription<?>> standardDescriptions =
                altStandardDescriptions(newLocationKnown, lichtverhaeltnisseInNewLocation);

        if (!alternativeDescriptionAllowed
                || oldLocation.is(spatialConnection.getTo())
                // Wenn bei jeder Alternative ein Counter hochgezählt werden soll,
                // wird es sinnvoll sein, die standardDescription anzuzeigen!
                || standardDescriptions.stream()
                .allMatch(td -> td.getCounterIdIncrementedIfTextIsNarrated() != null)) {
            return standardDescriptions;
        }

        final AvTimeSpan someTimeElapsed = standardDescriptions.stream()
                .map(TimedDescription::getTimeElapsed)
                .findAny()
                .orElse(NO_TIME);

        if (newLocationKnown == Known.KNOWN_FROM_LIGHT) {
            if (numberOfWays == ONLY_WAY) {
                if (sc.feelingsComp().hasMood(Mood.VOLLER_FREUDE) &&
                        lichtverhaeltnisseInNewLocation == HELL &&
                        n.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                        sc.memoryComp().getLastAction().is(Action.Type.NEHMEN)) {
                    return ImmutableList.of(
                            du("springst",
                                    "damit fort").mitVorfeldSatzglied("damit")
                                    .timed(someTimeElapsed.times(0.8))
                                    .undWartest()
                                    .dann());
                }

                if (sc.feelingsComp().hasMood(Mood.UNTROESTLICH)) {
                    return ImmutableList.of(
                            du("trottest", "tieftraurig von dannen")
                                    .mitVorfeldSatzglied("tieftraurig")
                                    .timed(someTimeElapsed.times(2))
                                    .undWartest());
                }
            } else if (numberOfWays == ONE_IN_ONE_OUT
                    && sc.memoryComp().getLastAction().is(BEWEGEN) &&
                    !sc.locationComp().lastLocationWas(spatialConnection.getTo()) &&
                    sc.feelingsComp().hasMood(Mood.VOLLER_FREUDE) &&
                    lichtverhaeltnisseInNewLocation ==
                            HELL &&
                    n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                return ImmutableList.of(
                        du("eilst", "weiter")
                                .timed(someTimeElapsed.times(0.8))
                                .undWartest());
            }
        }

        return standardDescriptions;
    }

    @VisibleForTesting
    ImmutableCollection<TimedDescription<?>>
    altStandardDescriptions(final Known newLocationKnown,
                            final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
        return mapToList(requireNonNull(spatialConnection.getScMoveAltTimedDescriptionProvider())
                        .altScMoveTimedDescriptions(newLocationKnown,
                                lichtverhaeltnisseInNewLocation),
                td -> td.multiplyTimeElapsedWith(calcSpeedFactor()));
    }

    private double calcSpeedFactor() {
        return world.loadSC().feelingsComp().getMovementSpeedFactor()
                * loadWetter().wetterComp().getMovementSpeedFactor(oldLocation, loadTo());
    }

    @NonNull
    private static String buildObjectInLocationDescriptionPrefix(
            @NonNull final ILocationGO location,
            final SubstantivischePhrase descriptionSingleOrReihung) {
        final String wo = location.storingPlaceComp().getLocationMode().getWo(false);
        return wo + " " + LIEGEN.getPraesensOhnePartikel(descriptionSingleOrReihung);
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