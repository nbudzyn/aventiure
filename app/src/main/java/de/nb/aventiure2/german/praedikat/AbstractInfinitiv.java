package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Abstrakte Oberklasse  Infinitivkonstruktion mit oder ohne "zu" ("den Frosch ignorieren",
 * "das Leben zu genießen").
 */
public abstract class AbstractInfinitiv implements IAlternativeKonstituentenfolgable {
    private final Konstituentenfolge ohneNachfeld;
    protected final Nachfeld nachfeld;

    AbstractInfinitiv(
            final Konstituentenfolge ohneNachfeld, final Nachfeld nachfeld) {
        this.ohneNachfeld = ohneNachfeld;
        this.nachfeld = nachfeld;
    }

    @Override
    public Collection<Konstituentenfolge> toAltKonstituentenfolgen() {
        return Collections.singleton(toKonstituentenfolge(true));
    }

    @Nullable
    Konstituentenfolge toKonstituentenfolgeOhneNachfeld() {
        return toKonstituentenfolge(true);
    }

    @Nullable
    private Konstituentenfolge toKonstituentenfolge(final boolean mitNachfeld) {
        return Konstituentenfolge.joinToNullKonstituentenfolge(
                ohneNachfeld,
                mitNachfeld ? nachfeld : null);
    }

    public Nachfeld getNachfeld() {
        return nachfeld;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AbstractInfinitiv abstractInfinitiv = (Infinitiv) o;
        return ohneNachfeld.equals(abstractInfinitiv.ohneNachfeld) && nachfeld
                .equals(abstractInfinitiv.nachfeld);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ohneNachfeld, nachfeld);
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
