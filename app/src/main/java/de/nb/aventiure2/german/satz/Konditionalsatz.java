package de.nb.aventiure2.german.satz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

    @Nonnull
    Konditionalsatz stelleVoran(@Nullable final Konditionalsatz other) {
        if (other == null) {
            return this;
        }

        // FIXME Hier kann es zu dem Fall kommen, dass die
        //  Kondition unterschiedliche ist, z.B. wenn man
        //  "als ich dich gesehen habe" mit "weil ich dich schon länger
        //  ansprechen wollte" reiht:
        //  "als ich dich gesehen habe und weil ich dich schon länger
        //  ansprechen wollte".
        //  Man bräuchte dazu eine Konditionalsatzreihe und könnte
        //  einige Dinge so lösen wie bei der Satzreihe.
        // if (kondition.equals(other.kondition)) {

        // FIXME Hier könnte man Satzreihungen vermeiden,
        //  wenn das Subjekt beider Sätze gleich ist
        //  ("als und um die Ecke kommst und du den Troll siehst") - und stattdessen
        //  besser die "Prädikate reihen" ("als du um die Ecke kommst und den
        //  Troll siehst").

        return new Konditionalsatz(other.kondition,
                Satzreihe.gereihtStandard(other.satz, satz));
        // }
    }
}
