package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituente.k;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.IKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Eine Modalpartikel ist etwas wie "doch", "halt", "eben".
 */
public class Modalpartikel implements IKonstituentenfolgable {
    private final String text;

    public Modalpartikel(final String text) {
        this.text = text;
    }

    @Override
    @NonNull
    public Konstituentenfolge toKonstituentenfolge() {
        return new Konstituentenfolge(k(text));
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
