package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL;

/**
 * Eine Nominalphrase, z.B. "ein dicker, hässlicher Frosch".
 */
public class Nominalphrase extends DeklinierbarePhrase {
    // Allgemeine Nominalfphrasen, die sich nicht auf ein
    // AvObject oder eine AbstractEntity beziehen.
    public static final Nominalphrase ANGEBOTE =
            np(PL, "Angebote", "Angeboten");
    public static final Nominalphrase GESPRAECH =
            np(N, "das Gespräch", "dem Gespräch");

    public static final Nominalphrase np(final NumerusGenus numerusGenus,
                                         final String nominativDativUndAkkusativ) {
        return new Nominalphrase(numerusGenus, nominativDativUndAkkusativ);
    }

    public static final Nominalphrase np(final NumerusGenus numerusGenus,
                                         final String nominativUndAkkusativ,
                                         final String dativ) {
        return new Nominalphrase(numerusGenus, nominativUndAkkusativ, dativ);
    }

    public static final Nominalphrase np(final NumerusGenus numerusGenus,
                                         final String nominativ, final String dativ,
                                         final String akkusativ) {
        return new Nominalphrase(numerusGenus, nominativ, dativ, akkusativ);
    }

    public Nominalphrase(final NumerusGenus numerusGenus,
                         final String nominativDativUndAkkusativ) {
        this(numerusGenus, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }

    public Nominalphrase(final NumerusGenus numerusGenus,
                         final String nominativAkkusativ, final String dativ) {
        this(numerusGenus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    public Nominalphrase(final NumerusGenus numerusGenus,
                         final String nominativ, final String dativ, final String akkusativ) {
        super(numerusGenus, nominativ, dativ, akkusativ);
    }

    public Relativpronomen relPron() {
        return Relativpronomen.get(getNumerusGenus());
    }
}
