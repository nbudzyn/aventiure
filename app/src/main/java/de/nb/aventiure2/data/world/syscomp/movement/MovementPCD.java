package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Mutable - and therefore persistent - data of the {@link MovementPCD} component.
 */
@Entity
public class MovementPCD extends AbstractPersistentComponentData {
    @Nullable
    private GameObjectId targetLocationId;

    @Embedded
    @Nullable
    private MovementStep currentStep;

    @Ignore
    MovementPCD(@NonNull final GameObjectId gameObjectId,
                @Nullable final GameObjectId targetLocationId) {
        this(gameObjectId, targetLocationId, null);
    }

    MovementPCD(@NonNull final GameObjectId gameObjectId,
                @Nullable final GameObjectId targetLocationId,
                @Nullable final MovementStep currentStep) {
        super(gameObjectId);
        this.targetLocationId = targetLocationId;
        this.currentStep = currentStep;
    }

    @Nullable
    GameObjectId getTargetLocationId() {
        return targetLocationId;
    }

    void setTargetLocationId(@Nullable final GameObjectId targetLocationId) {
        this.targetLocationId = targetLocationId;
    }

    void setCurrentStep(@Nullable final MovementStep currentStep) {
        this.currentStep = currentStep;
    }

    @Nullable
    MovementStep getCurrentStep() {
        return currentStep;
    }
}
