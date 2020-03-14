package de.nb.aventiure2.german;

/**
 * Eine Nominalphrase, z.B. "ein dicker, hässlicher Frosch".
 */
public class Nominalphrase extends DekliniertePhrase {
    public static final Nominalphrase np(final Genus genus,
                                         final String nominativDativUndAkkusativ) {
        return new Nominalphrase(genus, nominativDativUndAkkusativ);
    }

    public static final Nominalphrase np(final Genus genus,
                                         final String nominativUndAkkusativ,
                                         final String dativ) {
        return new Nominalphrase(genus, nominativUndAkkusativ, dativ);
    }

    public static final Nominalphrase np(final Genus genus,
                                         final String nominativ, final String dativ,
                                         final String akkusativ) {
        return new Nominalphrase(genus, nominativ, dativ, akkusativ);
    }

    public Nominalphrase(final Genus genus,
                         final String nominativDativUndAkkusativ) {
        this(genus, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }


    public Nominalphrase(final Genus genus,
                         final String nominativAkkusativ, final String dativ) {
        this(genus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    public Nominalphrase(final Genus genus,
                         final String nominativ, final String dativ, final String akkusativ) {
        super(genus, nominativ, dativ, akkusativ);
    }

    public Relativpronomen relPron() {
        return Relativpronomen.get(getGenus());
    }
}
