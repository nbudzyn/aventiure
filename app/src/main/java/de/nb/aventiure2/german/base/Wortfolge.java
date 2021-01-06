package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
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
     * Ob die wörtliche Rede noch "offen" ist.  Es steht also noch ein schließendes
     * Anführungszeichen aus. Wenn der Satz beendet wird, muss vielleicht außerdem
     * noch ein Punkt nach dem Anführungszeitchen gesetzt werden.
     */
    private final boolean woertlicheRedeNochOffen;

    /**
     * Ob noch ein Komma aussteht. Das Komma wird entweder unmittelbar folgen müssen -
     * oder es folgt ein Punkt, ein Ausrufezeichen, ein Fragezeichen, ein Doppelpunkt oder
     * ein Semikolon, der ebenfalls das Komma "abdeckt".
     */
    private final boolean kommmaStehtAus;

    /**
     * Fügt diese Teile zu einer Wortfolge zusammen, wobei eine nichtleere
     * Wortfolge das Ergebnis sein muss. Berücksichtigt auch die Information,
     * ob ein Kommma aussteht.
     */
    public static Wortfolge joinToWortfolge(final Object... parts) {
        return checkJoiningResultNotNull(joinToNullWortfolge(parts), parts);
    }

    static <R> R checkJoiningResultNotNull(
            @Nullable final R joiningResult,
            final Object... parts) {
        return checkJoiningResultNotNull(joiningResult, asList(parts));
    }

    private static <R> R checkJoiningResultNotNull(
            @Nullable final R joiningResult,
            final Iterable<?> parts) {
        if (joiningResult == null) {
            throw new IllegalStateException("Joining result was null. parts: " + parts);
        }

        return joiningResult;
    }

    /**
     * Fügt diese Teile zu mehreren alternativen Wortfolgen zusammen.
     *
     * @return Mehrere alternative Wortfolgen - die Collection kann statt einer
     * Wortfolgen auch <code>null</code> enthalten.
     */
    @Nonnull
    public static Collection<Wortfolge> joinToAltWortfolgen(final Object... parts) {
        return joinToAltWortfolgen(asList(parts));
    }

    /**
     * Fügt diese Teile zu mehreren alternativen Wortfolgen zusammen.
     *
     * @return Mehrere alternative Wortfolgen - die Collection kann statt einer
     * Wortfolgen auch <code>null</code> enthalten.
     */
    @Nonnull
    private static Collection<Wortfolge> joinToAltWortfolgen(final Iterable<?> parts) {
        @Nullable final Collection<Konstituentenfolge> konstituentenfolge =
                Konstituentenfolge.joinToAltKonstituentenfolgen(parts);

        return konstituentenfolge.stream()
                .map(k -> k != null ? joinToWortfolge(k) : null)
                .collect(Collectors.toSet());
    }

    /**
     * Fügt diese Teile zu einer Wortfolge zusammen - berücksichtigt auch die Information,
     * ob ein Komma aussteht.
     * <p>
     * Sollte für den ersten der Parts angegeben sein, dass ein Vorkommma nötig ist,
     * wird <i>keines</i> erzeugt.
     */
    @Nullable
    public static Wortfolge joinToNullWortfolge(final Object... parts) {
        return joinToNullWortfolge(asList(parts));
    }

    /**
     * Fügt diese Teile zu einer Wortfolge zusammen - einschließlich der Information,
     * ob ein Komma aussteht.
     * <p>
     * Sollte für den ersten der Parts angegeben sein, dass ein Vorkommma nötig ist,
     * wird <i>keines</i> erzeugt.
     */
    @Nullable
    static Wortfolge joinToNullWortfolge(final Iterable<?> parts) {
        @Nullable final Konstituentenfolge konstituentenfolge =
                Konstituentenfolge.joinToNullKonstituentenfolge(parts);
        if (konstituentenfolge == null) {
            return null;
        }

        return joinToWortfolge(konstituentenfolge);
    }

    /**
     * Fügt diese Konstituentenfolge zu einer Wortfolge zusammen
     */
    public static Wortfolge joinToWortfolge(final Konstituentenfolge konstituentenfolge) {
        final StringBuilder resString =
                new StringBuilder(konstituentenfolge.size() * 25);
        boolean first = true;
        boolean woertlicheRedeNochOffen = false;
        boolean kommaStehtAus = false;
        for (final Konstituente konstituente : konstituentenfolge) {
            final String konstitentenString = konstituente.getString();
            if (woertlicheRedeNochOffen) {
                if (resString.toString().trim().endsWith(".")) {
                    resString.append("“");
                    if (GermanUtil.spaceNeeded("“", konstitentenString)) {
                        resString.append(" ");
                    }
                } else if (!konstitentenString.trim().startsWith(".“")
                        && !konstitentenString.trim().startsWith("!“")
                        && !konstitentenString.trim().startsWith("?“")
                        && !konstitentenString.trim().startsWith("…“")
                        // Kein Satzende
                        && !konstitentenString.trim().startsWith("“")) {
                    resString.append("“");
                    if (GermanUtil.spaceNeeded("“", konstitentenString)) {
                        resString.append(" ");
                    }
                }
            }

            if ((kommaStehtAus
                    || (!first && konstituente.vorkommaNoetig()))
                    && !GermanUtil.beginnDecktKommaAb(konstitentenString)) {
                resString.append(",");
                if (GermanUtil.spaceNeeded(",", konstitentenString)) {
                    resString.append(" ");
                }
            } else if (GermanUtil.spaceNeeded(resString, konstitentenString)) {
                resString.append(" ");
            }

            resString.append(konstitentenString);
            kommaStehtAus = konstituente.kommaStehtAus();
            woertlicheRedeNochOffen = konstituente.woertlicheRedeNochOffen();
            first = false;
        }

        return w(resString.toString(), woertlicheRedeNochOffen, kommaStehtAus);
    }

    /**
     * Erzeugt eine Wortfolge, bei der kein Komma aussteht - null-safe.
     */
    @Nullable
    public static Wortfolge w(final @Nullable String string) {
        if (string == null) {
            return null;
        }

        return w(string, false, false);
    }

    public static Wortfolge w(final String string, final boolean woertlicheRedeNochOffen,
                              final boolean kommaStehtAus) {
        return new Wortfolge(string, woertlicheRedeNochOffen, kommaStehtAus);
    }

    private Wortfolge(final String string, final boolean woertlicheRedeNochOffen,
                      final boolean kommmaStehtAus) {
        this.string = string;
        this.woertlicheRedeNochOffen = woertlicheRedeNochOffen;
        this.kommmaStehtAus = kommmaStehtAus;
    }

    @NonNull
    String toStringFixWoertlicheRedeNochOffen() {
        final StringBuilder resString = new StringBuilder(getString());

        if (woertlicheRedeNochOffen()) {
            resString.append("“");
        }

        return resString.toString();
    }


    public String getString() {
        return string;
    }

    public boolean woertlicheRedeNochOffen() {
        return woertlicheRedeNochOffen;
    }

    public boolean kommaStehtAus() {
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

        // Wir prüfen kommaStehtAus nicht mit. Im Kontext von
        // cut...() könnte vielleicht so nötig sein...

        return Objects.equals(string, wortfolge.string);
    }

    @Override
    public int hashCode() {
        // kommaStehtAus nicht mitverwenden - damit hashCode() konsistent mit
        // equals() wird.

        return Objects.hash(string);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString()
                + ": "
                + getString()
                + (woertlicheRedeNochOffen ? "[“]" : "")
                + (kommmaStehtAus ? "[, ]" : "");
    }
}
