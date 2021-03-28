package de.nb.aventiure2.german.praedikat;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.Adjektiv;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praepositionalphrase;

/**
 * Eine adverbiale Angabe (z.B. "aus Langeweile" oder "fröhlich",
 * "den ganzen Tag", "auf dem Tisch").
 */
public abstract class AbstractAdvAngabe {
    private final AdjPhrOhneLeerstellen adjektivphrase;

    AbstractAdvAngabe(final Praepositionalphrase praepositionalphrase) {
        // TODO Wenn es sich um eine Präpositionalphrase handelt wie
        //  "mit Peters Hilfe", dann könnte diese Phrase möglicherweise einen
        //  Phorik-Kandidaten enthalten (Peter).
        //  Wenn die Phrase allerdings keinen Phorik-Kandidaten enthält,
        //  dann ist kannAlsBezugsobjektVerstandenWerdenFuer = X wohl eher nicht
        //  möglich; etwas wie "mit voller Konzentration" bewirkt nicht, dass man ein
        //  nachfolgendes "sie" auf "die Konzentration" beziehen würde.
        this(praepositionalphrase.getDescription().joinToString());
    }

    AbstractAdvAngabe(final String text) {
        // TODO Wenn es sich um eine Präpositionalphrase handelt wie
        //  "mit Peters Hilfe", dann könnte diese Phrase möglicherweise einen
        //  Phorik-Kandidaten enthalten (Peter).
        //  Wenn die Phrase allerdings keinen Phorik-Kandidaten enthält,
        //  dann ist kannAlsBezugsobjektVerstandenWerdenFuer = X wohl eher nicht
        //  möglich; etwas wie "mit voller Konzentration" bewirkt nicht, dass man ein
        //  nachfolgendes "sie" auf "die Konzentration" beziehen würde.

        // TODO Vielleicht sollte "AdjPhrOhneLeerstellen adjektivphrase" durch
        //  etwas Ähnliches wie das Interface Praedikativum ersetzt werden.
        this(Adjektiv.nichtFlektierbar(text).toAdjPhr());
    }

    AbstractAdvAngabe(final AdjPhrOhneLeerstellen adjektivphrase) {
        this.adjektivphrase = adjektivphrase;
    }

    @CheckReturnValue
    public Konstituente getDescription(final Person personSubjekt, final Numerus numerusSubjekt) {
        // Anscheinend muss die gesamte adverbiale Phrase kontinuierlich bleiben.
        // Dann können wir sie ohne Verlust zu einer einzigen Konstituente zusammenfassen.
        return joinToKonstituente(adjektivphrase, personSubjekt, numerusSubjekt);
    }

    @CheckReturnValue
    private static Konstituente joinToKonstituente(final AdjPhrOhneLeerstellen adjektivphrase,
                                                   final Person personSubjekt,
                                                   final Numerus numerusSubjekt) {
        return adjektivphrase.getPraedikativOderAdverbial(personSubjekt, numerusSubjekt)
                .joinToSingleKonstituente()
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
