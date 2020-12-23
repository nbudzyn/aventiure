package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;

import javax.annotation.concurrent.Immutable;

/**
 * Eine Folge von Wörter, Satzzeichen, wie sind in einem Text vorkommen könnte.
 */
@Immutable
public class Wortfolge {
    /**
     * Die eigentlichen Wörter und Satzzeichen
     */
    private final String string;

    /**
     * Ob noch ein Komma aussteht. Das Komma wird entweder unmittelbar folgen müssen -
     * oder es folgt ein Punkt, ein Ausrufezeichen, ein Fragezeichen, ein Doppelpunkt oder
     * ein Semikolon, der ebenfalls das Komma "abdeckt".
     */
    private final boolean kommmaStehtAus;

    /**
     * Erzeugt eine Wortfolge, bei der kein Komma aussteht.
     */
    public static Wortfolge w(final String string) {
        return w(string, false);
    }

    public static Wortfolge w(final String string, final boolean kommaStehtAus) {
        return new Wortfolge(string, kommaStehtAus);
    }

    private Wortfolge(final String string, final boolean kommmaStehtAus) {
        this.string = string;
        this.kommmaStehtAus = kommmaStehtAus;
    }

    public String getString() {
        return string;
    }

    public boolean kommmaStehtAus() {
        return kommmaStehtAus;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString()
                + ": "
                + getString()
                + (kommmaStehtAus ? "[, ]" : "");
    }
}
