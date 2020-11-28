package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.ISCActionDoneListenerComponent;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialStandardStep;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.DO_START_LEAVING;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.PAUSED;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.UNPAUSED;

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
 * <li>Zu Beginn eines Schritts könnte die Bewegung PAUSED werden - wenn der SC die Chance
 * bekommen soll, mit dem IMovingGO zu interagieren.
 * <li>Ein eigentliche Schritt beginnt dann damit, dass das IMovingGO seine Location verliert.
 * Es befindet sich nun im "Dawzischen" zwischen from und to.
 * <li>Am Ende des Schritts kommt das Wesen auf dem Folgeort an. Die Movement-Listener erhalten die
 * Information, das Wesen sei von from nach to gewechselt.
 * <li>Nach dem Schritt wird der nächste Schritt in Richtung Target Location ermittelt.
 * <li>Ist das Wesen an seiner Target Location angekommen, endet die Bewegung.
 * </ul>
 * <li>Der SC könnte sehen, wie das Wesen einen Raum verlässt (Beginn des Schritts), oder einen
 * Folgeraum betritt (Ende des SChritts). Das Interface {@link IMovementNarrator} bietet Methoden,
 * mit denen reagiert werden kann: Z.B. könnte eine Beschreibung erzeugt werden ("die Hexe geht
 * den Weg hinab") oder gespeichert werden, dass der Benutzer das Wesen jetzt kennt
 * (vgl. {@link de.nb.aventiure2.data.world.syscomp.memory.MemoryComp}.
 * <li>Wird die Location eines Wesens während seiner Bewegung durch einen Dritten auf andere
 * Weise verändert (Benutzer hebt das Wesen auf, setzt es an anderer Stelle wieder ab o.Ä.),
 * versucht das Wesen, seine Bewegung von seinem neuen Ort fortzusetzen.
 * </ul>
 */
public class MovementComp
        extends AbstractStatefulComponent<MovementPCD>
        implements ISCActionDoneListenerComponent {
    private final World world;

    private final SpatialConnectionSystem spatialConnectionSystem;

    private final IMovementNarrator movementNarrator;
    private final LocationComp locationComp;

    /**
     * The initial movement target - if any
     */
    @Nullable
    private final GameObjectId initialTargetLocationId;

    /**
     * Relative Geschwindigkeit. 1 = Standard-Geschwindigkeit (Geschwindigkeit des SC, der
     * sich aufkennt)
     */
    private final float relativeVelocity;

    /**
     * Constructor for a {@link MovementComp}.
     */
    public MovementComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final World world,
                        final SpatialConnectionSystem spatialConnectionSystem,
                        final IMovementNarrator movementNarrator,
                        final LocationComp locationComp,
                        final float relativeVelocity,
                        @Nullable final GameObjectId initialTargetLocationId) {
        super(gameObjectId, db.movementDao());
        this.world = world;
        this.spatialConnectionSystem = spatialConnectionSystem;
        this.movementNarrator = movementNarrator;
        this.locationComp = locationComp;
        this.relativeVelocity = relativeVelocity;
        this.initialTargetLocationId = initialTargetLocationId;
    }

    @Override
    @NonNull
    protected MovementPCD createInitialState() {
        return new MovementPCD(getGameObjectId(), initialTargetLocationId);
    }

    public void startMovement(final AvDateTime now,
                              final GameObjectId targetLocationId) {
        checkNotNull(targetLocationId, "targetLocationId is null");

        if (getGameObjectId().equals(targetLocationId)) {
            throw new IllegalStateException("A game object cannot move inside itself.");
        }

        getPcd().setTargetLocationId(targetLocationId);
        setupNextStepIfNecessaryAndPossible(now);

        if (hasCurrentStep()) {
            narrateAndDoMovementLeaves();
        }
    }

    public void stopMovement() {
        getPcd().setTargetLocationId(null);
        getPcd().setCurrentStep(null);
        getPcd().setPauseForSCAction(UNPAUSED);
    }

    /**
     * Supposed to be called regularly.
     */
    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void onTimePassed(final AvDateTime now) {
        if (!isMoving()) {
            return;
        }

        // Wurde das Game Object zwischenzeitlich versetzt?
        if (getPcd().getPauseForSCAction() == UNPAUSED &&
                locationComp.getLocation() != null &&
                !locationComp.hasLocation(getCurrentStep().getTo())) {
            // FIXME Ist die Logik korrekt?! Bei DO_START_LEAVING muss man eigentlich
            //  nicht noch einmal rechnen, es sei denn...?!
            setupNextStepIfNecessaryAndPossible(now);

            if (locationComp.hasSameUpperMostLocationAs(SPIELER_CHARAKTER)
                // STORY Wenn der SC schläft, dann hingegen das Game Object einfach
                //  vorbeilaufen lassen (in diesem Fall sollte es ja aber auch keine
                //  Narration geben...)
            ) {
                getPcd().setPauseForSCAction(PAUSED);

                // Dann soll das IMovingGO nicht sofort weiterlaufen - der Spieler soll
                // auf jeden Fall die Gelegenheit bekommen (einmalig) mit dem
                // IMovingGO zu interagieren!
            }
        }

        if (!hasCurrentStep()) {
            return;
        }

        if (getPcd().getPauseForSCAction() == PAUSED) {
            return;
        }

        if (getPcd().getPauseForSCAction() == DO_START_LEAVING) {
            narrateAndDoMovementLeaves();
        }

        // Je nachdem, wie lang der World-Tick war, soll das IMovingGO ggf. auch mehrere
        // Halbschritte gehen
        while (true) {
            //  STORY Zumindest manche Aktionen sollten wohl dazu führen,
            //   dass die Bewegung beendet oder zumindest für eine Weile die Bewegung
            //   "ausgesetzt" wird. Z.B. sollte ein Dialog beendet werden, bis
            //   das IMovingGO wieder weitergeht (sofern das IMovingGO auf den Dialog eingeht und
            //   ihn nicht von sich aus beendet)

            if (now.isEqualOrAfter(getCurrentStep().getExpDoneTime())) {
                locationComp.setLocation(getCurrentStep().getTo());

                if (world.loadSC().locationComp().hasRecursiveLocation(
                        getCurrentStep().getTo())) {
                    narrateAndDoEnters();
                }

                locationComp.narrateAndDoEnterReactions(
                        getCurrentStep().getFrom(), getCurrentStep().getTo()
                );

                // Befindet sich der SC an der Location, die das IMovingGO
                // jetzt gerade erreicht hat?
                if (locationComp.hasSameUpperMostLocationAs(SPIELER_CHARAKTER)
                    // STORY Wenn der SC schläft, dann hingegen das Game Object einfach
                    //  vorbeilaufen lassen (in diesem Fall sollte es ja aber auch keine
                    //  Narration geben...)
                ) {
                    if (locationComp.hasLocation(getTargetLocationId())) {
                        getPcd().setTargetLocationId(null);
                        getPcd().setCurrentStep(null);
                        getPcd().setPauseForSCAction(UNPAUSED);
                        break;
                    }

                    // Dann soll das IMovingGO nicht sofort weiterlaufen - der Spieler soll
                    // auf jeden Fall die Gelegenheit bekommen (einmalig) mit dem
                    // IMovingGO zu interagieren!
                    getPcd().setPauseForSCAction(PAUSED);
                    break;
                }

                setupNextStepIfNecessaryAndPossible(getCurrentStep().getExpDoneTime());
                if (!hasCurrentStep()) {
                    break;
                }

                narrateAndDoMovementLeaves();
            } else {
                break;
            }
        }
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO> void narrateAndDoEnters() {
        final FROM from = (FROM) world.load(getCurrentStep().getFrom());

        @Nullable final SpatialConnection spatialConnection =
                from.spatialConnectionComp()
                        .getConnection(getCurrentStep().getTo());

        final ILocationGO to =
                (ILocationGO) world.load(getCurrentStep().getTo());

        final NumberOfWays numberOfWaysIn =
                to instanceof ISpatiallyConnectedGO ?
                        ((ISpatiallyConnectedGO) to).spatialConnectionComp()
                                .getNumberOfWaysOut() :
                        NumberOfWays.NO_WAY;

        movementNarrator.narrateAndDoEnters(
                from,
                to,
                spatialConnection,
                numberOfWaysIn);
    }

    @Override
    public void onSCActionDone(final AvDateTime startTimeOfUserAction) {
        if (getPcd().getPauseForSCAction() == PAUSED) {
            setupNextStepIfNecessaryAndPossible(startTimeOfUserAction);
        }
    }

    private void setupNextStepIfNecessaryAndPossible(final AvDateTime startTime) {
        if (!isMoving()) {
            getPcd().setCurrentStep(null);
            getPcd().setPauseForSCAction(UNPAUSED);
            return;
        }

        if (locationComp.hasLocation(getTargetLocationId())) {
            getPcd().setTargetLocationId(null);
            getPcd().setCurrentStep(null);
            getPcd().setPauseForSCAction(UNPAUSED);
            return;
        }

        getPcd().setCurrentStep(calculateStep(startTime));

        // FIXME Idee: Wenn der SC ein IMovingBeing überholt: Hinterher einfach
        //  so lange warten, bis es hinterhergekommen ist (damit Interakttion
        //  möglich wird?)

        // FIXME Idee: Wenn ein MovingBeing auf dem Weg zu dem Ort ist, zu dem sich
        //  der SC bewegt hat: Sollte der SC einfach so lange warten, bis das
        //  MovingBeing da ist?? Also kein "kommt dir entgegen" mehr??

        getPcd().setPauseForSCAction(DO_START_LEAVING);
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
    private MovementStep toMovementStep(
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
                spatialStandardStep.getStandardDuration().times(relativeVelocity));
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateAndDoMovementLeaves() {
        getPcd().setPauseForSCAction(UNPAUSED);

        if (world.loadSC().locationComp().hasRecursiveLocation(getCurrentStep().getFrom())) {
            narrateAndDoLeaves();
        }

        locationComp.narrateAndDoLeaveReactions(getCurrentStep().getTo());
        locationComp.unsetLocation();
        // Ab jetzt befindet sich das IMovingBeing im "Dazwischen" zwischen
        // from und to.
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO> void narrateAndDoLeaves() {
        final FROM from = (FROM) world.load(getCurrentStep().getFrom());

        @Nullable final SpatialConnection spatialConnection =
                from.spatialConnectionComp().getConnection(getCurrentStep().getTo());

        movementNarrator.narrateAndDoLeaves(
                from,
                (ILocationGO) world.load(getCurrentStep().getTo()),
                spatialConnection,
                from.spatialConnectionComp().getNumberOfWaysOut());
    }

    public void narrateAndDoScTrifftMovingGOImDazwischen(
            @Nullable final ILocationGO scFrom,
            final ILocationGO scTo) {
        narrateScTrifftMovingGOImDazwischen(scFrom, scTo);

        world.loadSC().memoryComp().upgradeKnown(getGameObjectId());
    }

    public void narrateAndDoScTrifftStehendesMovingGOInTo(
            @Nullable final ILocationGO scFrom,
            final ILocationGO scTo) {
        narrateScTrifftStehendesMovingGOInTo(scFrom, scTo);

        world.loadSC().memoryComp().upgradeKnown(getGameObjectId());
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateScTrifftStehendesMovingGOInTo(@Nullable final ILocationGO scFrom,
                                              final ILocationGO scTo) {
        movementNarrator.narrateScTrifftStehendesMovingGO(
                locationComp.getLocation() != null ?
                        locationComp.getLocation() :
                        scTo);
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    void narrateScTrifftMovingGOImDazwischen(@Nullable final ILocationGO scFrom,
                                             final ILocationGO scTo) {
        movementNarrator.narrateScTrifftMovingGOImDazwischen(
                scFrom,
                scTo,
                (FROM) world.load(getCurrentStep().getFrom()));
    }

    public boolean isMoving() {
        return getPcd().getTargetLocationId() != null;
    }

    @Nullable
    private ILocationGO getTargetLocation() {
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
    public GameObjectId getCurrentStepFrom() {
        @Nullable final MovementStep currentStep = getCurrentStep();

        return currentStep != null ? currentStep.getFrom() : null;
    }

    @Nullable
    public GameObjectId getCurrentStepTo() {
        @Nullable final MovementStep currentStep = getCurrentStep();

        return currentStep != null ? currentStep.getTo() : null;
    }

    @Nullable
    private MovementStep getCurrentStep() {
        return getPcd().getCurrentStep();
    }
}
