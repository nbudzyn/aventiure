package de.nb.aventiure2.data.world.syscomp.spatialconnection.builder;

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
    interface DescriptionProviderAndSCMovingSideEffectsEffectuator {
        // TODO Wie wollen hier gerade KEINE Seiteneffekte, außer incAndGet(),
        //  wenn eine Beschreibung das erste / zweite... Mal erfolgt ist.
        //  Damit das incAndGet() aber funktioniert, müssen wir hier
        //  direkt das n.add() machen!! Also sollten die Methoden alle
        //  narrateSCMove...() oder ähnlich heißen!
        AbstractDescription getDescriptionAndDoSCMovingSideEffects(Known newRoomKnow,
                                                                   Lichtverhaeltnisse lichtverhaeltnisseInNewRoom);
    }

    private final GameObjectId to;
    private final String actionName;
    private final DescriptionProviderAndSCMovingSideEffectsEffectuator
            descriptionProviderAndSCMovingSideEffectsEffectuator;

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
                                 final DescriptionProviderAndSCMovingSideEffectsEffectuator descriptionProviderAndSCMovingSideEffectsEffectuator) {
        return new SpatialConnection(to, actionName,
                descriptionProviderAndSCMovingSideEffectsEffectuator);
    }

    private SpatialConnection(final GameObjectId to,
                              final String actionName,
                              final DescriptionProviderAndSCMovingSideEffectsEffectuator descriptionProviderAndSCMovingSideEffectsEffectuator) {
        this.to = to;
        this.actionName = actionName;
        this.descriptionProviderAndSCMovingSideEffectsEffectuator =
                descriptionProviderAndSCMovingSideEffectsEffectuator;
    }

    public String getActionName() {
        return actionName;
    }

    public GameObjectId getTo() {
        return to;
    }

    public AbstractDescription getDescriptionAndDoSCMovingSideEffects(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        return descriptionProviderAndSCMovingSideEffectsEffectuator
                .getDescriptionAndDoSCMovingSideEffects(newRoomKnown, lichtverhaeltnisseInNewRoom);
    }
}