package de.nb.aventiure2.playeraction.action.room.connection;

import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.AbstractDescription;

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
        AbstractDescription getDescription(boolean isNewRoomKnown);
    }

    private final AvRoom to;
    private final String actionName;
    private final RoomConnection.DescriptionProvider descriptionProvider;

    static RoomConnection con(final AvRoom to, final String actionDescription,
                              final AbstractDescription newRoomDescription) {
        return con(to, actionDescription,
                isNewRoomKnown -> newRoomDescription);
    }

    static RoomConnection con(final AvRoom to,
                              final String actionDescription,
                              final AbstractDescription newRoomDescriptionFirstTime,
                              final AbstractDescription newRoomDescriptionKnown) {
        return con(to, actionDescription,
                isNewRoomKnown ->
                        isNewRoomKnown ?
                                newRoomDescriptionKnown : newRoomDescriptionFirstTime);
    }

    static RoomConnection con(final AvRoom to,
                              final String actionName,
                              final DescriptionProvider descriptionProvider) {
        return new RoomConnection(to, actionName, descriptionProvider);
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
            final boolean isNewRoomKnown) {
        return descriptionProvider.getDescription(isNewRoomKnown);
    }
}