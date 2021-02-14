package de.nb.aventiure2.german.description;


import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.german.description.TimedDescription.toTimed;

/**
 * Ein Builder für alternative {@link TimedDescription}s.
 */
public class AltTimedDescriptionsBuilder {
    private final ImmutableList.Builder<TimedDescription<? extends AbstractDescription<?>>>
            altDescriptions;

    private UnaryOperator<TimedDescription<? extends AbstractDescription<?>>> op = null;

    public static AltTimedDescriptionsBuilder altTimed() {
        return new AltTimedDescriptionsBuilder();
    }

    /**
     * Erzeugt einen {@link AltTimedDescriptionsBuilder} auf Basis des
     * AltDescriptionsBuilders.
     */
    AltTimedDescriptionsBuilder(final AltDescriptionsBuilder altDescBuilder,
                                final AvTimeSpan timeElapsed) {
        altDescriptions =
                ImmutableList.<TimedDescription<? extends AbstractDescription<?>>>builder()
                        .addAll(toTimed(altDescBuilder.build(), timeElapsed));
    }

    private AltTimedDescriptionsBuilder() {
        altDescriptions = ImmutableList.builder();
    }

    public AltTimedDescriptionsBuilder addAllIfOtherwiseEmpty(
            final AltTimedDescriptionsBuilder other) {
        return addAllIfOtherwiseEmpty(other.altDescriptions);
    }

    public AltTimedDescriptionsBuilder addAllIfOtherwiseEmpty(
            final Stream<? extends TimedDescription<? extends AbstractDescription<?>>> stream) {
        return addAllIfOtherwiseEmpty(stream.collect(toImmutableSet()));
    }

    private AltTimedDescriptionsBuilder addAllIfOtherwiseEmpty(
            final ImmutableCollection.Builder<
                    ? extends TimedDescription<? extends AbstractDescription<?>>> builder) {
        return addAllIfOtherwiseEmpty(builder.build());
    }

    private AltTimedDescriptionsBuilder addAllIfOtherwiseEmpty(
            final Iterable<? extends TimedDescription<
                    ? extends AbstractDescription<?>>> altTimed) {
        if (isEmpty()) {
            addAll(altTimed);
        }

        return this;
    }

    public AltTimedDescriptionsBuilder addAll(final AltTimedDescriptionsBuilder other) {
        return addAll(other.altDescriptions);
    }

    public AltTimedDescriptionsBuilder addAll(
            final Stream<? extends TimedDescription<
                    ? extends AbstractDescription<?>>> stream) {
        return addAll(stream.collect(toImmutableSet()));
    }

    public AltTimedDescriptionsBuilder addAll(
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
    public final AltTimedDescriptionsBuilder addIfOtherwiseEmtpy(
            final TimedDescription<? extends AbstractDescription<?>>... altTimed) {
        if (isEmpty()) {
            add(altTimed);
        }

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

    public AltTimedDescriptionsBuilder beendet(final StructuralElement structuralElement) {
        return map(d -> d.beendet(structuralElement));
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
            final IBezugsobjekt bezugsobjekt) {
        return map(d -> d.phorikKandidat(numerusGenus, bezugsobjekt));
    }

    public AltTimedDescriptionsBuilder map(
            final UnaryOperator<TimedDescription<? extends AbstractDescription<?>>> op) {
        if (this.op == null) {
            this.op = op;
        } else {
            this.op = d -> op.apply(this.op.apply(d));
        }

        return this;
    }

    private boolean isEmpty() {
        return altDescriptions.build().isEmpty();
    }

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
