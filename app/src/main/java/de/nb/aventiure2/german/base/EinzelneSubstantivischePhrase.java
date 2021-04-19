package de.nb.aventiure2.german.base;

import javax.annotation.CheckReturnValue;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

/**
 * Eine ungereihte Phrase, die substantivisch verwendet werden kann, also insbesondere
 * ein Pronomen ("sie", "du", "wir") oder eine (andere) Nominalphrase ("die goldene Kugel").
 */
public interface EinzelneSubstantivischePhrase extends SubstantivischePhrase {
    @Override
    @CheckReturnValue
    default Konstituentenfolge artikellosDatK() {
        return joinToKonstituentenfolge(
                k(artikellosDatStr(), getNumerusGenus(), getBezugsobjekt()));
    }

    @Override
    @CheckReturnValue
    default Konstituentenfolge artikellosAkkK() {
        return joinToKonstituentenfolge(
                k(artikellosAkkStr(), getNumerusGenus(), getBezugsobjekt()));
    }

    @Override
    @CheckReturnValue
    default Konstituentenfolge imK(
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        return joinToKonstituentenfolge(
                k(imStr(kasusOderPraepositionalkasus), kannAlsBezugsobjektVerstandenWerdenFuer(),
                        getBezugsobjekt()));
    }

    default SubstPhrReihung und(final SubstantivischePhrase other) {
        return new SubstPhrReihung(this, other);
    }
}
