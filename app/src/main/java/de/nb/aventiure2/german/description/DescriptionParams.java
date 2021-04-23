package de.nb.aventiure2.german.description;

import java.util.Objects;

/**
 * Parameter einer {@link AbstractDescription} - mutable!
 */
class DescriptionParams {
    /**
     * Whether the narration can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject (such as " und gehst durch die Tür.")
     */
    private boolean allowsAdditionalDuSatzreihengliedOhneSubjekt;
    private boolean dann;

    /**
     * Ob die {@link AbstractDescription} (genauer: ihr Anfang) etwas beschreibt, das
     * schon schon eine Zeit lang vorliegt. Also nichts, was auf einmal passiert oder getan wird.
     * Wenn etwas schon eine Zeit lang vorliegt, wird ein Satzanschluss mit "Dann..." verhindert.
     */
    private boolean schonLaenger;

    DescriptionParams copy() {
        return new DescriptionParams(schonLaenger, allowsAdditionalDuSatzreihengliedOhneSubjekt,
                dann);
    }

    DescriptionParams() {
        this(false, false, false);
    }

    DescriptionParams(final boolean schonLaenger,
                      final boolean allowsAdditionalDuSatzreihengliedOhneSubjekt,
                      final boolean dann) {
        this.schonLaenger = schonLaenger;
        this.allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalDuSatzreihengliedOhneSubjekt;
        this.dann = dann;
    }

    /**
     * Sets a flag whether the text can be continued by a Satzreihenglied without subject where
     * the player character is the implicit subject
     */
    public void undWartest(
            final boolean allowsAdditionalPlayerSatzreihengliedOhneSubjekt) {
        allowsAdditionalDuSatzreihengliedOhneSubjekt =
                allowsAdditionalPlayerSatzreihengliedOhneSubjekt;
    }

    boolean isAllowsAdditionalDuSatzreihengliedOhneSubjekt() {
        return allowsAdditionalDuSatzreihengliedOhneSubjekt;
    }

    void dann(final boolean dann) {
        this.dann = dann;
    }

    boolean isDann() {
        return dann;
    }

    void schonLaenger(final boolean schonLaenger) {
        this.schonLaenger = schonLaenger;
    }

    boolean isSchonLaenger() {
        return schonLaenger;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DescriptionParams that = (DescriptionParams) o;
        return allowsAdditionalDuSatzreihengliedOhneSubjekt
                == that.allowsAdditionalDuSatzreihengliedOhneSubjekt &&
                dann == that.dann &&
                schonLaenger == that.schonLaenger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowsAdditionalDuSatzreihengliedOhneSubjekt, dann, schonLaenger);
    }
}