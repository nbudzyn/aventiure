package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Konstituente.k;

public interface IInterrogativadverb extends IInterrogativwort {
    default Konstituente getDescription(final Person personSubjekt, final Numerus numerusSubjekt) {
        return k(getString());
    }

    String getString();
}
