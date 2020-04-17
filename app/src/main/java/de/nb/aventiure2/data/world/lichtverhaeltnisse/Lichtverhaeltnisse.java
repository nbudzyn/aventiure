package de.nb.aventiure2.data.world.lichtverhaeltnisse;

import org.jetbrains.annotations.Contract;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.Tageszeit;

import static de.nb.aventiure2.data.world.room.Rooms.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.room.Rooms.SCHLOSS_VORHALLE_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.time.Tageszeit.NACHTS;

public enum Lichtverhaeltnisse {
    HELL("ins Helle"), DUNKEL("in die Dunkelheit");

    /**
     * Beschreibt, wohin der SC sich bewegt, wenn er sich in diese Lichtverhältnisse
     * bewegt, also etwas wie "in die Dunkelheit".
     */
    private final String wohin;

    private Lichtverhaeltnisse(final String wohin) {
        this.wohin = wohin;
    }

    @Contract(pure = true)
    public static Lichtverhaeltnisse getLichtverhaeltnisse(final Tageszeit tageszeit,
                                                           final GameObjectId room) {
        if (room.equals(SCHLOSS_VORHALLE) || room.equals(SCHLOSS_VORHALLE_TISCH_BEIM_FEST)) {
            // im Schloss ist es immer gut beleuchtet
            return HELL;
        }

        // STORY Der SC könnte eine Fackel dabei haben

        return getLichtverhaeltnisseDraussen(tageszeit);
    }

    /**
     * Gibt eine Beschreibung zurück, wohin der SC sich bewegt, wenn er sich in diese
     * Lichtverhältnisse bewegt, also etwas wie "in die Dunkelheit".
     */
    public String getWohin() {
        return wohin;
    }

    @Contract(pure = true)
    private static Lichtverhaeltnisse getLichtverhaeltnisseDraussen(final Tageszeit tageszeit) {
        if (tageszeit == NACHTS) {
            return DUNKEL;
        }

        return HELL;
    }
}
