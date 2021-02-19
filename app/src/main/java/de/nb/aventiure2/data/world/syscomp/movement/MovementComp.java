package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
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
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.description.TimedDescription;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.DO_START_LEAVING;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.PAUSED;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.UNPAUSED;
import static java.util.Objects.requireNonNull;

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

    @Nullable
    private final AbstractTalkingComp talkingComp;
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
                        @Nullable final AbstractTalkingComp talkingComp,
                        final float relativeVelocity,
                        @Nullable final GameObjectId initialTargetLocationId) {
        super(gameObjectId, db.movementDao());
        this.world = world;
        this.spatialConnectionSystem = spatialConnectionSystem;
        this.movementNarrator = movementNarrator;
        this.locationComp = locationComp;
        this.talkingComp = talkingComp;
        this.relativeVelocity = relativeVelocity;
        this.initialTargetLocationId = initialTargetLocationId;
    }

    @Override
    @NonNull
    protected MovementPCD createInitialState() {
        return new MovementPCD(getGameObjectId(), initialTargetLocationId);
    }

    /**
     * Beginnt die Bewegung.
     */
    public void startMovement(final AvDateTime now,
                              final GameObjectId targetLocationId) {
        startMovement(now, targetLocationId, false);
    }

    /**
     * Beginnt die Bewegung.
     *
     * @param firstStepTakesNoTime Bei <code>false</code> wird der erste Schritt
     *                             ganz normal ausgeführt - bei <code>true</code> wird der erste
     *                             Schritt in 0 Sekunden ausgeführt. Damit lässt sich z.B.
     *                             eine Bewegung in X starten, in der ein NPC dem SC sofort von X
     *                             her entgegenkommt.
     */
    public void startMovement(final AvDateTime now,
                              final GameObjectId targetLocationId,
                              final boolean firstStepTakesNoTime) {
        requireNonNull(targetLocationId, "targetLocationId is null");
        checkArgument(
                !getGameObjectId().equals(targetLocationId),
                "A game object cannot move inside itself.");

        setTargetLocationId(targetLocationId);
        setupNextStepIfNecessaryAndPossible(now, firstStepTakesNoTime);

        if (hasCurrentStep()) {
            narrateAndDoMovementLeaves();
        }
    }

    @Override
    public void onSCActionDone(final AvDateTime startTimeOfUserAction) {
        requirePcd().setHatDenSCGeradeVerlassen(false);

        if (requirePcd().getPauseForSCAction() == PAUSED && !stayPaused()) {
            setupNextStepIfNecessaryAndPossible(startTimeOfUserAction);
        }
    }

    /**
     * Ermittelt, ob im Fall, dass gerade eine Pause eingelegt ist, die Pause
     * fortgesetzt werden soll.
     */
    private boolean stayPaused() {
        return talkingComp != null && talkingComp.isInConversation();
    }

    /**
     * Supposed to be called regularly. Moves the {@link IMovingGO} - in case it is
     * moving and not paused.
     * <p>
     * Am Ende der Methode befindet sich das {@link IMovingGO} an einer Location - oder im
     * "Dazwischen" vor dem Zielort des aktuellen Schritts.
     */
    public void onTimePassed(final AvDateTime now) {
        if (!isMoving()) {
            return;
        }

        // Wurde das Game Object zwischenzeitlich versetzt?
        if (locationComp.getLocation() != null &&
                !locationComp.hasLocation(getCurrentStepToId()) &&
                requirePcd().getPauseForSCAction() == UNPAUSED) {
            setupNextStepIfNecessaryAndPossible(now);
            pauseIfSameOuterMostLocationWithSC();
        }

        if (!hasCurrentStep() || requirePcd().getPauseForSCAction() == PAUSED) {
            return;
        }

        narrateAndMove(now);
    }

    /**
     * Wenn ausreichend Zeit bis <code>now</code> vergangen ist, führt diese Methoden einen
     * oder mehrere Bewegungsschritte aus. Am Ende befindet sich das {@link IMovingGO}
     * am Zielort eines Schritts - oder im Dazwischen vor dem Zielort des aktuellen Schritts.
     */
    @SuppressWarnings("ConstantConditions")
    private void narrateAndMove(final AvDateTime now) {
        checkState(hasCurrentStep(), "No current step");

        if (requirePcd().getPauseForSCAction() == DO_START_LEAVING) {
            narrateAndDoMovementLeaves();
        }

        while (now.isEqualOrAfter(getCurrentStep().getExpDoneTime())) {
            if (!narrateAndMoveOneStep()) {
                // Das IMovingGO soll am Zielort des Schritts stehenbleiben.
                return;
            }
        }
    }

    /**
     * Führt einen Bewegungsschritt aus. Das {@link IMovingGO} betritt die nächste Location
     * (laut Step). Danach kann zu zwei unterschiedlichen Ergebnissen kommen:
     * <ul>
     * <li>Das <code>IMovingGO</code> bleibt an dieser Location, z.B. weil es sein Ziel erreicht
     * hat oder der Spieler dort die Gelegenheit erhalten soll, mit dem <code>IMovingGO</code>
     * zu interagieren. Die Methode gibt <code>false</code> zurück.
     * <li>Das <code>IMovingGO</code> verlässt die Location wieder und ist damit im "Dazwischen".
     * Die Methode gibt <code>true</code> zurück.
     * </ul>
     */
    @SuppressWarnings("ConstantConditions")
    private boolean narrateAndMoveOneStep() {
        checkState(hasCurrentStep(), "No current step");

        locationComp.setLocation(getCurrentStepToId());

        if (world.loadSC().locationComp().hasRecursiveLocation(getCurrentStepToId())) {
            narrateAndDoEnters();
        }

        locationComp.narrateAndDoEnterReactions(
                getCurrentStepFromId(), getCurrentStepToId()
        );

        if (locationComp.hasLocation(getTargetLocationId())) {
            stopMovement();
            return false;
        }

        if (pauseIfSameOuterMostLocationWithSC()) {
            return false;
        }

        setupNextStepIfNecessaryAndPossible(getCurrentStep().getExpDoneTime());
        if (!hasCurrentStep()) {
            return false;
        }

        narrateAndDoMovementLeaves();
        return true;
    }

    /**
     * Pausiert die aktuelle Bewegung, falls sich er SC an derselben outermost location
     * befindet (gibt dann <code>true</code> zurück) - tut sonst nichts.
     * <p>
     * Die Idee ist: Das {@link IMovingGO} soll in der Regel nicht einfach am SC
     * vorbeilaufen. Der SC soll die Möglichkeit erhalten, zumindest einmalig mit dem
     * IMovingGO zu interagieren.
     */
    private boolean pauseIfSameOuterMostLocationWithSC() {
        if (locationComp.hasSameOuterMostLocationAs(SPIELER_CHARAKTER)
            // IDEA Wenn der SC schläft, dann hingegen das Game Object einfach
            //  vorbeilaufen lassen (in diesem Fall sollte es ja aber auch keine
            //  Narration geben...)
        ) {
            requirePcd().setPauseForSCAction(PAUSED);
            return true;
        }

        return false;
    }

    /**
     * Erzählt das Betreten des Zielorts des aktuellen Schritts und führt ggf.
     * direkt verbundene Aktionen aus, z.B. das Speichern, dass der SC nun das {@link IMovingGO}
     * kennt o.Ä.
     */
    @SuppressWarnings("ConstantConditions")
    private <FROM extends ILocationGO & ISpatiallyConnectedGO> void narrateAndDoEnters() {
        checkState(hasCurrentStep(), "No current step");

        final FROM from = getCurrentStepFrom();

        @Nullable final SpatialConnection spatialConnection =
                from.spatialConnectionComp().getConnection(getCurrentStepToId());

        final ILocationGO to = getCurrentStepTo();

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

    /**
     * Plant den nächsten Schritt ein - sofern noch nötig und überhaupt möglich.
     */
    private void setupNextStepIfNecessaryAndPossible(final AvDateTime startTime) {
        setupNextStepIfNecessaryAndPossible(startTime, false);
    }

    /**
     * Plant den nächsten Schritt ein - sofern noch nötig und überhaupt möglich.
     *
     * @param stepTakesNoTime Bei <code>false</code> wird der Schritt
     *                        ganz normal ausgeführt - bei <code>true</code> wird der
     *                        Schritt in 0 Sekunden ausgeführt. Der NPC wird sofort ankommen.
     */
    private void setupNextStepIfNecessaryAndPossible(
            final AvDateTime startTime, final boolean stepTakesNoTime) {
        if (!isMoving() || locationComp.hasLocation(getTargetLocationId())) {
            stopMovement();
            return;
        }

        requirePcd().setCurrentStep(calculateStep(startTime, stepTakesNoTime));

        requirePcd().setPauseForSCAction(DO_START_LEAVING);
    }

    /**
     * Berechnet den nächsten Schritt - sofern überhaupt möglich.
     *
     * @param stepTakesNoTime Bei <code>false</code> wird der Schritt
     *                        ganz normal ausgeführt - bei <code>true</code> wird der
     *                        Schritt in 0 Sekunden ausgeführt. Der NPC wird sofort ankommen.
     */
    @Nullable
    private MovementStep calculateStep(final AvDateTime startTime, final boolean stepTakesNoTime) {
        final ILocationGO from = locationComp.getLocation();

        if (!(from instanceof ISpatiallyConnectedGO)) {
            return null;
        }

        @Nullable final SpatialStandardStep firstStep =
                spatialConnectionSystem
                        .findFirstStep((ISpatiallyConnectedGO) from, getTargetLocation());

        return toMovementStep(from, firstStep, startTime, stepTakesNoTime);
    }

    /**
     * Erzeugt den nächsten Schritt aus diesem {@link SpatialStandardStep} -
     * null-safe.
     *
     * @param takesNoTime Bei <code>false</code> wird der Schritt
     *                    ganz normal erzeugt - bei <code>true</code> werden für den
     *                    Schritt 0 Sekunden eingeplant. Der NPC wird also sofort ankommen.
     */
    private MovementStep toMovementStep(
            final ILocationGO from,
            @Nullable final SpatialStandardStep spatialStandardStep,
            final AvDateTime startTime,
            final boolean takesNoTime) {
        if (spatialStandardStep == null) {
            return null;
        }

        return new MovementStep(
                from.getId(),
                spatialStandardStep.getTo(),
                startTime,
                calcExpectedDuration(spatialStandardStep.getStandardDuration(), takesNoTime));
    }

    /**
     * Berechnet die erwartete Dauer für einen Schritt auf Basis der Standard-Dauer und
     * der relativen Geschwindigkeit dieses NPCs.
     *
     * @param takesNoTime Bei <code>false</code> wird die Dauer
     *                    ganz normal berechnet - bei <code>true</code> werden
     *                    0 Sekunden eingeplant. Der NPC wird also sofort ankommen.
     */
    private AvTimeSpan calcExpectedDuration(final AvTimeSpan standardDuration,
                                            final boolean takesNoTime) {
        if (takesNoTime) {
            return NO_TIME;
        }

        return standardDuration.times(relativeVelocity);
    }

    private void narrateAndDoMovementLeaves() {
        requirePcd().setPauseForSCAction(UNPAUSED);

        if (world.loadSC().locationComp().hasRecursiveLocation(getCurrentStepFromId())) {
            narrateAndDoLeaves();
        }

        locationComp.narrateAndDoLeaveReactions(getCurrentStepToId());
        locationComp.unsetLocation();
        // Ab jetzt befindet sich das IMovingBeing im "Dazwischen" zwischen
        // from und to.
    }

    @SuppressWarnings("ConstantConditions")
    private <FROM extends ILocationGO & ISpatiallyConnectedGO> void narrateAndDoLeaves() {
        final FROM from = getCurrentStepFrom();

        movementNarrator.narrateAndDoLeaves(
                from,
                getCurrentStepTo(),
                from.spatialConnectionComp().getConnection(getCurrentStepToId()),
                from.spatialConnectionComp().getNumberOfWaysOut());

        requirePcd().setHatDenSCGeradeVerlassen(true);
    }

    public void narrateAndDoScTrifftMovingGOImDazwischen(
            @Nullable final ILocationGO scFrom,
            final ILocationGO scTo) {
        narrateScTrifftMovingGOImDazwischen(scFrom, scTo);

        world.loadSC().memoryComp().narrateAndUpgradeKnown(getGameObjectId());
    }


    public void narrateScFolgtMovingGO(final TimedDescription<?> normalDescription) {
        movementNarrator.narrateScFolgtMovingGO(normalDescription);
    }

    public void narrateAndDoScTrifftStehendesMovingGOInTo(final ILocationGO scTo) {
        narrateScTrifftStehendesMovingGOInTo(scTo);

        world.loadSC().memoryComp().narrateAndUpgradeKnown(getGameObjectId());
    }

    private void narrateScTrifftStehendesMovingGOInTo(final ILocationGO scTo) {
        movementNarrator.narrateScTrifftStehendesMovingGO(
                locationComp.getLocation() != null ?
                        locationComp.getLocation() :
                        scTo);
    }

    private void narrateScTrifftMovingGOImDazwischen(@Nullable final ILocationGO scFrom,
                                                     final ILocationGO scTo) {
        movementNarrator.narrateScTrifftMovingGOImDazwischen(
                scFrom,
                scTo,
                requireNonNull(getCurrentStepFrom()));
    }

    public boolean isMoving() {
        return requirePcd().getTargetLocationId() != null;
    }

    /**
     * Beendet die Bewegung.
     */
    private void stopMovement() {
        requirePcd().stopMovement();
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
        return requirePcd().getTargetLocationId();
    }

    private void setTargetLocationId(final GameObjectId targetLocationId) {
        requirePcd().setTargetLocationId(targetLocationId);
    }

    private boolean hasCurrentStep() {
        return getCurrentStep() != null;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <FROM extends ILocationGO & ISpatiallyConnectedGO> FROM getCurrentStepFrom() {
        @Nullable final GameObjectId currentStepFromId = getCurrentStepFromId();

        return currentStepFromId != null ? (FROM) world.load(currentStepFromId) : null;
    }

    @Nullable
    public ILocationGO getCurrentStepTo() {
        @Nullable final GameObjectId currentStepToId = getCurrentStepToId();

        return currentStepToId != null ? (ILocationGO) world.load(currentStepToId) : null;
    }

    @Nullable
    @VisibleForTesting
    public GameObjectId getCurrentStepFromId() {
        @Nullable final MovementStep currentStep = getCurrentStep();

        return currentStep != null ? currentStep.getFromId() : null;
    }

    @Nullable
    @VisibleForTesting
    public GameObjectId getCurrentStepToId() {
        @Nullable final MovementStep currentStep = getCurrentStep();

        return currentStep != null ? currentStep.getToId() : null;
    }

    @Nullable
    private MovementStep getCurrentStep() {
        return requirePcd().getCurrentStep();
    }
}
