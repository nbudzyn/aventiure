package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Praepositionalphrase;

/**
 * Eine adverbiale Angabe der Richtung, des Ziel (wohin?) oder
 * der Herkunft (woher?).
 * Hierzu rechnen wir auch entsprechende "Adverbialobjekte" wie
 * "Du stellst die Vase AUF DEN TISCH."
 */
public class AdvAngabeSkopusVerbWohinWoher
        extends AbstractAdvAngabe
        implements IAdvAngabeOderInterrogativWohinWoher {
    public AdvAngabeSkopusVerbWohinWoher(final String text) {
        super(text);
    }

    public AdvAngabeSkopusVerbWohinWoher(final AdjPhrOhneLeerstellen adjektivphrase) {
        super(adjektivphrase);
    }

    public AdvAngabeSkopusVerbWohinWoher(final Praepositionalphrase praepositionalphrase) {
        super(praepositionalphrase);
    }
}
