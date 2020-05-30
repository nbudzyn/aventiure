package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;

/**
 * Eine Präposition, die einen bestimmten Kasus fordert.
 */
public enum PraepositionMitKasus implements KasusOderPraepositionalkasus {
    /**
     * "mit dem Frosch"
     */
    MIT_DAT("mit", DAT),

    AN_AKK("an", AKK);

    /**
     * Die Präposition (z.B. "mit")
     */
    private final String praeposition;

    /**
     * Der Kasus, den diese Präposition fordert.
     */
    private final Kasus kasus;

    private PraepositionMitKasus(final String praeposition, final Kasus kasus) {
        this.praeposition = praeposition;
        this.kasus = kasus;
    }

    public String getDescription(final SubstantivischePhrase substantivischePhrase) {
        return praeposition + " " + substantivischePhrase.im(kasus);
    }

    public String getPraeposition() {
        return praeposition;
    }

    public Kasus getKasus() {
        return kasus;
    }
}
