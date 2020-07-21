package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

/**
 * Eine adverbiale Angabe (z.B. "aus Langeweile" oder "fröhlich",
 * "den ganzen Tag").
 */
public class AdverbialeAngabe {
    private final String text;

    public AdverbialeAngabe(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
