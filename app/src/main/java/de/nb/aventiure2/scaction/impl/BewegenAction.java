package de.nb.aventiure2.scaction.impl;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.base.SpatialConnectionData;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractDuDescription;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.base.SpatialConnection.con;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays.ONLY_WAY;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.GermanUtil.buildAufzaehlung;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.uncapitalize;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAus;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;
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

    /**
     * Hier werden "Wege" in untergeordnete Objekte (der Weg "auf den Tisch") oder aus
     * untergeordneten Objekten heraus nicht mitgezählt.
     */
    private final NumberOfWays numberOfWays;

    public static ImmutableList<AbstractScAction> buildActions(
            final AvDatabase db,
            final World world,
            @NonNull final ILocationGO location) {
        final ImmutableList.Builder<AbstractScAction> res = builder();

        if (location instanceof ISpatiallyConnectedGO) {
            res.addAll(buildSpatiallyConnectedActions(db, world,
                    (ILocationGO & ISpatiallyConnectedGO) location));
        }

        // TODO Hochklettern an Rapunzels Haaren als Bewegen-Action modellieren:
        //  "Sogleich fallen die Haare herab und du steigst hinauf"

        for (final ILocationGO inventoryGO :
                world.loadDescribableNonLivingLocationInventory(location)) {
            @Nullable final SpatialConnectionData inData =
                    inventoryGO.storingPlaceComp().getSpatialConnectionInData();
            if (inData != null) {
                res.add(new BewegenAction<>(
                        db, world, location,
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
                res.add(new BewegenAction<>(
                        db, world, location,
                        con(outerLocation.getId(), outData),
                        NumberOfWays.NO_WAY));
            }
        }

        return res.build();
    }

    private static <LOC extends ILocationGO & ISpatiallyConnectedGO>
    ImmutableList<AbstractScAction> buildSpatiallyConnectedActions(final AvDatabase db,
                                                                   final World world,
                                                                   @NonNull final LOC location) {
        final ImmutableList.Builder<AbstractScAction> res = builder();

        final List<SpatialConnection> spatialConnections =
                location.spatialConnectionComp().getConnections();

        final NumberOfWays numberOfWays = NumberOfWays.get(spatialConnections.size());

        for (final SpatialConnection spatialConnection : spatialConnections) {
            res.add(new BewegenAction<>(db, world, location,
                    spatialConnection, numberOfWays));
        }

        return res.build();
    }

    /**
     * Creates a new {@link BewegenAction}.
     */
    private BewegenAction(final AvDatabase db,
                          final World world,
                          @NonNull final ILocationGO oldLocation,
                          @NonNull final SpatialConnection spatialConnection,
                          final NumberOfWays numberOfWays) {
        super(db, world);
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
            narrateAndDoSCMitEssenKonfrontiert();
        }

        updateMood();

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
        n.add(
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
        return capitalize(
                buildAufzaehlung(descriptionsPerLocation));
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
            // STORY Im Dunkeln kann man keine Früchte sehen
            return true;
        }

        return false;
    }

    private void narrateAndDoSCMitEssenKonfrontiert() {
        final Hunger hunger = sc.feelingsComp().getHunger();
        switch (hunger) {
            case SATT:
                return;
            case HUNGRIG:
                narrateAnDoSCMitEssenKonfrontiertReagiertHungrig();
                return;
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private void narrateAnDoSCMitEssenKonfrontiertReagiertHungrig() {
        n.addAlt(
                neuerSatz("Mmh!", noTime())
                        .beendet(PARAGRAPH),
                neuerSatz("Dir läuft das Wasser im Munde zusammen", noTime()),
                du(SENTENCE, "hast", "Hunger", noTime())
                        .undWartest(),
                du(SENTENCE, "bist", "hungrig", noTime())
                        .undWartest(),
                neuerSatz("Dir fällt auf, wie hungrig du bist", noTime())
                        .komma()
        );
    }

    /**
     * Aktualisiert den Gemütszustand des Spielercharakters. "Zeit heilt alle Wunden" - oder so
     * ähnlich.
     */
    private void updateMood() {
        final Mood mood = sc.feelingsComp().getMood();
        if (oldLocation.is(SCHLOSS_VORHALLE)
                && spatialConnection.getTo().equals(DRAUSSEN_VOR_DEM_SCHLOSS)
                && mood == Mood.ANGESPANNT) {
            sc.feelingsComp().setMood(Mood.NEUTRAL);
        } else if (spatialConnection.getTo().equals(BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD) &&
                db.counterDao().get(BaumFactory.HOCHKLETTERN) > 2) {
            sc.feelingsComp().setMood(ERSCHOEPFT);
        } else if (oldLocation.is(BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD) &&
                db.counterDao().get(BaumFactory.HINABKLETTERN) != 2) {
            sc.feelingsComp().setMood(ERSCHOEPFT);
        } else if (mood == Mood.ETWAS_GEKNICKT) {
            sc.feelingsComp().setMood(Mood.NEUTRAL);
        }
    }

    private void narrateLocationOnly(@NonNull final ILocationGO to) {
        // STORY Wenn Bewegung Wiederholung ist (z.B. Rund um den Turm): Zur Sicherheit...
        //  noch einmal. Um sicher zu
        //  gehen... noch einmal. Du gehst SOGAR noch einmal...

        final AbstractDescription<?> description = getNormalDescription(
                to.storingPlaceComp().getLichtverhaeltnisse());

        if (description instanceof AbstractDuDescription &&
                n.requireNarration().allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                isDefinitivDiskontinuitaet()) {
            final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

            alt.add(satzanschluss(", besinnst dich aber und "
                            + ((AbstractDuDescription) description)
                            .getDescriptionSatzanschlussOhneSubjekt(),
                    description.getTimeElapsed())
                    .dann(description.isDann())
                    .komma(description.isKommaStehtAus()));

            alt.addAll(drueckeAus(DISKONTINUITAET, description));
            n.addAlt(alt);
            return;
        }

        if (description.getStartsNew() == WORD &&
                n.requireNarration().allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                description instanceof AbstractDuDescription) {
            n.add(description);
            return;
        }

        if (isDefinitivDiskontinuitaet()) {
            final ImmutableList.Builder<AbstractDescription<?>> alt = builder();
            if (numberOfWays == ONLY_WAY) {
                alt.add(
                        du("schaust", "dich nur kurz um, dann "
                                        + uncapitalize(description.getDescriptionHauptsatz()),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus())
                                .undWartest(
                                        description
                                                .isAllowsAdditionalDuSatzreihengliedOhneSubjekt()));
            } else {
                alt.add(neuerSatz(
                        "Was willst du hier eigentlich? "
                                + description.getDescriptionHauptsatz(),
                        description.getTimeElapsed()));
                if (description instanceof AbstractDuDescription<?, ?>) {
                    alt.add(neuerSatz(
                            "Was willst du hier eigentlich? "
                                    + ((AbstractDuDescription<?, ?>) description)
                                    .getDescriptionHauptsatzMitSpeziellemVorfeld(),
                            description.getTimeElapsed()));
                }

                alt.addAll(drueckeAus(DISKONTINUITAET, description));
            }
            n.addAlt(alt);
            return;
        }

        if (sc.memoryComp().getLastAction().is(BEWEGEN)) {
            if (n.requireNarration().getEndsThis() == StructuralElement.WORD &&
                    n.requireNarration().dann()) {
                // "Du stehst wieder vor dem Schloss; dann gehst du wieder hinein in das Schloss."
                final String satzEvtlMitDann = description
                        .getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
                                "dann");
                n.add(
                        satzanschluss(
                                "; " + uncapitalize(satzEvtlMitDann),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus())
                                .dann(description.isDann()
                                        && !satzEvtlMitDann.startsWith("Dann")));
                return;
            } else {
                n.add(description);
                return;
            }
        } else {
            if (n.requireNarration().dann()) {
                n.add(
                        neuerSatz(PARAGRAPH, description
                                        .getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("danach"),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus())
                                .undWartest(description
                                        .isAllowsAdditionalDuSatzreihengliedOhneSubjekt()));
                return;
            }

            n.add(description);
            return;
        }
    }

    private AbstractDescription<?> getNormalDescription(final Lichtverhaeltnisse
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

        final AbstractDescription<?> standardDescription =
                spatialConnection.getSCMoveDescriptionProvider()
                        .getSCMoveDescription(newLocationKnown, lichtverhaeltnisseInNewLocation);

        if (!alternativeDescriptionAllowed) {
            return standardDescription;
        }

        if (newLocationKnown == Known.KNOWN_FROM_LIGHT) {
            if (numberOfWays == ONLY_WAY) {
                if (sc.feelingsComp().hasMood(Mood.VOLLER_FREUDE) &&
                        lichtverhaeltnisseInNewLocation == HELL &&
                        n.requireNarration().allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
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
                    n.requireNarration().allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                return du("eilst", "weiter", standardDescription.getTimeElapsed().times(0.8))
                        .undWartest();
            }
        }

        return standardDescription;
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
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
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