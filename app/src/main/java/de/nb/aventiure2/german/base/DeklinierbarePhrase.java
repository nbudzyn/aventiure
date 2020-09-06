package de.nb.aventiure2.german.base;

import java.util.Objects;

/**
 * Eine Phrase, die dekliniert werden kann.
 */
public abstract class DeklinierbarePhrase {
    private final NumerusGenus numerusGenus;

    public DeklinierbarePhrase(final NumerusGenus numerusGenus) {
        this.numerusGenus = numerusGenus;
    }

    public String im(final Kasus kasus) {
        switch (kasus) {
            case NOM:
                return nom();
            case DAT:
                return dat();
            case AKK:
                return akk();
            default:
                throw new IllegalArgumentException("Unexpected kasus: " + kasus);
        }
    }

    public abstract String nom();

    public abstract String dat();

    public abstract String akk();

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
        final DeklinierbarePhrase that = (DeklinierbarePhrase) o;
        return numerusGenus == that.numerusGenus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerusGenus);
    }
}
