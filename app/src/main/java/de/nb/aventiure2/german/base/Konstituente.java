package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.base.Wortfolge.w;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public class Konstituente {
    /**
     * Die eigentlichen Wörter und Satzzeichen
     */
    @Nonnull
    private final String string;

    /**
     * Ob noch ein Komma aussteht. Das Komma wird entweder unmittelbar folgen müssen -
     * oder es folgt ein Punkt, ein Ausrufezeichen, ein Fragezeichen, ein Doppelpunkt oder
     * ein Semikolon, der ebenfalls das Komma "abdeckt".
     */
    private final boolean kommmaStehtAus;

    public static Iterable<Konstituente> capitalize(final Iterable<Konstituente> input) {
        final ImmutableList<Konstituente> inputList = ImmutableList.copyOf(input);

        if (inputList.isEmpty()) {
            return ImmutableList.of();
        }

        return ImmutableList.<Konstituente>builder()
                .add(inputList.get(0).capitalize())
                .addAll(inputList.subList(1, inputList.size()))
                .build();
    }

    public static ImmutableList<Konstituente> withKommaStehtAus(
            final Iterable<Konstituente> input) {
        checkArgument(
                input.iterator().hasNext(),
                "Leere Konstituentenliste, aber Komma steht aus?!");

        final ImmutableList<Konstituente> inputList = ImmutableList.copyOf(input);

        return ImmutableList.<Konstituente>builder()
                .addAll(inputList.subList(0, inputList.size() - 1))
                .add(inputList.get(inputList.size() - 1).withKommaStehtAus())
                .build();
    }

    /**
     * Fügt diese Teile zu einer Liste von Konstituenten zusammen.
     *
     * @return Eine - ggf. leere - Liste von Konstituenten, enthält nicht <code>null</code>
     */
    public static Iterable<Konstituente> joinToKonstituenten(final Object... parts) {
        return joinToKonstituenten(asList(parts));
    }

    /**
     * Fügt diese Teile zu einer Liste von Konstituenten zusammen.
     *
     * @return Eine - ggf. leere - Liste von Konstituenten, enthält nicht <code>null</code>
     */
    static ImmutableList<Konstituente> joinToKonstituenten(final Iterable<?> parts) {
        final ImmutableList.Builder<Konstituente> res = ImmutableList.builder();
        for (final Object part : parts) {
            if (part == null) {
                continue;
            }

            @Nullable final Iterable<Konstituente> partKonstituenten;
            if (part.getClass().isArray()) {
                partKonstituenten = joinToKonstituenten((Object[]) part);
            } else if (part instanceof Iterable<?>) {
                partKonstituenten = joinToKonstituenten((Iterable<?>) part);
            } else if (part instanceof Konstituente) {
                partKonstituenten = ImmutableList.of((Konstituente) part);
            } else if (part instanceof Wortfolge) {
                partKonstituenten = ImmutableList.of(k((Wortfolge) part));
            } else {
                partKonstituenten = ImmutableList.of(k(part.toString()));
            }

            res.addAll(partKonstituenten);
        }

        return res.build();
    }

    /**
     * Fügt diese Konstituenten zu einer einzigen Konsituente zusammen.
     * <p>
     * Diese Methode wird man nur selten verwenden wollen - vgl.
     * {@link #joinToKonstituenten(Object...)}!
     *
     * @return Eine einzige Konstituente- ggf. null
     */
    @Nullable
    public static Konstituente joinToNullSingleKonstituente(final Iterable<Konstituente> parts) {
        return k(
                Wortfolge.joinToNullWortfolge(
                        StreamSupport.stream(parts.spliterator(), false)
                                .map(Konstituente::toWortfolge)
                                .collect(Collectors.toList())
                ));
    }


    @Nonnull
    public static List<Konstituente> cutLast(
            final Iterable<Konstituente> input,
            @Nullable final Konstituente part) {
        if (part == null) {
            return ImmutableList.copyOf(input);
        }

        return cutLast(input, ImmutableList.of(part));
    }

    @Nonnull
    public static List<Konstituente> cutLast(
            final Iterable<Konstituente> input,
            @Nullable final Iterable<Konstituente> parts) {
        return Lists.reverse(
                cutFirst(
                        Lists.reverse(ImmutableList.copyOf(input)),
                        Lists.reverse(ImmutableList.copyOf(parts))));
    }

    @Nonnull
    public static List<Konstituente> cutFirst(
            final Iterable<Konstituente> input,
            @Nullable final Konstituente part) {
        if (part == null) {
            return ImmutableList.copyOf(input);
        }

        return cutFirst(input, ImmutableList.of(part));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Nonnull
    private static List<Konstituente> cutFirst(
            final Iterable<Konstituente> input,
            final Iterable<Konstituente> parts) {
        final ImmutableList<Konstituente> inputList = ImmutableList.copyOf(input);
        final ImmutableList<Konstituente> partList = ImmutableList.copyOf(parts);

        requireNonNull(inputList, "inputList");
        checkArgument(inputList.stream().noneMatch(Objects::isNull));
        checkArgument(inputList.stream().map(Konstituente::getString).noneMatch(String::isEmpty));
        checkArgument(
                inputList.stream().map(Konstituente::getString).noneMatch(s -> s.startsWith(" ")));
        checkArgument(
                inputList.stream().map(Konstituente::getString).noneMatch(s -> s.endsWith(" ")));

        if (partList.isEmpty()) {
            return inputList;
        }

        final ImmutableList.Builder<Konstituente> res =
                ImmutableList.builderWithExpectedSize(inputList.size() - partList.size());
        boolean found = false;
        int i = 0;

        while (i < inputList.size()) {
            if (!found
                    && i <= inputList.size() - partList.size()
                    && inputList.subList(i, i + partList.size()).equals(partList)) {
                found = true;
                i = i + partList.size();

                continue;
            }

            res.add(inputList.get(i));
            i = i + 1;
        }

        checkArgument(found, "Konstituente(n) nicht gefunden. "
                + "Konstituente(n): %s  nicht gefunden in %s", partList, inputList);

        return res.build();
    }

    public static boolean kommaStehtAus(final Iterable<Konstituente> konstituenten) {
        final Iterator<Konstituente> iter = konstituenten.iterator();
        if (!iter.hasNext()) {
            return false;
        }

        Konstituente last;
        do {
            last = iter.next();
        } while (iter.hasNext());

        return last.kommmaStehtAus;
    }

    private Konstituente withKommaStehtAus() {
        return k(string, true);
    }

    /**
     * Erzeugt eine Konstituente gemäß dieser Wortfolge.
     */
    public static Konstituente k(final @Nonnull Wortfolge wortfolge) {
        return k(wortfolge.getString(), wortfolge.kommmaStehtAus());
    }

    /**
     * Erzeugt eine Konstituente, bei der nur dann kein Komma aussteht, wenn der
     * String (getrimmt) mit Komma geendet hat.
     */
    public static Konstituente k(final @Nonnull String string) {
        final String trimmed = string.trim();
        if (trimmed.endsWith(",")) {
            return k(trimmed.substring(0, trimmed.length() - 1), true);
        }

        return k(trimmed, false);
    }

    public static Konstituente k(final @Nonnull String string, final boolean kommaStehtAus) {
        return new Konstituente(string, kommaStehtAus);
    }

    private Konstituente(final String string, final boolean kommmaStehtAus) {
        requireNonNull(string, "string");
        checkArgument(!string.isEmpty(), "String ist empty");

        this.string = string;
        this.kommmaStehtAus = kommmaStehtAus;
    }

    public Konstituente capitalize() {
        return k(GermanUtil.capitalize(string), kommmaStehtAus);
    }

    Wortfolge toWortfolge() {
        return w(getString(), kommmaStehtAus);
    }

    @Nonnull
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
        final Konstituente that = (Konstituente) o;
        return kommmaStehtAus == that.kommmaStehtAus &&
                string.equals(that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(string);
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
