package de.nb.aventiure2.data.world.syscomp.amount;

import static com.google.common.base.Preconditions.checkArgument;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Eine (veränderliche) Menge (mindestens 1) eines Game Objects.
 */
@Entity
public
class AmountPCD extends AbstractPersistentComponentData {
    private int amount;

    AmountPCD(@NonNull final GameObjectId gameObjectId, final int amount) {
        super(gameObjectId);
        this.amount = amount;
    }

    void setAmount(final int amount) {
        checkArgument(amount > 0);

        if (this.amount == amount) {
            return;
        }

        setChanged();
        this.amount = amount;
    }

    int getAmount() {
        return amount;
    }
}
