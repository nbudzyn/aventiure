package de.nb.aventiure2.data.world.base;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;


/**
 * Eine Änderung (z.B. der Zeit oder der Temperatur).
 */
public class Change<E> {
    private final E vorher;

    private final E nachher;

    public Change(final E vorher, final E nachher) {
        checkArgument(!vorher.equals(nachher), "vorher gleich nachher: %s", vorher);

        this.vorher = vorher;
        this.nachher = nachher;
    }

    /**
     * Führt diese Funktion auf {@link #vorher} und {@link #nachher} aus. Setzt voraus,
     * dass die Ergebnisse unterschiedlich sind!
     */
    public <F> Change<F> map(final Function<E, F> function) {
        return new Change<>(function.apply(vorher), function.apply(nachher));
    }

    public boolean wasntBeforeButIsAfter(final Predicate<E> predicate) {
        return !predicate.test(vorher) && predicate.test(nachher);
    }

    public E getVorher() {
        return vorher;
    }

    public E getNachher() {
        return nachher;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Change<?> that = (Change<?>) o;
        return vorher.equals(that.vorher) &&
                nachher.equals(that.nachher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vorher, nachher);
    }

    @Override
    public String toString() {
        return vorher + " -> " + nachher;
    }
}
