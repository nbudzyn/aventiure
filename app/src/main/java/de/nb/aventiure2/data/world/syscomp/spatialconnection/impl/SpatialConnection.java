package de.nb.aventiure2.data.world.syscomp.spatialconnection.impl;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;

import static de.nb.aventiure2.data.world.syscomp.memory.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;

/**
 * Die Verbindung von einem Raum zu einem anderen, wie sie der SC beim Bewegen benutzten kann -
 * einschließlich ihrer Beschreibung, wie sie beim Bewegen angezeigt wird (ohne Gegenstände,
 * Kreaturen etc.)
 */
public class SpatialConnection {
    @FunctionalInterface
    public interface SCMoveDescriptionProvider {
        AbstractDescription<?> getSCMoveDescription(Known newRoomKnow,
                                                    Lichtverhaeltnisse lichtverhaeltnisseInNewRoom);
    }

    private final GameObjectId to;

    /**
     * Eine Adverbiale Angabe, die diese Verbinung
     * räumlich beschreibt
     * (aus Sicht des
     * {@link de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO},
     * nicht unbedingt aus Sicht von {@link #to}!):
     * "auf dem Weg" o.Ä.
     * <p>
     * Die Beschreibung sollte sich auf etwas beziehen, auf dem man sich eine längere Zeit
     * bewegen kann (nicht "in der Tür", sondern "auf der Treppe").
     * <p>
     * Es ist außerdem gut, wenn dieselbe Beschreibung innerhalb des
     * {@link de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO}s
     * nicht mehr als zweimal auftritt.
     */
    private final String wo;

    /**
     * Standard-Dauer für die Bewegung: Bewegungszeit eines Menschen (wie des SC), der sich
     * auskennt, tagsüber.
     */
    private final AvTimeSpan standardDuration;

    private final String actionName;
    private final SCMoveDescriptionProvider SCMoveDescriptionProvider;

    static SpatialConnection con(final GameObjectId to,
                                 final String wo,
                                 final String actionDescription,
                                 final AvTimeSpan standardDuration,
                                 final AbstractDescription newRoomDescription) {
        return con(to, wo, actionDescription, standardDuration,
                (isNewRoomKnown, lichtverhaeltnisseInNewRoom) -> newRoomDescription);
    }

    static SpatialConnection con(final GameObjectId to,
                                 final String wo,
                                 final String actionDescription,
                                 final AvTimeSpan standardDuration,
                                 final AbstractDescription newRoomDescriptionUnknown,
                                 final AbstractDescription newRoomDescriptionKnown) {
        return con(to, wo, actionDescription, standardDuration,
                (newRoomKnown, lichtverhaeltnisseInNewRoom) ->
                        newRoomKnown == UNKNOWN ?
                                newRoomDescriptionUnknown : newRoomDescriptionKnown);
    }

    static SpatialConnection con(final GameObjectId to,
                                 final String wo,
                                 final String actionDescription,
                                 final AvTimeSpan standardDuration,
                                 final AbstractDescription newRoomDescriptionUnknownHell,
                                 final AbstractDescription newRoomDescriptionUnknownDunkel,
                                 final AbstractDescription newRoomDescriptionKnownFromDarknessHell,
                                 final AbstractDescription newRoomDescriptionOther) {
        return con(to, wo, actionDescription, standardDuration,
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
                                 final String wo,
                                 final String actionName,
                                 final AvTimeSpan standardDuration,
                                 final SCMoveDescriptionProvider SCMoveDescriptionProvider) {
        return new SpatialConnection(to, wo, actionName, standardDuration,
                SCMoveDescriptionProvider);
    }

    private SpatialConnection(final GameObjectId to,
                              final String wo,
                              final String actionName,
                              final AvTimeSpan standardDuration,
                              final SCMoveDescriptionProvider SCMoveDescriptionProvider) {
        this.to = to;
        this.wo = wo;
        this.actionName = actionName;
        this.standardDuration = standardDuration;
        this.SCMoveDescriptionProvider =
                SCMoveDescriptionProvider;
    }

    public String getActionName() {
        return actionName;
    }

    public GameObjectId getTo() {
        return to;
    }

    public AvTimeSpan getStandardDuration() {
        return standardDuration;
    }

    public AdverbialeAngabe getWoAdvAngabe() {
        return new AdverbialeAngabe(getWo());
    }

    public String getWo() {
        return wo;
    }

    public AbstractDescription<?> getSCMoveDescription(
            final Known newRoomKnown, final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        return SCMoveDescriptionProvider
                .getSCMoveDescription(newRoomKnown, lichtverhaeltnisseInNewRoom);
    }

    @NonNull
    @Override
    public String toString() {
        return "SpatialConnection{" +
                "actionName='" + actionName + '\'' +
                '}';
    }
}