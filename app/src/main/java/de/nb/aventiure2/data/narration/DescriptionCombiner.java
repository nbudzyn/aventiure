package de.nb.aventiure2.data.narration;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractFlexibleDescription;
import de.nb.aventiure2.german.description.DescriptionParams;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.description.TextDescription;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

class DescriptionCombiner {
    private DescriptionCombiner() {
    }

    @CheckReturnValue
    static Collection<TextDescription> combine(
            final AbstractDescription<?> first,
            final AbstractDescription<?> second,
            final Narration initialNarration) {
        final ImmutableList.Builder<TextDescription>
                res = ImmutableList.builder();

        if (initialNarration.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                first.getStartsNew() == WORD &&
                first instanceof AbstractFlexibleDescription &&
                ((AbstractFlexibleDescription<?>) first).hasSubjektDu() &&
                first.isAllowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                second.getStartsNew() == WORD &&
                second instanceof AbstractFlexibleDescription &&
                ((AbstractFlexibleDescription<?>) second).hasSubjektDu()) {
            //  "Du kommst, siehst und siegst"
            res.addAll(toDuSatzanschlussMitKommaUndUnd(
                    (AbstractFlexibleDescription<?>) first,
                    (AbstractFlexibleDescription<?>) second
            ));
        }

        if (first instanceof StructuredDescription &&
                ((StructuredDescription) first).hasSubjektDu() &&
                second instanceof AbstractFlexibleDescription &&
                ((AbstractFlexibleDescription<?>) second).hasSubjektDu()) {
            // Evtl. etwas wie "Unten angekommen bist du ziemlich erschöpft"
            res.addAll(combineStructuredDescUndFlexibleDescDu(
                    (StructuredDescription) first,
                    (AbstractFlexibleDescription<?>) second
            ));
        }

        return res.build();
    }

    /**
     * Erzeugt einen doppelten Satzanschluss - einmal mit Komma, danach mit und:
     * "Du kommst, siehst und siegst"
     */
    @CheckReturnValue
    private static Iterable<TextDescription> toDuSatzanschlussMitKommaUndUnd(
            final AbstractFlexibleDescription<?> first,
            final AbstractFlexibleDescription<?> second) {
        checkArgument(first.getStartsNew() == WORD,
                "Satzanschluss unmöglich für " + first);

        final Wortfolge secondDescriptionSatzanschlussOhneSubjekt =
                second.getDescriptionSatzanschlussOhneSubjekt();

        final DescriptionParams params = second.copyParams();
        params.undWartest(false);
        params.woertlicheRedeNochOffen(
                secondDescriptionSatzanschlussOhneSubjekt.woertlicheRedeNochOffen());
        params.komma(secondDescriptionSatzanschlussOhneSubjekt.kommaStehtAus());

        return ImmutableList.of(
                new TextDescription(params,
                        GermanUtil.joinToString(
                                ",",
                                first.getDescriptionSatzanschlussOhneSubjekt(),
                                "und",
                                secondDescriptionSatzanschlussOhneSubjekt)));
    }

    @CheckReturnValue
    private static ImmutableList<TextDescription> combineStructuredDescUndFlexibleDescDu(
            final StructuredDescription first, final AbstractFlexibleDescription<?> second) {
        final ImmutableList.Builder<TextDescription> res = ImmutableList.builder();

        // Bei Partikelverben mit sein-Perfekt ohne Akkusativobjekt,
        //  bei denen das Subjekt gleich ist ("du") und bei denen mindestens ein weiteres
        //  Satzglied dabei ist (z.B. eine adverbiale Bestimmung: "unten") können zwei Sätze
        //  in dieser Form zusammengezogen werden:
        //  "Du kommst unten an" + "Du bist ziemlich erschöpft" ->
        //  "Unten angekommen bist du ziemlich erschöpft"

        if (first.getPraedikat().kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() &&
                first.getPraedikat().bildetPerfektMitSein() &&
                !first.getPraedikat().hatAkkusativobjekt() &&
                first.getPraedikat().isBezugAufNachzustandDesAktantenGegeben() &&
                first.getPraedikat().umfasstSatzglieder() &&
                second.getStartsNew() == WORD) {
            final String vorfeld =
                    // "unten angekommen"
                    first.getDescriptionPartizipIIPhrase(P2, SG) +
                            (first.isKommaStehtAus() ? ", " : "");

            res.add(
                    // "Unten angekommen bist du ziemlich erschäpft"
                    second.toTextDescriptionMitVorfeld(vorfeld)
                            .beginntZumindestSentence());
        }

        return res.build();
    }
}
