package de.nb.aventiure2.german.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public class Konstituente {
    /**
     * Die eigentlichen Wörter und Satzzeichen
     */
    @Nonnull
    private final String string;

    /**
     * Ob noch ein Komma <i>vor dieser Konstituente</i> nötig ist. Alternativ kann ein Punkt,
     * ein Ausrufezeichen, ein Fragezeichen, ein Doppelpunkt oder
     * ein Semikolon vorangehen, der ebenfalls das Komma "abdeckt".
     */
    private final boolean vorkommmaNoetig;

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

    public static ImmutableList<Konstituente> withVorkommaNoetig(
            final Iterable<Konstituente> input) {
        return withVorkommaNoetig(input, true);
    }

    private static ImmutableList<Konstituente> withVorkommaNoetig(
            final Iterable<Konstituente> input, final boolean vorkommaNoetig) {
        checkArgument(!vorkommaNoetig || input.iterator().hasNext(),
                "Leere Konstituentenliste, aber Komma nötig?!");

        final ImmutableList<Konstituente> inputList = ImmutableList.copyOf(input);

        return ImmutableList.<Konstituente>builder()
                .add(inputList.get(0).withVorkommaNoetig(vorkommaNoetig))
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
        return k(Wortfolge.joinToNullWortfolge(parts)).withVorkommaNoetig(
                vorkommaNoetig(parts));
    }


    @Nonnull
    public static List<Konstituente> cutLast(
            final Iterable<Konstituente> input,
            @Nullable final Konstituente part) throws KonstituentenNotFoundException {
        if (part == null) {
            return ImmutableList.copyOf(input);
        }

        return cutLast(input, ImmutableList.of(part));
    }

    @Nonnull
    public static List<Konstituente> cutLast(
            final Iterable<Konstituente> input,
            @Nullable final Iterable<Konstituente> parts) throws KonstituentenNotFoundException {
        return Lists.reverse(
                cutFirst(
                        Lists.reverse(ImmutableList.copyOf(input)),
                        Lists.reverse(ImmutableList.copyOf(parts))));
    }

    @Nonnull
    public static List<Konstituente> cutFirst(
            final Iterable<Konstituente> input,
            @Nullable final Konstituente part) throws KonstituentenNotFoundException {
        if (part == null) {
            return ImmutableList.copyOf(input);
        }

        return cutFirst(input, ImmutableList.of(part));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Nonnull
    private static List<Konstituente> cutFirst(
            final Iterable<Konstituente> input,
            final Iterable<Konstituente> parts) throws KonstituentenNotFoundException {
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

        if (!found) {
            throw new KonstituentenNotFoundException("Konstituente(n) nicht gefunden. "
                    + "Konstituente(n): "
                    + partList
                    + "  nicht gefunden in "
                    + inputList);
        }

        return res.build();
    }

    private static boolean vorkommaNoetig(final Iterable<Konstituente> konstituenten) {
        final Iterator<Konstituente> iter = konstituenten.iterator();
        if (!iter.hasNext()) {
            return false;
        }

        return iter.next().vorkommmaNoetig;
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

    private Konstituente withVorkommaNoetig() {
        return withVorkommaNoetig(true);
    }

    public Konstituente withVorkommaNoetig(final boolean vorkommaNoetig) {
        return new Konstituente(string, vorkommaNoetig, kommmaStehtAus);
    }

    private Konstituente withKommaStehtAus() {
        return k(string, true);
    }

    /**
     * Erzeugt eine Konstituente gemäß dieser Wortfolge.
     */
    public static Konstituente k(final @Nonnull Wortfolge wortfolge) {
        // Wenn die Wortfolge mit Komma anfängt, lassen wir es in der Konsituente stehen.
        // Damit wird das Komma definitiv ausgegeben - auch von Methoden, die das Vorkomma
        // vielleich sonst (ggf. bewusst) verschlucken. Vgl. Wortfolge#joinToNullWortfolge()

        return new Konstituente(
                wortfolge.getString().trim(),
                false,
                wortfolge.kommmaStehtAus());
    }

    /**
     * Erzeugt eine Konstituente, bei der nur dann kein Komma aussteht, wenn der
     * String (getrimmt) mit Komma geendet hat.
     */
    public static Konstituente k(final @Nonnull String string) {
        // Wenn der String mit Komma anfängt, lassen wir es in der Konsituente  stehen.
        // Damit wird das Komma definitiv ausgegeben - auch von Methoden, die das Vorkomma
        // vielleich sonst (ggf. bewusst) verschlucken. Vgl. Wortfolge#joinToNullWortfolge()
        return k(string.trim(), false);
    }

    public static Konstituente k(final @Nonnull String string, final boolean kommaStehtAus) {
        return new Konstituente(string, kommaStehtAus);
    }

    private Konstituente(final String string, final boolean kommmaStehtAus) {
        this(string, false, kommmaStehtAus);
    }

    private Konstituente(final String string, final boolean vorkommaNoetig,
                         final boolean kommmaStehtAus) {
        requireNonNull(string, "string");
        checkArgument(!string.isEmpty(), "String ist empty");

        this.string = string;
        vorkommmaNoetig = vorkommaNoetig;
        this.kommmaStehtAus = kommmaStehtAus;
    }

    public Konstituente capitalize() {
        return new Konstituente(GermanUtil.capitalize(string),
                // Wenn großgeschrieben werden soll, wäre es sinnlos, ein Komma zuvor
                // setzen zu vollen.
                false, kommmaStehtAus);
    }

    @Nonnull
    public String getString() {
        return string;
    }

    boolean vorkommmaNoetig() {
        return vorkommmaNoetig;
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
        final Konstituente that = (Konstituente) o;
        // WICHTIG: Hier darf man nur den Text vergleichen, nicht die Kommata!
        // Ansonsten funktioniert das Ausschneiden nicht mehr richtig
        return string.equals(that.string);
    }

    @Override
    public int hashCode() {
        // WICHTIG: Hier darf man nur den Text eingehen lassen, damit die Methode
        // konsistent mit equals arbeitet.
        return Objects.hash(string);
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString()
                + ": "
                + (vorkommmaNoetig ? "[, ]" : "")
                + string
                + (kommmaStehtAus ? "[, ]" : "");
    }
}
