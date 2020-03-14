package de.nb.aventiure2.german;

/**
 * A general description. The subject may be anything.
 */
public class AllgDescription implements AbstractDescription {
    /**
     * Something like "Der Weg führt weiter in den Wald hinein. Dann stehst du vor einer Kirche"
     */
    private final String description;

    private final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt;

    private final boolean dann;

    public static AllgDescription allg(final String description,
                                       final boolean
                                               allowsAdditionalDuSatzreihengliedOhneSubjekt,
                                       final boolean dann) {
        return new AllgDescription(description,
                allowsAdditionalDuSatzreihengliedOhneSubjekt, dann);
    }

    private AllgDescription(final String description,
                            final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                            final boolean dann) {
        this.description = description;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
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
    public boolean allowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    @Override
    public boolean dann() {
        return dann;
    }
}
