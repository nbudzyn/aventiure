package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.NumerusGenus.N;

/**
 * Ein Pronomen wie "alles", "nichts".
 */
public class Indefinitpronomen extends DeklinierbarePhrase {
    public static final Indefinitpronomen ALLES =
            ip(N, Relativpronomen.Typ.WERWAS, "alles", "allem");
    public static final Indefinitpronomen NICHTS =
            // Dativ: "Von NICHTS kommt nichts."
            ip(N, Relativpronomen.Typ.WERWAS, "nichts");

    /**
     * Mit welchem Typ von Relativpronomen steht das Indefinitpronomen?
     * ("alles, was"; "es gibt nichts, was mir fehlt")
     */
    private final Relativpronomen.Typ relPronTyp;

    private static Indefinitpronomen ip(final NumerusGenus numerusGenus,
                                        final Relativpronomen.Typ relPronTyp,
                                        final String nominativDativUndAkkusativ) {
        return ip(numerusGenus, relPronTyp, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }

    public static final Indefinitpronomen ip(final NumerusGenus numerusGenus,
                                             final Relativpronomen.Typ relPronTyp,
                                             final String nominativUndAkkusativ,
                                             final String dativ) {
        return ip(numerusGenus, relPronTyp, nominativUndAkkusativ, dativ, nominativUndAkkusativ);
    }

    public static final Indefinitpronomen ip(final NumerusGenus numerusGenus,
                                             final Relativpronomen.Typ relPronTyp,
                                             final String nominativ, final String dativ,
                                             final String akkusativ) {
        return new Indefinitpronomen(numerusGenus, relPronTyp, nominativ, dativ, akkusativ);
    }

    public Indefinitpronomen(final NumerusGenus numerusGenus,
                             final Relativpronomen.Typ relPronTyp,
                             final String nominativ, final String dativ, final String akkusativ) {
        super(numerusGenus, nominativ, dativ, akkusativ);
        this.relPronTyp = relPronTyp;
    }

    @Override
    public Personalpronomen persPron() {
        // "Ich habe mir alles angesehen. Es hat mir gefallen."
        return Personalpronomen.get(getNumerusGenus());
    }

    @Override
    public Relativpronomen relPron() {
        return Relativpronomen.get(relPronTyp, getNumerusGenus());
    }
}
