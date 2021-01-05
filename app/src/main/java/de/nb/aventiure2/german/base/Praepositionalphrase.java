package de.nb.aventiure2.german.base;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Wortfolge.w;

/**
 * Eine Präpositionalphrase, also eine Präposition mit einer davon abhängigen Phrase
 */
public class Praepositionalphrase implements Praedikativum {
    /**
     * Ein Adverb oder Adjektiv, dass die Phrase modifiziert:
     * "schräg über der Tür"
     */
    @Nullable
    private final String modAdverbOderAdjektiv;

    @Nonnull
    private final PraepositionMitKasus praepositionMitKasus;

    @Nonnull
    private final SubstantivischePhraseOderReflexivpronomen substantivischePhraseOderReflPron;

    Praepositionalphrase(
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhraseOderReflexivpronomen substantivischePhraseOderReflPron) {
        this(null, praepositionMitKasus, substantivischePhraseOderReflPron);
    }

    private Praepositionalphrase(
            @Nullable final String modAdverbOderAdjektiv,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstantivischePhraseOderReflexivpronomen substantivischePhraseOderReflPron) {
        this.modAdverbOderAdjektiv = modAdverbOderAdjektiv;
        this.substantivischePhraseOderReflPron = substantivischePhraseOderReflPron;
        this.praepositionMitKasus = praepositionMitKasus;
    }

    public Praepositionalphrase mitModAdverbOderAdjektiv(
            @Nullable final String modAdverbOderAdjektiv) {
        return new Praepositionalphrase(modAdverbOderAdjektiv, praepositionMitKasus,
                substantivischePhraseOderReflPron);
    }

    @Override
    public Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(
                modAdverbOderAdjektiv,
                k(praepositionMitKasus.getDescription(substantivischePhraseOderReflPron))
        );
    }

    @Override
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                       final Numerus numerus) {
        return null;
    }

    public String getDescription() {
        return GermanUtil.joinToString(
                modAdverbOderAdjektiv,
                w(praepositionMitKasus.getDescription(substantivischePhraseOderReflPron)));
    }
}
