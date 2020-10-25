package de.nb.aventiure2.data.narration;

import java.util.Objects;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.description.AllgDescription;

class AllgDescriptionWithScoreAndElapsedTime {
    final AllgDescription allgDescription;
    final float score;
    final AvTimeSpan timeElapsed;

    AllgDescriptionWithScoreAndElapsedTime(final AllgDescription allgDescription,
                                           final float score,
                                           final AvTimeSpan timeElapsed) {
        this.allgDescription = allgDescription;
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
        final AllgDescriptionWithScoreAndElapsedTime that =
                (AllgDescriptionWithScoreAndElapsedTime) o;
        return Float.compare(that.score, score) == 0 &&
                allgDescription.equals(that.allgDescription) &&
                timeElapsed.equals(that.timeElapsed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(allgDescription, score, timeElapsed);
    }

    @Override
    public String toString() {
        return "AllgDescriptionWithScoreAndElapsedTime{" +
                "allgDescription=" + allgDescription +
                ", score=" + score +
                ", elapsedTime=" + timeElapsed +
                '}';
    }
}
