package de.nb.aventiure2.german.base;

/**
 * Eine Phrase, die substantivisch verwendet werden kann, also insbesonder ein Pronomen ("sie") oder
 * eine (andere) Nominalphrase ("die goldene Kugel").
 */
public abstract class SubstantivischePhrase extends EinreihigDeklinierbarePhrase {

    public SubstantivischePhrase(final NumerusGenus numerusGenus,
                                 final String nominativDativUndAkkusativ) {
        this(numerusGenus, nominativDativUndAkkusativ, nominativDativUndAkkusativ);
    }


    public SubstantivischePhrase(final NumerusGenus numerusGenus,
                                 final String nominativAkkusativ, final String dativ) {
        this(numerusGenus, nominativAkkusativ, dativ, nominativAkkusativ);
    }

    public SubstantivischePhrase(final NumerusGenus numerusGenus,
                                 final String nominativ, final String dativ,
                                 final String akkusativ) {
        super(numerusGenus, nominativ, dativ, akkusativ);
    }

    public String im(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        if (kasusOderPraepositionalkasus instanceof Kasus) {
            return im((Kasus) kasusOderPraepositionalkasus);
        }

        if (kasusOderPraepositionalkasus instanceof PraepositionMitKasus) {
            final PraepositionMitKasus praepositionMitKasus =
                    (PraepositionMitKasus) kasusOderPraepositionalkasus;

            return praepositionMitKasus.getDescription(this);
        }

        throw new IllegalArgumentException("Unexpected Kasus or Präpositionalkasus: " +
                kasusOderPraepositionalkasus);
    }

    /**
     * Gibt ein Personalpronomen für diese Phrase zurück.
     */
    public abstract Personalpronomen persPron();

    /**
     * Gibt einen Possessivartikel für diese Phrase zurück.
     */
    public abstract Possessivartikel possArt();

    /**
     * Gibt ein Relativpronomen für diese Phrase zurück.
     */
    public abstract Relativpronomen relPron();
}
