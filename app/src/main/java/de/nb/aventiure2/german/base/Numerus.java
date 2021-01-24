package de.nb.aventiure2.german.base;

public enum Numerus {
    /**
     * Singular (Einzahl)
     */
    SG,
    /**
     * Plural (Mehrzahl)
     */
    PL;

    public static Numerus forNumber(final int number) {
        if (number == 1) {
            return SG;
        }

        return PL;
    }
}
