package de.nb.aventiure2.data.world.syscomp.wetter.base;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Änderung eines Wetter-Parameters (z.B. Temperatur)
 */
public class WetterParamChange<E extends Enum<?>> {
    private final E vorher;

    private final E nachher;

    public WetterParamChange(final E vorher, final E nachher) {
        checkArgument(vorher != nachher, "vorher == nachher: %s", vorher);

        this.vorher = vorher;
        this.nachher = nachher;
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
        final WetterParamChange<?> that = (WetterParamChange<?>) o;
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
