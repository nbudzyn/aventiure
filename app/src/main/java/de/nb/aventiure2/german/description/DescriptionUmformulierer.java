package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.GermanUtil;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.Modalpartikel;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.max;
import static de.nb.aventiure2.german.base.Wortfolge.uncapitalize;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

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

        if (desc instanceof AbstractDuDescription) {
            final AbstractDuDescription<?, ?> duDesc =
                    (AbstractDuDescription<?, ?>) desc;

            alt.add(duMitPraefixUndSatzanschluss(
                    "besinnst", "dich aber",
                    duDesc));

            alt.add(duMitVorfeld("noch einmal", duDesc));

            if (duDesc instanceof PraedikatDuDescription) {
                final PraedikatDuDescription pDuDesc = (PraedikatDuDescription) duDesc;

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusVerbAllg("noch einmal")));

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusVerbAllg("erneut")));

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusVerbAllg("sogleich wieder")));

                final Wortfolge duNimmstDieKugelBesserDoch =
                        Wortfolge.joinToNullWortfolge(
                                pDuDesc.getPraedikat().getDuHauptsatz(
                                        new Modalpartikel("besser"),
                                        new Modalpartikel("doch")));
                alt.add(neuerSatz(
                        max(duDesc.getStartsNew(), SENTENCE),
                        "Ach nein, " +
                                // du nimmst die Kugel besser doch
                                uncapitalize(duNimmstDieKugelBesserDoch).getString())
                        .komma(duNimmstDieKugelBesserDoch.kommmaStehtAus())
                        .undWartest(duDesc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                        .dann(duDesc.isDann())
                        .phorikKandidat(duDesc.getPhorikKandidat()));
            }
        } else {
            alt.add(mitPraefix("Aber dir kommt ein Gedanke und", desc));
            alt.add(mitPraefix("Dir kommt ein Gedanke –", desc));
        }

        return alt.build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> drueckeWiederholungAus(
            final AbstractDescription<?> desc) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (!(desc instanceof AbstractDuDescription)) {
            alt.add(duMitPraefix("gibst", "aber nicht auf:",
                    desc, false));
            alt.add(duMitPraefix("versuchst", "es noch einmal:",
                    desc, false));
            alt.add(duMitPraefix("lässt", "dich nicht entmutigen.",
                    desc, false));
        }

        if (desc instanceof AbstractDuDescription) {
            final AbstractDuDescription<?, ?> duDesc = (AbstractDuDescription<?, ?>) desc;

            alt.add(duMitPraefixUndSatzanschluss(
                    "gibst", "nicht auf",
                    duDesc));

            alt.add(duMitPraefixUndSatzanschluss(
                    "gibst aber", "nicht auf",
                    duDesc));

            alt.add(duMitPraefixUndSatzanschluss(
                    "versuchst", "es noch einmal",
                    duDesc));

            alt.add(duMitPraefixUndSatzanschluss(
                    "versuchst", "dich aber erneut", duDesc));

            alt.add(duMitVorfeld("noch einmal", duDesc));
            alt.add(duMitVorfeld("noch einmal aber", duDesc));
            alt.add(duMitVorfeld("erneut", duDesc));
            alt.add(duMitVorfeld("von neuem", duDesc));
            alt.add(duMitVorfeld("ein weiteres Mal", duDesc));
            alt.add(duMitVorfeld("nochmals", duDesc));
            alt.add(duMitVorfeld("wieder", duDesc));

            if (desc instanceof PraedikatDuDescription) {
                final PraedikatDuDescription pDuDesc = (PraedikatDuDescription) duDesc;

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusSatz("noch einmal")));

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusSatz("erneut")));

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusSatz("von neuem")));

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusSatz("ein weiteres Mal")));

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusSatz("nochmals")));

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusSatz("wieder")));
            }
        }

        return alt.build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> drueckeFortsetzungAus(
            final AbstractDescription<?> desc) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = builder();

        if (!(desc instanceof AbstractDuDescription)) {
            alt.add(duMitPraefix("gibst", "aber nicht auf:",
                    desc, false));
            alt.add(duMitPraefix("versuchst", "es weiter:",
                    desc, false));
            alt.add(duMitPraefix("versuchst", "es noch weiter:",
                    desc, false));
            alt.add(duMitPraefix("versuchst", "es weiterhin:",
                    desc, false));
            alt.add(duMitPraefix("lässt", "dich nicht entmutigen.",
                    desc, false));
        }

        if (desc instanceof AbstractDuDescription) {
            final AbstractDuDescription<?, ?> duDesc = (AbstractDuDescription<?, ?>) desc;

            alt.add(duMitPraefixUndSatzanschluss(
                    "gibst", "nicht auf",
                    duDesc));

            alt.add(duMitPraefixUndSatzanschluss(
                    "gibst aber", "nicht auf",
                    duDesc));

            alt.add(duMitPraefixUndSatzanschluss(
                    "versuchst", "es weiter",
                    duDesc));

            alt.add(duMitPraefixUndSatzanschluss(
                    "versuchst", "es noch weiter",
                    duDesc));

            alt.add(duMitPraefixUndSatzanschluss(
                    "versuchst", "es weiterhin",
                    duDesc));

            alt.add(duMitPraefixUndSatzanschluss(
                    "versuchst", "es unverdrossen weiter",
                    "unverdrossen",
                    duDesc));

            alt.add(duMitVorfeld("Immer noch", duDesc));

            if (desc instanceof PraedikatDuDescription) {
                final PraedikatDuDescription pDuDesc = (PraedikatDuDescription) duDesc;

                alt.add(mitAdvAngabe(pDuDesc,
                        new AdverbialeAngabeSkopusSatz("immer noch")));
            }
        }

        return alt.build();
    }

    @CheckReturnValue
    private static AllgDescription mitPraefix(final String praefix,
                                              final AbstractDescription<?> desc) {
        return mitPraefix(praefix, desc, true);
    }

    @CheckReturnValue
    private static AllgDescription mitPraefix(final String praefix,
                                              final AbstractDescription<?> desc,
                                              final boolean uncapitalize) {
        String hauptsatz = desc.getDescriptionHauptsatz();
        if (uncapitalize) {
            hauptsatz = GermanUtil.uncapitalize(hauptsatz);
        }

        return neuerSatz(
                max(desc.getStartsNew(), SENTENCE),
                praefix
                        + " "
                        + hauptsatz)
                .komma(desc.isKommaStehtAus())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }

    private static AbstractDuDescription<?, ?> duMitPraefix(
            final String praefixVerb,
            final String praefixRemainder,
            final AbstractDescription<?> desc,
            final boolean uncapitalize) {
        String hauptsatz = desc.getDescriptionHauptsatz();
        if (uncapitalize) {
            hauptsatz = GermanUtil.uncapitalize(hauptsatz);
        }

        return du(
                max(desc.getStartsNew(), SENTENCE),
                praefixVerb,
                praefixRemainder + " "
                        + hauptsatz)
                .komma(desc.isKommaStehtAus())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }

    @CheckReturnValue
    private static AbstractDuDescription<?, ?> duMitPraefixUndSatzanschluss(
            final String praefixVerb,
            final String praefixRemainder,
            final AbstractDuDescription<?, ?> desc) {
        return duMitPraefixUndSatzanschluss(praefixVerb, praefixRemainder, null,
                desc);
    }

    @CheckReturnValue
    private static AbstractDuDescription<?, ?> duMitPraefixUndSatzanschluss(
            final String praefixVerb,
            final String praefixRemainder,
            @Nullable final String praefixVorfeldSatzglied,
            final AbstractDuDescription<?, ?> desc) {
        return duMitPraefixUndSatzanschluss(praefixVerb, praefixRemainder, praefixVorfeldSatzglied,
                desc,
                desc.getDescriptionSatzanschlussOhneSubjekt());
    }

    @CheckReturnValue
    private static AbstractDuDescription<?, ?> duMitPraefixUndSatzanschluss(
            final String praefixVerb,
            final String praefixRemainder,
            final AbstractDescription<?> desc,
            final String descSatzanschluss) {
        return duMitPraefixUndSatzanschluss(
                praefixVerb, praefixRemainder, null, desc, descSatzanschluss);
    }

    @CheckReturnValue
    private static AbstractDuDescription<?, ?> duMitPraefixUndSatzanschluss(
            final String praefixVerb,
            final String praefixRemainder,
            @Nullable final String praefixVorfeldSatzglied,
            final AbstractDescription<?> desc,
            final String descSatzanschluss) {
        return du(max(desc.getStartsNew(), PARAGRAPH),
                praefixVerb,
                praefixRemainder
                        + " und "
                        + descSatzanschluss,
                praefixVorfeldSatzglied)
                .dann(desc.isDann())
                .komma(desc.isKommaStehtAus())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }

    @CheckReturnValue
    private static AllgDescription duMitVorfeld(final String vorfeld,
                                                final AbstractDuDescription<?, ?> duDesc) {
        return neuerSatz(max(duDesc.getStartsNew(), PARAGRAPH),
                duDesc.getDescriptionHauptsatzMitVorfeld(vorfeld))
                .komma(duDesc.isKommaStehtAus())
                .undWartest(duDesc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(duDesc.isDann())
                .phorikKandidat(duDesc.getPhorikKandidat())
                .beendet(duDesc.getEndsThis());
    }

    private static AbstractDuDescription<?, ?> mitAdvAngabe(
            final PraedikatDuDescription desc,
            final AdverbialeAngabeSkopusSatz advAngabe) {
        return du(
                max(desc.getStartsNew(), PARAGRAPH),
                desc.getPraedikat().mitAdverbialerAngabe(
                        // "Erneut gibst du der Frau die Kugel"
                        // "Du gibst erneut der Frau die Kugel"
                        advAngabe))
                .komma(desc.isKommaStehtAus())
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }

    @CheckReturnValue
    private static AbstractDuDescription<?, ?> mitAdvAngabe(
            final PraedikatDuDescription desc,
            final AdverbialeAngabeSkopusVerbAllg advAngabe) {
        return du(
                max(desc.getStartsNew(), PARAGRAPH),
                desc.getPraedikat().mitAdverbialerAngabe(
                        // "gibst der Frau die Kugel noch einmal"
                        advAngabe))
                .komma(desc.isKommaStehtAus())
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .phorikKandidat(desc.getPhorikKandidat())
                .beendet(desc.getEndsThis());
    }
}
