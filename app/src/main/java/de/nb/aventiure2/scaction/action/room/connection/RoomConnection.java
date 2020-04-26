package de.nb.aventiure2.scaction.action.room.connection;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.memory.Known;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.memory.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.memory.Known.UNKNOWN;

/**
 * Die Verbindung von einem Raum zu einem anderen, wie sie der SC beim Bewegen benutzten kann -
 * einschließlich ihrer Beschreibung, wie sie beim Bewegen angezeigt wird (ohne Gegenstände,
 * Kreaturen etc.)
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
         * Gegenstände, Kreaturen etc.)
         */
        AbstractDescription getDescription(Known newRoomKnow,
                                           Lichtverhaeltnisse lichtverhaeltnisseInNewRoom);
    }

    private final GameObjectId to;
    private final String actionName;
    private final RoomConnection.DescriptionProvider descriptionProvider;

    static RoomConnection con(final GameObjectId to, final String actionDescription,
                              final AbstractDescription newRoomDescription) {
        return con(to, actionDescription,
                (isNewRoomKnown, lichtverhaeltnisseInNewRoom) -> newRoomDescription);
    }

    static RoomConnection con(final GameObjectId to,
                              final String actionDescription,
                              final AbstractDescription newRoomDescriptionUnknown,
                              final AbstractDescription newRoomDescriptionKnown) {
        return con(to, actionDescription,
                (newRoomKnown, lichtverhaeltnisseInNewRoom) ->
                        newRoomKnown == UNKNOWN ?
                                newRoomDescriptionUnknown : newRoomDescriptionKnown);
    }

    static RoomConnection con(final GameObjectId to,
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

    static RoomConnection con(final GameObjectId to,
                              final String actionName,
                              final DescriptionProvider descriptionProvider) {
        return new RoomConnection(to, actionName, descriptionProvider);
    }

    private RoomConnection(final GameObjectId to,
                           final String actionName,
                           final DescriptionProvider descriptionProvider) {
        this.to = to;
        this.actionName = actionName;
        this.descriptionProvider = descriptionProvider;
    }

    public String getActionName() {
        return actionName;
    }

    public GameObjectId getTo() {
        return to;
    }

    public AbstractDescription getDescription(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        return descriptionProvider.getDescription(newRoomKnown, lichtverhaeltnisseInNewRoom);
    }
}