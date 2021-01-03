package de.nb.aventiure2.data.narration;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractDuDescription;
import de.nb.aventiure2.german.description.AllgDescription;
import de.nb.aventiure2.german.description.DescriptionParams;
import de.nb.aventiure2.german.description.StructuredDuDescription;

import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.StructuralElement.max;

class DescriptionCombiner {
    private DescriptionCombiner() {
    }

    @CheckReturnValue
    static Collection<AllgDescription> combine(
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

        if (first instanceof StructuredDuDescription &&
                second instanceof AbstractDuDescription<?, ?>) {
            // Evtl. etwas wie "Unten angekommen bist du ziemlich erschöpft"
            res.addAll(combinePraedikatDuDescUndDuDesc(
                    (StructuredDuDescription) first,
                    (AbstractDuDescription<?, ?>) second
            ));
        }

        return res.build();
    }

    /**
     * Erzeugt einen doppelten Satzanschluss - einmal mit Komma, danach mit und:
     * "Du kommst, siehst und siegst"
     */
    @CheckReturnValue
    private static Iterable<AllgDescription> toDuSatzanschlussMitKommaUndUnd(
            final AbstractDuDescription<?, ?> first,
            final AbstractDuDescription<?, ?> second) {
        final DescriptionParams params = second.copyParams();
        params.undWartest(false);

        return ImmutableList.of(
                new AllgDescription(params,
                        ", " +
                                first.getDescriptionSatzanschlussOhneSubjekt() +
                                (first.isWoertlicheRedeNochOffen() ? "“" : "") +
                                (first.isKommaStehtAus() ? "," : "") +
                                " und " +
                                second.getDescriptionSatzanschlussOhneSubjekt()));
    }

    @CheckReturnValue
    private static ImmutableList<AllgDescription> combinePraedikatDuDescUndDuDesc(
            final StructuredDuDescription first, final AbstractDuDescription<?, ?> second) {
        final ImmutableList.Builder<AllgDescription> res = ImmutableList.builder();

        // Bei Partikelverben mit sein-Perfekt ohne Akkusativobjekt,
        //  bei denen das Subjekt gleich ist ("du") und bei denen mindestens ein weiteres
        //  Satzglied dabei ist (z.B. eine adverbiale Bestimmung: "unten") können zwei Sätze
        //  in dieser Form zusammengezogen werden:
        //  "Du kommst unten an" + "Du bist ziemlich erschöpft" ->
        //  "Unten angekommen bist du ziemlich erschöpft"

        if (first.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() &&
                first.getPraedikat().bildetPerfektMitSein() &&
                !first.getPraedikat().hatAkkusativobjekt() &&
                first.getPraedikat().isBezugAufNachzustandDesAktantenGegeben() &&
                first.getPraedikat().umfasstSatzglieder() &&
                second.getStartsNew() == WORD) {
            final DescriptionParams params = second.copyParams();
            params.setStartsNew(max(first.getStartsNew(), SENTENCE));

            final String vorfeld =
                    // "unten angekommen"
                    first.getDescriptionPartizipIIPhrase(P2, SG) +
                            (first.isKommaStehtAus() ? ", " : "");

            final Wortfolge hauptsatzMitVorfeld =
                    second.getDescriptionHauptsatzMitVorfeld(vorfeld);

            params.komma(hauptsatzMitVorfeld.kommmaStehtAus());

            res.add(new AllgDescription(params,
                    // "Unten angekommen bist du ziemlich erschäpft"
                    hauptsatzMitVorfeld.getString()));
        }

        return res.build();
    }
}
