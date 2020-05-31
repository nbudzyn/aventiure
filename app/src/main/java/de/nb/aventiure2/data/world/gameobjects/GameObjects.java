package de.nb.aventiure2.data.world.gameobjects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.german.base.Nominalphrase;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;

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
    public static final GameObjectId GOLDENE_KUGEL = new GameObjectId(10_000);
    public static final GameObjectId SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST =
            new GameObjectId(10_001);

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

    /**
     * Vor einem Zugriff auf <code>ALL</code> muss {@link #prepare(AvDatabase)} aufgerufen werden!
     */
    private static GameObjectIdMap ALL;

    private static GOReactionsCoordinator REACTIONS_COORDINATOR;

    // SYSTEMS
    private static LocationSystem LOCATION_SYSTEM;

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
                            true,
                            new SchlossVorhalleConnectionComp(db)),
                    room.create(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
                            StoringPlaceType.NEBEN_SC_AUF_BANK,
                            true,
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
                            false)
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
     * Lädt (soweit noch nicht geschehen) die nicht-lebenden Game Objects im Inventar dieses
     * <code>inventoryHolder</code>s und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingInventory(
            final AvDatabase db,
            final IHasStoringPlaceGO inventoryHolder) {
        return filterNoLivingBeing(loadDescribableInventory(db, inventoryHolder));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * die <i>movable</i> sind (also z.B. vom SC bewegt werden könnten) an dieser
     * <i>locationId</i>, lädt sie (sofern noch nicht geschehen) und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingMovableInventory(
            final AvDatabase db,
            final GameObjectId locationId) {
        return filterMovable(loadDescribableNonLivingInventory(db, locationId));
    }

    private static <GO extends ILocatableGO> ImmutableList<GO> filterMovable(
            final List<GO> gameObjects) {
        return gameObjects.stream()
                .filter(go -> go.locationComp().isMovable())
                .collect(toImmutableList());
    }

    /**
     * Lädt (soweit noch nicht geschehen) die nicht-lebenden Game Objects von dieser
     * <i>locationId</i> zurück und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingInventory(
            final AvDatabase db,
            final GameObjectId locationId) {
        return filterNoLivingBeing(loadDescribableInventory(db, locationId));
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle lebenden Game Objects von dieser
     * * <i>locationId</i> zurück und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public static <LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> loadDescribableLivingInventory(final AvDatabase db,
                                                      final GameObjectId locationId) {
        return filterLivingBeing(loadDescribableInventory(db, locationId));
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle lebenden Game Objects im Inventar dieses
     * <code>inventoryHolder</code>s und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public static <LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> loadDescribableLivingInventory(final AvDatabase db,
                                                      final IHasStoringPlaceGO inventoryHolder) {
        return filterLivingBeing(loadDescribableInventory(db, inventoryHolder));
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle Game Objects im Inventar dieses
     * <code>inventoryHolder</code>s und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    private static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableInventory(final AvDatabase db,
                                                     final IHasStoringPlaceGO inventoryHolder) {
        return loadDescribableInventory(db, inventoryHolder.getId());
    }

    /**
     * Lädt (soweit noch nicht geschehen) die Game Objects im Inventar dieses
     * <code>inventoryHolder</code>s und gibt sie zurück -
     * nur Dinge (oder Kreaturen...), die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    private static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableInventory(final AvDatabase db,
                                                     final GameObjectId inventoryHolder) {
        prepare(db);

        final ImmutableList<GameObject> res =
                LOCATION_SYSTEM.findByLocation(inventoryHolder)
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
     * aus Sicht das Beobachters mit der <code>observerId</code> beschreibt.
     */
    public static @NonNull
    Nominalphrase getPOVDescription(final AvDatabase db, final GameObjectId observerId,
                                    final IDescribableGO describable) {
        return getPOVDescription(db, observerId, describable, false);
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
     * aus Sicht des <code>observer</code>s beschreibt.
     */
    public static @NonNull
    Nominalphrase getPOVDescription(final IGameObject observer,
                                    final IDescribableGO describable) {
        return getPOVDescription(observer, describable, false);
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
