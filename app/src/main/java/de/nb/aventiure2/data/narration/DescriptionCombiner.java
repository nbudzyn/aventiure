package de.nb.aventiure2.data.narration;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Belebtheit.BELEBT;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.EnumSet;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.description.AbstractDescription;
import de.nb.aventiure2.german.description.AbstractFlexibleDescription;
import de.nb.aventiure2.german.description.StructuredDescription;
import de.nb.aventiure2.german.description.TextDescription;
import de.nb.aventiure2.german.praedikat.PartizipIIPhrase;
import de.nb.aventiure2.german.praedikat.Perfektbildung;
import de.nb.aventiure2.german.praedikat.SemPraedikatOhneLeerstellen;

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

        if (initialNarration.allowsAdditionalDuSatzreihengliedOhneSubjekt()
                && first.getStartsNew() == WORD
                && first instanceof AbstractFlexibleDescription
                && ((AbstractFlexibleDescription<?>) first).hasSubjektDuBelebt()
                && first.isAllowsAdditionalDuSatzreihengliedOhneSubjekt()
                && second.getStartsNew() == WORD
                && second instanceof AbstractFlexibleDescription
                && ((AbstractFlexibleDescription<?>) second).hasSubjektDuBelebt()
                && !((AbstractFlexibleDescription<?>) first).hasAnschlusswortDasBedeutungTraegt()) {
            //  "Du kommst, siehst und siegst"
            res.addAll(toDuSatzanschlussMitKommaUndAnschlusswort(
                    (AbstractFlexibleDescription<?>) first,
                    (AbstractFlexibleDescription<?>) second
            ));
        }

        if (first instanceof StructuredDescription &&
                ((StructuredDescription) first).hasSubjektDuBelebt() &&
                second instanceof AbstractFlexibleDescription &&
                ((AbstractFlexibleDescription<?>) second).hasSubjektDuBelebt()) {
            // Evtl. etwas wie "Unten angekommen bist du ziemlich erschöpft"
            res.addAll(combineStructuredDescUndFlexibleDescDu(
                    (StructuredDescription) first,
                    (AbstractFlexibleDescription<?>) second
            ));
        }

        return res.build();
    }

    /**
     * Erzeugt einen doppelten Satzanschluss - einmal mit Komma, danach mit "und"
     * (oder einem anderen Anschlusswort): "Du kommst, siehst und siegst"
     * <p>
     * Diese Methode darf nur aufgerufen werden, wenn die <i>erste Beschreibung</i> kein
     * Anschlusswort hat oder es zumindest keine Semantik trägt (also nicht "oder", "aber" etc.).
     */
    @CheckReturnValue
    private static Iterable<TextDescription> toDuSatzanschlussMitKommaUndAnschlusswort(
            final AbstractFlexibleDescription<?> first,
            final AbstractFlexibleDescription<?> second) {
        checkArgument(first.getStartsNew() == WORD,
                "Satzanschluss unmöglich für %s", first);

        return ImmutableList.of(
                second.toTextDescriptionSatzanschlussMitAnschlusswortOderVorkomma()
                        .mitPraefix(
                                joinToKonstituentenfolge(
                                        ",",
                                        first.toTextDescriptionSatzanschlussOhneSubjektOhneAnschlusswort()))
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

        @Nullable final SemPraedikatOhneLeerstellen firstPraedikat =
                first.getPraedikatWennOhneInformationsverlustMoeglich();
        if (firstPraedikat != null
                && firstPraedikat.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden()
                && !firstPraedikat.hatAkkusativobjekt()
                && firstPraedikat.isBezugAufNachzustandDesAktantenGegeben()
                && firstPraedikat.umfasstSatzglieder()
                && second.getStartsNew() == WORD) {
            final ImmutableList<PartizipIIPhrase> partizipIIPhrasen =
                    firstPraedikat.getPartizipIIPhrasen(textContext, P2, SG, BELEBT);
            if (partizipIIPhrasen.size() == 1) { // Wenn es mehrere sind, bilden sie
                // bestimmt nicht alle das Perfekt mit "sein"!
                // Aber es ist nur eine!
                final PartizipIIPhrase partizipIIPhrase = partizipIIPhrasen.iterator().next();
                if (partizipIIPhrase.getPerfektbildung() == Perfektbildung.SEIN) {
                    // Und die Phrase bildet das Perfekt mit "sein"!
                    final Konstituente descriptionPartizipIIPhrase =
                            partizipIIPhrase.getPhrase().joinToSingleKonstituente();
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
                                                StructuralElement
                                                        .max(SENTENCE, first.getStartsNew())));
                    }
                }
            }
        }

        return res.build();
    }
}
