package de.nb.aventiure2.german.description;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableSet.builder;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToAltKonstituentenfolgen;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.DescriptionBuilder.prependObject;
import static java.util.Arrays.asList;

/**
 * Ein Builder für alternative {@link AbstractDescription}s.
 */
public class AltDescriptionsBuilder {
    private final ImmutableSet.Builder<AbstractDescription<?>> alt = builder();

    private UnaryOperator<AbstractDescription<?>> op = null;

    /**
     * Fügt diese Teile zu alternativen {@link AbstractDescription}s
     * zusammen, die jeweils einen Paragraph bilden.
     * Gibt es für mehrere Teile mehrere Alternativen, so werden
     * alle Kombinationen erzeugt.
     *
     * @return Mehrere alternative Wortfolgen. Wenn eine der Kombinationen ausschließlich
     * {@code null}-Werte enthält, wird die Collection auch den Wert
     * {@code null} enthalten.
     */
    @Nonnull
    @CheckReturnValue
    public static AltDescriptionsBuilder altParagraphs(final Object... parts) {
        return altNeueSaetze(DescriptionBuilder.prependAndAppendObjects(
                PARAGRAPH, parts, PARAGRAPH));
    }

    @Nonnull
    @CheckReturnValue
    public static AltDescriptionsBuilder altSaetze(final Collection<Satz> saetze) {
        return altSaetze(WORD, saetze);
    }

    @Nonnull
    @CheckReturnValue
    public static AltDescriptionsBuilder altNeueSaetze(final Satz... saetze) {
        return altNeueSaetze(asList(saetze));
    }


    @Nonnull
    @CheckReturnValue
    public static AltDescriptionsBuilder altNeueSaetze(final Collection<Satz> saetze) {
        return altSaetze(SENTENCE, saetze);
    }

    @Nonnull
    @CheckReturnValue
    private static AltDescriptionsBuilder altSaetze(
            final StructuralElement startsNew, final Collection<Satz> saetze) {
        return alt().addAll(saetze.stream().map(s -> DescriptionBuilder.satz(startsNew, s)));
    }

    /**
     * Fügt diese Teile zu alternativen {@link AbstractDescription}s
     * zusammen. Gibt es für mehrere Teile mehrere Alternativen, so werden
     * alle Kombinationen erzeugt.
     *
     * @return Mehrere alternative Wortfolgen. Wenn eine der Kombinationen ausschließlich
     * {@code null}-Werte enthält, wird die Collection auch den Wert
     * {@code null} enthalten.
     */
    @Nonnull
    @CheckReturnValue
    public static AltDescriptionsBuilder altNeueSaetze(final Object... parts) {
        if (!(parts[0] instanceof StructuralElement)) {
            return altNeueSaetze(prependObject(SENTENCE, parts));
        }

        return alt().addAll(joinToAltKonstituentenfolgen(parts).stream()
                .map(DescriptionBuilder::neuerSatz));
    }

    public static AltDescriptionsBuilder alt() {
        return new AltDescriptionsBuilder();
    }

    private AltDescriptionsBuilder() {
    }

    public AltDescriptionsBuilder addAllIfOtherwiseEmpty(final AltDescriptionsBuilder other) {
        return addAllIfOtherwiseEmpty(other.alt);
    }

    public AltDescriptionsBuilder addAllIfOtherwiseEmpty(final Stream<?> stream) {
        return addAllIfOtherwiseEmpty(stream.collect(toImmutableSet()));
    }

    private AltDescriptionsBuilder addAllIfOtherwiseEmpty(
            final ImmutableCollection.Builder<?> builder) {
        return addAllIfOtherwiseEmpty(builder.build());
    }

    private AltDescriptionsBuilder addAllIfOtherwiseEmpty(final Iterable<?> others) {
        if (isEmpty()) {
            addAll(others);
        }

        return this;
    }

    public AltDescriptionsBuilder addAll(final AltDescriptionsBuilder other) {
        return addAll(other.alt);
    }

    public AltDescriptionsBuilder addAll(final Stream<?> stream) {
        return addAll(stream.collect(toImmutableSet()));
    }

    public AltDescriptionsBuilder addAll(final ImmutableCollection.Builder<?> builder) {
        return addAll(builder.build());
    }

    public AltDescriptionsBuilder addAll(final Iterable<?> others) {
        for (final Object other : others) {
            if (other instanceof AbstractDescription<?>) {
                add((AbstractDescription<?>) other);
            } else if (other instanceof Satz) {
                add((Satz) other);
            } else {
                throw new IllegalArgumentException("Unexpected addition: " + other);
            }
        }

        return this;
    }

    public AltDescriptionsBuilder addIfOtherwiseEmpty(final Satz satz) {
        return addIfOtherwiseEmpty(DescriptionBuilder.satz(satz));
    }

    public AltDescriptionsBuilder addIfOtherwiseEmpty(
            final AbstractDescription<?>... altDescriptions) {
        if (isEmpty()) {
            add(altDescriptions);
        }

        return this;
    }

    public AltDescriptionsBuilder add(final Satz satz) {
        return add(DescriptionBuilder.satz(satz));
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

    private boolean isEmpty() {
        return alt.build().isEmpty();
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
