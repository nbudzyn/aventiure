package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.Praepositionalphrase;

/**
 * Eine adverbiale Angabe, die sich eher auf den gesamten Satz bezieht, z.B.
 * "LEIDER kannst du schlecht singen" /
 * "Du kannst LEIDER schlecht singen".
 * <p>
 * Hierhink gehören <i>auf jeden Fall</i>, die "Satzadverbien / Satzadverbialien", also diejenigen
 * Adverbien / adverbialen Angaben k, bei denen folgende Paraphrase eines Satzes s, der k enthält,
 * möglich ist (Test): Es ist k der Fall, dass s', wobei s' aus s entsteht durch Weglassen von k.
 * ("vielleicht", "gestern", ...)
 */
public class AdvAngabeSkopusSatz
        extends AbstractAdvAngabe
        implements IAdvAngabeOderInterrogativSkopusSatz {
    /**
     * Erzeugt eine adverbiale Bestimmung mit dem Skopus Satz; der Text darf
     * keine zu-Infinitive, keine Angabensätze (z.B. zum Adjektiv) und keine Ergönzungssätze
     * (z.B. zum Adjektiv) enthalten.
     */
    public AdvAngabeSkopusSatz(final String text) {
        super(text);
    }

    public AdvAngabeSkopusSatz(final AdjPhrOhneLeerstellen adjektivphrase) {
        super(adjektivphrase);
    }

    public AdvAngabeSkopusSatz(final Praepositionalphrase praepositionalphrase) {
        super(praepositionalphrase);
    }

    public AdvAngabeSkopusSatz(final Negationspartikelphrase negationspartikelphrase) {
        super(negationspartikelphrase.getDescription());
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
        return !enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
        // zu-Infinitivphrasen sowie Angaben- und Ergänzungssätze (z.B. indirekte Fragen)
        //  dürfen nicht im Mittelfeld stehen.
        // Vgl. *"Sie schaut dich, glücklich dich zu sehen, an."
        // (Möglich wären "Sie schaut dich an, glücklich, dich zu
        // sehen." - im Nachfeld - und "Glücklich, dich zu sehen, schaut sie dich
        // an." - im Vorfeld.)
    }

    @Override
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return getAdjektivphrase().enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz();
    }

    public AdvAngabeSkopusVerbAllg toSkopusVerbAllg() {
        return new AdvAngabeSkopusVerbAllg(getAdjektivphrase());
    }
}
