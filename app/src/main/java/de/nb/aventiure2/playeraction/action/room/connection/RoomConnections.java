package de.nb.aventiure2.playeraction.action.room.connection;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import java.util.Map;

import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.world.room.AvRoom.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.HINTER_DER_HUETTE;
import static de.nb.aventiure2.data.world.room.AvRoom.HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.room.AvRoom.VOR_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.DuDescription.du;
import static de.nb.aventiure2.german.base.AllgDescription.allg;
import static de.nb.aventiure2.playeraction.action.room.connection.RoomConnection.con;

public class RoomConnections {
    private static final Table<AvRoom, AvRoom, RoomConnection> ALL =
            ImmutableTable.<AvRoom, AvRoom, RoomConnection>builder()
                    .put(SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS,
                            con(
                                    "Das Schloss verlassen",
                                    du(
                                            "gehst",
                                            "hinaus.\n\n" +
                                                    "Draußen scheint dir die " +
                                                    "Sonne ins Gesicht; der Tag ist recht heiß. "
                                                    +
                                                    "Nahebei liegt ein großer, dunkler Wald",
                                            false,
                                            false,
                                            false,
                                            mins(1)),
                                    du(
                                            "verlässt",
                                            "das Schloss",
                                            false,
                                            true,
                                            true,
                                            mins(1))))
                    .put(DRAUSSEN_VOR_DEM_SCHLOSS, SCHLOSS_VORHALLE,
                            con(
                                    "Das Schloss betreten",
                                    du(
                                            "gehst",
                                            "wieder hinein in das Schloss",
                                            false,
                                            true,
                                            true,
                                            mins(1))))
                    .put(DRAUSSEN_VOR_DEM_SCHLOSS, IM_WALD_NAHE_DEM_SCHLOSS,
                            con(
                                    "In den Wald gehen",
                                    du(
                                            "folgst",
                                            "einem Pfad in den Wald",
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
                                            mins(10))))
                    .put(IM_WALD_NAHE_DEM_SCHLOSS, DRAUSSEN_VOR_DEM_SCHLOSS,
                            con(
                                    "Den Wald verlassen",
                                    du("erreichst", "bald das helle "
                                                    + "Tageslicht, in dem der Schlossgarten "
                                                    + "liegt",
                                            // STORY Und bei Nacht?!
                                            true,
                                            false,
                                            false,
                                            mins(10))))
                    .put(IM_WALD_NAHE_DEM_SCHLOSS, ABZWEIG_IM_WALD,
                            con("Tiefer in den Wald hineingehen",
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
                            ))
                    .put(ABZWEIG_IM_WALD, IM_WALD_NAHE_DEM_SCHLOSS,
                            con("In Richtung Schloss gehen",
                                    allg("Von dort gehst du weiter in Richtung Schloss",
                                            false,
                                            true,
                                            false,
                                            mins(5)
                                    )
                            ))
                    .put(ABZWEIG_IM_WALD, VOR_DER_HUETTE_IM_WALD,
                            con("Den überwachsenen Abzweig nehmen",
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
                                    du("wählst", "noch einmal den überwachsenen "
                                                    + "Pfad zur Hütte",
                                            false,
                                            true,
                                            false,
                                            mins(2))
                            ))
                    .put(VOR_DER_HUETTE_IM_WALD, ABZWEIG_IM_WALD,
                            con("Auf den Hauptpfad zurückkehren",
                                    allg("Durch Farn und Gestrüpp gehst du zurück zum "
                                                    + "Hauptpfad",
                                            false,
                                            true,
                                            true,
                                            mins(2)
                                    )
                            ))
                    .put(VOR_DER_HUETTE_IM_WALD, HUETTE_IM_WALD,
                            con("Die Hütte betreten",
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
                                    du("schiebst", "dich noch einmal in die "
                                                    + "kleine Hütte, in der es außer Tisch und "
                                                    + "Bett wenig zu sehen gibt",
                                            true,
                                            true,
                                            false,
                                            mins(1)
                                    )
                            ))
                    .put(HUETTE_IM_WALD, VOR_DER_HUETTE_IM_WALD,
                            con("Die Hütte verlassen",
                                    du("zwängst", "dich wieder durch die Tür nach "
                                                    + "draußen",
                                            false,
                                            true,
                                            true,
                                            secs(15)
                                    )
                            ))
                    .put(VOR_DER_HUETTE_IM_WALD, HINTER_DER_HUETTE,
                            con("Um die Hütte herumgehen",
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
                                    du("schaust", "noch einmal in den alten "
                                                    + "Garten hinter der Hütte, wo der "
                                                    + "Apfelbaum wächst",
                                            true,
                                            true,
                                            true,
                                            secs(30)
                                    )
                            ))
                    .put(HINTER_DER_HUETTE, VOR_DER_HUETTE_IM_WALD,
                            con("Zur Vorderseite der Hütte gehen",
                                    du("kehrst", "zurück zur Vorderseite der "
                                                    + "Hütte",
                                            false,
                                            true,
                                            true,
                                            secs(15)
                                    )
                            )
                    )
                    .put(HUETTE_IM_WALD, BETT_IN_DER_HUETTE_IM_WALD,
                            con("In das Bett legen",
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
                            ))
                    .

                            put(BETT_IN_DER_HUETTE_IM_WALD, HUETTE_IM_WALD,
                                    con("Aufstehen",
                                            allg("Du reckst dich noch einmal und stehst "
                                                            + "wieder auf",
                                                    false,
                                                    false,
                                                    true,
                                                    secs(10)
                                            )
                                    ))
                    .

                            put(ABZWEIG_IM_WALD, IM_WALD_BEIM_BRUNNEN,
                                    con(
                                            "Auf dem Hauptpfad tiefer in den Wald gehen",
                                            allg(
                                                    "Der breitere Pfad führt zu einer alten "
                                                            + "Linde, unter der ist ein Brunnen. Du setzt "
                                                            + "dich an den Brunnenrand – hier ist es "
                                                            + "angenehm kühl",
                                                    false,
                                                    false,
                                                    true,
                                                    mins(5)
                                            ),

                                            du("kehrst",
                                                    "zurück zum Brunnen unter der Linde",
                                                    false,
                                                    true,
                                                    true,
                                                    mins(3))))
                    .put(IM_WALD_BEIM_BRUNNEN, ABZWEIG_IM_WALD,
                            con(
                                    "Den Weg Richtung Schloss gehen",
                                    du("verlässt",
                                            "den Brunnen und erreichst bald "
                                                    + "die Stelle, wo der überwachsene Weg "
                                                    + "abzweigt",
                                            true,
                                            false,
                                            false,
                                            mins(3)
                                    ))).build();


    public static Map<AvRoom, RoomConnection> getFrom(final AvRoom from) {
        return ALL.row(from);
    }
}
