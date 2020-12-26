package de.nb.aventiure2.german.praedikat;

import de.nb.aventiure2.german.base.Praepositionalphrase;

/**
 * Eine adverbiale Angabe, die sich eher auf das Verb allein bezieht, z.B.
 * "Leider kannst du SCHLECHT singen" - und zwar <i>keine</i> Angabe von
 * Richtung / Ziel (wohin?) oder Herkunft (woher?).
 */
public class AdverbialeAngabeSkopusVerbAllg extends AbstractAdverbialeAngabe {
    public AdverbialeAngabeSkopusVerbAllg(final String text) {
        super(text);
    }

    /**
     * Eine adverbiale Angabe wie "vor Wut".
     */
    public AdverbialeAngabeSkopusVerbAllg(final Praepositionalphrase praepositionalphrase) {
        super(praepositionalphrase.getDescription());
    }
}
