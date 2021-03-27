package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Eine ungereihte Phrase, die substantivisch verwendet werden kann, also insbesondere
 * ein Pronomen ("sie", "du", "wir") oder eine (andere) Nominalphrase ("die goldene Kugel"),
 * die eine Fokuspartikel umfassen kann.
 */
public abstract class EinzelneSubstantivischePhraseMitOptFokuspartikel implements
        EinzelneSubstantivischePhrase {
    /**
     * Etwas hinzu wie "auch", "allein", "ausgerechnet", "wenigstens" etc.
     */
    @Nullable
    private final String fokuspartikel;

    private final NumerusGenus numerusGenus;

    /**
     * Eine Person, ein Gegenstand, ein Konzept o.Ä., auf das sich diese substantivische
     * Phrase bezieht.
     */
    @Nullable
    private final IBezugsobjekt bezugsobjekt;

    EinzelneSubstantivischePhraseMitOptFokuspartikel(@Nullable final String fokuspartikel,
                                                     final NumerusGenus numerusGenus,
                                                     @Nullable final IBezugsobjekt bezugsobjekt) {
        checkArgument(fokuspartikel == null || !fokuspartikel.isEmpty(),
                "Fokuspartikel ist Leerstring");

        this.fokuspartikel = fokuspartikel;
        this.numerusGenus = numerusGenus;
        this.bezugsobjekt = bezugsobjekt;
    }

    /**
     * Gibt diese substantivischen Phrase ohne Fokuspartikel zurück
     */
    @Override
    public SubstantivischePhrase ohneFokuspartikel() {
        if (fokuspartikel == null) {
            return this;
        }

        return mitFokuspartikel(null);
    }

    @Override
    @Nullable
    public String getFokuspartikel() {
        return fokuspartikel;
    }

    @Override
    @Nullable
    public IBezugsobjekt getBezugsobjekt() {
        return bezugsobjekt;
    }

    @Override
    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EinzelneSubstantivischePhraseMitOptFokuspartikel
                that = (EinzelneSubstantivischePhraseMitOptFokuspartikel) o;
        return Objects.equals(fokuspartikel, that.fokuspartikel) &&
                numerusGenus == that.numerusGenus &&
                Objects.equals(bezugsobjekt, that.bezugsobjekt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fokuspartikel, numerusGenus, bezugsobjekt);
    }
}
