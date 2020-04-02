package de.nb.aventiure2.data.world.room;

import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;

public enum RoomKnown {
    // Order is relevant, see #max()
    UNKNOWN, KNOWN_FROM_DARKNESS, KNOWN_FROM_LIGHT;

    public static RoomKnown getKnown(final Lichtverhaeltnisse lichtverhaeltnisse) {
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

    public static RoomKnown max(final RoomKnown one, final RoomKnown other) {
        if (one.ordinal() > other.ordinal()) {
            return one;
        }

        return other;
    }
}
