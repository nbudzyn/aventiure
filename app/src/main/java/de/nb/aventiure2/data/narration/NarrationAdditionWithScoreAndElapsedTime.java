package de.nb.aventiure2.data.narration;

import java.util.Objects;

import de.nb.aventiure2.data.world.time.*;

class NarrationAdditionWithScoreAndElapsedTime {
    final NarrationAddition narrationAddition;
    final float score;
    final AvTimeSpan timeElapsed;

    NarrationAdditionWithScoreAndElapsedTime(final NarrationAddition narrationAddition,
                                             final float score,
                                             final AvTimeSpan timeElapsed) {
        this.narrationAddition = narrationAddition;
        this.score = score;
        this.timeElapsed = timeElapsed;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NarrationAdditionWithScoreAndElapsedTime that =
                (NarrationAdditionWithScoreAndElapsedTime) o;
        return Float.compare(that.score, score) == 0 &&
                narrationAddition.equals(that.narrationAddition) &&
                timeElapsed.equals(that.timeElapsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(narrationAddition, score, timeElapsed);
    }

    @Override
    public String toString() {
        return "NarrationAdditionWithScoreAndElapsedTime{" +
                "narrationAddition=" + narrationAddition +
                ", score=" + score +
                ", elapsedTime=" + timeElapsed +
                '}';
    }
}
