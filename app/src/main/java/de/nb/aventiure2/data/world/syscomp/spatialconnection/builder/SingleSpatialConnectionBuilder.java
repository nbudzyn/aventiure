package de.nb.aventiure2.data.world.syscomp.spatialconnection.builder;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.data.world.time.Tageszeit;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.HINTER_DER_HUETTE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.VOR_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

class SingleSpatialConnectionBuilder {
    private final AvDatabase db;

    private final SpielerCharakter sc;

    private final ISpatiallyConnectedGO from;

    SingleSpatialConnectionBuilder(final AvDatabase db, final ISpatiallyConnectedGO from) {
        this.db = db;
        sc = loadSC(db);
        this.from = from;
    }

    List<SpatialConnection> getConnections() {
        // TODO Meldungen auf from-Räume aufteilen in der Art
        //  schloss::getDesc_DraussenVorDemSchloss().
        //  from-Räume (SchlossConnectionBuilder etc.) von AbstractRoomConnectionBuilder ableiten.

        if (from.is(SCHLOSS_VORHALLE)) {
            return getSpatialConnectionsSchlossVorhalle();
        }
        if (from.is(SCHLOSS_VORHALLE_TISCH_BEIM_FEST)) {
            return ImmutableList.of(
                    SpatialConnection.con(SCHLOSS_VORHALLE,
                            "Vom Tisch aufstehen",
                            SingleSpatialConnectionBuilder::getDesc_SchlossVorhalleTischBeimFest_SchlossVorhalle));
        }
        if (from.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return ImmutableList.of(

                    SpatialConnection.con(SCHLOSS_VORHALLE,
                            "Das Schloss betreten",
                            this::getDesc_DraussenVorDemSchloss_SchlossVorhalle),

                    SpatialConnection.con(IM_WALD_NAHE_DEM_SCHLOSS,
                            "In den Wald gehen",
                            du("folgst", "einem Pfad in den Wald", mins(10))
                                    .undWartest()
                                    .dann(),

                            neuerSatz("Jeder kennt die Geschichten, die man "
                                    + "sich über den Wald erzählt: Räuber sind noch "
                                    + "die kleinste Gefahr. Aber das schreckt dich ganz "
                                    + "offenbar nicht und du folgst dem erstbesten "
                                    + "Pfad hinein in den dunklen Wald", mins(12)),

                            du("läufst", "wieder in den dunklen Wald", "wieder", mins(10))
                                    .undWartest()
                                    .dann(),

                            du("läufst", "wieder in den dunklen Wald", "wieder", mins(10))
                                    .undWartest()
                                    .dann()));
        }
        if (from.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            return ImmutableList.of(
                    SpatialConnection.con(DRAUSSEN_VOR_DEM_SCHLOSS,
                            "Den Wald verlassen",
                            this::getDesc_ImWaldNaheDemSchloss_DraussenVorDemSchloss),
                    SpatialConnection.con(ABZWEIG_IM_WALD,
                            "Tiefer in den Wald hineingehen",
                            neuerSatz("Nicht lang, und zur Linken geht zwischen "
                                    + "den Bäumen ein alter, düsterer Weg ab, über "
                                    + "den Farn wuchert", mins(5))
                                    .komma(),
                            du("kommst", "an den farnüberwachsenen Abzweig", mins(5))
                                    .undWartest()
                    ));
        }
        if (from.is(ABZWEIG_IM_WALD)) {
            // Verhindert "Von dort gehst du weiter in Richtung
            // Schloss und gehst noch eine Weile vorsichtig durch
            // den dunklen Wald..."
            return ImmutableList.of(

                    SpatialConnection.con(IM_WALD_NAHE_DEM_SCHLOSS,
                            "In Richtung Schloss gehen",
                            neuerSatz("Von dort gehst du weiter in Richtung Schloss", mins(5))
                    ),

                    SpatialConnection.con(VOR_DER_HUETTE_IM_WALD,
                            "Den überwachsenen Abzweig nehmen",
                            du(SENTENCE, "fasst",
                                    "dir ein Herz und stapfst zwischen "
                                            + "dem Unkraut einen Weg entlang, "
                                            + "der wohl schon länger nicht mehr benutzt wurde. Hinter der "
                                            + "nächsten Biegung stehst du unvermittelt vor"
                                            + " einer Holzhütte. "
                                            + "Die Fensterläden sind "
                                            + "geschlossen, die Tür hängt nur noch lose "
                                            + "in den Angeln", mins(2)),

                            neuerSatz("Hat gerade neben dir im Unterholz geknarzt? "
                                    + "Wie auch immer, du fasst dir ein Herz und "
                                    + "stapfst durch das "
                                    + "dem Unkraut einen düsteren Trampelpfad entlang. "
                                    + "Hinter der "
                                    + "nächsten Biegung stehst du unvermittelt vor"
                                    + " der Tür einer Holzhütte. "
                                    + "Die Tür hängt nur noch lose "
                                    + "in den Angeln", mins(2)),

                            du("wählst", "noch einmal den überwachsenen "
                                    + "Pfad zur Hütte. Es wirkt alles so, also sei "
                                    + "er schon lange nicht mehr benutzt worden", mins(2))
                                    .komma()
                                    .dann(),

                            du("wählst", "noch einmal den überwachsenen "
                                    + "Pfad zur Hütte", mins(2))
                                    .undWartest()
                    ),

                    SpatialConnection.con(IM_WALD_BEIM_BRUNNEN,
                            "Auf dem Hauptpfad tiefer in den Wald gehen",
                            neuerSatz("Der breitere Pfad führt zu einer alten "
                                    + "Linde, unter der ist ein Brunnen. "
                                    + "Hinter dem Brunnen endet der Weg und der "
                                    + "wilde Wald beginnt.\n"
                                    + "Du setzt "
                                    + "dich an den Brunnenrand – "
                                    + "hier ist es "
                                    + "angenehm kühl", mins(5))
                                    .dann(),

                            du("gehst", "den breiteren Pfad weiter in "
                                    + "den Wald hinein. Wohl ist dir dabei nicht.\n"
                                    + "In der Ferne heult ein Wolf – oder hast du "
                                    + "dir das eingebildet?\nDann kommst du an einen "
                                    + "Baum, unter dem ist ein Brunnen. Kühl ist es "
                                    + "hier, und der Weg scheint zu Ende zu sein", mins(10)),

                            du("kehrst", "zurück zum Brunnen – unter einer Linde, wie "
                                    + "du bei Licht erkennen kannst. Hinter dem "
                                    + "Brunnen beginnt der wilde Wald", mins(4))
                                    .komma(),

                            du("kehrst", "zurück zum Brunnen", mins(3))
                                    .undWartest()
                                    .dann()));
        }
        if (from.is(VOR_DER_HUETTE_IM_WALD)) {
            return ImmutableList.of(
                    SpatialConnection.con(ABZWEIG_IM_WALD,
                            "Auf den Hauptpfad zurückkehren",
                            neuerSatz("Durch Farn und Gestrüpp gehst du zurück zum "
                                    + "Hauptpfad", mins(2))
                                    .undWartest()
                                    .dann()
                    ),
                    SpatialConnection.con(HUETTE_IM_WALD,
                            "Die Hütte betreten",
                            neuerSatz("Du schiebst die Tür zur Seite und "
                                    + "zwängst dich hinein. Durch Ritzen in den "
                                    + "Fensterläden fällt ein wenig Licht: "
                                    + "Die Hütte ist "
                                    + "anscheinend trocken und, wie es aussieht, "
                                    + "bis auf einige "
                                    + "Tausendfüßler "
                                    + "unbewohnt. Du siehst ein Bettgestell, "
                                    + "einen Tisch, aber sonst keine Einrichtung", mins(1)),
                            neuerSatz("Du schiebst die Tür zur Seite und "
                                    + "zwängst dich hinein. Erst ist alles "
                                    + "stockdunkel, aber dann kannst du doch mit "
                                    + "Mühe ein Bettgestell und einen Tisch "
                                    + "ausmachen", secs(90)),
                            du("schiebst", "dich noch einmal in die "
                                    + "kleine Hütte. Durch Ritzen in den "
                                    + "Fensterläden fällt ein wenig Licht: "
                                    + "Die Hütte ist "
                                    + "anscheinend trocken und, wie es aussieht, "
                                    + "bis auf einige "
                                    + "Tausendfüßler "
                                    + "unbewohnt. Du siehst ein Bettgestell, "
                                    + "einen Tisch, aber sonst keine Einrichtung", mins(1))
                                    .komma()
                                    .undWartest(),
                            du("schiebst", "dich noch einmal in die "
                                    + "kleine Hütte, in der es außer Tisch und "
                                    + "Bett wenig zu sehen gibt", secs(15))
                                    .komma()
                                    .undWartest()),
                    SpatialConnection.con(HINTER_DER_HUETTE,
                            "Um die Hütte herumgehen",
                            neuerSatz("Ein paar Schritte um die Hütte herum und "
                                    + "du kommst in einen kleinen, völlig "
                                    + "verwilderten Garten. In seiner Mitte "
                                    + "steht einzeln… es könnte ein "
                                    + "Apfelbaum sein. Früchte siehst du von "
                                    + "unten keine.", secs(30)),
                            neuerSatz("Vorsichtig gehst du im Dunkeln ein paar Schritte "
                                    + "um die Hütte herum. Du kannst die Silhuette "
                                    + "eines einzelnen Baums erkennen, vielleicht – "
                                    + "ein Apfelbaum", mins(1)),
                            du("schaust", "noch einmal hinter die Hütte. "
                                    + "Im Licht erkennst du dort einen kleinen, völlig "
                                    + "verwilderten Garten mit dem einzelnen Baum in "
                                    + "der Mitte", secs(30)),
                            du("schaust", "noch einmal in den alten "
                                    + "Garten hinter der Hütte, wo der "
                                    + "Baum wächst", secs(30))
                                    .komma()
                                    .undWartest()
                                    .dann()
                    ));
        }
        if (from.is(HUETTE_IM_WALD)) {
            // STORY Spieler richtet Hütte gemütlich ein. Hütte ist gegen Wölfe etc. geschützt.

            return ImmutableList.of(
                    SpatialConnection.con(VOR_DER_HUETTE_IM_WALD,
                            "Die Hütte verlassen",
                            du("zwängst", "dich wieder durch die Tür nach "
                                    + "draußen", secs(15))
                                    .undWartest()
                                    .dann()
                    ),

                    SpatialConnection.con(BETT_IN_DER_HUETTE_IM_WALD,
                            "In das Bett legen",
                            du("legst", "dich in das hölzere Bettgestell. "
                                    + "Gemütlich ist etwas anderes, aber nach den "
                                    + "vielen Schritten tut es sehr gut, sich "
                                    + "einmal auszustrecken", secs(15)),

                            neuerSatz("Noch einmal legst du dich in das Holzbett", secs(15))
                                    .undWartest()
                                    .dann()
                    ));
        }
        if (from.is(HINTER_DER_HUETTE)) {
            return ImmutableList.of(
                    SpatialConnection.con(VOR_DER_HUETTE_IM_WALD,
                            "Zur Vorderseite der Hütte gehen",
                            du("kehrst", "zurück zur Vorderseite der "
                                    + "Hütte", secs(15))
                                    .undWartest()
                                    .dann()
                    )
            );
        }
        if (from.is(BETT_IN_DER_HUETTE_IM_WALD)) {
            return ImmutableList.of(SpatialConnection.con(HUETTE_IM_WALD,
                    "Aufstehen",
                    du(SENTENCE, "reckst", "dich noch einmal und stehst "
                            + "wieder auf", secs(10))
                            .dann()
            ));
        }
        if (from.is(IM_WALD_BEIM_BRUNNEN)) {
            final ImmutableList.Builder<SpatialConnection> resImWaldBeimBrunnnen =
                    ImmutableList.builder();

            resImWaldBeimBrunnnen.add(SpatialConnection.con(ABZWEIG_IM_WALD,
                    "Den Weg Richtung Schloss gehen",
                    du(SENTENCE, "verlässt", "den Brunnen und erreichst bald "
                            + "die Stelle, wo der überwachsene Weg "
                            + "abzweigt", mins(3))
                            .komma()));
            if (getLichtverhaeltnisseFrom() == HELL ||
                    sc.memoryComp().isKnown(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
                resImWaldBeimBrunnnen.add(SpatialConnection.con(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                        "Hinter dem Brunnen in die Wildnis schlagen",
                        du(SENTENCE, "verlässt", "den Brunnen und schlägst dich in die "
                                + "Wildnis "
                                + "hinter dem "
                                + "Brunnen. Umgestürzte Bäume, abgefallene "
                                + "Äste, modriger Grund – es ist schwer, durch "
                                + "diese Wildnis voranzukommen. "
                                + "Nicht weit in den Wald, und dir fällt ein "
                                + "Strauch mit kleinen, "
                                + "purpurnen Früchten auf, wie zu klein geratene "
                                + "Äpfel", mins(5))
                                .komma()
                                .dann(),
                        du("kämpfst", "dich noch einmal durch den wilden "
                                        + "Wald hinter dem Brunnen, bis du den Strauch mit den "
                                        + "kleinen, violetten Früchten erreichst",
                                "noch einmal",
                                mins(4))
                                .komma()
                                .undWartest()
                                .dann()));
            }

            return resImWaldBeimBrunnnen.build();
        }
        if (from.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            //allg(
            // "Durch den wilden Wald suchst du dir einen Weg"
            return ImmutableList.of(SpatialConnection.con(IM_WALD_BEIM_BRUNNEN,
                    "Zum Brunnen gehen",
                    du("suchst", "dir einen Weg "
                                    + "durch den wilden Wald zurück zum Brunnen", "durch den wilden Wald",
                            mins(3))
                            .undWartest()
                            .dann()
            ));
            // STORY Nächster Raum: "wirst du von einer dichten Dornenhecke
            //  zurückgehalten"
        }

        throw new IllegalStateException("Unexpected from: " + from);

    }

    private List<SpatialConnection> getSpatialConnectionsSchlossVorhalle() {
        final ImmutableList.Builder<SpatialConnection> resSchlossVorhalle =
                ImmutableList.builder();
        resSchlossVorhalle.add(SpatialConnection.con(DRAUSSEN_VOR_DEM_SCHLOSS,
                "Das Schloss verlassen",
                this::getDesc_SchlossVorhalle_DraussenVorDemSchloss));
        if (((IHasStateGO) load(db, SCHLOSSFEST)).stateComp().hasState(BEGONNEN)) {
            resSchlossVorhalle.add(SpatialConnection.con(SCHLOSS_VORHALLE_TISCH_BEIM_FEST,
                    "An einen Tisch setzen",
                    this::getDesc_SchlossVorhalle_SchlossVorhalleTischBeimFest));
        }
        return resSchlossVorhalle.build();
    }

// -------------------------------------------------------------------
// --- SCHLOSS_VORHALLE
// -------------------------------------------------------------------


    private AbstractDescription getDesc_SchlossVorhalle_DraussenVorDemSchloss(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (((IHasStateGO) load(db, SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDesc_SchlossVorhalle_DraussenVorDemSchloss_FestBegonnen();

            default:
                return getDesc_SchlossVorhalle_DraussenVorDemSchlosss_KeinFest(newRoomKnown,
                        lichtverhaeltnisse);
        }
    }

    private static AbstractDescription getDesc_SchlossVorhalle_DraussenVorDemSchlosss_KeinFest(
            final Known known, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (known == UNKNOWN) {
            return getDesc_SchlossVorhalle_DraussenVorDemSchlosss_KeinFest_Unknown(
                    lichtverhaeltnisse);
        }

        if (known == KNOWN_FROM_DARKNESS && lichtverhaeltnisse == HELL) {
            // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
            //  noch nicht?
            return du("verlässt", "das Schloss. Draußen scheint dir die " +
                    "Sonne ins Gesicht; "
                    // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
                    //  noch nicht?
                    + "der Tag ist recht heiß", mins(1));
        }

        // STORY: Wenn man aus dem hellen (Schloss) ins Dunkle kommt:
        //  "Draußen ist es dunkel" o.Ä.

        return du("verlässt", "das Schloss", mins(1))
                .undWartest()
                .dann();
    }

    @NonNull
    private static AbstractDescription
    getDesc_SchlossVorhalle_DraussenVorDemSchlosss_KeinFest_Unknown(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == HELL) {
            // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
            //  noch nicht?
            return du("gehst", "über eine Marmortreppe hinaus in die Gärten vor dem Schloss.\n\n" +
                            "Draußen scheint dir die " +
                            "Sonne ins Gesicht; "
                            // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
                            //  noch nicht?
                            + "der Tag ist recht heiß. " +
                            "Nahebei liegt ein großer, dunkler Wald", "über eine Marmortreppe",
                    mins(1));
        }

        return du("gehst", "über eine Marmortreppe hinaus den Garten vor dem Schloss.\n\n" +
                        "Draußen ist es dunkel. " +
                        "In der Nähe liegt ein großer Wald, der sehr bedrohlich wirkt",
                "über eine Marmortreppe", mins(1))
                .komma();
    }

    @NonNull
    private static AbstractDescription
    getDesc_SchlossVorhalle_DraussenVorDemSchloss_FestBegonnen() {
        // STORY: Nachts ist weniger Trubel? (Wäre das ein Statuswechsel beim
        //  Schlossfest? Oder Zumindest auch eine Reaction wie der Auf- /
        //  Abbau des Schlossfestes?)
        return du("gehst",
                "über die Marmortreppe hinaus in den Trubel "
                        + "im Schlossgarten",
                // TODO Nach dem "Trubel" funktioniert "Aus Langeweile..." nicht mehr.
                //  Stimmung des Spielers verbessern? ("Aufgedreht"? "Aufgekratzt"?)
                "über die Marmortreppe",
                mins(3))
                .dann();
    }

    private AbstractDescription getDesc_SchlossVorhalle_SchlossVorhalleTischBeimFest(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (db.counterDao().incAndGet(
                "RoomConnectionBuilder_SchlossVorhalle_SchlossVorhalleTischBeimFest")
                == 1) {
            return du("ergatterst", "einen Platz auf einer Bank.\n"
                    + "Unter einem Baldachin sitzen – soweit du durch das Gedänge "
                    + "erkennen kannst – "
                    + "einige Hofleute an einer Tafel mit "
                    + "goldenen Tellern vor Fasan und anderem Wildbret. "
                    + "Immerhin stellt "
                    + "dir ein eifriger Diener einen leeren Holzteller und einen "
                    + "Löffel bereit", mins(3));
        }

        return du("suchst", "dir erneut im Gedränge einen Platz an einem Tisch", "erneut", mins(3));
    }

// -------------------------------------------------------------------
// --- SCHLOSS_VORHALLE_TISCH_BEIM_FEST
// -------------------------------------------------------------------

    private static AbstractDescription getDesc_SchlossVorhalleTischBeimFest_SchlossVorhalle(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        return du("stehst", "vom Tisch auf", mins(3))
                .undWartest()
                .dann();
    }

// -------------------------------------------------------------------
// --- DRAUSSEN_VOR_DEM_SCHLOSS
// -------------------------------------------------------------------

    private AbstractDescription getDesc_DraussenVorDemSchloss_SchlossVorhalle(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (((IHasStateGO) load(db, SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDesc_DraussenVorDemSchloss_SchlossVorhalle_FestBegonnen();

            default:
                return getDesc_DraussenVorDemSchloss_SchlossVorhalle_KeinFest();
        }
    }

    @NonNull
    private static AbstractDescription
    getDesc_DraussenVorDemSchloss_SchlossVorhalle_KeinFest() {
        return du("gehst", "wieder hinein in das Schloss", mins(1))
                .undWartest()
                .dann();
    }

    private AbstractDescription
    getDesc_DraussenVorDemSchloss_SchlossVorhalle_FestBegonnen() {
        if (db.counterDao().incAndGet(
                "RoomConnectionBuilder_DraussenVorDemSchloss_SchlossVorhalle_FestBegonnen")
                == 1) {
            return neuerSatz("Vor dem Schloss gibt es ein großes Gedränge und es dauert "
                    + "eine Weile, bis "
                    + "die Menge dich hineinschiebt. Die prächtige Vorhalle steht voller "
                    + "Tische, auf denen in großen Schüsseln Eintöpfe dampfen", mins(7))
                    .komma();
        }

        return du("betrittst", "wieder das Schloss", "wieder", mins(2))
                .undWartest()
                .dann();
    }

// -------------------------------------------------------------------
// --- IM_WALD_NAHE_DEM_SCHLOSS
// -------------------------------------------------------------------

    private AbstractDescription getDesc_ImWaldNaheDemSchloss_DraussenVorDemSchloss(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {

        switch (((IHasStateGO) load(db, SCHLOSSFEST)).stateComp().getState()) {
            case BEGONNEN:
                return getDesc_ImWaldNaheDemSchloss_DraussenVorDemSchloss_FestBegonnen(
                        mins(10));

            default:
                return getDesc_ImWaldNaheDemSchloss_DraussenVorDemSchloss_KeinFest(
                        lichtverhaeltnisse);
        }
    }

    @NonNull
    private static AbstractDescription
    getDesc_ImWaldNaheDemSchloss_DraussenVorDemSchloss_KeinFest(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == HELL) {
            return du("erreichst", "bald das helle "
                    + "Tageslicht, in dem der Schlossgarten "
                    + "liegt", "bald", mins(10))
                    .undWartest()
                    .komma();
        }

        return du(SENTENCE, "gehst", "noch eine Weile vorsichtig durch den dunklen "
                + "Wald, dann öffnet sich der Weg wieder und du stehst im Schlossgarten "
                + "unter dem Sternenhimmel", "noch eine Weile", mins(15));
        // STORY Lichtverhältnisse auch bei den anderen Aktionen berücksichtigen,
        //  insbesondere nach derselben Logik (z.B. "im Schloss ist es immer hell",
        //  "eine Fackel bringt auch nachts Licht" etc.)
        // STORY gegen Abend wird man müde und kann auf jeden Fall einschlafen
        // STORY Wenn man schläft, "verpasst" man Reactions, die man dann später
        //  (beim Aufwachen) merkt ("Der Frosch ist verschwunden".) Man könnte alle
        //  verpassten Reactions als Texte speichern (Problem, wenn der Frosch nur kurz
        //  verschwundenn ist), inhaltlich speichern
        //  (WasIstAllesPassiert.FroschIstVerschwunden = true), oder man speichert
        //  den Stand VOR dem Einschlafen und vergleicht mit dem Stand NACH dem
        //  Einschlafen.
        // STORY Nachts sieht man nicht so gut - sieht man alle Objects?
        // STORY Nachts schlafen die Creatures?
        // STORY Nachts ist man hauptsächlich MUEDE / ERSCHOEPFT
    }

    @NonNull
    private AbstractDescription
    getDesc_ImWaldNaheDemSchloss_DraussenVorDemSchloss_FestBegonnen
            (
                    final AvTimeSpan timeSpan) {
        if (db.counterDao().incAndGet(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN) == 1) {
            return du("bist", "von dem Lärm überrascht, der dir "
                    + "schon von weitem "
                    + "entgegenschallt. Als du aus dem Wald heraustrittst, "
                    + "ist der Anblick überwältigend: "
                    + "Überall im Schlossgarten stehen kleine Pagoden "
                    + "in lustigen Farben. Kinder werden auf Kähnen durch Kanäle "
                    + "gestakt und aus dem Schloss duftet es verführerisch nach "
                    + "Gebratenem", timeSpan);
        }

        return neuerSatz("Das Schlossfest ist immer noch in vollem Gange", timeSpan);
    }

// -------------------------------------------------------------------
// --- Allgemein
// -------------------------------------------------------------------

    private Lichtverhaeltnisse getLichtverhaeltnisseFrom() {
        final Tageszeit tageszeit = db.dateTimeDao().now().getTageszeit();
        return Lichtverhaeltnisse.getLichtverhaeltnisse(tageszeit, from.getId());
    }
}
