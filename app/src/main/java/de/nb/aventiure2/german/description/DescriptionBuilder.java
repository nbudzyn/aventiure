package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.SeinUtil;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.SCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase.WIRKEN;

public class DescriptionBuilder {
    private DescriptionBuilder() {
    }

    @CheckReturnValue
    public static AllgDescription paragraph(final String paragraph) {
        return neuerSatz(PARAGRAPH,
                paragraph)
                .beendet(PARAGRAPH);
    }

    @CheckReturnValue
    @NonNull
    public static ImmutableList<TimedDescription<AllgDescription>> altNeuerPraedikativumSatz(
            final GameObjectId subjektGameObject, final SubstantivischePhrase subjekt,
            final Collection<AllgDescription> altPraedikativa,
            final AvTimeSpan timeElapsed) {
        return altNeuerPraedikativumSatz(subjektGameObject, subjekt, altPraedikativa).stream()
                .map(s -> new TimedDescription<>(s, timeElapsed))
                .collect(ImmutableList.toImmutableList());
    }

    @CheckReturnValue
    @NonNull
    public static ImmutableList<TimedDescription<AllgDescription>> altNeuerWirkenScheinenSatz(
            final GameObjectId subjektGameObject, final SubstantivischePhrase subjekt,
            final Collection<AllgDescription> altPraedAdjPhrasen,
            final AvTimeSpan timeElapsed) {
        return altNeuerWirkenScheinenSatz(subjektGameObject, subjekt, altPraedAdjPhrasen).stream()
                .map(s -> new TimedDescription<>(s, timeElapsed))
                .collect(ImmutableList.toImmutableList());
    }

    @CheckReturnValue
    @NonNull
    public static ImmutableList<AllgDescription> altNeuerPraedikativumSatz(
            final GameObjectId subjektGameObject, final SubstantivischePhrase subjekt,
            final Collection<AllgDescription> altPraedikativa) {
        final ImmutableList.Builder<AllgDescription> alt = ImmutableList.builder();

        for (final AllgDescription praedikativum : altPraedikativa) {
            alt.add(
                    neuerSatz(subjekt.nom() + " "
                            + SeinUtil.VERB
                            .getPraesensOhnePartikel(subjekt.getPerson(), subjekt.getNumerus())
                            + " "
                            + praedikativum.getDescriptionHauptsatz())
                            .komma(praedikativum.isKommaStehtAus())
                            .phorikKandidat(subjekt, subjektGameObject));
        }

        return alt.build();
    }

    @CheckReturnValue
    @NonNull
    public static ImmutableList<AllgDescription> altNeuerWirkenScheinenSatz(
            final GameObjectId subjektGameObject, final SubstantivischePhrase subjekt,
            final Collection<AllgDescription> altPraedAdjPhrasen) {
        final ImmutableList.Builder<AllgDescription> alt = ImmutableList.builder();

        for (final AllgDescription praedAdjPhrase : altPraedAdjPhrasen) {
            alt.addAll(altNeuerWirkenScheinenSatz(subjektGameObject, subjekt, praedAdjPhrase));
        }

        return alt.build();
    }

    @CheckReturnValue
    @NonNull
    private static ImmutableList<AllgDescription> altNeuerWirkenScheinenSatz(
            final GameObjectId subjektGameObject, final SubstantivischePhrase subjekt,
            final AllgDescription praedAdjPhrase) {
        return ImmutableList.of(
                neuerWirkenScheinenSatz(subjektGameObject, subjekt,
                        wirkenVerbform(subjekt.getPerson(), subjekt.getNumerus()),
                        praedAdjPhrase),
                neuerWirkenScheinenSatz(subjektGameObject, subjekt,
                        scheinenVerbform(subjekt.getPerson(), subjekt.getNumerus()),
                        praedAdjPhrase)
        );
    }

    private static String wirkenVerbform(final Person person, final Numerus numerus) {
        switch (person) {
            case P1:
                return numerus == SG ? "wirke" : "wirken";
            case P2:
                return numerus == SG ? WIRKEN.getDuForm() : "wirkt";
            case P3:
                return numerus == SG ? "wirkt" : "wirken";
            default:
                throw new IllegalStateException("Unexpected Person: " + person);
        }
    }

    private static String scheinenVerbform(final Person person, final Numerus numerus) {
        switch (person) {
            case P1:
                return numerus == SG ? "scheine" : "scheinen";
            case P2:
                return numerus == SG ? SCHEINEN.getDuForm() : "scheint";
            case P3:
                return numerus == SG ? "scheint" : "scheinen";
            default:
                throw new IllegalStateException("Unexpected Person: " + person);
        }
    }


    @CheckReturnValue
    @NonNull
    private static AllgDescription neuerWirkenScheinenSatz(
            final GameObjectId subjektGameObject, final SubstantivischePhrase subjekt,
            final String verb,
            final AbstractDescription<?> praedikativum) {
        return neuerSatz(subjekt.nom() + " " + verb + " "
                + praedikativum.getDescriptionHauptsatz())
                .komma(praedikativum.isKommaStehtAus())
                .phorikKandidat(subjekt, subjektGameObject);
    }

    @CheckReturnValue
    public static TimedDescription<AllgDescription> neuerSatz(
            final String description,
            final AvTimeSpan timeElapsed) {
        return neuerSatz(description, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<AllgDescription> neuerSatz(
            final String description,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return new TimedDescription<>(
                neuerSatz(description),
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @NonNull
    @CheckReturnValue
    public static AllgDescription neuerSatz(final String description) {
        return neuerSatz(StructuralElement.SENTENCE, description);
    }

    @CheckReturnValue
    public static TimedDescription<AllgDescription> neuerSatz(
            final StructuralElement startsNew,
            final String description,
            final AvTimeSpan timeElapsed) {
        return neuerSatz(startsNew, description, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<AllgDescription> neuerSatz(
            final StructuralElement startsNew,
            final String description,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return new TimedDescription<>(
                neuerSatz(startsNew, description),
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @NonNull
    @CheckReturnValue
    public static AllgDescription neuerSatz(final StructuralElement startsNew,
                                            final String description) {
        return new AllgDescription(startsNew, capitalize(description));
    }

    @CheckReturnValue
    public static TimedDescription<AllgDescription> satzanschluss(
            final String description,
            final AvTimeSpan timeElapsed) {
        return satzanschluss(description, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<AllgDescription> satzanschluss(
            final String description,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return new TimedDescription<>(
                satzanschluss(description),
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @NonNull
    @CheckReturnValue
    public static AllgDescription satzanschluss(final String description) {
        return new AllgDescription(StructuralElement.WORD, description);
    }

    @NonNull
    @CheckReturnValue
    public static AllgDescription satzanschluss(final Wortfolge wortfolge) {
        return new AllgDescription(StructuralElement.WORD, wortfolge);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(final String verb,
                                                           final AvTimeSpan timeElapsed) {
        return du(verb, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(
            final String verb,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return du(verb, null, timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(final StructuralElement startsNew,
                                                           final String verb,
                                                           final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, null, timeElapsed);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(final String verb,
                                                           @Nullable final String remainder,
                                                           final AvTimeSpan timeElapsed) {
        return du(verb, remainder, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(
            final String verb,
            @Nullable final String remainder,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return du(verb, remainder, null, timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(final StructuralElement startsNew,
                                                           final String verb,
                                                           @Nullable final String remainder,
                                                           final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, remainder, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(
            final StructuralElement startsNew,
            final String verb,
            @Nullable final String remainder,
            final AvTimeSpan timeElapsed,
            @Nullable final
            String counterIdIncrementedIfTextIsNarrated) {
        return du(startsNew, verb, remainder, null,
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(final String verb,
                                                           @Nullable final String remainder,
                                                           @Nullable final String vorfeldSatzglied,
                                                           final AvTimeSpan timeElapsed) {
        return du(verb, remainder, vorfeldSatzglied, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(
            final String verb,
            @Nullable final String remainder,
            @Nullable final String vorfeldSatzglied,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return du(StructuralElement.WORD, verb, remainder, vorfeldSatzglied,
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(final StructuralElement startsNew,
                                                           final String verb,
                                                           @Nullable final String remainder,
                                                           @Nullable final String vorfeldSatzglied,
                                                           final AvTimeSpan timeElapsed) {
        return du(
                startsNew, verb, remainder, vorfeldSatzglied,
                timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(
            final StructuralElement startsNew,
            final String verb,
            @Nullable final String remainder,
            @Nullable final String vorfeldSatzglied,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated
    ) {
        return new TimedDescription<>(
                du(startsNew, verb, remainder, vorfeldSatzglied),
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb) {
        return du(verb, (String) null, (String) null);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew,
                                         final String verb) {
        return du(startsNew, verb, (String) null, (String) null);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final String remainder) {
        return du(verb, remainder, (String) null);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew,
                                         final String verb,
                                         @Nullable final String remainder) {
        return du(startsNew, verb, remainder, (String) null);
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew, final String verb,
                                         @Nullable final String remainder,
                                         @Nullable final String vorfeldSatzglied) {
        return new SimpleDuDescription(startsNew,
                new SimpleDuTextPart(verb, remainder, vorfeldSatzglied));
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final String remainder,
                                         @Nullable final String vorfeldSatzglied) {
        return du(StructuralElement.WORD, verb, remainder, vorfeldSatzglied);
    }

    @CheckReturnValue
    public static TimedDescription<PraedikatDuDescription> du(
            final PraedikatOhneLeerstellen duTextPart,
            final AvTimeSpan timeElapsed) {
        return du(duTextPart, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<PraedikatDuDescription> du(
            final PraedikatOhneLeerstellen duTextPart,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return du(StructuralElement.WORD, duTextPart,
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<PraedikatDuDescription> du(
            final StructuralElement startsNew,
            final PraedikatOhneLeerstellen duTextPart,
            final AvTimeSpan timeElapsed) {
        return du(startsNew, duTextPart, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<PraedikatDuDescription> du(
            final StructuralElement startsNew,
            final PraedikatOhneLeerstellen duTextPart,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIs) {
        return new TimedDescription<>(
                du(startsNew, duTextPart),
                timeElapsed, counterIdIncrementedIfTextIs);
    }

    @CheckReturnValue
    public static PraedikatDuDescription du(final PraedikatOhneLeerstellen duTextPart) {
        return du(StructuralElement.WORD, duTextPart);
    }

    @NonNull
    @CheckReturnValue
    public static PraedikatDuDescription du(final StructuralElement startsNew,
                                            final PraedikatOhneLeerstellen duTextPart) {
        return new PraedikatDuDescription(startsNew, duTextPart);
    }
}
