package de.nb.aventiure2.data.world.room.connection;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

import java.util.Map;

import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.AbstractDescription;

import static de.nb.aventiure2.data.world.room.AvRoom.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.SCHLOSS;
import static de.nb.aventiure2.german.AllgDescription.allg;
import static de.nb.aventiure2.german.DuDescription.du;

public class RoomConnection {
    private static final Table<AvRoom, AvRoom, RoomConnection> ALL =
            ImmutableTable.<AvRoom, AvRoom, RoomConnection>builder()
                    .put(SCHLOSS, DRAUSSEN_VOR_DEM_SCHLOSS,
                            con(
                                    "Das Schloss verlassen",
                                    du(
                                            "gehst",
                                            "hinaus. " +
                                                    "Draußen scheint dir die " +
                                                    "Sonne ins Gesicht; der Tag ist recht heiß. " +
                                                    "Nahebei liegt ein großer, dunkler Wald",
                                            false,
                                            false),
                                    du(
                                            "verlässt",
                                            "das Schloss",
                                            true,
                                            true)))
                    .put(DRAUSSEN_VOR_DEM_SCHLOSS, SCHLOSS,
                            con(
                                    "Das Schloss betreten",
                                    du(
                                            "gehst",
                                            "wieder hinein in das Schloss",
                                            true,
                                            true)))
                    .put(DRAUSSEN_VOR_DEM_SCHLOSS, IM_WALD_NAHE_DEM_SCHLOSS,
                            con(
                                    "In den Wald gehen",
                                    du(
                                            "folgst",
                                            "einem Pfad in den Wald",
                                            true,
                                            true),
                                    du(
                                            "läufst",
                                            "wieder in den dunklen Wald",
                                            true,
                                            true)))
                    .put(IM_WALD_NAHE_DEM_SCHLOSS, DRAUSSEN_VOR_DEM_SCHLOSS,
                            con(
                                    "Den Wald verlassen",
                                    du(
                                            "gehst",
                                            "zurück ans helle Tageslicht. Du stehst " +
                                                    "wieder vor dem Schloss",
                                            false,
                                            true)))
                    .put(IM_WALD_NAHE_DEM_SCHLOSS, IM_WALD_BEIM_BRUNNEN,
                            con(
                                    "Tiefer in den Wald hinein gehen",
                                    allg(
                                            "Der Pfad führt zu einer alten Linde, unter " +
                                                    "der ist ein Brunnen. Du setzt dich an den " +
                                                    "Brunnenrand - hier ist es kühler",
                                            false,
                                            true),
                                    du("kehrst",
                                            "zurück zum Brunnen unter der Linde",
                                            true,
                                            true)))
                    .put(IM_WALD_BEIM_BRUNNEN, IM_WALD_NAHE_DEM_SCHLOSS,
                            con(
                                    "Den Weg Richtung Schloss gehen",
                                    du("verlässt",
                                            "den Brunnen",
                                            true,
                                            true)))
                    .build();

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