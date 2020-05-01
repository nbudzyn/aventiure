package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.german.base.AbstractDescription;

import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.UNKNOWN;

/**
 * Die Verbindung von einem Raum zu einem anderen, wie sie der SC beim Bewegen benutzten kann -
 * einschließlich ihrer Beschreibung, wie sie beim Bewegen angezeigt wird (ohne Gegenstände,
 * Kreaturen etc.)
 */
public class SpatialConnection {
    @FunctionalInterface
    interface SCMoveDescriptionProvider {
        AbstractDescription getSCMoveDescription(Known newRoomKnow,
                                                 Lichtverhaeltnisse lichtverhaeltnisseInNewRoom);
    }

    private final GameObjectId to;
    private final String actionName;
    private final SCMoveDescriptionProvider
            SCMoveDescriptionProvider;

    static SpatialConnection con(final GameObjectId to, final String actionDescription,
                                 final AbstractDescription newRoomDescription) {
        return con(to, actionDescription,
                (isNewRoomKnown, lichtverhaeltnisseInNewRoom) -> newRoomDescription);
    }

    static SpatialConnection con(final GameObjectId to,
                                 final String actionDescription,
                                 final AbstractDescription newRoomDescriptionUnknown,
                                 final AbstractDescription newRoomDescriptionKnown) {
        return con(to, actionDescription,
                (newRoomKnown, lichtverhaeltnisseInNewRoom) ->
                        newRoomKnown == UNKNOWN ?
                                newRoomDescriptionUnknown : newRoomDescriptionKnown);
    }

    static SpatialConnection con(final GameObjectId to,
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

    static SpatialConnection con(final GameObjectId to,
                                 final String actionName,
                                 final SCMoveDescriptionProvider SCMoveDescriptionProvider) {
        return new SpatialConnection(to, actionName,
                SCMoveDescriptionProvider);
    }

    private SpatialConnection(final GameObjectId to,
                              final String actionName,
                              final SCMoveDescriptionProvider SCMoveDescriptionProvider) {
        this.to = to;
        this.actionName = actionName;
        this.SCMoveDescriptionProvider =
                SCMoveDescriptionProvider;
    }

    public String getActionName() {
        return actionName;
    }

    public GameObjectId getTo() {
        return to;
    }

    public AbstractDescription getSCMoveDescription(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        return SCMoveDescriptionProvider
                .getSCMoveDescription(newRoomKnown, lichtverhaeltnisseInNewRoom);
    }
}