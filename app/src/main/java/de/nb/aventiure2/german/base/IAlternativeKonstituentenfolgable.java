package de.nb.aventiure2.german.base;

import java.util.Collection;

/**
 * Ein Objekt, das man als Konstituentenfolge darstellen kann - vielleicht auch als
 * mehrere alternative Konstituentenfolgen.
 */
public interface IAlternativeKonstituentenfolgable {
    /**
     * Stellt das Objekt als alternative Konstituentenfolgen dar -
     * eine der Alternativen darf auch {@code null} sein.
     */
    Collection<Konstituentenfolge> toAltKonstituentenfolgen();
}
