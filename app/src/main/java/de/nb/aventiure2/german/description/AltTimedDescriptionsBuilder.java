package de.nb.aventiure2.german.description;


import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.german.description.TimedDescription.toTimed;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Builder für alternative {@link TimedDescription}s.
 */
@CanIgnoreReturnValue
public class AltTimedDescriptionsBuilder {
    private final ImmutableList.Builder<TimedDescription<? extends AbstractDescription<?>>>
            altDescriptions;

    private UnaryOperator<TimedDescription<? extends AbstractDescription<?>>> op = null;

    @CheckReturnValue
    public static AltTimedDescriptionsBuilder altTimed() {
        return new AltTimedDescriptionsBuilder();
    }

    /**
     * Erzeugt einen {@link AltTimedDescriptionsBuilder} auf Basis des
     * AltDescriptionsBuilders.
     */
    @CheckReturnValue
    AltTimedDescriptionsBuilder(final AltDescriptionsBuilder altDescBuilder,
                                final AvTimeSpan timeElapsed) {
        altDescriptions =
                ImmutableList.<TimedDescription<? extends AbstractDescription<?>>>builder()
                        .addAll(toTimed(altDescBuilder.build(), timeElapsed));
    }

    @CheckReturnValue
    private AltTimedDescriptionsBuilder() {
        altDescriptions = ImmutableList.builder();
    }

    public AltTimedDescriptionsBuilder addAll(final AltTimedDescriptionsBuilder other) {
        return addAll(other.altDescriptions);
    }

    public AltTimedDescriptionsBuilder addAllIfOtherwiseEmtpy(
            final Stream<? extends TimedDescription<
                    ? extends AbstractDescription<?>>> stream) {
        if (isEmpty()) {
            addAll(stream);
        }

        return this;
    }

    public AltTimedDescriptionsBuilder addAll(
            final Stream<? extends TimedDescription<
                    ? extends AbstractDescription<?>>> stream) {
        return addAll(stream.collect(toImmutableSet()));
    }

    private AltTimedDescriptionsBuilder addAll(
            final ImmutableCollection.Builder<
                    ? extends TimedDescription<
                            ? extends AbstractDescription<?>>> builder) {
        return addAll(builder.build());
    }

    public AltTimedDescriptionsBuilder addAll(final Iterable<? extends TimedDescription<
            ? extends AbstractDescription<?>>> altTimed) {
        altDescriptions.addAll(altTimed);
        return this;
    }

    @SafeVarargs
    public final AltTimedDescriptionsBuilder add(
            final AvTimeSpan timeElapsed,
            final AbstractDescription<? extends AbstractDescription<?>>... alt) {
        addAll(toTimed(timeElapsed, alt));
        return this;
    }

    @SafeVarargs
    public final AltTimedDescriptionsBuilder add(
            final TimedDescription<? extends AbstractDescription<?>>... altTimed) {
        altDescriptions.add(altTimed);
        return this;
    }

    public AltTimedDescriptionsBuilder komma() {
        return map(TimedDescription::komma);
    }

    public AltTimedDescriptionsBuilder undWartest() {
        return map(TimedDescription::undWartest);
    }

    public AltTimedDescriptionsBuilder dann() {
        return map(TimedDescription::dann);
    }

    public AltTimedDescriptionsBuilder withCounterIdIncrementedIfTextIsNarrated(
            final Enum<?> counterId) {
        return map(d -> d.withCounterIdIncrementedIfTextIsNarrated(counterId));
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     *
     * @param substantivischePhrase Substantivische Phrase in der dritten Person
     */
    public AltTimedDescriptionsBuilder phorikKandidat(
            final SubstantivischePhrase substantivischePhrase,
            final IBezugsobjekt bezugsobjekt) {
        return map(d -> d.phorikKandidat(substantivischePhrase, bezugsobjekt));
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     */
    public AltTimedDescriptionsBuilder phorikKandidat(
            final NumerusGenus numerusGenus,
            final Belebtheit belebtheit,
            final IBezugsobjekt bezugsobjekt) {
        return map(d -> d.phorikKandidat(numerusGenus, belebtheit, bezugsobjekt));
    }

    private AltTimedDescriptionsBuilder map(
            final UnaryOperator<TimedDescription<? extends AbstractDescription<?>>> op) {
        if (this.op == null) {
            this.op = op;
        } else {
            final UnaryOperator<TimedDescription<? extends AbstractDescription<?>>> oldOp = this.op;
            // oldOp nicht inlinen! Das ändert wohl die Semantik zu einer Endlosschleife!
            this.op = d -> op.apply(oldOp.apply(d));
        }

        return this;
    }

    @CheckReturnValue
    public boolean isEmpty() {
        return altDescriptions.build().isEmpty();
    }

    @CheckReturnValue
    public ImmutableList<TimedDescription<? extends AbstractDescription<?>>> build() {
        if (op == null) {
            return altDescriptions.build();
        }

        final ImmutableList.Builder<TimedDescription<? extends AbstractDescription<?>>> res =
                ImmutableList.builder();

        for (final TimedDescription<? extends AbstractDescription<?>> desc : altDescriptions
                .build()) {
            res.add(op.apply(desc));
        }

        return res.build();
    }
}
