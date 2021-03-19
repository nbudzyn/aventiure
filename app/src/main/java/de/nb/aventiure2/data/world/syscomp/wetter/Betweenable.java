package de.nb.aventiure2.data.world.syscomp.wetter;

public interface Betweenable<T> extends Comparable<T> {
    default boolean isBetweenIncluding(final T one, final T other) {
        return compareTo(one) >= 0 && compareTo(other) <= 0;
    }
}
