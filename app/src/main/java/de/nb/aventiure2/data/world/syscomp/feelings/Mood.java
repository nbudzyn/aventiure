package de.nb.aventiure2.data.world.syscomp.feelings;

import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;

/**
 * Gemütszustand
 */
public enum Mood {
    VOLLER_FREUDE(FeelingIntensity.SEHR_STARK, "fröhlich"),
    GLUECKLICH(FeelingIntensity.STARK, "glücklich"),
    BEGEISTERT(FeelingIntensity.DEUTLICH, "begeistert"),
    AUFGEDREHT(FeelingIntensity.MERKLICH, "aufgedreht"),
    BEWEGT(FeelingIntensity.MERKLICH, "selbstvergessen"),
    ZUFRIEDEN(FeelingIntensity.NUR_LEICHT, "spielerisch"),
    // STORY (welche Emotion?) "versonnen"
    NEUTRAL(FeelingIntensity.NEUTRAL, "aus Langeweile"),
    ANGESPANNT(FeelingIntensity.NEUTRAL, "trotzig"),
    ETWAS_GEKNICKT(-FeelingIntensity.MERKLICH, "etwas geknickt"),
    VERUNSICHERT(-FeelingIntensity.MERKLICH, "verunsichert"),
    BETRUEBT(-FeelingIntensity.DEUTLICH, "betrübt"),
    TRAURIG(-FeelingIntensity.STARK, "traurig"),
    UNTROESTLICH(-FeelingIntensity.SEHR_STARK, "voller Trauer");

    private final int gradDerFreude;
    private final AdverbialeAngabeSkopusSatz adverbialeAngabe;

    Mood(final int gradDerFreude, final String adverbialeAngabeText) {
        this(gradDerFreude, new AdverbialeAngabeSkopusSatz(adverbialeAngabeText));
    }

    Mood(final int gradDerFreude, final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        FeelingIntensity.checkValue(Math.abs(gradDerFreude));

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

    /**
     * Ob der Spielercharakter gerade "sehr emotional" ist.
     */
    public boolean isSehrEmotional() {
        return Math.abs(gradDerFreude) > 3;
    }

    int getGradDerFreude() {
        return gradDerFreude;
    }
}
