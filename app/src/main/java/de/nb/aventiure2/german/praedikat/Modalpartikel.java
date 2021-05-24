package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituentenfolge;

import static de.nb.aventiure2.german.base.Konstituente.k;

/**
 * Eine Modalpartikel ist etwas wie "doch", "halt", "eben".
 */
public class Modalpartikel implements IAlternativeKonstituentenfolgable {
    private final String text;

    public Modalpartikel(final String text) {
        this.text = text;
    }

    @Override
    public ImmutableList<Konstituentenfolge> toAltKonstituentenfolgen() {
        return ImmutableList.of(new Konstituentenfolge(k(text)));
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
