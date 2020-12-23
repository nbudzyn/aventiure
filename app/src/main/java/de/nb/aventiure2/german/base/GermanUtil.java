package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Wortfolge.w;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Static helper methods for the german language.
 */
public class GermanUtil {
    // Not to be called
    private GermanUtil() {
    }

    public static Wortfolge capitalize(final Wortfolge wortfolge) {
        return w(capitalize(wortfolge.getString()), wortfolge.kommmaStehtAus());
    }

    public static String capitalize(final String str) {
        if (str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static Wortfolge uncapitalize(final Wortfolge wortfolge) {
        return w(uncapitalize(wortfolge.getString()), wortfolge.kommmaStehtAus());
    }

    public static String uncapitalize(final String str) {
        if (str.isEmpty()) {
            return "";
        }

        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * Fügt diese Teile zu einem String zusammen - berücksichtigt auch die Information,
     * ob ein Komma aussteht.
     *
     * @see Wortfolge
     */
    @Nullable
    public static Wortfolge joinToNull(final Object... parts) {
        return joinToNull(asList(parts));
    }

    /**
     * Fügt diese Teile zu einem String zusammen. Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    @Nullable
    public static String joinToNullString(final Object... parts) {
        return joinToNullString(asList(parts));
    }

    /**
     * Fügt diese Teile zu einem String zusammen. Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    @Nullable
    public static String joinToNullString(final Iterable<?> parts) {
        @Nullable final Wortfolge res = joinToNull(parts);
        if (res == null) {
            return null;
        }

        return res.getString();
    }

    /**
     * Fügt diese Teile zu einem String zusammen - einschließlich der Information,
     * ob ein Komma aussteht.
     *
     * @see Wortfolge
     */
    @Nullable
    private static Wortfolge joinToNull(final Iterable<?> parts) {
        final StringBuilder resString = new StringBuilder();
        boolean kommaStehtAus = false;
        for (final Object part : parts) {
            if (part == null) {
                continue;
            }

            @Nullable final Wortfolge partWortfolge;
            if (part.getClass().isArray()) {
                partWortfolge = joinToNull((Object[]) part);
            } else if (part instanceof Iterable<?>) {
                partWortfolge = joinToNull((Iterable<?>) part);
            } else if (part instanceof Wortfolge) {
                partWortfolge = (Wortfolge) part;
            } else {
                partWortfolge = w(part.toString());
            }

            if (partWortfolge != null) {
                if (kommaStehtAus && !beginnDecktKommaAb(partWortfolge.getString())) {
                    resString.append(",");
                    if (spaceNeeded(",", partWortfolge.getString())) {
                        resString.append(" ");
                    }
                } else if (spaceNeeded(resString, partWortfolge.getString())) {
                    resString.append(" ");
                }

                resString.append(partWortfolge.getString());
                kommaStehtAus = partWortfolge.kommmaStehtAus();
            }
        }

        if (resString.length() == 0) {
            return null;
        }

        return w(resString.toString(), kommaStehtAus);
    }


    /**
     * Gibt eine Aufzählung zurück wie "der hässliche Frosch",
     * "die goldene Kugel und der hässliche Frosch" oder "das schöne Glas, die goldene Kugel und
     * der hässliche Frosch".
     */
    @NonNull
    public static String buildAufzaehlung(final List<String> elemente) {
        checkArgument(!elemente.isEmpty(), "Elemente war leer");

        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < elemente.size(); i++) {
            res.append(elemente.get(i));
            if (i == elemente.size() - 2) {
                // one before the last
                res.append(" und ");
            } else if (i < elemente.size() - 2) {
                // more than one after this
                res.append(", ");
            }
        }

        return res.toString();
    }

    public static boolean spaceNeeded(@Nullable final CharSequence base,
                                      @Nullable final CharSequence addition) {
        if (base == null || base.length() == 0 ||
                addition == null || addition.length() == 0) {
            return false;
        }

        final CharSequence lastCharBase = base.subSequence(base.length() - 1, base.length());
        if (" „\n" .contains(lastCharBase)) {
            return false;
        }

        final CharSequence firstCharAddition = addition.subSequence(0, 1);
        return !" ,;.:!?“\n" .contains(firstCharAddition);
    }

    private static boolean beginnDecktKommaAb(final CharSequence charSequence) {
        requireNonNull(charSequence, "charSequence");
        checkArgument(charSequence.length() > 0, "charSequence was empty");

        final CharSequence firstChar = charSequence.subSequence(0, 1);

        checkArgument(!"\n" .equals(firstChar), "charSequence beginnt mit "
                + "Zeilenwechsel. Hier wäre keine Möglichkeit, syntaktisch korrekt noch ein "
                + "Komma unterzubringen.");

        checkArgument(!"“" .contains(firstChar), "charSequence beginnt "
                + "mit Abführungszeichen. Hier müsste man eigentlich erst das Abführungszeichen "
                + "schreiben und dann das Komma (oder Punkt o.Ä.). Diese Logik ist noch nicht "
                + "implementiert");

        return ",;.:!?" .contains(firstChar);
    }

    /**
     * Schneidet das Satzglied (einmalig) aus diesem Text. Die Suche nach
     * dem Satzglied beginnt von vorn.
     */
    public static @Nullable
    Wortfolge cutSatzglied(@Nullable final Wortfolge text, @Nullable final String satzglied) {
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
        return w(cutSatzglied(text.getString(), satzglied), text.kommmaStehtAus());
    }

    /**
     * Schneidet das Satzglied (einmalig) aus diesem Text. Die Suche nach
     * dem Satzglied beginnt von vorn.
     */
    public static @Nullable
    String cutSatzglied(@Nullable final String text, @Nullable final String satzglied) {
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

        final int startIndex = text.indexOf(satzglied);
        if (startIndex < 0) {
            throw new IllegalArgumentException("Satzglied \"" + satzglied + "\" not contained "
                    + "in \"" + text + "\"");
        }

        return cutSatzglied(text, startIndex, satzglied.length());
    }

    /**
     * Schneidet das Satzglied (einmalig) aus diesem Text. Die Suche nach
     * dem Satzglied beginnt von vorn.
     */
    public static @Nullable
    Wortfolge cutSatzgliedVonHinten(@Nullable final Wortfolge text,
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

        // FIXME Hier gibt es ernste Probleme, s.o. Grob gesagt:
        //  - Es könnte zu falscher Zeichensetzung ", , " o.Ä. kommmen.
        //  - Wenn ein Satzglied entfernt wird, weiß man in einigen Fällen nicht, ob
        //   Kommata vor oder nach dem Satzglied erhalten bleiben müssen oder nicht.
        return w(cutSatzgliedVonHinten(text.getString(), satzglied), text.kommmaStehtAus());
    }

    /**
     * Schneidet das Satzglied (einmalig) aus diesem Text;  die Suche nach
     * dem Satzglied beginnt von hinten.
     */
    public static @Nullable
    String cutSatzgliedVonHinten(@Nullable final String text, @Nullable final String satzglied) {
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

        final int startIndex = text.lastIndexOf(satzglied);
        if (startIndex < 0) {
            throw new IllegalArgumentException("Satzglied \"" + satzglied + "\" not contained "
                    + "in \"" + text + "\"");
        }

        return cutSatzglied(text, startIndex, satzglied.length());
    }

    @Nullable
    private static String cutSatzglied(@NonNull final String text, final int startIndex,
                                       final int satzgliedLength) {
        requireNonNull(text, "text");

        @Nullable final String charBefore = startIndex == 0 ?
                null :
                text.substring(startIndex - 1, startIndex);

        final int endIndex = startIndex + satzgliedLength;
        @Nullable final String charAfter = endIndex >= text.length() ?
                null :
                text.substring(endIndex, startIndex + satzgliedLength + 1);

        if (charBefore == null) {
            if (charAfter == null) {
                return null;
            }

            if (charAfter.equals(" ")) {
                return text.substring(endIndex + 1);
            }

            return text.substring(endIndex);
        }

        // charBefore != null
        if (charBefore.equals(" ")) {
            if (charAfter == null) {
                return text.substring(0, startIndex - 1);
            }

            if (charAfter.equals(" ")) {
                return text.substring(0, startIndex - 1) + text.substring(endIndex);
            }

            return text.substring(0, startIndex - 1) + " " + text.substring(endIndex);
        }

        // charBefore != null, !charBefore.equals(" ")
        if (charAfter == null) {
            return text.substring(0, startIndex);
        }

        if (charAfter.equals(" ")) {
            return text.substring(0, startIndex) + text.substring(endIndex + 1);
        }

        return text.substring(0, startIndex) + " " + text.substring(endIndex);
    }

    public static String buildHauptsatz(final String vorfeld, final String verb,
                                        @Nullable final String mittelfeldEtc) {
        return joinToNullString(
                capitalize(vorfeld),
                verb,
                mittelfeldEtc);
    }
}
