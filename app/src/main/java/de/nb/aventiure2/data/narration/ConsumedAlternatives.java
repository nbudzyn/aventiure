package de.nb.aventiure2.data.narration;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.description.TextDescription;

/**
 * Welche Alternativen "verbraucht" sind. Diese Alternativen sollen
 * eher nicht verwendet werden, um Wiederholungen zu vermieden.
 */
@Immutable
class ConsumedAlternatives {
    /**
     * Hash-Codes der "verbrauchten" Alternativen.
     */
    private final ImmutableSet<Integer> consumedHashes;

    ConsumedAlternatives(final Collection<Integer> consumedHashes) {
        this.consumedHashes = ImmutableSet.copyOf(consumedHashes);
    }

    /**
     * Gibt zurück, ob diese Alternative "verbraucht" ist.
     * Diese Alternative soll eher nicht verwendet werden, um
     * Wiederholungen zu vermeiden.
     */
    boolean isConsumed(final TextDescription alternative) {
        return isConsumed(alternative.getText());
    }

    /**
     * Gibt zurück, ob diese Alternative "verbraucht" ist.
     * Diese Alternative soll eher nicht verwendet werden, um
     * Wiederholungen zu vermeiden.
     */
    private boolean isConsumed(final String alternative) {
        return isConsumed(alternative.hashCode());
    }

    /**
     * Gibt zurück, ob die Alternative mit diesem Hash-Code "verbraucht" ist.
     * Diese Alternative soll eher nicht verwendet werden, um
     * Wiederholungen zu vermeiden.
     */
    private boolean isConsumed(final int alternativeHash) {
        return consumedHashes.contains(alternativeHash);
    }
}
