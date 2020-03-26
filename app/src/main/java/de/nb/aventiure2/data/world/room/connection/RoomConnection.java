package de.nb.aventiure2.data.world.room.connection;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import java.util.Map;

import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.AbstractDescription;

import static de.nb.aventiure2.data.world.room.AvRoom.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.room.AvRoom.VOR_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.german.DuDescription.du;
import static de.nb.aventiure2.german.base.AllgDescription.allg;

public class RoomConnection {
    private static final Table<AvRoom, AvRoom, RoomConnection> ALL =
            ImmutableTable.<AvRoom, AvRoom, RoomConnection>builder()
                    .put(SCHLOSS_VORHALLE, DRAUSSEN_VOR_DEM_SCHLOSS,
                            con(
                                    "Das Schloss verlassen",
                                    du(
                                            "gehst",
                                            "hinaus.\n\n" +
                                                    "Draußen scheint dir die " +
                                                    "Sonne ins Gesicht; der Tag ist recht heiß. " +
                                                    "Nahebei liegt ein großer, dunkler Wald",
                                            false,
                                            false,
                                            false),
                                    du(
                                            "verlässt",
                                            "das Schloss",
                                            false,
                                            true,
                                            true)))
                    .put(DRAUSSEN_VOR_DEM_SCHLOSS, SCHLOSS_VORHALLE,
                            con(
                                    "Das Schloss betreten",
                                    du(
                                            "gehst",
                                            "wieder hinein in das Schloss",
                                            false,
                                            true,
                                            true)))
                    .put(DRAUSSEN_VOR_DEM_SCHLOSS, IM_WALD_NAHE_DEM_SCHLOSS,
                            con(
                                    "In den Wald gehen",
                                    du(
                                            "folgst",
                                            "einem Pfad in den Wald",
                                            false,
                                            true,
                                            true),
                                    du(
                                            "läufst",
                                            "wieder in den dunklen Wald",
                                            false,
                                            true,
                                            true)))
                    .put(IM_WALD_NAHE_DEM_SCHLOSS, DRAUSSEN_VOR_DEM_SCHLOSS,
                            con(
                                    "Den Wald verlassen",
                                    allg("Von dort gehst du zurück ans helle "
                                                    + "Tageslicht, in den Schlossgarten",
                                            false,
                                            false,
                                            true)))
                    .put(IM_WALD_NAHE_DEM_SCHLOSS, ABZWEIG_IM_WALD,
                            con("Tiefer in den Wald hineingehen",
                                    allg("Nicht lang, und zur Linken geht zwischen "
                                                    + "den Bäumen ein alter, düsterer Weg ab, über "
                                                    + "den Farn wuchert",
                                            true,
                                            false,
                                            false
                                    ),
                                    du("kommst",
                                            "an den farnüberwachsenen Abzweig",
                                            false,
                                            true,
                                            false
                                    )
                            ))
                    .put(ABZWEIG_IM_WALD, IM_WALD_NAHE_DEM_SCHLOSS,
                            con("In Richtung Schloss gehen",
                                    du("erreichst",
                                            "die Stelle, wo der überwachsene Weg "
                                                    + "abzweigt",
                                            true,
                                            true,
                                            false
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
                                            false
                                    ),
                                    du("wählst", "noch einmal den überwachsenen "
                                                    + "Pfad zur Hütte",
                                            false,
                                            true,
                                            false)
                            ))
                    .put(VOR_DER_HUETTE_IM_WALD, ABZWEIG_IM_WALD,
                            con("Auf den Hauptpfad zurückkehren",
                                    allg("Durch Farn und Gestrüpp gehst du zurück zum "
                                                    + "Hauptpfad",
                                            false,
                                            true,
                                            true
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
                                            false
                                    ),
                                    du("schiebst", "dich noch einmal in die "
                                                    + "kleine Hütte, in der es außer Tisch und "
                                                    + "Bett wenig zu sehen gibt",
                                            true,
                                            true,
                                            false
                                    )
                            ))
                    .put(HUETTE_IM_WALD, VOR_DER_HUETTE_IM_WALD,
                            con("Die Hütte verlassen",
                                    du("zwängst", "dich wieder durch die Tür nach "
                                                    + "draußen",
                                            false,
                                            true,
                                            true
                                    )
                            ))
                    .put(HUETTE_IM_WALD, BETT_IN_DER_HUETTE_IM_WALD,
                            con("In das Bett legen",
                                    du("legst", "dich in das hölzere Bettgestell. "
                                                    + "Gemütlich ist etwas anderes, aber nach den "
                                                    + "vielen Schritten tut es sehr gut, sich "
                                                    + "einmal auszustrecken",
                                            false,
                                            false,
                                            false
                                    ),
                                    allg("Noch einmal legst du dich in das Holzbett",
                                            false,
                                            true,
                                            true
                                    )
                            ))
                    .put(BETT_IN_DER_HUETTE_IM_WALD, HUETTE_IM_WALD,
                            con("Aufstehen",
                                    allg("Du reckst dich noch einmal und stehst "
                                                    + "wieder auf",
                                            false,
                                            false,
                                            true
                                    )
                            ))
                    .put(ABZWEIG_IM_WALD, IM_WALD_BEIM_BRUNNEN,
                            con(
                                    "Auf dem Hauptpfad tiefer in den Wald gehen",
                                    allg(
                                            "Der breitere Pfad führt zu einer alten "
                                                    + "Linde, unter der ist ein Brunnen. Du setzt "
                                                    + "dich an den Brunnenrand – hier ist es "
                                                    + "angenehm kühl",
                                            false,
                                            false,
                                            true),

                                    du("kehrst",
                                            "zurück zum Brunnen unter der Linde",
                                            false,
                                            true,
                                            true)))
                    .put(IM_WALD_BEIM_BRUNNEN, ABZWEIG_IM_WALD,
                            con(
                                    "Den Weg Richtung Schloss gehen",
                                    du("verlässt",
                                            "den Brunnen",
                                            false,
                                            true,
                                            true)))
                    .

                            build();

    private final String actionName;
    private final AbstractDescription descriptionFirstTime;
    private final AbstractDescription descriptionKnown;

    public static Map<AvRoom, RoomConnection> getFrom(final AvRoom from) {
        return ALL.row(from);
    }

    private static RoomConnection con(final String actionDescription,
                                      final AbstractDescription newRoomDescription) {
        return con(actionDescription, newRoomDescription,
                newRoomDescription);
    }

    private static RoomConnection con(final String actionDescription,
                                      final AbstractDescription newRoomDescriptionFirstTime,
                                      final AbstractDescription newRoomDescriptionKnown) {
        return new RoomConnection(actionDescription, newRoomDescriptionFirstTime,
                newRoomDescriptionKnown);
    }

    private RoomConnection(final String actionName,
                           final AbstractDescription descriptionFirstTime,
                           final AbstractDescription descriptionKnown) {
        this.actionName = actionName;
        this.descriptionFirstTime = descriptionFirstTime;
        this.descriptionKnown = descriptionKnown;
    }

    public String getActionName() {
        return actionName;
    }

    public AbstractDescription getDescription(
            final boolean isNewRoomKnown) {
        if (isNewRoomKnown) {
            return descriptionKnown;
        }

        return descriptionFirstTime;
    }

}