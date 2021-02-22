package de.nb.aventiure2.german.base;

import androidx.annotation.Nullable;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Eine ungereihte Phrase, die substantivisch verwendet werden kann, also insbesondere
 * ein Pronomen ("sie", "du", "wir") oder eine (andere) Nominalphrase ("die goldene Kugel").
 */
public abstract class EinzelneSubstantivischePhrase extends SubstantivischePhrase {
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

    EinzelneSubstantivischePhrase(@Nullable final String fokuspartikel,
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
    Konstituentenfolge artikellosDatK() {
        return joinToKonstituentenfolge(
                k(artikellosDatStr(), getNumerusGenus(), getBezugsobjekt()));
    }

    @Override
    public final Konstituentenfolge imK(
            final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        return joinToKonstituentenfolge(
                k(imStr(kasusOderPraepositionalkasus), kannAlsBezugsobjektVerstandenWerdenFuer(),
                        getBezugsobjekt()));
    }

    @Nullable
    @Override
    public NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer() {
        if (getPerson() != P3) {
            return null;
        }

        return numerusGenus;
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
    public Numerus getNumerus() {
        return getNumerusGenus().getNumerus();
    }

    @Override
    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }

    public SubstPhrReihung und(final SubstantivischePhrase other) {
        return new SubstPhrReihung(this, other);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EinzelneSubstantivischePhrase that = (EinzelneSubstantivischePhrase) o;
        return Objects.equals(fokuspartikel, that.fokuspartikel) &&
                numerusGenus == that.numerusGenus &&
                Objects.equals(bezugsobjekt, that.bezugsobjekt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fokuspartikel, numerusGenus, bezugsobjekt);
    }
}
