package de.nb.aventiure2.data.world.syscomp.movement;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.DO_START_LEAVING;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.PAUSED;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.UNPAUSED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.Collection;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.ISCActionDoneListenerComponent;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialStandardStep;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.TimedDescription;

/**
 * Component for a {@link GameObject}: The game object
 * moves through the world autonomously.
 * <p>
 * Das Konzept ist in etwa so:
 * <ul>
 * <li>Game Objects (Wesen) mit einer MovementComp können eine <i>Target Location</i> haben -
 * ein Ziel, auf das sie sich zubewegen. Haben sie <i>keine</i> Target Location, stehen sie
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

    @Nullable
    private final FeelingsComp feelingsComp;

    private final LocationComp locationComp;

    /**
     * The initial movement target - if any
     */
    @Nullable
    private final GameObjectId initialTargetLocationId;

    private IConversationable conversationable = new NonConversationable();

    /**
     * Relative Geschwindigkeit. 1 = Standard-Geschwindigkeit (Geschwindigkeit des SC, der
     * sich aufkennt)
     */
    private final float relativeVelocity;

    public MovementComp(final GameObjectId gameObjectId,
                        final AvDatabase db,
                        final World world,
                        final SpatialConnectionSystem spatialConnectionSystem,
                        final IMovementNarrator movementNarrator,
                        @Nullable final FeelingsComp feelingsComp,
                        final LocationComp locationComp,
                        final float relativeVelocity,
                        @Nullable final GameObjectId initialTargetLocationId) {
        super(gameObjectId, db.movementDao());
        this.world = world;
        this.spatialConnectionSystem = spatialConnectionSystem;
        this.movementNarrator = movementNarrator;
        this.feelingsComp = feelingsComp;
        this.locationComp = locationComp;
        this.relativeVelocity = relativeVelocity;
        this.initialTargetLocationId = initialTargetLocationId;
    }

    public void setConversationable(final IConversationable conversationable) {
        this.conversationable = conversationable;
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

        if (!hasCurrentStep()
                || requirePcd().getPauseForSCAction() == PAUSED
                || conversationable.isInConversation()) {
            return;
        }

        narrateAndDoMovementLeaves();
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
        return
                // Vielleicht ist das hier auch gar nicht nötig. Dann das Movable läuft nicht
                // davon, so lange das Gespräch nicht beendet wurde.
                conversationable.isInConversation();
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
            pauseIfSameVisibleOuterMostLocationWithSC();
        }

        if (!hasCurrentStep()
                || requirePcd().getPauseForSCAction() == PAUSED
                || conversationable.isInConversation()) {
            return;
        }

        narrateAndMove(now);
    }

    /**
     * Wenn ausreichend Zeit bis <code>now</code> vergangen ist, führt diese Methoden einen
     * oder mehrere Bewegungsschritte aus. Am Ende befindet sich das {@link IMovingGO}
     * am Zielort eines Schritts - oder im Dazwischen vor dem Zielort des aktuellen Schritts.
     */
    private void narrateAndMove(final AvDateTime now) {
        checkState(hasCurrentStep(), "No current step");

        if (requirePcd().getPauseForSCAction() == DO_START_LEAVING) {
            narrateAndDoMovementLeaves();
        }

        while (now.isEqualOrAfter(requireCurrentStep().getExpDoneTime())) {
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
    private boolean narrateAndMoveOneStep() {
        checkState(hasCurrentStep(), "No current step");

        // (Dies hier müsste angepasst werden, wenn das Objekt ein
        // IAmountableGO wäre. Das wäre aber sehr seltsam.)
        locationComp.setLocation(requireCurrentStepToId());

        if (world.loadSC().locationComp().hasVisiblyRecursiveLocation(requireCurrentStepToId())) {
            narrateAndDoEnters();
        }

        locationComp
                .narrateAndDoEnterReactions(requireCurrentStepFromId(), requireCurrentStepToId());

        if (locationComp.hasLocation(getTargetLocationId())) {
            stopMovement();
            return false;
        }

        if (pauseIfSameVisibleOuterMostLocationWithSC()) {
            return false;
        }

        setupNextStepIfNecessaryAndPossible(requireCurrentStep().getExpDoneTime());
        if (!hasCurrentStep()
                || requirePcd().getPauseForSCAction() == PAUSED
                || conversationable.isInConversation()) {
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
    @CanIgnoreReturnValue
    private boolean pauseIfSameVisibleOuterMostLocationWithSC() {
        if (locationComp.hasSameVisibleOuterMostLocationAs(SPIELER_CHARAKTER)
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
    private <FROM extends ILocationGO & ISpatiallyConnectedGO> void narrateAndDoEnters() {
        checkState(hasCurrentStep(), "No current step");

        final FROM from = requireCurrentStepFrom();

        @Nullable final SpatialConnection spatialConnection =
                from.spatialConnectionComp().getConnection(requireCurrentStepToId());

        final ILocationGO to = requireCurrentStepTo();

        final NumberOfWays numberOfWaysIn =
                to instanceof ISpatiallyConnectedGO ?
                        ((ISpatiallyConnectedGO) to).spatialConnectionComp()
                                .getNumberOfWaysOut() :
                        NumberOfWays.NO_WAY;

        movementNarrator.narrateAndDoEnters(from, to, spatialConnection, numberOfWaysIn);
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
    @Nullable
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
                calcExpectedDuration(
                        from, world.load(spatialStandardStep.getTo()),
                        spatialStandardStep.getStandardDuration(), takesNoTime));
    }

    /**
     * Berechnet die erwartete Dauer für einen Schritt auf Basis der Standard-Dauer und
     * der relativen Geschwindigkeit dieses NPCs.
     *
     * @param takesNoTime Bei <code>false</code> wird die Dauer
     *                    ganz normal berechnet - bei <code>true</code> werden
     */
    private AvTimeSpan calcExpectedDuration(
            final ILocationGO from,
            final ILocationGO to, final AvTimeSpan standardDuration,
            final boolean takesNoTime) {
        if (takesNoTime) {
            return NO_TIME;
        }

        return standardDuration.times(calcSpeedFactor(from, to));
    }

    private double calcSpeedFactor(final ILocationGO from, final ILocationGO to) {
        final double feelingsFactor =
                feelingsComp != null ? feelingsComp.getMovementSpeedFactor() : 1.0;

        return relativeVelocity
                * feelingsFactor
                * world.loadWetter().wetterComp().getMovementSpeedFactor(from, to);
    }

    private void narrateAndDoMovementLeaves() {
        checkState(hasCurrentStep(), "No current step");

        requirePcd().setPauseForSCAction(UNPAUSED);

        if (world.loadSC().locationComp().hasVisiblyRecursiveLocation(requireCurrentStepFromId())) {
            narrateAndDoLeaves();
        }

        locationComp.narrateAndDoLeaveReactions(requireCurrentStepToId());
        locationComp.unsetLocation();
        // Ab jetzt befindet sich das IMovingBeing im "Dazwischen" zwischen
        // from und to.
    }

    private <FROM extends ILocationGO & ISpatiallyConnectedGO> void narrateAndDoLeaves() {
        checkState(hasCurrentStep(), "No current step");

        final FROM from = requireCurrentStepFrom();

        movementNarrator.narrateAndDoLeaves(
                from,
                requireCurrentStepTo(),
                from.spatialConnectionComp().getConnection(requireCurrentStepToId()),
                from.spatialConnectionComp().getNumberOfWaysOut());

        requirePcd().setHatDenSCGeradeVerlassen(true);
    }

    public void narrateAndDoScTrifftMovingGOImDazwischen(
            @Nullable final ILocationGO scFrom,
            final ILocationGO scTo) {
        checkState(hasCurrentStep(), "No current step");

        narrateScTrifftMovingGOImDazwischen(scFrom, scTo);

        world.narrateAndUpgradeScKnownAndAssumedState(getGameObjectId());
    }


    public void narrateScFolgtMovingGO(
            final Collection<TimedDescription<?>> normalTimedDescriptions) {
        movementNarrator.narrateScFolgtMovingGO(normalTimedDescriptions);
    }

    public void narrateAndDoScTrifftStehendesMovingGOInTo(final ILocationGO scTo) {
        narrateScTrifftStehendesMovingGOInTo(scTo);

        world.narrateAndUpgradeScKnownAndAssumedState(getGameObjectId());
    }

    private void narrateScTrifftStehendesMovingGOInTo(final ILocationGO scTo) {
        movementNarrator.narrateScTrifftStehendesMovingGO(
                locationComp.getLocation() != null ?
                        locationComp.getLocation() :
                        scTo);
    }

    private void narrateScTrifftMovingGOImDazwischen(@Nullable final ILocationGO scFrom,
                                                     final ILocationGO scTo) {
        checkState(hasCurrentStep(), "No current step");

        movementNarrator.narrateScTrifftMovingGOImDazwischen(
                scFrom,
                scTo,
                requireCurrentStepFrom());
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
        return world.load(getTargetLocationId());
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

    @Nonnull
    private <FROM extends ILocationGO & ISpatiallyConnectedGO> FROM requireCurrentStepFrom() {
        return requireNonNull(getCurrentStepFrom());
    }

    @Nullable
    public <FROM extends ILocationGO & ISpatiallyConnectedGO> FROM getCurrentStepFrom() {
        return world.load(getCurrentStepFromId());
    }

    @Nonnull
    private ILocationGO requireCurrentStepTo() {
        return requireNonNull(getCurrentStepTo());
    }

    @Nullable
    public ILocationGO getCurrentStepTo() {
        return world.load(getCurrentStepToId());
    }

    @Nonnull
    @VisibleForTesting
    public GameObjectId requireCurrentStepFromId() {
        return requireNonNull(getCurrentStepFromId());
    }

    @Nullable
    @VisibleForTesting
    private GameObjectId getCurrentStepFromId() {
        @Nullable final MovementStep currentStep = getCurrentStep();

        return currentStep != null ? currentStep.getFromId() : null;
    }

    @Nonnull
    @VisibleForTesting
    public GameObjectId requireCurrentStepToId() {
        return requireNonNull(getCurrentStepToId());
    }

    @Nullable
    @VisibleForTesting
    private GameObjectId getCurrentStepToId() {
        @Nullable final MovementStep currentStep = getCurrentStep();

        return currentStep != null ? currentStep.getToId() : null;
    }

    @Nonnull
    private MovementStep requireCurrentStep() {
        return requireNonNull(getCurrentStep());
    }

    @Nullable
    private MovementStep getCurrentStep() {
        return requirePcd().getCurrentStep();
    }
}
