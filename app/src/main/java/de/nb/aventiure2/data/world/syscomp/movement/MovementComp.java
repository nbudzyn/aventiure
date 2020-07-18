package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialStandardStep;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementStep.Phase.FIRST_LEAVING;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementStep.Phase.SECOND_ENTERING;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Component for a {@link GameObject}: The game object
 * moves through the world autonomously.
 * <p>
 * Das Konzept ist in etwa so:
 * <ul>
 * <li>Game Objects (Wesen) mit einer MovementComp können eine <i>Target Location</i> haben -
 * ein Ziel, auf dass sie sich zubewegen. Haben sie <i>keine</i> Target Location, stehen sie
 * still.
 * <li>Die Wesen gehen Schritt für Schritt in Richtung Target Location. Der aktuelle Schritt
 * ist ein {@link MovementStep}.
 * <li>Ein Schritt besteht aus zwei Phasen:
 * <ul>
 * <li>In der ersten Phase ist das Wesen noch an seinem Ausgangsort, aber schon in Bewegung auf
 * den Folgeort zu
 * <li>In der zweiten Phase ist das Wesen schon am Folgeort, aber es <i>ist noch in Bewegung</i>
 * und damit noch nicht wirklich am "Zentrum" des Folgeorts angekommen.
 * <li>Beide Phasen dauern gleich lang.
 * <li>Die Interaktion des Benutzers mit einem Wesen <i>in Bewegung</i> könnte eingeschränkt sein
 * <li>Ist das Wesen am "Zentrum" des Folgeorts angekommen (zweite Phase abgeschlossen),
 * wird der nächste Schritt in Richtung Target Location ermittelt.
 * <li>Ist das Wesen <i>am "Zentrum"</i> der Target Location angekommen, endet die Bewegung.
 * </ul>
 * <li>Der SC könnte sehen, wie das Wesen einen Raum verlässt (1. Phase), oder einen Folgeraum
 * betritt (zweite Phase). Das Interface {@link IMovementNarrator} bietet Methoden,
 * mit denen reagiert werden kann: Z.B. könnte eine Beschreibung erzeugt werden ("die Hexe geht
 * den Weg hinab") oder gespeichert werden, dass der Benutzer das Wesen jetzt kennt
 * (vgl. {@link de.nb.aventiure2.data.world.syscomp.memory.MemoryComp}.
 * <li>Wird die Location eines Wesens während seiner Bewegung durch einen Dritten auf andere
 * Weise verändert (Benutzer hebt das Wesen auf setzt es an anderer Stelle wieder ab o.Ä.),
 * versucht das Wesen, seine Bewegung von seinem neuen Ort fortzusetzen.
 * </ul>
 */
public class MovementComp extends AbstractStatefulComponent<MovementPCD> {
    private final World world;

    private final SpatialConnectionSystem spatialConnectionSystem;

    private final LocationComp locationComp;

    /**
     * The initial movement target - if any
     */
    @Nullable
    private final GameObjectId initialTargetLocationId;

    /**
     * Constructor for a {@link MovementComp}.
     */
    public MovementComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final World world,
                        final SpatialConnectionSystem spatialConnectionSystem,
                        final LocationComp locationComp,
                        @Nullable final GameObjectId initialTargetLocationId) {
        super(gameObjectId, db.movementDao());
        this.world = world;
        this.spatialConnectionSystem = spatialConnectionSystem;
        this.locationComp = locationComp;
        this.initialTargetLocationId = initialTargetLocationId;
    }

    @Override
    @NonNull
    protected MovementPCD createInitialState() {
        return new MovementPCD(getGameObjectId(), initialTargetLocationId);
    }

    public AvTimeSpan startMovement(final AvDateTime now,
                                    final GameObjectId targetLocationId,
                                    @Nullable
                                    final ILeavingStartedNarrator leavingStartedNarrator) {
        checkNotNull(targetLocationId, "targetLocationId is null");

        if (getGameObjectId().equals(targetLocationId)) {
            throw new IllegalStateException("A game object cannot move inside itself.");
        }

        getPcd().setTargetLocationId(targetLocationId);
        setupNextStepIfNecessaryAndPossible(now);

        return hasCurrentStep() ?
                narrateAndDoMovementIfExperiencedBySCStartsLeaving(leavingStartedNarrator) :
                noTime();
    }

    public void stopMovement() {
        getPcd().setTargetLocationId(null);
        getPcd().setCurrentStep(null);
    }

    /**
     * Supposed to be called regularly.
     */
    public AvTimeSpan onTimePassed(final AvDateTime now,
                                   @Nullable final IMovementNarrator movementNarrator) {
        if (!isMoving()) {
            return noTime();
        }

        // Wurde das Game Object zwischenzeitlich versetzt?
        if (currentStepHasPhase(FIRST_LEAVING) &&
                !locationComp.hasLocation(getCurrentStep().getFrom()) ||
                currentStepHasPhase(SECOND_ENTERING) &&
                        !locationComp.hasLocation(getCurrentStep().getTo())) {
            setupNextStepIfNecessaryAndPossible(now);
        }

        if (!hasCurrentStep()) {
            return noTime();
        }

        AvTimeSpan extraTime = noTime();

        while (true) {
            if (currentStepHasPhase(FIRST_LEAVING)) {
                if (now.isEqualOrAfter(getCurrentStep().getExpLeaveDoneTime())) {
                    extraTime =
                            extraTime.plus(leaveAndEnterAndNarrateIfSCPresent(movementNarrator));
                } else {
                    break;
                }
            }

            if (currentStepHasPhase(SECOND_ENTERING)) {
                // FIXME Solange dieser Zeitpunkt noch NICHT erreicht ist,
                //  müsste die IMovingGO eigentlich für gewisse
                //  Interaktionen mit dem Spieler (Reden, nehmen, geben...)
                //  gesperrt sein, und es könnte eine Aktion "Auf die Zauberin warten"
                //  o.Ä. geben!
                if (now.isEqualOrAfter(getCurrentStep().getExpDoneTime())) {
                    setupNextStepIfNecessaryAndPossible(getCurrentStep().getExpDoneTime());
                    if (!hasCurrentStep()) {
                        break;
                    }

                    extraTime =
                            extraTime.plus(narrateAndDoMovementIfExperiencedBySCStartsLeaving(
                                    movementNarrator));
                } else {
                    break;
                }
            }
        }

        return extraTime;
    }

    private void setupNextStepIfNecessaryAndPossible(final AvDateTime startTime) {
        if (!isMoving()) {
            getPcd().setCurrentStep(null);
            return;
        }

        if (locationComp.hasLocation(getTargetLocationId())) {
            getPcd().setTargetLocationId(null);
            getPcd().setCurrentStep(null);
            return;
        }

        getPcd().setCurrentStep(calculateStep(startTime));
    }

    @Nullable
    private MovementStep calculateStep(final AvDateTime startTime) {
        final ILocationGO from = locationComp.getLocation();

        if (!(from instanceof ISpatiallyConnectedGO)) {
            return null;
        }

        @Nullable final SpatialStandardStep firstStep =
                spatialConnectionSystem
                        .findFirstStep((ISpatiallyConnectedGO) from, getTargetLocation());

        return toMovementStep(from, firstStep, startTime);
    }

    @Contract("_, null, _ -> null; _, !null, _ -> new")
    private static MovementStep toMovementStep(
            final ILocationGO from,
            @Nullable final SpatialStandardStep spatialStandardStep,
            final AvDateTime startTime) {
        if (spatialStandardStep == null) {
            return null;
        }

        return new MovementStep(
                from.getId(),
                spatialStandardStep.getTo(),
                startTime,
                // STORY Allow for different velocities based on this standard duration
                spatialStandardStep.getStandardDuration());
    }

    private AvTimeSpan narrateAndDoMovementIfExperiencedBySCStartsLeaving(
            @Nullable final ILeavingStartedNarrator leavingStartedNarrator) {
        if (!world.loadSC().locationComp().hasRecursiveLocation(locationComp.getLocation())) {
            return noTime();
        }

        if (leavingStartedNarrator == null) {
            return noTime();
        }

        return leavingStartedNarrator.narrateAndDoMovementAsExperiencedBySCStartsLeaving(
                (ILocationGO & ISpatiallyConnectedGO) locationComp.getLocation(),
                (ILocationGO) world.load(getCurrentStep().getTo())
        );
    }

    @NonNull
    private AvTimeSpan leaveAndEnterAndNarrateIfSCPresent(
            @Nullable final IMovementNarrator movementNarrator) {
        return locationComp.narrateAndSetLocation(
                getCurrentStep().getTo(),
                () -> {
                    getCurrentStep().setPhase(SECOND_ENTERING);

                    if (!world.loadSC().locationComp().hasRecursiveLocation(
                            getCurrentStep().getTo())) {
                        return noTime();
                    }

                    if (movementNarrator == null) {
                        return noTime();
                    }

                    return movementNarrator.narrateAndDoMovementAsExperiencedBySCStartsEntering(
                            (ILocationGO & ISpatiallyConnectedGO)
                                    world.load(getCurrentStep().getFrom()),
                            (ILocationGO) world.load(getCurrentStep().getTo()));
                });
    }

    public boolean isMoving() {
        return getPcd().getTargetLocationId() != null;
    }

    @Nullable
    public ILocationGO getTargetLocation() {
        @Nullable final GameObjectId targetLocationId = getTargetLocationId();
        if (targetLocationId == null) {
            return null;
        }

        return (ILocationGO) world.load(targetLocationId);
    }

    @Nullable
    private GameObjectId getTargetLocationId() {
        return getPcd().getTargetLocationId();
    }

    public boolean currentStepHasPhase(final MovementStep.Phase phase) {
        if (!hasCurrentStep()) {
            return false;
        }

        return getCurrentStep().hasPhase(phase);
    }

    private boolean hasCurrentStep() {
        return getCurrentStep() != null;
    }

    @Nullable
    private MovementStep getCurrentStep() {
        return getPcd().getCurrentStep();
    }
}
