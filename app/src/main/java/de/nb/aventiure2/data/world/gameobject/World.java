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
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.gameobject.wetter.*;
import de.nb.aventiure2.data.world.syscomp.alive.AliveSystem;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.location.RoomFactory;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.IHasMentalModelGO;
import de.nb.aventiure2.data.world.syscomp.movement.IMovingGO;
import de.nb.aventiure2.data.world.syscomp.movement.MovementSystem;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.system.ReactionSystem;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.AbzweigImWaldConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.DraussenVorDemSchlossConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ImWaldNaheDemSchlossConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ObenImTurmConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SchlossVorhalleConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SimpleConnectionCompFactory;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.VorDemTurmConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.VorDerHuetteImWaldConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ZwischenDenHeckenVorDemSchlossExternConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstPhrReihung;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp.LEUCHTET_IMMER;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp.LEUCHTET_NIE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.EINE_TASCHE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.HAENDE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.TISCH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANG;
import static de.nb.aventiure2.german.base.Artikel.Typ.DEF;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.BRETTERTISCH;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DINGE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KUGEL;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * The world contains and manages all game objects.
 */
@SuppressWarnings("unchecked")
public class World {
    public static final AvDateTime SCHLOSSFEST_BEGINN_DATE_TIME =
            new AvDateTime(2,
                    oClock(5, 30));

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
    public static final GameObjectId BETTGESTELL_IN_DER_HUETTE_IM_WALD = new GameObjectId(10_120);
    public static final GameObjectId BAUM_IM_GARTEN_HINTER_DER_HUETTE_IM_WALD =
            new GameObjectId(10_130);
    public static final GameObjectId RAPUNZELS_HAARE = new GameObjectId(10_140);
    public static final GameObjectId BETT_OBEN_IM_ALTEN_TURM = new GameObjectId(10_150);

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
    public static final GameObjectId ZWISCHEN_DEN_HECKEN_VOR_DEM_SCHLOSS_EXTERN =
            new GameObjectId(30_003);
    public static final GameObjectId IM_WALD_NAHE_DEM_SCHLOSS = new GameObjectId(30_004);
    public static final GameObjectId VOR_DEM_ALTEN_TURM = new GameObjectId(30_005);
    public static final GameObjectId OBEN_IM_ALTEN_TURM = new GameObjectId(30_006);
    public static final GameObjectId ABZWEIG_IM_WALD = new GameObjectId(30_010);
    public static final GameObjectId VOR_DER_HUETTE_IM_WALD = new GameObjectId(30_011);
    public static final GameObjectId HUETTE_IM_WALD = new GameObjectId(30_012);
    public static final GameObjectId HINTER_DER_HUETTE = new GameObjectId(30_014);
    public static final GameObjectId IM_WALD_BEIM_BRUNNEN = new GameObjectId(30_015);
    public static final GameObjectId UNTEN_IM_BRUNNEN = new GameObjectId(30_016);
    public static final GameObjectId WALDWILDNIS_HINTER_DEM_BRUNNEN = new GameObjectId(30_017);

    // INVISIBLES
    public static final GameObjectId WETTER = new GameObjectId(40_000);
    public static final GameObjectId TAGESZEIT = new GameObjectId(40_001);
    public static final GameObjectId SCHLOSSFEST = new GameObjectId(40_010);
    public static final GameObjectId RAPUNZELS_NAME = new GameObjectId(40_012);
    public static final GameObjectId RAPUNZELS_GESANG = new GameObjectId(40_013);
    public static final GameObjectId RAPUNZELRUF = new GameObjectId(40_014);
    public static final GameObjectId RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU =
            new GameObjectId(40_015);
    public static final GameObjectId RAPUNZELS_FREIHEITSWUNSCH = new GameObjectId(40_016);
    public static final GameObjectId SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT =
            new GameObjectId(40_020);


    // MEANING
    public static final GameObjectId STORY_WEB = new GameObjectId(100_000);

    private static volatile World INSTANCE;

    private final AvDatabase db;

    private final TimeTaker timeTaker;

    private final Narrator n;

    private GameObjectIdMap all;

    private ReactionSystem reactionSystem;

    // SYSTEMS
    private AliveSystem aliveSystem;

    private SpatialConnectionSystem spatialConnectionSystem;

    private LocationSystem locationSystem;

    private MovementSystem movementSystem;

    public static World getInstance(final AvDatabase db,
                                    final TimeTaker timeTaker,
                                    final Narrator n) {
        if (INSTANCE == null) {
            synchronized (World.class) {
                if (INSTANCE == null) {
                    INSTANCE = new World(db, timeTaker, n);
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

    private World(final AvDatabase db, final TimeTaker timeTaker,
                  final Narrator n) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.n = n;
    }

    /**
     * Füllt die interne Liste aller Game Objects (dabei
     * werden die Daten der Objekte <i>noch nicht</i> aus der Datenbank geladen),
     * startet außerdem alle <i>Systems</i> (sofern noch nicht geschehen).
     */
    @SuppressWarnings("InstantiationOfUtilityClass")
    private void prepare() {
        if (all != null) {
            return;
        }

        if (aliveSystem == null) {
            aliveSystem = new AliveSystem();
        }

        if (locationSystem == null) {
            locationSystem = new LocationSystem(db);
        }

        if (movementSystem == null) {
            movementSystem = new MovementSystem(db);
        }

        if (spatialConnectionSystem == null) {
            spatialConnectionSystem = new SpatialConnectionSystem(this);
        }

        if (reactionSystem == null) {
            reactionSystem = new ReactionSystem(n, this, timeTaker);
        }

        final SpielerCharakterFactory spieler = new SpielerCharakterFactory(db, timeTaker, n, this);
        final GeneralObjectFactory object = new GeneralObjectFactory(db, timeTaker, this);
        final BankAmTischBeimSchlossfestFactory bankAmTischBeimSchlossfest =
                new BankAmTischBeimSchlossfestFactory(db, timeTaker, this);
        final SchattenDerBaeumeFactory schattenDerBaeume =
                new SchattenDerBaeumeFactory(db, timeTaker, this);
        final BettgestellFactory bettgestell = new BettgestellFactory(db, timeTaker, this);
        final BettFactory bett = new BettFactory(db, timeTaker, this);
        final BaumFactory baum = new BaumFactory(db, timeTaker, this);
        final CreatureFactory creature = new CreatureFactory(db, timeTaker, n, this);
        final InvisibleFactory invisible = new InvisibleFactory(db, timeTaker, n, this);
        final WetterFactory wetter = new WetterFactory(db, timeTaker, n, this);
        final MeaningFactory meaning = new MeaningFactory(db, timeTaker, n, this,
                spatialConnectionSystem);
        final RoomFactory room = new RoomFactory(db, timeTaker, n, this);
        final SimpleConnectionCompFactory connection =
                new SimpleConnectionCompFactory(db, timeTaker, n, this);

        all = new GameObjectIdMap();
        all.putAll(
                spieler.create(SPIELER_CHARAKTER),
                room.create(SCHLOSS_VORHALLE, StoringPlaceType.EIN_TISCH,
                        false, false,
                        LEUCHTET_IMMER,
                        new SchlossVorhalleConnectionComp(db, timeTaker, n, this)),
                room.create(DRAUSSEN_VOR_DEM_SCHLOSS,
                        StoringPlaceType.BODEN_VOR_DEM_SCHLOSS,
                        false, true, LEUCHTET_NIE,
                        new DraussenVorDemSchlossConnectionComp(db, timeTaker, n, this)),
                room.create(ZWISCHEN_DEN_HECKEN_VOR_DEM_SCHLOSS_EXTERN,
                        StoringPlaceType.BODEN_VOR_DEM_SCHLOSS,
                        false, true, LEUCHTET_NIE,
                        new ZwischenDenHeckenVorDemSchlossExternConnectionComp(db, timeTaker, n,
                                this)),
                room.create(IM_WALD_NAHE_DEM_SCHLOSS, StoringPlaceType.WEG,
                        false, true, LEUCHTET_NIE,
                        new ImWaldNaheDemSchlossConnectionComp(db, timeTaker, n, this)),
                room.create(VOR_DEM_ALTEN_TURM,
                        StoringPlaceType.STEINIGER_GRUND_VOR_TURM,
                        false, true, LEUCHTET_NIE,
                        new VorDemTurmConnectionComp(db, timeTaker, n, this)),
                room.create(OBEN_IM_ALTEN_TURM,
                        StoringPlaceType.HOLZDIELEN_OBEN_IM_TURM,
                        false, true, LEUCHTET_NIE,
                        new ObenImTurmConnectionComp(db, timeTaker, n, this)),
                room.create(ABZWEIG_IM_WALD, StoringPlaceType.WEG,
                        false, true, LEUCHTET_NIE,
                        new AbzweigImWaldConnectionComp(db, timeTaker, n, this)),
                room.create(VOR_DER_HUETTE_IM_WALD,
                        StoringPlaceType.ERDBODEN_VOR_DER_HUETTE,
                        false, true, LEUCHTET_NIE,
                        new VorDerHuetteImWaldConnectionComp(db, timeTaker, n, this)),
                room.create(HUETTE_IM_WALD, StoringPlaceType.HOLZTISCH,
                        false, true, LEUCHTET_NIE,
                        connection.createHuetteImWald()),
                room.create(HINTER_DER_HUETTE, StoringPlaceType.UNTER_DEM_BAUM,
                        false, true, LEUCHTET_NIE,
                        connection.createHinterDerHuette()),
                room.createImWaldBeimBrunnen(),
                room.create(UNTEN_IM_BRUNNEN, StoringPlaceType.AM_GRUNDE_DES_BRUNNENS,
                        false, true, LEUCHTET_NIE,
                        connection.createNoConnections(UNTEN_IM_BRUNNEN)),
                room.create(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                        StoringPlaceType.MATSCHIGER_WALDBODEN,
                        false, true, LEUCHTET_NIE,
                        connection.createWaldwildnisHinterDemBrunnen()),
                creature.createSchlosswache(),
                creature.createFroschprinz(),
                creature.createRapunzel(),
                creature.createRapunzelsZauberin(),

                meaning.createStoryWeb(),

                invisible.createSchlossfest(),
                wetter.create(),
                invisible.createTageszeit(),
                InvisibleFactory.create(RAPUNZELS_NAME),
                InvisibleFactory.create(RAPUNZELS_GESANG),
                InvisibleFactory.create(RAPUNZELRUF),
                InvisibleFactory
                        .create(RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU),
                InvisibleFactory.create(RAPUNZELS_FREIHEITSWUNSCH),
                InvisibleFactory.create(SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT),
                object.create(EINE_TASCHE_DES_SPIELER_CHARAKTERS,
                        // Weil nicht klar, welche Tasche -> kein Bezugsobjekt
                        NomenFlexionsspalte.TASCHE_EINE,
                        SPIELER_CHARAKTER, null,
                        false, // Man kann nicht "eine Tasche hinlegen" o.Ä.
                        EINE_TASCHE,
                        true, false,
                        LEUCHTET_NIE),
                object.create(HAENDE_DES_SPIELER_CHARAKTERS,
                        np(NomenFlexionsspalte.HAENDE, HAENDE_DES_SPIELER_CHARAKTERS),
                        SPIELER_CHARAKTER, null,
                        false,
                        false, HAENDE, true),
                object.create(GOLDENE_KUGEL,
                        np(F, INDEF, "goldene Kugel",
                                "goldenen Kugel", GOLDENE_KUGEL),
                        np(F, DEF,
                                "goldene Kugel",
                                "goldenen Kugel", GOLDENE_KUGEL),
                        np(KUGEL, GOLDENE_KUGEL),
                        SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS,
                        true),
                // IDEA Die goldene Kugel kann verloren gehen, zum Beispiel wenn man sie im
                //  Sumpf ablegt. Dann gibt es eine art Reset und eine ähnliche goldene
                //  Kugel erscheint wieder im Schloss. Der Text dort sagt so dann etwas wie
                //  "eine goldene Kugel, wie du sie schon einmal gesehen hast, nur etwas
                //  kleiner | größer".
                // IDEA Dinge, die man auf den Weg legt oder beim Schlossfest
                //  auf den Tisch, verschwinden einfach, wenn man weggeht (sie werden
                //  gestohlen) - vorausgesetzt, man braucht sie nicht mehr und die
                //  emotionale Bindung ist gering (also nicht die goldene Kugel)!
                bankAmTischBeimSchlossfest.create(),
                object.create(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST,
                        np(M, INDEF, "langer, aus Brettern gezimmerter Tisch",
                                "langen, aus Brettern gezimmertem Tisch",
                                "langen, aus Brettern gezimmerten Tisch",
                                SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST),
                        np(LANG, BRETTERTISCH, SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST),
                        np(NomenFlexionsspalte.TISCH, SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST),
                        // Der Tisch wird erst spontan hinzugefügt, wenn
                        // sich der Benutzer an einen Platz setzt.
                        // Ansonsten bekommen wir vorher Aktionen wie
                        // "Die Kugel auf den Tisch legen" angeboten. (Auf welchen der
                        // vielen Tisch denn??)

                        // Der Tisch wird sozusagen "instanziiert".
                        // Man könnte ein Framework für diese "Instanziierung"
                        // anbieten, wo man eine "Tisch-Instanz" betritt und
                        // später wieder verlässt (worauf alles zurückgesetzt wird und
                        // der SC ggf. alle relevanten Gegenstände automatisch mitnimmt.)
                        null, null,
                        false,
                        TISCH, false),
                schattenDerBaeume.createVorDemAltenTurm(),
                bettgestell.createInDerHuetteImWald(),
                bett.createObenImAltenTurm(),
                baum.createImGartenHinterDerHuetteImWald(),
                GeneralObjectFactory.create(RAPUNZELS_HAARE)
        );
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


    /**
     * Gibt <code>true</code> zurück falls das Game Object eines dieser anderen ist oder
     * sich (ggf. rekusiv) an einer dieser Locations befindet.
     */
    public static boolean isOrHasRecursiveLocation(
            @Nullable final IGameObject one, final GameObject... others) {
        if (one == null) {
            return false;
        }

        for (final GameObject other : others) {
            if (isOrHasRecursiveLocation(one, other)) {
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

        return isOrHasRecursiveLocation(gameObject, location);
    }


    /**
     * Gibt <code>true</code> zurück falls
     * <ul>
     * <li>das Game Object mit der ID <code>oneId</code>  <code>other</code> ist
     * <li>oder sich das Game Object mit der ID <code>oneId</code>
     * an der Location <code>other</code> befindet
     * (ggf. rekusiv).
     * </ul>
     */
    public boolean isOrHasRecursiveLocation(final GameObjectId oneId,
                                            @Nullable final IGameObject other) {
        if (other == null) {
            return false;
        }

        return isOrHasRecursiveLocation(load(oneId), other);
    }

    /**
     * Gibt <code>true</code> zurück falls
     * <ul>
     * <li><code>one</code>  <code>other</code> ist
     * <li>oder sich <code>one</code> an der Location <code>other</code> befindet
     * (ggf. rekusiv).
     * </ul>
     */
    public static boolean isOrHasRecursiveLocation(@Nullable final IGameObject one,
                                                   @Nullable final IGameObject other) {
        if (one == null) {
            return other == null;
        }

        if (other == null) {
            return false;
        }

        if (one.equals(other)) {
            return true;
        }

        if (!(one instanceof ILocatableGO)) {
            return false;
        }

        if (!(other instanceof ILocationGO)) {
            return false;
        }

        return ((ILocatableGO) one).locationComp().hasRecursiveLocation((ILocationGO) other);
    }


    @Contract(pure = true)
    @NonNull
    public ReactionSystem narrateAndDoReactions() {
        return reactionSystem;
    }

    public void resetSchonBegruesstMitSC() {
        prepare();

        for (final ITalkerGO<?> talker : loadTalkerGOs()) {
            talker.talkingComp().setSchonBegruesstMitSC(false);
        }
    }

    private List<ITalkerGO<?>> loadTalkerGOs() {
        final ImmutableList<GameObject> res =
                find(ITalkerGO.class).stream()
                        .filter(t -> !t.is(SPIELER_CHARAKTER))
                        .collect(toImmutableList());
        return loadGameObjects(res);
    }

    public <R extends IReactions, G extends GameObject & IResponder>
    List<G> loadResponders(final Class<R> reactionsInterface) {
        final ImmutableList<GameObject> res =
                find(IResponder.class).stream()
                        .filter(resp -> IResponder.reactsTo(resp, reactionsInterface))
                        .collect(toImmutableList());
        return loadGameObjects(res);
    }


    /**
     * (Lädt und) gibt alle {@link ILocatableGO}s mit Beschreibung zurück, die sich
     * <i>gemäß mentalem Modell dieses
     * {@link de.nb.aventiure2.data.world.syscomp.mentalmodel.IHasMentalModelGO}s</i>
     * an dieser Location befinden, auch rekursiv
     * (ebenfalls gemäß dem mentalen Modell: wenn das {@code IHasMentalModelGO}
     * davon ausgeht, dass hier ein Tisch steht, dann wird auch die Kugel zurückgeben, von der
     * er ausgeht, dass sie auf dem Tisch liegt).
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadAssumedDescribableRecursiveInventory(
            final IHasMentalModelGO hasMentalModelGO,
            final GameObjectId locationId) {
        return (ImmutableList<LOC_DESC>) (ImmutableList<?>)
                hasMentalModelGO.mentalModelComp()
                        .getAssumedRecursiveInventory(locationId)
                        .stream()
                        .filter(locatable -> !locatable.is(SPIELER_CHARAKTER))
                        .filter(IDescribableGO.class::isInstance)
                        .collect(toImmutableList());
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
    private <LOCATABLE_DESC_LOCATION extends ILocatableGO & IDescribableGO & ILocationGO>
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
     * soweit sie dem SC bekannt sind, lädt sie (sofern noch nicht geschehen) und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingMovableKnownToSCRecursiveInventory(
            final GameObjectId locationId) {
        return loadSC().memoryComp()
                .filterKnown(loadDescribableNonLivingMovableRecursiveInventory(locationId));
    }

    /**
     * Ermittelt alle MovingBeings an dieser <i>locationId</i>
     * (auch rekursiv enthaltene, z.B. Kugel auf einem Tisch in einem Raum),
     * die derzeit <i>in Bewegung</i> (<i>moving</i>) sind -
     * nur solche, die eine Beschreibung haben.
     */
    public <MOV extends IMovingGO & ILocatableGO & IDescribableGO>
    ImmutableList<MOV> loadMovingBeingsMovingDescribableInventory(
            final GameObjectId locationId) {
        return MovementSystem.filterMoving(
                filterMovingGO(loadDescribableRecursiveInventory(locationId)));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * die <i>movable</i> sind (also z.B. vom SC bewegt werden könnten) an dieser
     * <i>locationId</i> (auch rekursiv enthaltene, z.B. Kugel auf einem Tisch in einem Raum),
     * lädt sie (sofern noch nicht geschehen) und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
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
                LocationSystem.filterNotMovable(loadDescribableInventory(locationId)));
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
        final ImmutableList.Builder<LOC_DESC> res = ImmutableList.builder();

        final ImmutableList<LOC_DESC> directlyContainedList = loadDescribableInventory(locationId);
        res.addAll(directlyContainedList);

        for (final LOC_DESC directlyContained : directlyContainedList) {
            if (directlyContained instanceof ILocationGO) {
                res.addAll(loadDescribableRecursiveInventory((ILocationGO) directlyContained));
            }
        }

        return res.build();
    }


    /**
     * (Speichert ggf. alle Änderungen und) ermittelt, ob sich an diese Location
     * (auch rekursiv) ein Objekt befindet, dass die Location erleuchtet.
     * <p>
     * Ob die Location selbst leuchtet (oder auch das Tageslicht) wird nicht berücksichtigt.
     */
    public boolean inventoryErleuchtetLocation(final GameObjectId locationId) {
        // Bisher können nur Locations leuchten. Wenn später auch andere
        // Objekte leuchten sollen, muss man hier und in der StoringPlaceComp
        // Dinge anpassen: Dann sind oft sowohl Location interessant (wegen der
        // Rekursion) als auch andere Dinge, wenn sie leuchten könnten.
        for (final ILocationGO directlyContained : loadLocationInventory(locationId)) {
            if (directlyContained.storingPlaceComp()
                    .manKannHineinsehenUndLichtScheintHineinUndHinaus()) {
                if (directlyContained.storingPlaceComp().leuchtet()) {
                    return true;
                }

                if (inventoryErleuchtetLocation(directlyContained.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private static <DESC_OBJ extends ILocatableGO & IDescribableGO,
            MOV extends ILocatableGO & IDescribableGO & IMovingGO>
    ImmutableList<MOV> filterMovingGO(final List<DESC_OBJ> gameObjects) {
        return (ImmutableList<MOV>) gameObjects.stream()
                .filter(IMovingGO.class::isInstance)
                .collect(toImmutableList());
    }

    /**
     * Ermittelt ein {@link IMovingGO}, das den SC gerade verlassen hat
     * (oder ihm gerade auf dem Weg entgegengekommen ist).
     *
     * @param toId Richtung, in der das {@link IMovingGO} gegangen ist.
     */
    @Nullable
    public IMovingGO loadWerDenSCGeradeVerlassenHat(final GameObjectId toId) {
        saveAll(false);

        @Nullable final GameObjectId id = movementSystem.findWerDenSCGeradeVerlassenHat(toId);
        if (id == null) {
            return null;
        }

        return (IMovingGO) load(id);
    }

    /**
     * (Speichert ggf. alle Änderungen und) lädt (soweit noch nicht geschehen) die Game Objects
     * an dieser <code>locationId</code> (<i>nicht</i> rekursiv)
     * und gibt sie zurück - nur {@link ILocationGO}s.
     */
    private ImmutableList<ILocationGO> loadLocationInventory(final GameObjectId locationId) {
        saveAll(false);

        final ImmutableList<GameObject> res =
                locationSystem.findByLocation(locationId)
                        .stream()
                        .map(this::get)
                        .filter(ILocationGO.class::isInstance)
                        .collect(toImmutableList());
        return loadGameObjects(res);
    }

    /**
     * (Speichert ggf. alle Änderungen und) lädt (soweit noch nicht geschehen) die Game Objects
     * an dieser
     * <code>locationId</code> (<i>nicht</i> rekursiv, also <i>nicht</i> die Kugel
     * auf einem Tisch in einem Raum, sondern nur den Tisch)
     * und gibt sie zurück - nur Game Objects, die eine Beschreibung haben,
     * nicht den Spieler-Charakter.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableInventory(final GameObjectId locationId) {
        saveAll(false);

        final ImmutableList<GameObject> res =
                locationSystem.findByLocation(locationId).stream()
                        .filter(((Predicate<GameObjectId>) SPIELER_CHARAKTER::equals).negate())
                        .map(this::get)
                        .filter(IDescribableGO.class::isInstance)
                        .collect(toImmutableList());
        return loadGameObjects(res);
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
     * Lädt (soweit noch nicht geschehen) alle Living Beings, die sich an
     * einem Ort befinden können und eine Beschreibung haben - nicht den Spieler-Charakter.
     */
    public <LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
    ImmutableList<LIV> loadDescribableLocatableLivingBeings() {
        final ImmutableList<GameObject> res =
                ((Collection<? extends GameObject>) find(ILivingBeingGO.class)).stream()
                        .filter(liv -> !liv.is(SPIELER_CHARAKTER))
                        .filter(ILocatableGO.class::isInstance)
                        .filter(IDescribableGO.class::isInstance)
                        .collect(toImmutableList());
        return loadGameObjects(res);
    }

    /**
     * Gibt alle Game Objects mit dieser Klasse oder diesem Interface zurück -
     * <i>aber lädt sie noch nicht</i>!
     */
    private <GO_IN extends IGameObject, GO_OUT extends GameObject & IGameObject>
    Collection<GO_OUT> find(final Class<GO_IN> goClass) {
        saveAll(false);

        return (ImmutableList<GO_OUT>) all.values().stream()
                .filter(goClass::isInstance)
                .collect(toImmutableList());
    }

    public SubstantivischePhrase getDescriptionSingleOrReihung(
            final Collection<? extends IDescribableGO> objects) {
        if (objects.isEmpty()) {
            return Indefinitpronomen.NICHTS;
        }

        if (objects.size() == 1) {
            final IDescribableGO object = objects.iterator().next();

            return getDescription(object, false);
        }

        return new SubstPhrReihung(
                mapToList(objects, o -> getDescription(o, false)));
    }

    /**
     * Gibt eine Beschreibung dieses Objekts zurück - wenn es nur eines ist - sonst
     * etwas wie "die Dinge".
     */
    public SubstantivischePhrase getDescriptionSingleOrCollective(
            final Collection<? extends IDescribableGO> objects) {
        return getDescriptionSingleOrCollective(objects, false);
    }

    /**
     * Gibt eine Beschreibung dieses Objekts zurück - wenn es nur eines ist - sonst
     * etwas wie "die Dinge".
     */
    public SubstantivischePhrase getDescriptionSingleOrCollective(
            final Collection<? extends IDescribableGO> objects,
            final boolean shortIfKnown) {
        if (objects.isEmpty()) {
            return Indefinitpronomen.NICHTS;
        }

        if (objects.size() == 1) {
            final IDescribableGO object = objects.iterator().next();

            return getDescription(object, shortIfKnown);
        }

        return DINGE;
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine kurze
     * Beschreibung des Game Objects.
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die mysteriöse Lampe" oder "die Lampe" zurück.
     */
    public SubstantivischePhrase anaph(final IDescribableGO describableGO) {
        return anaph(describableGO, true);
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
    public SubstantivischePhrase anaph(final GameObjectId describableId) {
        return anaph(describableId, true);
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
    public SubstantivischePhrase anaph(
            final GameObjectId describableId,
            final boolean descShortIfKnown) {
        return anaph((IDescribableGO) load(describableId), descShortIfKnown);
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
    public SubstantivischePhrase anaph(
            final IDescribableGO describableGO,
            final boolean descShortIfKnown) {
        // IDEA Auch andere "Anaphern" (im weitesten Sinne) erzeugen, nicht nur Personalpronomen:
        //  Auch Synonyme, Überbegriffe oder schlicht Wiederholung könnten Anaphern sein.
        //  Dann am besten Alternativen zurückgeben!
        //  Man könnte Anaphern wie "die Frau" für alle Dinge "im Raum" erzeugen und
        //  dann darauf achten, dass es nicht zu Verwechslungen kommen kann.

        @Nullable final Personalpronomen anaphPersPron = n.getAnaphPersPronWennMgl(describableGO);
        if (anaphPersPron != null) {
            return anaphPersPron;
        }

        return getDescription(describableGO, descShortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    public EinzelneSubstantivischePhrase getDescription(final GameObjectId gameObjectId) {
        return getDescription((IDescribableGO) load(gameObjectId));
    }

    /**
     * Gibt eine (evtl. auch etwas längere) Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * der Spieler das Game Object schon kennt oder nicht.
     */
    public EinzelneSubstantivischePhrase getDescription(final IDescribableGO gameObject) {
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
    public EinzelneSubstantivischePhrase getDescription(final GameObjectId gameObjectId,
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
    public EinzelneSubstantivischePhrase getDescription(final IDescribableGO gameObject,
                                                        final boolean shortIfKnown) {
        return getPOVDescription(loadSC(), gameObject, shortIfKnown);
    }

    /**
     * Gibt eine beschreibende Nominalphrase zurück, die das <code>describable</code>
     * aus Sicht das Beobachters mit der <code>observerId</code> beschreibt, ggf. kurz.
     */
    public @NonNull
    EinzelneSubstantivischePhrase getPOVDescription(final GameObjectId observerId,
                                                    final IDescribableGO describable,
                                                    final boolean shortIfKnown) {
        return getPOVDescription(load(observerId), describable, shortIfKnown);
    }

    /**
     * Gibt eine beschreibende Nominalphrase zurück, die das <code>describable</code>
     * aus Sicht des <code>observer</code>s beschreibt, ggf. kurz-
     */
    @NonNull
    private static EinzelneSubstantivischePhrase getPOVDescription(final IGameObject observer,
                                                                   final IDescribableGO describable,
                                                                   final boolean shortIfKnown) {
        if (observer instanceof IHasMemoryGO) {
            return getPOVDescription((IHasMemoryGO) observer, describable,
                    shortIfKnown);
        }

        return describable.descriptionComp().getNormalDescriptionWhenKnown();
    }

    private static EinzelneSubstantivischePhrase getPOVDescription(final IHasMemoryGO observer,
                                                                   final IDescribableGO describable,
                                                                   final boolean shortIfKnown) {
        return describable.descriptionComp().getDescription(
                observer.memoryComp().isKnown(describable), shortIfKnown);
    }

    public final boolean hasSameOuterMostLocationAsSC(@Nullable final GameObjectId gameObjectId) {
        if (gameObjectId == null) {
            return false;
        }

        return hasSameOuterMostLocationAsSC(load(gameObjectId));
    }

    public final boolean hasSameOuterMostLocationAsSC(@Nullable final IGameObject gameObject) {
        if (gameObject == null) {
            return false;
        }

        return loadSC().locationComp().hasSameOuterMostLocationAs(gameObject);
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
     * Lädt (sofern nicht schon geschehen) das Wetter-Game-Object und gibt es zurück.
     */
    public @Nonnull
    Wetter loadWetter() {
        prepare();

        return (Wetter) load(WETTER);
    }

    /**
     * Lädt (sofern nicht schon geschehen) diese Game Objects und gibt sie
     * gecastet zurück.
     */
    private static <GO extends IGameObject>
    ImmutableList<GO> loadGameObjects(final Collection<? extends GameObject> gameObjects) {
        for (final GameObject gameObject : gameObjects) {
            gameObject.load();
        }

        return (ImmutableList<GO>) ImmutableList.copyOf(gameObjects);
    }

    /**
     * Lädt (sofern nicht schon geschehen) dieses Game Object und gibt sie zurück.
     */
    @Nonnull
    public ImmutableList<GameObject> load(final Collection<GameObjectId> ids) {
        return mapToList(ids, this::load);
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

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public LocationSystem getLocationSystem() {
        return locationSystem;
    }

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public MovementSystem getMovementSystem() {
        return movementSystem;
    }

    SpatialConnectionSystem getSpatialConnectionSystem() {
        return spatialConnectionSystem;
    }

    /**
     * Gibt ein Personalpronomen für die zweite Person zurück, dass den Spielercharakter meint.
     * <p>
     * Wir behaupten hier implizit, der SC wäre männlich. Es ist die Verantwortung des Aufrufers,
     * keine Sätze mit Konstruktionen wie "Du, der du" zu erzeugen, die
     * weibliche Adressaten ("du, die du") ausschließen.
     */
    public static Personalpronomen duSc() {
        return Personalpronomen.get(P2, M, SPIELER_CHARAKTER);
    }
}
