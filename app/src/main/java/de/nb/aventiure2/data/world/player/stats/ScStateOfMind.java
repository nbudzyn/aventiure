package de.nb.aventiure2.data.world.player.stats;

import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;

/**
 * Gemütszustand des Spielercharakters
 */
// TODO Umbenennen in mood
public enum ScStateOfMind {
    // STORY Abends wird man MUEDE (neuer Status, ähnlich
    //  wie ERSCHOEPFT. Wer MUEDE ist, kann ebenfalls einschlafen).

    // STORY Man BLEIBT in aller Regel MUEDE, solange man nicht schläft -
    //  kommt drauf an, ob welche Emotion die STAERKERE ist
    //  Z.B. wird man nicht GEKNICKT, wenn man MUEDE ist.

    VOLLER_FREUDE(5, "fröhlich"),
    NEUTRAL(0, "aus Langeweile"),
    ANGESPANNT(0, "aus Trotz"),
    ERSCHOEPFT(0, "erschöpft"),
    ETWAS_GEKNICKT(-2, "etwas geknickt"), // TODO Alternativen: betrübt, ...
    UNTROESTLICH(-5, "voller Trauer");

    private final int gradDerFreude;
    private final AdverbialeAngabe adverbialeAngabe;

    ScStateOfMind(final int gradDerFreude, final String adverbialeAngabeText) {
        this(gradDerFreude, new AdverbialeAngabe(adverbialeAngabeText));
    }

    ScStateOfMind(final int gradDerFreude, final AdverbialeAngabe adverbialeAngabe) {
        this.gradDerFreude = gradDerFreude;
        this.adverbialeAngabe = adverbialeAngabe;
    }

    public boolean isTraurigerAls(final ScStateOfMind other) {
        return gradDerFreude < other.gradDerFreude;
    }

    public AdverbialeAngabe getAdverbialeAngabe() {
        return adverbialeAngabe;
    }

    /**
     * Ob der Spielercharakter gerade "emotional" ist.
     */
    public boolean isEmotional() {
        return Math.abs(gradDerFreude) > 1;
    }
}
