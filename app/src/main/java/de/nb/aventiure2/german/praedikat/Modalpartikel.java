package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

/**
 * Eine Modalpartikel ist etwas wie "doch", "halt", "eben".
 */
public class Modalpartikel {
    private final String text;

    public Modalpartikel(final String text) {
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
