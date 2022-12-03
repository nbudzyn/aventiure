package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Ein (syntaktisches) Nachfeld.
 */
public class Nachfeld implements IKonstituentenfolgable {
    static final Nachfeld EMPTY = new Nachfeld();

    @Nullable
    private final Konstituentenfolge konstituentenfolge;

    /**
     * Erzeugt ein leeres Nachfeld
     */
    private Nachfeld() {
        this((Konstituentenfolge) null);
    }

    public Nachfeld(@Nullable final Konstituente konstituente) {
        this(Konstituentenfolge.joinToKonstituentenfolge(konstituente));
    }

    public Nachfeld(@Nullable final Konstituentenfolge konstituentenfolge) {
        this.konstituentenfolge = konstituentenfolge;
    }

    public boolean contains(@Nullable final Vorfeld vorfeld) {
        if (vorfeld == null) {
            return true;
        }

        return contains(vorfeld.toKonstituentenfolge());
    }

    public boolean contains(@Nullable final Konstituentenfolge part) {
        if (part == null) {
            return true;
        }

        @Nullable final Konstituentenfolge konstituentenfolge = toKonstituentenfolge();

        if (konstituentenfolge == null) {
            return false;
        }

        return konstituentenfolge.contains(part);
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
        final Nachfeld nachfeld = (Nachfeld) o;
        return Objects.equals(konstituentenfolge, nachfeld.konstituentenfolge);
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
