package de.nb.aventiure2.german;

import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

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

    /**
     * Ob ein Komma aussteht. Wenn ein Komma aussteht, muss als Nächstes ein Komma folgen -
     * oder das Satzende.
     */
    private final boolean kommaStehtAus;

    private final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt;

    private final boolean dann;

    private final AvTimeSpan timeElapsed;

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
                          final boolean kommmaStehtAus,
                          final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                          final boolean dann,
                          final AvTimeSpan timeElapsed) {
        this.verb = verb;
        this.remainder = remainder;
        kommaStehtAus = kommmaStehtAus;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        this.timeElapsed = timeElapsed;
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
    public boolean kommaStehtAus() {
        return kommaStehtAus;
    }

    @Override
    public boolean allowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    @Override
    public boolean dann() {
        return dann;
    }

    @Override
    public AvTimeSpan getTimeElapsed() {
        return timeElapsed;
    }
}
