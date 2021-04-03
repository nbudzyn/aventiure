package de.nb.aventiure2.data.narration;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.EnumSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractFlexibleDescription;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.description.TextDescription;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
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
                "Satzanschluss unmöglich für %s", first);

        final TextDescription secondDescriptionSatzanschlussOhneSubjekt =
                second.toTextDescriptionSatzanschlussOhneSubjekt();

        return ImmutableList.of(
                secondDescriptionSatzanschlussOhneSubjekt.mitPraefix(
                        joinToKonstituentenfolge(
                                ",",
                                first.toTextDescriptionSatzanschlussOhneSubjekt()
                                        .toSingleKonstituente(),
                                second.vorangestelltenSatzanschlussMitUndVermeiden() ?
                                        "," : "und"))
                        .undWartest(false));
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
            final Konstituente descriptionPartizipIIPhrase =
                    first.getDescriptionPartizipIIPhrase(P2, SG);
            if (!descriptionPartizipIIPhrase.vordoppelpunktNoetig() &&
                    EnumSet.of(WORD, SENTENCE)
                            .contains(descriptionPartizipIIPhrase.getEndsThis())) {
                final String vorfeld =
                        // "unten angekommen"
                        descriptionPartizipIIPhrase.toTextOhneKontext() +
                                (descriptionPartizipIIPhrase.kommaStehtAus() ? ", " : "");
                // Einen Phorik-Kandidat aus first übernehmen wir nicht - das Vorfeld ist
                // schließlich maximal weit vom Satzende entfernt.

                res.add(
                        // "Unten angekommen bist du ziemlich erschäpft"
                        second.toTextDescriptionMitVorfeld(vorfeld)
                                .beginntZumindest(
                                        StructuralElement.max(SENTENCE, first.getStartsNew())));
            }
        }

        return res.build();
    }
}
