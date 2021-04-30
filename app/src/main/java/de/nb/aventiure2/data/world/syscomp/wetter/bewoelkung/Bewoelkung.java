package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Betweenable;

import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;

@SuppressWarnings("DuplicateBranchesInSwitch")
public enum Bewoelkung implements Betweenable<Bewoelkung> {
    // Reihenfolge ist relevant, nicht ändern!
    WOLKENLOS,
    LEICHT_BEWOELKT,
    BEWOELKT,
    BEDECKT;

    public boolean isUnauffaellig(final Tageszeit tageszeit) {
        if (tageszeit == NACHTS) {
            return compareTo(BEWOELKT) <= 0;
        }

        return compareTo(LEICHT_BEWOELKT) <= 0;
    }

    public boolean hasNachfolger(final Bewoelkung other) {
        return getNachfolger() == other;
    }

    public Bewoelkung getVorgaenger() {
        if (ordinal() == 0) {
            return values()[values().length - 1];
        }

        return values()[ordinal() - 1];
    }

    public Bewoelkung getNachfolger() {
        if (ordinal() == values().length - 1) {
            return values()[0];
        }

        return values()[ordinal() + 1];
    }

    public int minus(final Bewoelkung other) {
        return ordinal() - other.ordinal();
    }
}