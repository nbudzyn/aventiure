package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.german.base.Person.P3;

/**
 * Ein Kandidat, auf den sich ein Pronomen beziehen könnte, insbesondere ein Personalpronomen.
 * Anders gesagt: Ein Referent.
 * <br>
 * Beispiel: In "Ich sehe das Mödchen." wäre "das Mödchen" ein solcher Kandidat.
 * <br>
 * Relevant bei einem <code>PhorikKandidat</code>en sind zwei Dinge:
 * <ol>
 *     <li>Die grammatikalischen Kategorien Numerus und Genus
 *     <li>Das Objekt als solches (auf das man sich beziehen könnte)
 * </ol>
 * <p>
 * Wir unterstützen noch Phorikkandiaten in der dritten Person.
 */
@Immutable
public class PhorikKandidat {
    private final NumerusGenus numerusGenus;

    private final IBezugsobjekt bezugsobjekt;

    /**
     * Konstruktor. Wir unterstützen nur Phorikkandidaten in der dritten Person.
     */
    public PhorikKandidat(final NumerusGenus numerusGenus,
                          final IBezugsobjekt bezugsobjekt) {
        checkNotNull(numerusGenus, "numerusGenus ist null");
        checkNotNull(bezugsobjekt, "bezugsobjekt ist null");

        this.numerusGenus = numerusGenus;
        this.bezugsobjekt = bezugsobjekt;
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer (rückgreifender) Bezug auf dieses
     * andere Objekt möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     */
    @Nullable
    public static Personalpronomen getAnaphPersPronWennMgl(
            @Nullable final PhorikKandidat phorikKandidat, final IBezugsobjekt other) {
        if (phorikKandidat == null) {
            return null;
        }

        return phorikKandidat.getAnaphPersPronWennMgl(other);
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer (rückgreifender) Bezug auf dieses
     * andere Objekt möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     */
    @Nullable
    private Personalpronomen getAnaphPersPronWennMgl(final IBezugsobjekt other) {
        if (!getBezugsobjekt().equals(other)) {
            return null;
        }

        return Personalpronomen.get(P3, getNumerusGenus(), bezugsobjekt);
    }

    /**
     * Ob ein anaphorischer Bezug (z.B. mit einem Personalpronomen) auf dieses
     * andere Objekt möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe mittels des Personalpronomens "sie" möglich:
     * "... und nimmst sie mit."
     */
    public static boolean isAnaphorischerBezugMoeglich(
            @Nullable final PhorikKandidat phorikKandidat, final IBezugsobjekt other) {
        if (phorikKandidat == null) {
            return false;
        }

        return phorikKandidat.getBezugsobjekt().equals(other);
    }

    /**
     * Ob ein anaphorischer Bezug (z.B. mit einem Personalpronomen) auf dieses
     * andere Objekt möglich ist.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe mittels des Personalpronomens "sie" möglich:
     * "... und nimmst sie mit."
     */
    private boolean isAnaphorischerBezugMoeglich(final IBezugsobjekt other) {
        return getBezugsobjekt().equals(other);
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
