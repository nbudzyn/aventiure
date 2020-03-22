package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.NumerusGenus.N;

/**
 * Ein Pronomen wie "alles".
 */
public class Indefinitpronomen extends DeklinierbarePhrase {
    public static final Indefinitpronomen ALLES =
            ip(N, "alles", "allem");

    private static Indefinitpronomen ip(final NumerusGenus numerusGenus,
                                        final String nominativDativUndAkkusativ) {
        return ip(numerusGenus, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }

    public static final Indefinitpronomen ip(final NumerusGenus numerusGenus,
                                             final String nominativUndAkkusativ,
                                             final String dativ) {
        return ip(numerusGenus, nominativUndAkkusativ, dativ, nominativUndAkkusativ);
    }

    public static final Indefinitpronomen ip(final NumerusGenus numerusGenus,
                                             final String nominativ, final String dativ,
                                             final String akkusativ) {
        return new Indefinitpronomen(numerusGenus, nominativ, dativ, akkusativ);
    }

    public Indefinitpronomen(final NumerusGenus numerusGenus,
                             final String nominativ, final String dativ, final String akkusativ) {
        super(numerusGenus, nominativ, dativ, akkusativ);
    }

    public Relativpronomen relPron() {
        return Relativpronomen.get(getNumerusGenus());
    }
}
