package de.nb.aventiure2.data.world.syscomp.movement;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.GameObjectId;

@Immutable
class MovementStep {
    private final GameObjectId fromId;
    private final GameObjectId toId;
    private final AvDateTime startTime;
    private final AvTimeSpan expDuration;

    MovementStep(final GameObjectId fromId, final GameObjectId toId,
                 final AvDateTime startTime,
                 final AvTimeSpan expDuration) {
        this.fromId = fromId;
        this.toId = toId;
        this.startTime = startTime;
        this.expDuration = expDuration;
    }

    AvDateTime getExpDoneTime() {
        return startTime.plus(expDuration);
    }

    GameObjectId getFromId() {
        return fromId;
    }

    GameObjectId getToId() {
        return toId;
    }

    AvDateTime getStartTime() {
        return startTime;
    }

    AvTimeSpan getExpDuration() {
        return expDuration;
    }
}
