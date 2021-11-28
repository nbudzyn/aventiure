package de.nb.aventiure2.german.base;

import static com.google.common.base.Preconditions.checkArgument;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Eine ungereihte Phrase, die substantivisch verwendet werden kann, also insbesondere
 * ein Pronomen ("sie", "du", "wir") oder eine (andere) Nominalphrase ("die goldene Kugel"),
 * die eine Fokuspartikel oder auch eine {@link Negationspartikelphrase} umfassen kann.
 */
public abstract class EinzelneKomplexeSubstantivischePhrase implements
        EinzelneSubstantivischePhrase {
    /**
     * Etwas hinzu wie "auch", "allein", "ausgerechnet", "wenigstens" etc.
     */
    @Nullable
    private final String fokuspartikel;

    /**
     * Eine Phrase, deren Kern die Negationspartikel "nicht" ist und die
     * diese substantivische Phrase negiert. Sie kann direkt verwendet werden
     * ("überhaupt nicht mehr dieser Mann") oder in ein negativ-indefinites Artikelwort
     * übertragen ("überhaupt kein Verdächtiger mehr").
     */
    @Nullable
    private final Negationspartikelphrase negationspartikelphrase;

    private final NumerusGenus numerusGenus;

    /**
     * Eine Person, ein Gegenstand, ein Konzept o.Ä., auf das sich diese substantivische
     * Phrase bezieht.
     */
    @Nullable
    private final IBezugsobjekt bezugsobjekt;

    private final Belebtheit belebtheit;

    EinzelneKomplexeSubstantivischePhrase(
            @Nullable final String fokuspartikel,
            @Nullable final Negationspartikelphrase negationspartikelphrase,
            final NumerusGenus numerusGenus,
            final Belebtheit belebtheit,
            @Nullable final IBezugsobjekt bezugsobjekt) {
        checkArgument(fokuspartikel == null || !fokuspartikel.isEmpty(),
                "Fokuspartikel ist Leerstring");

        this.fokuspartikel = fokuspartikel;
        this.negationspartikelphrase = negationspartikelphrase;
        this.numerusGenus = numerusGenus;
        this.bezugsobjekt = bezugsobjekt;
        this.belebtheit = belebtheit;
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
    public Negationspartikelphrase getNegationspartikelphrase() {
        return negationspartikelphrase;
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
    public Belebtheit getBelebtheit() {
        return belebtheit;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EinzelneKomplexeSubstantivischePhrase
                that = (EinzelneKomplexeSubstantivischePhrase) o;
        return Objects.equals(fokuspartikel, that.fokuspartikel)
                && Objects.equals(negationspartikelphrase, that.negationspartikelphrase)
                && numerusGenus == that.numerusGenus
                && Objects.equals(bezugsobjekt, that.bezugsobjekt)
                && belebtheit == that.belebtheit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fokuspartikel, negationspartikelphrase, numerusGenus, bezugsobjekt,
                belebtheit);
    }
}
