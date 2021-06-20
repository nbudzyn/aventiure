package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import de.nb.aventiure2.german.string.NoLetterException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.GermanUtil.endeDecktKommaAb;
import static de.nb.aventiure2.german.base.GermanUtil.spaceNeeded;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.string.GermanStringUtil.appendBreak;
import static de.nb.aventiure2.german.string.GermanStringUtil.beginnStehtCapitalizeNichtImWeg;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Eine Folge von {@link Konstituente}n.
 */
@Immutable
public class Konstituentenfolge
        implements IAlternativeKonstituentenfolgable,
        Iterable<IKonstituenteOrStructuralElement> {
    private static final int GEDAECHTNISWEITE_PHORIK = 6;
    private static final int MAX_ANZAHL_ALTERNATIVEN_FOR_1_PART = 300;
    private static final int EXTRA_ANZAHL_ALTERNATIVEN_PER_EXTRA_PART = 25;
    private final ImmutableList<IKonstituenteOrStructuralElement> konstituenten;

    @Nullable
    @CheckReturnValue
    public static Konstituentenfolge kf(final Iterable<?> parts) {
        return joinToNullKonstituentenfolge(parts);
    }

    public Konstituentenfolge(final IKonstituenteOrStructuralElement konstituente) {
        this(ImmutableList.of(konstituente));
    }

    private Konstituentenfolge(
            final ImmutableList<IKonstituenteOrStructuralElement> konstituenten) {
        // Wird auch mit ImmutableList.subList() aufgerufen, was leider zu einer
        // Kopie führt. Daher ImmutableList verlangen!

        requireNonNull(konstituenten, "konstituenten is null");
        checkArgument(konstituenten.stream().noneMatch(Objects::isNull));

        if (konstituenten.contains(WORD)) {
            // WORD als "Trenner" ist sinnlos. wir entfernen es hier.
            this.konstituenten = konstituenten.stream()
                    .filter(k -> !k.equals(WORD))
                    .collect(toImmutableList());
        } else {
            this.konstituenten = konstituenten;
        }

        checkArgument(!this.konstituenten.isEmpty(),
                "konstituenten is empty or contained only WORD elements");
    }


    /**
     * Gibt denselben Input zurück, gegebenenfalls in Kommata eingeschlossen.
     */
    @Nullable
    @CheckReturnValue
    static Konstituentenfolge schliesseInKommaEin(
            @Nullable final Konstituentenfolge input, final boolean schliesseInKommaEin) {
        if (!schliesseInKommaEin) {
            return input;
        }

        return schliesseInKommaEin(input);
    }

    /**
     * Gibt denselben Input zurück, wobei ein Vorkomma und ein Folgekomma gefordert werden.
     * Ist der Input leer, wird eine leere Konsitutenten-Liste zurückgeben.
     */
    @SuppressWarnings("GrazieInspection")
    @Nullable
    @CheckReturnValue
    public static Konstituentenfolge schliesseInKommaEin(
            @Nullable final Konstituentenfolge input) {
        if (input == null) {
            return null;
        }

        return input.withKommaStehtAus().withVorkommaNoetig();
    }

    /**
     * Fügt diese Teile zu einer Konstituentenfolge zusammen - es darf sich nicht
     * <code>null</code> ergeben.
     *
     * @return Eine Konstituentenfolge, nie <code>null</code>>
     */
    @Nonnull
    public static Konstituentenfolge joinToKonstituentenfolge(final Object... parts) {
        return checkJoiningResultNotNull(joinToNullKonstituentenfolge(parts), parts);
    }

    /**
     * Fügt diese Teile zu einer Konstituentenfolge zusammen.
     *
     * @return Eine Konstituentenfolge - oder <code>null</code>
     */
    @Nullable
    @CheckReturnValue
    public static Konstituentenfolge joinToNullKonstituentenfolge(final Object... parts) {
        return joinToNullKonstituentenfolge(asList(parts));
    }

    /**
     * Fügt diese Teile zu einer Konstituentenfolge zusammen.
     *
     * @return Eine Konstituentenfolge - oder <code>null</code>
     */
    @Nullable
    @CheckReturnValue
    static Konstituentenfolge joinToNullKonstituentenfolge(final Iterable<?> parts) {
        final Collection<Konstituentenfolge> alternatives = joinToAltKonstituentenfolgen(parts);
        if (alternatives.isEmpty()) {
            return null;
        }

        if (alternatives.size() > 1) {
            throw new IllegalArgumentException("Unerwartet mehrere Alternativen! "
                    + "parts: " + parts
                    + ", alternatives : " + alternatives);
        }

        return alternatives.iterator().next();
    }


    /**
     * Fügt diese Teile zu mehreren alternativen Konstituentenfolgen zusammen.
     *
     * @return Mehrere alternative Konstituentenfolgen - die Collection kann statt einer
     * Konstituentenfolge auch <code>null</code> enthalten.
     */
    @Nonnull
    @CheckReturnValue
    public static Collection<Konstituentenfolge> joinToAltKonstituentenfolgen(
            final Object... parts) {
        return joinToAltKonstituentenfolgen(asList(parts));
    }

    /**
     * Fügt diese Teile zu mehreren alternativen Konstituentenfolgen zusammen.
     *
     * @return Mehrere alternative Konstituentenfolgen - die Collection kann statt einer
     * Konstituentenfolge auch <code>null</code> enthalten.
     */
    @Nonnull
    @CheckReturnValue
    private static Collection<Konstituentenfolge> joinToAltKonstituentenfolgen(
            final Iterable<?> parts) {
        // IDEA Ggf. Konstituentenfolge und AbstractDescription zusammenführen?

        ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>>
                alternativeKonstituentenfolgenBuilder = new ArrayList<>();

        alternativeKonstituentenfolgenBuilder.add(ImmutableList.builder());

        int partCount = 0;
        for (final Object part : parts) {
            partCount++;

            final Collection<Konstituentenfolge> alternativePartKonstituentenfolgen;

            if (part instanceof Stream<?>) {
                // Alternativen!
                alternativePartKonstituentenfolgen =
                        ((Stream<?>) part)
                                .flatMap(p -> joinToAltKonstituentenfolgen(p).stream())
                                .collect(Collectors.toSet());
                // Früher stand hier dies:
                // alternativePartKonstituentenfolgen =
                //       joinToAltKonstituentenfolgen(((Stream<?>) part).collect(toSet()));
            } else if (part != null && part.getClass().isArray()) {
                // Alternativen!
                final List<Object> content = new ArrayList<>(Array.getLength(part));
                for (int i = 0; i < Array.getLength(part); i++) {
                    content.add(Array.get(part, i));
                }

                alternativePartKonstituentenfolgen =
                        content.stream()
                                .flatMap(p -> joinToAltKonstituentenfolgen(p).stream())
                                .collect(Collectors.toSet());
            } else if (part instanceof Collection<?>) {
                // Alternativen!
                alternativePartKonstituentenfolgen =
                        ((Collection<?>) part).stream()
                                .flatMap(p -> joinToAltKonstituentenfolgen(p).stream())
                                .collect(Collectors.toSet());
            } else if (part instanceof IAlternativeKonstituentenfolgable) {
                alternativePartKonstituentenfolgen =
                        ((IAlternativeKonstituentenfolgable) part).toAltKonstituentenfolgen();
            } else if (part == null || "".equals(part)) {
                alternativePartKonstituentenfolgen = Collections.singletonList(null);
            } else if (part instanceof CharSequence) {
                alternativePartKonstituentenfolgen =
                        ImmutableList.of(new Konstituentenfolge(Konstituente.k(part.toString())));
            } else {
                throw new IllegalArgumentException("Ungültiges Argument für "
                        + "joinToAltKonstituentenfolgen: Klasse " + part.getClass() +
                        ", Wert " + part + ". Ggf. IAlternativeKonstituentenfolgable "
                        + "implementieren?");
            }

            final int limit = MAX_ANZAHL_ALTERNATIVEN_FOR_1_PART +
                    (partCount - 1) * EXTRA_ANZAHL_ALTERNATIVEN_PER_EXTRA_PART;
            // Im ersten Schritt akzeptieren wir maximal (z.B.) 300 Alternativen.
            // Im zweiten Schritt dürfen dann (durch "Ausmultiplizieren") maximal
            // (z.B.) 25 weitere hinzukommen etc.
            alternativeKonstituentenfolgenBuilder =
                    appendAllCombinations(alternativeKonstituentenfolgenBuilder,
                            alternativePartKonstituentenfolgen, limit);
        }

        return buildKonstituentenfolgen(alternativeKonstituentenfolgenBuilder);
    }

    @NonNull
    private static ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>>
    appendAllCombinations(
            final ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>>
                    leftBuilderList,
            final Collection<Konstituentenfolge> rightList,
            final int limit) {
        final ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>>
                res = new ArrayList<>();
        int count = 0;

        // Falls wir zu viele Eregebnisse hätten, wollen wird möglichst links Variabilität.
        // (Auf einsilbige rechte Seiten kann der Folgetext abwechslungsreich reagieren.)
        // Daher gehen wir in der äußeren ("langsamen") Schleife die rechten Seiten durch.

        for (final @Nullable Konstituentenfolge right : rightList) {
            final ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>>
                    tmp = appendToAll(leftBuilderList, right, limit - count);

            res.addAll(tmp);
            count += tmp.size();

            if (count >= limit) {
                return res;
            }
        }

        return res;
    }

    private static ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>> appendToAll(
            final ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>> leftBuilderList,
            @Nullable final Konstituentenfolge right, final int limit) {
        final ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>>
                res = new ArrayList<>();

        int count = 0;

        for (final ImmutableList.Builder<IKonstituenteOrStructuralElement> left : leftBuilderList) {
            if (count >= limit) {
                return res;
            }

            res.add(append(left, right));
            count++;
        }

        return res;
    }

    private static ImmutableList.Builder<IKonstituenteOrStructuralElement> append(
            final ImmutableList.Builder<IKonstituenteOrStructuralElement> left,
            final @Nullable Konstituentenfolge right) {
        if (right == null) {
            return left;
        }

        return ImmutableList.<IKonstituenteOrStructuralElement>builder()
                .addAll(left.build())
                .addAll(right.konstituenten);
    }

    @NonNull
    private static HashSet<Konstituentenfolge> buildKonstituentenfolgen(
            final ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>>
                    konstituentenfolgenBuilderList) {
        final HashSet<Konstituentenfolge> res = new HashSet<>();
        for (final ImmutableList.Builder<IKonstituenteOrStructuralElement> builder :
                konstituentenfolgenBuilderList) {
            final ImmutableList<IKonstituenteOrStructuralElement> konstituenten = builder.build();

            if (konstituenten.isEmpty()) {
                res.add(null);
            } else {
                res.add(new Konstituentenfolge(konstituenten));
            }
        }

        return res;
    }

    /**
     * Schneidet die einzelnen <code>listOfParts</code> einen nach dem anderen aus dem Input.
     * Doppelt auftretende Texte werden also auch zweimal herausgeschnitten.
     * Für jeden einzelnen Schnitt wird  {@link #cutFirst(Konstituentenfolge)} verwendet.
     */
    @Nullable
    public static Konstituentenfolge cutFirstOneByOne(@Nullable final Konstituentenfolge input,
                                                      final List<Konstituentenfolge> listOfParts) {
        if (input == null) {
            return null;
        }

        Konstituentenfolge res = input;
        for (final Konstituentenfolge parts : listOfParts) {
            res = res.cutFirst(parts);
            if (res == null) {
                return null;
            }
        }

        return res;
    }

    private static <R> R checkJoiningResultNotNull(
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

    private static Integer interpretPair(final Pair<Integer, Boolean> phorikKandidatAndSicherheit) {
        requireNonNull(phorikKandidatAndSicherheit.second,
                "phorikKandidatAndSicherheit.second null");

        if (!phorikKandidatAndSicherheit.second) {
            return null;
        }

        return phorikKandidatAndSicherheit.first;
    }

    public boolean kommaStehtAus() {
        final int i = konstituenten.size() - 1;
        final IKonstituenteOrStructuralElement konst = konstituenten.get(i);
        if (konst instanceof StructuralElement) {
            return false;
        }

        if (konst instanceof Konstituente) {
            return ((Konstituente) konst).kommaStehtAus();
        }

        throw new IllegalStateException("Unexpected konst: " + konst);
    }

    private Integer lastIndexKannAlsBezugsobjektVerstandenWerdenFuer(
            final NumerusGenus numerusGenus) {
        // Optimierung: Sonderfall, dass die
        // Konstituentenfolge nur genau eine Konstituente enthält
        if (size() == 1) {
            final IKonstituenteOrStructuralElement konst = get(0);
            if (konst instanceof Konstituente) {
                if (((Konstituente) konst)
                        .koennteAlsBezugsobjektVerstandenWerdenFuer(numerusGenus)) {
                    return 0;
                }
            }

            return null;
        }

        final Pair<Integer, Boolean> phorikKandidatAndSicherheit =
                findPhorikKandidatAndSicherheit(numerusGenus);

        requireNonNull(phorikKandidatAndSicherheit.second,
                "phorikKandidatAndSicherheit.second null");

        if (phorikKandidatAndSicherheit.first == null) {
            // Es wurde nichts gefunden. Es kann auch keine Missverständnisse geben.
            // ->  kann nicht AlsBezugsobjektVerstandenWerdenFuer
            return null;
        }

        // Es wurde etwas gefunden - aber nichts eindeutiges.
        // Oder etwas anderes (bei dem kein Bezugsobjekt angegeben wurde) könnte
        // als Bezugsobjekt verstanden werden.
        // Oder es wurde ein eindeutiger Kandidat gefunden
        // ->  kannAlsBezugsobjektVerstandenWerden
        return phorikKandidatAndSicherheit.first;
    }

    @Nullable
    private NumerusGenus calcKannAlsBezugsobjektVerstandenWerdenFuer() {
        // Dies ist eine grobe Näherung - natürlich könnten in der Konstituentenfolge
        // leicht alle möglichen Genera / Numeri als mögliche Bezugsobjekte vorkommen.
        // Wir nehmen das letzte - bei "Gleichstand" haben wir eine Priorisierung.

        Integer bestIndex = null;
        NumerusGenus res = null;

        for (final NumerusGenus numerusGenus : asList(M, F, PL_MFN, N)) {
            final Integer lastIndexKannAlsBezugsobjektVerstandenWerdenFuer =
                    lastIndexKannAlsBezugsobjektVerstandenWerdenFuer(numerusGenus);

            if (lastIndexKannAlsBezugsobjektVerstandenWerdenFuer != null
                    && (bestIndex == null
                    || lastIndexKannAlsBezugsobjektVerstandenWerdenFuer > bestIndex)) {
                bestIndex = lastIndexKannAlsBezugsobjektVerstandenWerdenFuer;
                res = numerusGenus;
            }

            if (bestIndex != null && bestIndex == size() - 1) {
                // Optimierung
                return res;
            }
        }

        return res;
    }

    /**
     * Ermittelt einen denkbaren Phorik-Kandidaten mit diesem Numerus und Genus (wenn er
     * sich ist) und eine Angabe, wie sicher ein solcher Kandidat für diese Numerus und Genua
     * erscheint.
     * <p>
     * Bezugsobjekte werden dabei gegenüber anderen Nominalphrasen etwas stärker
     * gewichtet - die Methode geht davon aus, dass vorherige Nominalphrasen in den Hintergrund
     * treten, wenn ein Bezugsobjekt erscheint.
     */
    private Pair<Integer, Boolean> findPhorikKandidatAndSicherheit(
            final NumerusGenus numerusGenus) {
        Integer indexVorigerAbweichenderKandidat = null;
        Integer indexKandidat = null;

        for (int i = 0; i < size(); i++) {
            final IKonstituenteOrStructuralElement konst = get(i);
            if (konst instanceof Konstituente) {
                final Konstituente konstituente = (Konstituente) konst;
                if (konstituente.getPhorikKandidat() != null
                        && konstituente.getPhorikKandidat().getNumerusGenus()
                        .equals(numerusGenus)) {
                    if (indexKandidat != null
                            && !requireNonNull(
                            ((Konstituente) get(indexKandidat)).getPhorikKandidat())
                            .getBezugsobjekt().equals(
                                    konstituente.getPhorikKandidat().getBezugsobjekt())) {
                        if (((Konstituente) get(indexKandidat)).getPhorikKandidat() != null) {
                            // Es gab bereits ein Bezugsobjekt, und zwar ein anderes!
                            indexVorigerAbweichenderKandidat = indexKandidat;
                        } else {
                            // Es gab bereits eine Konstituente, die als Bezugsobjekt
                            // verstanden werden könnte. In diesem Fall gehen wir davon aus,
                            // das das Bezugsobjekt für den Leser klar im Vordergrund steht
                            // und es zu keiner Doppeldeutigkeit kommen kann.
                            // Beispiel: "Die SONNE (kein
                            // Bezugsobjekt) steht hoch. Rapunzel (Bezugsobjekt) kommt daher. Sie
                            // (eindeutig Rapunzel!) lächelt dich an."
                            indexVorigerAbweichenderKandidat = null;
                        }
                    }

                    indexKandidat = i;
                } else if (konstituente.koennteAlsBezugsobjektVerstandenWerdenFuer(numerusGenus)) {
                    //  Doppeldeutigkeit verhindern: "Du nimmst den Ball und den Schuh und wirfst
                    //  ihn in die Luft."
                    indexVorigerAbweichenderKandidat = indexKandidat;
                    indexKandidat = i;
                }

                indexVorigerAbweichenderKandidat =
                        vergissWennZuLangeHer(indexVorigerAbweichenderKandidat, i);

                indexKandidat = vergissWennZuLangeHer(indexKandidat, i);
            } else if (konst instanceof StructuralElement) {
                if (konst == CHAPTER) {
                    indexVorigerAbweichenderKandidat = null;
                    indexKandidat = null;
                }
            } else {
                throw new IllegalStateException("Unexpected konst: " + konst);
            }
        }

        if (indexKandidat == null) {
            // Es wurde nichts gefunden. Es kann auch keine Missverständnisse geben.
            return new Pair<>(null, true);
        }

        if (indexVorigerAbweichenderKandidat != null
                // Doppeldeutigkeit verhindern: "Du nimmst den Ball und den Schuh und wirfst ihn
                // in die Luft." -> Es wurde etwas gefunden - aber nichts eindeutiges.
                // Diese Numerus/Genus-Kombination kann zu Missverständnissenn führen.
                || ((Konstituente) get(indexKandidat)).getPhorikKandidat() == null) {
            // Oder etwas  anderes (bei dem kein Bezugsobjekt angegeben wurde) könnte
            // als Bezugsobjekt verstanden werden.
            return new Pair<>(indexKandidat, false);
        }

        // Es wurde ein eindeutiger Kandidat gefunden
        return new Pair<>(indexKandidat, true);
    }

    @Nullable
    private static Integer vergissWennZuLangeHer(@Nullable final Integer gemerkterIndex,
                                                 final int aktuellerIndex) {
        if (gemerkterIndex != null
                && aktuellerIndex - gemerkterIndex > GEDAECHTNISWEITE_PHORIK) {
            // Irgendwann wird der Abstand zu groß. Dinge vermeiden wie "Du stellst
            // die Lampe auf den Tisch. Der Tisch ist aus Holz und hat viele
            // schöne Gravuren - er muss sehr wertvoll sein. Dann nimmst du sie wieder
            // in die Hand."

            return null;
        }

        return gemerkterIndex;
    }

    private PhorikKandidat findPhorikKandidat() {
        // Optimierung: Sonderfall, dass die
        // Konstituentenfolge nur genau eine Konstituente enthält
        if (size() == 1) {
            final IKonstituenteOrStructuralElement konst = get(0);
            if (konst instanceof Konstituente) {
                return ((Konstituente) konst).getPhorikKandidat();
            }
        }

        final Pair<Integer, Boolean> phorikKandidatAndSicherheitF =
                findPhorikKandidatAndSicherheit(F);
        final Pair<Integer, Boolean> phorikKandidatAndSicherheitPL =
                findPhorikKandidatAndSicherheit(PL_MFN);

        requireNonNull(phorikKandidatAndSicherheitF.second);
        requireNonNull(phorikKandidatAndSicherheitPL.second);

        if (phorikKandidatAndSicherheitF.first != null && phorikKandidatAndSicherheitF.second
                && phorikKandidatAndSicherheitPL.first != null
                && !phorikKandidatAndSicherheitPL.second
                && phorikKandidatAndSicherheitF.first < phorikKandidatAndSicherheitPL.first) {
            // Es trat ein sicheres F-Bezugsobjekt auf - aber danach kam noch etwas,
            // das ein PL sein könnte. Hier könnte es zu Doppeldeutigkeiten kommen:
            //  "Die Frau füttert die Vögel. Du beobachtest sie." (Die Frau? Die Vögel?)
            return null;
        }

        if (phorikKandidatAndSicherheitPL.first != null && phorikKandidatAndSicherheitPL.second
                && phorikKandidatAndSicherheitF.first != null
                && !phorikKandidatAndSicherheitF.second
                && phorikKandidatAndSicherheitPL.first < phorikKandidatAndSicherheitF.first) {
            // Es trat ein sicheres PL-Bezugsobjekt auf - aber danach kam noch etwas,
            // das ein F sein könnte. Hier könnte es zu Doppeldeutigkeiten kommen:
            //  "Die Frauen füttern die Taube. Du beobachtest sie." (Die Frauen? Die Taube?)
            return null;
        }

        final Integer indexKandidatF = interpretPair(phorikKandidatAndSicherheitF);
        final Integer indexKandidatPL = interpretPair(phorikKandidatAndSicherheitPL);

        if (indexKandidatF != null && indexKandidatPL != null) {
            // Hier könnte es zu Doppeldeutigkeiten kommen:
            // "Die Frau füttert die Vögel. Du beobachtest sie." (Die Frau? Die Vögel?)
            return null;
        }

        final Integer indexKandidatM = interpretPair(findPhorikKandidatAndSicherheit(M));
        final Integer indexKandidatN = interpretPair(findPhorikKandidatAndSicherheit(N));

        final int bestIndex =
                Stream.of(indexKandidatM, indexKandidatF, indexKandidatN, indexKandidatPL)
                        .filter(Objects::nonNull)
                        .mapToInt(Integer::intValue)
                        .max()
                        .orElse(-1);
        if (bestIndex == -1) {
            return null;
        }

        return ((Konstituente) get(bestIndex)).getPhorikKandidat();
    }

    /**
     * Fügt diese Konstituentenfolge zu einem String zusammen, wobei ein nichtleerer
     * String das Ergebnis sein muss. Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    @NonNull
    @CheckReturnValue
    public String joinToString() {
        return joinToSingleKonstituente().toTextOhneKontext();
    }

    /**
     * Fügt diese Konstituenten zu einer einzigen Konsituente zusammen. Damit gehen natürlich
     * Detailinformationen verloren, und man kann nachträglich nicht mehr gut einzelne
     * Teile entfernen, weil z.B. unklar ist, ob auch Kommata entfernt werden müssen.
     * Die Ergebnis-Konsituente wird nicht automatisch großgeschrieben.
     */
    @NonNull
    @CheckReturnValue
    public Konstituente joinToSingleKonstituente() {
        final IKonstituenteOrStructuralElement res = joinToSingleKonstituenteOrStructuralElement();
        if (!(res instanceof Konstituente)) {
            throw new IllegalStateException("Joining result was " + res);
        }

        return (Konstituente) res;
    }

    /**
     * Fügt diese Konstituenten zu einer einzigen Konsituente oder eine StructuralElement
     * zusammen. Damit gehen natürlich Detailinformationen verloren, und man kann nachträglich
     * nicht mehr gut einzelne
     * Teile entfernen, weil z.B. unklar ist, ob auch Kommata entfernt werden müssen.
     * Die Ergebnis-Konstituente wird nicht automatisch großgeschrieben.
     */
    @NonNull
    @CheckReturnValue
    public IKonstituenteOrStructuralElement joinToSingleKonstituenteOrStructuralElement() {
        final StringBuilder resTextBuilder = new StringBuilder(size() * 25);
        boolean bislangNurStructuralElements = true;
        boolean vorkommaNoetig = false;
        boolean vordoppelpunktNoetig = false;
        StructuralElement startsNew = WORD;
        StructuralElement brreak = WORD;
        boolean woertlicheRedeNochOffen = false;
        boolean kommaStehtAus = false;
        for (final IKonstituenteOrStructuralElement konst : this) {
            if (konst instanceof StructuralElement) {
                // this enthält (vgl. Konstruktor!) keine WORDs!
                if (konst == WORD) {
                    throw new IllegalStateException("StructuralElement.WORD found in  " + this);
                }

                brreak = StructuralElement.max(brreak, (StructuralElement) konst);

                if (bislangNurStructuralElements) {
                    startsNew = brreak;
                }
            } else if (konst instanceof Konstituente) {
                final Konstituente konstituente = (Konstituente) konst;

                if (woertlicheRedeNochOffen) {
                    resTextBuilder.append(woertlicheRedeabschlussToString(
                            resTextBuilder, konstituente.getText()));
                }

                if (bislangNurStructuralElements) {
                    startsNew = StructuralElement.max(startsNew, konstituente.getStartsNew());
                }

                if (bislangNurStructuralElements) {
                    vorkommaNoetig = konstituente.vorkommaNoetig() && startsNew == WORD;
                    vordoppelpunktNoetig = konstituente.vordoppelpunktNoetig()
                            && startsNew != CHAPTER && startsNew != PARAGRAPH;
                }

                brreak = appendKonstituente(resTextBuilder, bislangNurStructuralElements, brreak,
                        kommaStehtAus, konstituente);
                kommaStehtAus = konstituente.kommaStehtAus();
                woertlicheRedeNochOffen = konstituente.woertlicheRedeNochOffen();
                bislangNurStructuralElements = false;
            } else {
                throw new IllegalArgumentException("Unexpected konst: " + konst);
            }
        }

        return buildKonstituenteOrStructuralElement(resTextBuilder.toString().trim(),
                vorkommaNoetig, vordoppelpunktNoetig,
                startsNew, woertlicheRedeNochOffen, kommaStehtAus, brreak);
    }

    private static StructuralElement appendKonstituente(final StringBuilder stringBuilder,
                                                        final boolean firstKonstituente,
                                                        final StructuralElement brreak,
                                                        final boolean kommaStehtAus,
                                                        final Konstituente konstituente) {
        final boolean capitalize =
                appendVorsatzzeichen(stringBuilder, firstKonstituente, brreak, kommaStehtAus,
                        konstituente);

        return appendKonstituente(stringBuilder, konstituente, capitalize);
    }

    private static boolean appendVorsatzzeichen(final StringBuilder stringBuilder,
                                                final boolean firstKonstituente,
                                                final StructuralElement brreak,
                                                final boolean kommaStehtAus,
                                                final Konstituente konstituente) {
        // Vordoppelpunkt ist in neuem Satz, Absatz, ... nicht nötig
        if (!firstKonstituente && brreak != CHAPTER && brreak != PARAGRAPH
                && konstituente.vordoppelpunktNoetig()) {
            stringBuilder.append(satzzeichenToString(":", konstituente.getText()));

            return brreak != WORD; // Danach Großschreibung, wenn mindestens ein Satz beginnt
        }

        // Vorkomma ist in neuem Satz, Absatz, ... nicht nötig
        if (kommaStehtAus && brreak == WORD
                || (!firstKonstituente && brreak == WORD && konstituente.vorkommaNoetig()
                && !endeDecktKommaAb(stringBuilder))) {
            stringBuilder.append(satzzeichenToString(",", konstituente.getText()));
            return false; // Danach Kleinschreibung
        }

        appendBreak(stringBuilder, brreak, konstituente.getText());
        return !firstKonstituente && brreak != WORD; // Danach Großschreibung, wenn mindestens
        // ein Satz beginnt, außer bei der ersten Konstituente
    }

    private static StructuralElement appendKonstituente(final StringBuilder stringBuilder,
                                                        final Konstituente konstituente,
                                                        final boolean capitalize) {
        if (capitalize && beginnStehtCapitalizeNichtImWeg(konstituente.getText())) {
            try {
                stringBuilder.append(konstituente.capitalizeFirstLetter().getText());
                return konstituente.getEndsThis();
            } catch (final NoLetterException e) {
                // Diese Konstituente war nur etwas wie "„".
                // Der Text der folgenden Konstituente muss großgeschrieben werden.
                stringBuilder.append(konstituente.getText());
                return StructuralElement.max(SENTENCE, konstituente.getEndsThis());
            }
        }

        stringBuilder.append(konstituente.getText());
        return konstituente.getEndsThis();
    }

    private static String woertlicheRedeabschlussToString(final CharSequence base,
                                                          final String addition) {
        if (base.length() > 0) {
            final CharSequence lastCharBase = base.subSequence(base.length() - 1, base.length());
            if (".".contentEquals(lastCharBase)) {
                return "“";
            }

            if (base.length() > 1) {
                final CharSequence twoLastCharsBase =
                        base.subSequence(base.length() - 2, base.length());

                if (". ".contentEquals(twoLastCharsBase)) {
                    return "“";
                }
            }
        }

        if (!addition.trim().startsWith(".“")
                && !addition.trim().startsWith("!“")
                && !addition.trim().startsWith("?“")
                && !addition.trim().startsWith("…“")
                // Kein Satzende
                && !addition.trim().startsWith("“")) {
            return "“";
        }

        return "";
    }

    private static String satzzeichenToString(final String satzzeichen,
                                              final String addition) {
        if (spaceNeeded(satzzeichen, addition)) {
            return satzzeichen + " ";
        }

        return satzzeichen;
    }

    @NonNull
    public IKonstituenteOrStructuralElement buildKonstituenteOrStructuralElement(
            final String text,
            final boolean vorkommaNoetig,
            final boolean vordoppelpunktNoetig,
            final StructuralElement startsNew,
            final boolean woertlicheRedeNochOffen,
            final boolean kommaStehtAus,
            final StructuralElement brreak) {
        if (text.isEmpty()) {
            return StructuralElement.max(startsNew, brreak);
        }

        return buildKonstituente(text, vorkommaNoetig, vordoppelpunktNoetig, startsNew,
                woertlicheRedeNochOffen, kommaStehtAus, brreak);
    }

    @NonNull
    private Konstituente buildKonstituente(final String text,
                                           final boolean vorkommaNoetig,
                                           final boolean vordoppelpunktNoetig,
                                           final StructuralElement startsNew,
                                           final boolean woertlicheRedeNochOffen,
                                           final boolean kommaStehtAus,
                                           final StructuralElement brreak) {
        final PhorikKandidat resPhorikKandidat = findPhorikKandidat();
        final NumerusGenus resKannAlsBezugsobjektVerstandenWerdenFuer =
                resPhorikKandidat != null ?
                        resPhorikKandidat.getNumerusGenus() :
                        calcKannAlsBezugsobjektVerstandenWerdenFuer();

        return new Konstituente(
                text,
                vorkommaNoetig,
                vordoppelpunktNoetig, startsNew, woertlicheRedeNochOffen,
                kommaStehtAus,
                brreak, resKannAlsBezugsobjektVerstandenWerdenFuer,
                resPhorikKandidat != null ? resPhorikKandidat.getBezugsobjekt() : null
        );
    }

    /**
     * Entfernt das letzte Vorkommen dieser Konstituentenfolge - wobei auch die enthaltenen
     * {@link StructuralElement}s genau übereinstimmen müssen - nicht jedoch die
     * Vorkommata etc.
     */
    @Nullable
    public Konstituentenfolge cutLast(
            @Nullable final Konstituentenfolge parts) {
        if (parts == null) {
            return this;
        }

        final Konstituentenfolge res = reverse().cutFirst(
                parts.reverse());
        if (res == null) {
            return null;
        }

        return res.reverse();
    }

    private Konstituentenfolge withKommaStehtAus() {
        if (konstituenten.get(konstituenten.size() - 1) instanceof StructuralElement) {
            // WORD kann nicht enthalten sein, am Ende der Konstituentenfolge endet also
            // der Satz.
            // Kein Komma nötig.
            return this;
        }

        return new Konstituentenfolge(
                ImmutableList.<IKonstituenteOrStructuralElement>builder()
                        .addAll(konstituenten.subList(0, size() - 1)) // evtl. leer
                        .add(((Konstituente) get(size() - 1)).withKommaStehtAus())
                        .build());
    }

    @CheckReturnValue
    Konstituentenfolge withVorkommaNoetig() {
        return withVorkommaNoetigMin(true);
    }

    /**
     * Gibt eine Kopie der Konstituentenfolge zurück - unverändert, falls
     * {@code vorkommaNoetigMin} {@code false} ist, sonst mit der Angabe, dass
     * ein Vorkomma nötig ist.
     */
    @NonNull
    public Konstituentenfolge withVorkommaNoetigMin(final boolean vorkommaNoetigMin) {
        if (!vorkommaNoetigMin || konstituenten.get(0) instanceof StructuralElement) {
            // WORD kann nicht enthalten sein, mit der Konstituentenfolge beginnt also
            // ein neuer
            // Satz. Kein Komma nötig.
            return this;
        }

        return new Konstituentenfolge(
                ImmutableList.<IKonstituenteOrStructuralElement>builder()
                        .add(((Konstituente) get(0)).withVorkommaNoetigMin(true))
                        .addAll(konstituenten.subList(1, size())) // evtl. leer
                        .build());
    }

    /**
     * Entfernt das erste Vorkommen dieser Konstituentenfolge - wobei auch die enthaltenen
     * {@link StructuralElement}s genau übereinstimmen müssen - nicht jedoch die
     * Vorkommata etc.
     */
    @SuppressWarnings("UnstableApiUsage")
    @Nullable
    public Konstituentenfolge cutFirst(@Nullable final Konstituentenfolge part) {
        if (part == null) {
            return this;
        }
        final ImmutableList.Builder<IKonstituenteOrStructuralElement> resBuilder =
                ImmutableList.builderWithExpectedSize(size() - part.size());
        boolean found = false;
        int i = 0;

        while (i < size()) {
            if (!found
                    && i <= size() - part.size()
                    && new Konstituentenfolge(konstituenten.subList(i, i + part.size()))
                    .equals(part)) {
                found = true;
                i = i + part.size();

                continue;
            }

            resBuilder.add(get(i));
            i = i + 1;
        }

        checkArgument(found, "Konstituente(n) nicht gefunden. "
                + "Konstituente(n) %s nicht gefunden in %s", part, this);

        final ImmutableList<IKonstituenteOrStructuralElement> res = resBuilder.build();

        if (res.isEmpty()) {
            return null;
        }

        return new Konstituentenfolge(res);
    }

    private Konstituentenfolge reverse() {
        return new Konstituentenfolge(konstituenten.reverse());
    }

    @NonNull
    @Override
    public Iterator<IKonstituenteOrStructuralElement> iterator() {
        return konstituenten.iterator();
    }

    public int size() {
        return konstituenten.size();
    }

    @NonNull
    public Stream<IKonstituenteOrStructuralElement> stream() {
        return konstituenten.stream();
    }

    public IKonstituenteOrStructuralElement get(final int index) {
        return konstituenten.get(index);
    }

    @Override
    public ImmutableList<Konstituentenfolge> toAltKonstituentenfolgen() {
        return ImmutableList.of(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Konstituentenfolge that = (Konstituentenfolge) o;
        return konstituenten.equals(that.konstituenten);
    }

    @Override
    public int hashCode() {
        return Objects.hash(konstituenten);
    }

    @NonNull
    @Override
    public String toString() {
        return "Konstituentenfolge{" +
                "konstituenten=" + konstituenten +
                '}';
    }
}
