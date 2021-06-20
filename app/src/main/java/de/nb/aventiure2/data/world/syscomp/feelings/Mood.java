package de.nb.aventiure2.data.world.syscomp.feelings;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.util.StreamUtil.*;
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
            new AdvAngabeSkopusSatz("trotzig")),
    ETWAS_GEKNICKT(-FeelingIntensity.MERKLICH,
            AdjektivOhneErgaenzungen.GEKNICKT.mitGraduativerAngabe("etwas"),
            new AdvAngabeSkopusSatz("etwas geknickt")),
    VERUNSICHERT(-FeelingIntensity.MERKLICH, AdjektivOhneErgaenzungen.VERUNSICHERT),
    BETRUEBT(-FeelingIntensity.DEUTLICH, AdjektivOhneErgaenzungen.BETRUEBT),
    VERDROSSEN(-FeelingIntensity.DEUTLICH, AdjektivOhneErgaenzungen.VERDROSSEN),
    TRAURIG(-FeelingIntensity.STARK, AdjektivOhneErgaenzungen.TRAURIG),
    UNTROESTLICH(-FeelingIntensity.SEHR_STARK, "voller Trauer");

    private final int gradDerFreude;

    /**
     * Adjektive zur Beschreibung dieses "Moods", <i>möglicherweise leer</i>.
     */
    private final ImmutableList<AdjPhrOhneLeerstellen> altSpAdjPhr;

    /**
     * Adverbiale Angabe zur Beschreibung dieses "Moods".
     */
    private final ImmutableList<AdvAngabeSkopusSatz> altAdvAngaben;

    Mood(final int gradDerFreude, final String... altAdvAngabenTexte) {
        this(gradDerFreude,
                ImmutableList.of(),
                stream(altAdvAngabenTexte)
                        .map(AdvAngabeSkopusSatz::new)
                        .collect(ImmutableList.toImmutableList())
        );
    }

    Mood(final int gradDerFreude,
         final AdjektivOhneErgaenzungen... altSpAdjPhr) {
        this(gradDerFreude,
                ImmutableList.copyOf(altSpAdjPhr),
                stream(altSpAdjPhr)
                        .map(a -> new AdvAngabeSkopusSatz(
                                a.getPraedikativ(
                                        // irrelevant für AdjektivOhneErgaenzungen
                                        P2, SG).joinToString()))
                        .collect(toList()));
    }

    Mood(final int gradDerFreude,
         final AdvAngabeSkopusSatz altAdvAngabe) {
        this(gradDerFreude, ImmutableList.of(), ImmutableList.of(altAdvAngabe));
    }

    Mood(final int gradDerFreude,
         final AdjPhrOhneLeerstellen altSpAdjPhr,
         final AdvAngabeSkopusSatz altAdvAngabe) {
        this(gradDerFreude, ImmutableList.of(altSpAdjPhr), ImmutableList.of(altAdvAngabe));
    }

    Mood(final int gradDerFreude,
         final AdjPhrOhneLeerstellen[] altSpAdjPhr,
         final AdvAngabeSkopusSatz[] altAdvAngaben) {
        this(gradDerFreude, asList(altSpAdjPhr), asList(altAdvAngaben));
    }

    Mood(final int gradDerFreude,
         final Collection<AdjPhrOhneLeerstellen> altSpAdjPhr,
         final Collection<AdvAngabeSkopusSatz> altAdvAngaben) {
        FeelingIntensity.checkValuePositive(Math.abs(gradDerFreude));

        this.gradDerFreude = gradDerFreude;
        this.altSpAdjPhr = ImmutableList.copyOf(altSpAdjPhr);
        this.altAdvAngaben = ImmutableList.copyOf(altAdvAngaben);
    }

    public boolean isFroehlicherAls(final Mood other) {
        return gradDerFreude > other.gradDerFreude;
    }

    public boolean isTraurigerAls(final Mood other) {
        return gradDerFreude < other.gradDerFreude;
    }

    public ImmutableList<AdvAngabeSkopusVerbAllg> altAdvAngabenSkopusVerbAllg() {
        return mapToList(altAdvAngaben, AdvAngabeSkopusSatz::toSkopusVerbAllg);
    }

    public ImmutableList<AdvAngabeSkopusSatz> altAdvAngabenSkopusSatz() {
        return altAdvAngaben;
    }

    /**
     * Eventuell Adjektive zur Beschreibung dieses "Moods", <i>möglicherweise leer</i>.
     */
    public ImmutableList<AdjPhrOhneLeerstellen> altSpAdjPhr() {
        return altSpAdjPhr;
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
