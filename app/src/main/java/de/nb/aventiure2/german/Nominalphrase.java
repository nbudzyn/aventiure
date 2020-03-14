package de.nb.aventiure2.german;

/**
 * Eine Nominalphrase, z.B. "ein dicker, hässlicher Frosch".
 */
public class Nominalphrase extends DekliniertePhrase {
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
