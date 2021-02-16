package de.nb.aventiure2.activity.main.viewmodel;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.scaction.AbstractScAction;

/**
 * An {@link AbstractScAction} as it is displayed to the
 * player.
 */
public interface GuiAction {
    String getDisplayName();

    void execute();

    @Nullable
    CardinalDirection getCardinalDirection();

    String getActionType();
}
