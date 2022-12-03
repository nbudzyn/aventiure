package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Konstituente.k;

import androidx.annotation.NonNull;

import javax.annotation.CheckReturnValue;

public interface IInterrogativadverb extends IKonstituentenfolgable, IInterrogativwort,
        IAdvAngabeOderInterrogativ {
    @Override
    @NonNull
    default Konstituentenfolge toKonstituentenfolge() {
        return new Konstituentenfolge(getDescription());
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
