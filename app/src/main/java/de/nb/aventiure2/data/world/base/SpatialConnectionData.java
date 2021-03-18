package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Supplier;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.TimedDescription;

import static com.google.common.base.Preconditions.checkArgument;

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
    private final SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider;

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

    public static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final String actionDescription,
            final AvTimeSpan standardDuration,
            final AbstractDescription<?> newLocationDescription) {
        return conData(wo, cardinalDirection, actionDescription, standardDuration,
                newLocationDescription.timed(standardDuration));
    }

    public static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final String actionDescription,
            final AvTimeSpan standardDuration,
            final TimedDescription<?> newLocationDescription) {
        return conData(wo, cardinalDirection, actionDescription, standardDuration,
                (SCMoveTimedDescriptionProvider)
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

    public static SpatialConnectionData conData(
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
            final SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return conData(wo, null, actionName, standardDuration,
                scMoveTimedDescriptionProvider);
    }

    public static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            final String actionName,
            final AvTimeSpan standardDuration,
            final SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return conData(wo, cardinalDirection, () -> actionName, standardDuration,
                scMoveTimedDescriptionProvider);
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

    public static SpatialConnectionData conData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            @Nullable final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            @Nullable final SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return new SpatialConnectionData(
                wo,
                cardinalDirection, actionNameProvider,
                standardDuration,
                scMoveTimedDescriptionProvider
        );
    }

    private SpatialConnectionData(
            final String wo,
            @Nullable final CardinalDirection cardinalDirection,
            @Nullable final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            @Nullable final SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        checkArgument((actionNameProvider != null && scMoveTimedDescriptionProvider != null)
                        ||
                        (actionNameProvider == null && scMoveTimedDescriptionProvider == null),
                "actionNameProvider und scMoveTimedDescriptionProvider "
                        + "müssen entweder beide null sein (für nicht-SC-Connection-Data) oder "
                        + "beide ungleich null. actionNameProvider: "
                        + "%s, scMoveTimedDescriptionProvider: %s", actionNameProvider,
                scMoveTimedDescriptionProvider);

        this.wo = wo;
        this.cardinalDirection = cardinalDirection;
        this.actionNameProvider = actionNameProvider;
        this.standardDuration = standardDuration;
        this.scMoveTimedDescriptionProvider = scMoveTimedDescriptionProvider;
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
    SCMoveTimedDescriptionProvider getSCMoveTimedDescriptionProvider() {
        return scMoveTimedDescriptionProvider;
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
    public interface SCMoveTimedDescriptionProvider {
        TimedDescription<?>
            // FIXME mehrere Alternativen als Rückgabewert erlauben
        getSCMoveTimedDescription(
                Known newLocationKnown,
                Lichtverhaeltnisse lichtverhaeltnisseInNewLocation);
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