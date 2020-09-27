package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Numerus.PL;
import static de.nb.aventiure2.german.base.Numerus.SG;

public enum NumerusGenus {
    // Singular Maskulinum
    M(SG),
    // Singular Feminimum
    F(SG),
    // Singular Neutrum
    N(SG),
    // Plural
    PL_MFN(PL);

    private final Numerus numerus;

    NumerusGenus(final Numerus numerus) {
        this.numerus = numerus;
    }

    public Numerus getNumerus() {
        return numerus;
    }
}
