package de.nb.aventiure2.scaction.action.room.connection;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.invisible.Invisible;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.RoomKnown;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.data.world.time.Tageszeit;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.invisible.InvisibleState.BEGONNEN;
import static de.nb.aventiure2.data.world.invisible.Invisibles.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.room.RoomKnown.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.room.RoomKnown.UNKNOWN;
import static de.nb.aventiure2.data.world.room.Rooms.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.room.Rooms.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.Rooms.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.Rooms.HINTER_DER_HUETTE;
import static de.nb.aventiure2.data.world.room.Rooms.HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.Rooms.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.room.Rooms.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.Rooms.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.room.Rooms.SCHLOSS_VORHALLE_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.room.Rooms.VOR_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.Rooms.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.allg;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.scaction.action.room.connection.RoomConnection.con;

class RoomConnectionBuilder {
    private final AvDatabase db;
    private final AvRoom from;

    RoomConnectionBuilder(final AvDatabase db, final AvRoom from) {
        this.db = db;
        this.from = from;
    }

    List<RoomConnection> getConnections() {
        // TODO Meldungen auf from-Räume aufteilen in der Art
        //  schloss::getDesc_DraussenVorDemSchloss().
        //  from-Räume (SchlossConnectionBuilder etc.) von AbstractRoomConnectionBuilder ableiten.

        if (from.is(SCHLOSS_VORHALLE)) {
            final ImmutableList.Builder<RoomConnection> resSchlossVorhalle =
                    ImmutableList.builder();
            resSchlossVorhalle.add(con(DRAUSSEN_VOR_DEM_SCHLOSS,
                    "Das Schloss verlassen",
                    this::getDesc_SchlossVorhalle_DraussenVorDemSchloss));
            if (db.invisibleDataDao().getInvisible(Invisible.SCHLOSSFEST)
                    .hasState(BEGONNEN)) {
                resSchlossVorhalle.add(con(SCHLOSS_VORHALLE_TISCH_BEIM_FEST,
                        "An einen Tisch setzen",
                        this::getDesc_SchlossVorhalle_SchlossVorhalleTischBeimFest));
            }
            return resSchlossVorhalle.build();
        }
        if (from.is(SCHLOSS_VORHALLE_TISCH_BEIM_FEST)) {
            return ImmutableList.of(
                    con(SCHLOSS_VORHALLE,
                            "Vom Tisch aufstehen",
                            RoomConnectionBuilder::getDesc_SchlossVorhalleTischBeimFest_SchlossVorhalle));
        }
        if (from.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return ImmutableList.of(

                    con(SCHLOSS_VORHALLE,
                            "Das Schloss betreten",
                            this::getDesc_DraussenVorDemSchloss_SchlossVorhalle),

                    con(IM_WALD_NAHE_DEM_SCHLOSS,
                            "In den Wald gehen",
                            du(
                                    "folgst",
                                    "einem Pfad in den Wald",
                                    false,
                                    true,
                                    true,
                                    mins(10)),

                            allg(
                                    "Jeder kennt die Geschichten, die man "
                                            + "sich über den Wald erzählt: Räuber sind noch "
                                            + "die kleinste Gefahr. Aber das schreckt dich ganz "
                                            + "offenbar nicht und du folgst dem erstbesten "
                                            + "Pfad hinein in den dunklen Wald",
                                    false,
                                    false,
                                    false,
                                    mins(12)),

                            du(
                                    "läufst",
                                    "wieder in den dunklen Wald",
                                    false,
                                    true,
                                    true,
                                    mins(10)),

                            du(
                                    "läufst",
                                    "wieder in den dunklen Wald",
                                    false,
                                    true,
                                    true,
                                    mins(10))));
        }
        if (from.is(IM_WALD_NAHE_DEM_SCHLOSS)) {
            return ImmutableList.of(
                    con(DRAUSSEN_VOR_DEM_SCHLOSS,
                            "Den Wald verlassen",
                            this::getDesc_ImWaldNaheDemSchloss_DraussenVorDemSchloss),
                    con(ABZWEIG_IM_WALD,
                            "Tiefer in den Wald hineingehen",
                            allg("Nicht lang, und zur Linken geht zwischen "
                                            + "den Bäumen ein alter, düsterer Weg ab, über "
                                            + "den Farn wuchert",
                                    true,
                                    false,
                                    false,
                                    mins(5)
                            ),
                            du("kommst",
                                    "an den farnüberwachsenen Abzweig",
                                    false,
                                    true,
                                    false,
                                    mins(5)
                            )
                    ));
        }
        if (from.is(ABZWEIG_IM_WALD)) {
            return ImmutableList.of(

                    con(IM_WALD_NAHE_DEM_SCHLOSS,
                            "In Richtung Schloss gehen",
                            allg("Von dort gehst du weiter in Richtung Schloss",
                                    false,
                                    false,
                                    // Verhindert "Von dort gehst du weiter in Richtung
                                    // Schloss und gehst noch eine Weile vorsichtig durch
                                    // den dunklen Wald..."
                                    false,
                                    mins(5)
                            )
                    ),

                    con(VOR_DER_HUETTE_IM_WALD,
                            "Den überwachsenen Abzweig nehmen",
                            allg("Du fasst dir ein Herz und stapfst zwischen "
                                            + "dem Unkraut einen Weg entlang, "
                                            + "der wohl schon länger nicht mehr benutzt wurde. Hinter der "
                                            + "nächsten Biegung stehst du unvermittelt vor"
                                            + " einer Holzhütte. "
                                            + "Die Fensterläden sind "
                                            + "geschlossen, die Tür hängt nur noch lose "
                                            + "in den Angeln",
                                    false,
                                    false,
                                    false,
                                    mins(2)
                            ),

                            allg("Hat gerade neben dir im Unterholz geknarzt? "
                                            + "Wie auch immer, du fasst dir ein Herz und "
                                            + "stapfst durch das "
                                            + "dem Unkraut einen düsteren Trampelpfad entlang. "
                                            + "Hinter der "
                                            + "nächsten Biegung stehst du unvermittelt vor"
                                            + " der Tür einer Holzhütte. "
                                            + "Die Tür hängt nur noch lose "
                                            + "in den Angeln",
                                    false,
                                    false,
                                    false,
                                    mins(2)
                            ),

                            du("wählst", "noch einmal den überwachsenen "
                                            + "Pfad zur Hütte. Es wirkt alles so, also sei "
                                            + "er schon lange nicht mehr benutzt worden",
                                    true,
                                    false,
                                    true,
                                    mins(2)),

                            du("wählst", "noch einmal den überwachsenen "
                                            + "Pfad zur Hütte",
                                    false,
                                    true,
                                    false,
                                    mins(2))
                    ),

                    con(IM_WALD_BEIM_BRUNNEN,
                            "Auf dem Hauptpfad tiefer in den Wald gehen",
                            allg(
                                    "Der breitere Pfad führt zu einer alten "
                                            + "Linde, unter der ist ein Brunnen. "
                                            + "Hinter dem Brunnen endet der Weg und der "
                                            + "wilde Wald beginnt.\n"
                                            + "Du setzt "
                                            + "dich an den Brunnenrand – "
                                            + "hier ist es "
                                            + "angenehm kühl",
                                    false,
                                    false,
                                    true,
                                    mins(5)
                            ),

                            du(
                                    "gehst", "den breiteren Pfad weiter in "
                                            + "den Wald hinein. Wohl ist dir dabei nicht.\n"
                                            + "In der Ferne heult ein Wolf – oder hast du "
                                            + "dir das eingebildet?\nDann kommst du an einen "
                                            + "Baum, unter dem ist ein Brunnen. Kühl ist es "
                                            + "hier, und der Weg scheint zu Ende zu sein",
                                    false,
                                    false,
                                    false,
                                    mins(10)
                            ),

                            du("kehrst",
                                    "zurück zum Brunnen – unter einer Linde, wie "
                                            + "du bei Licht erkennen kannst. Hinter dem "
                                            + "Brunnen beginnt der wilde Wald",
                                    true,
                                    false,
                                    false,
                                    mins(4)),

                            du("kehrst",
                                    "zurück zum Brunnen unter der Linde",
                                    false,
                                    true,
                                    true,
                                    mins(3))));
        }
        if (from.is(VOR_DER_HUETTE_IM_WALD)) {
            return ImmutableList.of(
                    con(ABZWEIG_IM_WALD,
                            "Auf den Hauptpfad zurückkehren",
                            allg("Durch Farn und Gestrüpp gehst du zurück zum "
                                            + "Hauptpfad",
                                    false,
                                    true,
                                    true,
                                    mins(2)
                            )
                    ),
                    con(HUETTE_IM_WALD,
                            "Die Hütte betreten",
                            allg("Du schiebst die Tür zur Seite und "
                                            + "zwängst dich hinein. Durch Ritzen in den "
                                            + "Fensterläden fällt ein wenig Licht: "
                                            + "Die Hütte ist "
                                            + "anscheinend trocken und, wie es aussieht, "
                                            + "bis auf einige "
                                            + "Tausendfüßler "
                                            + "unbewohnt. Du siehst ein Bettgestell, "
                                            + "einen Tisch, aber sonst keine Einrichtung",
                                    false,
                                    false,
                                    false,
                                    mins(1)
                            ),
                            allg("Du schiebst die Tür zur Seite und "
                                            + "zwängst dich hinein. Erst ist alles "
                                            + "stockdunkel, aber dann kannst du doch mit "
                                            + "Mühe ein Bettgestell und einen Tisch "
                                            + "ausmachen",
                                    false,
                                    false,
                                    false,
                                    secs(90)
                            ),
                            du("schiebst", "dich noch einmal in die "
                                            + "kleine Hütte. Durch Ritzen in den "
                                            + "Fensterläden fällt ein wenig Licht: "
                                            + "Die Hütte ist "
                                            + "anscheinend trocken und, wie es aussieht, "
                                            + "bis auf einige "
                                            + "Tausendfüßler "
                                            + "unbewohnt. Du siehst ein Bettgestell, "
                                            + "einen Tisch, aber sonst keine Einrichtung",
                                    true,
                                    true,
                                    false,
                                    mins(1)
                            ),
                            du("schiebst", "dich noch einmal in die "
                                            + "kleine Hütte, in der es außer Tisch und "
                                            + "Bett wenig zu sehen gibt",
                                    true,
                                    true,
                                    false,
                                    secs(15)
                            )),
                    con(HINTER_DER_HUETTE,
                            "Um die Hütte herumgehen",
                            allg("Ein paar Schritte um die Hütte herum und "
                                            + "du kommst in einen kleinen, völlig "
                                            + "verwilderten Garten. In seiner Mitte "
                                            + "steht einzeln… es könnte ein "
                                            + "Apfelbaum sein. Früchte siehst du von "
                                            + "unten keine.",
                                    false,
                                    false,
                                    false,
                                    secs(30)
                            ),
                            allg("Vorsichtig gehtst du im Dunkeln ein paar Schritte "
                                            + "um die Hütte herum. Du kannst die Silhuette "
                                            + "eines einzelnen Baums erkennen, vielleicht – "
                                            + "ein Apfelbaum",
                                    false,
                                    false,
                                    false,
                                    mins(1)
                            ),
                            du("schaust", "noch einmal hinter die Hütte. "
                                            + "Im Licht erkennst du dort einen kleinen, völlig "
                                            + "verwilderten Garten mit dem einzelnen Baum in "
                                            + "der Mitte",
                                    false,
                                    false,
                                    false,
                                    secs(30)
                            ),
                            du("schaust", "noch einmal in den alten "
                                            + "Garten hinter der Hütte, wo der "
                                            + "Baum wächst",
                                    true,
                                    true,
                                    true,
                                    secs(30)
                            )
                    ));
        }
        if (from.is(HUETTE_IM_WALD)) {
            // STORY Spieler richtet Hütte gemütlich ein. Hütte ist gegen Wölfe etc. geschützt.

            return ImmutableList.of(

                    con(VOR_DER_HUETTE_IM_WALD,
                            "Die Hütte verlassen",
                            du("zwängst", "dich wieder durch die Tür nach "
                                            + "draußen",
                                    false,
                                    true,
                                    true,
                                    secs(15)
                            )
                    ),

                    con(BETT_IN_DER_HUETTE_IM_WALD,
                            "In das Bett legen",
                            du("legst", "dich in das hölzere Bettgestell. "
                                            + "Gemütlich ist etwas anderes, aber nach den "
                                            + "vielen Schritten tut es sehr gut, sich "
                                            + "einmal auszustrecken",
                                    false,
                                    false,
                                    false,
                                    secs(15)
                            ),

                            allg("Noch einmal legst du dich in das Holzbett",
                                    false,
                                    true,
                                    true,
                                    secs(15)
                            )
                    ));
        }
        if (from.is(HINTER_DER_HUETTE)) {
            return ImmutableList.of(

                    con(VOR_DER_HUETTE_IM_WALD,
                            "Zur Vorderseite der Hütte gehen",
                            du("kehrst", "zurück zur Vorderseite der "
                                            + "Hütte",
                                    false,
                                    true,
                                    true,
                                    secs(15)
                            )
                    )
            );
        }
        if (from.is(BETT_IN_DER_HUETTE_IM_WALD)) {
            return ImmutableList.of(

                    con(HUETTE_IM_WALD,
                            "Aufstehen",
                            allg("Du reckst dich noch einmal und stehst "
                                            + "wieder auf",
                                    false,
                                    false,
                                    true,
                                    secs(10)
                            )
                    ));
        }
        if (from.is(IM_WALD_BEIM_BRUNNEN)) {
            final ImmutableList.Builder<RoomConnection> resImWaldBeimBrunnnen =
                    ImmutableList.builder();

            resImWaldBeimBrunnnen.add(

                    con(ABZWEIG_IM_WALD,
                            "Den Weg Richtung Schloss gehen",
                            allg("Du verlässt den Brunnen und erreichst bald "
                                            + "die Stelle, wo der überwachsene Weg "
                                            + "abzweigt",
                                    true,
                                    false,
                                    false,
                                    mins(3)
                            )));
            if (

                    getLichtverhaeltnisseFrom() == HELL ||
                            db.roomDao().

                                    getKnown(WALDWILDNIS_HINTER_DEM_BRUNNEN).

                                    isKnown()) {
                resImWaldBeimBrunnnen.add(con(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                        "Hinter dem Brunnen in die Wildnis schlagen",
                        allg("Du verlässt den Brunnen und schlägst dich in die "
                                        + "Wildnis "
                                        + "hinter dem "
                                        + "Brunnen. Umgestürzte Bäume, abgefallene "
                                        + "Äste, modriger Grund – es ist schwer, durch "
                                        + "diese Wildnis voranzukommen. "
                                        + "Nicht weit in den Wald, und dir fällt ein "
                                        + "Strauch mit kleinen, "
                                        + "purpurnen Früchten auf, wie zu klein geratene "
                                        + "Äpfel",
                                true,
                                false,
                                true,
                                mins(5)),
                        allg("Noch einmal kämpfst du dich durch den wilden "
                                        + "Wald hinter dem Brunnen, bis du den Strauch mit den "
                                        + "kleinen, violetten Früchten erreichst",
                                false,
                                false,
                                false,
                                mins(4))));
            }

            return resImWaldBeimBrunnnen.build();
        }
        if (from.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            return ImmutableList.of(

                    con(IM_WALD_BEIM_BRUNNEN,
                            "Zum Brunnen gehen",
                            allg(
                                    "Durch den wilden Wald suchst du dir einen Weg "
                                            + "zurück zum Brunnen",
                                    false,
                                    true,
                                    true,
                                    mins(3))
                    ));
            // STORY Nächster Raum: "wirst du von einer dichten Dornenhecke
            //  zurückgehalten"
        }

        throw new

                IllegalStateException("Unexpected from: " + from);

    }

// -------------------------------------------------------------------
// --- SCHLOSS_VORHALLE
// -------------------------------------------------------------------


    private AbstractDescription getDesc_SchlossVorhalle_DraussenVorDemSchloss(
            final RoomKnown newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (db.invisibleDataDao().getInvisible(Invisible.SCHLOSSFEST).getState()) {
            case BEGONNEN:
                return getDesc_SchlossVorhalle_DraussenVorDemSchloss_FestBegonnen();

            default:
                return getDesc_SchlossVorhalle_DraussenVorDemSchlosss_KeinFest(newRoomKnown,
                        lichtverhaeltnisse);
        }
    }

    private static AbstractDescription getDesc_SchlossVorhalle_DraussenVorDemSchlosss_KeinFest(
            final RoomKnown roomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (roomKnown == UNKNOWN) {
            return getDesc_SchlossVorhalle_DraussenVorDemSchlosss_KeinFest_Unknown(
                    lichtverhaeltnisse);
        }

        if (roomKnown == KNOWN_FROM_DARKNESS && lichtverhaeltnisse == HELL) {
            return du(
                    "verlässt",
                    "das Schloss. Draußen scheint dir die " +
                            "Sonne ins Gesicht; "
                            // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
                            //  noch nicht?
                            + "der Tag ist recht heiß",
                    false,
                    false,
                    false,
                    mins(1));
        }

        // STORY: Wenn man aus dem hellen (Schloss) ins Dunkle kommt:
        //  "Draußen ist es dunkel" o.Ä.

        return du(
                "verlässt",
                "das Schloss",
                false,
                true,
                true,
                mins(1));
    }

    @NonNull
    private static AbstractDescription
    getDesc_SchlossVorhalle_DraussenVorDemSchlosss_KeinFest_Unknown(
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        if (lichtverhaeltnisse == HELL) {
            return du(
                    "gehst",
                    "über die Marmortreppe hinaus in die Gärten vor dem Schloss.\n\n" +
                            "Draußen scheint dir die " +
                            "Sonne ins Gesicht; "
                            // STORY Vielleicht ist es nur tagsüber / mittags heiß und morgens
                            //  noch nicht?
                            + "der Tag ist recht heiß. " +
                            "Nahebei liegt ein großer, dunkler Wald",
                    false,
                    false,
                    false,
                    mins(1));
        }

        return du(
                "gehst",
                "über die Marmortreppe hinaus den Garten vor dem Schloss.\n\n" +
                        "Draußen ist es dunkel. " +
                        "In der Nähe liegt ein großer Wald, der sehr bedrohlich wirkt",
                true,
                false,
                false,
                mins(1));
    }

    @NonNull
    private static AbstractDescription
    getDesc_SchlossVorhalle_DraussenVorDemSchloss_FestBegonnen() {
        return allg(
                "Du stehst "
                        // STORY: Nachts ist weniger Trubel? (Wäre das ein Statuswechsel beim
                        //  Schlossfest? Oder Zumindest auch eine Reaction wie der Auf- /
                        //  Abbau des Schlossfestes?)
                        + "vom Tisch auf und gehst über die Marmortreppe hinaus in den Trubel "
                        + "im Schlossgarten",
                false,
                false,
                true,
                mins(3));
    }

    private AbstractDescription getDesc_SchlossVorhalle_SchlossVorhalleTischBeimFest(
            final RoomKnown newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
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
                            + "Löffel bereit",
                    false,
                    false,
                    false,
                    mins(3));
        }

        return du("suchst",
                "dir im Gedränge einen Platz an einem Tisch",
                false,
                false,
                false,
                mins(3));
    }

// -------------------------------------------------------------------
// --- SCHLOSS_VORHALLE_TISCH_BEIM_FEST
// -------------------------------------------------------------------

    private static AbstractDescription getDesc_SchlossVorhalleTischBeimFest_SchlossVorhalle(
            final RoomKnown newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        return allg(
                "Du stehst vom Tisch auf",
                false,
                true,
                true,
                mins(3));
    }

// -------------------------------------------------------------------
// --- DRAUSSEN_VOR_DEM_SCHLOSS
// -------------------------------------------------------------------

    private AbstractDescription getDesc_DraussenVorDemSchloss_SchlossVorhalle(
            final RoomKnown newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (db.invisibleDataDao().getInvisible(Invisible.SCHLOSSFEST).getState()) {
            case BEGONNEN:
                return getDesc_DraussenVorDemSchloss_SchlossVorhalle_FestBegonnen();

            default:
                return getDesc_DraussenVorDemSchloss_SchlossVorhalle_KeinFest();
        }
    }

    @NonNull
    private static AbstractDescription
    getDesc_DraussenVorDemSchloss_SchlossVorhalle_KeinFest() {
        return du(
                "gehst",
                "wieder hinein in das Schloss",
                false,
                true,
                true,
                mins(1));
    }

    private AbstractDescription
    getDesc_DraussenVorDemSchloss_SchlossVorhalle_FestBegonnen() {
        if (db.counterDao().incAndGet(
                "RoomConnectionBuilder_DraussenVorDemSchloss_SchlossVorhalle_FestBegonnen")
                == 1) {
            return allg("Vor dem Schloss gibt es ein großes Gedränge und es dauert "
                            + "eine Weile, bis "
                            + "die Menge dich hineinschiebt. Die prächtige Vorhalle steht voller "
                            + "Tische, auf denen in großen Schüsseln Eintöpfe dampfen",
                    true,
                    false,
                    false,
                    mins(7));
        }

        return du("betrittst",
                "wieder das Schloss",
                false,
                true,
                true,
                mins(2));
    }

// -------------------------------------------------------------------
// --- IM_WALD_NAHE_DEM_SCHLOSS
// -------------------------------------------------------------------

    private AbstractDescription getDesc_ImWaldNaheDemSchloss_DraussenVorDemSchloss(
            final RoomKnown newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisse) {

        switch (db.invisibleDataDao().getInvisible(Invisible.SCHLOSSFEST).getState()) {
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
                            + "liegt",
                    true,
                    false,
                    false,
                    mins(10));
        }

        return du("gehts", "noch eine Weile vorsichtig durch den dunklen "
                        + "Wald, dann öffnet sich der Weg wieder und du stehst im Schlossgarten "
                        + "unter dem Sternenhimmel",
                false,
                false,
                false,
                mins(15));


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
                            + "Gebratenem",
                    false,
                    false,
                    false,
                    timeSpan);
        }

        return allg("Das Schlossfest ist immer noch in vollem Gange",
                false,
                false,
                false,
                timeSpan);
    }

// -------------------------------------------------------------------
// --- Allgemein
// -------------------------------------------------------------------

    private Lichtverhaeltnisse getLichtverhaeltnisseFrom() {
        final Tageszeit tageszeit = db.dateTimeDao().now().getTageszeit();
        return Lichtverhaeltnisse.getLichtverhaeltnisse(tageszeit, from.getId());
    }
}
