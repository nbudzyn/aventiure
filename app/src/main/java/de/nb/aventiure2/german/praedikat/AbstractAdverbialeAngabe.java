package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.Adjektiv;
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
        this(
                // TODO Bei der dem Text könnte es sich durchaus um etwas anderes als ein
                //  einzelnes Adjektiv handeln. Beispielsweise könnte es sich um
                //  ein Präspositionalphrase handeln wie "in die Hände".
                //  Dann könnte diese Phrase möglicherweise einen Phorik-Kandidaten enthalten -
                //  zumindest ein kannAlsBezugsobjektVerstandenWerdenFuer = X wäre gut möglich.
                //  Vielleicht sollte "AdjPhrOhneLeerstellen adjektivphrase" durch
                //  etwas Ähnliches wie das Interface Praedikativum ersetzt werden.

                new Adjektiv(text).toAdjPhr());
    }

    AbstractAdverbialeAngabe(final AdjPhrOhneLeerstellen adjektivphrase) {
        this.adjektivphrase = adjektivphrase;
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
