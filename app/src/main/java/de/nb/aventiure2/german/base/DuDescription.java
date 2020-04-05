package de.nb.aventiure2.german.base;

import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

/**
 * A description - assuming the player character is the (first) subject. Somehting like
 * "Du gehts in den Wald."
 */
public class DuDescription extends AbstractDescription {
    /**
     * Something like "gehst"
     */
    private final String verb;
    /**
     * Something like "in den Wald"
     */
    private final String remainder;

    public static DuDescription du(final String verb, final String remainder,
                                   final boolean kommaStehtAus,
                                   final boolean
                                           allowsAdditionalDuSatzreihengliedOhneSubjekt,
                                   final boolean dann,
                                   final AvTimeSpan timeElapsed) {
        return new DuDescription(verb, remainder,
                kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann, timeElapsed);
    }

    private DuDescription(final String verb, final String remainder,
                          final boolean kommaStehtAus,
                          final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                          final boolean dann,
                          final AvTimeSpan timeElapsed) {
        super(kommaStehtAus, allowsAdditionalDuSatzreihengliedOhneSubjekt, dann, timeElapsed);
        this.verb = verb;
        this.remainder = remainder;
    }

    @Override
    public String getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        // Konjunktionaladverb ist nötig:
        // Du gehst in den Wald. Dann gehst du den Fluss entlang.
        return capitalize(konjunktionaladverb) + " " + verb + " du " + remainder;
    }

    @Override
    public String getDescriptionHauptsatz() {
        return "Du " + verb + " " + remainder;
    }

    /**
     * Gibt etwas zurück wie "gehst weiter"
     */
    public String getDescriptionSatzanschlussOhneSubjekt() {
        return verb + " " + remainder;
    }
}
