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
        final PhorikKandidat theoretischerPhorikKandidat =
                substantivischePhraseOderReflPron.imK(praepositionMitKasus.getKasus())
                        .getPhorikKandidat();

        return Konstituentenfolge.joinToKonstituentenfolge(
                modAdverbOderAdjektiv,
                k(praepositionMitKasus.getDescription(substantivischePhraseOderReflPron),
                        // Es sollte wohl eher selten sein, dass man ein prädikativ
                        // gebrauchte Phrase danach mit "er..." referenziert.
                        // Allerdings könnte es eventuell zu Verwirrung führen?
                        theoretischerPhorikKandidat.getNumerusGenus(), null)
        );
    }

    @Override
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                       final Numerus numerus) {
        return null;
    }

    public String getDescription() {
        // TODO Hier könnte die substantivischePhraseOderReflPron
        //  durchaus einen Phorik-Kandidaten enthalten - auch
        //  kannAlsBezugsobjektVerstandenWerdenFuer = X wäre gut möglich.
        //  Also sollte hier besser eine Konstituente mit diesen
        //  Angaben zurückgegeben werden.
        return GermanUtil.joinToString(
                modAdverbOderAdjektiv,
                w(praepositionMitKasus.getDescription(substantivischePhraseOderReflPron),
                        null));
    }
}
