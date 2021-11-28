package de.nb.aventiure2.german.description;

import static java.util.Arrays.asList;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.max;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.util.StreamUtil.*;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.Modalpartikel;

/**
 * Statische Methoden, die {@link AbstractDescription}s umformulieren.
 */
public class DescriptionUmformulierer {
    private DescriptionUmformulierer() {
    }

    @CheckReturnValue
    public static ImmutableCollection<TimedDescription<?>> drueckeAusTimed(
            final Kohaerenzrelation kohaerenzrelation,
            final boolean hoechstensUnauffaelligeAnpassungen,
            final TimedDescription<?>... desc) {
        return drueckeAusTimed(kohaerenzrelation, hoechstensUnauffaelligeAnpassungen, asList(desc));
    }

    @CheckReturnValue
    public static ImmutableCollection<TimedDescription<?>> drueckeAusTimed(
            final Kohaerenzrelation kohaerenzrelation,
            final boolean hoechstensUnauffaelligeAnpassungen,
            final Collection<? extends TimedDescription<?>> descriptions) {
        return descriptions.stream()
                .flatMap(d ->
                        drueckeAus(kohaerenzrelation,
                                hoechstensUnauffaelligeAnpassungen,
                                d.getDescription()).stream()
                                .map(r -> r.timed(d.getTimeElapsed())
                                        .withCounterIdIncrementedIfTextIsNarrated(
                                                d.getCounterIdIncrementedIfTextIsNarrated()))
                )
                .collect(ImmutableList.toImmutableList());
    }

    @CheckReturnValue
    public static ImmutableCollection<AbstractDescription<?>> drueckeAus(
            final Kohaerenzrelation kohaerenzrelation,
            final boolean hoechstensUnauffaelligeAnpassungen,
            final AbstractDescription<?>... desc) {
        return drueckeAus(kohaerenzrelation,
                hoechstensUnauffaelligeAnpassungen,
                asList(desc));
    }

    @CheckReturnValue
    public static ImmutableCollection<AbstractDescription<?>> drueckeAus(
            final Kohaerenzrelation kohaerenzrelation,
            final boolean hoechstensUnauffaelligeAnpassungen,
            final Collection<? extends AbstractDescription<?>> descriptions) {
        return descriptions.stream()
                .flatMap(d -> drueckeAus(kohaerenzrelation,
                        hoechstensUnauffaelligeAnpassungen,
                        d)
                        .stream())
                .collect(ImmutableList.toImmutableList());
    }

    @CheckReturnValue
    public static ImmutableCollection<AbstractDescription<?>> drueckeAus(
            final Kohaerenzrelation kohaerenzrelation,
            final boolean hoechstensUnauffaelligeAnpassungen,
            final AbstractDescription<?> desc) {
        switch (kohaerenzrelation) {
            case DISKONTINUITAET:
                return drueckeDiskontinuitaetAus(hoechstensUnauffaelligeAnpassungen, desc);
            case WIEDERHOLUNG:
                return drueckeWiederholungAus(hoechstensUnauffaelligeAnpassungen, desc);
            case FORTSETZUNG:
                return drueckeFortsetzungAus(hoechstensUnauffaelligeAnpassungen, desc);
            case VERSTEHT_SICH_VON_SELBST:
                return ImmutableList.of(desc);
            default:
                throw new IllegalStateException("Unexpected Kohaerenzrelation: " +
                        kohaerenzrelation);
        }
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> drueckeDiskontinuitaetAus(
            final boolean hoechstensUnauffaelligeAnpassungen,
            final AbstractDescription<?> desc) {
        final AltDescriptionsBuilder alt = alt();

        if (desc instanceof AbstractFlexibleDescription) {
            final AbstractFlexibleDescription<?> fDesc = (AbstractFlexibleDescription<?>) desc;

            if (fDesc.hasSubjektDuBelebt()) {
                if (!hoechstensUnauffaelligeAnpassungen) {
                    alt.add(duMitPraefixUndSatzanschluss(
                            "besinnst", "dich aber",
                            fDesc));
                }
            }

            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("noch einmal", fDesc));

            if (fDesc instanceof StructuredDescription) {
                final StructuredDescription sDesc = (StructuredDescription) fDesc;

                alt.add(mitAdvAngabe(sDesc,
                        new AdvAngabeSkopusVerbAllg("noch einmal")));

                alt.add(mitAdvAngabe(sDesc,
                        new AdvAngabeSkopusVerbAllg("erneut")));

                alt.add(mitAdvAngabe(sDesc,
                        new AdvAngabeSkopusVerbAllg("sogleich wieder")).schonLaenger()
                );

                final Konstituente duNimmstDieKugelBesserDoch =
                        sDesc.getSatz()
                                .mitModalpartikeln(
                                        new Modalpartikel("besser"),
                                        new Modalpartikel("doch"))
                                .getVerbzweitsatzStandard()
                                .joinToSingleKonstituente();
                alt.add(neuerSatz(
                        max(fDesc.getStartsNew(), SENTENCE),
                        "Ach nein,",
                        // du nimmst die Kugel besser doch
                        duNimmstDieKugelBesserDoch)
                        .undWartest(fDesc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                        .dann(fDesc.isDann())
                        .phorikKandidat(fDesc.getPhorikKandidat()));
            }
        } else {
            if (!hoechstensUnauffaelligeAnpassungen) {
                alt.addAll(desc.altMitPraefix(joinToKonstituentenfolge(
                        SENTENCE, "Aber dir kommt ein Gedanke:", SENTENCE)));
                alt.addAll(desc.altMitPraefix(joinToKonstituentenfolge(
                        SENTENCE, "Dir kommt ein Gedanke:", SENTENCE)));
            }
        }

        alt.addIfOtherwiseEmpty(desc);

        return alt.build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> drueckeWiederholungAus(
            final boolean hoechstensUnauffaelligeAnpassungen,
            final AbstractDescription<?> desc) {
        final AltDescriptionsBuilder alt = alt();

        if (desc instanceof StructuredDescription) {
            final StructuredDescription sDesc = (StructuredDescription) desc;

            alt.add(mitAdvAngabe(sDesc,
                    new AdvAngabeSkopusSatz("noch einmal")));

            alt.add(mitAdvAngabe(sDesc,
                    new AdvAngabeSkopusSatz("erneut")));

            alt.add(mitAdvAngabe(sDesc,
                    new AdvAngabeSkopusSatz("von neuem")));

            alt.add(mitAdvAngabe(sDesc,
                    new AdvAngabeSkopusSatz("ein weiteres Mal")));

            alt.add(mitAdvAngabe(sDesc,
                    new AdvAngabeSkopusSatz("nochmals")).schonLaenger()
            );

            alt.add(mitAdvAngabe(sDesc,
                    new AdvAngabeSkopusSatz("wieder")).schonLaenger()
            );
        }

        if (desc instanceof AbstractFlexibleDescription) {
            final AbstractFlexibleDescription<?> fDesc = (AbstractFlexibleDescription<?>) desc;

            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("noch einmal", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("noch einmal aber", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("erneut", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("von neuem", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("ein weiteres Mal", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("nochmals", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("wieder", fDesc));

            if (!(desc instanceof StructuredDescription)) {
                if (fDesc.hasSubjektDuBelebt()) {
                    if (!hoechstensUnauffaelligeAnpassungen) {
                        alt.add(duMitPraefixUndSatzanschluss(
                                "gibst", "nicht auf",
                                fDesc).schonLaenger()
                        );

                        alt.add(duMitPraefixUndSatzanschluss(
                                "gibst aber", "nicht auf",
                                fDesc).schonLaenger()
                        );

                        alt.add(duMitPraefixUndSatzanschluss(
                                "versuchst", "es noch einmal",
                                fDesc));

                        alt.add(duMitPraefixUndSatzanschluss(
                                "versuchst", "dich aber erneut", fDesc));
                    }
                }
            }
        }

        if (!(desc instanceof AbstractFlexibleDescription)) {
            if (!hoechstensUnauffaelligeAnpassungen) {
                alt.addAll(duMitPraefixAltNeueSaetzeSchonLaenger("gibst", "aber nicht auf:",
                        desc));
                alt.addAll(duMitPraefixAltNeueSaetzeSchonLaenger("versuchst", "es noch einmal:",
                        desc));
                alt.addAll(duMitPraefixAltNeueSaetzeSchonLaenger("lässt", "dich nicht entmutigen.",
                        desc));
            }
        }

        alt.addIfOtherwiseEmpty(desc);

        return alt.build();
    }

    @CheckReturnValue
    private static ImmutableCollection<AbstractDescription<?>> drueckeFortsetzungAus(
            final boolean hoechstensUnauffaelligeAnpassungen,
            final AbstractDescription<?> desc) {
        final AltDescriptionsBuilder alt = alt();

        if (desc instanceof StructuredDescription) {
            final StructuredDescription sDesc = (StructuredDescription) desc;

            alt.add(mitAdvAngabe(sDesc, new AdvAngabeSkopusSatz("immer noch"))
                            .schonLaenger(),
                    mitAdvAngabe(sDesc, new AdvAngabeSkopusSatz("unverdrossen"))
                            .schonLaenger(),
                    mitAdvAngabe(sDesc,
                            new AdvAngabeSkopusVerbAllg("weiter")),
                    mitAdvAngabe(sDesc,
                            new AdvAngabeSkopusVerbAllg("weiterhin")));
        }


        if (desc instanceof AbstractFlexibleDescription<?>) {
            final AbstractFlexibleDescription<?> fDesc = (AbstractFlexibleDescription<?>) desc;

            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("Immer noch", fDesc));
            alt.add(toTextDescriptionMindestensParagraphMitVorfeld("Unverdrossen", fDesc));

            if (!(desc instanceof StructuredDescription)) {
                if (fDesc.hasSubjektDuBelebt()) {
                    if (!hoechstensUnauffaelligeAnpassungen) {
                        alt.add(duMitPraefixUndSatzanschluss(
                                "gibst", "nicht auf",
                                fDesc).schonLaenger()
                        );

                        alt.add(duMitPraefixUndSatzanschluss(
                                "gibst aber", "nicht auf",
                                fDesc).schonLaenger()
                        );

                        alt.add(duMitPraefixUndSatzanschluss(
                                "versuchst", "es weiter",
                                fDesc).schonLaenger()
                        );

                        alt.add(duMitPraefixUndSatzanschluss(
                                "versuchst", "es noch weiter",
                                fDesc).schonLaenger()
                        );

                        alt.add(duMitPraefixUndSatzanschluss(
                                "versuchst", "es weiterhin",
                                fDesc).schonLaenger()
                        );

                        alt.add(duMitPraefixUndSatzanschluss(
                                "versuchst", "es unverdrossen weiter",
                                "unverdrossen",
                                fDesc).schonLaenger()
                        );
                    }
                }
            }
        }

        if (!(desc instanceof AbstractFlexibleDescription<?>)) {
            if (!hoechstensUnauffaelligeAnpassungen) {
                alt.addAll(duMitPraefixAltNeueSaetzeSchonLaenger("gibst", "aber nicht auf:",
                        desc));
                alt.addAll(duMitPraefixAltNeueSaetzeSchonLaenger("versuchst", "es weiter:",
                        desc));
                alt.addAll(duMitPraefixAltNeueSaetzeSchonLaenger("versuchst", "es noch weiter:",
                        desc));
                alt.addAll(duMitPraefixAltNeueSaetzeSchonLaenger("versuchst", "es weiterhin:",
                        desc));
                alt.addAll(duMitPraefixAltNeueSaetzeSchonLaenger("lässt", "dich nicht entmutigen.",
                        desc));
            }
        }

        alt.addIfOtherwiseEmpty(desc);

        return alt.build();
    }

    private static Collection<AbstractFlexibleDescription<?>> duMitPraefixAltNeueSaetzeSchonLaenger(
            final String praefixVerb,
            final String praefixRemainder,
            final AbstractDescription<?> desc) {
        return mapToSet(desc.altTextDescriptions(), d -> du(
                max(d.getStartsNew(), SENTENCE),
                praefixVerb,
                praefixRemainder,
                SENTENCE,
                d.toSingleKonstituente()).schonLaenger());
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
        return du(max(desc.getStartsNew(), PARAGRAPH),
                praefixVerb,
                praefixRemainder,
                desc.toTextDescriptionSatzanschlussMitAnschlusswortOderVorkomma(),
                desc.getEndsThis())
                .mitVorfeldSatzglied(praefixVorfeldSatzglied)
                .dann(desc.isDann());
    }

    @CheckReturnValue
    private static TextDescription toTextDescriptionMindestensParagraphMitVorfeld(
            final String vorfeld,
            final AbstractFlexibleDescription<?> desc) {
        return desc.toTextDescriptionMitVorfeld(vorfeld).beginntZumindest(PARAGRAPH);
    }

    private static AbstractDescription<?> mitAdvAngabe(
            final StructuredDescription desc,
            final AdvAngabeSkopusSatz advAngabe) {
        return DescriptionBuilder.satz(
                max(desc.getStartsNew(), PARAGRAPH),
                desc.getSatz().mitAdvAngabe(
                        // "Erneut gibst du der Frau die Kugel"
                        // "Du gibst erneut der Frau die Kugel"
                        advAngabe),
                desc.getEndsThis())
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .phorikKandidat(desc.getPhorikKandidat());
    }

    @CheckReturnValue
    private static AbstractDescription<?> mitAdvAngabe(
            final StructuredDescription desc,
            final AdvAngabeSkopusVerbAllg advAngabe) {
        return DescriptionBuilder.satz(
                max(desc.getStartsNew(), PARAGRAPH),
                desc.getSatz().mitAdvAngabe(
                        // "gibst der Frau die Kugel noch einmal"
                        advAngabe),
                desc.getEndsThis())
                .undWartest(desc.isAllowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.isDann())
                .phorikKandidat(desc.getPhorikKandidat());
    }
}
