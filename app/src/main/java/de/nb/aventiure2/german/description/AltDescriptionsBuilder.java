package de.nb.aventiure2.german.description;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static com.google.common.collect.ImmutableSet.builder;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToAltKonstituentenfolgen;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * Ein Builder für alternative {@link AbstractDescription}s.
 */
public class AltDescriptionsBuilder {
    private final ImmutableSet.Builder<AbstractDescription<?>> alt = builder();

    private UnaryOperator<AbstractDescription<?>> op = null;

    /**
     * Fügt diese Teile zu alternativen {@link AbstractDescription}s
     * zusammen. Gibt es für mehrere Teile mehrere Alternativen, so werden
     * alle Kombinationen erzeugt. Jede der Alternativen beginnt
     * einen neuen Satz im Sinne von {@link StructuralElement#SENTENCE}.
     *
     * @return Mehrere alternative Wortfolgen. Wenn eine der Kombinationen ausschließlich
     * {@code null}-Werte enthält, wird die Collection auch den Wert
     * {@code null} enthalten.
     */
    @Nonnull
    public static AltDescriptionsBuilder altNeueSaetze(final Object... parts) {
        return altNeueSaetze(SENTENCE, parts);
    }

    /**
     * Fügt diese Teile zu alternativen {@link AbstractDescription}s
     * zusammen. Gibt es für mehrere Teile mehrere Alternativen, so werden
     * alle Kombinationen erzeugt. Jede der Alternativen beginnt das
     * structuralElement neu (z.B. einen neuen Absatz).
     *
     * @return Mehrere alternative Wortfolgen. Wenn eine der Kombinationen ausschließlich
     * {@code null}-Werte enthält, wird die Collection auch den Wert
     * {@code null} enthalten.
     */
    @Nonnull
    public static AltDescriptionsBuilder altNeueSaetze(final StructuralElement structuralElement,
                                                       final Object... parts) {
        return alt().addAll(joinToAltKonstituentenfolgen(parts).stream()
                .map(k -> neuerSatz(structuralElement, k)));
    }

    public static AltDescriptionsBuilder alt() {
        return new AltDescriptionsBuilder();
    }

    private AltDescriptionsBuilder() {
    }

    public AltDescriptionsBuilder addAll(final AltDescriptionsBuilder other) {
        return addAll(other.alt);
    }

    public AltDescriptionsBuilder addAll(final Stream<? extends AbstractDescription<?>> stream) {
        return addAll(stream.collect(toImmutableSet()));
    }

    public AltDescriptionsBuilder addAll(
            final ImmutableCollection.Builder<? extends AbstractDescription<?>> builder) {
        return addAll(builder.build());
    }

    public AltDescriptionsBuilder addAll(
            final Iterable<? extends AbstractDescription<?>> altDescriptions) {
        alt.addAll(altDescriptions);
        return this;
    }

    public AltDescriptionsBuilder add(final AbstractDescription<?>... altDescriptions) {
        alt.add(altDescriptions);
        return this;
    }

    public AltDescriptionsBuilder komma() {
        return map(AbstractDescription::komma);
    }

    public AltDescriptionsBuilder undWartest() {
        return map(AbstractDescription::undWartest);
    }

    public AltDescriptionsBuilder dann() {
        return map(AbstractDescription::dann);
    }

    public AltDescriptionsBuilder beendet(final StructuralElement structuralElement) {
        return map(d -> d.beendet(structuralElement));
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     *
     * @param substantivischePhrase Substantivische Phrase in der dritten Person
     */
    public AltDescriptionsBuilder phorikKandidat(
            final SubstantivischePhrase substantivischePhrase,
            final IBezugsobjekt bezugsobjekt) {
        return map(d -> d.phorikKandidat(substantivischePhrase, bezugsobjekt));
    }

    /**
     * Erzeugt einen {@link PhorikKandidat}en. Wir unterstützen nur
     * Phorik-Kandidaten in der dritten Person!
     */
    public AltDescriptionsBuilder phorikKandidat(
            final NumerusGenus numerusGenus,
            final IBezugsobjekt bezugsobjekt) {
        return map(d -> d.phorikKandidat(numerusGenus, bezugsobjekt));
    }

    public AltDescriptionsBuilder map(final UnaryOperator<AbstractDescription<?>> op) {
        if (this.op == null) {
            this.op = op;
        } else {
            this.op = d -> op.apply(this.op.apply(d));
        }

        return this;
    }

    public AltTimedDescriptionsBuilder timed(final AvTimeSpan timeElapsed) {
        return new AltTimedDescriptionsBuilder(this, timeElapsed);
    }

    public ImmutableSet<AbstractDescription<?>> build() {
        if (op == null) {
            return alt.build();
        }

        final ImmutableSet.Builder<AbstractDescription<?>> res =
                builder();

        for (final AbstractDescription<?> desc : alt.build()) {
            res.add(op.apply(desc));
        }

        return res.build();
    }
}
