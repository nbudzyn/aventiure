package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.Modalpartikel;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.german.base.GermanUtil.joinToString;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.max;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalize;

/**
 * Statische Methoden, die {@link AbstractDescription}s umformulieren.
 */
public class DescriptionUmformulierer {
    private DescriptionUmformulierer() {
    }

    @CheckReturnValue
    public static ImmutableCollection<TimedDescription<?>> drueckeAusTimed(
            final Kohaerenzrelation kohaerenzrelation,
            final TimedDescription<?>... desc) {
        return drueckeAusTimed(kohaerenzrelation,
                Arrays.asList(desc));
    }

    @CheckReturnValue
    public static ImmutableCollection<TimedDescription<?>> drueckeAusTimed(
            final Kohaerenzrelation kohaerenzrelation,
            final Collection<? extends TimedDescription<?>> descriptions) {
        return descriptions.stream()
                .flatMap(d ->
                        drueckeAus(kohaerenzrelation, d.getDescription()).stream()
                                .map(r -> new TimedDescription<>(r, d.getTimeElapsed(),
                                        d.getCounterIdIncrementedIfTextIsNarrated()))
                )
                .collect(ImmutableList.toImmutableList());
    }

    @CheckReturnValue
    public static ImmutableCollection<AbstractDescription<?>> drueckeAus(
            final Kohaerenzrelation kohaerenzrelation,
            final AbstractDescription<?>... desc) {
        return drueckeAus(kohaerenzrelation,
                Arrays.asList(desc));
    }

    @CheckReturnValue
    public static ImmutableCollection<AbstractDescription<?>> drueckeAus(
            final Kohaerenzrelation kohaerenzrelation,
            final Collection<? extends AbstractDescription<?>> descriptions) {
        return descriptions.stream()
                .flatMap(d -> drueckeAus(kohaerenzrelation, d).stream())
                .collect(ImmutableList.toImmutableList());
    }

    @CheckReturnValue
    public static ImmutableCollection<AbstractDescription<?>> drueckeAus(
            final Kohaerenzrelation kohaerenzrelation,
            final AbstractDescription<?> desc) {
        switch (kohaerenzrelation) {
            case DISKONTINUITAET:
                return drueckeDiskontinuitaetAus(desc);
            case WIEDERHOLUNG:
                return drueckeWiederholungAus(desc);
            case FORTSETZUNG:
                return drueckeFortsetzungAus(desc);
            case VERSTEHT_SICH_VON_SELBST:
                return ImmutableList.of(desc);
            default:
                throw new IllegalStateException("Unexpected Kohaerenzrelation: " +
                        kohaerenzrelation);
        }
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> drueckeDiskontinuitaetAus(
            final AbstractDescription<?> desc) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (desc instanceof AbstractFlexibleDescription) {
            final AbstractFlexibleDescription<?> fDesc = (AbstractFlexibleDescription<?>) desc;

            if (fDesc.hasSubjektDu()) {
                alt.add(duMitPraefixUndSatzanschluss(
                        "besinnst", "dich aber",
                        fDesc));
            }

            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("noch einmal", fDesc));

            if (fDesc instanceof StructuredDescription) {
                final StructuredDescription sDesc = (StructuredDescription) fDesc;

                alt.add(mitAdvAngabe(sDesc,
                        new AdverbialeAngabeSkopusVerbAllg("noch einmal")));

                alt.add(mitAdvAngabe(sDesc,
                        new AdverbialeAngabeSkopusVerbAllg("erneut")));

                alt.add(mitAdvAngabe(sDesc,
                        new AdverbialeAngabeSkopusVerbAllg("sogleich wieder")));

                final Wortfolge duNimmstDieKugelBesserDoch =
                        Wortfolge.joinToWortfolge(
                                sDesc.getSatz()
                                        .mitModalpartikeln(
                                                new Modalpartikel("besser"),
                                                new Modalpartikel("doch"))
                                        .getVerbzweitsatzStandard());
                alt.add(neuerSatz(
                        max(fDesc.getStartsNew(), SENTENCE),
                        Wortfolge.joinToWortfolge(
                                "Ach nein,",
                                // du nimmst die Kugel besser doch
                                duNimmstDieKugelBesserDoch))
                        .undWartest(fDesc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                        .dann(fDesc.isDann())
                        .phorikKandidat(fDesc.getPhorikKandidat()));
            }
        } else {
            alt.addAll(mitPraefixCap("Aber dir kommt ein Gedanke:", desc));
            alt.addAll(mitPraefixCap("Dir kommt ein Gedanke:", desc));
        }

        return alt.build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> drueckeWiederholungAus(
            final AbstractDescription<?> desc) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (!(desc instanceof AbstractFlexibleDescription)) {
            alt.addAll(duMitPraefixCapitalize("gibst", "aber nicht auf:",
                    desc));
            alt.addAll(duMitPraefixCapitalize("versuchst", "es noch einmal:",
                    desc));
            alt.addAll(duMitPraefixCapitalize("lässt", "dich nicht entmutigen.",
                    desc));
        }

        if (desc instanceof AbstractFlexibleDescription) {
            final AbstractFlexibleDescription<?> fDesc = (AbstractFlexibleDescription<?>) desc;

            if (fDesc.hasSubjektDu()) {
                alt.add(duMitPraefixUndSatzanschluss(
                        "gibst", "nicht auf",
                        fDesc));

                alt.add(duMitPraefixUndSatzanschluss(
                        "gibst aber", "nicht auf",
                        fDesc));

                alt.add(duMitPraefixUndSatzanschluss(
                        "versuchst", "es noch einmal",
                        fDesc));

                alt.add(duMitPraefixUndSatzanschluss(
                        "versuchst", "dich aber erneut", fDesc));
            }

            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("noch einmal", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("noch einmal aber", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("erneut", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("von neuem", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("ein weiteres Mal", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("nochmals", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("wieder", fDesc));

            if (desc instanceof StructuredDescription) {
                final StructuredDescription sDesc = (StructuredDescription) fDesc;

                alt.add(mitAdvAngabe(sDesc,
                        new AdverbialeAngabeSkopusSatz("noch einmal")));

                alt.add(mitAdvAngabe(sDesc,
                        new AdverbialeAngabeSkopusSatz("erneut")));

                alt.add(mitAdvAngabe(sDesc,
                        new AdverbialeAngabeSkopusSatz("von neuem")));

                alt.add(mitAdvAngabe(sDesc,
                        new AdverbialeAngabeSkopusSatz("ein weiteres Mal")));

                alt.add(mitAdvAngabe(sDesc,
                        new AdverbialeAngabeSkopusSatz("nochmals")));

                alt.add(mitAdvAngabe(sDesc,
                        new AdverbialeAngabeSkopusSatz("wieder")));
            }
        }

        return alt.build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> drueckeFortsetzungAus(
            final AbstractDescription<?> desc) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (!(desc instanceof AbstractFlexibleDescription<?>)) {
            alt.addAll(duMitPraefixCapitalize("gibst", "aber nicht auf:",
                    desc));
            alt.addAll(duMitPraefixCapitalize("versuchst", "es weiter:",
                    desc));
            alt.addAll(duMitPraefixCapitalize("versuchst", "es noch weiter:",
                    desc));
            alt.addAll(duMitPraefixCapitalize("versuchst", "es weiterhin:",
                    desc));
            alt.addAll(duMitPraefixCapitalize("lässt", "dich nicht entmutigen.",
                    desc));
        }

        if (desc instanceof AbstractFlexibleDescription<?>) {
            final AbstractFlexibleDescription<?> fDesc = (AbstractFlexibleDescription<?>) desc;

            if (fDesc.hasSubjektDu()) {
                alt.add(duMitPraefixUndSatzanschluss(
                        "gibst", "nicht auf",
                        fDesc));

                alt.add(duMitPraefixUndSatzanschluss(
                        "gibst aber", "nicht auf",
                        fDesc));

                alt.add(duMitPraefixUndSatzanschluss(
                        "versuchst", "es weiter",
                        fDesc));

                alt.add(duMitPraefixUndSatzanschluss(
                        "versuchst", "es noch weiter",
                        fDesc));

                alt.add(duMitPraefixUndSatzanschluss(
                        "versuchst", "es weiterhin",
                        fDesc));

                alt.add(duMitPraefixUndSatzanschluss(
                        "versuchst", "es unverdrossen weiter",
                        "unverdrossen",
                        fDesc));
            }

            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("Immer noch", fDesc));

            if (desc instanceof StructuredDescription) {
                final StructuredDescription sDesc = (StructuredDescription) fDesc;

                alt.add(mitAdvAngabe(sDesc, new AdverbialeAngabeSkopusSatz("immer noch")));
            }
        }

        return alt.build();
    }

    @CheckReturnValue
    private static Collection<TextDescription> mitPraefixCap(final String praefix,
                                                             final AbstractDescription<?> desc) {
        return desc.altTextDescriptions().stream()
                .map(d -> d.mitPraefixCapitalize(praefix + " ").beginntZumindestSentence())
                .collect(Collectors.toSet());
    }

    private static Collection<AbstractFlexibleDescription<?>> duMitPraefixCapitalize(
            final String praefixVerb,
            final String praefixRemainder,
            final AbstractDescription<?> desc) {
        return desc.altTextDescriptions().stream()
                .map(d -> du(
                        max(d.getStartsNew(), SENTENCE),
                        praefixVerb,
                        capitalize(joinToString(praefixRemainder, d.toWortfolge())))
                        .komma(d.isKommaStehtAus())
                        .phorikKandidat(d.getPhorikKandidat())
                        .beendet(d.getEndsThis()))
                .collect(Collectors.toSet());
    }

    @CheckReturnValue
    private static AbstractFlexibleDescription<?> duMitPraefixUndSatzanschluss(
            final String praefixVerb,
            final String praefixRemainder,
            final AbstractFlexibleDescription<?> desc) {
        return duMitPraefixUndSatzanschluss(praefixVerb, praefixRemainder, null,
                desc);
    }

    @CheckReturnValue
    private static AbstractFlexibleDescription<?> duMitPraefixUndSatzanschluss(
            final String praefixVerb,
            final String praefixRemainder,
            @Nullable final String praefixVorfeldSatzglied,
            final AbstractFlexibleDescription<?> desc) {
        final TextDescription descriptionSatzanschlussOhneSubjekt =
                desc.toTextDescriptionSatzanschlussOhneSubjekt();
        return du(max(desc.getStartsNew(), PARAGRAPH),
                praefixVerb,
                joinToString(
                        praefixRemainder,
                        "und",
                        descriptionSatzanschlussOhneSubjekt.toWortfolge()),
                praefixVorfeldSatzglied)
                .dann(desc.isDann())
                .komma(descriptionSatzanschlussOhneSubjekt.isKommaStehtAus())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }

    @CheckReturnValue
    private static TextDescription toTextDescriptionMindestensParagraphMitVorfeld(
            final String vorfeld,
            final AbstractFlexibleDescription<?> desc) {
        return desc.toTextDescriptionMitVorfeld(vorfeld).beginntZumindestParagraph();
    }

    private static AbstractDescription<?> mitAdvAngabe(
            final StructuredDescription desc,
            final AdverbialeAngabeSkopusSatz advAngabe) {
        return DescriptionBuilder.satz(
                max(desc.getStartsNew(), PARAGRAPH),
                desc.getSatz().mitAdverbialerAngabe(
                        // "Erneut gibst du der Frau die Kugel"
                        // "Du gibst erneut der Frau die Kugel"
                        advAngabe))
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }

    @CheckReturnValue
    private static AbstractDescription<?> mitAdvAngabe(
            final StructuredDescription desc,
            final AdverbialeAngabeSkopusVerbAllg advAngabe) {
        return DescriptionBuilder.satz(
                max(desc.getStartsNew(), PARAGRAPH),
                desc.getSatz().mitAdverbialerAngabe(
                        // "gibst der Frau die Kugel noch einmal"
                        advAngabe))
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }
}
