package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.Praepositionalphrase;

/**
 * Eine adverbiale Angabe, die sich eher auf das Verb allein bezieht, z.B.
 * "Leider kannst du SCHLECHT singen" - und zwar <i>keine</i> Angabe von
 * Richtung / Ziel (wohin?) oder Herkunft (woher?).
 * <p>
 * "Satzadverbien / Satzadverbialien" gehören auf jeden Fall <i>nicht</i> hierher. (Das wären
 * Adverbien / adverbialen Angaben k, bei denen folgende Paraphrase eines Satzes s, der k enthält,
 * möglich ist (Test): Es ist k der Fall, dass s', wobei s' aus s entsteht durch Weglassen von k.)
 * Dazu siehe {@link AdverbialeAngabeSkopusSatz}
 */
public class AdverbialeAngabeSkopusVerbAllg extends AbstractAdverbialeAngabe {
    private final boolean imMittelfeldErlaubt;

    /**
     * Erzeugt eine adverbiale Angabe wie "aus lauter Wut".
     */
    public AdverbialeAngabeSkopusVerbAllg(final Praepositionalphrase praepositionalphrase) {
        this(praepositionalphrase.getDescription(),
                true
                // Wenn die Präpositionalphrase später einmal einen Ergänzungs- oder Angaben-Nebensatz
                // enthalten könnte - oder eine zu-Infinitiv-Phrase - dann wäre sie im
                // Mittelfeld nicht erlaubt.
        );
    }

    /**
     * Erzeugt eine Adverbiale Angabe aus dieser Adjektivphrase. (Z.B. "glücklich" oder
     * "mich wundernd")
     *
     * @param personSubjekt  Die Person des Subjekts
     * @param numerusSubjekt Der Numerus des Subjekts
     */
    public AdverbialeAngabeSkopusVerbAllg(final AdjPhrOhneLeerstellen adjektivphrase,
                                          final Person personSubjekt,
                                          final Numerus numerusSubjekt) {
        this( // Anscheinend muss die gesamte adverbiale Phrase kontinuierlich bleiben.
                // Dann können wir sie ohne Verlust zu einer einzigen Konstituente zusammenfassen.
                Konstituente.joinToNullSingleKonstituente(
                        adjektivphrase.getPraedikativOderAdverbial(personSubjekt, numerusSubjekt))
                        .withVorkommaNoetig(
                                adjektivphrase
                                        .enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz()),
                // zu-Infinitivphrasen sowie Angaben- und Ergänzungssätze (z.B. indirekte Fragen)
                //  dürfen nicht im Mittelfeld stehen.
                // Vgl. *"Sie schaut dich, glücklich dich zu sehen, an."
                // (Möglich wären "Sie schaut dich an, glücklich, dich zu
                // sehen." - im Nachfeld - und "Glücklich, dich zu sehen, schaut sie dich
                // an." - im Vorfeld.)
                // Außerdem muss so eine adverbiale Angabe offenbar auch vorn durch Komma
                // Seiten abgetrennt sein: *"Sie schaut dich an glücklich, dich zu
                // sehen.")
                !adjektivphrase.enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz());
    }

    public AdverbialeAngabeSkopusVerbAllg(final String text) {
        this(text, true);
    }

    public AdverbialeAngabeSkopusVerbAllg(final String text, final boolean imMittelfeldErlaubt) {
        super(text);
        this.imMittelfeldErlaubt = imMittelfeldErlaubt;
    }

    public AdverbialeAngabeSkopusVerbAllg(final Konstituente konstituente,
                                          final boolean imMittelfeldErlaubt) {
        super(konstituente);
        this.imMittelfeldErlaubt = imMittelfeldErlaubt;
    }

    /**
     * Gibt zurück, ob diese adverbiale Angabe im Mittelfeld erlaubt ist.
     * <p>
     * Im Mittelfeld <i>nicht erlaubt</i> sind (adverbiale Angaben mit) Ergänzungs- oder
     * Angabe-Nebensätzen sowie zu-Infinitiv-Phrasen. (Beispielsweise ist nicht erlaubt:
     * *"Sie schaut dich glücklich, dich zu sehen, an.")
     */
    boolean imMittelfeldErlaubt() {
        return imMittelfeldErlaubt;
    }
}
