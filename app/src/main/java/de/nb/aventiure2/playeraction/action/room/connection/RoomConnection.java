package de.nb.aventiure2.playeraction.action.room.connection;

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
    private interface DescriptionProvider {
        /**
         * Erzeugt die Beschreibung für die Bewegung von einem Raum zu einem anderen (ohne
         * Gegenstände, {@link de.nb.aventiure2.data.world.entity.creature.Creature}s etc.)
         */
        AbstractDescription getDescription(boolean isNewRoomKnown);
    }

    private final String actionName;
    private final RoomConnection.DescriptionProvider descriptionProvider;

    static RoomConnection con(final String actionDescription,
                              final AbstractDescription newRoomDescription) {
        return new RoomConnection(actionDescription,
                isNewRoomKnown -> newRoomDescription);
    }

    static RoomConnection con(final String actionDescription,
                              final AbstractDescription newRoomDescriptionFirstTime,
                              final AbstractDescription newRoomDescriptionKnown) {
        return new RoomConnection(actionDescription,
                isNewRoomKnown ->
                        isNewRoomKnown ?
                                newRoomDescriptionKnown : newRoomDescriptionFirstTime);
    }

    private RoomConnection(final String actionName,
                           final DescriptionProvider descriptionProvider) {
        this.actionName = actionName;
        this.descriptionProvider = descriptionProvider;
    }

    public String getActionName() {
        return actionName;
    }

    public AbstractDescription getDescription(
            final boolean isNewRoomKnown) {
        return descriptionProvider.getDescription(isNewRoomKnown);
    }
}