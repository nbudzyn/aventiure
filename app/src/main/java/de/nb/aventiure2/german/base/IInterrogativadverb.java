package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import static de.nb.aventiure2.german.base.Konstituente.k;

public interface IInterrogativadverb extends IAlternativeKonstituentenfolgable, IInterrogativwort {
    @CheckReturnValue
    default Konstituente getDescription(final Person personSubjekt, final Numerus numerusSubjekt) {
        return k(getString());
    }

    @Override
    default ImmutableList<Konstituentenfolge> toAltKonstituentenfolgen() {
        return ImmutableList.of(new Konstituentenfolge(k(getString())));
    }

    String getString();
}
