package de.nb.aventiure2.data.narration;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractDuDescription;
import de.nb.aventiure2.german.description.AllgDescription;
import de.nb.aventiure2.german.description.DescriptionParams;

import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;

class DescriptionCombiner {
    private DescriptionCombiner() {
    }

    public static Collection<AllgDescription> combine(
            final AbstractDescription<?> first,
            final AbstractDescription<?> second,
            final Narration initialNarration) {
        final ImmutableList.Builder<AllgDescription>
                res = ImmutableList.builder();

        if (initialNarration.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                first.getStartsNew() == WORD &&
                first instanceof AbstractDuDescription &&
                first.isAllowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                second.getStartsNew() == WORD &&
                second instanceof AbstractDuDescription) {
            //  "Du kommst, siehst und siegst"
            res.addAll(toDuSatzanschlussMitKommaUndUnd(
                    (AbstractDuDescription<?, ?>) first,
                    (AbstractDuDescription<?, ?>) second
            ));
        }

        // FIXME "Unten angekommen..."

        // STORY "Als du unten angekommen bist..."

        return res.build();
    }

    /**
     * Erzeugt einen doppelten Satzanschluss - einmal mit Komma, danach mit und:
     * "Du kommst, siehst und siegst"
     */
    private static Iterable<AllgDescription>
    toDuSatzanschlussMitKommaUndUnd(
            final AbstractDuDescription<?, ?> first,
            final AbstractDuDescription<?, ?> second) {
        final DescriptionParams params = second.copyParams();
        params.undWartest(false);

        return ImmutableList.of(
                satzanschluss(", " +
                        first.getDescriptionSatzanschlussOhneSubjekt() +
                        (first.isKommaStehtAus() ? ", " : "") +
                        " und " +
                        second.getDescriptionSatzanschlussOhneSubjekt()));
    }
}
