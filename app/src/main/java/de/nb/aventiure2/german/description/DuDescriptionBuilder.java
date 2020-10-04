package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.PraedikatOhneLeerstellen;

public class DuDescriptionBuilder {
    private DuDescriptionBuilder() {
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         final AvTimeSpan timeElapsed) {
        return du(verb, null, timeElapsed);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew,
                                         final String verb,
                                         final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, null, timeElapsed);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final String remainder,
                                         final AvTimeSpan timeElapsed) {
        return du(verb, remainder, null, timeElapsed);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew,
                                         final String verb,
                                         @Nullable final String remainder,
                                         final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, remainder, null, timeElapsed);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final String verb,
                                         @Nullable final String remainder,
                                         @Nullable final String vorfeldSatzglied,
                                         final AvTimeSpan timeElapsed) {
        return du(StructuralElement.WORD, verb, remainder, vorfeldSatzglied, timeElapsed);
    }

    @CheckReturnValue
    public static SimpleDuDescription du(final StructuralElement startsNew,
                                         final String verb,
                                         @Nullable final String remainder,
                                         @Nullable final String vorfeldSatzglied,
                                         final AvTimeSpan timeElapsed) {
        return new SimpleDuDescription(startsNew,
                new SimpleDuTextPart(verb, remainder, vorfeldSatzglied), timeElapsed);
    }

    @CheckReturnValue
    public static PraedikatDuDescription du(final PraedikatOhneLeerstellen duTextPart,
                                            final AvTimeSpan timeElapsed) {
        return du(StructuralElement.WORD, duTextPart, timeElapsed);
    }

    @CheckReturnValue
    public static PraedikatDuDescription du(
            final StructuralElement startsNew,
            final PraedikatOhneLeerstellen duTextPart,
            final AvTimeSpan timeElapsed) {
        return new PraedikatDuDescription(startsNew, duTextPart, timeElapsed);
    }
}
