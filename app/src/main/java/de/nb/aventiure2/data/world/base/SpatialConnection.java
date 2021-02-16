package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Supplier;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.TimedDescription;

import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_DARKNESS;
import static de.nb.aventiure2.data.world.base.Known.UNKNOWN;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conData;
import static de.nb.aventiure2.data.world.base.SpatialConnectionData.conDataNichtSC;
import static java.util.Objects.requireNonNull;

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
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescription) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescription.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescription) {
        return con(to, wo, cardinalDirection, () -> actionName, standardDuration,
                newLocationDescription);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescription) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescription.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescription) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                (SpatialConnectionData.SCMoveTimedDescriptionProvider)
                        (isnewLocationKnown, lichtverhaeltnisseInNewLocation) -> newLocationDescription);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknown,
                                        final AbstractDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknown.timed(standardDuration),
                newLocationDescriptionKnown.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknown,
                                        final TimedDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknown.timed(standardDuration),
                newLocationDescriptionKnown);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknown,
                                        final AbstractDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknown,
                newLocationDescriptionKnown.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknown,
                                        final TimedDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, cardinalDirection, () -> actionName, standardDuration,
                newLocationDescriptionUnknown, newLocationDescriptionKnown);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameSupplier,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknown,
                                        final AbstractDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, cardinalDirection, actionNameSupplier, standardDuration,
                newLocationDescriptionUnknown.timed(standardDuration),
                newLocationDescriptionKnown.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameSupplier,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknown,
                                        final AbstractDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, cardinalDirection, actionNameSupplier, standardDuration,
                newLocationDescriptionUnknown,
                newLocationDescriptionKnown.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameSupplier,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknown,
                                        final TimedDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, cardinalDirection, actionNameSupplier, standardDuration,
                newLocationDescriptionUnknown.timed(standardDuration),
                newLocationDescriptionKnown);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameSupplier,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknown,
                                        final TimedDescription<?> newLocationDescriptionKnown) {
        return con(to, wo, cardinalDirection, actionNameSupplier, standardDuration,
                (SpatialConnectionData.SCMoveTimedDescriptionProvider)
                        (newLocationKnown, lichtverhaeltnisseInNewLocation) ->
                                newLocationKnown == UNKNOWN ?
                                        newLocationDescriptionUnknown :
                                        newLocationDescriptionKnown);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, null, actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        @Nullable final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionName, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        @Nullable final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, () -> actionName, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel, newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final AbstractDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell.timed(standardDuration),
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final AbstractDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel.timed(
                        standardDuration),
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther.timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final AbstractDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell.timed(
                        standardDuration),
                newLocationDescriptionOther);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final AbstractDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                newLocationDescriptionUnknownHell,
                newLocationDescriptionUnknownDunkel,
                newLocationDescriptionKnownFromDarknessHell,
                newLocationDescriptionOther.timed(standardDuration));
    }


    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        @Nullable final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameProvider,
                                        final AvTimeSpan standardDuration,
                                        final TimedDescription<?> newLocationDescriptionUnknownHell,
                                        final TimedDescription<?> newLocationDescriptionUnknownDunkel,
                                        final TimedDescription<?> newLocationDescriptionKnownFromDarknessHell,
                                        final TimedDescription<?> newLocationDescriptionOther) {
        return con(to, wo, cardinalDirection, actionNameProvider, standardDuration,
                (SpatialConnectionData.SCMoveTimedDescriptionProvider)
                        (newLocationKnown, lichtverhaeltnisseInNewLocation) -> {
                            if (newLocationKnown == UNKNOWN
                                    && lichtverhaeltnisseInNewLocation == HELL) {
                                return newLocationDescriptionUnknownHell;
                            }
                            if (newLocationKnown == UNKNOWN
                                    && lichtverhaeltnisseInNewLocation == DUNKEL) {
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
                                        final SpatialConnectionData.SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return con(to, wo, null, actionName, standardDuration, scMoveTimedDescriptionProvider);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        @Nullable final CardinalDirection cardinalDirection,
                                        final String actionName,
                                        final AvTimeSpan standardDuration,
                                        final SpatialConnectionData.SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return con(to, wo, cardinalDirection, () -> actionName, standardDuration,
                scMoveTimedDescriptionProvider);
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameSupplier,
                                        final AvTimeSpan standardDuration,
                                        final SpatialConnectionData.SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return con(to, wo, cardinalDirection, actionNameSupplier, standardDuration,
                (Known k, Lichtverhaeltnisse l) ->
                        scMoveDescriptionProvider.getSCMoveDescription(k, l)
                                .timed(standardDuration));
    }

    public static SpatialConnection con(final GameObjectId to,
                                        final String wo,
                                        @Nullable final CardinalDirection cardinalDirection,
                                        final Supplier<String> actionNameSupplier,
                                        final AvTimeSpan standardDuration,
                                        final SpatialConnectionData.SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return con(to,
                conData(
                        wo,
                        cardinalDirection,
                        requireNonNull(actionNameSupplier, "action name supplier expected to be "
                                + "non-null. Use conNichtSC() to create a SpatialConnection "
                                + "the SC cannot use"),
                        standardDuration,
                        scMoveTimedDescriptionProvider
                ));
    }

    /**
     * Erzeugt eine SpatialConnection, die der SC niemals benutzen kann. Damit können z.B.
     * die NSCs "in der Ferne verschwinden" o.Ä.
     */
    public static SpatialConnection conNichtSC(final GameObjectId to,
                                               final String wo,
                                               final AvTimeSpan standardDuration) {
        return conNichtSC(to, wo, null, standardDuration);
    }

    /**
     * Erzeugt eine SpatialConnection, die der SC niemals benutzen kann. Damit können z.B.
     * die NSCs "in der Ferne verschwinden" o.Ä.
     */
    public static SpatialConnection conNichtSC(final GameObjectId to,
                                               final String wo,
                                               @Nullable final CardinalDirection cardinalDirection,
                                               final AvTimeSpan standardDuration) {
        return con(to, conDataNichtSC(wo, cardinalDirection, standardDuration));
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

    @Nullable
    public String getActionName() {
        return data.getActionName();
    }

    /**
     * Die Himmelsrichtung der Bewegung.
     */
    @Nullable
    public CardinalDirection getCardinalDirection() {
        return data.getCardinalDirection();
    }

    public GameObjectId getTo() {
        return to;
    }

    public AvTimeSpan getStandardDuration() {
        return data.getStandardDuration();
    }

    public String getWo() {
        return data.getWo();
    }

    public SpatialConnectionData.SCMoveTimedDescriptionProvider getSCMoveDescriptionProvider() {
        return data.getSCMoveTimedDescriptionProvider();
    }

    @NonNull
    @Override
    public String toString() {
        return data.toString();
    }
}