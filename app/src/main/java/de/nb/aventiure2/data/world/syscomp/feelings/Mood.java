package de.nb.aventiure2.data.world.syscomp.feelings;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

/**
 * Gemütszustand
 */
public enum Mood {
    VOLLER_FREUDE(FeelingIntensity.SEHR_STARK, "fröhlich"),
    GLUECKLICH(FeelingIntensity.STARK, AdjektivOhneErgaenzungen.GLUECKLICH),
    BEGEISTERT(FeelingIntensity.DEUTLICH, "begeistert"),
    AUFGEDREHT(FeelingIntensity.MERKLICH, "aufgedreht"),
    BEWEGT(FeelingIntensity.MERKLICH, "selbstvergessen"),
    ZUFRIEDEN(FeelingIntensity.NUR_LEICHT, "spielerisch", "versonnen"),
    NEUTRAL(FeelingIntensity.NEUTRAL, "aus Langeweile"),
    ANGESPANNT(FeelingIntensity.NEUTRAL,
            AdjektivOhneErgaenzungen.ANGESPANNT,
            new AdverbialeAngabeSkopusSatz("trotzig")),
    ETWAS_GEKNICKT(-FeelingIntensity.MERKLICH,
            AdjektivOhneErgaenzungen.GEKNICKT.mitGraduativerAngabe("etwas"),
            new AdverbialeAngabeSkopusSatz("etwas geknickt")),
    VERUNSICHERT(-FeelingIntensity.MERKLICH, AdjektivOhneErgaenzungen.VERUNSICHERT),
    BETRUEBT(-FeelingIntensity.DEUTLICH, AdjektivOhneErgaenzungen.BETRUEBT),
    VERDROSSEN(-FeelingIntensity.DEUTLICH, AdjektivOhneErgaenzungen.VERDROSSEN),
    TRAURIG(-FeelingIntensity.STARK, AdjektivOhneErgaenzungen.TRAURIG),
    UNTROESTLICH(-FeelingIntensity.SEHR_STARK, "voller Trauer");

    private final int gradDerFreude;

    /**
     * Adjektive zur Beschreibung dieses "Moods", <i>möglicherweise leer</i>.
     */
    private final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhr;

    /**
     * Adverbiale Angabe zur Beschreibung dieses "Moods".
     */
    private final ImmutableList<AdverbialeAngabeSkopusSatz> altAdverbialeAngaben;

    Mood(final int gradDerFreude, final String... altAdverbialeAngabenTexte) {
        this(gradDerFreude,
                ImmutableList.of(),
                stream(altAdverbialeAngabenTexte)
                        .map(AdverbialeAngabeSkopusSatz::new)
                        .collect(ImmutableList.toImmutableList())
        );
    }

    Mood(final int gradDerFreude,
         final AdjektivOhneErgaenzungen... altAdjPhr) {
        this(gradDerFreude,
                ImmutableList.copyOf(altAdjPhr),
                stream(altAdjPhr)
                        .map(a -> new AdverbialeAngabeSkopusSatz(
                                a.getPraedikativ(
                                        // irrelevant für AdjektivOhneErgaenzungen
                                        P2, SG).joinToString(
                                )))
                        .collect(toList()));
    }

    Mood(final int gradDerFreude,
         final AdverbialeAngabeSkopusSatz altAdverbialeAngabe) {
        this(gradDerFreude, ImmutableList.of(), ImmutableList.of(altAdverbialeAngabe));
    }

    Mood(final int gradDerFreude,
         final AdjPhrOhneLeerstellen altAdjPhr,
         final AdverbialeAngabeSkopusSatz altAdverbialeAngabe) {
        this(gradDerFreude, ImmutableList.of(altAdjPhr), ImmutableList.of(altAdverbialeAngabe));
    }

    Mood(final int gradDerFreude,
         final AdjPhrOhneLeerstellen[] altAdjPhr,
         final AdverbialeAngabeSkopusSatz[] altAdverbialeAngaben) {
        this(gradDerFreude, asList(altAdjPhr), asList(altAdverbialeAngaben));
    }

    Mood(final int gradDerFreude,
         final Collection<AdjPhrOhneLeerstellen> altAdjPhr,
         final Collection<AdverbialeAngabeSkopusSatz> altAdverbialeAngaben) {
        FeelingIntensity.checkValuePositive(Math.abs(gradDerFreude));

        this.gradDerFreude = gradDerFreude;
        this.altAdjPhr = ImmutableList.copyOf(altAdjPhr);
        this.altAdverbialeAngaben = ImmutableList.copyOf(altAdverbialeAngaben);
    }

    public boolean isFroehlicherAls(final Mood other) {
        return gradDerFreude > other.gradDerFreude;
    }

    public boolean isTraurigerAls(final Mood other) {
        return gradDerFreude < other.gradDerFreude;
    }

    public ImmutableList<AdverbialeAngabeSkopusVerbAllg> altAdverbialeAngabenSkopusVerbAllg() {
        return altAdverbialeAngaben.stream()
                .map(AdverbialeAngabeSkopusSatz::toSkopusVerbAllg)
                .collect(toImmutableList());
    }

    public ImmutableList<AdverbialeAngabeSkopusSatz> altAdverbialeAngabenSkopusSatz() {
        return altAdverbialeAngaben;
    }

    /**
     * Eventuell Adjektive zur Beschreibung dieses "Moods", <i>möglicherweise leer</i>.
     */
    public ImmutableList<AdjPhrOhneLeerstellen> altAdjPhr() {
        return altAdjPhr;
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
