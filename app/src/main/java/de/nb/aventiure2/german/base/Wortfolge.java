package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Eine Folge von Wörter, Satzzeichen, wie sind in einem Text vorkommen könnte.
 */
@Immutable
public class Wortfolge {
    private static final int GEDAECHTNISWEITE_PHORIK = 6;

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
     * Hierauf könnte sich ein Pronomen (z.B. ein Personalpronomen) unmittelbar
     * danach (<i>anaphorisch</i>) beziehen. Dazu müssen (in aller Regel) die grammatischen
     * Merkmale übereinstimmen und es muss mit dem Pronomen dieses Bezugsobjekt
     * gemeint sein.
     * <p>
     * Dieses Feld sollte nur gesetzt werden, wenn man sich sicher ist, wenn es also keine
     * Fehlreferenzierungen, Doppeldeutigkeiten
     * oder unerwünschten Wiederholungen geben kann. Typische Fälle wären "Du nimmst die Lampe und
     * zündest sie an." oder "Du stellst die Lampe auf den Tisch und zündest sie an."
     * <p>
     * Negatitvbeispiele wäre:
     * <ul>
     *     <li>"Du stellst die Lampe auf die Theke und zündest sie an." (Fehlreferenzierung)
     *     <li>"Du nimmst den Ball und den Schuh und wirfst ihn in die Luft." (Doppeldeutigkeit)
     *     <li>"Du nimmst die Lampe und zündest sie an. Dann stellst du sie wieder ab,
     *     schaust sie dir aber dann noch einmal genauer an: Sie ... sie ... sie" (Unerwünschte
     *     Wiederholung)
     *     <li>"Du stellst die Lampe auf den Tisch. Der Tisch ist aus Holz und hat viele
     *     schöne Gravuren - er muss sehr wertvoll sein. Dann nimmst du sie wieder in die Hand."
     *     (Referenziertes Objekt zu weit entfernt.)
     * </ul>
     */
    @Nullable
    private final PhorikKandidat phorikKandidat;

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
     * Fügt diese Teile zu mehreren alternativen Wortfolgen zusammen. Gibt es für mehrere Teile mehrere Alternativen, so werden
     * * alle Kombinationen erzeugt.
     *
     * @return Mehrere alternative Wortfolgen. Wenn eine der Kombinationen ausschließlich
     * {@code null}-Werte enthält, wird die Collection auch den Wert
     * {@code null} enthalten.
     */
    @Nonnull
    public static Collection<Wortfolge> joinToAltWortfolgen(final Object... parts) {
        return joinToAltWortfolgen(asList(parts));
    }

    /**
     * Fügt diese Teile zu mehreren alternativen Wortfolgen zusammen. Gibt es für mehrere Teile mehrere Alternativen, so werden
     * * alle Kombinationen erzeugt.
     *
     * @return Mehrere alternative Wortfolgen. Wenn eine der Kombinationen ausschließlich
     * {@code null}-Werte enthält, wird die Collection auch den Wert
     * {@code null} enthalten.
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

        return w(resString.toString(), woertlicheRedeNochOffen, kommaStehtAus,
                findPhorikKandidat(konstituentenfolge));
    }

    private static PhorikKandidat findPhorikKandidat(final Konstituentenfolge konstituentenfolge) {
        final Integer indexKandidatM = interpretPair(
                findPhorikKandidatAndSicherheit(konstituentenfolge, M));
        final Integer indexKandidatF = interpretPair(
                findPhorikKandidatAndSicherheit(konstituentenfolge, F));
        final Integer indexKandidatN = interpretPair(
                findPhorikKandidatAndSicherheit(konstituentenfolge, N));
        final Integer indexKandidatPL = interpretPair(
                findPhorikKandidatAndSicherheit(konstituentenfolge, PL_MFN));

        if (indexKandidatF != null && indexKandidatPL != null) {
            // Hier könnte es zu Doppeldeutigkeiten kommen:
            // "Die Frau füttert die Vögel. Du beobachtest sie." (Die Frau? Die Vögel?)
            return null;
        }

        final int bestIndex =
                Stream.of(indexKandidatM, indexKandidatF, indexKandidatN, indexKandidatPL)
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .max()
                        .orElse(-1);
        if (bestIndex == -1) {
            return null;
        }

        return konstituentenfolge.get(bestIndex).getPhorikKandidat();
    }

    private static Integer interpretPair(final Pair<Integer, Boolean> phorikKandidatAndSicherheit) {
        if (!phorikKandidatAndSicherheit.second) {
            return null;
        }

        return phorikKandidatAndSicherheit.first;
    }

    @Nullable
    static NumerusGenus calcKannAlsBezugsobjektVerstandenWerdenFuer(
            final Konstituentenfolge konstituentenfolge) {
        // Dies ist eine grobe Näherung - natürlich könnten in der Konstituentenfolge
        // leicht alle möglichen Genera / Numeri als mögliche Bezugsobjekte vorkommen.
        return Stream.of(M, F, PL_MFN, N)
                .filter(g -> calcKannAlsBezugsobjektVerstandenWerdenFuer
                        (konstituentenfolge, g))
                .findFirst()
                .orElse(null);
    }

    private static boolean calcKannAlsBezugsobjektVerstandenWerdenFuer(
            final Konstituentenfolge konstituentenfolge,
            final NumerusGenus numerusGenus) {
        final Pair<Integer, Boolean> phorikKandidatAndSicherheit =
                findPhorikKandidatAndSicherheit(konstituentenfolge, numerusGenus);
        if (phorikKandidatAndSicherheit.first != null) {
            return true;
        }

        return !phorikKandidatAndSicherheit.second;
    }

    private static Pair<Integer, Boolean> findPhorikKandidatAndSicherheit(
            final Konstituentenfolge konstituentenfolge,
            final NumerusGenus numerusGenus) {
        Integer indexVorigerAbweichenderKandidat = null;
        Integer indexKandidat = null;

        for (int i = 0; i < konstituentenfolge.size(); i++) {
            final Konstituente konstituente = konstituentenfolge.get(i);

            if (konstituente.getPhorikKandidat() != null
                    && konstituente.getPhorikKandidat().getNumerusGenus().equals(numerusGenus)) {
                if (indexKandidat != null
                        && !requireNonNull(
                        konstituentenfolge.get(indexKandidat).getPhorikKandidat())
                        .getBezugsobjekt().equals(
                                konstituente.getPhorikKandidat().getBezugsobjekt())) {
                    // Es gab bereits ein Bezugsobjekt, und zwar ein anderes!
                    indexVorigerAbweichenderKandidat = indexKandidat;
                }

                indexKandidat = i;
            } else if (konstituente.koennteAlsBezugsobjektVerstandenWerdenFuer(numerusGenus)
                    && indexKandidat != null) {

                //  Doppeldeutigkeit verhindern: "Du nimmst den Ball und den Schuh und wirfst ihn
                //  in die Luft."
                indexVorigerAbweichenderKandidat = indexKandidat;
                indexKandidat = i;
            }

            if (indexVorigerAbweichenderKandidat != null
                    && i - indexVorigerAbweichenderKandidat > GEDAECHTNISWEITE_PHORIK) {
                indexVorigerAbweichenderKandidat = null;
            }

            if (indexKandidat != null
                    && i - indexKandidat > GEDAECHTNISWEITE_PHORIK) {
                // Irgendwann wird der Abstand zu groß. Dinge vermeiden wie "Du stellst
                // die Lampe auf den Tisch. Der Tisch ist aus Holz und hat viele
                // schöne Gravuren - er muss sehr wertvoll sein. Dann nimmst du sie wieder
                // in die Hand."

                indexKandidat = null;
            }
        }

        if (indexVorigerAbweichenderKandidat != null) {
            // Doppeldeutigkeit verhindern: "Du nimmst den Ball und den Schuh und wirfst ihn
            // in die Luft." -> Es wurde etwas gefunden - aber nichts eindeutiges.
            // Diese Numerus/Genus-Kombination kann zu Missverständnissenn führen.
            return new Pair<>(null, false);
        }

        if (indexKandidat == null) {
            // Es wurde nichts gefunden. Es kann auch keine Missverständnisse geben.
            return new Pair<>(null, true);
        }

        if (konstituentenfolge.get(indexKandidat).getPhorikKandidat() == null) {
            // Etwas anderes (bei dem kein Bezugsobjekt angegeben wurde) könnte
            // als Bezugsobjekt verstanden werden.
            return new Pair<>(null, false);
        }

        // Es wurde ein eindeutiger Kandidat gefunden
        return new Pair<>(indexKandidat, true);
    }

    /**
     * Erzeugt eine Wortfolge ohne Phorik-Kandidaten - null-safe.
     */
    @Nullable
    public static Wortfolge w(final @Nullable String string) {
        return w(string, null);
    }

    /**
     * Erzeugt eine Wortfolge, bei der kein Komma aussteht - null-safe.
     */
    @Nullable
    public static Wortfolge w(final @Nullable String string,
                              @Nullable final PhorikKandidat phorikKandidat) {
        if (string == null) {
            return null;
        }

        return w(string, false, false, phorikKandidat);
    }

    public static Wortfolge w(final String string, final boolean woertlicheRedeNochOffen,
                              final boolean kommaStehtAus,
                              @Nullable final PhorikKandidat phorikKandidat) {
        return new Wortfolge(string, woertlicheRedeNochOffen, kommaStehtAus, phorikKandidat);
    }

    private Wortfolge(final String string, final boolean woertlicheRedeNochOffen,
                      final boolean kommmaStehtAus,
                      @Nullable final PhorikKandidat phorikKandidat) {
        this.string = string;
        this.woertlicheRedeNochOffen = woertlicheRedeNochOffen;
        this.kommmaStehtAus = kommmaStehtAus;
        this.phorikKandidat = phorikKandidat;
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

    /**
     * Hierauf könnte sich ein Pronomen (z.B. ein Personalpronomen) unmittelbar
     * <i>nach der Wortfolge</i>> (<i>anaphorisch</i>) beziehen. Dazu müssen (in aller Regel) die
     * grammatischen Merkmale übereinstimmen und es muss mit dem Pronomen dieses Bezugsobjekt
     * gemeint sein. Außerdem muss dabei sichergestellt werden, dass es nicht zu unerwünschten
     * Wiederholungen kommt ("Du nimmst die Lampe und zündest sie an. Dann stellst du sie wieder
     * ab, schaust sie dir aber dann noch einmal genauer an: Sie... sie... sie..."
     */
    @Nullable
    public PhorikKandidat getPhorikKandidat() {
        return phorikKandidat;
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
