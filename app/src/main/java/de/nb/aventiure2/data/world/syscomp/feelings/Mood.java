package de.nb.aventiure2.data.world.syscomp.feelings;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.stream.Stream;

import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;

import static com.google.common.collect.ImmutableList.toImmutableList;

/**
 * Gemütszustand
 */
public enum Mood {
    VOLLER_FREUDE(FeelingIntensity.SEHR_STARK, "fröhlich"),
    GLUECKLICH(FeelingIntensity.STARK, "glücklich"),
    BEGEISTERT(FeelingIntensity.DEUTLICH, "begeistert"),
    AUFGEDREHT(FeelingIntensity.MERKLICH, "aufgedreht"),
    BEWEGT(FeelingIntensity.MERKLICH, "selbstvergessen"),
    ZUFRIEDEN(FeelingIntensity.NUR_LEICHT, "spielerisch", "versonnen"),
    NEUTRAL(FeelingIntensity.NEUTRAL, "aus Langeweile"),
    ANGESPANNT(FeelingIntensity.NEUTRAL, "trotzig"),
    ETWAS_GEKNICKT(-FeelingIntensity.MERKLICH, "etwas geknickt"),
    VERUNSICHERT(-FeelingIntensity.MERKLICH, "verunsichert"),
    BETRUEBT(-FeelingIntensity.DEUTLICH, "betrübt"),
    TRAURIG(-FeelingIntensity.STARK, "traurig"),
    UNTROESTLICH(-FeelingIntensity.SEHR_STARK, "voller Trauer");

    private final int gradDerFreude;
    private final ImmutableList<AdverbialeAngabeSkopusSatz> altAdverbialeAngaben;

    Mood(final int gradDerFreude, final String... altAdverbialeAngabenTexte) {
        this(gradDerFreude,
                Stream.of(altAdverbialeAngabenTexte)
                        .map(AdverbialeAngabeSkopusSatz::new)
                        .collect(toImmutableList()));
    }

    Mood(final int gradDerFreude,
         final Collection<AdverbialeAngabeSkopusSatz> altAdverbialeAngaben) {
        FeelingIntensity.checkValuePositive(Math.abs(gradDerFreude));

        this.gradDerFreude = gradDerFreude;
        this.altAdverbialeAngaben = ImmutableList.copyOf(altAdverbialeAngaben);
    }

    public boolean isFroehlicherAls(final Mood other) {
        return gradDerFreude > other.gradDerFreude;
    }

    public boolean isTraurigerAls(final Mood other) {
        return gradDerFreude < other.gradDerFreude;
    }

    public ImmutableList<AdverbialeAngabeSkopusSatz> altAdverbialeAngaben() {
        return altAdverbialeAngaben;
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
