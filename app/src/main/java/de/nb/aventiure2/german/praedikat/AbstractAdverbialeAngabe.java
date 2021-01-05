package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.Adjektiv;
import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Eine adverbiale Angabe (z.B. "aus Langeweile" oder "fröhlich",
 * "den ganzen Tag", "auf dem Tisch").
 */
public abstract class AbstractAdverbialeAngabe {
    private final AdjPhrOhneLeerstellen adjektivphrase;

    AbstractAdverbialeAngabe(final String text) {
        this(new Adjektiv(text).toAdjPhr());
    }

    AbstractAdverbialeAngabe(final AdjPhrOhneLeerstellen adjektivphrase) {
        this.adjektivphrase = adjektivphrase;
    }

    /**
     * Gibt den Text der adverbialen Angabe zurück - Achtung, der Aufrufer muss sich
     * darum kümmmern, dass kein noch ausstehendes Nachkomma verloren geht!
     */
    private String getText(final Person personSubjekt, final Numerus numerusSubjekt) {
        return GermanUtil.joinToString(getDescription(personSubjekt, numerusSubjekt));
    }

    public Konstituente getDescription(final Person personSubjekt, final Numerus numerusSubjekt) {
        // Anscheinend muss die gesamte adverbiale Phrase kontinuierlich bleiben.
        // Dann können wir sie ohne Verlust zu einer einzigen Konstituente zusammenfassen.
        return joinToKonstituente(adjektivphrase, personSubjekt, numerusSubjekt);
    }

    private static Konstituente joinToKonstituente(final AdjPhrOhneLeerstellen adjektivphrase,
                                                   final Person personSubjekt,
                                                   final Numerus numerusSubjekt) {
        return adjektivphrase.getPraedikativOderAdverbial(personSubjekt, numerusSubjekt)
                .joinToNullSingleKonstituente(
                )
                .withVorkommaNoetig(
                        // So eine adverbiale Angabe muss offenbar durch Komma
                        // abgetrennt sein: *"Sie schaut dich an glücklich, dich zu
                        // sehen.")
                        adjektivphrase.enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz());
    }

    AdjPhrOhneLeerstellen getAdjektivphrase() {
        return adjektivphrase;
    }
}
