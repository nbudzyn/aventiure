package de.nb.aventiure2.data.world.syscomp.movement;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;

@Immutable
class MovementStep {
    private final GameObjectId from;
    private final GameObjectId to;
    private final AvDateTime startTime;
    private final AvTimeSpan expDuration;

    MovementStep(final GameObjectId from, final GameObjectId to,
                 final AvDateTime startTime,
                 final AvTimeSpan expDuration) {
        this.from = from;
        this.to = to;
        this.startTime = startTime;
        this.expDuration = expDuration;
    }

    AvDateTime getExpDoneTime() {
        return startTime.plus(expDuration);
    }

    public GameObjectId getFrom() {
        return from;
    }

    public GameObjectId getTo() {
        return to;
    }

    AvDateTime getStartTime() {
        return startTime;
    }

    AvTimeSpan getExpDuration() {
        return expDuration;
    }
}
