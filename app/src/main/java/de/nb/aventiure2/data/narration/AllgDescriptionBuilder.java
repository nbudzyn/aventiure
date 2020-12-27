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
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractDuDescription;
import de.nb.aventiure2.german.description.AllgDescription;
import de.nb.aventiure2.german.description.DescriptionParams;

import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.StructuralElement.max;

/**
 * Builds {@link AllgDescription}s from {@link AbstractDescription}s.
 */
class AllgDescriptionBuilder {
    /**
     * Builds alternative narration additions from these alternative
     * {@link AbstractDescription}s, always based on the given initial narration.
     */
    @CheckReturnValue
    static List<AllgDescription> toAllgDescriptions(
            final Collection<? extends AbstractDescription<?>> altDescriptions,
            final Narration initialNarration) {
        return altDescriptions.stream()
                .flatMap(d -> toAllgDescriptions(d, initialNarration).stream())
                .filter(distinctByKey(AllgDescription::getDescriptionHauptsatz))
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Builds alternative narration additions from an {@link AbstractDescription}, based
     * on the given initial narration.
     */
    @CheckReturnValue
    static List<AllgDescription> toAllgDescriptions(
            final AbstractDescription<?> desc,
            final Narration initialNarration) {
        if (initialNarration.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                desc.getStartsNew() == WORD &&
                desc instanceof AbstractDuDescription) {
            return toAllgDescriptionsDuSatzanschlussMitUnd(
                    (AbstractDuDescription<?, ?>) desc);
        } else if (initialNarration.dann()) {
            return toAllgDescriptionsImDannFall(desc);
        } else {
            return toDefaultAllgDescriptions(desc);
        }
    }

    @NonNull
    @CheckReturnValue
    private static List<AllgDescription> toAllgDescriptionsDuSatzanschlussMitUnd(
            final AbstractDuDescription<?, ?> duDesc) {
        final DescriptionParams params = duDesc.copyParams();
        params.undWartest(false);

        return ImmutableList.of(
                new AllgDescription(
                        params,
                        "und " +
                                duDesc.getDescriptionSatzanschlussOhneSubjekt()));
    }

    @NonNull
    @CheckReturnValue
    private static List<AllgDescription> toAllgDescriptionsImDannFall(
            final AbstractDescription<?> desc) {
        final String satzEvtlMitDann =
                desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("dann");
        final DescriptionParams params = desc.copyParams();
        params.setStartsNew(startsNewAtLeastSentenceForDuDescription(desc));
        params.dann(desc.isDann() && !satzEvtlMitDann.startsWith("Dann"));

        return ImmutableList.of(
                new AllgDescription(
                        params,
                        satzEvtlMitDann));
    }

    @CheckReturnValue
    private static List<AllgDescription> toDefaultAllgDescriptions(
            final AbstractDescription<?> desc) {
        final ImmutableList.Builder<AllgDescription> alternatives = ImmutableList.builder();

        final AllgDescription standard = toHauptsatzAllgDescription(
                startsNewAtLeastSentenceForDuDescription(desc), desc);
        alternatives.add(standard);

        if (desc instanceof AbstractDuDescription) {
            final AllgDescription speziellesVorfeld =
                    toHauptsatzMitSpeziellemVorfeldAllgDescription(
                            startsNewAtLeastSentenceForDuDescription(desc),
                            (AbstractDuDescription<?, ?>) desc);
            if (!speziellesVorfeld.getDescriptionHauptsatz().equals(
                    standard.getDescriptionHauptsatz())) {
                alternatives.add(speziellesVorfeld);
            }
        }

        return alternatives.build();
    }

    @CheckReturnValue
    private static AllgDescription toHauptsatzAllgDescription(
            final StructuralElement startsNew,
            @NonNull final AbstractDescription<?> desc) {
        final DescriptionParams params = desc.copyParams();
        params.setStartsNew(startsNew);

        return new AllgDescription(
                params, desc.getDescriptionHauptsatz());
    }

    @CheckReturnValue
    private static AllgDescription toHauptsatzMitSpeziellemVorfeldAllgDescription(
            final StructuralElement startsNew,
            @NonNull final AbstractDuDescription<?, ?> desc) {
        final DescriptionParams params = desc.copyParams();
        params.setStartsNew(startsNew);

        return new AllgDescription(
                params, desc.getDescriptionHauptsatzMitSpeziellemVorfeld());
    }

    private static StructuralElement startsNewAtLeastSentenceForDuDescription(
            final AbstractDescription<?> desc) {
        return (desc instanceof AbstractDuDescription) ?
                // Bei einer AbstractDuDescription ist der Hauptsatz ein echter
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