package de.nb.aventiure2.data.world.gameobjects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

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
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.german.base.Nominalphrase;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUFMERKSAM;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.NOCH_NICHT_BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.NORMAL;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectStateList.sl;
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
    public static GameObjectId GOLDENE_KUGEL = new GameObjectId(10_000);

    // CREATURES
    public static final GameObjectId FROSCHPRINZ = new GameObjectId(20_001);
    public static final GameObjectId SCHLOSSWACHE = new GameObjectId(20_000);

    // RÄUME
    public static final GameObjectId SCHLOSS_VORHALLE = new GameObjectId(30_000);
    public static final GameObjectId SCHLOSS_VORHALLE_TISCH_BEIM_FEST = new GameObjectId(30_001);
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
            final RoomFactory room = new RoomFactory();

            ALL = new GameObjectIdMap();
            ALL.putAll(
                    spieler.create(SPIELER_CHARAKTER),
                    room.create(SCHLOSS_VORHALLE, StoringPlaceType.EIN_TISCH),
                    room.create(SCHLOSS_VORHALLE_TISCH_BEIM_FEST, StoringPlaceType.HOLZTISCH),
                    room.create(DRAUSSEN_VOR_DEM_SCHLOSS),
                    room.create(IM_WALD_NAHE_DEM_SCHLOSS, StoringPlaceType.WALDWEG),
                    room.create(ABZWEIG_IM_WALD, StoringPlaceType.WALDWEG),
                    room.create(VOR_DER_HUETTE_IM_WALD, StoringPlaceType.VOR_DER_HUETTE),
                    room.create(HUETTE_IM_WALD, StoringPlaceType.HOLZTISCH),
                    room.create(BETT_IN_DER_HUETTE_IM_WALD, StoringPlaceType.NEBEN_DIR_IM_BETT),
                    room.create(HINTER_DER_HUETTE, StoringPlaceType.UNTER_DEM_BAUM),
                    room.create(IM_WALD_BEIM_BRUNNEN, StoringPlaceType.GRAS_NEBEN_DEM_BRUNNEN),
                    room.create(UNTEN_IM_BRUNNEN, StoringPlaceType.AM_GRUNDE_DES_BRUNNENS),
                    room.create(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                            StoringPlaceType.MATSCHIGER_WALDBODENN),
                    creature.createBasic(SCHLOSSWACHE,
                            np(F, "eine Schlosswache mit langer Hellebarde",
                                    "einer Schlosswache mit langer Hellebarde"),
                            np(F, "die Schlosswache mit ihrer langen Hellebarde",
                                    "der Schlosswache mit ihrer langen Hellebarde"),
                            np(F, "die Schlosswache",
                                    "der Schlosswache"),
                            sl(UNAUFFAELLIG, AUFMERKSAM),
                            SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS),
                    creature.createTalking(FROSCHPRINZ,
                            np(M, "ein dicker, hässlicher Frosch",
                                    "einem dicken, hässlichen Frosch",
                                    "einen dicken, hässlichen Frosch"),
                            np(M, "der hässliche Frosch",
                                    "dem hässlichen Frosch",
                                    "den hässlichen Frosch"),
                            np(M, "der Frosch",
                                    "dem Frosch",
                                    "den Frosch"),
                            sl(UNAUFFAELLIG, HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                                    HAT_NACH_BELOHNUNG_GEFRAGT, HAT_FORDERUNG_GESTELLT,
                                    AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN,
                                    ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                                    AUF_DEM_WEG_ZUM_SCHLOSSFEST,
                                    HAT_HOCHHEBEN_GEFORDERT),
                            IM_WALD_BEIM_BRUNNEN, ABZWEIG_IM_WALD),
                    // STORY Anhand eines StatusDatums kann das Spiel ermitteln, wann der
                    //  Frosch im Schloss ankommt.
                    //  Besser nicht Nullable! (Nicht schlüssig, weil jedes HasState,
                    //  wenn es exisitiert immer einen Status hat!)

                    // STORY Wölfe (Creatures? Invisibles?) hetzen Spieler nachts

                    invisible.create(SCHLOSSFEST, sl(NOCH_NICHT_BEGONNEN, BEGONNEN)),
                    invisible.create(TAGESZEIT, sl(NORMAL)),
                    object.create(GOLDENE_KUGEL,
                            np(F, "eine goldene Kugel",
                                    "einer goldenen Kugel"),
                            np(F, "die goldene Kugel", "der goldenen Kugel"),
                            np(F, "die Kugel", "der Kugel"),
                            SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS)
                    // STORY Die goldene Kugel kann verloren gehen, zum Beispiel wenn man sie im
                    //  Sumpf ablegt. Dann gibt es eine art Reset und eine ähnliche goldene
                    //  Kugel erscheint wieder im Schloss. Der Text dort sagt so dann etwas wie
                    //  "eine goldene kugel wie du sie schon einmal gesehen hast, nur etwas
                    //  kleiner".

                    // STORY Spieler kauft Lampe (z.B. für Hütte) auf Schlossfest
            );
        }

        if (LOCATION_SYSTEM == null) {
            LOCATION_SYSTEM = new LocationSystem(db);
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


    /**
     * Lädt (soweit noch nicht geschehen) alle Gegenstände im Inventar des Spieler-Charakters,
     * und gibt sie zurück.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadSCInventory(final AvDatabase db) {
        return loadDescribableInventory(db, SPIELER_CHARAKTER);
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
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableInventory(final AvDatabase db,
                                                     final IHasStoringPlaceGO inventoryHolder) {
        return loadDescribableInventory(db, inventoryHolder.getId());
    }

    /**
     * Lädt (soweit noch nicht geschehen) die Game Objects im Inventar dieses
     * <code>inventoryHolder</code>s und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben, nicht den Spieler-Charakter.
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableInventory(final AvDatabase db,
                                                     final GameObjectId inventoryHolder) {
        prepare(db);

        final ImmutableList<GameObject> res =
                LOCATION_SYSTEM.findByLocation(inventoryHolder)
                        .stream()
                        .filter(not(SPIELER_CHARAKTER::equals))
                        .map(id -> get(db, id))
                        .filter(IDescribableGO.class::isInstance)
                        .collect(toImmutableList());
        GameObjects.loadGameObjects(db, res);

        return (ImmutableList<LOC_DESC>) (ImmutableList<?>) res;
    }

    public static ImmutableList<ILivingBeingGO> loadLivingBeings(final AvDatabase db) {
        final ImmutableList<GameObject> res =
                ALL.values().stream()
                        .filter(ILivingBeingGO.class::isInstance)
                        .collect(toImmutableList());
        GameObjects.loadGameObjects(db, res);

        return (ImmutableList<ILivingBeingGO>) (ImmutableList<?>) res;
    }

    private static <GO extends IGameObject> ImmutableList<GO> filterNoLivingBeing(
            final List<GO> gameObjects) {
        return gameObjects.stream()
                .filter(not(ILivingBeingGO.class::isInstance))
                .collect(toImmutableList());
    }

    private static <DESC_OBJ extends ILocatableGO & IDescribableGO,
            LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> filterLivingBeing(final List<DESC_OBJ> gameObjects) {
        return (ImmutableList<LIV>) (ImmutableList<?>)
                gameObjects.stream()
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
            if (((IHasMemoryGO) observer).memoryComp().isKnown(describable)) {
                return shortIfKnown ?
                        describable.descriptionComp().getShortDescriptionWhenKnown() :
                        describable.descriptionComp().getNormalDescriptionWhenKnown();
            }

            return describable.descriptionComp().getDescriptionAtFirstSight();
        }

        return describable.descriptionComp().getNormalDescriptionWhenKnown();

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
    private static void loadGameObjects(final AvDatabase db,
                                        final Collection<? extends GameObject> gameObjects) {
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
