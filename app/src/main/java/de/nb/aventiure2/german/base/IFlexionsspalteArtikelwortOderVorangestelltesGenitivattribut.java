package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

public interface IFlexionsspalteArtikelwortOderVorangestelltesGenitivattribut
        extends DeklinierbarePhrase {

    static boolean traegtKasusendungFuerNominalphrasenkern(
            @Nullable
            final IFlexionsspalteArtikelwortOderVorangestelltesGenitivattribut flexionsspalte,
            final Kasus kasus) {
        if (flexionsspalte == null) {
            return false;
        }

        return flexionsspalte.traegtKasusendungFuerNominalphrasenkern(kasus);
    }

    /**
     * Gibt zurück, dass die vorangestellte Genitivphrase keine Kasusendung
     * <i>für den Phrasenkern der Nominalphrase</i> trägt:
     * "Annas großem [!!] Hunger" - anders als "dem großen [!] Hunger"
     * ("dem" trägt Kasusendung für den Phrasenkern der Nominalphrase).
     */
    boolean traegtKasusendungFuerNominalphrasenkern(Kasus kasus);
}
