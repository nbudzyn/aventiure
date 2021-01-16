package de.nb.aventiure2.data.narration;

import androidx.room.Entity;
import androidx.room.Ignore;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.Objects;

import de.nb.aventiure2.german.description.TextDescription;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * Angabe, dass zu einem <i>Satz von Alternativen</i>
 * eine Alternative bereits "verbraucht" ist. Eine Alternative
 * wird "verbraucht", wenn sie gewählt wird. (Die Alternative sollte also eher
 * nicht erneut gewählt werden, um Wiederholungen zu vermeiden.)
 */
@Entity(primaryKeys = {"alternativesStringHash", "consumedAlternativeStringHash"})
public class ConsumedNarrationAlternativeInfo {
    /**
     * Hash-Code des Satzes von Alternativen (nur die Strings werden berücksichtigt)
     */
    private int alternativesStringHash;

    /**
     * Hash-Code der verbrauchten Alternative (nur der String wird berücksichtigt)
     */
    private int consumedAlternativeStringHash;

    @Ignore
    ConsumedNarrationAlternativeInfo(final ImmutableList<? extends TextDescription> alternatives,
                                     final TextDescription consumedAlternative) {
        this(calcAlternativesStringHash(alternatives), consumedAlternative.getText().hashCode());
    }

    @SuppressWarnings("WeakerAccess")
    ConsumedNarrationAlternativeInfo(final int alternativesStringHash,
                                     final int consumedAlternativeStringHash) {
        this.alternativesStringHash = alternativesStringHash;
        this.consumedAlternativeStringHash = consumedAlternativeStringHash;
    }

    int getAlternativesStringHash() {
        return alternativesStringHash;
    }

    void setAlternativesStringHash(final int alternativesStringHash) {
        this.alternativesStringHash = alternativesStringHash;
    }

    int getConsumedAlternativeStringHash() {
        return consumedAlternativeStringHash;
    }

    void setConsumedAlternativeStringHash(final int consumedAlternativeStringHash) {
        this.consumedAlternativeStringHash = consumedAlternativeStringHash;
    }

    static int calcAlternativesStringHash(
            final Collection<? extends TextDescription> alternatives) {
        return alternatives.stream()
                .map(TextDescription::getText)
                .collect(toImmutableList()).hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ConsumedNarrationAlternativeInfo that = (ConsumedNarrationAlternativeInfo) o;
        return alternativesStringHash == that.alternativesStringHash &&
                consumedAlternativeStringHash == that.consumedAlternativeStringHash;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alternativesStringHash, consumedAlternativeStringHash);
    }
}
