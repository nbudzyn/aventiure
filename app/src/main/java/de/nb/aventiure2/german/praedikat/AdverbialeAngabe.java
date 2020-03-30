package de.nb.aventiure2.german.praedikat;

/**
 * Eine adverbiale Angabe (z.B. "aus Langeweile" oder "fröhlich").
 */
public class AdverbialeAngabe {
    private final String text;

    public AdverbialeAngabe(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
