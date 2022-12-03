package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Ein (syntaktisches) Vorfeld.
 */
public class Vorfeld implements IKonstituentenfolgable {
    static final Vorfeld EMPTY = new Vorfeld();

    @Nullable
    private final Konstituentenfolge konstituentenfolge;

    /**
     * Erzeugt ein leeres Vorfeld
     */
    private Vorfeld() {
        this((Konstituentenfolge) null);
    }

    public Vorfeld(@Nullable final String string) {
        this(Konstituentenfolge.joinToKonstituentenfolge(string));
    }

    public Vorfeld(@Nullable final Konstituente konstituente) {
        this(Konstituentenfolge.joinToKonstituentenfolge(konstituente));
    }

    public Vorfeld(@Nullable final Konstituentenfolge konstituentenfolge) {
        this.konstituentenfolge = konstituentenfolge;
    }

    @Override
    @Nullable
    public Konstituentenfolge toKonstituentenfolge() {
        return konstituentenfolge;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Vorfeld vorfeld = (Vorfeld) o;
        return Objects.equals(konstituentenfolge, vorfeld.konstituentenfolge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(konstituentenfolge);
    }

    @NonNull
    @Override
    public String toString() {
        if (konstituentenfolge == null) {
            return "";
        }

        return konstituentenfolge.joinToSingleKonstituente().toTextOhneKontext();
    }
}
