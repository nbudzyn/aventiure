package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;

import java.util.Objects;

import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.description.TimedDescription;

class TimedTextDescriptionWithScore {
    final TimedDescription<TextDescription> timedTextDescription;
    private final float score;

    TimedTextDescriptionWithScore(
            final TimedDescription<TextDescription> timedTextDescription,
            final float score) {
        this.timedTextDescription = timedTextDescription;
        this.score = score;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TimedTextDescriptionWithScore that = (TimedTextDescriptionWithScore) o;
        return Float.compare(that.score, score) == 0 &&
                Objects.equals(timedTextDescription, that.timedTextDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score);
    }

    @NonNull
    @Override
    public String toString() {
        return "TimedTextDescriptionWithScore{" +
                "allgTimedDescription=" + timedTextDescription
                +
                ", score=" + score +
                '}';
    }
}
