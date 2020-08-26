package de.nb.aventiure2.data.world.gameobject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakterFactory;
import de.nb.aventiure2.data.world.syscomp.alive.AliveSystem;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.location.RoomFactory;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.DraussenVorDemSchlossConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ImWaldNaheDemSchlossConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SchlossVorhalleConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SimpleConnectionCompFactory;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.VorDemTurmConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.german.base.Nominalphrase;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.EINE_TASCHE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.HAENDE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.TISCH;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;

/**
 * The world contains and manages all game objects.
 */
public class World {
    public static final AvDateTime SCHLOSSFEST_BEGINN_DATE_TIME =
            new AvDateTime(2,
                    oClock(5, 30));

    public static final String COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN =
            "Invisibles_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN";

    // SPIELER-CHARAKTER
    public static final GameObjectId SPIELER_CHARAKTER = new GameObjectId(1);

    // OBJECTS
    // - On Player
    public static final GameObjectId HAENDE_DES_SPIELER_CHARAKTERS = new GameObjectId(10_000);
    public static final GameObjectId EINE_TASCHE_DES_SPIELER_CHARAKTERS = new GameObjectId(10_001);

    // - Non-Movable
    public static final GameObjectId SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST =
            new GameObjectId(10_100);
    public static final GameObjectId SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST =
            new GameObjectId(10_101);
    public static final GameObjectId VOR_DEM_ALTEN_TURM_SCHATTEN_DER_BAEUME =
            // (in deren Schatten man sich setzen kann)
            new GameObjectId(10_110);
    public static final GameObjectId BETT_IN_DER_HUETTE_IM_WALD = new GameObjectId(10_120);
    public static final GameObjectId BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD =
            new GameObjectId(10_130);

    // - Movable
    public static final GameObjectId GOLDENE_KUGEL = new GameObjectId(11_000);


    // CREATURES
    public static final GameObjectId SCHLOSSWACHE = new GameObjectId(20_000);
    public static final GameObjectId FROSCHPRINZ = new GameObjectId(20_001);
    public static final GameObjectId RAPUNZEL = new GameObjectId(20_002);
    public static final GameObjectId RAPUNZELS_ZAUBERIN = new GameObjectId(20_003);

    // RÄUME
    public static final GameObjectId SCHLOSS_VORHALLE = new GameObjectId(30_000);
    public static final GameObjectId DRAUSSEN_VOR_DEM_SCHLOSS = new GameObjectId(30_002);
    public static final GameObjectId IM_WALD_NAHE_DEM_SCHLOSS = new GameObjectId(30_003);
    public static final GameObjectId VOR_DEM_ALTEN_TURM = new GameObjectId(30_004);
    public static final GameObjectId OBEN_IM_ALTEN_TURM = new GameObjectId(30_005);
    public static final GameObjectId ABZWEIG_IM_WALD = new GameObjectId(30_010);
    public static final GameObjectId VOR_DER_HUETTE_IM_WALD = new GameObjectId(30_011);
    public static final GameObjectId HUETTE_IM_WALD = new GameObjectId(30_012);
    public static final GameObjectId HINTER_DER_HUETTE = new GameObjectId(30_014);
    public static final GameObjectId IM_WALD_BEIM_BRUNNEN = new GameObjectId(30_015);
    public static final GameObjectId UNTEN_IM_BRUNNEN = new GameObjectId(30_016);
    public static final GameObjectId WALDWILDNIS_HINTER_DEM_BRUNNEN = new GameObjectId(30_017);

    // INVISIBLES
    public static final GameObjectId TAGESZEIT = new GameObjectId(40_001);
    public static final GameObjectId SCHLOSSFEST = new GameObjectId(40_000);

    // MEANING
    public static final GameObjectId STORY_WEB = new GameObjectId(100_000);

    // Sonstige Konstanten
    private static final boolean SCHLOSS_VORHALLE_DAUERHAFT_BELEUCHTET = true;

    private static volatile World INSTANCE;

    private final AvDatabase db;

    private GameObjectIdMap all;

    private GOReactionsCoordinator reactionsCoordinator;

    // SYSTEMS
    private AliveSystem aliveSystem;

    private SpatialConnectionSystem spatialConnectionSystem;

    private LocationSystem locationSystem;

    public static World getInstance(final AvDatabase db) {
        if (INSTANCE == null) {
            synchronized (World.class) {
                if (INSTANCE == null) {
                    INSTANCE = new World(db);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    @WorkerThread
    public static void reset() {
        INSTANCE = null;
    }

    private World(final AvDatabase db) {
        this.db = db;
    }

    /**
     * Füllt die interne Liste aller Game Objects (dabei
     * werden die Daten der Objekte <i>noch nicht</i> aus der Datenbank geladen),
     * startet außerdem alle <i>Systems</i> (sofern noch nicht geschehen).
     */
    private void prepare() {
        if (all == null) {
            if (aliveSystem == null) {
                aliveSystem = new AliveSystem(db);
            }

            if (locationSystem == null) {
                locationSystem = new LocationSystem(db);
            }

            if (spatialConnectionSystem == null) {
                spatialConnectionSystem = new SpatialConnectionSystem(this);
            }

            if (reactionsCoordinator == null) {
                reactionsCoordinator = new GOReactionsCoordinator(this, db.narrationDao());
            }

            final SpielerCharakterFactory spieler = new SpielerCharakterFactory(db, this);
            final GeneralObjectFactory object = new GeneralObjectFactory(db, this);
            final BankAmTischBeimSchlossfestFactory bankAmTischBeimSchlossfest =
                    new BankAmTischBeimSchlossfestFactory(db, this);
            final SchattenDerBaeumeFactory schattenDerBaeume =
                    new SchattenDerBaeumeFactory(db, this);
            final BettFactory bett = new BettFactory(db, this);
            final BaumFactory baum = new BaumFactory(db, this);
            final CreatureFactory creature = new CreatureFactory(db, this);
            final InvisibleFactory invisible = new InvisibleFactory(db, this);
            final MeaningFactory meaning = new MeaningFactory(db, this);
            final RoomFactory room = new RoomFactory(db, this);
            final SimpleConnectionCompFactory connection =
                    new SimpleConnectionCompFactory(db, this);

            all = new GameObjectIdMap();
            all.putAll(
                    spieler.create(SPIELER_CHARAKTER),
                    room.create(SCHLOSS_VORHALLE, StoringPlaceType.EIN_TISCH,
                            SCHLOSS_VORHALLE_DAUERHAFT_BELEUCHTET,
                            new SchlossVorhalleConnectionComp(db, this)),
                    room.create(DRAUSSEN_VOR_DEM_SCHLOSS,
                            StoringPlaceType.BODEN_VOR_DEM_SCHLOSS,
                            false,
                            new DraussenVorDemSchlossConnectionComp(db, this)),
                    room.create(IM_WALD_NAHE_DEM_SCHLOSS, StoringPlaceType.WEG,
                            false,
                            new ImWaldNaheDemSchlossConnectionComp(db, this)),
                    room.create(VOR_DEM_ALTEN_TURM, StoringPlaceType.STEINIGER_GRUND_VOR_TURM,
                            false,
                            new VorDemTurmConnectionComp(db, this)),
                    room.create(OBEN_IM_ALTEN_TURM,
                            StoringPlaceType.TEPPICHBODEN_OBEN_IM_TURM,
                            false,
                            connection.createNoConnections(OBEN_IM_ALTEN_TURM)),
                    room.create(ABZWEIG_IM_WALD, StoringPlaceType.WEG,
                            false,
                            connection.createAbzweigImWald()),
                    room.create(VOR_DER_HUETTE_IM_WALD, StoringPlaceType.ERDBODEN_VOR_DER_HUETTE,
                            false,
                            connection.createVorDerHuetteImWald()),
                    room.create(HUETTE_IM_WALD, StoringPlaceType.HOLZTISCH,
                            false,
                            connection.createHuetteImWald()),
                    room.create(HINTER_DER_HUETTE, StoringPlaceType.UNTER_DEM_BAUM,
                            false,
                            connection.createHinterDerHuette()),
                    room.createImWaldBeimBrunnen(),
                    room.create(UNTEN_IM_BRUNNEN, StoringPlaceType.AM_GRUNDE_DES_BRUNNENS,
                            false,
                            connection.createNoConnections(UNTEN_IM_BRUNNEN)),
                    room.create(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                            StoringPlaceType.MATSCHIGER_WALDBODEN,
                            false,
                            connection.createWaldwildnisHinterDemBrunnen()),
                    creature.createSchlosswache(),
                    creature.createFroschprinz(),
                    creature.createRapunzel(),
                    creature.createRapunzelsZauberin(),
                    // STORY: Ein Wolf könnte nachts "unsichtbar" zufällig durch den
                    //  Wald laufen (MovementComp) und immer mal heulen oder rascheln, wenn er direkt beim SC
                    //  vorbeiläuft (onReachesCenter() o.Ä.).

                    // STORY Wölfe (Creatures? Invisibles?) hetzen Spieler nachts
                    //  Es könnte z.B. Räume neben dem Weg geben, die der Spieler in aller Regel
                    //  nicht betreten, kann, wo aber die Wölfe laufen.
                    //  Es könnte einen sicheren Platz geben - z.B. wäre der Weg sicher
                    //  oder die Hütte.

                    meaning.createStoryWeb(),

                    invisible.createSchlossfest(),
                    invisible.createTageszeit(),
                    object.create(EINE_TASCHE_DES_SPIELER_CHARAKTERS,
                            np(F, "eine Tasche", "einer Tasche"),
                            SPIELER_CHARAKTER, null,
                            false, // Man kann nicht "eine Tasche hinlegen" o.Ä.
                            EINE_TASCHE,
                            false),
                    object.create(HAENDE_DES_SPIELER_CHARAKTERS,
                            np(PL_MFN, "die Hände", "den Händen"),
                            SPIELER_CHARAKTER, null,
                            false,
                            HAENDE,
                            false),
                    object.create(GOLDENE_KUGEL,
                            np(F, "eine goldene Kugel",
                                    "einer goldenen Kugel"),
                            np(F, "die goldene Kugel", "der goldenen Kugel"),
                            np(F, "die Kugel", "der Kugel"),
                            SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS,
                            true),
                    // STORY Die goldene Kugel kann verloren gehen, zum Beispiel wenn man sie im
                    //  Sumpf ablegt. Dann gibt es eine art Reset und eine ähnliche goldene
                    //  Kugel erscheint wieder im Schloss. Der Text dort sagt so dann etwas wie
                    //  "eine goldene kugel wie du sie schon einmal gesehen hast, nur etwas
                    //  kleiner".
                    // STORY Wenn man die goldene Kugel auf den Weg legt oder beim Schlossfest
                    //  auf den Tisch, verschwindet sie einfach, wenn man weggeht (sie wird
                    //  gestohlen) - vorausgesetzt, man braucht sie nicht mehr.
                    bankAmTischBeimSchlossfest.create(SCHLOSS_VORHALLE_DAUERHAFT_BELEUCHTET),
                    object.create(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST,
                            np(M, "ein langer, aus Brettern gezimmerter Tisch",
                                    "einem langen, aus Brettern gezimmertem Tisch",
                                    "einen langen, aus Brettern gezimmerten Tisch"),
                            np(M, "der lange Brettertisch", "dem langen Brettertisch",
                                    "den langen Brettertisch"),
                            np(M, "der Tisch", "dem Tisch", "den Tisch"),
                            // Der Tisch wird erst spontan hinzugefügt, wenn
                            // sich der Benutzer an einen Platz setzt.
                            // Ansonsten bekommen wir vorher Aktionen wie
                            // "Die Kugel auf den Tisch legen" angeboten. (Auf welchen der
                            // vielen Tisch denn??)
                            // TODO Man könnte ein Framework für diese "Instanziierung"
                            //  anbieten, wo man eine "Tisch-Instanz" betritt und
                            //  später wieder verlässt (worauf alles zurückgesetzt wird und
                            //  der SC ggf. alle relevanten Gegenstände automatisch mitnimmt.)
                            null, null,
                            false,
                            TISCH,
                            SCHLOSS_VORHALLE_DAUERHAFT_BELEUCHTET),
                    // STORY Spieler kauft Lampe (z.B. für Hütte) auf Schlossfest

                    // STORY Rapunzel: Man muss eine Strickleiter
                    //  besorgen - oder Seide kaufen und etwas zum Stricken??? Gold gabs vielleicht
                    //  vom Froschprinzen?
                    schattenDerBaeume.createVorDemAltenTurm(),
                    bett.createInDerHuetteImWald(),
                    baum.createImGartenHinterDerHuetteImWald()
            );
        }
    }

    /**
     * Speichert für alle Game Objects ihre initialen Daten in die Datenbank.
     * War die Datenbank vorher leer, ist hiermit das Spiel auf Anfang zurückgesetzt.
     */
    public void saveAllInitialState() {
        prepare();

        for (final GameObject gameObject : all.values()) {
            gameObject.saveInitialState();
        }
    }

    public void scActionDone(final AvDateTime startTimeOfUserAction) {
        prepare();

        for (final GameObject gameObject : all.values()) {
            gameObject.scActionDone(startTimeOfUserAction);
        }
    }

    public void upgradeKnownToSC(
            final IGameObject gameObject) {
        upgradeKnownToSC(gameObject.getId());
    }

    public void upgradeKnownToSC(
            final GameObjectId gameObjectId) {
        upgradeKnownToSC(gameObjectId,
                loadSC().locationComp().getLocationId());
    }

    public void upgradeKnownToSC(
            final GameObjectId gameObjectId,
            @Nullable final GameObjectId locationId) {
        upgradeKnownToSC(gameObjectId,
                locationId != null ? ((ILocationGO) load(locationId)) : null);
    }

    public void upgradeKnownToSC(
            final IGameObject gameObject,
            @Nullable final ILocationGO location) {
        upgradeKnownToSC(gameObject.getId(), location);
    }

    public void upgradeKnownToSC(
            final GameObjectId gameObjectId,
            @Nullable final ILocationGO location) {
        loadSC().memoryComp().upgradeKnown(gameObjectId, getKnown(location));
    }

    public Known getKnown(@Nullable final ILocationGO location) {
        return Known.getKnown(locationSystem.getLichtverhaeltnisse(location));
    }

    /**
     * Gibt <code>true</code> zurück falls das Game Object eine dieser Locations ist oder
     * sich (ggf. rekusiv) an einer dieser Locations befindet.
     */
    public static boolean isOrHasRecursiveLocation(
            @Nullable final IGameObject gameObject, final ILocationGO... locationAlternatives) {
        if (gameObject == null) {
            return false;
        }

        for (final ILocationGO locationAlternative : locationAlternatives) {
            if (isOrHasRecursiveLocation(gameObject, locationAlternative)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gibt <code>true</code> zurück, falls das Game Object als ID eine dieser
     * <code>locationIds</code> hat oder
     * sich (ggf. rekusiv) an einer dieser Locations befindet.
     */
    public boolean isOrHasRecursiveLocation(
            final GameObjectId gameObjectId, final GameObjectId... locationIds) {
        for (final GameObjectId locationId : locationIds) {
            if (isOrHasRecursiveLocation(load(gameObjectId), locationId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gibt <code>true</code> zurück, falls das Game Object als ID diese <code>locationId</code> hat
     * oder sich (ggf. rekusiv) an dieser Location befindet.
     */
    public boolean isOrHasRecursiveLocation(final GameObjectId gameObjectId,
                                            final GameObjectId locationId) {
        return isOrHasRecursiveLocation(load(gameObjectId), locationId);
    }

    /**
     * Gibt <code>true</code> zurück, falls das Game Object als ID diese <code>locationId</code> hat
     * oder sich (ggf. rekusiv) an dieser Location befindet.
     */
    public boolean isOrHasRecursiveLocation(@Nullable final IGameObject gameObject,
                                            final GameObjectId locationId) {
        if (gameObject == null) {
            return false;
        }

        if (gameObject.getId().equals(locationId)) {
            return true;
        }

        final GameObject location = load(locationId);
        if (!(location instanceof ILocationGO)) {
            return false;
        }

        return isOrHasRecursiveLocation(gameObject, (ILocationGO) location);
    }


    /**
     * Gibt <code>true</code> zurück, falls das Game Object als ID diese <code>locationId</code> hat
     * oder sich (ggf. rekusiv) an dieser Location befindet.
     */
    public boolean isOrHasRecursiveLocation(final GameObjectId gameObjectId,
                                            @Nullable final ILocationGO location) {
        if (location == null) {
            return false;
        }

        return isOrHasRecursiveLocation(load(gameObjectId), location);
    }

    /**
     * Gibt <code>true</code> zurück falls das Game Object diese Location ist oder
     * sich (ggf. rekusiv) an dieser Location befindet.
     */
    public static boolean isOrHasRecursiveLocation(@Nullable final IGameObject gameObject,
                                                   @Nullable final ILocationGO location) {
        if (gameObject == null) {
            return location == null;
        }

        if (location == null) {
            return false;
        }

        if (gameObject.equals(location)) {
            return true;
        }

        if (!(gameObject instanceof ILocatableGO)) {
            return false;
        }

        return ((ILocatableGO) gameObject).locationComp().hasRecursiveLocation(location);
    }


    @Contract(pure = true)
    @NonNull
    public GOReactionsCoordinator narrateAndDoReactions() {
        return reactionsCoordinator;
    }

    <R extends IReactions, G extends GameObject & IResponder>
    List<G> loadResponders(final Class<R> reactionsInterface) {
        prepare();

        final ImmutableList<GameObject> res =
                all.values().stream()
                        .filter(IResponder.class::isInstance)
                        .filter(resp -> IResponder.reactsTo(resp, reactionsInterface))
                        .collect(toImmutableList());
        loadGameObjects(res);

        return (ImmutableList<G>) (ImmutableList<?>) res;
    }

    /**
     * Lädt (soweit noch nicht geschehen) die nicht-lebenden Game Objects an dieser
     * <code>location</code> (auch rekursiv enthaltene, z.B. Kugel auf Tisch in Raum)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingRecursiveInventory(
            final ILocationGO location) {
        return AliveSystem.filterNoLivingBeing(loadDescribableRecursiveInventory(location));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * an denen etwas abgelegt werden kann an diesem Ort (auch rekursiv), lädt sie
     * (sofern noch nicht geschehen)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOCATABLE_DESC_LOCATION extends ILocatableGO & IDescribableGO & ILocationGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> loadDescribableNonLivingLocationRecursiveInventory(
            final ILocationGO inventoryHolder) {
        return loadDescribableNonLivingLocationRecursiveInventory(inventoryHolder.getId());
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * an denen etwas abgelegt werden kann an diesem Ort (auch rekursiv), lädt sie
     * (sofern noch nicht geschehen)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOCATABLE_DESC_LOCATION extends ILocatableGO & IDescribableGO & ILocationGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> loadDescribableNonLivingLocationRecursiveInventory(
            final GameObjectId inventoryHolderId) {
        return filterLocation(
                loadDescribableNonLivingRecursiveInventory(inventoryHolderId));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * an denen etwas abgelegt werden kann (<i>nicht</i>> rekursiv), lädt sie
     * (sofern noch nicht geschehen)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOCATABLE_DESC_LOCATION extends ILocatableGO & IDescribableGO & ILocationGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> loadDescribableNonLivingLocationInventory(
            final ILocationGO inventoryHolder) {
        return filterLocation(
                loadDescribableNonLivingInventory(inventoryHolder.getId()));
    }

    private static <LOC_DESC extends ILocatableGO & IDescribableGO,
            LOCATABLE_DESC_LOCATION extends ILocatableGO & IDescribableGO & ILocationGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> filterLocation(
            final List<LOC_DESC> gameObjects) {
        return (ImmutableList<LOCATABLE_DESC_LOCATION>) gameObjects.stream()
                .filter(ILocationGO.class::isInstance)
                .collect(toImmutableList());
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * die <i>movable</i> sind (also z.B. vom SC bewegt werden könnten) an dieser
     * <i>locationId</i> (auch rekursiv enthaltene, z.B. Kugel auf einem Tisch in einem Raum),
     * lädt sie (sofern noch nicht geschehen) und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingMovableRecursiveInventory(
            final GameObjectId locationId) {
        return LocationSystem.filterMovable(loadDescribableNonLivingRecursiveInventory(locationId));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * die <i>movable</i> sind (also z.B. vom SC bewegt werden könnten) an dieser
     * <i>locationId</i> (aber <i>nicht</i> rekursiv enthaltene, z.B. <i>nicht</i>
     * die Kugel auf einem Tisch in einem Raum),
     * lädt sie (sofern noch nicht geschehen) und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingMovableInventory(
            final GameObjectId locationId) {
        return LocationSystem.filterMovable(loadDescribableNonLivingInventory(locationId));
    }

    /**
     * Ermittelt die Game Objects,
     * die <i>nicht lebendig</i> und <i>nicht movable</i> sind (also <i>nicht</i> bewegt werden
     * könnten) an dieser
     * <i>locationId</i> (aber <i>nicht</i> rekursiv enthaltene, z.B. eine
     * <i>nicht</i> die schwere Vase auf einem Tisch in einem Raum),
     * lädt sie (sofern noch nicht geschehen) und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingNonMovableInventory(
            final GameObjectId locationId) {
        return filterNonLivingBeing(
                LocationSystem.filterNotMovable(
                        loadDescribableInventory(locationId)));
    }

    /**
     * Lädt (soweit noch nicht geschehen) die nicht-lebenden Game Objects von dieser
     * <i>locationId</i> zurück (auch rekursiv, z.B. Kugel auf einem Tisch in einem Raum)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingRecursiveInventory(
            final GameObjectId locationId) {
        return AliveSystem.filterNoLivingBeing(loadDescribableRecursiveInventory(locationId));
    }

    /**
     * Lädt (soweit noch nicht geschehen) die nicht-lebenden Game Objects von dieser
     * <i>locationId</i> zurück (aber <i>nicht</i> rekursiv, z.B. <i>nicht</i> die
     * Kugel auf einem Tisch in einem Raum)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingInventory(
            final GameObjectId locationId) {
        return AliveSystem.filterNoLivingBeing(loadDescribableInventory(locationId));
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle lebenden Game Objects von dieser
     * <i>locationId</i> zurück und gibt sie zurück, auch rekursiv
     * (also Kugel auf einem Tisch im Raum o.ä.) -
     * nur Gegenstände, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public <LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> loadDescribableLivingRecursiveInventory(
            final GameObjectId locationId) {
        return filterLivingBeing(loadDescribableRecursiveInventory(locationId));
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle lebenden Game Objects im Inventar dieser
     * <code>location</code>s und gibt sie zurück, auch rekursiv
     * (also Kugel auf einem Tisch im Raum o.ä.) -
     * nur Game Objects, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public <LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> loadDescribableLivingRecursiveInventory(
            final ILocationGO location) {
        return filterLivingBeing(loadDescribableRecursiveInventory(location));
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle Game Objects an dieser
     * <code>location</code>s (auch rekursiv enthaltene, z.B. Kugel auf einem Tisch in einem Raum)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableRecursiveInventory(
            final ILocationGO location) {
        return loadDescribableRecursiveInventory(location.getId());
    }

    /**
     * Lädt (soweit noch nicht geschehen) die Game Objects an dieser
     * <code>locationId</code> (auch rekursiv, z.B. Kugel auf Tisch in Raum)
     * und gibt sie zurück - nur Game Objects, die eine Beschreibung haben,
     * nicht den Spieler-Charakter.
     */
    public <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableRecursiveInventory(final GameObjectId locationId) {
        final ImmutableList<LOC_DESC> directContainedList =
                loadDescribableInventory(locationId);

        final ImmutableList.Builder<LOC_DESC> res = ImmutableList.builder();
        res.addAll(directContainedList);

        for (final LOC_DESC directContained : directContainedList) {
            if (directContained instanceof ILocationGO) {
                res.addAll(loadDescribableRecursiveInventory((ILocationGO) directContained));
            }
        }

        return res.build();
    }

    /**
     * Lädt (soweit noch nicht geschehen) die Game Objects an dieser
     * <code>location</code> (<i>nicht</i> rekursiv, also <i>nicht</i> die Kugel
     * auf einem Tisch in einem Raum, sondern nur den Tisch)
     * und gibt sie zurück - nur Game Objects, die eine Beschreibung haben,
     * nicht den Spieler-Charakter.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableInventory(final ILocationGO location) {
        return loadDescribableInventory(location.getId());
    }

    /**
     * (Speichert ggf. alle Änderungen und) lädt (soweit noch nicht geschehen) die Game Objects an dieser
     * <code>locationId</code> (<i>nicht</i> rekursiv, also <i>nicht</i> die Kugel
     * auf einem Tisch in einem Raum, sondern nur den Tisch)
     * und gibt sie zurück - nur Game Objects, die eine Beschreibung haben,
     * nicht den Spieler-Charakter.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableInventory(final GameObjectId locationId) {
        prepare();
        saveAll(false);

        final ImmutableList<GameObject> res =
                locationSystem.findByLocation(locationId)
                        .stream()
                        .filter(((Predicate<GameObjectId>) SPIELER_CHARAKTER::equals).negate())
                        .map(this::get)
                        .filter(IDescribableGO.class::isInstance)
                        .collect(toImmutableList());
        loadGameObjects(res);

        return (ImmutableList<LOC_DESC>) (ImmutableList<?>) res;
    }

    private static <DESC_OBJ extends ILocatableGO & IDescribableGO>
    ImmutableList<DESC_OBJ> filterNonLivingBeing(final List<DESC_OBJ> gameObjects) {
        return gameObjects.stream()
                .filter(go -> !(go instanceof ILivingBeingGO))
                .collect(toImmutableList());
    }

    private static <DESC_OBJ extends ILocatableGO & IDescribableGO,
            LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> filterLivingBeing(final List<DESC_OBJ> gameObjects) {
        return (ImmutableList<LIV>) gameObjects.stream()
                .filter(ILivingBeingGO.class::isInstance)
                .collect(toImmutableList());
    }

    /**
     * Gibt eine (evtl. auch etwas längere) Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * der Spieler das Game Object schon kennt oder nicht.
     */
    public Nominalphrase getDescription(final GameObjectId gameObjectId) {
        return getDescription((IDescribableGO) load(gameObjectId));
    }

    /**
     * Gibt eine (evtl. auch etwas längere) Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * der Spieler das Game Object schon kennt oder nicht.
     */
    public Nominalphrase getDescription(final IDescribableGO gameObject) {
        return getDescription(gameObject, false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    public Nominalphrase getDescription(final GameObjectId gameObjectId,
                                        final boolean shortIfKnown) {
        return getDescription((IDescribableGO) load(gameObjectId), shortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    public Nominalphrase getDescription(final IDescribableGO gameObject,
                                        final boolean shortIfKnown) {
        return getPOVDescription(loadSC(), gameObject, shortIfKnown);
    }

    /**
     * Gibt eine beschreibende Nominalphrase zurück, die das <code>describable</code>
     * aus Sicht das Beobachters mit der <code>observerId</code> beschreibt, ggf. kurz.
     */
    public @NonNull
    Nominalphrase getPOVDescription(final GameObjectId observerId,
                                    final IDescribableGO describable,
                                    final boolean shortIfKnown) {
        return getPOVDescription(load(observerId), describable, shortIfKnown);
    }

    /**
     * Gibt eine beschreibende Nominalphrase zurück, die das <code>describable</code>
     * aus Sicht des <code>observer</code>s beschreibt, ggf. kurz-
     */
    public @NonNull
    static Nominalphrase getPOVDescription(final IGameObject observer,
                                           final IDescribableGO describable,
                                           final boolean shortIfKnown) {
        if (observer instanceof IHasMemoryGO) {
            return getPOVDescription((IHasMemoryGO) observer, describable,
                    shortIfKnown);
        }

        return describable.descriptionComp().getNormalDescriptionWhenKnown();
    }

    private static Nominalphrase getPOVDescription(final IHasMemoryGO observer,
                                                   final IDescribableGO describable,
                                                   final boolean shortIfKnown) {
        return describable.descriptionComp().getDescription(
                observer.memoryComp().isKnown(describable), shortIfKnown);
    }

    /**
     * Lädt (sofern nicht schon geschehen) den Spieler-Charakter und gibt ihn zurück.
     */
    public @Nonnull
    SpielerCharakter loadSC() {
        prepare();

        return (SpielerCharakter) load(SPIELER_CHARAKTER);
    }

    /**
     * Lädt (sofern nicht schon geschehen) diese Game Objects und gibt es zurück.
     */
    private static void loadGameObjects(final Collection<? extends GameObject> gameObjects) {
        for (final GameObject gameObject : gameObjects) {
            gameObject.load();
        }
    }

    /**
     * Lädt (sofern nicht schon geschehen) dieses Game Object und gibt sie zurück.
     */
    @Nonnull
    public ImmutableList<GameObject> load(final Collection<GameObjectId> ids) {
        return ids.stream()
                .map(this::load)
                .collect(toImmutableList());
    }

    /**
     * Lädt (sofern nicht schon geschehen) dieses Game Object und gibt es zurück.
     */
    @Nonnull
    public GameObject load(final GameObjectId id) {
        final GameObject gameObject = get(id);
        gameObject.load();
        return gameObject;
    }

    /**
     * Speichert für alle Game Objects ihre aktuellen Daten in die Datenbank - löscht
     * außerdem alle Daten aus alle geladenen Daten aus dem Speicher.
     */
    public void saveAll(final boolean unload) {
        prepare();

        for (final GameObject gameObject : all.values()) {
            gameObject.save(unload);
        }
    }

    /**
     * Gibt das {@link GameObject} mit dieser {@link GameObjectId} zurück.
     * Kann gefahrlos mehrfach hintereinander aufgerufen werden.
     * Wenn das GameObject noch nicht geladen wurde, wird es auch hiermit nicht
     * geladen.
     */
    @NonNull
    private GameObject get(final GameObjectId id) {
        prepare();

        @Nullable final GameObject res = all.get(id);

        if (res == null) {
            throw new IllegalArgumentException("No game object found for id " + id);
        }

        return res;
    }


    public LocationSystem getLocationSystem() {
        return locationSystem;
    }

    SpatialConnectionSystem getSpatialConnectionSystem() {
        return spatialConnectionSystem;
    }
}
