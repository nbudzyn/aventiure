package de.nb.aventiure2.german.description;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * A description of something and the time it takes.
 */
public class TimedDescription<D extends AbstractDescription<?>> {
    private final D description;

    /**
     * Zeit, die vergangen ist, während das Beschriebene geschehen ist
     */
    private final AvTimeSpan timeElapsed;

    /**
     * Wenn der Text sicher erzählt wird, wird dieser Counter hochgezählt.
     */
    @Nullable
    private final Enum<?> counterIdIncrementedIfTextIsNarrated;

    @SafeVarargs
    @CheckReturnValue
    @NonNull
    public static <D extends AbstractDescription<?>>
    ImmutableSet<TimedDescription<D>> toTimed(final AvTimeSpan timeElapsed,
                                              final D... descriptions) {
        return toTimed(asList(descriptions), timeElapsed);
    }

    @CheckReturnValue
    @NonNull
    public static <D extends AbstractDescription<?>>
    ImmutableSet<TimedDescription<D>> toTimed(
            final Collection<D> descriptions, final AvTimeSpan timeElapsed) {
        return toTimed(descriptions, timeElapsed, null);
    }

    @CheckReturnValue
    @NonNull
    public static <D extends AbstractDescription<?>>
    ImmutableSet<TimedDescription<D>> toTimed(
            final Collection<D> descriptions, final AvTimeSpan timeElapsed,
            @Nullable final Enum<?> counterIdIncrementedIfTextIsNarrated) {
        return mapToSet(descriptions, d -> new TimedDescription<>(
                d, timeElapsed, counterIdIncrementedIfTextIsNarrated));
    }

    @CheckReturnValue
    @NonNull
    public static <D extends AbstractDescription<?>>
    ImmutableList<D> toUntimed(
            final Collection<? extends TimedDescription<D>> timedDescriptions) {
        return mapToList(timedDescriptions, TimedDescription::getDescription);
    }

    public TimedDescription(final D description,
                            final AvTimeSpan timeElapsed) {
        this(description, timeElapsed, null);
    }

    private TimedDescription(final D description,
                             final AvTimeSpan timeElapsed,
                             @Nullable final Enum<?> counterIdIncrementedIfTextIsNarrated) {
        this.description = description;
        this.timeElapsed = timeElapsed;
        this.counterIdIncrementedIfTextIsNarrated = counterIdIncrementedIfTextIsNarrated;
    }

    @CheckReturnValue
    public <OTHER extends AbstractDescription<?>> TimedDescription<OTHER>
    withDescription(final OTHER description) {
        return new TimedDescription<>(description, timeElapsed,
                counterIdIncrementedIfTextIsNarrated);
    }

    @NonNull
    @CheckReturnValue
    public ImmutableList<TimedDescription<TextDescription>> altMitPraefix(
            final Konstituentenfolge praefixKonstituentenfolge) {
        return withAltDescriptions(getDescription().altMitPraefix(praefixKonstituentenfolge));
    }

    @NonNull
    @CheckReturnValue
    public static ImmutableList<TimedDescription<?>> withAltDescriptions(
            final Collection<TimedDescription<?>> timedDescriptions,
            final Function<AbstractDescription<?>, Collection<? extends AbstractDescription<?>>> altFunction) {
        return timedDescriptions.stream()
                .flatMap(d -> d.withAltDescriptions(
                        altFunction.apply(d.getDescription())).stream())
                .collect(toImmutableList());
    }

    @SafeVarargs
    @NonNull
    @CheckReturnValue
    public final <OTHER extends AbstractDescription<?>>
    ImmutableList<TimedDescription<OTHER>> withAltDescriptions(
            final OTHER... altDescriptions) {
        return withAltDescriptions(asList(altDescriptions));
    }

    @NonNull
    @CheckReturnValue
    private <OTHER extends AbstractDescription<?>> ImmutableList<TimedDescription<OTHER>> withAltDescriptions(
            final Collection<OTHER> altDescriptions) {
        return mapToList(altDescriptions, this::withDescription);
    }

    @NonNull
    @CheckReturnValue
    public TimedDescription<TextDescription> mitPraefix(
            final Konstituentenfolge praefixKonstituentenfolge) {
        return withDescription(getDescription().mitPraefix(praefixKonstituentenfolge));
    }

    public TimedDescription<D> withCounterIdIncrementedIfTextIsNarrated(
            @Nullable final Enum<?> counterIdIncrementedIfTextIsNarrated) {
        return new TimedDescription<>(description, timeElapsed,
                counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public TimedDescription<D> multiplyTimeElapsedWith(final double speedFactor) {
        return new TimedDescription<>(description,
                timeElapsed.times(speedFactor), counterIdIncrementedIfTextIsNarrated);
    }

    public StructuralElement getStartsNew() {
        return getDescription().getStartsNew();
    }

    public TimedDescription<D> komma() {
        getDescription().komma(true);
        return this;
    }

    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public TimedDescription<D> undWartest() {
        getDescription().undWartest(true);
        return this;
    }

    public TimedDescription<D> undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        getDescription().undWartest(
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt);
        return this;
    }

    public boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return getDescription().isAllowsAdditionalDuSatzreihengliedOhneSubjekt();
    }

    public TimedDescription<D> dann() {
        getDescription().dann(true);
        return this;
    }

    public TimedDescription<D> dann(final boolean dann) {
        getDescription().dann(dann);
        return this;
    }

    public boolean isDann() {
        return getDescription().isDann();
    }


    public TimedDescription<D> schonLaenger() {
        getDescription().schonLaenger(true);
        return this;
    }

    public TimedDescription<D> schonLaenger(final boolean schonLaenger) {
        getDescription().schonLaenger(schonLaenger);
        return this;
    }

    public boolean isSchonLaenger() {
        return getDescription().isSchonLaenger();
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     *
     * @param substantivischePhrase Substantivische Phrase in der dritten Person
     */
    public TimedDescription<D> phorikKandidat(
            final SubstantivischePhrase substantivischePhrase,
            final IBezugsobjekt bezugsobjekt) {
        checkArgument(substantivischePhrase.getPerson() == P3,
                "Substantivische Phrase %s hat falsche "
                        + "Person: %s. Für Phorik-Kandiaten "
                        + "ist nur 3. Person zugelassen.", substantivischePhrase,
                substantivischePhrase.getPerson());
        getDescription().phorikKandidat(substantivischePhrase.getNumerusGenus(),
                substantivischePhrase.getBelebtheit(),
                bezugsobjekt);
        return this;
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     */
    public TimedDescription<D> phorikKandidat(final NumerusGenus numerusGenus,
                                              final Belebtheit belebtheit,
                                              final IBezugsobjekt bezugsobjekt) {
        getDescription().phorikKandidat(new PhorikKandidat(numerusGenus,
                belebtheit, bezugsobjekt));
        return this;
    }

    public TimedDescription<D> phorikKandidat(@Nullable final PhorikKandidat phorikKandidat) {
        getDescription().phorikKandidat(phorikKandidat);
        return this;
    }

    public D getDescription() {
        return description;
    }

    public AvTimeSpan getTimeElapsed() {
        return timeElapsed;
    }

    @Nullable
    public Enum<?> getCounterIdIncrementedIfTextIsNarrated() {
        return counterIdIncrementedIfTextIsNarrated;
    }

    @NonNull
    @Override
    public String toString() {
        return "\"" + description + "\" (" + timeElapsed
                + (counterIdIncrementedIfTextIsNarrated != null ?
                ", counterIdIncrementedIfTextIsNarrated=" + counterIdIncrementedIfTextIsNarrated :
                "")
                + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TimedDescription<?> that = (TimedDescription<?>) o;
        return timeElapsed.equals(that.timeElapsed) &&
                Objects.equals(counterIdIncrementedIfTextIsNarrated,
                        that.counterIdIncrementedIfTextIsNarrated) &&
                description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeElapsed, counterIdIncrementedIfTextIsNarrated, description);
    }
}
