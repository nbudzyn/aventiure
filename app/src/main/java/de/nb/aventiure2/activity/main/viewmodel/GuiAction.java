package de.nb.aventiure2.activity.main.viewmodel;

import de.nb.aventiure2.scaction.AbstractScAction;

/**
 * An {@link AbstractScAction} as it is displayed to the
 * player.
 */
public interface GuiAction {
    String getDisplayName();

    void execute();

    String getActionType();
}
