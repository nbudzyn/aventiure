package de.nb.aventiure2.german.base;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.Person.P3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

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

    private final Belebtheit belebtheit;

    private final IBezugsobjekt bezugsobjekt;

    /**
     * Konstruktor. Wir unterstützen nur Phorikkandidaten in der dritten Person.
     */
    public PhorikKandidat(final NumerusGenus numerusGenus,
                          final Belebtheit belebtheit,
                          final IBezugsobjekt bezugsobjekt) {
        requireNonNull(numerusGenus, "numerusGenus ist null");
        requireNonNull(belebtheit, "belebtheit ist null");
        requireNonNull(bezugsobjekt, "bezugsobjekt ist null");

        this.numerusGenus = numerusGenus;
        this.belebtheit = belebtheit;
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
            @Nullable final PhorikKandidat phorikKandidat, final IBezugsobjekt etwas) {
        @Nullable final NumerusGenus numerusGenusAnaphWennMgl =
                getNumerusGenusAnaphWennMgl(phorikKandidat, etwas);
        if (numerusGenusAnaphWennMgl == null) {
            return null;
        }

        return Personalpronomen.get(P3, numerusGenusAnaphWennMgl,
                phorikKandidat.belebtheit, phorikKandidat.bezugsobjekt);
    }

    /**
     * Gibt Numerus und Genus zurück, mit denen ein
     * anaphorischer (rückgreifender) Bezug auf das
     * Objekt möglich ist, sonst {@code null}.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt Singular Femininum zurück.
     */
    @Nullable
    public static NumerusGenus getNumerusGenusAnaphWennMgl(
            @Nullable final PhorikKandidat phorikKandidat, final IBezugsobjekt etwas) {
        if (phorikKandidat == null) {
            return null;
        }

        return phorikKandidat.getNumerusGenusAnaphWennMgl(etwas);
    }

    /**
     * Gibt Numerus und Genus zurück, mit denen ein
     * anaphorischer (rückgreifender) Bezug auf das
     * Objekt möglich ist, sonst {@code null}.
     * <br/>
     * Beispiel: "Du hebst du Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt Singular Femininum zurück.
     */
    @Nullable
    private NumerusGenus getNumerusGenusAnaphWennMgl(final IBezugsobjekt etwas) {
        if (!getBezugsobjekt().equals(etwas)) {
            return null;
        }

        return getNumerusGenus();
    }

    public NumerusGenus getNumerusGenus() {
        return numerusGenus;
    }

    public Belebtheit getBelebtheit() {
        return belebtheit;
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
        return numerusGenus == that.numerusGenus
                && belebtheit == that.belebtheit
                && bezugsobjekt.equals(that.bezugsobjekt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerusGenus, belebtheit, bezugsobjekt);
    }

    @Override
    @NonNull
    public String toString() {
        return "PhorikKandidat{" +
                "numerusGenus=" + numerusGenus +
                ", belebtheit=" + belebtheit +
                ", bezugsobjekt=" + bezugsobjekt +
                '}';
    }
}
