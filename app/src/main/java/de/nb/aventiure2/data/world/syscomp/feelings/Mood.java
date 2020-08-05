package de.nb.aventiure2.data.world.syscomp.feelings;

import de.nb.aventiure2.german.praedikat.AdverbialeAngabe;

/**
 * Gemütszustand
 */
public enum Mood {
    // STORY Abends wird man MUEDE (neuer Status, ähnlich
    //  wie ERSCHOEPFT. Wer MUEDE ist, kann ebenfalls einschlafen).

    // STORY Man BLEIBT in aller Regel MUEDE, solange man nicht schläft -
    //  kommt drauf an, ob welche Emotion die STAERKERE ist
    //  Z.B. wird man nicht GEKNICKT, wenn man MUEDE ist.

    VOLLER_FREUDE(5, "fröhlich"),
    GLUECKLICH(4, "glücklich"),
    AUFGEDREHT(2, "aufgedreht"),
    BEWEGT(2, "selbstvergessen"),
    ZUFRIEDEN(1, "spielerisch"),
    // STORY (welche Emotion?) "versonnen"
    NEUTRAL(0, "aus Langeweile"),
    ANGESPANNT(0, "trotzig"),
    ERSCHOEPFT(-1, "erschöpft"),
    ETWAS_GEKNICKT(-2, "etwas geknickt"),
    // TODO Alternativen: betrübt, ...
    VERUNSICHERT(-2, "verunsichert"),
    UNTROESTLICH(-5, "voller Trauer");

    private final int gradDerFreude;
    private final AdverbialeAngabe adverbialeAngabe;

    Mood(final int gradDerFreude, final String adverbialeAngabeText) {
        this(gradDerFreude, new AdverbialeAngabe(adverbialeAngabeText));
    }

    Mood(final int gradDerFreude, final AdverbialeAngabe adverbialeAngabe) {
        this.gradDerFreude = gradDerFreude;
        this.adverbialeAngabe = adverbialeAngabe;
    }

    public boolean isTraurigerAls(final Mood other) {
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
