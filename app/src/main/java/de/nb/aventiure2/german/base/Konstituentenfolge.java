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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static de.nb.aventiure2.german.base.GermanUtil.spaceNeeded;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;
import static de.nb.aventiure2.german.base.NumerusGenus.PL_MFN;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Eine Folge von {@link Konstituente}n.
 */
@Immutable
public class Konstituentenfolge implements Iterable<Konstituente> {
    private static final int GEDAECHTNISWEITE_PHORIK = 6;
    private final ImmutableList<Konstituente> konstituenten;

    @Nullable
    public static Konstituentenfolge kf(final Iterable<?> parts) {
        return joinToNullKonstituentenfolge(parts);
    }

    public Konstituentenfolge(final Konstituente konstituente) {
        this(ImmutableList.of(konstituente));
    }

    private Konstituentenfolge(final ImmutableList<Konstituente> konstituenten) {
        // Wird auch mit ImmutableList.subList() aufgerufen, was leider zu einer
        // Kopie führt. Daher ImmutableList verlangen!

        checkNotNull(konstituenten, "konstituenten  is null");
        checkArgument(!konstituenten.isEmpty(), "konstituenten  is empty");
        checkArgument(konstituenten.stream().noneMatch(Objects::isNull));

        this.konstituenten = konstituenten;
    }

    /**
     * Gibt denselben Input zurück, wobei ein Vorkomma und ein Folgekomma gefordert werden.
     * Ist der Input leer, wird eine leere Konsitutenten-Liste zurückgeben.
     */
    @SuppressWarnings("GrazieInspection")
    @Nullable
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
    public static Konstituentenfolge joinToNullKonstituentenfolge(final Object... parts) {
        return joinToNullKonstituentenfolge(asList(parts));
    }

    /**
     * Fügt diese Teile zu einer Konstituentenfolge zusammen.
     *
     * @return Eine Konstituentenfolge - oder <code>null</code>
     */
    @Nullable
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
    public static Collection<Konstituentenfolge> joinToAltKonstituentenfolgen(
            final Iterable<?> parts) {
        ArrayList<ImmutableList.Builder<Konstituente>> alternativeKonstituentenfolgen =
                new ArrayList<>();

        alternativeKonstituentenfolgen.add(ImmutableList.builder());

        for (final Object part : parts) {
            final Collection<Konstituentenfolge> alternativePartKonstituentenfolgen;

            if (part == null) {
                alternativePartKonstituentenfolgen = Collections.singletonList(null);
            } else if (part.getClass().isArray()) {
                alternativePartKonstituentenfolgen =
                        Collections.singletonList(joinToNullKonstituentenfolge((Object[]) part));
            } else if (part instanceof Konstituentenfolge) {
                alternativePartKonstituentenfolgen =
                        Collections
                                .singletonList(
                                        joinToNullKonstituentenfolge((Konstituentenfolge) part));
            } else if (part instanceof Stream<?>) {
                alternativePartKonstituentenfolgen =
                        ((Stream<?>) part)
                                .map(Konstituentenfolge::joinToNullKonstituentenfolge)
                                .collect(Collectors.toSet());
                // Früher stand hier dies:
                // alternativePartKonstituentenfolgen =
                //       joinToAltKonstituentenfolgen(((Stream<?>) part).collect(toSet()));
            } else if (part instanceof Collection<?>) {
                alternativePartKonstituentenfolgen =
                        ((Collection<?>) part).stream()
                                .map(Konstituentenfolge::joinToNullKonstituentenfolge)
                                .collect(Collectors.toSet());
            } else if (part instanceof Konstituente) {
                alternativePartKonstituentenfolgen =
                        Collections.singletonList(new Konstituentenfolge((Konstituente) part));
            } else {
                alternativePartKonstituentenfolgen =
                        Collections.singletonList(
                                new Konstituentenfolge(Konstituente.k(part.toString())));
            }

            final ArrayList<ImmutableList.Builder<Konstituente>>
                    ergaenzteAlternativeKonstituentenfolgen = new ArrayList<>();

            for (final ImmutableList.Builder<Konstituente> alternative :
                    alternativeKonstituentenfolgen) {
                for (final Konstituentenfolge alternativePartKonstituentenfolge :
                        alternativePartKonstituentenfolgen) {
                    if (alternativePartKonstituentenfolge != null) {
                        final ImmutableList.Builder<Konstituente> ergaenzteKonstituentenfolge =
                                ImmutableList.builder();
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
        for (final ImmutableList.Builder<Konstituente> alternative :
                alternativeKonstituentenfolgen) {
            final ImmutableList<Konstituente> konstituenten = alternative.build();

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
        return konstituenten.get(konstituenten.size() - 1).kommaStehtAus();
    }

    public boolean isPersonalpronomenEs() {
        if (konstituenten.size() > 1) {
            return false;
        }

        return "es".equals(konstituenten.get(0).getString());
    }

    public boolean isPersonalpronomen() {
        if (konstituenten.size() > 1) {
            return false;
        }

        return konstituenten.get(0).isPersonalpronomen();
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
            final Konstituente konstituente = get(i);

            if (konstituente.getPhorikKandidat() != null
                    && konstituente.getPhorikKandidat().getNumerusGenus().equals(numerusGenus)) {
                if (indexKandidat != null
                        && !requireNonNull(
                        get(indexKandidat).getPhorikKandidat())
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

        if (get(indexKandidat).getPhorikKandidat() == null) {
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

        return get(bestIndex).getPhorikKandidat();
    }

    /**
     * Fügt diese Konstituentenfolge zu einem String zusammen, wobei ein nichtleerer
     * String das Ergebnis sein muss. Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    @NonNull
    public String joinToString() {
        return joinToSingleKonstituente().toStringFixWoertlicheRedeNochOffen();
    }

    /**
     * Fügt diese Konstituenten zu einer einzigen Konsituente zusammen. Damit gehen natürlich
     * Detailinformationen verloren, und man kann nachträglich nicht mehr gut einzelne
     * Teile entfernen, weil z.B. unklar ist, ob auch Kommata entfernt werden müssen.
     */
    @NonNull
    public Konstituente joinToSingleKonstituente() {
        final StringBuilder resString = new StringBuilder(size() * 25);
        boolean first = true;
        boolean vorkommaNoetig = false;
        boolean vordoppelpunktNoetig = false;
        boolean woertlicheRedeNochOffen = false;
        boolean kommaStehtAus = false;
        for (final Konstituente konstituente : this) {
            final String konstituentenString = konstituente.getString();
            if (woertlicheRedeNochOffen) {
                if (resString.toString().trim().endsWith(".")) {
                    resString.append("“");
                } else if (!konstituentenString.trim().startsWith(".“")
                        && !konstituentenString.trim().startsWith("!“")
                        && !konstituentenString.trim().startsWith("?“")
                        && !konstituentenString.trim().startsWith("…“")
                        // Kein Satzende
                        && !konstituentenString.trim().startsWith("“")) {
                    resString.append("“");
                }
            }

            if (first) {
                vorkommaNoetig =
                        konstituente.vorkommaNoetig() &&
                                !GermanUtil.beginnDecktKommaAb(konstituentenString);
                vordoppelpunktNoetig =
                        konstituente.vordoppelpunktNoetig() &&
                                !GermanUtil.beginnDecktDoppelpunktAb(konstituentenString);
            }

            if (!first && konstituente.vordoppelpunktNoetig()
                    && !GermanUtil.beginnDecktDoppelpunktAb(konstituentenString)) {
                resString.append(":");
                if (spaceNeeded(":", konstituentenString)) {
                    resString.append(" ");
                }
            } else if ((kommaStehtAus
                    || (!first && konstituente.vorkommaNoetig()))
                    && !GermanUtil.beginnDecktKommaAb(konstituentenString)) {
                resString.append(",");
                if (spaceNeeded(",", konstituentenString)) {
                    resString.append(" ");
                }
            } else if (spaceNeeded(resString, konstituentenString)) {
                resString.append(" ");
            }

            resString.append(konstituentenString);
            kommaStehtAus = konstituente.kommaStehtAus();
            woertlicheRedeNochOffen = konstituente.woertlicheRedeNochOffen();
            first = false;
        }

        final PhorikKandidat phorikKandidat = findPhorikKandidat();
        final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer =
                phorikKandidat != null ?
                        phorikKandidat.getNumerusGenus() :
                        calcKannAlsBezugsobjektVerstandenWerdenFuer();

        return new Konstituente(
                resString.toString().trim(),
                vorkommaNoetig,
                vordoppelpunktNoetig, woertlicheRedeNochOffen,
                kommaStehtAus,
                kannAlsBezugsobjektVerstandenWerdenFuer,
                phorikKandidat != null ? phorikKandidat.getBezugsobjekt() : null
        );
    }

    @Nullable
    public Konstituentenfolge cutFirst(
            @Nullable final Konstituente part) {
        if (part == null) {
            return this;
        }

        return cutFirst(new Konstituentenfolge(part));
    }

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
        return new Konstituentenfolge(
                ImmutableList.<Konstituente>builder()
                        .addAll(subFolge(0, size() - 1))
                        .add(get(size() - 1).withKommaStehtAus())
                        .build());
    }

    public Konstituentenfolge withVorkommaNoetig() {
        return new Konstituentenfolge(
                ImmutableList.<Konstituente>builder()
                        .add(get(0).withVorkommaNoetig(true))
                        .addAll(subFolge(1, size()))
                        .build());
    }

    @SuppressWarnings("UnstableApiUsage")
    @Nullable
    public Konstituentenfolge cutFirst(@Nullable final Konstituentenfolge part) {
        if (part == null) {
            return this;
        }
        final ImmutableList.Builder<Konstituente> resBuilder =
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

        final ImmutableList<Konstituente> res = resBuilder.build();

        if (res.isEmpty()) {
            return null;
        }

        return new Konstituentenfolge(res);
    }

    public Konstituentenfolge capitalize() {
        return new Konstituentenfolge(
                ImmutableList.<Konstituente>builder()
                        .add(konstituenten.get(0).capitalize())
                        .addAll(konstituenten.subList(1, konstituenten.size()))
                        .build());
    }

    private Konstituentenfolge reverse() {
        return new Konstituentenfolge(konstituenten.reverse());
    }

    @NonNull
    @Override
    public Iterator<Konstituente> iterator() {
        return konstituenten.iterator();
    }

    public int size() {
        return konstituenten.size();
    }

    private Konstituentenfolge subFolge(final int fromIndex, final int toIndex) {
        return new Konstituentenfolge(konstituenten.subList(fromIndex, toIndex));
    }

    @NonNull
    public Stream<Konstituente> stream() {
        return konstituenten.stream();
    }

    public Konstituente get(final int index) {
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
