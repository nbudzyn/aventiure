package de.nb.aventiure2.data.world.base;

import de.nb.aventiure2.data.world.time.AvDateTime;

/**
 * Interface für eine Component im Sinne des Entity-Component-System-Patterns, die
 * informiert werden möchte, wenn der SC eine Aktion ausgeführt hat.
 */
public interface ISCActionDoneListenerComponent extends IComponent {
    /**
     * Aufgerufen, nachdem der SC eine Aktion ausgeführt hat
     */
    void onSCActionDone(AvDateTime startTimeOfUserAction);
}
