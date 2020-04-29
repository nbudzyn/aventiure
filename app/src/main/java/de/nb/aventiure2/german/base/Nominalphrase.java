package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;

/**
 * Eine Nominalphrase, z.B. "ein dicker, hässlicher Frosch".
 */
public class Nominalphrase extends DeklinierbarePhrase {
    // Allgemeine Nominalfphrasen, die sich nicht auf ein
    // AvObject oder eine AbstractEntity beziehen.
    public static final Nominalphrase ANGEBOTE =
            np(PL_MFN, "Angebote", "Angeboten");
    public static final Nominalphrase DINGE =
            np(PL_MFN, "die Dinge", "den Dingen");
    public static final Nominalphrase GESPRAECH =
            np(N, "das Gespräch", "dem Gespräch");

    public static final Nominalphrase np(final NumerusGenus numerusGenus,
                                         final String nominativDativUndAkkusativ) {
        return np(numerusGenus, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }

    public static final Nominalphrase np(final NumerusGenus numerusGenus,
                                         final String nominativUndAkkusativ,
                                         final String dativ) {
        return np(numerusGenus, nominativUndAkkusativ, dativ, nominativUndAkkusativ);
    }

    public static final Nominalphrase np(final NumerusGenus numerusGenus,
                                         final String nominativ, final String dativ,
                                         final String akkusativ) {
        return new Nominalphrase(numerusGenus, nominativ, dativ, akkusativ);
    }

    public Nominalphrase(final NumerusGenus numerusGenus,
                         final String nominativ, final String dativ, final String akkusativ) {
        super(numerusGenus, nominativ, dativ, akkusativ);
    }

    @Override
    public Personalpronomen persPron() {
        return Personalpronomen.get(getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(getNumerusGenus());
    }
}
