package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Einzelne konkrete topologischen Felder (Mittelfeld, Nachfeld, ...), aus denen ein Satz
 * zusammengesetzt werden kann.
 */
public class TopolFelder {
    static final TopolFelder EMPTY = new TopolFelder(Mittelfeld.EMPTY, Nachfeld.EMPTY);

    private final Mittelfeld mittelfeld;

    private final Nachfeld nachfeld;

    public TopolFelder(final Mittelfeld mittelfeld, final Nachfeld nachfeld) {
        this.mittelfeld = mittelfeld;
        this.nachfeld = nachfeld;
    }

    public Mittelfeld getMittelfeld() {
        return mittelfeld;
    }

    public Nachfeld getNachfeld() {
        return nachfeld;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TopolFelder that = (TopolFelder) o;
        return mittelfeld.equals(that.mittelfeld) && nachfeld.equals(that.nachfeld);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mittelfeld, nachfeld);
    }

    @NonNull
    @Override
    public String toString() {
        return "[mittelfeld=\"" + mittelfeld +
                "\", nachfeld=\"" + nachfeld +
                "\"]";
    }
}
