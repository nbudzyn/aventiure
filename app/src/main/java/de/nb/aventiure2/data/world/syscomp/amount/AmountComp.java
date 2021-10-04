package de.nb.aventiure2.data.world.syscomp.amount;

import static com.google.common.base.Preconditions.checkArgument;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Komponente für eine (veränderliche) Menge (mindestens 1).
 */
public class AmountComp extends AbstractStatefulComponent<AmountPCD> {
    private final int initialAmount;

    public AmountComp(final GameObjectId gameObjectId,
                      final AvDatabase db,
                      final int initialAmount) {
        super(gameObjectId, db.amountDao());

        checkArgument(initialAmount > 0);

        this.initialAmount = initialAmount;
    }

    @Override
    @NonNull
    protected AmountPCD createInitialState() {
        return new AmountPCD(getGameObjectId(), initialAmount);
    }

    public void setAmount(final int amount) {
        requirePcd().setAmount(amount);
    }

    public int getAmount() {
        return requirePcd().getAmount();
    }
}
