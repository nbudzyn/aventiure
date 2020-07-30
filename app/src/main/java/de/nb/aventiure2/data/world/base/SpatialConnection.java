package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;

import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;

/**
 * Die Verbindung von einem {@link de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO}
 * zu einem anderen, wie sie der SC beim Bewegen benutzten kann -
 * einschließlich ihrer Beschreibung, wie sie beim Bewegen angezeigt wird.
 */
public class SpatialConnection {
    private final GameObjectId to;

    private final SpatialConnectionData data;

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
                                        final SpatialConnectionData.SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return con(to,
                conData(
                        wo,
                        actionName,
                        standardDuration,
                        scMoveDescriptionProvider
                ));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final SpatialConnectionData data) {
        return new SpatialConnection(to, data);
    }

    private SpatialConnection(final GameObjectId to,
                              final SpatialConnectionData data) {
        this.to = to;
        this.data = data;
    }

    public String getActionName() {
        return data.getActionName();
    }

    public GameObjectId getTo() {
        return to;
    }

    public AvTimeSpan getStandardDuration() {
        return data.getStandardDuration();
    }

    public AdverbialeAngabe getWoAdvAngabe() {
        return data.getWoAdvAngabe();
    }

    public String getWo() {
        return data.getWo();
    }

    public SpatialConnectionData.SCMoveDescriptionProvider getSCMoveDescriptionProvider() {
        return data.getSCMoveDescriptionProvider();
    }

    @NonNull
    @Override
    public String toString() {
        return "SpatialConnection{" +
                "data='" + data + '\'' +
                '}';
    }
}