package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Ein (syntaktisches) Nachfeld.
 */
public class Nachfeld implements IAlternativeKonstituentenfolgable {
    static final Nachfeld EMPTY = new Nachfeld();

    @Nullable
    private final Konstituentenfolge konstituentenfolge;

    /**
     * Erzeugt ein leeres Nachfeld
     */
    private Nachfeld() {
        this(null);
    }

    public Nachfeld(@Nullable final Konstituentenfolge konstituentenfolge) {
        this.konstituentenfolge = konstituentenfolge;
    }

    @Override
    public Collection<Konstituentenfolge> toAltKonstituentenfolgen() {
        return Collections.singleton(konstituentenfolge);
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
        @Nullable final Konstituentenfolge konstituentenfolge =
                toAltKonstituentenfolgen().iterator().next();
        if (konstituentenfolge == null) {
            return "";
        }

        return konstituentenfolge.joinToSingleKonstituente().toTextOhneKontext();
    }
}
