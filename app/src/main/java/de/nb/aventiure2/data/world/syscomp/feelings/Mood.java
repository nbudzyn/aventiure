package de.nb.aventiure2.data.world.syscomp.feelings;

import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;

/**
 * Gemütszustand
 */
public enum Mood {
    // FIXME Abends wird man MUEDE (neuer Status, ähnlich
    //  wie ERSCHOEPFT. Wer MUEDE ist, kann ebenfalls einschlafen).

    // FIXME Man BLEIBT in aller Regel MUEDE, solange man nicht schläft -
    //  kommt drauf an, ob welche Emotion die STAERKERE ist
    //  Z.B. wird man nicht GEKNICKT, wenn man MUEDE ist.

    // 6 / -6 wäre pathologisch

    VOLLER_FREUDE(FeelingIntensity.SEHR_STARK, "fröhlich"),
    GLUECKLICH(FeelingIntensity.STARK, "glücklich"),
    BEGEISTERT(FeelingIntensity.DEUTLICH, "begeistert"),
    AUFGEDREHT(FeelingIntensity.MERKLICH, "aufgedreht"),
    BEWEGT(FeelingIntensity.MERKLICH, "selbstvergessen"),
    ZUFRIEDEN(FeelingIntensity.NUR_LEICHT, "spielerisch"),
    // STORY (welche Emotion?) "versonnen"
    NEUTRAL(FeelingIntensity.NEUTRAL, "aus Langeweile"),
    ANGESPANNT(FeelingIntensity.NEUTRAL, "trotzig"),
    ERSCHOEPFT(-FeelingIntensity.NUR_LEICHT, "erschöpft"),
    ETWAS_GEKNICKT(-FeelingIntensity.MERKLICH, "etwas geknickt"),
    // TODO Alternativen: betrübt, ...
    VERUNSICHERT(-FeelingIntensity.MERKLICH, "verunsichert"),
    UNTROESTLICH(-FeelingIntensity.SEHR_STARK, "voller Trauer");

    private final int gradDerFreude;
    private final AdverbialeAngabeSkopusSatz adverbialeAngabe;

    Mood(final int gradDerFreude, final String adverbialeAngabeText) {
        this(gradDerFreude, new AdverbialeAngabeSkopusSatz(adverbialeAngabeText));
    }

    Mood(final int gradDerFreude, final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        this.gradDerFreude = gradDerFreude;
        this.adverbialeAngabe = adverbialeAngabe;
    }

    public boolean isFroehlicherAls(final Mood other) {
        return gradDerFreude > other.gradDerFreude;
    }

    public boolean isTraurigerAls(final Mood other) {
        return gradDerFreude < other.gradDerFreude;
    }

    public AdverbialeAngabeSkopusSatz getAdverbialeAngabe() {
        return adverbialeAngabe;
    }

    /**
     * Ob der Spielercharakter gerade "emotional" ist.
     */
    public boolean isEmotional() {
        return Math.abs(gradDerFreude) > 1;
    }
}
