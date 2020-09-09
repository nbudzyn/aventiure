package de.nb.aventiure2.german.description;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.DuTextPart;

/**
 * A description - assuming the player character is the (first) subject. Somehting like
 * "Du gehst in den Wald."
 */
public class DuDescription extends AbstractDescription<DuDescription> {
    private final DuTextPart duHauptsatzBuilder;

    public static DuDescription du(final String verb,
                                   final AvTimeSpan timeElapsed) {
        return du(verb, null, timeElapsed);
    }

    public static DuDescription du(final StructuralElement startsNew,
                                   final String verb,
                                   final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, null, timeElapsed);
    }

    public static DuDescription du(final String verb,
                                   @Nullable final String remainder,
                                   final AvTimeSpan timeElapsed) {
        return du(verb, remainder, null, timeElapsed);
    }

    public static DuDescription du(final StructuralElement startsNew,
                                   final String verb,
                                   @Nullable final String remainder,
                                   final AvTimeSpan timeElapsed) {
        return du(startsNew, verb, remainder, null, timeElapsed);
    }

    public static DuDescription du(final String verb,
                                   @Nullable final String remainder,
                                   @Nullable final String vorfeldSatzglied,
                                   final AvTimeSpan timeElapsed) {
        return du(StructuralElement.WORD, verb, remainder, vorfeldSatzglied, timeElapsed);
    }

    public static DuDescription du(final StructuralElement startsNew,
                                   final String verb,
                                   @Nullable final String remainder,
                                   @Nullable final String vorfeldSatzglied,
                                   final AvTimeSpan timeElapsed) {
        return new DuDescription(startsNew, verb, remainder, vorfeldSatzglied, timeElapsed);
    }

    private DuDescription(final StructuralElement startsNew,
                          final String verb,
                          @Nullable final String remainder,
                          @Nullable final String vorfeldSatzglied,
                          final AvTimeSpan timeElapsed) {
        // TODO Alle du()-Aufrufe prüfen, ggf. auf SENTENCE setzen
        super(startsNew, timeElapsed);

        duHauptsatzBuilder =
                new SimpleDuTextPart(verb, remainder, vorfeldSatzglied);
    }

    @Override
    public String getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        return duHauptsatzBuilder.getDuHauptsatzMitKonjunktionaladverbWennNoetig(
                konjunktionaladverb
        );
    }

    public String getDescriptionHauptsatzMitSpeziellemVorfeld() {
        return duHauptsatzBuilder.getDuHauptsatzMitSpeziellemVorfeld();
    }

    @Override
    public String getDescriptionHauptsatz() {
        return duHauptsatzBuilder.getDuHauptsatz();
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    public String getDescriptionSatzanschlussOhneSubjekt() {
        return duHauptsatzBuilder.getDuSatzanschlussOhneSubjekt();
    }
}
