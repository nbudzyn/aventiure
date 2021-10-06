package de.nb.aventiure2.data.world.gameobject;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Geschlossenheit.MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Geschlossenheit.MAN_KANN_NICHT_DIREKT_HINEINSEHEN_UND_LICHT_SCHEINT_NICHT_HINEIN_ODER_HINAUS;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Geschlossenheit.NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT;
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
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.util.StreamUtil.*;

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
import de.nb.aventiure2.data.world.base.EnumRange;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.gameobject.wetter.*;
import de.nb.aventiure2.data.world.syscomp.alive.AliveSystem;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.amount.IAmountableGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.description.impl.AmountDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.feelings.EinschlafhindernisSc;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableLocationGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.location.RoomFactory;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.IHasMentalModelGO;
import de.nb.aventiure2.data.world.syscomp.movement.IMovingGO;
import de.nb.aventiure2.data.world.syscomp.movement.MovementSystem;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.system.ReactionSystem;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.AbzweigImWaldConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.DraussenVorDemSchlossConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.HinterDerHuetteConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ImWaldNaheDemSchlossConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ObenImTurmConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SchlossVorhalleConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SimpleConnectionCompFactory;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.VorDemTurmConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.VorDerHuetteImWaldConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.ZwischenDenHeckenVorDemSchlossExternConnectionComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.typed.GameObjectType;
import de.nb.aventiure2.data.world.syscomp.typed.ITypedGO;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstPhrReihung;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * The world contains and manages all game objects.
 */
@SuppressWarnings("unchecked")
public class World {
    // IDEA: Code klarer und strenger in Module schneiden:
    //  - Jedes Modul ist nur für eine fachliche Sache zuständig und hab die Hoheit über
    //   Daten und Logik für diese Sache. Jede Datenbanktabelle gehört nur einem einzigen Modul?
    //   (Problem: Counter!)
    //   Ein Modul benötigt möglichst wenige andere Module, keine zyklischen Abhängigkeiten
    //   (IntelliJ Dependency Matrix)
    //   Vermutlich gibt es
    //  -- ein UI-Modul,
    //  -- Module, die nichts mit Objekten der Welt zu tun haben
    //  -- Listener-Interfaces, die "jedermann" implementiern kann, um über Dinge unterrichtet zu
    //  werden ("poor man's event bus") - oder andere Basis für Dependency Injection? Hier braucht
    //     man eine Lösung für "Rapunzel sagt der Zauberin..., die Zauberin macht..., Rapunzel..."
    //  -- und vielleicht sind außerdem die Components mit ihren Systemen je ein Modul?
    //  -- Sowie vielleicht ein paar "Querschnittsmodule" mit jeweils spezifischer Funktionalität,
    //     die von mehreren anderen Modulen benötigt wird.
    //  - Ein Modul enthält keine "Inseln", die mit dem Rest des Moduls nichts zu tun haben.
    //    Also sollte ein Modul keine technische Schicht sein.
    //  - Die Module sind gegeneinander gekapselt und haben nur definierte, möglichst schnmale
    //   Schnittstellen.
    //  - Jedes Modul in ein einzige separates top-Level-Package schieben. Die
    //    Packages nach "Schichten" sortieren: client, fachlich high-level und low-level,
    //    technische Basisfunktionalität.
    //  - Innerhalb des Moduls ggf. Subpackages für einzelne Schichten, so dass die modul-internen
    //    Aufrufreihenfolgen klar sind. Auch
    //   Tests entsprechend den richtigen Modulen zuordnen.
    //  - Für jedes Modul eine definierte Fassade erstellen - nur über diese Fassade darf mit
    //    dem Modul interagiert werden. Aus der Fassade ausschließlich immutable Objects
    //    rausreichen?
    //  - Danach Module evtl. in Gradle-Projekte auslagern, so dass die Abhängigkeiten
    //   kontrolliert werden und je eine öffentliche Schnittstelle definiert werden kann? Oder
    //   zumindest mit IntelliJ prüfen, dass es keine unerwünschten Abhängigkeiten gibt?
    //   (Depency Matrix) Oder ArchiUnit-Tests anlegen?

    // FIXME Es soll möglich werden, zur Laufzeit gewisse Objekte zu erzeungen.

    // FIXME Ein solches Objekt sollte dupliziert werden können (Prototyping), z.B.
    //  man nimmt nur einen Teil der ausgerupften Binsen - dann wird das bestehende
    //  ausgerupfte-Binsen-Objekt kopiert und nur die Menge zweimal geändert.

    // FIXME Man muss feststellen können: Liegt an einen Ort schon ein
    //  ausgerupfte-Binsen-Objekt - oder nicht? Und welche ID hat es?

    // FIXME Idee: Jedes Objekt, das on-the-fly erzeugt werden soll, erhält einen Typ. Der Typ
    //  ist (zunächst)
    //  unveränderlich und ist hauptsächlich für Suchen vorgesehen
    //  (Finde alle Objekte vom Typ SC oder alle Objekte vom Typ ausgerupfte Binsen).

    public static final AvDateTime SCHLOSSFEST_BEGINN_DATE_TIME =
            new AvDateTime(2,
                    oClock(5, 30));

    // SPIELER-CHARAKTERF
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
    public static final GameObjectId HOLZ_FUER_STRICKLEITER = new GameObjectId(11_001);

    // CREATURES
    public static final GameObjectId SCHLOSSWACHE = new GameObjectId(20_000);
    public static final GameObjectId FROSCHPRINZ = new GameObjectId(20_001);
    /**
     * Der "Lobebauer" lobt den SC, nachdem er den Froschprinzen erlöst hat
     */
    public static final GameObjectId LOBEBAUER = new GameObjectId(20_002);
    public static final GameObjectId RAPUNZEL = new GameObjectId(20_010);
    public static final GameObjectId RAPUNZELS_ZAUBERIN = new GameObjectId(20_011);

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
    public static final GameObjectId BINSENSUMPF = new GameObjectId(30_015);
    public static final GameObjectId IM_WALD_BEIM_BRUNNEN = new GameObjectId(30_020);
    public static final GameObjectId UNTEN_IM_BRUNNEN = new GameObjectId(30_021);
    public static final GameObjectId WALDWILDNIS_HINTER_DEM_BRUNNEN = new GameObjectId(30_022);

    // INVISIBLES
    public static final GameObjectId WETTER = new GameObjectId(40_000);
    public static final GameObjectId SCHLOSSFEST = new GameObjectId(40_010);
    public static final GameObjectId RAPUNZELS_NAME = new GameObjectId(40_012);
    public static final GameObjectId RAPUNZELS_GESANG = new GameObjectId(40_013);
    public static final GameObjectId RAPUNZELRUF = new GameObjectId(40_014);
    public static final GameObjectId RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU =
            new GameObjectId(40_015);
    public static final GameObjectId RAPUNZELS_FREIHEITSWUNSCH = new GameObjectId(40_016);
    public static final GameObjectId SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT =
            new GameObjectId(40_020);

    /**
     * Maximale ID für feste Game-Object-IDs. Darüber beginnt der Bereich für
     * on-the-fly-Game-Objects (vgl. {@link OnTheFlyGOFactory}).
     */
    static final GameObjectId MAX_STATIC_GAME_OBJECT_ID = new GameObjectId(999_999);

    // MEANING
    public static final GameObjectId STORY_WEB = new GameObjectId(100_000);

    private static volatile World INSTANCE;

    private final AvDatabase db;

    private final TimeTaker timeTaker;

    private final Narrator n;

    /**
     * Enthält alle Game Objects (statische und on-the-fly-Game-Objects).
     */
    private GameObjectIdMap all;

    /**
     * Enthält alle on-the-fly-Game-Objekte, die beim Speichern aus der Datenbank gelöscht
     * werden sollen.
     */
    private GameObjectIdMap toBeDeleted;

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

        if (toBeDeleted == null) {
            toBeDeleted = new GameObjectIdMap();
        }

        final SpielerCharakterFactory spieler = new SpielerCharakterFactory(db, timeTaker, n, this);
        final GeneralObjectFactory object = new GeneralObjectFactory(db, timeTaker, this);
        final HolzFuerStrickleiterFactory holzFuerStrickleiter =
                new HolzFuerStrickleiterFactory(db, timeTaker, n, this);
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
                        false,
                        MAN_KANN_NICHT_DIREKT_HINEINSEHEN_UND_LICHT_SCHEINT_NICHT_HINEIN_ODER_HINAUS,
                        LEUCHTET_IMMER, EnumRange.of(Temperatur.KUEHL, Temperatur.WARM),
                        new SchlossVorhalleConnectionComp(db, timeTaker, n, this)),
                room.create(DRAUSSEN_VOR_DEM_SCHLOSS,
                        StoringPlaceType.BODEN_VOR_DEM_SCHLOSS,
                        false, NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT,
                        LEUCHTET_NIE,
                        new DraussenVorDemSchlossConnectionComp(db, timeTaker, n, this)),
                room.create(ZWISCHEN_DEN_HECKEN_VOR_DEM_SCHLOSS_EXTERN,
                        StoringPlaceType.BODEN_VOR_DEM_SCHLOSS,
                        false, NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT,
                        LEUCHTET_NIE,
                        new ZwischenDenHeckenVorDemSchlossExternConnectionComp(db, timeTaker, n,
                                this)),
                room.create(IM_WALD_NAHE_DEM_SCHLOSS, StoringPlaceType.WEG,
                        false, MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS,
                        LEUCHTET_NIE,
                        EnumRange.of(Temperatur.KLIRREND_KALT, Temperatur.RECHT_HEISS),
                        new ImWaldNaheDemSchlossConnectionComp(db, timeTaker, n, this)),
                room.create(VOR_DEM_ALTEN_TURM,
                        StoringPlaceType.STEINIGER_GRUND_VOR_TURM,
                        false, NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT,
                        LEUCHTET_NIE,
                        new VorDemTurmConnectionComp(db, timeTaker, n, this)),
                room.create(OBEN_IM_ALTEN_TURM,
                        StoringPlaceType.HOLZDIELEN_OBEN_IM_TURM,
                        false,
                        MAN_KANN_NICHT_DIREKT_HINEINSEHEN_UND_LICHT_SCHEINT_NICHT_HINEIN_ODER_HINAUS,
                        LEUCHTET_IMMER, // Rapunzel macht immer das Licht an, bevor sie
                        // die Haare herunterlässt - und sie macht das Licht nicht aus, solange
                        // der SC noch bei ihr ist.
                        EnumRange.of(Temperatur.KUEHL, Temperatur.WARM),
                        new ObenImTurmConnectionComp(db, timeTaker, n, this)),
                room.create(ABZWEIG_IM_WALD, StoringPlaceType.WEG,
                        false, MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS,
                        LEUCHTET_NIE,
                        EnumRange.of(Temperatur.KLIRREND_KALT, Temperatur.RECHT_HEISS),
                        new AbzweigImWaldConnectionComp(db, timeTaker, n, this)),
                room.create(VOR_DER_HUETTE_IM_WALD,
                        StoringPlaceType.ERDBODEN_VOR_DER_HUETTE,
                        false, MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS,
                        LEUCHTET_NIE,
                        EnumRange.of(Temperatur.KLIRREND_KALT, Temperatur.RECHT_HEISS),
                        new VorDerHuetteImWaldConnectionComp(db, timeTaker, n, this)),
                room.create(HUETTE_IM_WALD, StoringPlaceType.HOLZTISCH,
                        false,
                        MAN_KANN_NICHT_DIREKT_HINEINSEHEN_UND_LICHT_SCHEINT_NICHT_HINEIN_ODER_HINAUS,
                        LEUCHTET_NIE,
                        EnumRange.of(Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT,
                                Temperatur.SEHR_HEISS),
                        connection.createHuetteImWald()),
                room.create(HINTER_DER_HUETTE, StoringPlaceType.UNTER_DEM_BAUM,
                        false, MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS,
                        LEUCHTET_NIE,
                        EnumRange.of(Temperatur.KLIRREND_KALT, Temperatur.RECHT_HEISS),
                        new HinterDerHuetteConnectionComp(db, timeTaker, n, this)),
                room.create(BINSENSUMPF, StoringPlaceType.IM_MORAST,
                        false, MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS,
                        LEUCHTET_NIE,
                        EnumRange.of(Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT,
                                Temperatur.RECHT_HEISS),
                        connection.createBinsensumpf()),
                room.createImWaldBeimBrunnen(),
                room.create(UNTEN_IM_BRUNNEN, StoringPlaceType.AM_GRUNDE_DES_BRUNNENS,
                        false, MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS,
                        LEUCHTET_NIE,
                        EnumRange.of(Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT,
                                Temperatur.KUEHL),
                        connection.createNoConnections(UNTEN_IM_BRUNNEN)),
                room.create(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                        StoringPlaceType.MATSCHIGER_WALDBODEN,
                        false, MAN_KANN_HINEINSEHEN_UND_LICHT_SCHEINT_HINEIN_UND_HINAUS,
                        LEUCHTET_NIE,
                        EnumRange.of(Temperatur.KLIRREND_KALT, Temperatur.RECHT_HEISS),
                        connection.createWaldwildnisHinterDemBrunnen()),
                creature.createSchlosswache(),
                creature.createFroschprinz(),
                creature.createLobebauer(),
                creature.createRapunzel(),
                creature.createRapunzelsZauberin(),

                meaning.createStoryWeb(),

                invisible.createSchlossfest(),
                wetter.create(),
                InvisibleFactory.create(RAPUNZELS_NAME),
                InvisibleFactory.create(RAPUNZELS_GESANG),
                InvisibleFactory.create(RAPUNZELRUF),
                InvisibleFactory
                        .create(RAPUNZELS_ZAUBERIN_DIE_SIE_GEFANGEN_HAELT_IST_DIE_MAGERE_FRAU),
                InvisibleFactory.create(RAPUNZELS_FREIHEITSWUNSCH),
                InvisibleFactory.create(SC_HAT_RAPUNZEL_RETTUNG_ZUGESAGT),
                object.create(EINE_TASCHE_DES_SPIELER_CHARAKTERS,
                        // Weil nicht klar, welche Tasche -> kein Bezugsobjekt
                        np(INDEF, NomenFlexionsspalte.TASCHE),
                        SPIELER_CHARAKTER, null,
                        false, // Man kann nicht "eine Tasche hinlegen" o.Ä.
                        EINE_TASCHE,
                        true,
                        MAN_KANN_NICHT_DIREKT_HINEINSEHEN_UND_LICHT_SCHEINT_NICHT_HINEIN_ODER_HINAUS,
                        LEUCHTET_NIE,
                        EnumRange.of(Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT, Temperatur.WARM)),
                object.create(HAENDE_DES_SPIELER_CHARAKTERS,
                        np(NomenFlexionsspalte.HAENDE, HAENDE_DES_SPIELER_CHARAKTERS),
                        SPIELER_CHARAKTER, null,
                        false,
                        NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT, false, HAENDE,
                        EnumRange.of(Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT, Temperatur.WARM)),
                object.create(GOLDENE_KUGEL,
                        np(F, INDEF, "goldene Kugel",
                                "goldenen Kugel", GOLDENE_KUGEL),
                        np(F, DEF,
                                "goldene Kugel",
                                "goldenen Kugel", GOLDENE_KUGEL),
                        np(KUGEL, GOLDENE_KUGEL),
                        SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS,
                        true),
                holzFuerStrickleiter.createDraussenVorDemSchloss(),
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
                        np(NomenFlexionsspalte.TISCH,
                                SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST),
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
                        TISCH,
                        NACH_OBEN_WEITGEHEND_OFFEN_UND_UNGESCHUETZT,
                        false),
                schattenDerBaeume.createVorDemAltenTurm(),
                bettgestell.createInDerHuetteImWald(),
                bett.createObenImAltenTurm(),
                baum.createImGartenHinterDerHuetteImWald(),
                GeneralObjectFactory.create(RAPUNZELS_HAARE)
        );
    }

    /**
     * Initialisiert diese Game Object neu und macht es der Welt bekannt.
     * Erst danach darf man auf die Komponeten des Objekts zugreifen.
     */
    public void attachNew(final GameObject gameObject) {
        prepare();

        // Der Fall, dass ein Objekt mit dieser ID zuvor zum Löschen markiert wurde,
        // sollte sehr selten sein!
        toBeDeleted.remove(gameObject.getId());

        all.put(gameObject);

        gameObject.saveInitialState(false);
    }

    /**
     * Setzt die Location und ruft dabei entsprechende Narration-Callbacks auf;
     * oder erhöht die Menge des Objekts mit gleichem Typ am Zielort und löscht
     * das Locatable.
     */
    public void narrateAndSetLocationOrIncAmount(final ILocatableGO locatable,
                                                 @Nullable final ILocationGO newLocation) {
        narrateAndSetLocationOrIncAmount(locatable,
                newLocation != null ? newLocation.getId() : null);
    }

    /**
     * Setzt die Location und ruft dabei entsprechende Narration-Callbacks auf;
     * oder erhöht die Menge des Objekts mit gleichem Typ am Zielort und löscht
     * das Locatable.
     */
    public void narrateAndSetLocationOrIncAmount(final ILocatableGO locatable,
                                                 @Nullable final GameObjectId newLocationId) {
        narrateAndSetLocationOrIncAmount(locatable, newLocationId, () -> {});
    }

    /**
     * Setzt die Location und ruft dabei entsprechende Narration-Callbacks auf;
     * oder erhöht die Menge des Objekts mit gleichem Typ am Zielort und löscht
     * das Locatable.
     */
    public void narrateAndSetLocationOrIncAmount(final ILocatableGO locatable,
                                                 @Nullable final ILocationGO newLocation,
                                                 final Runnable onEnter) {
        narrateAndSetLocationOrIncAmount(locatable,
                newLocation != null ? newLocation.getId() : null, onEnter);
    }

    /**
     * Setzt die Location und ruft dabei entsprechende Narration-Callbacks auf;
     * oder erhöht die Menge des Objekts mit gleichem Typ am Zielort und löscht
     * das Locatable.
     */
    private void narrateAndSetLocationOrIncAmount(final ILocatableGO locatable,
                                                  @Nullable final GameObjectId newLocationId,
                                                  final Runnable onEnter) {
        if (newLocationId != null && locatable instanceof ITypedGO
                && locatable instanceof GameObject) {
            // Das locatable hat einen Typ (z.B. "ausgerupfte Binsen").
            // Dann prüfen, ob am Zielort bereits ein Objekt von diesem Typ liegt.

            final ImmutableList<? extends ITypedGO> objectsOfSameTypeAlreadyInNewLocation =
                    loadTypedInventory(newLocationId,
                            ((ITypedGO) locatable).typeComp().getType());
            if (!objectsOfSameTypeAlreadyInNewLocation.isEmpty()) {
                // Im Zielort liegt bereits ein Objekt von diesem Typ.
                // Kein weiteres Objekt desselben Typs dazulegen!
                final ITypedGO objectOfSameTypeAlreadyInNewLocation =
                        objectsOfSameTypeAlreadyInNewLocation.iterator().next();

                if (locatable instanceof IAmountableGO
                        && objectOfSameTypeAlreadyInNewLocation instanceof IAmountableGO) {
                    // Menge erhöhen
                    ((IAmountableGO) objectOfSameTypeAlreadyInNewLocation).amountComp()
                            .addAmount(((IAmountableGO) locatable).amountComp().getAmount());
                }

                onEnter.run();

                delete((GameObject) locatable);
                return;
            }
        }

        locatable.locationComp().narrateAndSetLocation(newLocationId, onEnter);
    }

    /**
     * Markiert dieses Game Object (diese ID) zum Löschen.
     * Danach darf man nicht mehr auf dieses Objekt zugreifen.
     */
    public void delete(final GameObject gameObject) {
        prepare();

        toBeDeleted.put(gameObject);
        all.remove(gameObject.getId());
    }

    /**
     * Speichert für alle Game Objects ihre initialen Daten in die Datenbank.
     * War die Datenbank vorher leer, ist hiermit das Spiel auf Anfang zurückgesetzt.
     */
    public void saveAllInitialState() {
        prepare();

        for (final GameObject gameObject : all.values()) {
            gameObject.saveInitialState(true);
        }

        toBeDeleted = new GameObjectIdMap();
    }

    public void scActionDone(final AvDateTime startTimeOfUserAction) {
        prepare();

        for (final GameObject gameObject : all.values()) {
            gameObject.scActionDone(startTimeOfUserAction);
        }
    }

    /**
     * Ermittelt die minimale Müdigkeit, die der SC verspüren muss, um an einem Ort einzuschlafen,
     * sowie alternative Beschreibungen, warum der SC ggf. nicht einschlafen kann.
     * Hängt z.B. davon ab, ob ein Sturm um ihn herum tobt oder wie gemütlich es an dem Ort ist.
     * <p>
     * Natürlich schläft der SC generell nur ein, wenn er es versucht oder sehr langweilige,
     * einschläfernde Dinge tut etc. Dies hier ist nur eine minimale Schwelle.
     */
    public EinschlafhindernisSc getEinschlafhindernisSc(
            final boolean gemuetlich) {
        final Windstaerke lokaleWindstaerke =
                loadWetter().wetterComp().getLokaleWindstaerke(
                        loadSC().locationComp().getLocation());

        final Hunger hunger = loadSC().feelingsComp().getHunger();

        // Achtung, Intensity-Schranken in aufsteigender Reihenfolge aufführen, damit
        // diese Logik funktioniert
        EinschlafhindernisSc res =
                new EinschlafhindernisSc(FeelingIntensity.NUR_LEICHT,
                        alt().add(
                                neuerSatz("Die Aufregung der letzten Stunden",
                                        "steckt dir noch in den Knochen – an Einschlafen ist",
                                        "nicht zu denken"),
                                du(SENTENCE, "bist", "noch nicht müde")
                                        .mitVorfeldSatzglied("müde"),
                                du(SENTENCE, "bist", "nicht müde")
                                        .mitVorfeldSatzglied("müde"),
                                du("fühlst", "dich wach")
                        ).schonLaenger().dann());

        if (!gemuetlich) {
            res =
                    new EinschlafhindernisSc(FeelingIntensity.MERKLICH,
                            alt().add(
                                    neuerSatz("hier ist es sehr ungemütlich"),
                                    neuerSatz("wie ungemütlich es hier ist!"),
                                    du(SENTENCE, "bist", "so müde nun auch wieder nicht")
                                            .mitVorfeldSatzglied("so müde")
                            ).schonLaenger().dann());
        }

        if (lokaleWindstaerke.compareTo(Windstaerke.WINDIG) >= 0) {
            res =
                    new EinschlafhindernisSc(FeelingIntensity.MERKLICH,
                            alt().add(
                                    neuerSatz("der Wind hält dich wach"),
                                    neuerSatz("der Wind rauscht"),
                                    du("kannst", "nicht einschlafen –",
                                            "bei diesem Wind")
                            ).schonLaenger().dann());
        }


        if (hunger == Hunger.HUNGRIG) {
            res =
                    new EinschlafhindernisSc(FeelingIntensity.DEUTLICH,
                            alt().add(
                                    du(SENTENCE, "kannst", "vor Hunger nicht einschlafen")
                                            .mitVorfeldSatzglied("vor Hunger"),
                                    neuerSatz("wie dir der Magen knurrt, ist an",
                                            "Einschlafen nicht zu denken"),
                                    neuerSatz("dir knurrt der Magen")
                            ).schonLaenger().dann());
        }


        if (lokaleWindstaerke.compareTo(Windstaerke.KRAEFTIGER_WIND) >= 0) {
            res =
                    new EinschlafhindernisSc(FeelingIntensity.MERKLICH,
                            alt().add(
                                    neuerSatz("es weht zu stark, als dass du",
                                            "einschlafen könntest"),
                                    neuerSatz("der Wind ist zu stark – du kannst nicht",
                                            "einschlafen"),
                                    neuerSatz("Einschlafen bei diesem Wind? – Keine Chance")
                            ).schonLaenger().dann());
        }

        return res;
    }

    /**
     * Gibt zurück, ob dieses <code>movable</code> Game Object in der Beschreibung erwähnt werden
     * soll, wenn der SC sich von <code>from</code> nach <code>to</code> bewegt hat. Das
     * Movable Game Object muss sich bereits an seiner neuen Location befinden.
     * <p
     * Unbewegliche Objekte sollen bereits in der Location-Beschreibung mitgenannt werden!
     * Das betrifft auch <code>unbewegliche Objekte</code>, die in (z.B. beweglichen Objekten)
     * direkt oder
     * rekursiv enthalten sind.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean shouldBeDescribedAfterScMovement(
            final @Nullable ILocationGO from,
            final IGameObject to,
            final GameObjectId movableGameObjectId) {
        return shouldBeDescribedAfterScMovement(from, to,
                this.<ILocatableGO>load(movableGameObjectId));
    }

    /**
     * Gibt zurück, ob dieses <code>movable</code> Game Object in der Beschreibung erwähnt werden
     * soll, wenn der SC sich von <code>from</code> nach <code>to</code> bewegt hat. Das
     * Movable Game Object muss sich bereits an seiner neuen Location befinden.
     * <p
     * Unbewegliche Objekte sollen bereits in der Location-Beschreibung mitgenannt werden!
     * Das betrifft auch <code>unbewegliche Objekte</code>, die in (z.B. beweglichen Objekten)
     * direkt oder
     * rekursiv enthalten sind.
     */
    private boolean shouldBeDescribedAfterScMovement(
            final @Nullable ILocationGO from,
            final IGameObject to,
            final ILocatableGO movableGameObject) {
        @Nullable final GameObjectId perceivedGOLocationId;
        // Ermitteln: (Wo) nimmt der SC das Game Object wahr?
        if (movableGameObject instanceof ILivingBeingGO
                && !IMovementReactions.scBemerkt(movableGameObject)) {
            perceivedGOLocationId = null;
        } else {
            perceivedGOLocationId = movableGameObject.locationComp().getLocationId();
        }

        // Ermitteln: (Wo) hat der SC das Game Object erwartet?
        @Nullable final GameObjectId assumedGOLocationId = loadSC().mentalModelComp()
                .getAssumedLocationId(movableGameObject.getId());

        if (assumedGOLocationId == null) {
            // Der SC hat das Game Object nirgendwo erwartet!
            if (perceivedGOLocationId == null) {
                // Und es ist auch nirgendwo! -> Nicht erwähnen!
                return false;
            }

            // Der SC hat das Game Object nirgendwo erwartet, und irgendwo ist es. Dann erwähnen,
            // falls es in to ist (oder sichtbar rekursiv innerhalb von to).
            return isOrHasVisiblyRecursiveLocation(perceivedGOLocationId, to);
        }

        // Der SC hat das Game Object irgendwo erwartet!

        if (perceivedGOLocationId == null) {
            // Aber das Game Object ist nirgendwo. Dann erwähnen,
            // falls der SC es in to erwartet hat (oder sichtbar rekursiv innerhalb von to).
            return isOrHasVisiblyRecursiveLocation(assumedGOLocationId, to);
        }

        // Der SC hat das Game Object irgendwo erwartet - und irgendwo sieht er es auch.

        if (!isOrHasVisiblyRecursiveLocation(perceivedGOLocationId, to)
                && !isOrHasVisiblyRecursiveLocation(assumedGOLocationId, to)) {
            // Weder der erwartete noch der wahrgenommene Ort liegen in to (auch nicht
            // sichtbar rekursiv). Dann nicht erwähnen!
            return false;
        }

        // Der wahrgenommene oder der erwartete Ort liegen innerhalb von to (ggf.
        // sichtbar rekursiv).

        if (!assumedGOLocationId.equals(perceivedGOLocationId)) {
            // Das movableGameObject ist an einem anderen Ort (innerhalb von to) als
            // erwartet -> erwähnen!
            return true;
        }

        if ( // Der SC hat das Game Object mitgebracht...
                isOrHasRecursiveLocation(movableGameObject, SPIELER_CHARAKTER)) {
            // Dann nicht erwaehnen
            return false;
        }

        // Der wahrgenommene und der erwartete Ort sind gleich - der Ort liegt innerhalb von to
        // (ggf. sichtbar rekursiv).

        if (// Wenn man z.B. in einem Zimmer auf einen Tisch steigt: Nicht noch einmal
            // beschreiben, was sonst auf dem Tisch steht!
                LocationSystem.isOrHasRecursiveLocation(to, from)) {
            // Nur, wenn man unter das Bett kriecht, unter das man bisher nicht hat
            //  sehen können
            return LocationSystem.isBewegungUeberSichtschranke(from, to);
        }

        // Ansonsten: Game Object soll beschrieben werden.
        return true;
    }

    /**
     * Gibt <code>true</code> zurück falls
     * <ul>
     * <li>das Game Object mit der ID <code>oneId</code>  <code>other</code> ist
     * <li>oder sich das Game Object mit der ID <code>oneId</code>
     * an der Location <code>other</code> befindet
     * (ggf. rekusiv) - soweit die Sichtbarkeit reicht.
     * </ul>
     */
    private boolean isOrHasVisiblyRecursiveLocation(final GameObjectId oneId,
                                                    @Nullable final IGameObject other) {
        if (other == null) {
            return false;
        }

        return LocationSystem.isOrHasVisiblyRecursiveLocation(load(oneId), other);
    }

    /**
     * Gibt <code>true</code> zurück, falls das Game Object als ID eine dieser
     * <code>locationIds</code> hat oder
     * sich (ggf. rekusiv) an einer dieser Locations befindet (soweit die Sichtbarkeit reicht).
     */
    public boolean isOrHasVisiblyRecursiveLocation(final GameObjectId gameObjectId,
                                                   final GameObjectId locationId,
                                                   // In der API
                                                   // isOrHasVisiblyRecursiveLocation(gameObjectId)
                                                   // verhindern.
                                                   final GameObjectId... moreLocationIds) {
        if (isOrHasVisiblyRecursiveLocation((IGameObject) load(gameObjectId), locationId)) {
            return true;
        }

        for (final GameObjectId otherLocationId : moreLocationIds) {
            if (isOrHasVisiblyRecursiveLocation((IGameObject) load(gameObjectId),
                    otherLocationId)) {
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
    public boolean isOrHasVisiblyRecursiveLocation(@Nullable final IGameObject gameObject,
                                                   final GameObjectId locationId,
                                                   // In der API
                                                   // isOrHasVisiblyRecursiveLocation(gameObject)
                                                   // verhindern.
                                                   final GameObjectId... moreLocationIds) {
        if (gameObject == null) {
            return false;
        }

        if (gameObject.getId().equals(locationId)) {
            return true;
        }

        final GameObject location = load(locationId);

        if (LocationSystem.isOrHasVisiblyRecursiveLocation(gameObject, location)) {
            return true;
        }

        for (final GameObjectId otherLocationId : moreLocationIds) {
            if (isOrHasVisiblyRecursiveLocation(gameObject, otherLocationId)) {
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
        return isOrHasRecursiveLocation((IGameObject) load(gameObjectId), locationId);
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

        return LocationSystem.isOrHasRecursiveLocation(gameObject, location);
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

        return LocationSystem.isOrHasRecursiveLocation(load(oneId), other);
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
     * an dieser Location befinden, auch rekursiv, soweit man
     * man in Dinge hineinsehen kann.
     * (Rekursion ebenfalls gemäß dem mentalen Modell: wenn das {@code IHasMentalModelGO}
     * davon ausgeht, dass hier ein Tisch steht, dann wird auch die Kugel zurückgeben, von der
     * er ausgeht, dass sie auf dem Tisch liegt).
     */
    public static <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadAssumedDescribableVisiblyRecursiveInventory(
            final IHasMentalModelGO hasMentalModelGO,
            final GameObjectId locationId) {
        return (ImmutableList<LOC_DESC>) (ImmutableList<?>)
                hasMentalModelGO.mentalModelComp()
                        .getAssumedVisiblyRecursiveInventory(locationId)
                        .stream()
                        .filter(locatable -> !locatable.is(SPIELER_CHARAKTER))
                        .filter(IDescribableGO.class::isInstance)
                        .collect(toImmutableList());
    }

    /**
     * Lädt (soweit noch nicht geschehen) die nicht-lebenden Game Objects an dieser
     * <code>location</code> - auch rekursiv enthaltene, z.B. Kugel auf dem Tisch im Raum, -
     * soweit sie sichtbar sind (nicht z.B. eine Kugel unter dem Bett im Raum)
     * und gibt sie zurück - nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingVisiblyRecursiveInventory(
            final ILocationGO location) {
        return AliveSystem.filterNoLivingBeing(loadDescribableVisiblyRecursiveInventory(location));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * an denen etwas abgelegt werden kann an diesem Ort (auch rekursiv), lädt sie
     * (sofern noch nicht geschehen)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOCATABLE_DESC_LOCATION extends ILocatableLocationGO & IDescribableGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> loadDescribableNonLivingLocationVisiblyRecursiveInventory(
            final ILocationGO inventoryHolder) {
        return loadDescribableNonLivingLocationVisiblyRecursiveInventory(inventoryHolder.getId());
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * an denen etwas abgelegt werden kann an diesem Ort (auch rekursiv), lädt sie
     * (sofern noch nicht geschehen)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    private <LOCATABLE_DESC_LOCATION extends ILocatableLocationGO & IDescribableGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> loadDescribableNonLivingLocationVisiblyRecursiveInventory(
            final GameObjectId inventoryHolderId) {
        return filterLocation(
                loadDescribableNonLivingVisiblyRecursiveInventory(inventoryHolderId));
    }

    /**
     * Ermittelt die nicht-lebenden Game Objects,
     * an denen etwas abgelegt werden kann (<i>nicht</i>> rekursiv), lädt sie
     * (sofern noch nicht geschehen)
     * und gibt sie zurück -
     * nur Gegenstände, die eine Beschreibung haben.
     */
    public <LOCATABLE_DESC_LOCATION extends ILocatableLocationGO & IDescribableGO>
    ImmutableList<LOCATABLE_DESC_LOCATION> loadDescribableNonLivingLocationInventory(
            final ILocationGO inventoryHolder) {
        return filterLocation(
                loadDescribableNonLivingInventory(inventoryHolder.getId()));
    }

    private static <LOC_DESC extends ILocatableGO & IDescribableGO,
            LOCATABLE_DESC_LOCATION extends ILocatableLocationGO & IDescribableGO>
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
    ImmutableList<MOV> loadMovingBeingsMovingDescribableVisiblyRecursiveInventory(
            final GameObjectId locationId) {
        return MovementSystem.filterMoving(
                filterMovingGO(loadDescribableVisiblyRecursiveInventory(locationId)));
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
     * <i>locationId</i> zurück - auch rekursiv, z.B. Kugel auf einem Tisch in einem Raum,
     * soweit sie sichtbar sind (also z.B. keine Kugel unter dem Bett im Raum) -
     * und gibt sie zurück - nur Gegenstände, die eine Beschreibung haben.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableNonLivingVisiblyRecursiveInventory(
            final GameObjectId locationId) {
        return AliveSystem
                .filterNoLivingBeing(loadDescribableVisiblyRecursiveInventory(locationId));
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
     * <code>location</code>s - auch rekursiv enthaltene, z.B. Kugel auf einem Tisch in einem Raum,
     * sofern sich sichtbar sind (also z.B. keine Kugel unter dem Bett im Raum) -
     * und gibt sie zurück - nur Gegenstände, die eine Beschreibung haben.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableVisiblyRecursiveInventory(
            final ILocationGO location) {
        return loadDescribableVisiblyRecursiveInventory(location.getId());
    }

    /**
     * Lädt (soweit noch nicht geschehen) alle Game Objects an dieser
     * <code>location</code>s - auch rekursiv enthaltene, z.B. Kugel auf einem Tisch in einem Raum -
     * und gibt sie zurück - nur Gegenstände, die eine Beschreibung haben.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableRecursiveInventory(
            final ILocationGO location) {
        return loadDescribableRecursiveInventory(location.getId());
    }

    /**
     * Lädt (soweit noch nicht geschehen) die Game Objects an dieser
     * <code>locationId</code> - auch rekursiv, z.B. Kugel auf Tisch in Raum, sofern
     * sie sichtbar sind (nicht die Kugel unter dem Bett im Raum) -
     * und gibt sie zurück - nur Game Objects, die eine Beschreibung haben,
     * nicht den Spieler-Charakter.
     */
    public <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableVisiblyRecursiveInventory(
            final GameObjectId locationId) {
        final ImmutableList.Builder<LOC_DESC> res = ImmutableList.builder();

        final ImmutableList<LOC_DESC> directlyContainedList =
                loadDescribableInventory(locationId);
        res.addAll(directlyContainedList);

        for (final LOC_DESC directlyContained : directlyContainedList) {
            if (LocationSystem.manKannHinsehenUndLichtScheintHineinUndHinaus(directlyContained)) {
                res.addAll(
                        loadDescribableVisiblyRecursiveInventory((ILocationGO) directlyContained));
            }
        }

        return res.build();
    }

    /**
     * Lädt (soweit noch nicht geschehen) die Game Objects an dieser
     * <code>locationId</code> - auch rekursiv, z.B. Kugel auf Tisch in Raum -
     * und gibt sie zurück - nur Game Objects, die eine Beschreibung haben,
     * nicht den Spieler-Charakter.
     */
    private <LOC_DESC extends ILocatableGO & IDescribableGO>
    ImmutableList<LOC_DESC> loadDescribableRecursiveInventory(
            final GameObjectId locationId) {
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

        return load(movementSystem.findWerDenSCGeradeVerlassenHat(toId));
    }

    /**
     * (Speichert ggf. alle Änderungen und) lädt (soweit noch nicht geschehen) die
     * Objekte von diesem GameObjectType
     * (z.B. nur "ausgerupfte-Binsen"-Objekte) an dieser <code>locationId</code> (<i>nicht</i>
     * rekursiv, also <i>keine</i>  ausgerupften Binsen auf einem Tisch in einem Raum)
     * und gibt sie zurück.
     */
    public <LOC_TYPED extends ILocatableGO & ITypedGO> ImmutableList<LOC_TYPED> loadTypedInventory(
            final GameObjectId locationId, final GameObjectType gameObjectType) {
        saveAll(false);

        final ImmutableList<GameObject> res =
                locationSystem.findByLocation(locationId).stream()
                        .filter(((Predicate<GameObjectId>) SPIELER_CHARAKTER::equals).negate())
                        .map(this::get)
                        .filter(ITypedGO.class::isInstance)
                        .filter(go -> ((ITypedGO) go).typeComp().hasType(gameObjectType))
                        .collect(toImmutableList());
        return loadGameObjects(res);
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
        return getPOVDescription((IGameObject) load(observerId), describable, shortIfKnown);
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
        final boolean known = observer.memoryComp().isKnown(describable);

        if ((describable instanceof IAmountableGO)
                && describable.descriptionComp() instanceof AmountDescriptionComp) {
            return ((AmountDescriptionComp) describable.descriptionComp())
                    .getDescription(
                            ((IAmountableGO) describable).amountComp().getAmount(),
                            known, shortIfKnown);
        }

        return describable.descriptionComp().getDescription(
                known, shortIfKnown);
    }

    public static EinzelneSubstantivischePhrase getDescriptionAtFirstSight(
            final IDescribableGO describable) {
        if ((describable instanceof IAmountableGO)
                && describable.descriptionComp() instanceof AmountDescriptionComp) {
            return ((AmountDescriptionComp) describable.descriptionComp())
                    .getDescriptionAtFirstSight(
                            ((IAmountableGO) describable).amountComp().getAmount());
        }

        return describable.descriptionComp().getDescriptionAtFirstSight();
    }

    public final boolean hasSameOuterMostLocationAsSC(@Nullable final GameObjectId gameObjectId) {
        return hasSameOuterMostLocationAsSC((IGameObject) load(gameObjectId));
    }

    public final boolean hasSameVisibleOuterMostLocationAsSC(
            @Nullable final GameObjectId gameObjectId) {
        return hasSameVisibleOuterMostLocationAsSC((IGameObject) load(gameObjectId));
    }

    public final boolean hasSameOuterMostLocationAsSC(@Nullable final IGameObject gameObject) {
        if (gameObject == null) {
            return false;
        }

        return loadSC().locationComp().hasSameOuterMostLocationAs(gameObject);
    }

    public final boolean hasSameVisibleOuterMostLocationAsSC(
            @Nullable final IGameObject gameObject) {
        if (gameObject == null) {
            return false;
        }

        return loadSC().locationComp().hasSameVisibleOuterMostLocationAs(gameObject);
    }

    public void narrateAndUpgradeScKnownAndAssumedState(
            final Iterable<? extends IGameObject> objects) {
        for (final IGameObject object : objects) {
            narrateAndUpgradeScKnownAndAssumedState(object);
        }
    }

    public final void narrateAndUpgradeScKnownAndAssumedState(final GameObjectId gameObjectId) {
        narrateAndUpgradeScKnownAndAssumedState((IGameObject) load(gameObjectId));
    }

    public final void narrateAndUpgradeScKnownAndAssumedState(final IGameObject gameObject) {
        loadSC().memoryComp().narrateAndUpgradeKnown(gameObject);

        if (gameObject instanceof IHasStateGO<?>) {
            loadSC().mentalModelComp().setAssumedStateToActual((IHasStateGO<?>) gameObject);
        }
    }

    /**
     * Lädt (sofern nicht schon geschehen) den Spieler-Charakter und gibt ihn zurück.
     */
    public @Nonnull
    SpielerCharakter loadSC() {
        prepare();

        return load(SPIELER_CHARAKTER);
    }

    /**
     * Lädt (sofern nicht schon geschehen) das Wetter-Game-Object und gibt es zurück.
     */
    public @Nonnull
    Wetter loadWetter() {
        prepare();

        return load(WETTER);
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
     * Lädt (sofern nicht schon geschehen) dieses Game Object und gibt es zurück - null-safe.
     */
    @Nullable
    public <T extends IGameObject> T load(@Nullable final GameObjectId id) {
        if (id == null) {
            return null;
        }

        final GameObject gameObject = get(id);
        gameObject.load();
        return (T) gameObject;
    }

    /**
     * Speichert für alle Game Objects ihre aktuellen Daten in die Datenbank - löscht
     * außerdem alle Daten aus alle geladenen Daten aus dem Speicher.
     */
    public void saveAll(final boolean unload) {
        prepare();

        for (final GameObject gameObject : toBeDeleted.values()) {
            gameObject.delete();
        }
        toBeDeleted = new GameObjectIdMap();

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
