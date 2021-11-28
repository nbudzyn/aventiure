package de.nb.aventiure2.german.description;

import static com.google.common.collect.ImmutableSet.builder;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.Arrays.asList;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToAltKonstituentenfolgen;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.DescriptionBuilder.prependObject;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.Collection;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Belebtheit;
import de.nb.aventiure2.german.base.IAlternativeKonstituentenfolgable;
import de.nb.aventiure2.german.base.IBezugsobjekt;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.PhorikKandidat;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.satz.SemSatz;

/**
 * Ein Builder für alternative {@link AbstractDescription}s.
 */
@CanIgnoreReturnValue
public class AltDescriptionsBuilder implements IAlternativeKonstituentenfolgable {
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
    public static AltDescriptionsBuilder altSaetze(final Collection<? extends SemSatz> saetze) {
        return altSaetze(WORD, saetze);
    }

    @Nonnull
    @CheckReturnValue
    public static AltDescriptionsBuilder altNeueSaetze(final SemSatz... saetze) {
        return altNeueSaetze(asList(saetze));
    }

    @Nonnull
    @CheckReturnValue
    public static AltDescriptionsBuilder altNeueSaetze(final Collection<? extends SemSatz> saetze) {
        return altSaetze(SENTENCE, saetze);
    }

    @Nonnull
    @CheckReturnValue
    private static AltDescriptionsBuilder altSaetze(
            final StructuralElement startsNew, final Collection<? extends SemSatz> saetze) {
        return alt().addAll(saetze.stream().map(s -> DescriptionBuilder.satz(startsNew, s)));
    }

    /**
     * Fügt diese Teile zu alternativen {@link AltDescriptionsBuilder}n
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

    @CheckReturnValue
    public static AltDescriptionsBuilder alt() {
        return new AltDescriptionsBuilder();
    }

    @CheckReturnValue
    private AltDescriptionsBuilder() {
    }

    public AltDescriptionsBuilder addAll(final AltDescriptionsBuilder other) {
        return addAll(other.alt);
    }

    public AltDescriptionsBuilder addAll(final Stream<?> stream) {
        return addAll(stream.collect(toImmutableSet()));
    }

    private AltDescriptionsBuilder addAll(final ImmutableCollection.Builder<?> builder) {
        return addAll(builder.build());
    }

    public AltDescriptionsBuilder addAll(final Iterable<?> others) {
        for (final Object other : others) {
            if (other instanceof AltDescriptionsBuilder) {
                alt.addAll(((AltDescriptionsBuilder) other).build());
            } else if (other instanceof AbstractDescription<?>) {
                add((AbstractDescription<?>) other);
            } else if (other instanceof SemSatz) {
                add((SemSatz) other);
            } else {
                throw new IllegalArgumentException("Unexpected addition: " + other);
            }
        }

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public AltDescriptionsBuilder addIfOtherwiseEmpty(
            final AbstractDescription<?>... altDescriptions) {
        if (isEmpty()) {
            add(altDescriptions);
        }

        return this;
    }

    public AltDescriptionsBuilder add(final SemSatz... saetze) {
        for (final SemSatz semSatz : saetze) {
            add(DescriptionBuilder.satz(semSatz));
        }
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

    public AltDescriptionsBuilder schonLaenger() {
        return map(AbstractDescription::schonLaenger);
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
            final Belebtheit belebtheit,
            final IBezugsobjekt bezugsobjekt) {
        return map(d -> d.phorikKandidat(numerusGenus, belebtheit, bezugsobjekt));
    }

    private AltDescriptionsBuilder map(final UnaryOperator<AbstractDescription<?>> op) {
        if (this.op == null) {
            this.op = op;
        } else {
            final UnaryOperator<AbstractDescription<?>> oldOp = this.op;
            // oldOp nicht inlinen! Das ändert wohl die Semantik zu einer Endlosschleife!
            this.op = d -> op.apply(oldOp.apply(d));
        }

        return this;
    }

    public AltTimedDescriptionsBuilder timed(final AvTimeSpan timeElapsed) {
        return new AltTimedDescriptionsBuilder(this, timeElapsed);
    }

    @CheckReturnValue
    private boolean isEmpty() {
        return alt.build().isEmpty();
    }

    /**
     * Stellt das Objekt als alternative Konstituentenfolgen dar. Dabei können Informationen
     * verloren gehen (vielleicht solche, wie sie in den {@link DescriptionParams} stehen)!
     */
    @Override
    public ImmutableSet<Konstituentenfolge> toAltKonstituentenfolgen() {
        return build().stream()
                .flatMap(d -> d.toAltKonstituentenfolgen().stream())
                .collect(ImmutableSet.toImmutableSet());
    }

    @CheckReturnValue
    public ImmutableSet<AbstractDescription<?>> build() {
        if (op == null) {
            return alt.build();
        }

        final ImmutableSet.Builder<AbstractDescription<?>> res = builder();

        for (final AbstractDescription<?> desc : alt.build()) {
            res.add(op.apply(desc));
        }

        return res.build();
    }
}
