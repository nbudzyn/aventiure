package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

/**
 * Eine adverbiale Angabe (z.B. "aus Langeweile" oder "fröhlich",
 * "den ganzen Tag", "auf dem Tisch").
 */
public abstract class AbstractAdverbialeAngabe {
    private final String text;

    public AbstractAdverbialeAngabe(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @NonNull
    @Override
    public String toString() {
        // So muss es bleiben - wird so ausgegeben!
        return text;
    }
}
