package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import java.util.function.Supplier;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AbstractAdverbialeAngabe;

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
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescription) {
        return con(to, wo, () -> actionName, standardDuration, newLocationDescription);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescription) {
        return con(to, wo, actionNameProvider, standardDuration,
                (isnewLocationKnown, lichtverhaeltnisseInNewLocation) -> newLocationDescription);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknown,
                                        final TimedDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, () -> actionName, standardDuration,
                newLocationDescriptionUnknown, newLocationDescriptionKnown);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final Supplier<String> actionNameSupplier,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknown,
                                        final TimedDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, actionNameSupplier, standardDuration,
                (newLocationKnown, lichtverhaeltnisseInNewLocation) ->
                        newLocationKnown == UNKNOWN ?
                                newLocationDescriptionUnknown : newLocationDescriptionKnown);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, () -> actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel, newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, actionNameProvider, standardDuration,
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
        return con(to, wo, () -> actionName, standardDuration, scMoveDescriptionProvider);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final Supplier<String> actionNameSupplier,
                                        final AvTimeSpan standardDuration,
                                        final SpatialConnectionData.SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return con(to,
                conData(
                        wo,
                        actionNameSupplier,
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

    public AbstractAdverbialeAngabe getWoAdvAngabe() {
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