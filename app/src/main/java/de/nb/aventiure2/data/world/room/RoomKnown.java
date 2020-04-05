package de.nb.aventiure2.data.world.room;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;

public enum RoomKnown {
    // Order is relevant, see #max()
    UNKNOWN(false), KNOWN_FROM_DARKNESS(true), KNOWN_FROM_LIGHT(true);

    private final boolean known;

    RoomKnown(final boolean known) {
        this.known = known;
    }

    public static RoomKnown getKnown(@NonNull final Lichtverhaeltnisse lichtverhaeltnisse) {
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
    public static RoomKnown max(@NonNull final RoomKnown one, @NonNull final RoomKnown other) {
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
