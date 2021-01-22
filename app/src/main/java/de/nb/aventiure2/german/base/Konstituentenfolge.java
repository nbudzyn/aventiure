package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

/**
 * Eine Folge von {@link Konstituente}n.
 */
@Immutable
public class Konstituentenfolge implements Iterable<Konstituente> {
    private final ImmutableList<Konstituente> konstituenten;

    @Nullable
    public static Konstituentenfolge kf(final Iterable<?> parts) {
        return joinToNullKonstituentenfolge(parts);
    }

    public Konstituentenfolge(final Konstituente konstituente) {
        this(ImmutableList.of(konstituente));
    }

    private Konstituentenfolge(final Collection<Konstituente> konstituenten) {
        checkNotNull(konstituenten, "konstituenten  is null");
        checkArgument(!konstituenten.isEmpty(), "konstituenten  is empty");
        checkArgument(konstituenten.stream().noneMatch(Objects::isNull));

        this.konstituenten = ImmutableList.copyOf(konstituenten);
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
    public static Konstituentenfolge joinToKonstituentenfolge(
            final
            // FIXME CharSequence ?! Das wäre quasi type-safe!!
                    Object... parts) {
        return Wortfolge.checkJoiningResultNotNull(joinToNullKonstituentenfolge(parts), parts);
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
    static Collection<Konstituentenfolge> joinToAltKonstituentenfolgen(
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
                        joinToAltKonstituentenfolgen(((Stream<?>) part).collect(toSet()));
            } else if (part instanceof Collection<?>) {
                alternativePartKonstituentenfolgen =
                        ((Collection<?>) part).stream()
                                .map(Konstituentenfolge::joinToNullKonstituentenfolge)
                                .collect(Collectors.toSet());
            } else if (part instanceof Konstituente) {
                alternativePartKonstituentenfolgen =
                        Collections.singletonList(new Konstituentenfolge((Konstituente) part));
            } else if (part instanceof Wortfolge) {
                alternativePartKonstituentenfolgen =
                        Collections.singletonList(
                                new Konstituentenfolge(Konstituente.k((Wortfolge) part)));
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
     * Schneidet die einzelnen <code>singleParts</code> einen nach dem anderen aus dem Input.
     * Doppelt auftretende Texte werden also auch zweimal herausgeschnitten.
     * Für jeden einzelnen Schnitt wird  {@link #cutFirst(Konstituentenfolge)} verwendet.
     */
    @Nullable
    public static Konstituentenfolge cutFirstOneByOne(@Nullable final Konstituentenfolge input,
                                                      final List<Konstituente> singleParts) {
        if (input == null) {
            return null;
        }

        Konstituentenfolge res = input;
        for (final Konstituente singlePart : singleParts) {
            res = res.cutFirst(singlePart);
            if (res == null) {
                return null;
            }
        }

        return res;
    }

    /**
     * Fügt diese Konstituentenfolge zu einem String zusammen, wobei ein nichtleerer
     * String das Ergebnis sein muss. Diese Methode darf nur verwendet werden,
     * wenn nach dem letzten der Teile definitiv kein Komma aussteht - oder das
     * ausstehende Kommma auf andere Weise behandelt wird.
     */
    public String joinToString() {
        return Wortfolge.joinToWortfolge(this).toStringFixWoertlicheRedeNochOffen();
    }

    /**
     * Fügt diese Konstituenten zu einer einzigen Konsituente zusammen - es darf
     * sich nich {@code null} ergeben.
     * <p>
     * Diese Methode wird man nur selten verwenden wollen - vgl.
     * {@link Konstituentenfolge#joinToNullKonstituentenfolge(Object...)}!
     *
     * @return Eine einzige Konstituente, nie null
     */
    public Konstituente joinToSingleKonstituente() {
        @Nullable final Konstituente res = joinToNullSingleKonstituente();

        if (res == null) {
            throw new IllegalStateException("Konstituentenfolge was joined to null: " + this);
        }

        return res;
    }

    /**
     * Fügt diese Konstituenten zu einer einzigen Konsituente zusammen.
     * <p>
     * Diese Methode wird man nur selten verwenden wollen - vgl.
     * {@link Konstituentenfolge#joinToNullKonstituentenfolge(Object...)}!
     *
     * @return Eine einzige Konstituente- ggf. null
     */
    private Konstituente joinToNullSingleKonstituente() {
        final Wortfolge wortfolge = Wortfolge.joinToWortfolge(this);

        final NumerusGenus kannAlsBezugsobjektVerstandenWerdenFuer =
                wortfolge.getPhorikKandidat() != null ?
                        wortfolge.getPhorikKandidat().getNumerusGenus() :
                        Wortfolge.
                                calcKannAlsBezugsobjektVerstandenWerdenFuer(this);
        return Konstituente.k(wortfolge,
                kannAlsBezugsobjektVerstandenWerdenFuer)
                .withVorkommaNoetig(vorkommaNoetig());
    }

    private boolean vorkommaNoetig() {
        return iterator().next().vorkommaNoetig();
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
    private Konstituentenfolge cutFirst(
            @Nullable final Konstituentenfolge part) {
        requireNonNull(this, "input");

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
        return new Konstituentenfolge(Lists.reverse(konstituenten));
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
