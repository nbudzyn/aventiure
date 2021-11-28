package de.nb.aventiure2.german.base;

import static de.nb.aventiure2.german.base.Belebtheit.UNBELEBT;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P3;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * Die grammatischen Merkmale, mit denen (in der Regel) das Subjekt das Prädikat regiert.
 */
public class PraedRegMerkmale {
    private final Person person;
    private final Numerus numerus;
    private final Belebtheit belebtheit;

    public PraedRegMerkmale(final Person person, final Numerus numerus,
                            final Belebtheit belebtheit) {
        this.person = person;
        this.numerus = numerus;
        this.belebtheit = belebtheit;
    }

    public void checkExpletivesEs() {
        if (person != P3) {
            throw new IllegalStateException(
                    "Ungültige Person, kein expletives es: " + person);
        }

        if (numerus != SG) {
            throw new IllegalStateException(
                    "Ungültiger Numerus, kein expletives es: " + numerus);
        }

        if (belebtheit != UNBELEBT) {
            throw new IllegalStateException(
                    "Ungültige Belebtheit, kein expletives es: " + belebtheit);
        }
    }

    public Person getPerson() {
        return person;
    }

    public Numerus getNumerus() {
        return numerus;
    }

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
        final PraedRegMerkmale that = (PraedRegMerkmale) o;
        return person == that.person && numerus == that.numerus && belebtheit == that.belebtheit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, numerus, belebtheit);
    }

    @Override
    @Nonnull
    public String toString() {
        return person + " " + numerus + " (" + belebtheit + ")";
    }
}
