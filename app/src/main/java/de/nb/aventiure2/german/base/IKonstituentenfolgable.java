package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * Ein Objekt, das man - ohne weitere Informationen zu benötigen -
 * als eine eindeutige Konstituentenfolge darstellen kann.
 */
public interface IKonstituentenfolgable extends IAlternativeKonstituentenfolgable {
    @Override
    default Collection<Konstituentenfolge> toAltKonstituentenfolgen() {
        return Collections.singleton(toKonstituentenfolge());
    }

    /**
     * Stellt das Objekt als Konstituentenfolge dar -
     * ggf. als {@code null} (sozusagen als "leere Konstituentenfolge").
     */
    @Nullable
    Konstituentenfolge toKonstituentenfolge();
}
