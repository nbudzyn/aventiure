package de.nb.aventiure2.german.satz;

import javax.annotation.Nonnull;

import de.nb.aventiure2.german.base.Konstituentenfolge;

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

    public Konditionalsatz perfekt() {
        return new Konditionalsatz(kondition, satz.perfekt());
    }

    /**
     * Gibt den eigentlichen Konditionalsatz zurück, allerdings <i>wird kein ausstehendes
     * Komma gefordert!</i> Das wird der Aufrufer in vielen Fällen selbst tun wollen.
     */
    public Konstituentenfolge getDescription() {
        return Konstituentenfolge.joinToKonstituentenfolge(
                kondition, // "weil"
                // FIXME Den Fall beachten, dass der Satz ein Anschlusswort haben könnte.
                //  Idee: "und" weglassen, "weil aber", ...
                satz.getVerbletztsatz() // "du etwas zu berichten hast"
        );
    }
}
