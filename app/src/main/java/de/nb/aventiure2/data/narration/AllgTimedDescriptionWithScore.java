package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;

import java.util.Objects;

import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.description.TimedDescription;

class AllgTimedDescriptionWithScore {
    final TimedDescription<TextDescription> allgTimedDescription;
    final float score;

    AllgTimedDescriptionWithScore(
            final TimedDescription<TextDescription> allgTimedDescription,
            final float score) {
        this.allgTimedDescription = allgTimedDescription;
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
        final AllgTimedDescriptionWithScore that = (AllgTimedDescriptionWithScore) o;
        return Float.compare(that.score, score) == 0 &&
                Objects.equals(allgTimedDescription, that.allgTimedDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score);
    }

    @NonNull
    @Override
    public String toString() {
        return "AllgTimedDescriptionWithScore{" +
                "allgTimedDescription=" + allgTimedDescription +
                ", score=" + score +
                '}';
    }
}
