package de.nb.aventiure2.german;

import static de.nb.aventiure2.german.GermanUtil.capitalize;

/**
 * A description - assuming the player character is the (first) subject. Somehting like
 * "Du gehts in den Wald."
 */
public class DuDescription implements AbstractDescription {
    /**
     * Something like "gehst"
     */
    private final String verb;
    /**
     * Something like "in den Wald"
     */
    private final String remainder;

    private final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt;

    private final boolean dann;

    public static DuDescription du(final String verb, final String remainder,
                                   final boolean
                                           allowsAdditionalDuSatzreihengliedOhneSubjekt,
                                   final boolean dann) {
        return new DuDescription(verb, remainder,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann);
    }

    private DuDescription(final String verb, final String remainder,
                          final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                          final boolean dann) {
        this.verb = verb;
        this.remainder = remainder;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
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

    @Override
    public boolean allowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    @Override
    public boolean dann() {
        return dann;
    }
}
