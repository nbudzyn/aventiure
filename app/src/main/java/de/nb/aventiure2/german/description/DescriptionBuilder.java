package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.Wortfolge;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.base.Wortfolge.w;

public class DescriptionBuilder {
    private DescriptionBuilder() {
    }

    @CheckReturnValue
    public static TextDescription paragraph(final String paragraph) {
        return neuerSatz(PARAGRAPH,
                paragraph)
                .beendet(PARAGRAPH);
    }


    @CheckReturnValue
    public static TimedDescription<TextDescription> neuerSatz(
            final String description,
            final AvTimeSpan timeElapsed) {
        return neuerSatz(description, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<TextDescription> neuerSatz(
            final String description,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return new TimedDescription<>(
                neuerSatz(description),
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    public static TextDescription neuerSatz(final Iterable<Konstituente> konsituenten) {
        return neuerSatz(SENTENCE, konsituenten);
    }

    public static TextDescription neuerSatz(final StructuralElement startsNew,
                                            final Iterable<Konstituente> konsituenten) {
        return neuerSatz(startsNew, Wortfolge.joinToWortfolge(konsituenten));
    }

    @NonNull
    @CheckReturnValue
    public static TimedDescription<TextDescription> neuerSatz(final Wortfolge wortfolge,
                                                              final AvTimeSpan timeElapsed) {
        return neuerSatz(StructuralElement.SENTENCE, wortfolge, timeElapsed);
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription neuerSatz(final Wortfolge wortfolge) {
        return neuerSatz(StructuralElement.SENTENCE, wortfolge);
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription neuerSatz(final String description) {
        return neuerSatz(StructuralElement.SENTENCE, description);
    }

    @CheckReturnValue
    public static TimedDescription<TextDescription> neuerSatz(
            final StructuralElement startsNew,
            final String description,
            final AvTimeSpan timeElapsed) {
        return neuerSatz(startsNew, description, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<TextDescription> neuerSatz(
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
    public static TimedDescription<TextDescription> neuerSatz(final StructuralElement startsNew,
                                                              final Wortfolge wortfolge,
                                                              final AvTimeSpan timeElapsed) {
        return new TimedDescription<>(neuerSatz(startsNew, wortfolge), timeElapsed);
    }


    @NonNull
    @CheckReturnValue
    public static TextDescription neuerSatz(final StructuralElement startsNew,
                                            final Wortfolge wortfolge) {
        checkArgument(startsNew != WORD,
                "Neuer Satz unmöglich für " + startsNew);

        return new TextDescription(startsNew, wortfolge);
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription neuerSatz(final StructuralElement startsNew,
                                            final String description) {
        checkArgument(startsNew != WORD,
                "Neuer Satz unmöglich für " + startsNew);

        return neuerSatz(startsNew, w(description));
    }

    @NonNull
    @CheckReturnValue
    public static TimedDescription<StructuredDescription> satz(
            final Satz satz,
            final AvTimeSpan timeElapsed) {
        return satz(WORD, satz, timeElapsed);
    }

    @NonNull
    @CheckReturnValue
    public static TimedDescription<StructuredDescription> satz(
            final Satz satz,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return satz(WORD, satz, timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @NonNull
    @CheckReturnValue
    public static TimedDescription<StructuredDescription> satz(
            final StructuralElement startsNew, final Satz satz,
            final AvTimeSpan timeElapsed) {
        return satz(startsNew, satz, timeElapsed, null);
    }

    @NonNull
    @CheckReturnValue
    public static TimedDescription<StructuredDescription> satz(
            final StructuralElement startsNew, final Satz satz,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return new TimedDescription<>(
                satz(startsNew, satz), timeElapsed,
                counterIdIncrementedIfTextIsNarrated);
    }


    @CheckReturnValue
    public static TimedDescription<TextDescription> satzanschluss(
            final String description,
            final AvTimeSpan timeElapsed) {
        return satzanschluss(description, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<TextDescription> satzanschluss(
            final String description,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return new TimedDescription<>(
                satzanschluss(description),
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription satzanschluss(final String description) {
        return satzanschluss(w(description));
    }

    @NonNull
    @CheckReturnValue
    public static TextDescription satzanschluss(final Wortfolge wortfolge) {
        return new TextDescription(StructuralElement.WORD, wortfolge);
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
        return du(verb, (Wortfolge) null, timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(final StructuralElement startsNew,
                                                           final String verb,
                                                           final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, (Wortfolge) null, timeElapsed);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(final String verb,
                                                           @Nullable final String remainder,
                                                           final AvTimeSpan timeElapsed) {
        return du(verb, w(remainder), timeElapsed);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(final String verb,
                                                           @Nullable final Wortfolge remainder,
                                                           final AvTimeSpan timeElapsed) {
        return du(verb, remainder, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(
            final String verb,
            @Nullable final String remainder,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return du(verb, w(remainder), timeElapsed,
                counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(
            final String verb,
            @Nullable final Wortfolge remainder,
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
    public static TimedDescription<SimpleDuDescription> du(final StructuralElement startsNew,
                                                           final String verb,
                                                           @Nullable final Wortfolge remainder,
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
    public static TimedDescription<SimpleDuDescription> du(
            final StructuralElement startsNew,
            final String verb,
            @Nullable final Wortfolge remainder,
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
        return du(verb, w(remainder), vorfeldSatzglied,
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(
            final String verb,
            @Nullable final Wortfolge remainder,
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
        return du(startsNew, verb, w(remainder), vorfeldSatzglied, timeElapsed,
                counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<SimpleDuDescription> du(
            final StructuralElement startsNew,
            final String verb,
            @Nullable final Wortfolge remainder,
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
        return du(startsNew, verb, (Wortfolge) null, (String) null);
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
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final Wortfolge remainder) {
        return du(WORD, verb, remainder);
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew, final String verb,
                                         @Nullable final Wortfolge remainder) {
        return du(startsNew, verb, remainder, (String) null);
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew, final String verb,
                                         @Nullable final String remainder,
                                         @Nullable final String vorfeldSatzglied) {
        return du(startsNew, verb, w(remainder), vorfeldSatzglied);
    }

    @NonNull
    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew, final String verb,
                                         @Nullable final Wortfolge remainder,
                                         @Nullable final String vorfeldSatzglied) {
        return new SimpleDuDescription(startsNew,
                verb,
                remainder != null ? remainder.getString() : null,
                vorfeldSatzglied,
                remainder != null && remainder.woertlicheRedeNochOffen(),
                remainder != null && remainder.kommaStehtAus(),
                remainder != null ? remainder.getPhorikKandidat() : null);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final String remainder,
                                         @Nullable final String vorfeldSatzglied) {
        return du(StructuralElement.WORD, verb, remainder, vorfeldSatzglied);
    }

    @CheckReturnValue
    public static TimedDescription<StructuredDescription> du(
            final PraedikatOhneLeerstellen praedikat,
            final AvTimeSpan timeElapsed) {
        return du(praedikat, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<StructuredDescription> du(
            final PraedikatOhneLeerstellen praedikat,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIsNarrated) {
        return du(StructuralElement.WORD, praedikat,
                timeElapsed, counterIdIncrementedIfTextIsNarrated);
    }

    @CheckReturnValue
    public static TimedDescription<StructuredDescription> du(
            final StructuralElement startsNew,
            final PraedikatOhneLeerstellen praedikat,
            final AvTimeSpan timeElapsed) {
        return du(startsNew, praedikat, timeElapsed, null);
    }

    @CheckReturnValue
    public static TimedDescription<StructuredDescription> du(
            final StructuralElement startsNew,
            final PraedikatOhneLeerstellen praedikat,
            final AvTimeSpan timeElapsed,
            @Nullable final String counterIdIncrementedIfTextIs) {
        return new TimedDescription<>(
                du(startsNew, praedikat),
                timeElapsed, counterIdIncrementedIfTextIs);
    }

    @CheckReturnValue
    public static StructuredDescription du(final PraedikatOhneLeerstellen praedikat) {
        return du(StructuralElement.WORD, praedikat);
    }

    @NonNull
    @CheckReturnValue
    public static StructuredDescription du(final StructuralElement startsNew,
                                           final PraedikatOhneLeerstellen praedikat) {
        return satz(startsNew,
                praedikat.alsSatzMitSubjekt(Personalpronomen.get(P2,
                        // Wir behaupten hier, der Adressat wäre männlich.
                        // Es ist die Verantwortung des Aufrufers, keine
                        // Sätze mit Konstruktionen wie "Du, der du" zu erzeugen, die
                        // weibliche Adressaten ("du, die du") ausschließen.
                        M, SPIELER_CHARAKTER)));
    }

    @NonNull
    @CheckReturnValue
    public static StructuredDescription satz(final Satz satz) {
        return satz(WORD, satz);
    }

    @NonNull
    @CheckReturnValue
    static StructuredDescription satz(final StructuralElement startsNew, final Satz satz) {
        return new StructuredDescription(startsNew, satz);
    }
}
