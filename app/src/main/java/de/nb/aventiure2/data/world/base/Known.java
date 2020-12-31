package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public enum Known {
    // Order is relevant, see #max()
    UNKNOWN(false), KNOWN_FROM_DARKNESS(true), KNOWN_FROM_LIGHT(true);

    private final boolean known;

    Known(final boolean known) {
        this.known = known;
    }

    public static Known getKnown(final Lichtverhaeltnisse lichtverhaeltnisse) {
        switch (lichtverhaeltnisse) {
            case DUNKEL:
                return KNOWN_FROM_DARKNESS;
            case HELL:
                return KNOWN_FROM_LIGHT;
            default:
                throw new IllegalStateException(
                        "Unerwartete Lichtverhältnisse: " + lichtverhaeltnisse);
        }
    }

    @NonNull
    public static Known max(final Known one, final Known other) {
        if (one.ordinal() > other.ordinal()) {
            return one;
        }

        return other;
    }

    @Contract(pure = true)
    public boolean isKnown() {
        return known;
    }
}
