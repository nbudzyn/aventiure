package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Ein Kandidat, auf den sich ein Pronomen beziehen könnte, insbesondere ein Personalpronomen.
 * <br>
 * Beispiel: In "Ich sehe das Mödchen." wäre "das Mödchen" ein solcher Kandidat.
 * <br>
 * Relevant bei einem <code>PhorikKandidat</code>en sind zwei Dinge:
 * <ol>
 *     <li>Die grammatikalischen Kategorien Numerus und Genus
 *     <li>Das Objekt als solches (auf das man sich beziehen könnte)
 * </ol>
 */
public class PhorikKandidat {
    private final NumerusGenus numerusGenus;

    private final IBezugsobjekt bezugsobjekt;

    public PhorikKandidat(final NumerusGenus numerusGenus,
                          final IBezugsobjekt bezugsobjekt) {
        this.numerusGenus = numerusGenus;
        this.bezugsobjekt = bezugsobjekt;
    }

    /**
     * Ob ein Bezug (z.B. mit einem Personalpronomen) möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe mittels des Personalpronomens "sie" möglich:
     * "... und nimmst sie mit."
     */
    public boolean isBezugMoeglich(final NumerusGenus numerusGenus,
                                   final IBezugsobjekt bezugsobjekt) {
        return getNumerusGenus() == numerusGenus &&
                getBezugsobjekt().equals(bezugsobjekt);
    }

    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }

    public IBezugsobjekt getBezugsobjekt() {
        return bezugsobjekt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PhorikKandidat that = (PhorikKandidat) o;
        return numerusGenus == that.numerusGenus &&
                bezugsobjekt.equals(that.bezugsobjekt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerusGenus, bezugsobjekt);
    }

    @Override
    @NonNull
    public String toString() {
        return "PhorikKandidat{" +
                "numerusGenus=" + numerusGenus +
                ", bezugsobjekt=" + bezugsobjekt +
                '}';
    }
}
