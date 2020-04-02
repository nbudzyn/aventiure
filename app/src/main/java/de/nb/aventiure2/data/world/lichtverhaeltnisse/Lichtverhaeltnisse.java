package de.nb.aventiure2.data.world.lichtverhaeltnisse;

import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.Tageszeit;

import static de.nb.aventiure2.data.world.room.AvRoom.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.time.Tageszeit.NACHTS;

public enum Lichtverhaeltnisse {
    HELL, DUNKEL;

    public static Lichtverhaeltnisse getLichtverhaeltnisse(final Tageszeit tageszeit,
                                                           final AvRoom room) {
        if (room == SCHLOSS_VORHALLE) {
            // im Schloss ist es immer gut beleuchtet
            return HELL;
        }

        // STORY Der SC könnte eine Fackel dabei haben

        return getLichtverhaeltnisseDraussen(tageszeit);
    }

    private static Lichtverhaeltnisse getLichtverhaeltnisseDraussen(final Tageszeit tageszeit) {
        if (tageszeit == NACHTS) {
            return DUNKEL;
        }

        return HELL;
    }
}
