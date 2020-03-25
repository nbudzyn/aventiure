package de.nb.aventiure2.activity.main.viewmodel;

/**
 * An {@link de.nb.aventiure2.playeraction.AbstractPlayerAction} as it is displayed to the
 * player.
 */
public interface GuiAction {
    String getDisplayName();

    void execute();

    String getActionType();
}
