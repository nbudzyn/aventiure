package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.concurrent.Immutable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * Eine Folge von {@link Konstituente}n.
 */
@Immutable
public class Konstituentenfolge implements Iterable<Konstituente> {
    private final ImmutableList<Konstituente> konstituenten;

    @Nullable
    public static Konstituentenfolge kf(final Iterable<?> parts) {
        return joinToKonstituentenfolge(parts);
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
    @Nullable
    public static Konstituentenfolge schliesseInKommaEin(
            @Nullable final Konstituentenfolge input) {
        if (input == null) {
            return null;
        }

        return input.withKommaStehtAus().withVorkommaNoetig();
    }

    /**
     * Fügt diese Teile zu einer Liste von Konstituenten zusammen.
     *
     * @return Eine - ggf. leere - Liste von Konstituenten, enthält nicht <code>null</code>
     */
    @Nullable
    public static Konstituentenfolge joinToKonstituentenfolge(final Object... parts) {
        return joinToKonstituentenfolge(asList(parts));
    }

    /**
     * Fügt diese Teile zu einer Liste von Konstituenten zusammen.
     *
     * @return Eine - ggf. leere - Liste von Konstituenten, enthält nicht <code>null</code>
     */
    @Nullable
    static Konstituentenfolge joinToKonstituentenfolge(final Iterable<?> parts) {
        final ImmutableList.Builder<Konstituente> resBuilder = ImmutableList.builder();
        for (final Object part : parts) {
            if (part == null) {
                continue;
            }

            final Konstituentenfolge partKonstituentenfolge;
            if (part.getClass().isArray()) {
                partKonstituentenfolge = joinToKonstituentenfolge((Object[]) part);
            } else if (part instanceof Konstituentenfolge) {
                partKonstituentenfolge = joinToKonstituentenfolge((Konstituentenfolge) part);
            } else if (part instanceof Iterable<?>) {
                // FIXME Hier bei Satz Alternativen erzeugen!
                throw new IllegalStateException(
                        "Iterables sollen nur noch für Alternativen verwendet werden...");
            } else if (part instanceof Konstituente) {
                partKonstituentenfolge = new Konstituentenfolge((Konstituente) part);
            } else if (part instanceof Wortfolge) {
                partKonstituentenfolge = new Konstituentenfolge(Konstituente.k((Wortfolge) part));
            } else {
                partKonstituentenfolge = new Konstituentenfolge(Konstituente.k(part.toString()));
            }

            if (partKonstituentenfolge != null) {
                resBuilder.addAll(partKonstituentenfolge);
            }
        }

        final ImmutableList<Konstituente> res = resBuilder.build();

        if (res.isEmpty()) {
            return null;
        }

        return new Konstituentenfolge(res);
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
     * Fügt diese Konstituenten zu einer einzigen Konsituente zusammen.
     * <p>
     * Diese Methode wird man nur selten verwenden wollen - vgl.
     * {@link Konstituentenfolge#joinToKonstituentenfolge(Object...)}!
     *
     * @return Eine einzige Konstituente- ggf. null
     */
    public Konstituente joinToNullSingleKonstituente() {
        return Konstituente.k(Wortfolge.joinToWortfolge(this))
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
