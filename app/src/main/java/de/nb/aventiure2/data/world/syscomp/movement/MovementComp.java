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
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.NumberOfWays;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialStandardStep;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.system.SpatialConnectionSystem;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.DO_START_LEAVING;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.PAUSED;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.UNPAUSED;
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

    public AvTimeSpan startMovement(final AvDateTime now,
                                    final GameObjectId targetLocationId) {
        checkNotNull(targetLocationId, "targetLocationId is null");

        if (getGameObjectId().equals(targetLocationId)) {
            throw new IllegalStateException("A game object cannot move inside itself.");
        }

        getPcd().setTargetLocationId(targetLocationId);
        setupNextStepIfNecessaryAndPossible(now);

        return hasCurrentStep() ?
                narrateAndDoMovementIfExperiencedBySCStartsLeaving() :
                noTime();
    }

    public void stopMovement() {
        getPcd().setTargetLocationId(null);
        getPcd().setCurrentStep(null);
        getPcd().setPauseForSCAction(UNPAUSED);
    }

    /**
     * Supposed to be called regularly.
     */
    public AvTimeSpan onTimePassed(final AvDateTime now) {
        if (!isMoving()) {
            return noTime();
        }

        // Wurde das Game Object zwischenzeitlich versetzt?
        if (isLeaving() &&
                !locationComp.hasLocation(getCurrentStep().getFrom()) ||
                isEntering() &&
                        !locationComp.hasLocation(getCurrentStep().getTo())) {
            setupNextStepIfNecessaryAndPossible(now);
        }

        if (!hasCurrentStep()) {
            return noTime();
        }

        if (getPcd().getPauseForSCAction() == PAUSED) {
            return noTime();
        }

        AvTimeSpan extraTime = noTime();

        if (getPcd().getPauseForSCAction() == DO_START_LEAVING) {
            extraTime =
                    extraTime.plus(narrateAndDoMovementIfExperiencedBySCStartsLeaving());
        }

        // Je nachdem, wie lang der World-Tick war, sollen das IMovingGO ggf. auch mehrere
        // Halbschritte gehen
        while (true) {
            if (isLeaving()) {
                // TODO Wenn das IMovingGO schon leaving ist und der Benutzer möchte
                //  gewisse Interaktionen durchführen (z.B. ihm etwas geben o.Ä.) -
                //  sollte dann automatisch die Zeit aufgerechnet werden, bis
                //  das GO wieder zum SC zurückgegangen ist?
                //  Und danach? Sollte das GO dann wieder die volle Zeit für das
                //  Leaving brauchen?

                if (now.isEqualOrAfter(getCurrentStep().getExpLeaveDoneTime())) {
                    extraTime =
                            extraTime.plus(leaveAndEnterAndNarrateIfSCPresent());
                } else {
                    break;
                }
            }

            if (isEntering()) {
                // STORY Wie damit umgehen, wenn dieser Zeitpunkt noch NICHT erreicht ist?
                //  - Kann SC mit X uneingeschränkt interagieren (z.B. mit X reden)?
                //  -- Sollte dann bei einer solchen Aktion die Zeit, die das IMovingGO noch
                //     benötigt, bis es das "Zentrum" der location erreicht hat, auf die Zeit
                //     der eigentlichen Aktion aufgeschlagen werden? (Der SC wartet
                //     also implizit, bis X herangekommen ist?)
                //  -- Oder später den Zustand prüfen (z.B. wenn der Spieler
                //   vor der Zauberin fliehen möchte)?
                //  --- Sollte es dann einen Zusatztext in der Art "Du wartest, bis ...
                //      herangekommen ist und..." geben?
                //  - Oder sind nur manche Aktionen möglich? (nehmen, geben, ... gesperrt?)
                //    Dann bräuchte es aber wohl eine Aktion "Auf die Zauberin warten" o.Ä.

                //  STORY Zumindest manche Aktionen sollten wohl dazu führen,
                //   dass die Bewegung beendet oder zumindest für eine Weile die Bewegung
                //   "ausgesetzt" wird. Z.B. sollte er der Dialog beendet werden, bis
                //   das IMovingGO wieder weitergeht (sofern X auf den Dialog eingeht und ihn
                //   nicht von sich aus beendet)

                if (now.isEqualOrAfter(getCurrentStep().getExpDoneTime())) {
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

                    extraTime =
                            extraTime.plus(narrateAndDoMovementIfExperiencedBySCStartsLeaving());
                } else {
                    break;
                }
            }
        }

        return extraTime;
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
    AvTimeSpan narrateAndDoMovementIfExperiencedBySCStartsLeaving() {
        getPcd().setPauseForSCAction(UNPAUSED);

        if (!world.loadSC().locationComp().hasRecursiveLocation(locationComp.getLocation())) {
            return noTime();
        }

        final FROM from = (FROM) locationComp.getLocation();

        @Nullable final SpatialConnection spatialConnection =
                from.spatialConnectionComp().getConnection(getCurrentStep().getTo());

        return movementNarrator.narrateAndDoStartsLeaving(
                from,
                (ILocationGO) world.load(getCurrentStep().getTo()),
                spatialConnection,
                from.spatialConnectionComp().getNumberOfWaysOut());
    }

    @NonNull
    private <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan leaveAndEnterAndNarrateIfSCPresent() {
        return locationComp.narrateAndSetLocation(
                getCurrentStep().getTo(),
                () -> {
                    getCurrentStep().setPhase(SECOND_ENTERING);

                    if (!world.loadSC().locationComp().hasRecursiveLocation(
                            getCurrentStep().getTo())) {
                        return noTime();
                    }

                    final FROM from = (FROM) world.load(getCurrentStep().getFrom());

                    @Nullable final SpatialConnection spatialConnection =
                            from.spatialConnectionComp().getConnection(getCurrentStep().getTo());

                    final ILocationGO to = (ILocationGO) world.load(getCurrentStep().getTo());

                    final NumberOfWays numberOfWaysIn =
                            to instanceof ISpatiallyConnectedGO ?
                                    ((ISpatiallyConnectedGO) to).spatialConnectionComp()
                                            .getNumberOfWaysOut() :
                                    NumberOfWays.NO_WAY;

                    return movementNarrator.narrateAndDoStartsEntering(
                            from,
                            to,
                            spatialConnection,
                            numberOfWaysIn);
                });
    }

    public AvTimeSpan narrateAndDoSCTrifftEvtlMovingGOInFrom(final ILocationGO scFrom,
                                                             @Nullable final ILocationGO scTo) {
        // Das hier sind sehr spezielle Spezialfälle, wo SC und die Zauberin treffen
        // noch in scFrom zusammentreffen:
        if (isLeaving() &&
                scTo != null &&
                getCurrentStep().getTo().equals(scTo.getId())) {
            // Zauberin verlässt gerade auch scFrom und will auch nach scTo

            final AvTimeSpan extraTime = movementNarrator.narrateScUeberholtMovingGO();

            world.upgradeKnownToSC(getGameObjectId());

            return extraTime;
        }
        if (isEntering() &&
                scTo != null &&
                locationComp.getLastLocationId() != null &&
                world.isOrHasRecursiveLocation(locationComp.getLastLocationId(), scTo)) {
            // Zauberin kommt von scTo, hat aber schon scFrom betreten
            final AvTimeSpan extraTime =
                    movementNarrator.narrateScGehtMovingGOEntgegenUndLaesstEsHinterSich();

            world.upgradeKnownToSC(getGameObjectId());

            return extraTime;
        }

        return noTime();
    }

    public AvTimeSpan narrateAndDoSCTrifftMovingGOInTo(
            @Nullable final ILocationGO scFrom,
            final ILocationGO scTo) {
        final AvTimeSpan extraTime = narrateScTrifftMovingGOInTo(scFrom, scTo);

        world.upgradeKnownToSC(getGameObjectId());

        return extraTime;
    }

    public <FROM extends ILocationGO & ISpatiallyConnectedGO>
    AvTimeSpan narrateScTrifftMovingGOInTo(@Nullable final ILocationGO scFrom,
                                           final ILocationGO scTo) {
        if (!isMoving()) {
            return movementNarrator.narrateScTrifftStehendesMovingGO(
                    locationComp.getLocation() != null ?
                            locationComp.getLocation() :
                            scTo);
        }

        if (isEntering()) {
            return movementNarrator.narrateScTrifftEnteringMovingGO(
                    scFrom,
                    scTo,
                    (FROM) locationComp.getLastLocation());
        }

        // MovingGO ist leaving
        return movementNarrator.narrateScTrifftLeavingMovingGO(
                scFrom,
                (ILocationGO & ISpatiallyConnectedGO) scTo,
                (ILocationGO) world.load(getCurrentStep().getTo()));
    }

    public boolean isLeaving() {
        return isMoving() && currentStepHasPhase(FIRST_LEAVING);
    }

    public boolean isEntering() {
        return isMoving() && currentStepHasPhase(SECOND_ENTERING);
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

        assert getCurrentStep() != null;
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
