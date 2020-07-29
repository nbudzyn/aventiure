package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;

import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;

/**
 * Die Verbindung von einem {@link de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO}
 * zu einem anderen, wie sie der SC beim Bewegen benutzten kann -
 * einschließlich ihrer Beschreibung, wie sie beim Bewegen angezeigt wird.
 */
public class SpatialConnection {
    @FunctionalInterface
    public interface SCMoveDescriptionProvider {
        AbstractDescription<?> getSCMoveDescription(
                Known newLocationKnown,
                Lichtverhaeltnisse lichtverhaeltnisseInNewLocation);
    }

    private final GameObjectId to;

    /**
     * Eine Adverbiale Angabe, die diese Verbinung
     * räumlich beschreibt
     * (aus Sicht des
     * {@link de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO},
     * nicht unbedingt aus Sicht von {@link #to}!):
     * "auf dem Weg" o.Ä.
     * <p>
     * Die Beschreibung sollte sich auf etwas beziehen, auf dem man sich eine längere Zeit
     * bewegen kann (nicht "in der Tür", sondern "auf der Treppe").
     * <p>
     * Es ist außerdem gut, wenn dieselbe Beschreibung innerhalb eines
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
    private final SCMoveDescriptionProvider scMoveDescriptionProvider;

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final String actionDescription,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescription) {
        return con(to, wo, actionDescription, standardDuration,
                (isnewLocationKnown, lichtverhaeltnisseInNewLocation) -> newLocationDescription);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final String actionDescription,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknown,
                                        final AbstractDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, actionDescription, standardDuration,
                (newLocationKnown, lichtverhaeltnisseInNewLocation) ->
                        newLocationKnown == UNKNOWN ?
                                newLocationDescriptionUnknown : newLocationDescriptionKnown);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final String actionDescription,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, actionDescription, standardDuration,
                (newLocationKnown, lichtverhaeltnisseInNewLocation) -> {
                    if (newLocationKnown == UNKNOWN && lichtverhaeltnisseInNewLocation == HELL) {
                        return newLocationDescriptionUnknownHell;
                    }
                    if (newLocationKnown == UNKNOWN && lichtverhaeltnisseInNewLocation == DUNKEL) {
                        return newLocationDescriptionUnknownDunkel;
                    }
                    if (newLocationKnown == KNOWN_FROM_DARKNESS
                            && lichtverhaeltnisseInNewLocation == HELL) {
                        return newLocationDescriptionKnownFromDarknessHell;
                    }
                    return newLocationDescriptionOther;
                });
    }

    public static SpatialConnection con(final GameObjectId to,
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
                              final SCMoveDescriptionProvider scMoveDescriptionProvider) {
        this.to = to;
        this.wo = wo;
        this.actionName = actionName;
        this.standardDuration = standardDuration;
        this.scMoveDescriptionProvider = scMoveDescriptionProvider;
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

    public SCMoveDescriptionProvider getSCMoveDescriptionProvider() {
        return scMoveDescriptionProvider;
    }

    @NonNull
    @Override
    public String toString() {
        return "SpatialConnection{" +
                "actionName='" + actionName + '\'' +
                '}';
    }
}