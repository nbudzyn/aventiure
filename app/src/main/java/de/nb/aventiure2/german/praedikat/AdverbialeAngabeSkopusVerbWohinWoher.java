package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.Praepositionalphrase;

/**
 * Eine adverbiale Angabe der Richtung, des Ziel (wohin?) oder
 * der Herkunft (woher?).
 * Hierzu rechnen wir auch entsprechende "Adverbialobjekte" wie
 * "Du stellst die Vase AUF DEN TISCH."
 */
public class AdverbialeAngabeSkopusVerbWohinWoher extends AbstractAdverbialeAngabe {
    public AdverbialeAngabeSkopusVerbWohinWoher(final String text) {
        super(text);
    }

    public AdverbialeAngabeSkopusVerbWohinWoher(final AdjPhrOhneLeerstellen adjektivphrase) {
        super(adjektivphrase);
    }

    public AdverbialeAngabeSkopusVerbWohinWoher(final Praepositionalphrase praepositionalphrase) {
        super(praepositionalphrase);
    }
}
