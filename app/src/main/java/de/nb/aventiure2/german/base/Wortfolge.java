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
    // FIXME Verwendungen suchen, ggf. entfernen?!
    public static Wortfolge joinToNullWortfolge(final Object... parts) {
        return joinToNullWortfolge(asList(parts));
    }

    /**
     * Fügt diese Teile zu einem String zusammen - einschließlich der Information,
     * ob ein Komma aussteht.
     *
     * @see Wortfolge
     */
    // FIXME Verwendungen suchen, ggf. durch joinToKonstituenten() ersetzen?
    @Nullable
    static Wortfolge joinToNullWortfolge(final Iterable<?> parts) {
        final StringBuilder resString = new StringBuilder();
        boolean kommaStehtAus = false;
        for (final Konstituente konstituente : Konstituente.joinToKonstituenten(parts)) {
            final Wortfolge partWortfolge = konstituente.toWortfolge();

            if (kommaStehtAus && !GermanUtil.beginnDecktKommaAb(partWortfolge.getString())) {
                resString.append(",");
                if (GermanUtil.spaceNeeded(",", partWortfolge.getString())) {
                    resString.append(" ");
                }
            } else if (GermanUtil.spaceNeeded(resString, partWortfolge.getString())) {
                resString.append(" ");
            }

            resString.append(partWortfolge.getString());
            kommaStehtAus = partWortfolge.kommmaStehtAus();
        }

        if (resString.length() == 0) {
            return null;
        }

        return w(resString.toString(), kommaStehtAus);
    }

    /**
     * Schneidet das Satzglied (einmalig) aus diesem Text. Die Suche nach
     * dem Satzglied beginnt von vorn.
     */
    public static @Nullable
    Wortfolge cutFirst(@Nullable final Wortfolge text,
                       @Nullable final String satzglied) {
        if (text == null) {
            if (satzglied != null) {
                throw new IllegalArgumentException(
                        "Text null, but Satzglied was \"" + satzglied + "\".");
            }

            return null;
        }

        if (satzglied == null) {
            return text;
        }

        // FIXME Hier gibt es ernste Probleme. Grob gesagt:
        //  - Es könnte zu falscher Zeichensetzung ", , " o.Ä. kommmen.
        //  - Wenn ein Satzglied entfernt wird, weiß man in einigen Fällen nicht, ob
        //   Kommata vor oder nach dem Satzglied erhalten bleiben müssen oder nicht.
        //  Die richtige Lösung wäre vermutlich, dass die Wortfolge nicht einfach nur einen
        //  String speichert, sondern ihre einzelnen Satzglieder - und zu jedem Satzglied
        //  auch noch die Information, ob danach ein Komma aussteht.
        //  Vielleicht sollte man auch Differenzieren zwischen der Wortfolge und dem
        //  "Mittelfeld", das seine Satzglieder kennt...
        return w(GermanUtil.cutFirst(text.getString(), satzglied), text.kommmaStehtAus());
    }

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
