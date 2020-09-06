package de.nb.aventiure2.german.base;

/**
 * Eine Phrase, die substantivisch verwendet werden kann, also insbesonder ein Pronomen ("sie") oder
 * eine (andere) Nominalphrase ("die goldene Kugel").
 */
public abstract class SubstantivischePhrase extends DeklinierbarePhrase {
    public SubstantivischePhrase(final NumerusGenus numerusGenus) {
        super(numerusGenus);
    }

    /**
     * Ob die substantivische Phase mit einem Artikel beginnt, der mit einer
     * dazu geeigneten Präposition verschmolzen werden darf ("dem Haus" -> "zum Haus")
     * oder nicht ("einem Haus", "dem Haus zugewandte Straßenlaternen")
     */
    public abstract boolean erlaubtVerschmelzungVonPraepositionMitArtikel();

    /**
     * Die substantivische Phrase im Dativ, aber ohne Artikel
     * ("(zum) Haus")
     */
    public abstract String artikellosDat();

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
