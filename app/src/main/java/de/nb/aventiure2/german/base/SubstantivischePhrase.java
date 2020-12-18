package de.nb.aventiure2.german.base;

import java.util.Objects;

/**
 * Eine Phrase, die substantivisch verwendet werden kann, also insbesondere ein Pronomen ("sie") oder
 * eine (andere) Nominalphrase ("die goldene Kugel").
 */
public abstract class SubstantivischePhrase implements DeklinierbarePhrase {
    private final NumerusGenus numerusGenus;

    public SubstantivischePhrase(final NumerusGenus numerusGenus) {
        this.numerusGenus = numerusGenus;
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
     * Gibt ein {@link Reflexivpronomen} für diese Phrase zurück.
     */
    public abstract Reflexivpronomen reflPron();

    /**
     * Gibt einen Possessivartikel für diese Phrase zurück.
     */
    public abstract Possessivartikel possArt();

    /**
     * Gibt ein Relativpronomen für diese Phrase zurück.
     */
    public abstract Relativpronomen relPron();

    public Numerus getNumerus() {
        return getNumerusGenus().getNumerus();
    }

    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }

    public abstract Person getPerson();

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SubstantivischePhrase that = (SubstantivischePhrase) o;
        return numerusGenus == that.numerusGenus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerusGenus);
    }
}
