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
    private final DuTextPart duTextPart;

    public static DuDescription du(final String verb,
                                   final AvTimeSpan timeElapsed) {
        return du(verb, null, timeElapsed);
    }

    public static DuDescription du(final DuTextPart duTextPart,
                                   final AvTimeSpan timeElapsed) {
        return du(StructuralElement.WORD, duTextPart, timeElapsed);
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
        return du(startsNew, new SimpleDuTextPart(verb, remainder, vorfeldSatzglied), timeElapsed);
    }

    public static DuDescription du(final StructuralElement startsNew,
                                   final DuTextPart duTextPart,
                                   final AvTimeSpan timeElapsed) {
        return new DuDescription(startsNew, duTextPart, timeElapsed);
    }


    private DuDescription(final StructuralElement startsNew,
                          final DuTextPart duTextPart,
                          final AvTimeSpan timeElapsed) {
        // TODO Alle du()-Aufrufe prüfen, ggf. auf SENTENCE setzen
        super(startsNew, timeElapsed);
        this.duTextPart = duTextPart;
    }

    @Override
    public String getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        return duTextPart.getDuHauptsatzMitKonjunktionaladverbWennNoetig(konjunktionaladverb);
    }

    public String getDescriptionHauptsatzMitSpeziellemVorfeld() {
        return duTextPart.getDuHauptsatzMitSpeziellemVorfeld();
    }

    @Override
    public String getDescriptionHauptsatz() {
        return duTextPart.getDuHauptsatz();
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    public String getDescriptionSatzanschlussOhneSubjekt() {
        return duTextPart.getDuSatzanschlussOhneSubjekt();
    }
}
