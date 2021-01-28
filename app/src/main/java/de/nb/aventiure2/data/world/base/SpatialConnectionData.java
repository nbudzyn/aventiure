package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Supplier;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.TimedDescription;

import static com.google.common.base.Preconditions.checkArgument;

public class SpatialConnectionData {
    /**
     * Eine Adverbiale Angabe, die diese Verbinung
     * räumlich beschreibt
     * (aus Sicht des
     * {@link ILocationGO},
     * nicht unbedingt aus Sicht von <code>to</code>:
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

    public static SpatialConnectionData conData(
            final String wo,
            final String actionDescription,
            final AvTimeSpan standardDuration,
            final AbstractDescription<?> newLocationDescription) {
        return conData(wo, actionDescription, standardDuration,
                newLocationDescription.timed(standardDuration));
    }

    public static SpatialConnectionData conData(
            final String wo,
            final String actionDescription,
            final AvTimeSpan standardDuration,
            final TimedDescription<?> newLocationDescription) {
        return conData(wo, actionDescription, standardDuration,
                (SCMoveTimedDescriptionProvider)
                        (isnewLocationKnown, lichtverhaeltnisseInNewLocation) ->
                                newLocationDescription);
    }

    public static SpatialConnectionData conData(
            final String wo,
            final String actionName,
            final AvTimeSpan standardDuration,
            final SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return conData(wo, actionName, standardDuration,
                (Known k, Lichtverhaeltnisse l) ->
                        scMoveDescriptionProvider.getSCMoveDescription(k, l)
                                .timed(standardDuration));
    }

    public static SpatialConnectionData conData(
            final String wo,
            final String actionName,
            final AvTimeSpan standardDuration,
            final SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return conData(wo, () -> actionName, standardDuration, scMoveTimedDescriptionProvider);
    }

    public static SpatialConnectionData conData(
            final String wo,
            final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            final SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return conData(wo, actionNameProvider, standardDuration,
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
        return new SpatialConnectionData(
                wo,
                null,
                standardDuration,
                null
        );
    }


    public static SpatialConnectionData conData(
            final String wo,
            @Nullable final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            @Nullable final SCMoveTimedDescriptionProvider scMoveTimedDescriptionProvider) {
        return new SpatialConnectionData(
                wo,
                actionNameProvider,
                standardDuration,
                scMoveTimedDescriptionProvider
        );
    }

    private SpatialConnectionData(
            final String wo,
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

    @FunctionalInterface
    public interface SCMoveTimedDescriptionProvider {
        TimedDescription<?> getSCMoveTimedDescription(
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