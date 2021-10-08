package de.nb.aventiure2.data.world.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.function.Supplier;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.TimedDescription;

public class SpatialConnectionData {
    /**
     * Eine Adverbiale Angabe, die diese Verbindung räumlich beschreibt (aus Sicht des
     * {@link ILocationGO}, nicht unbedingt aus Sicht von <code>to</code>:
     * "auf dem Weg" o.Ä.
     * <p>
     * Die Beschreibung sollte sich auf etwas beziehen, auf dem man sich eine längere Zeit
     * bewegen kann (nicht "in der Tür", sondern "auf der Treppe").
     * <p>
     * Es ist außerdem gut, wenn dieselbe Beschreibung innerhalb eines
     * {@link ISpatiallyConnectedGO}s
     * nicht mehr als zweimal auftritt.
     */
    private final String wo;
    /**
     * Standard-Dauer für die Bewegung: Bewegungszeit eines Menschen (wie des SC), der sich
     * auskennt, tagsüber.
     */
    private final AvTimeSpan standardDuration;

    @Nullable
    private final Supplier<String> actionNameProvider;

    @Nullable
    private final ScMoveAltTimedDescriptionProvider scMoveAltTimedDescriptionProvider;

    /**
     * Himmelrichtung der Bewegung. Kann {@code null}, selbst wenn der SC diese Bewengung
     * durchführen kann (z.B. bei einer Bewegung nach oben oder unten).
     */
    @Nullable
    private final CardinalDirection cardinalDirection;

    public static SpatialConnectionData conData(
            final String wo,
            final String actionDescription,
            final AvTimeSpan standardDuration,
            final AbstractDescription<?> newLocationDescription) {
        return conData(wo, null, actionDescription, standardDuration,
                newLocationDescription);
    }

    @SuppressWarnings("SameParameterValue")
    private static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final String actionDescription,
            final AvTimeSpan standardDuration,
            final AbstractDescription<?> newLocationDescription) {
        return conData(wo, cardinalDirection, actionDescription, standardDuration,
                newLocationDescription.timed(standardDuration));
    }

    private static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final String actionDescription,
            final AvTimeSpan standardDuration,
            final TimedDescription<?> newLocationDescription) {
        return conData(wo, cardinalDirection, actionDescription, standardDuration,
                (ScMoveTimedDescriptionProvider)
                        (isnewLocationKnown, lichtverhaeltnisseInNewLocation) ->
                                newLocationDescription);
    }

    public static SpatialConnectionData conData(
            final String wo,
            final String actionName,
            final AvTimeSpan standardDuration,
            final SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return conData(wo, null, actionName, standardDuration, scMoveDescriptionProvider);
    }

    @SuppressWarnings("SameParameterValue")
    private static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final String actionName,
            final AvTimeSpan standardDuration,
            final SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return conData(wo, cardinalDirection, actionName, standardDuration,
                (Known k, Lichtverhaeltnisse l) ->
                        scMoveDescriptionProvider.getSCMoveDescription(k, l)
                                .timed(standardDuration));
    }

    public static SpatialConnectionData conData(
            final String wo,
            final String actionName,
            final AvTimeSpan standardDuration,
            final ScMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return conData(wo, null, actionName, standardDuration,
                scMoveTimedDescriptionProvider);
    }

    public static SpatialConnectionData conDataAltDescTimed(
            final String wo,
            final String actionName,
            final AvTimeSpan standardDuration,
            final ScMoveAltTimedDescriptionProvider scMoveAltTimedDescriptionProvider) {
        return conDataAltDescTimed(wo, null, actionName, standardDuration,
                scMoveAltTimedDescriptionProvider);
    }

    private static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final String actionName,
            final AvTimeSpan standardDuration,
            final ScMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return conData(wo, cardinalDirection, () -> actionName, standardDuration,
                scMoveTimedDescriptionProvider);
    }

    @SuppressWarnings("SameParameterValue")
    private static SpatialConnectionData conDataAltDescTimed(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final String actionName,
            final AvTimeSpan standardDuration,
            final ScMoveAltTimedDescriptionProvider scMoveAltTimedDescriptionProvider) {
        return conDataAltDescTimed(wo, cardinalDirection, () -> actionName, standardDuration,
                scMoveAltTimedDescriptionProvider);
    }

    public static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            final SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return conData(wo, cardinalDirection, actionNameProvider, standardDuration,
                (Known k, Lichtverhaeltnisse l) ->
                        scMoveDescriptionProvider.getSCMoveDescription(k, l)
                                .timed(standardDuration));
    }

    /**
     * Erzeugt ein SpatialConnectionData, das der SC niemals benutzen kann. Damit können z.B.
     * die NSCs "in der Ferne verschwinden" o.Ä.
     */
    static SpatialConnectionData conDataNichtSC(
            final String wo,
            final AvTimeSpan standardDuration) {
        return conDataNichtSC(wo, null, standardDuration);
    }

    /**
     * Erzeugt ein SpatialConnectionData, das der SC niemals benutzen kann. Damit können z.B.
     * die NSCs "in der Ferne verschwinden" o.Ä.
     */
    static SpatialConnectionData conDataNichtSC(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final AvTimeSpan standardDuration) {
        return new SpatialConnectionData(
                wo,
                cardinalDirection, null,
                standardDuration,
                null
        );
    }

    private static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            @Nullable final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            @Nullable final ScMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return conDataAltDescTimed(
                wo,
                cardinalDirection, actionNameProvider,
                standardDuration,
                scMoveTimedDescriptionProvider
        );
    }

    static SpatialConnectionData conDataAltDesc(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            @Nullable final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            @Nullable final ScMoveAltDescriptionProvider scMoveDescriptionProvider) {
        return conDataAltDescTimed(wo, cardinalDirection, actionNameProvider, standardDuration,
                scMoveDescriptionProvider == null ? null :
                        (Known k, Lichtverhaeltnisse l) ->
                                scMoveDescriptionProvider.altScMoveDescriptions(k, l).stream()
                                        .map(d -> d.timed(standardDuration))
                                        .collect(toImmutableSet()));
    }

    static SpatialConnectionData conDataAltDescTimed(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            @Nullable final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            @Nullable final ScMoveAltTimedDescriptionProvider scMoveAltTimedDescriptionProvider) {
        return new SpatialConnectionData(
                wo,
                cardinalDirection, actionNameProvider,
                standardDuration,
                scMoveAltTimedDescriptionProvider
        );
    }

    private SpatialConnectionData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            @Nullable final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            @Nullable final ScMoveAltTimedDescriptionProvider scMoveAltTimedDescriptionProvider) {
        checkArgument((actionNameProvider != null && scMoveAltTimedDescriptionProvider != null)
                        ||
                        (actionNameProvider == null && scMoveAltTimedDescriptionProvider == null),
                "actionNameProvider und scMoveTimedDescriptionProvider "
                        + "müssen entweder beide null sein (für nicht-SC-Connection-Data) oder "
                        + "beide ungleich null. actionNameProvider: "
                        + "%s, scMoveTimedDescriptionProvider: %s", actionNameProvider,
                scMoveAltTimedDescriptionProvider);

        this.wo = wo;
        this.cardinalDirection = cardinalDirection;
        this.actionNameProvider = actionNameProvider;
        this.standardDuration = standardDuration;
        this.scMoveAltTimedDescriptionProvider = scMoveAltTimedDescriptionProvider;
    }

    /**
     * Gibt den Namen der Bewegungsaktion für den SC zurück - oder {@code null}, wenn
     * der SC diese Bewegung nicht durchführen kann (sondern nur NSCs).
     */
    @Nullable
    public String getActionName() {
        if (actionNameProvider == null) {
            return null;
        }

        return actionNameProvider.get();
    }

    AvTimeSpan getStandardDuration() {
        return standardDuration;
    }

    public String getWo() {
        return wo;
    }

    @Nullable
    ScMoveAltTimedDescriptionProvider getScMoveAltTimedDescriptionProvider() {
        return scMoveAltTimedDescriptionProvider;
    }

    /**
     * Die Himmelsrichtung der Bewegung. Kann nur {@code null}, sein wenn der SC diese
     * Bewegung niemals durchführen kann.
     */
    @Nullable
    public CardinalDirection getCardinalDirection() {
        return cardinalDirection;
    }

    @FunctionalInterface
    public interface ScMoveAltTimedDescriptionProvider {
        ImmutableCollection<TimedDescription<?>> altScMoveTimedDescriptions(
                final Known newLocationKnown,
                final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation);
    }

    @FunctionalInterface
    public interface ScMoveTimedDescriptionProvider extends ScMoveAltTimedDescriptionProvider {
        @Override
        default ImmutableCollection<TimedDescription<?>> altScMoveTimedDescriptions(
                final Known newLocationKnown,
                final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation) {
            return ImmutableList.of(
                    getScMoveTimedDescription(newLocationKnown, lichtverhaeltnisseInNewLocation));
        }

        TimedDescription<?> getScMoveTimedDescription(
                Known newLocationKnown,
                Lichtverhaeltnisse lichtverhaeltnisseInNewLocation);
    }

    @FunctionalInterface
    public interface ScMoveAltDescriptionProvider {
        ImmutableCollection<AbstractDescription<?>> altScMoveDescriptions(
                final Known newLocationKnown,
                final Lichtverhaeltnisse lichtverhaeltnisseInNewLocation);
    }

    @FunctionalInterface
    public interface SCMoveDescriptionProvider {
        AbstractDescription<?> getSCMoveDescription(
                Known newLocationKnown,
                Lichtverhaeltnisse lichtverhaeltnisseInNewLocation);
    }

    @NonNull
    @Override
    public String toString() {
        return wo;
    }
}