package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import javax.annotation.concurrent.Immutable;

import static java.util.Arrays.asList;

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


    public static Wortfolge uncapitalize(final Wortfolge wortfolge) {
        return w(GermanUtil.uncapitalize(wortfolge.getString()), wortfolge.kommmaStehtAus());
    }

    /**
     * Fügt diese Teile zu einem String zusammen - berücksichtigt auch die Information,
     * ob ein Komma aussteht.
     *
     * @see Wortfolge
     */
    @Nullable
    public static Wortfolge joinToNullWortfolge(final Object... parts) {
        return joinToNullWortfolge(asList(parts));
    }

    /**
     * Fügt diese Teile zu einer Wortfolge zusammen - einschließlich der Information,
     * ob ein Komma aussteht.
     * <p>
     * Sollte für den ersten der parts angegeben sein, dass ein Vorkommma nötig ist,
     * wird <i>keines</i> erzeugt.
     */
    @Nullable
    static Wortfolge joinToNullWortfolge(final Iterable<?> parts) {
        final StringBuilder resString = new StringBuilder();
        boolean first = true;
        boolean kommaStehtAus = false;
        for (final Konstituente konstituente : Konstituente.joinToKonstituenten(parts)) {
            if ((kommaStehtAus
                    || (!first && konstituente.vorkommmaNoetig()))
                    && !GermanUtil.beginnDecktKommaAb(konstituente.getString())) {
                resString.append(",");
                if (GermanUtil.spaceNeeded(",", konstituente.getString())) {
                    resString.append(" ");
                }
            } else if (GermanUtil.spaceNeeded(resString, konstituente.getString())) {
                resString.append(" ");
            }

            resString.append(konstituente.getString());
            kommaStehtAus = konstituente.kommaStehtAus();
            first = false;
        }

        if (resString.length() == 0) {
            return null;
        }

        return w(resString.toString(), kommaStehtAus);
    }

    /**
     * Erzeugt eine Wortfolge, bei der kein Komma aussteht - null-safe.
     */
    @Nullable
    public static Wortfolge w(final @Nullable String string) {
        if (string == null) {
            return null;
        }

        return w(string, false);
    }

    public static Wortfolge w(final String string, final boolean kommaStehtAus) {
        return new Wortfolge(string, kommaStehtAus);
    }

    private Wortfolge(final String string, final boolean kommmaStehtAus) {
        this.string = string;
        this.kommmaStehtAus = kommmaStehtAus;
    }

    public Wortfolge capitalize() {
        return w(GermanUtil.capitalize(getString()), kommmaStehtAus());
    }

    public String getString() {
        return string;
    }

    public boolean kommmaStehtAus() {
        return kommmaStehtAus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Wortfolge wortfolge = (Wortfolge) o;
        return kommmaStehtAus == wortfolge.kommmaStehtAus &&
                Objects.equals(string, wortfolge.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string, kommmaStehtAus);
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
