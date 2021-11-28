package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

/**
 * Eine ungereihte Phrase, die substantivisch verwendet werden kann, also insbesondere
 * ein Pronomen ("sie", "du", "wir") oder eine (andere) Nominalphrase ("die goldene Kugel").
 */
public interface EinzelneSubstantivischePhrase extends SubstantivischePhrase {
    @Override
    @CheckReturnValue
    default Konstituentenfolge artikellosDatK() {
        return joinToKonstituentenfolge(
                k(artikellosDatStr(), getNumerusGenus(), getBelebtheit(), getBezugsobjekt()));
    }

    @Override
    @CheckReturnValue
    default Konstituentenfolge artikellosAkkK() {
        return joinToKonstituentenfolge(
                k(artikellosAkkStr(), getNumerusGenus(), getBelebtheit(), getBezugsobjekt()));
    }

    @Override
    @CheckReturnValue
    default Konstituentenfolge imK(
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        return joinToKonstituentenfolge(
                k(imStr(kasusOderPraepositionalkasus), kannAlsBezugsobjektVerstandenWerdenFuer(),
                        getBelebtheit(),
                        getBezugsobjekt()));
    }

    default SubstPhrReihung und(final SubstantivischePhrase other) {
        return new SubstPhrReihung(this, other);
    }

    @Override
    EinzelneSubstantivischePhrase mitFokuspartikel(@Nullable final String fokuspartikel);

    @Override
    EinzelneSubstantivischePhrase neg(Negationspartikelphrase negationspartikelphrase,
                                      boolean moeglichstNegativIndefiniteWoerterVerwenden);
}
