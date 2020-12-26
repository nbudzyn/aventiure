package de.nb.aventiure2.german.satz;

import javax.annotation.Nonnull;

import de.nb.aventiure2.german.base.Wortfolge;

import static de.nb.aventiure2.german.base.GermanUtil.joinToNull;

/**
 * Ein Nebensatz, der mit einer Kondition beginnt. Beispiel:
 * "als du das hörst"
 */
public class Konditionalsatz {
    @Nonnull
    private final String kondition;

    @Nonnull
    private final Satz satz;

    public Konditionalsatz(@Nonnull final String kondition, @Nonnull final Satz satz) {
        this.kondition = kondition;
        this.satz = satz;
    }

    /**
     * Gibt den eigentlichen Konditionalsatz zurück, allerdings <i>wird kein ausstehendes
     * Komma gefordert!</i> Das wird der Aufrufer in vielen Fällen selbst tun wollen.
     */
    public Wortfolge getDescription() {
        return joinToNull(
                kondition, // "weil"
                satz.getVerbletztsatz() // "du etwas zu berichten hast"
        );
    }
}
