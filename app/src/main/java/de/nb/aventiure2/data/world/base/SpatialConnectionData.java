package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import java.util.function.Supplier;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AbstractAdverbialeAngabe;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;

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
    private final Supplier<String> actionNameProvider;
    private final SCMoveDescriptionProvider scMoveDescriptionProvider;

    public static SpatialConnectionData conData(
            final String wo,
            final String actionDescription,
            final AvTimeSpan standardDuration,
            final TimedDescription<?> newLocationDescription) {
        return conData(wo, actionDescription, standardDuration,
                (isnewLocationKnown, lichtverhaeltnisseInNewLocation) -> newLocationDescription);
    }

    public static SpatialConnectionData conData(
            final String wo,
            final String actionName,
            final AvTimeSpan standardDuration,
            final SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return conData(wo, () -> actionName, standardDuration, scMoveDescriptionProvider);
    }

    public static SpatialConnectionData conData(
            final String wo,
            final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            final SCMoveDescriptionProvider scMoveDescriptionProvider) {
        return new SpatialConnectionData(
                wo,
                actionNameProvider,
                standardDuration,
                scMoveDescriptionProvider
        );
    }

    private SpatialConnectionData(
            final String wo,
            final Supplier<String> actionNameProvider,
            final AvTimeSpan standardDuration,
            final SpatialConnectionData.SCMoveDescriptionProvider scMoveDescriptionProvider) {
        this.wo = wo;
        this.actionNameProvider = actionNameProvider;
        this.standardDuration = standardDuration;
        this.scMoveDescriptionProvider = scMoveDescriptionProvider;
    }

    String getActionName() {
        return actionNameProvider.get();
    }

    AvTimeSpan getStandardDuration() {
        return standardDuration;
    }

    AbstractAdverbialeAngabe getWoAdvAngabe() {
        return new AdverbialeAngabeSkopusVerbAllg(getWo());
    }

    public String getWo() {
        return wo;
    }

    SCMoveDescriptionProvider getSCMoveDescriptionProvider() {
        return scMoveDescriptionProvider;
    }

    @FunctionalInterface
    public interface SCMoveDescriptionProvider {
        TimedDescription<?> getSCMoveDescription(
                Known newLocationKnown,
                Lichtverhaeltnisse lichtverhaeltnisseInNewLocation);
    }

    @NonNull
    @Override
    public String toString() {
        return "SpatialConnectionData";
    }
}