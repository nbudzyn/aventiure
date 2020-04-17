package de.nb.aventiure2.scaction.action.room.connection;

import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.RoomKnown;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.room.RoomKnown.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.room.RoomKnown.UNKNOWN;

/**
 * Die Verbindung von einem Raum zu einem anderen, wie sie der SC beim Bewegen benutzten kann -
 * einschließlich ihrer Beschreibung, wie sie beim Bewegen angezeigt wird (ohne Gegenstände,
 * {@link de.nb.aventiure2.data.world.entity.creature.Creature}s etc.)
 */
public class RoomConnection {
    /**
     * Interface zur Erzeugung der Beschreibung für die Bewegung von einem Raum zu einem
     * anderen.
     */
    @FunctionalInterface
    interface DescriptionProvider {
        /**
         * Erzeugt die Beschreibung für die Bewegung von einem Raum zu einem anderen (ohne
         * Gegenstände, {@link de.nb.aventiure2.data.world.entity.creature.Creature}s etc.)
         */
        AbstractDescription getDescription(RoomKnown newRoomKnow,
                                           Lichtverhaeltnisse lichtverhaeltnisseInNewRoom);
    }

    private final AvRoom to;
    private final String actionName;
    private final RoomConnection.DescriptionProvider descriptionProvider;

    static RoomConnection con(final AvRoom.Key to, final String actionDescription,
                              final AbstractDescription newRoomDescription) {
        return con(to, actionDescription,
                (isNewRoomKnown, lichtverhaeltnisseInNewRoom) -> newRoomDescription);
    }

    static RoomConnection con(final AvRoom.Key to,
                              final String actionDescription,
                              final AbstractDescription newRoomDescriptionUnknown,
                              final AbstractDescription newRoomDescriptionKnown) {
        return con(to, actionDescription,
                (newRoomKnown, lichtverhaeltnisseInNewRoom) ->
                        newRoomKnown == UNKNOWN ?
                                newRoomDescriptionUnknown : newRoomDescriptionKnown);
    }

    static RoomConnection con(final AvRoom.Key to,
                              final String actionDescription,
                              final AbstractDescription newRoomDescriptionUnknownHell,
                              final AbstractDescription newRoomDescriptionUnknownDunkel,
                              final AbstractDescription newRoomDescriptionKnownFromDarknessHell,
                              final AbstractDescription newRoomDescriptionOther) {
        return con(to, actionDescription,
                (newRoomKnown, lichtverhaeltnisseInNewRoom) -> {
                    if (newRoomKnown == UNKNOWN && lichtverhaeltnisseInNewRoom == HELL) {
                        return newRoomDescriptionUnknownHell;
                    }
                    if (newRoomKnown == UNKNOWN && lichtverhaeltnisseInNewRoom == DUNKEL) {
                        return newRoomDescriptionUnknownDunkel;
                    }
                    if (newRoomKnown == KNOWN_FROM_DARKNESS
                            && lichtverhaeltnisseInNewRoom == HELL) {
                        return newRoomDescriptionKnownFromDarknessHell;
                    }
                    return newRoomDescriptionOther;
                });
    }

    static RoomConnection con(final AvRoom.Key to,
                              final String actionName,
                              final DescriptionProvider descriptionProvider) {
        return new RoomConnection(to, actionName, descriptionProvider);
    }

    private RoomConnection(final AvRoom.Key to,
                           final String actionName,
                           final DescriptionProvider descriptionProvider) {
        this(AvRoom.get(to), actionName, descriptionProvider);
    }

    private RoomConnection(final AvRoom to,
                           final String actionName,
                           final DescriptionProvider descriptionProvider) {
        this.to = to;
        this.actionName = actionName;
        this.descriptionProvider = descriptionProvider;
    }

    public String getActionName() {
        return actionName;
    }

    public AvRoom getTo() {
        return to;
    }

    public AbstractDescription getDescription(
            final RoomKnown newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        return descriptionProvider.getDescription(newRoomKnown, lichtverhaeltnisseInNewRoom);
    }
}