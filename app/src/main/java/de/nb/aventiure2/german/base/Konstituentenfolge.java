package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import com.google.common.collect.ImmutableList;

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

import de.nb.aventiure2.german.satz.Satz;
import de.nb.aventiure2.german.string.NoLetterException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.base.GermanUtil.spaceNeeded;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.string.GermanStringUtil.beginnStehtCapitalizeNichtImWeg;
import static de.nb.aventiure2.german.string.GermanStringUtil.breakToString;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Eine Folge von {@link Konstituente}n.
 */
@Immutable
public class Konstituentenfolge implements Iterable<IKonstituenteOrStructuralElement> {
    private static final int GEDAECHTNISWEITE_PHORIK = 6;
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
    public static Collection<Konstituentenfolge> joinToAltKonstituentenfolgen(
            final Iterable<?> parts) {
        // IDEA Ggf. Konstituentenfolge und AbstractDescription zusammenführen?

        ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>>
                alternativeKonstituentenfolgen = new ArrayList<>();

        alternativeKonstituentenfolgen.add(ImmutableList.builder());

        for (final Object part : parts) {
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
                alternativePartKonstituentenfolgen =
                        Stream.of((Object[]) part)
                                .flatMap(p -> joinToAltKonstituentenfolgen(p).stream())
                                .collect(Collectors.toSet());
            } else if (part instanceof Collection<?>) {
                // Alternativen!
                alternativePartKonstituentenfolgen =
                        ((Collection<?>) part).stream()
                                .flatMap(p -> joinToAltKonstituentenfolgen(p).stream())
                                .collect(Collectors.toSet());
            } else if (part instanceof Satz) {
                // Alternativen!
                alternativePartKonstituentenfolgen = ((Satz) part).altVerzweitsaetze();
            } else if (part instanceof Konstituentenfolge) {
                alternativePartKonstituentenfolgen =
                        Collections.singletonList(
                                joinToNullKonstituentenfolge((Konstituentenfolge) part));
            } else if (part instanceof IKonstituenteOrStructuralElement) {
                alternativePartKonstituentenfolgen =
                        Collections.singletonList(
                                part == WORD ? null :
                                        new Konstituentenfolge(
                                                (IKonstituenteOrStructuralElement) part));
            } else if (part == null || "".equals(part)) {
                alternativePartKonstituentenfolgen = Collections.singletonList(null);
            } else {
                alternativePartKonstituentenfolgen =
                        Collections.singletonList(
                                new Konstituentenfolge(Konstituente.k(part.toString())));
            }

            final ArrayList<ImmutableList.Builder<IKonstituenteOrStructuralElement>>
                    ergaenzteAlternativeKonstituentenfolgen = new ArrayList<>();

            for (final ImmutableList.Builder<IKonstituenteOrStructuralElement> alternative :
                    alternativeKonstituentenfolgen) {
                for (final Konstituentenfolge alternativePartKonstituentenfolge :
                        alternativePartKonstituentenfolgen) {
                    if (alternativePartKonstituentenfolge != null) {
                        final ImmutableList.Builder<IKonstituenteOrStructuralElement>
                                ergaenzteKonstituentenfolge = ImmutableList.builder();
                        ergaenzteKonstituentenfolge.addAll(alternative.build());
                        ergaenzteKonstituentenfolge
                                .addAll(alternativePartKonstituentenfolge.konstituenten);
                        ergaenzteAlternativeKonstituentenfolgen.add(ergaenzteKonstituentenfolge);
                    } else {
                        ergaenzteAlternativeKonstituentenfolgen.add(alternative);
                    }
                }
            }

            alternativeKonstituentenfolgen = ergaenzteAlternativeKonstituentenfolgen;
        }

        final HashSet<Konstituentenfolge> res = new HashSet<>();
        for (final ImmutableList.Builder<IKonstituenteOrStructuralElement> alternative :
                alternativeKonstituentenfolgen) {
            final ImmutableList<IKonstituenteOrStructuralElement> konstituenten =
                    alternative.build();

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

    private boolean calcKannAlsBezugsobjektVerstandenWerdenFuer(final NumerusGenus numerusGenus) {
        final Pair<Integer, Boolean> phorikKandidatAndSicherheit =
                findPhorikKandidatAndSicherheit(numerusGenus);

        requireNonNull(phorikKandidatAndSicherheit.second,
                "phorikKandidatAndSicherheit.second null");

        if (phorikKandidatAndSicherheit.first != null) {
            return true;
        }

        return !phorikKandidatAndSicherheit.second;
    }

    @Nullable
    private NumerusGenus calcKannAlsBezugsobjektVerstandenWerdenFuer() {
        // Dies ist eine grobe Näherung - natürlich könnten in der Konstituentenfolge
        // leicht alle möglichen Genera / Numeri als mögliche Bezugsobjekte vorkommen.
        return Stream.of(M, F, PL_MFN, N)
                .filter(this::calcKannAlsBezugsobjektVerstandenWerdenFuer)
                .findFirst()
                .orElse(null);
    }

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
                        // Es gab bereits ein Bezugsobjekt, und zwar ein anderes!
                        indexVorigerAbweichenderKandidat = indexKandidat;
                    }

                    indexKandidat = i;
                } else if (konstituente.koennteAlsBezugsobjektVerstandenWerdenFuer(numerusGenus)
                        && indexKandidat != null) {

                    //  Doppeldeutigkeit verhindern: "Du nimmst den Ball und den Schuh und wirfst
                    //  ihn
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
            } else if (konst instanceof StructuralElement) {
                if (konst == CHAPTER) {
                    indexVorigerAbweichenderKandidat = null;
                    indexKandidat = null;
                }
            } else {
                throw new IllegalStateException("Unexpected konst: " + konst);
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

        if (((Konstituente) get(indexKandidat)).getPhorikKandidat() == null) {
            // Etwas anderes (bei dem kein Bezugsobjekt angegeben wurde) könnte
            // als Bezugsobjekt verstanden werden.
            return new Pair<>(null, false);
        }

        // Es wurde ein eindeutiger Kandidat gefunden
        return new Pair<>(indexKandidat, true);
    }

    private PhorikKandidat findPhorikKandidat() {
        final Integer indexKandidatM = interpretPair(
                findPhorikKandidatAndSicherheit(M));
        final Integer indexKandidatF = interpretPair(
                findPhorikKandidatAndSicherheit(F));
        final Integer indexKandidatN = interpretPair(
                findPhorikKandidatAndSicherheit(N));
        final Integer indexKandidatPL = interpretPair(
                findPhorikKandidatAndSicherheit(PL_MFN));

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
        boolean firstAbgesehenVonWORD = true;
        boolean firstKonstituente = true;
        boolean resVorkommaNoetig = false;
        boolean resVordoppelpunktNoetig = false;
        StructuralElement resStartsNew = WORD;
        StructuralElement brreak = WORD;
        boolean woertlicheRedeNochOffen = false;
        boolean kommaStehtAus = false;
        for (final IKonstituenteOrStructuralElement konst : this) {
            if (konst instanceof StructuralElement) {
                brreak =
                        StructuralElement
                                .max(brreak, (StructuralElement) konst);

                if (firstAbgesehenVonWORD) {
                    resStartsNew = brreak;
                }

                if (konst != WORD) {
                    // Vorkomma und Vordoppelpunkt sind in neuem Satz, Absatz, ... nicht nötig
                    firstAbgesehenVonWORD = false;
                }
            } else if (konst instanceof Konstituente) {
                final Konstituente konstituente = (Konstituente) konst;

                final String konstituentenText = konstituente.getText();
                if (woertlicheRedeNochOffen) {
                    if (resTextBuilder.toString().trim().endsWith(".")) {
                        resTextBuilder.append("“");
                    } else if (!konstituentenText.trim().startsWith(".“")
                            && !konstituentenText.trim().startsWith("!“")
                            && !konstituentenText.trim().startsWith("?“")
                            && !konstituentenText.trim().startsWith("…“")
                            // Kein Satzende
                            && !konstituentenText.trim().startsWith("“")) {
                        resTextBuilder.append("“");
                    }
                }

                if (firstKonstituente) {
                    resStartsNew =
                            StructuralElement.max(resStartsNew, konstituente.getStartsNew());
                    firstKonstituente = false;
                }

                if (firstAbgesehenVonWORD) {
                    resVorkommaNoetig =
                            konstituente.vorkommaNoetig() && resStartsNew == WORD;
                    resVordoppelpunktNoetig = konstituente.vordoppelpunktNoetig();
                }

                boolean capitalize = false;

                if (!firstAbgesehenVonWORD && brreak != CHAPTER
                        && brreak != PARAGRAPH
                        && konstituente.vordoppelpunktNoetig()) {
                    resTextBuilder.append(":");
                    if (spaceNeeded(":", konstituentenText)) {
                        resTextBuilder.append(" ");
                    }

                    if (brreak != WORD) {
                        capitalize = true;
                    }
                } else if (kommaStehtAus && brreak == WORD
                        || (!firstAbgesehenVonWORD && konstituente.vorkommaNoetig())) {
                    resTextBuilder.append(",");
                    if (spaceNeeded(",", konstituentenText)) {
                        resTextBuilder.append(" ");
                    }
                } else {
                    if (!firstAbgesehenVonWORD && brreak != WORD) {
                        capitalize = true;
                    }

                    resTextBuilder.append(breakToString(
                            resTextBuilder.toString(), brreak,
                            konstituentenText));
                }

                if (capitalize && beginnStehtCapitalizeNichtImWeg(konstituentenText)) {
                    try {
                        resTextBuilder.append(konstituente.capitalizeFirstLetter().getText());
                        brreak = konstituente.getEndsThis();
                    } catch (final NoLetterException e) {
                        // Diese Konstituente war nur etwas wie "„".
                        // Der Text der folgenden Konstituente muss großgeschrieben werden.
                        resTextBuilder.append(konstituente.getText());
                        brreak = StructuralElement.max(SENTENCE, konstituente.getEndsThis());
                    }
                } else {
                    resTextBuilder.append(konstituentenText);
                    brreak = konstituente.getEndsThis();
                }

                kommaStehtAus = konstituente.kommaStehtAus();
                woertlicheRedeNochOffen = konstituente.woertlicheRedeNochOffen();
                firstAbgesehenVonWORD = false;
            } else {
                throw new IllegalArgumentException("Unexpected konst: " + konst);
            }
        }

        final PhorikKandidat resPhorikKandidat = findPhorikKandidat();
        final NumerusGenus resKannAlsBezugsobjektVerstandenWerdenFuer =
                resPhorikKandidat != null ?
                        resPhorikKandidat.getNumerusGenus() :
                        calcKannAlsBezugsobjektVerstandenWerdenFuer();

        final String resText = resTextBuilder.toString().trim();

        if (resText.isEmpty()) {
            return StructuralElement.max(resStartsNew, brreak);
        }

        return new Konstituente(
                resText,
                resVorkommaNoetig,
                resVordoppelpunktNoetig, resStartsNew, woertlicheRedeNochOffen,
                kommaStehtAus,
                brreak, resKannAlsBezugsobjektVerstandenWerdenFuer,
                resPhorikKandidat != null ? resPhorikKandidat.getBezugsobjekt() : null
        );
    }

    /**
     * Entfernt das letzte Vorkommen dieser Konstituentenfolge - wobei auch die enthaltenen
     * {@link StructuralElement}s genau übereinstimmen müssen - nicht jedoch die Vorkommata etc.
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
            // Am Ende der Konstituentenfolge endet der Satz. Kein Komma nötig.
            return this;
        }

        return new Konstituentenfolge(
                ImmutableList.<IKonstituenteOrStructuralElement>builder()
                        .addAll(subFolge(0, size() - 1))
                        .add(((Konstituente) get(size() - 1)).withKommaStehtAus())
                        .build());
    }

    @CheckReturnValue
    public Konstituentenfolge withVorkommaNoetig() {
        if (konstituenten.get(konstituenten.size() - 1) instanceof StructuralElement) {
            // Mit der Konstituentenfolge beginnt ein neuer Satz. Kein Vorkomma nötig.
            return this;
        }

        return new Konstituentenfolge(
                ImmutableList.<IKonstituenteOrStructuralElement>builder()
                        .add(((Konstituente) get(0)).withVorkommaNoetig(true))
                        .addAll(subFolge(1, size()))
                        .build());
    }

    /**
     * Entfernt das erste Vorkommen dieser Konstituentenfolge - wobei auch die enthaltenen
     * {@link StructuralElement}s genau übereinstimmen müssen - nicht jedoch die Vorkommata etc.
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
                    && subFolge(i, i + part.size()).equals(part)) {
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

    private Konstituentenfolge subFolge(final int fromIndex, final int toIndex) {
        return new Konstituentenfolge(konstituenten.subList(fromIndex, toIndex));
    }

    @NonNull
    public Stream<IKonstituenteOrStructuralElement> stream() {
        return konstituenten.stream();
    }

    public IKonstituenteOrStructuralElement get(final int index) {
        return konstituenten.get(index);
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
