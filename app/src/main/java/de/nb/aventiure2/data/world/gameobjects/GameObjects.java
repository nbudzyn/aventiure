package de.nb.aventiure2.data.world.gameobjects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

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
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakterFactory;
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
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SchlossVorhalleTischBeimFestConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SimpleConnectionCompFactory;
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
 * All game objects
 */
public class GameObjects {
    public static final AvDateTime SCHLOSSFEST_BEGINN_DATE_TIME =
            new AvDateTime(2,
                    oClock(5, 30));

    public static final String COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN =
            "Invisibles_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN";

    // SPIELER-CHARAKTER
    public static final GameObjectId SPIELER_CHARAKTER = new GameObjectId(1);

    // OBJECTS
    public static final GameObjectId HAENDE_DES_SPIELER_CHARAKTERS = new GameObjectId(10_000);
    public static final GameObjectId EINE_TASCHE_DES_SPIELER_CHARAKTERS = new GameObjectId(10_001);
    public static final GameObjectId GOLDENE_KUGEL = new GameObjectId(10_100);
    public static final GameObjectId SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST =
            new GameObjectId(10_101);

    // CREATURES
    public static final GameObjectId FROSCHPRINZ = new GameObjectId(20_001);
    public static final GameObjectId SCHLOSSWACHE = new GameObjectId(20_000);

    // RÄUME
    public static final GameObjectId SCHLOSS_VORHALLE = new GameObjectId(30_000);
    public static final GameObjectId SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST = new GameObjectId(30_001);
    public static final GameObjectId DRAUSSEN_VOR_DEM_SCHLOSS = new GameObjectId(30_002);
    public static final GameObjectId IM_WALD_NAHE_DEM_SCHLOSS = new GameObjectId(30_003);
    public static final GameObjectId ABZWEIG_IM_WALD = new GameObjectId(30_004);
    public static final GameObjectId VOR_DER_HUETTE_IM_WALD = new GameObjectId(30_005);
    public static final GameObjectId HUETTE_IM_WALD = new GameObjectId(30_006);
    public static final GameObjectId BETT_IN_DER_HUETTE_IM_WALD = new GameObjectId(30_007);
    public static final GameObjectId HINTER_DER_HUETTE = new GameObjectId(30_008);
    public static final GameObjectId IM_WALD_BEIM_BRUNNEN = new GameObjectId(30_009);
    public static final GameObjectId UNTEN_IM_BRUNNEN = new GameObjectId(30_010);
    public static final GameObjectId WALDWILDNIS_HINTER_DEM_BRUNNEN = new GameObjectId(30_011);

    // INVISIBLES
    public static final GameObjectId TAGESZEIT = new GameObjectId(40_001);
    public static final GameObjectId SCHLOSSFEST = new GameObjectId(40_000);

    // Sonstige Konstanten
    private static final boolean SCHLOSS_VORHALLE_DAUERHAFT_BELEUCHTET = true;

    /**
     * Vor einem Zugriff auf <code>ALL</code> muss {@link #prepare(AvDatabase)} aufgerufen werden!
     */
    private static GameObjectIdMap ALL;

    private static GOReactionsCoordinator REACTIONS_COORDINATOR;

    // SYSTEMS
    private static LocationSystem LOCATION_SYSTEM;

    @VisibleForTesting
    public static void reset(final AvDatabase db) {
        ALL = null;
        REACTIONS_COORDINATOR = null;
        LOCATION_SYSTEM = null;
        saveAllInitialState(db);
    }

    /**
     * Speichert für alle Game Objects ihre initialen Daten in die Datenbank.
     * War die Datenbank vorher leer, ist hiermit das Spiel auf Anfang zurückgesetzt.
     */
    public static void saveAllInitialState(final AvDatabase db) {
        prepare(db);

        for (final GameObject gameObject : ALL.values()) {
            gameObject.saveInitialState();
        }
    }

    /**
     * Muss vor jedem Datenbank-Zugriff
     * aufgerufen werden. Kann gefahrlos mehrfach hintereinander aufgerufen werden.
     * Füllt beim ersten Aufruf die interne Liste aller Game Objects (dabei
     * werden die Daten der Objekte <i>noch nicht</i> aus der Datenbank geladen),
     * startet außerdem alle <i>Systems</i> (sofern noch nicht geschehen).
     */
    private static void prepare(final AvDatabase db) {
        if (ALL == null) {
            final SpielerCharakterFactory spieler = new SpielerCharakterFactory(db);
            final ObjectFactory object = new ObjectFactory(db);
            final CreatureFactory creature = new CreatureFactory(db);
            final InvisibleFactory invisible = new InvisibleFactory(db);
            final RoomFactory room = new RoomFactory(db);
            final SimpleConnectionCompFactory connection = new SimpleConnectionCompFactory(db);

            ALL = new GameObjectIdMap();
            ALL.putAll(
                    spieler.create(SPIELER_CHARAKTER),
                    room.create(SCHLOSS_VORHALLE, StoringPlaceType.EIN_TISCH,
                            SCHLOSS_VORHALLE_DAUERHAFT_BELEUCHTET,
                            new SchlossVorhalleConnectionComp(db)),
                    room.create(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
                            StoringPlaceType.NEBEN_SC_AUF_BANK,
                            SCHLOSS_VORHALLE_DAUERHAFT_BELEUCHTET,
                            new SchlossVorhalleTischBeimFestConnectionComp(db)),
                    room.create(DRAUSSEN_VOR_DEM_SCHLOSS,
                            false,
                            new DraussenVorDemSchlossConnectionComp(db)),
                    room.create(IM_WALD_NAHE_DEM_SCHLOSS, StoringPlaceType.WALDWEG,
                            false,
                            new ImWaldNaheDemSchlossConnectionComp(db)),
                    room.create(ABZWEIG_IM_WALD, StoringPlaceType.WALDWEG,
                            false,
                            connection.createAbzweigImWald()),
                    room.create(VOR_DER_HUETTE_IM_WALD, StoringPlaceType.VOR_DER_HUETTE,
                            false,
                            connection.createVorDerHuetteImWald()),
                    room.create(HUETTE_IM_WALD, StoringPlaceType.HOLZTISCH,
                            false,
                            connection.createHuetteImWald()),
                    room.create(BETT_IN_DER_HUETTE_IM_WALD, StoringPlaceType.NEBEN_DIR_IM_BETT,
                            false,
                            connection.createBettInDerHuetteImWald()),
                    room.create(HINTER_DER_HUETTE, StoringPlaceType.UNTER_DEM_BAUM,
                            false,
                            connection.createHinterDerHuette()),
                    room.createImWaldBeimBrunnen(),
                    room.create(UNTEN_IM_BRUNNEN, StoringPlaceType.AM_GRUNDE_DES_BRUNNENS,
                            false,
                            connection.createNoConnections(UNTEN_IM_BRUNNEN)),
                    room.create(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                            StoringPlaceType.MATSCHIGER_WALDBODENN,
                            false,
                            connection.createWaldwildnisHinterDemBrunnen()),
                    creature.createSchlosswache(),
                    creature.createFroschprinz(),
                    // STORY Anhand eines StatusDatums kann das Spiel ermitteln, wann der
                    //  Frosch im Schloss ankommt.

                    // STORY Wölfe (Creatures? Invisibles?) hetzen Spieler nachts
                    //  Es könnte z.B. Räume neben dem Weg geben, die der Spieler in aller Regel
                    //  nicht betreten, kann, wo aber die Wölfe laufen.
                    //  Es könnte einen sicheren Platz geben - z.B. wäre der Weg sicher
                    //  oder die Hütte.

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
                    object.create(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST,
                            np(M, "ein langer, aus Brettern gezimmerter Tisch",
                                    "einem langen, aus Brettern gezimmertem Tisch",
                                    "einen langen, aus Brettern gezimmerten Tisch"),
                            np(M, "der lange Brettertisch", "dem langen Brettertisch",
                                    "den langen Brettertisch"),
                            np(M, "der Tisch", "dem Tisch", "den Tisch"),
                            SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST, SCHLOSS_VORHALLE,
                            false,
                            TISCH,
                            SCHLOSS_VORHALLE_DAUERHAFT_BELEUCHTET)
                    // STORY Spieler kauft Lampe (z.B. für Hütte) auf Schlossfest
            );
        }

        if (LOCATION_SYSTEM == null) {
            LOCATION_SYSTEM = new LocationSystem(db);
        }

        if (REACTIONS_COORDINATOR == null) {
            REACTIONS_COORDINATOR = new GOReactionsCoordinator(db);
        }
    }

    // TODO Aus dem, was in den Subklassen on AbstractGameObject noch ist,
    //  components machen. Components werden mit der Game-Object-ID
    //  verknüpft (Schlüssel gemeinsam mit dem Component-Typ)
    //  und nur Components speichern ihren State (wenn sie einen haben).

    // TODO ObjectData etc. zu Components umbauen, Gemeinsamkeiten zu separaten
    //  Components zusammenfassen.
    //  Interfaces für die Components verwenden?
    //  Idee:
    //  RoomFactory roomFactory = new Assemblage(component1::new, component2::new);
    //  Room schloss = RoomFactory.create();

    // TODO Dinge / Frösche etc. könnten collectible sein.

    // TODO Vielleich ist eine Components ist das Inventory / ContainerComponent
    //  (allerdings ist das quasi die Rückrichtung zur location....). Der Player
    //  - aber vielleicht auch Räume oder bisherige AvObjects - könnten ein Inventory haben.

    @Contract(pure = true)
    @NonNull
    public static GOReactionsCoordinator narrateAndDoReactions() {
        return REACTIONS_COORDINATOR;
    }

    static <R extends IReactions,
            G extends GameObject & IResponder>
    List<G>
    loadResponders(final AvDatabase db, final Class<R> reactionsInterface) {
        prepare(db);

        final ImmutableList<GameObject> res =
                ALL.values().stream()
                        .filter(IResponder.class::isInstance)
                        .filter(resp -> IResponder.reactsTo(resp, reactionsInterface))
                        .collect(toImmutableList());
        GameObjects.loadGameObjects(res);

        return (ImmutableList<G>) (ImmutableList<?>) res;
    }

    /**
     * Lädt (soweit noch nicht geschehen) die nicht-lebenden Game Objects an dieser
     * <code>location</code> (auch rekursiv enthaltene, z.B. Kugel auf Tisch in Raum)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingRecursiveInventory(
            final AvDatabase db,
            final ILocationGO location) {
        return filterNoLivingBeing(loadDescribableRecursiveInventory(db, location));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * an denen etwas abgelegt werden kann an diesem Ort (auch rekursiv), lädt sie
     * (sofern noch nicht geschehen)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public static <LOCATABLE_DESC_LOCATION extends ILocatableGO & IDescribableGO & ILocationGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> loadDescribableNonLivingLocationRecursiveInventory(
            final AvDatabase db,
            final ILocationGO inventoryHolder) {
        return loadDescribableNonLivingLocationRecursiveInventory(db, inventoryHolder.getId());
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * an denen etwas abgelegt werden kann an diesem Ort (auch rekursiv), lädt sie
     * (sofern noch nicht geschehen)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public static <LOCATABLE_DESC_LOCATION extends ILocatableGO & IDescribableGO & ILocationGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> loadDescribableNonLivingLocationRecursiveInventory(
            final AvDatabase db,
            final GameObjectId inventoryHolderId) {
        return filterLocation(
                loadDescribableNonLivingRecursiveInventory(db, inventoryHolderId));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * an denen etwas abgelegt werden kann an diesem Ort (<i>nicht</i>> rekursiv), lädt sie
     * (sofern noch nicht geschehen)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public static <LOCATABLE_DESC_LOCATION extends ILocatableGO & IDescribableGO & ILocationGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> loadDescribableNonLivingLocationInventory(
            final AvDatabase db,
            final ILocationGO inventoryHolder) {
        return filterLocation(
                loadDescribableNonLivingInventory(db, inventoryHolder.getId()));
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
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingMovableRecursiveInventory(
            final AvDatabase db,
            final GameObjectId locationId) {
        return filterMovable(
                loadDescribableNonLivingRecursiveInventory(db, locationId));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * die <i>movable</i> sind (also z.B. vom SC bewegt werden könnten) an dieser
     * <i>locationId</i> (aber <i>nicht</i> rekursiv enthaltene, z.B. <i>nicht</i>
     * die Kugel auf einem Tisch in einem Raum),
     * lädt sie (sofern noch nicht geschehen) und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingMovableInventory(
            final AvDatabase db,
            final GameObjectId locationId) {
        return filterMovable(
                loadDescribableNonLivingInventory(db, locationId));
    }

    /**
     * Ermittelt die Game Objects,
     * die <i>nicht movable</i> sind (also <i>nicht</i> bewegt werden könnten) an dieser
     * <i>locationId</i> (aber <i>nicht</i> rekursiv enthaltene, z.B. eine
     * <i>nicht</i> die schwere Vase auf einem Tisch in einem Raum),
     * lädt sie (sofern noch nicht geschehen) und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonMovableInventory(
            final AvDatabase db,
            final GameObjectId locationId) {
        return filterNotMovable(loadDescribableInventory(db, locationId));
    }

    private static <GO extends ILocatableGO> ImmutableList<GO> filterMovable(
            final List<GO> gameObjects) {
        return gameObjects.stream()
                .filter(go -> go.locationComp().isMovable())
                .collect(toImmutableList());
    }

    private static <GO extends ILocatableGO> ImmutableList<GO> filterNotMovable(
            final List<GO> gameObjects) {
        return gameObjects.stream()
                .filter(go -> !go.locationComp().isMovable())
                .collect(toImmutableList());
    }

    /**
     * Lädt (soweit noch nicht geschehen) die nicht-lebenden Game Objects von dieser
     * <i>locationId</i> zurück (auch rekursiv, z.B. Kugel auf einem Tisch in einem Raum)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingRecursiveInventory(
            final AvDatabase db,
            final GameObjectId locationId) {
        return filterNoLivingBeing(loadDescribableRecursiveInventory(db, locationId));
    }

    /**
     * Lädt (soweit noch nicht geschehen) die nicht-lebenden Game Objects von dieser
     * <i>locationId</i> zurück (aber <i>nicht</i> rekursiv, z.B. <i>nicht</i> die
     * Kugel auf einem Tisch in einem Raum)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    private static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingInventory(
            final AvDatabase db,
            final GameObjectId locationId) {
        return filterNoLivingBeing(loadDescribableInventory(db, locationId));
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle lebenden Game Objects von dieser
     * <i>locationId</i> zurück und gibt sie zurück, auch rekursiv
     * (also Kugel auf einem Tisch im Raum o.ä.) -
     * nur Gegenstände, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public static <LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> loadDescribableLivingRecursiveInventory(final AvDatabase db,
                                                               final GameObjectId locationId) {
        return filterLivingBeing(loadDescribableRecursiveInventory(db, locationId));
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle lebenden Game Objects im Inventar dieser
     * <code>location</code>s und gibt sie zurück, auch rekursiv
     * (also Kugel auf einem Tisch im Raum o.ä.) -
     * nur Game Objects, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public static <LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> loadDescribableLivingRecursiveInventory(final AvDatabase db,
                                                               final ILocationGO location) {
        return filterLivingBeing(loadDescribableRecursiveInventory(db, location));
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle Game Objects an dieser
     * <code>location</code>s (auch rekursiv enthaltene, z.B. Kugel auf einem Tisch in einem Raum)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    private static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableRecursiveInventory(final AvDatabase db,
                                                              final ILocationGO location) {
        return loadDescribableRecursiveInventory(db, location.getId());
    }

    /**
     * Lädt (soweit noch nicht geschehen) die Game Objects an dieser
     * <code>locationId</code> (auch rekursiv, z.B. Kugel auf Tisch in Raum)
     * und gibt sie zurück - nur Game Objects, die eine Beschreibung haben,
     * nicht den Spieler-Charakter.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableRecursiveInventory(final AvDatabase db,
                                                              final GameObjectId locationId) {
        final ImmutableList<LOC_DESC> directContainedList =
                loadDescribableInventory(db, locationId);

        final ImmutableList.Builder<LOC_DESC> res = ImmutableList.builder();
        res.addAll(directContainedList);

        for (final LOC_DESC directContained : directContainedList) {
            if (directContained instanceof ILocationGO) {
                res.addAll(loadDescribableInventory(db, (ILocationGO) directContained));
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
    private static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableInventory(final AvDatabase db,
                                                     final ILocationGO location) {
        return loadDescribableInventory(db, location.getId());
    }

    /**
     * Lädt (soweit noch nicht geschehen) die Game Objects an dieser
     * <code>locationId</code> (<i>nicht</i> rekursiv, also <i>nicht</i> die Kugel
     * auf einem Tisch in einem Raum, sondern nur den Tisch)
     * und gibt sie zurück - nur Game Objects, die eine Beschreibung haben,
     * nicht den Spieler-Charakter.
     */
    private static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableInventory(final AvDatabase db,
                                                     final GameObjectId locationId) {
        prepare(db);

        final ImmutableList<GameObject> res =
                LOCATION_SYSTEM.findByLocation(locationId)
                        .stream()
                        .filter(((Predicate<GameObjectId>) SPIELER_CHARAKTER::equals).negate())
                        .map(id -> get(db, id))
                        .filter(IDescribableGO.class::isInstance)
                        .collect(toImmutableList());
        GameObjects.loadGameObjects(res);

        return (ImmutableList<LOC_DESC>) (ImmutableList<?>) res;
    }


    private static <GO extends IGameObject> ImmutableList<GO> filterNoLivingBeing(
            final List<GO> gameObjects) {
        return gameObjects.stream()
                .filter(((Predicate<GO>) ILivingBeingGO.class::isInstance).negate())
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
     * Gibt eine beschreibende Nominalphrase zurück, die das <code>describable</code>
     * aus Sicht das Beobachters mit der <code>observerId</code> beschreibt, ggf. kurz.
     */
    public static @NonNull
    Nominalphrase getPOVDescription(final AvDatabase db, final GameObjectId observerId,
                                    final IDescribableGO describable,
                                    final boolean shortIfKnown) {
        return getPOVDescription(load(db, observerId), describable, shortIfKnown);
    }

    /**
     * Gibt eine beschreibende Nominalphrase zurück, die das <code>describable</code>
     * aus Sicht des <code>observer</code>s beschreibt, ggf. kurz-
     */
    public static @NonNull
    Nominalphrase getPOVDescription(final IGameObject observer,
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
    public static @Nonnull
    SpielerCharakter loadSC(final AvDatabase db) {
        return (SpielerCharakter) load(db, SPIELER_CHARAKTER);
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
    public static ImmutableList<GameObject> load(final AvDatabase db,
                                                 final Collection<GameObjectId> ids) {
        return ids.stream()
                .map(id -> load(db, id))
                .collect(toImmutableList());
    }

    /**
     * Lädt (sofern nicht schon geschehen) dieses Game Object und gibt es zurück.
     */
    @Nonnull
    public static GameObject load(final AvDatabase db, final GameObjectId id) {
        final GameObject gameObject = get(db, id);
        gameObject.load();
        return gameObject;
    }

    /**
     * Speichert für alle Game Objects ihre aktuellen Daten in die Datenbank - löscht
     * außerdem alle Daten aus alle geladenen Daten aus dem Speicher.
     */
    public static void saveAll(final AvDatabase db) {
        prepare(db);

        for (final GameObject gameObject : ALL.values()) {
            gameObject.save();
        }
    }

    /**
     * Gibt das {@link GameObject} mit dieser {@link GameObjectId} zurück.
     * Kann gefahrlos mehrfach hintereinander aufgerufen werden.
     * Wenn das GameObject noch nicht geladen wurde, wird es auch hiermit nicht
     * geladen.
     */
    @NonNull
    private static GameObject get(final AvDatabase db, final GameObjectId id) {
        prepare(db);

        @Nullable final GameObject res = ALL.get(id);

        if (res == null) {
            throw new IllegalArgumentException("No game object found for id " + id);
        }

        return res;
    }

    private GameObjects() {
    }
}
