package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;

public enum Known {
    // Order is relevant, see #max()
    UNKNOWN(false), KNOWN_FROM_DARKNESS(true), KNOWN_FROM_LIGHT(true);

    private final boolean known;

    Known(final boolean known) {
        this.known = known;
    }

    // TODO Die Lichtverhältnisse - gehören die nicht in eine eigene
    //  Component, so in der Art IstLichtverhaeltnissenAusgesetzt?
    public static Known getKnown(@NonNull final Lichtverhaeltnisse lichtverhaeltnisse) {
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
    public static Known max(@NonNull final Known one, @NonNull final Known other) {
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
