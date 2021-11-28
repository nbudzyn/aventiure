package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Konstituente.k;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

public interface IInterrogativadverb extends IAlternativeKonstituentenfolgable, IInterrogativwort,
        IAdvAngabeOderInterrogativ {
    @Override
    default ImmutableList<Konstituentenfolge> toAltKonstituentenfolgen() {
        return ImmutableList.of(new Konstituentenfolge(getDescription()));
    }

    @Override
    @CheckReturnValue
    default Konstituente getDescription(final PraedRegMerkmale praedRegMerkmale) {
        return getDescription();
    }

    @CheckReturnValue
    default Konstituente getDescription() {
        return k(getString());
    }

    String getString();
}
