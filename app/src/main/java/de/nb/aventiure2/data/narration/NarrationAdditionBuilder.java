package de.nb.aventiure2.data.narration;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractDuDescription;
import de.nb.aventiure2.german.description.DescriptionParams;

import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.StructuralElement.max;

/**
 * Builds {@link NarrationAddition}s from {@link AbstractDescription}s.
 */
class NarrationAdditionBuilder {
    static List<NarrationAddition> toNarrationAdditions(
            final AbstractDescription<?> desc,
            final Narration initialNarration) {
        // STORY Statt "und gehst nach Norden": ", bevor du nach Norden gehst"?
        //  (Allerdings sollte der Nebensatz dann eher eine Nebensache enthalten...)

        if (initialNarration.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                desc.getStartsNew() == WORD &&
                desc instanceof AbstractDuDescription) {
            return toNarrationAdditionsDuSatzanschlussMitUnd(desc);
        } else if (initialNarration.dann()) {
            return toNarrationAdditionsImDannFall(desc);
        } else {
            return toDefaultNarrationAdditions(desc);
        }
    }

    @NonNull
    private static List<NarrationAddition> toNarrationAdditionsDuSatzanschlussMitUnd(
            final AbstractDescription<?> desc) {
        final AbstractDuDescription<?, ?> duDesc = (AbstractDuDescription<?, ?>) desc;

        final DescriptionParams params = desc.copyParams();
        params.undWartest(false);

        return ImmutableList.of(
                new NarrationAddition(
                        params,
                        "und " +
                                duDesc.getDescriptionSatzanschlussOhneSubjekt()));
    }

    @NonNull
    private static List<NarrationAddition> toNarrationAdditionsImDannFall(
            final AbstractDescription<?> desc) {
        final String satzEvtlMitDann =
                desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("dann");
        final DescriptionParams params = desc.copyParams();
        params.setStartsNew(startsNewAtLeastSentenceForDuDescription(desc));
        params.dann(desc.isDann() && !satzEvtlMitDann.startsWith("Dann"));

        return ImmutableList.of(
                new NarrationAddition(
                        params,
                        satzEvtlMitDann));
    }

    private static List<NarrationAddition> toDefaultNarrationAdditions(
            final AbstractDescription<?> desc) {
        final ImmutableList.Builder<NarrationAddition> alternatives = ImmutableList.builder();

        final NarrationAddition standard = toHauptsatzNarrationAddition(
                startsNewAtLeastSentenceForDuDescription(desc), desc);
        alternatives.add(standard);

        if (desc instanceof AbstractDuDescription) {
            final NarrationAddition speziellesVorfeld =
                    toHauptsatzMitSpeziellemVorfeldNarrationAddition(
                            startsNewAtLeastSentenceForDuDescription(desc),
                            (AbstractDuDescription<?, ?>) desc);
            if (!speziellesVorfeld.getText().equals(standard.getText())) {
                alternatives.add(speziellesVorfeld);
            }
        }

        return alternatives.build();
    }

    static NarrationAddition toHauptsatzNarrationAddition(
            final StructuralElement startsNew,
            @NonNull final AbstractDescription<?> desc) {
        final DescriptionParams params = desc.copyParams();
        params.setStartsNew(startsNew);

        return new NarrationAddition(
                params, desc.getDescriptionHauptsatz());
    }

    static NarrationAddition toHauptsatzMitSpeziellemVorfeldNarrationAddition(
            final StructuralElement startsNew,
            @NonNull final AbstractDuDescription<?, ?> desc) {
        final DescriptionParams params = desc.copyParams();
        params.setStartsNew(startsNew);

        return new NarrationAddition(
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
}