package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.annotation.NonNull;

import java.util.Objects;

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

    @NonNull
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final MovementStep that = (MovementStep) o;
        return fromId.equals(that.fromId) &&
                toId.equals(that.toId) &&
                startTime.equals(that.startTime) &&
                expDuration.equals(that.expDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromId, toId, startTime, expDuration);
    }

    @Override
    public String toString() {
        return "MovementStep{" +
                "fromId=" + fromId +
                ", toId=" + toId +
                ", startTime=" + startTime +
                ", expDuration=" + expDuration +
                '}';
    }
}
