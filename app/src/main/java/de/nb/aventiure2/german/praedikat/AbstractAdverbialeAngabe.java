package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Konstituente;

import static de.nb.aventiure2.german.base.Konstituente.k;

/**
 * Eine adverbiale Angabe (z.B. "aus Langeweile" oder "fröhlich",
 * "den ganzen Tag", "auf dem Tisch").
 */
public abstract class AbstractAdverbialeAngabe {
    private final Konstituente konstituente;

    AbstractAdverbialeAngabe(final String text) {
        this(k(text));
    }

    AbstractAdverbialeAngabe(final Konstituente konstituente) {
        this.konstituente = konstituente;
    }

    public String getText() {
        return GermanUtil.joinToNullString(konstituente);
    }

    public Konstituente getDescription() {
        return konstituente;
    }

    @NonNull
    @Override
    public String toString() {
        return getText();
    }
}
