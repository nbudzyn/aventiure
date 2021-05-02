package de.nb.aventiure2.german.adjektiv;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;

/**
 * Eine Ergänzung des Adjektivs wie "sehr", "äußerst", "kaum" etc. Sie kann in
 * der Regel <i>nicht</i> abgetrennt (aus der Adjektivphrase herausgelöst) werden.
 */
class GraduativeAngabe implements IAlternativeKonstituentenfolgable {
    private final String text;

    GraduativeAngabe(final String text) {
        this.text = text;
    }

    @Override
    public ImmutableList<Konstituentenfolge> toAltKonstituentenfolgen() {
        return Konstituente.k(text).toAltKonstituentenfolgen();
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
