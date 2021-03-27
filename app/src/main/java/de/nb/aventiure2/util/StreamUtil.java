package de.nb.aventiure2.util;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;

/**
 * Static helper methods for streams.
 */
public class StreamUtil {
    private StreamUtil() {
    }

    @NonNull
    public static <T, U> ImmutableSet<U> mapToSet(
            final Collection<T> collection,
            final Function<? super T, ? extends U> function) {
        return collection.stream()
                .map(function)
                .collect(toImmutableSet());
    }

    @NonNull
    public static <T, U> ImmutableList<U> mapToList(
            final Collection<T> collection,
            final Function<? super T, ? extends U> function) {
        return collection.stream()
                .map(function)
                .collect(toImmutableList());
    }
}
