package de.nb.aventiure2.data.narration;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractFlexibleDescription;
import de.nb.aventiure2.german.description.SimpleDuDescription;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.description.TextDescription;

/**
 * Builds {@link TextDescription}s from {@link AbstractDescription}s.
 */
class TextDescriptionBuilder {
    /**
     * Builds alternative narration additions from these alternative
     * {@link AbstractDescription}s, always based on the given initial narration.
     */
    @CheckReturnValue
    static ImmutableList<TextDescription> toTextDescriptions(
            final Collection<? extends AbstractDescription<?>> altDescriptions,
            final Narration initialNarration) {
        return altDescriptions.stream()
                .flatMap(d -> toTextDescriptions(initialNarration, d).stream())
                .filter(distinctByKey(TextDescription::getTextOhneKontext))
                .collect(toImmutableList());
    }

    /**
     * Builds alternative narration additions from an {@link AbstractDescription}, based
     * on the given initial narration.
     */
    @CheckReturnValue
    static List<TextDescription> toTextDescriptions(
            final Narration initialNarration, final AbstractDescription<?> desc) {
        if (initialNarration.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                desc.getStartsNew() == WORD) {
            if (desc instanceof SimpleDuDescription) {
                final ImmutableList.Builder<TextDescription> res = ImmutableList.builder();
                res.add(((SimpleDuDescription) desc)
                        .toTextDescriptionSatzanschlussMitAnschlusswortOderVorkomma());
                if (initialNarration.dann() && !desc.isSchonLaenger()) {
                    res.add(toTextDescriptionMitKommaDann((SimpleDuDescription) desc));
                }
                return res.build();
            } else if (desc instanceof StructuredDescription
                    && ((StructuredDescription) desc).hasSubjektDuBelebt()) {
                final ImmutableList.Builder<TextDescription> res = ImmutableList.builder();
                res.add(((StructuredDescription) desc)
                        .toTextDescriptionSatzanschlussMitAnschlusswortOderVorkomma());
                if (initialNarration.dann() && !desc.isSchonLaenger()) {
                    res.add(toTextDescriptionMitKommaDann((StructuredDescription) desc));
                }
                return res.build();
            }
        }

        if (initialNarration.dann() && !desc.isSchonLaenger()) {
            return toTextDescriptionsImDannFall(desc);
        } else {
            return toTextDescriptionsUnveraendert(desc);
        }
    }

    private static ImmutableList<TextDescription> toTextDescriptionsUnveraendert(
            final AbstractDescription<?> desc) {
        if (desc instanceof AbstractFlexibleDescription) {
            // Eine AbstractFlexibleDescription entspricht einem eigenständigen SemSatz(beginn -
            // vielleicht auch mehreren Sätzen). Daher soll hier, wo kein Anschluss
            // zum vorhergehenden Text hergestellt wurde, ein neuer SemSatz begonnen werden.
            return mapToList(desc.altTextDescriptions(), d -> d.beginntZumindest(SENTENCE));
        }
        // else: Ansonsten könnte die AbstractDescription auch einfach ein paar Wörter sein,
        // die Vorgabe WORD soll dann erhalten bleiben

        return desc.altTextDescriptions();
    }

    @NonNull
    @CheckReturnValue
    private static TextDescription toTextDescriptionMitKommaDann(
            final AbstractFlexibleDescription<?> desc) {
        checkArgument(desc.getStartsNew() == WORD,
                "Satzanschluss unmöglich für %s", desc);

        return desc.toTextDescriptionMitVorfeld("dann").mitPraefix(", ").dann(false);
    }

    @NonNull
    @CheckReturnValue
    private static List<TextDescription> toTextDescriptionsImDannFall(
            final AbstractDescription<?> desc) {
        final TextDescription res =
                desc.toTextDescriptionMitKonjunktionaladverbWennNoetig("dann");
        if (desc instanceof AbstractFlexibleDescription) {
            // Eine AbstractFlexibleDescription entspricht einem eigenständigen SemSatz(beginn -
            // vielleicht auch mehreren Sätzen). Daher soll hier, wo kein Anschluss
            // zum vorhergehenden Text hergestellt wurde, ein neuer SemSatz begonnen werden.
            res.beginntZumindest(SENTENCE);
        }
        // else: Ansonsten könnte die AbstractDescription auch einfach ein paar Wörter sein,
        // die Vorgabe WORD soll dann erhalten bleiben

        if (res.getTextOhneKontext().startsWith("Dann")) {
            res.dann(false);
        }

        return ImmutableList.of(res);
    }


    static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}