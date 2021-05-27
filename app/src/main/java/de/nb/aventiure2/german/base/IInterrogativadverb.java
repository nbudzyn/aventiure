package de.nb.aventiure2.german.base;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import static de.nb.aventiure2.german.base.Konstituente.k;

public interface IInterrogativadverb extends IAlternativeKonstituentenfolgable, IInterrogativwort {
    @Override
    default ImmutableList<Konstituentenfolge> toAltKonstituentenfolgen() {
        return ImmutableList.of(new Konstituentenfolge(getDescription()));
    }

    @CheckReturnValue
    default Konstituente getDescription() {
        return k(getString());
    }

    String getString();
}
