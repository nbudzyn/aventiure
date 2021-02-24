package de.nb.aventiure2.german.description;

/**
 * Parameter einer {@link AbstractDescription} - mutable!
 */
public class DescriptionParams {
    /**
     * Whether the narration can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject (such as " und gehst durch die Tür.")
     */
    private boolean allowsAdditionalDuSatzreihengliedOhneSubjekt;
    private boolean dann;

    DescriptionParams copy() {
        return new DescriptionParams(allowsAdditionalDuSatzreihengliedOhneSubjekt, dann);
    }

    DescriptionParams() {
        this(false, false);
    }

    DescriptionParams(final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                      final boolean dann) {
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
    }

    /**
     * Sets a flag that the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public void undWartest() {
        undWartest(true);
    }

    public void undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt;
    }

    void dann(final boolean dann) {
        this.dann = dann;
    }

    boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    boolean isDann() {
        return dann;
    }

}