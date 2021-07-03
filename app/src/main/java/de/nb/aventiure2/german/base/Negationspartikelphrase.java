package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.util.Objects;

import static de.nb.aventiure2.german.base.GermanUtil.joinToString;
import static de.nb.aventiure2.german.base.Konstituente.k;

/**
 * Eine Phrase, deren Kern die Negationspartikel "nicht" ist. Beispiele:
 * "nicht", "gar nicht", "immer noch nicht", "gar nicht mehr" etc.
 */
public class Negationspartikelphrase implements IAlternativeKonstituentenfolgable {
    public static final Negationspartikelphrase NICHT = new Negationspartikelphrase();

    /**
     * Die dem "nicht" vorangestellten Wörter (in der Regel Adverbien oder Abtönungspartikeln)
     * wie "absolut", "gar, "absolut gar", "erst recht",  "noch", "immer noch", "lange",
     * "noch gar", "noch lange", "überhaupt", "noch überhaupt", "schon gar", "schon lange" etc.
     */
    @Nullable
    private final String vorangestellteWoerter;

    /**
     * Die dem "nicht" nachgestellten Wörter wie "mehr" oder "länger".
     */
    @Nullable
    private final String nachgestellteWoerter;

    private Negationspartikelphrase() {
        this(null, null);
    }

    /**
     * Erzeugt eine Negationspartikelphrase ("nicht", "überhaupt nicht", "nicht mehr",
     * "überhaupt nicht mehr"...) - optional mit vorangestellten Wörtern
     * ("überhaupt") oder auch nachgestellten Wörtern ("mehr").
     */
    public Negationspartikelphrase(@Nullable final String vorangestellteWoerter,
                                   @Nullable final String nachgestellteWoerter) {
        this.vorangestellteWoerter = Strings.emptyToNull(vorangestellteWoerter);
        this.nachgestellteWoerter = Strings.emptyToNull(nachgestellteWoerter);
    }

    @Override
    public ImmutableList<Konstituentenfolge> toAltKonstituentenfolgen() {
        return ImmutableList.of(new Konstituentenfolge(k(getDescription())));
    }

    public String getDescription() {
        return joinToString(vorangestellteWoerter, "nicht", nachgestellteWoerter);
    }

    public static boolean isMehrteilig(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        return negationspartikelphrase != null
                && negationspartikelphrase.isMehrteilig();
    }

    public boolean isMehrteilig() {
        return vorangestellteWoerter != null || nachgestellteWoerter != null;

    }

    public static boolean impliziertZustandsaenderung(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        if (negationspartikelphrase == null) {
            return false;
        }

        return negationspartikelphrase.impliziertZustandsaenderung();
    }

    public boolean impliziertZustandsaenderung() {
        // Eine Zustandsänderung wird anscheinend immer (und nur) durch nachgestellte
        // Wörter impliziert: "nicht mehr", "nicht länger" vs. "nicht", "noch nicht" etc.
        return nachgestellteWoerter != null;
    }

    @Nullable
    public String getVorangestellteWoerter() {
        return vorangestellteWoerter;
    }

    @Nullable
    public String getNachgestellteWoerter() {
        return nachgestellteWoerter;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Negationspartikelphrase that = (Negationspartikelphrase) o;
        return Objects.equals(vorangestellteWoerter, that.vorangestellteWoerter) &&
                Objects.equals(nachgestellteWoerter, that.nachgestellteWoerter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vorangestellteWoerter, nachgestellteWoerter);
    }
}
