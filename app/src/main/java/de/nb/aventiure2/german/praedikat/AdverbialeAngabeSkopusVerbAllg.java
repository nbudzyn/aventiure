package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
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
public class AdverbialeAngabeSkopusVerbAllg
        extends AbstractAdverbialeAngabe
        implements IAdvAngabeOderInterrogativVerbAllg {

    /**
     * Erzeugt eine adverbiale Angabe wie "aus lauter Wut".
     */
    @SuppressWarnings("GrazieInspection")
    public AdverbialeAngabeSkopusVerbAllg(final Praepositionalphrase praepositionalphrase) {
        this(praepositionalphrase.getDescription().joinToString()
                // Wenn die Präpositionalphrase später einmal einen Ergänzungs- oder
                // Angaben-Nebensatz
                // enthalten könnte - oder eine zu-Infinitiv-Phrase -, dann wäre sie im
                // Mittelfeld nicht erlaubt.
        );
    }

    /**
     * Erzeugt eine Adverbiale Angabe aus dieser Adjektivphrase. (Z.B. "glücklich" oder
     * "mich wundernd")
     */
    public AdverbialeAngabeSkopusVerbAllg(final AdjPhrOhneLeerstellen adjektivphrase) {
        super(adjektivphrase);
    }

    /**
     * Erzeugt eine adverbiale Bestimmung mit dem Skopus Verb allgemein; der Text darf
     * keine zu-Infinitive, keine Angabensätze (z.B. zum Adjektiv) und keine Ergönzungssätze
     * (z.B. zum Adjektiv) enthalten.
     */
    public AdverbialeAngabeSkopusVerbAllg(final String text) {
        super(text);
    }

    /**
     * Gibt zurück, ob diese adverbiale Angabe im Mittelfeld erlaubt ist.
     * <p>
     * Im Mittelfeld <i>nicht erlaubt</i> sind (adverbiale Angaben mit) Ergänzungs- oder
     * Angabe-Nebensätzen sowie zu-Infinitiv-Phrasen. (Beispielsweise ist nicht erlaubt:
     * *"Sie schaut dich glücklich, dich zu sehen, an.")
     */
    @Override
    public boolean imMittelfeldErlaubt() {
        return !getAdjektivphrase().enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
        // zu-Infinitivphrasen sowie Angaben- und Ergänzungssätze (z.B. indirekte Fragen)
        //  dürfen nicht im Mittelfeld stehen.
        // Vgl. *"Sie schaut dich, glücklich dich zu sehen, an."
        // (Möglich wären "Sie schaut dich an, glücklich, dich zu
        // sehen." - im Nachfeld - und "Glücklich, dich zu sehen, schaut sie dich
        // an." - im Vorfeld.)
    }
}
