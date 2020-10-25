package de.nb.aventiure2.german.description;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

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
    public static TimedDescription neuerSatz(
            final String description,
            final AvTimeSpan timeElapsed) {
        return new TimedDescription(
                neuerSatz(description),
                timeElapsed);
    }

    @NonNull
    public static AllgDescription neuerSatz(final String description) {
        return neuerSatz(StructuralElement.SENTENCE, description);
    }

    @CheckReturnValue
    public static TimedDescription neuerSatz(
            final StructuralElement startsNew,
            final String description,
            final AvTimeSpan timeElapsed) {
        return new TimedDescription(
                neuerSatz(startsNew, description),
                timeElapsed);
    }

    @NonNull
    public static AllgDescription neuerSatz(final StructuralElement startsNew,
                                            final String description) {
        return new AllgDescription(startsNew, capitalize(description));
    }

    @CheckReturnValue
    public static TimedDescription satzanschluss(
            final String description,
            final AvTimeSpan timeElapsed) {
        return new TimedDescription(
                satzanschluss(description),
                timeElapsed);
    }

    @NonNull
    public static AllgDescription satzanschluss(final String description) {
        return new AllgDescription(StructuralElement.WORD, description);
    }

    @CheckReturnValue
    public static TimedDescription du(final String verb,
                                      final AvTimeSpan timeElapsed) {
        return du(verb, null, timeElapsed);
    }

    @CheckReturnValue
    public static TimedDescription du(final StructuralElement startsNew,
                                      final String verb,
                                      final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, null, timeElapsed);
    }

    @CheckReturnValue
    public static TimedDescription du(final String verb,
                                      @Nullable final String remainder,
                                      final AvTimeSpan timeElapsed) {
        return du(verb, remainder, null, timeElapsed);
    }

    @CheckReturnValue
    public static TimedDescription du(final StructuralElement startsNew,
                                      final String verb,
                                      @Nullable final String remainder,
                                      final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, remainder, null, timeElapsed);
    }

    @CheckReturnValue
    public static TimedDescription du(final String verb,
                                      @Nullable final String remainder,
                                      @Nullable final String vorfeldSatzglied,
                                      final AvTimeSpan timeElapsed) {
        return du(StructuralElement.WORD, verb, remainder, vorfeldSatzglied, timeElapsed);
    }

    @CheckReturnValue
    public static TimedDescription du(final StructuralElement startsNew,
                                      final String verb,
                                      @Nullable final String remainder,
                                      @Nullable final String vorfeldSatzglied,
                                      final AvTimeSpan timeElapsed) {
        return new TimedDescription(
                du(startsNew, verb, remainder, vorfeldSatzglied),
                timeElapsed);
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
    public static SimpleDuDescription du(final StructuralElement startsNew,
                                         final String verb,
                                         @Nullable final String remainder) {
        return du(startsNew, verb, remainder, (String) null);
    }

    @NonNull
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
    public static TimedDescription du(final PraedikatOhneLeerstellen duTextPart,
                                      final AvTimeSpan timeElapsed) {
        return du(StructuralElement.WORD, duTextPart, timeElapsed);
    }

    @CheckReturnValue
    public static TimedDescription du(
            final StructuralElement startsNew,
            final PraedikatOhneLeerstellen duTextPart,
            final AvTimeSpan timeElapsed) {
        return new TimedDescription(
                du(startsNew, duTextPart),
                timeElapsed);
    }

    @CheckReturnValue
    public static PraedikatDuDescription du(final PraedikatOhneLeerstellen duTextPart) {
        return du(StructuralElement.WORD, duTextPart);
    }

    @NonNull
    public static PraedikatDuDescription du(final StructuralElement startsNew,
                                            final PraedikatOhneLeerstellen duTextPart) {
        return new PraedikatDuDescription(startsNew, duTextPart);
    }
}
