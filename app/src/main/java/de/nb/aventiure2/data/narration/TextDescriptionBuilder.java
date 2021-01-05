package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractFlexibleDescription;
import de.nb.aventiure2.german.description.DescriptionParams;
import de.nb.aventiure2.german.description.SimpleDuDescription;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.description.TextDescription;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.StructuralElement.max;

/**
 * Builds {@link TextDescription}s from {@link AbstractDescription}s.
 */
class TextDescriptionBuilder {
    /**
     * Builds alternative narration additions from these alternative
     * {@link AbstractDescription}s, always based on the given initial narration.
     */
    @CheckReturnValue
    static List<TextDescription> toAllgDescriptions(
            final Collection<? extends AbstractDescription<?>> altDescriptions,
            final Narration initialNarration) {
        return altDescriptions.stream()
                .flatMap(d -> toAllgDescriptions(initialNarration, d).stream())
                .filter(distinctByKey(TextDescription::getText))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Builds alternative narration additions from an {@link AbstractDescription}, based
     * on the given initial narration.
     */
    @CheckReturnValue
    static List<TextDescription> toAllgDescriptions(
            final Narration initialNarration, final AbstractDescription<?> desc) {
        if (initialNarration.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                desc.getStartsNew() == WORD) {
            if (desc instanceof SimpleDuDescription) {
                final ImmutableList.Builder<TextDescription> res = ImmutableList.builder();
                res.add(toAllgDescriptionSatzanschlussMitUnd((SimpleDuDescription) desc));
                if (initialNarration.dann()) {
                    res.add(toAllgDescriptionMitKommaDann((SimpleDuDescription) desc));
                }
                return res.build();
            } else if (desc instanceof StructuredDescription
                    && ((StructuredDescription) desc).hasSubjektDu()) {
                final ImmutableList.Builder<TextDescription> res = ImmutableList.builder();
                res.add(toAllgDescriptionSatzanschlussMitUnd((StructuredDescription) desc));
                if (initialNarration.dann()) {
                    res.add(toAllgDescriptionMitKommaDann((StructuredDescription) desc));
                }
                return res.build();
            }
        }

        if (initialNarration.dann()) {
            return toAllgDescriptionsImDannFall(desc);
        } else {
            return toDefaultAllgDescriptions(desc);
        }
    }

    @NonNull
    @CheckReturnValue
    private static TextDescription toAllgDescriptionSatzanschlussMitUnd(
            final SimpleDuDescription duDesc) {
        checkArgument(duDesc.getStartsNew() == WORD,
                "Satzanschluss unmöglich für " + duDesc.getStartsNew());

        final DescriptionParams params = duDesc.copyParams();
        params.undWartest(false);

        return duDesc.toTextDescriptionSatzanschlussOhneSubjekt()
                .mitPraefix("und ");
    }

    @NonNull
    @CheckReturnValue
    private static TextDescription toAllgDescriptionSatzanschlussMitUnd(
            final StructuredDescription desc) {
        final Wortfolge satzanschlussMitUnd =
                Wortfolge.joinToWortfolge(desc.getSatz()
                        .mitAnschlusswort("und")
                        .getSatzanschlussOhneSubjekt());

        return desc.toTextDescriptionKeepParams(satzanschlussMitUnd)
                // Noch nicht einmal bei P2 SG soll ein erneuter und-Anschluss erfolgen!
                .undWartest(false);
    }

    @NonNull
    @CheckReturnValue
    private static TextDescription toAllgDescriptionMitKommaDann(
            final AbstractFlexibleDescription<?> desc) {
        checkArgument(desc.getStartsNew() == WORD,
                "Satzanschluss unmöglich für " + desc);

        return desc.toTextDescriptionMitVorfeld("dann")
                .dann(false)
                .mitPraefix(", ");
    }

    @NonNull
    @CheckReturnValue
    private static List<TextDescription> toAllgDescriptionsImDannFall(
            final AbstractDescription<?> desc) {
        final TextDescription res =
                desc.toTextDescriptionMitKonjunktionaladverbWennNoetig("dann");
        if (desc instanceof AbstractFlexibleDescription) {
            // Bei einer AbstractFlexibleDescription ist der Hauptsatz ein echter
            // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
            res.beginntZumindestSentence();
        }
        // else: Ansonsten könnte der "Hauptsatz" auch einfach ein paar Wörter sein,
        // die Vorgabe WORD soll dann erhalten bleiben

        if (res.getText().startsWith("Dann")) {
            res.dann(false);
        }

        return ImmutableList.of(res);
    }


    @CheckReturnValue
    private static List<TextDescription> toDefaultAllgDescriptions(
            final AbstractDescription<?> desc) {
        if (desc instanceof AbstractFlexibleDescription) {
            return ((AbstractFlexibleDescription) desc).altTextDescriptions();
        }

        return ImmutableList.of((TextDescription) desc);
    }

    private static StructuralElement startsNewAtLeastSentenceForFlexbibleDescription(
            final AbstractDescription<?> desc) {
        return (desc instanceof AbstractFlexibleDescription) ?
                // Bei einer AbstractFlexibleDescription ist der Hauptsatz ein echter
                // Hauptsatz. Daher muss ein neuer Satz begonnen werden.
                max(desc.getStartsNew(), SENTENCE) :
                // Ansonsten könnte der "Hauptsatz" auch einfach ein paar Wörter sein,
                // die Vorgabe WORD soll dann erhalten bleiben
                desc.getStartsNew();
    }


    private static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}