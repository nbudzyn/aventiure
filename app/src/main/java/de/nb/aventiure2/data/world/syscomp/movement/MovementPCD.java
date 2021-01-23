package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;

import static de.nb.aventiure2.data.world.syscomp.movement.MovementPCD.PauseForSCAction.UNPAUSED;

/**
 * Mutable - and therefore persistent - data of the {@link MovementPCD} component.
 */
@Entity
@SuppressWarnings("unused")
public class MovementPCD extends AbstractPersistentComponentData {
    /**
     * Ob eine Pause eingelegt wurde, damit der SC mit dem
     * {@link IMovingGO} interagieren kann
     */
    enum PauseForSCAction {
        /**
         * Zurzeit ist keine Pause eingelegt
         */
        UNPAUSED,
        /**
         * Es wurde eine Pause eingelegt
         */
        PAUSED,
        /**
         * Die Pause ist beendet, das {@link IMovingGO} soll als nächstes
         * beginnen, die aktuelle Location zu verlassen.
         */
        DO_START_LEAVING
    }

    @Nullable
    private GameObjectId targetLocationId;

    @Embedded
    @Nullable
    private MovementStep currentStep;

    private PauseForSCAction pauseForSCAction;

    /**
     * Ob das GO den SC gerade verlassen hat.
     */
    private boolean hatDenSCGeradeVerlassen;

    @Ignore
    MovementPCD(final GameObjectId gameObjectId,
                @Nullable final GameObjectId targetLocationId) {
        this(gameObjectId, targetLocationId, null, UNPAUSED, false);
    }

    @SuppressWarnings("WeakerAccess")
    public MovementPCD(final GameObjectId gameObjectId,
                       @Nullable final GameObjectId targetLocationId,
                       @Nullable final MovementStep currentStep,
                       final PauseForSCAction pauseForSCAction,
                       final boolean hatDenSCGeradeVerlassen) {
        super(gameObjectId);
        this.targetLocationId = targetLocationId;
        this.currentStep = currentStep;
        this.pauseForSCAction = pauseForSCAction;
        this.hatDenSCGeradeVerlassen = hatDenSCGeradeVerlassen;
    }

    /**
     * Beendet die aktuelle Bewegung.
     */
    void stopMovement() {
        setTargetLocationId(null);
        setCurrentStep(null);
        setPauseForSCAction(UNPAUSED);
        setHatDenSCGeradeVerlassen(false);
    }

    @Nullable
    GameObjectId getTargetLocationId() {
        return targetLocationId;
    }

    void setTargetLocationId(@Nullable final GameObjectId targetLocationId) {
        setChanged();
        this.targetLocationId = targetLocationId;
    }

    void setCurrentStep(@Nullable final MovementStep currentStep) {
        setChanged();
        this.currentStep = currentStep;
    }

    @Nullable
    MovementStep getCurrentStep() {
        return currentStep;
    }

    void setPauseForSCAction(final PauseForSCAction pauseForSCAction) {
        setChanged();
        this.pauseForSCAction = pauseForSCAction;
    }

    PauseForSCAction getPauseForSCAction() {
        return pauseForSCAction;
    }

    void setHatDenSCGeradeVerlassen(final boolean hatDenSCGeradeVerlassen) {
        if (this.hatDenSCGeradeVerlassen == hatDenSCGeradeVerlassen) {
            return;
        }

        setChanged();
        this.hatDenSCGeradeVerlassen = hatDenSCGeradeVerlassen;
    }

    boolean isHatDenSCGeradeVerlassen() {
        return hatDenSCGeradeVerlassen;
    }
}
