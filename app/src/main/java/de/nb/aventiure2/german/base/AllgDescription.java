package de.nb.aventiure2.german.base;

import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.AbstractDescription;

/**
 * A general description. The subject may be anything.
 */
public class AllgDescription implements AbstractDescription {
    /**
     * Something like "Der Weg führt weiter in den Wald hinein. Dann stehst du vor einer Kirche"
     */
    private final String description;

    private final boolean kommaStehtAus;

    private final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt;

    private final boolean dann;

    private final AvTimeSpan timeElapsed;

    public static AllgDescription allg(final String description,
                                       final boolean kommaStehtAus,
                                       final boolean
                                               allowsAdditionalDuSatzreihengliedOhneSubjekt,
                                       final boolean dann,
                                       final AvTimeSpan timeElapsed) {
        return new AllgDescription(description,
                kommaStehtAus,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann, timeElapsed);
    }

    private AllgDescription(final String description,
                            final boolean kommaStehtAus,
                            final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                            final boolean dann,
                            final AvTimeSpan timeElapsed) {
        this.description = description;
        this.kommaStehtAus = kommaStehtAus;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
        this.timeElapsed = timeElapsed;
    }

    @Override
    public String getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
            final String konjunktionaladverb) {
        // Konjunktionaladverb ist in diesen Fällen nicht nötig:
        // "Du gehst in den Wald. Der Weg führt an einem Bach entlang."
        return getDescriptionHauptsatz();
    }

    @Override
    public String getDescriptionHauptsatz() {
        return description;
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
