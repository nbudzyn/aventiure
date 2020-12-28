package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.german.praedikat.VerbSubjPraedikativeAdjektivphrase;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.TimedDescription.toTimed;

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
    public static ImmutableList<TimedDescription<AllgDescription>> neuePraedikativumSaetze(
            final GameObjectId subjektGameObjectId, final SubstantivischePhrase subjekt,
            final Collection<Wortfolge> altPraedikativa,
            final AvTimeSpan timeElapsed) {
        return toTimed(neuePraedikativumSaetze(subjektGameObjectId, subjekt, altPraedikativa),
                timeElapsed);
    }

    @CheckReturnValue
    @NonNull
    public static ImmutableList<AbstractDescription<?>> neueSaetzeMitPhorikKandidat(
            final SubstantivischePhrase phorikKandidatPhrase,
            final GameObjectId phorikKandidatGameObjectId,
            final Collection<Satz> saetze) {
        return saetze.stream()
                .map(s -> neuerSatz(s)
                        .phorikKandidat(phorikKandidatPhrase, phorikKandidatGameObjectId))
                .collect(toImmutableList());
    }

    @CheckReturnValue
    @NonNull
    private static ImmutableList<AllgDescription> neuePraedikativumSaetze(
            final GameObjectId subjektGameObjectId, final SubstantivischePhrase subjekt,
            final Collection<Wortfolge> praedikativa) {
        return praedikativa.stream()
                .map(p -> neuerSatz(
                        subjekt.nom() + " "
                                + SeinUtil.VERB
                                .getPraesensOhnePartikel(subjekt.getPerson(), subjekt.getNumerus())
                                + " "
                                + p.getString())
                        .komma(p.kommmaStehtAus())
                        .phorikKandidat(subjekt, subjektGameObjectId))
                .collect(toImmutableList());
    }

    @CheckReturnValue
    @NonNull
    public static ImmutableList<AbstractDescription<?>> neueAdjPhrSaetze(
            final GameObjectId subjektGameObjectId, final SubstantivischePhrase subjekt,
            final Collection<? extends VerbSubjPraedikativeAdjektivphrase> verben,
            final AdjPhrOhneLeerstellen adjPhrase) {
        return verben.stream()
                .map(v -> neuerSatz(v.mit(adjPhrase).alsSatzMitSubjekt(subjekt))
                        .phorikKandidat(subjekt, subjektGameObjectId))
                .collect(toImmutableList());
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
    public static AbstractDescription<?> neuerSatz(final Satz satz) {
        if (satz.getSubjekt().getPerson() == P2 && satz.getSubjekt().getNumerus() == SG) {
            return du(SENTENCE, satz.getPraedikat());
        }

        return neuerSatz(satz.getVerbzweitsatz());
    }

    public static AllgDescription neuerSatz(final Iterable<Konstituente> verbzweitsatz) {
        return neuerSatz(Wortfolge.joinToNullWortfolge(verbzweitsatz));
    }

    @NonNull
    @CheckReturnValue
    public static AllgDescription neuerSatz(final Wortfolge wortfolge) {
        return neuerSatz(StructuralElement.SENTENCE, wortfolge);
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
                                            final Wortfolge wortfolge) {
        return new AllgDescription(startsNew, wortfolge.capitalize());
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
        return du(startsNew, verb, null, (String) null);
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
            final PraedikatOhneLeerstellen praedikat,
            final AvTimeSpan timeElapsed) {
        return du(praedikat, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<PraedikatDuDescription> du(
            final PraedikatOhneLeerstellen praedikat,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return du(StructuralElement.WORD, praedikat,
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<PraedikatDuDescription> du(
            final StructuralElement startsNew,
            final PraedikatOhneLeerstellen praedikat,
            final AvTimeSpan timeElapsed) {
        return du(startsNew, praedikat, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<PraedikatDuDescription> du(
            final StructuralElement startsNew,
            final PraedikatOhneLeerstellen praedikat,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIs) {
        return new TimedDescription<>(
                du(startsNew, praedikat),
                timeElapsed, counterIdIncrementedIfTextIs);
    }

    @CheckReturnValue
    public static PraedikatDuDescription du(final PraedikatOhneLeerstellen praedikat) {
        return du(StructuralElement.WORD, praedikat);
    }

    @NonNull
    @CheckReturnValue
    public static PraedikatDuDescription du(final StructuralElement startsNew,
                                            final PraedikatOhneLeerstellen praedikat) {
        return new PraedikatDuDescription(startsNew, praedikat);
    }
}
