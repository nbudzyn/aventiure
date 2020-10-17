package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.room.Ignore;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;

import static com.google.common.base.Preconditions.checkState;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementStep.Phase.FIRST_LEAVING;
import static de.nb.aventiure2.data.world.syscomp.movement.MovementStep.Phase.SECOND_ENTERING;

@Immutable
class MovementStep {
    enum Phase {
        FIRST_LEAVING,
        SECOND_ENTERING
    }

    private final GameObjectId from;
    private final GameObjectId to;
    private final AvDateTime startTime;
    private final AvTimeSpan expDuration;
    private final Phase phase;

    @Ignore
    MovementStep(final GameObjectId from, final GameObjectId to,
                 final AvDateTime startTime,
                 final AvTimeSpan expDuration) {
        this(from, to, startTime, expDuration, FIRST_LEAVING);
    }

    MovementStep(final GameObjectId from, final GameObjectId to,
                 final AvDateTime startTime,
                 final AvTimeSpan expDuration,
                 final Phase phase) {
        this.from = from;
        this.to = to;
        this.startTime = startTime;
        this.expDuration = expDuration;
        this.phase = phase;
    }

    AvDateTime getExpDoneTime() {
        return startTime.plus(expDuration);
    }

    AvDateTime getExpLeaveDoneTime() {
        return startTime.plus(
                expDuration.dividedBy(2)
        );
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

    MovementStep withPhase(final Phase newPhase) {
        checkState(
                hasPhase(newPhase) ||
                        (hasPhase(FIRST_LEAVING) && newPhase == SECOND_ENTERING),
                "Illegal phase transition: From " + getPhase() + " to " + newPhase
        );

        return new MovementStep(from, to, startTime, expDuration, newPhase);
    }

    boolean hasPhase(final Phase phase) {
        return this.phase == phase;
    }

    Phase getPhase() {
        return phase;
    }
}
