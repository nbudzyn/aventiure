package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementStep.Phase.FIRST_LEAVING;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementStep.Phase.SECOND_ENTERING;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * Component for a {@link GameObject}: The game object
 * moves through the world autonomously.
 */
public class MovementComp extends AbstractStatefulComponent<MovementPCD> {
    // STORY: Ein Wolf könnte nachts "unsichtbar" zufällig durch den
    //  Wald laufen und immer mal heulen oder rascheln, wenn er direkt beim SC
    //  vorbeiläuft.

    private final World world;

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
                        final LocationComp locationComp,
                        @Nullable final GameObjectId initialTargetLocationId) {
        super(gameObjectId, db.movementDao());
        this.world = world;
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

        return hasCurrentStep() ? narrateLeavingStartedIfSCIsPresent(leavingStartedNarrator) :
                noTime();
    }

    public void stopMovement() {
        getPcd().setTargetLocationId(null);
        getPcd().setCurrentStep(null);
    }

    /**
     * Supposed to be called regularly.
     */
    public AvTimeSpan onTimePassed(final AvDateTime now, final IMovementNarrator movementNarrator) {
        if (!isMoving()) {
            return noTime();
        }

        // Wurde das Game Object zwischenzeitlich versetzt?
        if (!hasCurrentStep() ||
                (getCurrentStep().hasPhase(FIRST_LEAVING) &&
                        !locationComp.hasLocation(getCurrentStep().getFrom())) ||
                (getCurrentStep().hasPhase(SECOND_ENTERING) &&
                        !locationComp.hasLocation(getCurrentStep().getTo()))) {
            setupNextStepIfNecessaryAndPossible(now);
        }

        if (!hasCurrentStep()) {
            return noTime();
        }

        AvTimeSpan extraTime = noTime();

        while (true) {
            if (getCurrentStep().hasPhase(FIRST_LEAVING)) {
                if (now.isEqualOrAfter(getCurrentStep().getExpLeaveDoneTime())) {
                    extraTime =
                            extraTime.plus(leaveAndEnterAndNarrateIfSCPresent(movementNarrator));
                } else {
                    break;
                }
            }

            if (getCurrentStep().hasPhase(SECOND_ENTERING)) {
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
                            extraTime.plus(narrateLeavingStartedIfSCIsPresent(movementNarrator));
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
    private MovementStep calculateStep(final AvDateTime start) {
        if (locationComp.getLocation() == null) {
            return null;
        }

        if (getTargetLocation().is(VOR_DEM_ALTEN_TURM)) {
            if (locationComp.getLocation().is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
                return new MovementStep(
                        DRAUSSEN_VOR_DEM_SCHLOSS,
                        IM_WALD_NAHE_DEM_SCHLOSS,
                        start,
                        mins(10));
            }

            if (locationComp.getLocation().is(IM_WALD_NAHE_DEM_SCHLOSS)) {
                return new MovementStep(
                        IM_WALD_NAHE_DEM_SCHLOSS,
                        VOR_DEM_ALTEN_TURM,
                        start,
                        mins(30));
            }

            // STORY Andere Wege ermitteln (Pathfinding, X*...),
            //  soweit es einen Weg gibt
            return null;
        }

        if (getTargetLocation().is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            if (locationComp.getLocation().is(VOR_DEM_ALTEN_TURM)) {
                return new MovementStep(
                        VOR_DEM_ALTEN_TURM,
                        IM_WALD_NAHE_DEM_SCHLOSS,
                        start,
                        mins(30));
            }

            if (locationComp.getLocation().is(IM_WALD_NAHE_DEM_SCHLOSS)) {
                return new MovementStep(
                        IM_WALD_NAHE_DEM_SCHLOSS,
                        DRAUSSEN_VOR_DEM_SCHLOSS,
                        start,
                        mins(10));
            }

            // STORY Andere Wege ermitteln (Pathfinding, X*...)
            return null;
        }

        // STORY Wege für andere Ziele ermitteln (Pathfinding, X*...)
        return null;
    }

    private AvTimeSpan narrateLeavingStartedIfSCIsPresent(
            final ILeavingStartedNarrator leavingStartedNarrator) {
        if (!world.loadSC().locationComp().hasRecursiveLocation(locationComp.getLocation())) {
            return noTime();
        }

        return leavingStartedNarrator.narrateAndDoMovementAsExperiencedBySCStartsLeaving(
                (ILocationGO & ISpatiallyConnectedGO) locationComp.getLocation(),
                (ILocationGO) world.load(getCurrentStep().getTo())
        );
    }

    private AvTimeSpan leaveAndEnterAndNarrateIfSCPresent(
            final IMovementNarrator movementNarrator) {
        return locationComp.narrateAndSetLocation(
                getCurrentStep().getTo(),
                () -> {
                    getCurrentStep().setPhase(SECOND_ENTERING);

                    if (!world.loadSC().locationComp().hasRecursiveLocation(
                            getCurrentStep().getTo())) {
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

    private boolean hasCurrentStep() {
        return getCurrentStep() != null;
    }

    @Nullable
    private MovementStep getCurrentStep() {
        return getPcd().getCurrentStep();
    }
}
