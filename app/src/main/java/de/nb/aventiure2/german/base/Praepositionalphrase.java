package de.nb.aventiure2.german.base;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;

/**
 * Eine Präpositionalphrase, also eine Präposition mit einer davon abhängigen Phrase
 */
public class Praepositionalphrase implements Praedikativum {
    /**
     * Ein Adverb oder Adjektiv, dass die Phrase modifiziert:
     * "schräg über der Tür".
     * <p>
     * Hierzu zählen wir auch vorangestellte "Lokalkombinatoren"
     * wie "rings (um den Teich)", "mitten (in der Stadt)" und
     * "quer (zur Straße)". Vgl. zu denen
     * {@link http://sprachwissenschaft.fau.de/personen/daten/breindl/breindl_2006_quer-durch-die
     * -wortarten-rings-um-die-phrasensyntax-mitten-in-die-semantik.pdf}
     */
    // Es gibt auch noch nachgestellte Lokalkombinatoren wie "am Haus (entlang)" und
    // "an der Straße (längs)". Die sind derzeit nicht abgedeckt.
    // Auch zu denen vgl. http://sprachwissenschaft.fau
    // .de/personen/daten/breindl/breindl_2006_quer-durch-die-wortarten-rings-um-die
    // -phrasensyntax-mitten-in-die-semantik.pdf .
    @Nullable
    private final String modAdverbOderAdjektiv;

    @Nonnull
    private final PraepositionMitKasus praepositionMitKasus;

    @Nonnull
    private final SubstPhrOderReflexivpronomen substPhrOderReflPron;

    Praepositionalphrase(
            final PraepositionMitKasus praepositionMitKasus,
            final SubstPhrOderReflexivpronomen substPhrOderReflPron) {
        this(null, praepositionMitKasus, substPhrOderReflPron);
    }

    private Praepositionalphrase(
            @Nullable final String modAdverbOderAdjektiv,
            final PraepositionMitKasus praepositionMitKasus,
            final SubstPhrOderReflexivpronomen substPhrOderReflPron) {
        this.modAdverbOderAdjektiv = modAdverbOderAdjektiv;
        this.substPhrOderReflPron = substPhrOderReflPron;
        this.praepositionMitKasus = praepositionMitKasus;
    }

    public Praepositionalphrase mitModAdverbOderAdjektiv(
            @Nullable final String modAdverbOderAdjektiv) {
        return new Praepositionalphrase(modAdverbOderAdjektiv, praepositionMitKasus,
                substPhrOderReflPron);
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge getPraedikativ(final Person person, final Numerus numerus) {
        return joinToKonstituentenfolge(
                modAdverbOderAdjektiv,
                // Es sollte wohl eher selten sein, dass man ein prädikativ
                // gebrauchte Phrase danach mit "er..." referenziert.
                // Allerdings könnte ein enthaltenes Substantiv zur Verwirrung
                // führen.
                praepositionMitKasus.getDescription(substPhrOderReflPron).ohneBezugsobjekt());
    }

    @Override
    public Konstituentenfolge getPraedikativAnteilKandidatFuerNachfeld(final Person person,
                                                                       final Numerus numerus) {
        return null;
    }

    public Konstituentenfolge getDescription() {
        return joinToKonstituentenfolge(
                modAdverbOderAdjektiv,
                praepositionMitKasus.getDescription(substPhrOderReflPron));
    }
}
