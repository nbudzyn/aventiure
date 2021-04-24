package de.nb.aventiure2.data.world.base;

import androidx.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public class EnumRange<E extends Enum<? extends E>> {
    private final E from;
    private final E to;

    public static <E extends Enum<? extends E>> EnumRange<E> all(final Class<E> eenum) {
        final E[] enumConstants = requireNonNull(eenum.getEnumConstants());
        return new EnumRange<>(enumConstants[0], enumConstants[enumConstants.length - 1]);
    }

    public static <E extends Enum<? extends E>> EnumRange<E> of(final E from, final E to) {
        return new EnumRange<>(from, to);
    }

    private EnumRange(final E from, final E to) {
        checkArgument(from.ordinal() <= to.ordinal());

        this.from = from;
        this.to = to;
    }

    @SuppressWarnings("unchecked")
    public @Nullable
    EnumRange<E> intersect(final EnumRange<E> other) {
        final int resFromOrdinal = Math.max(from.ordinal(), other.from.ordinal());
        final int resToOrdinal = Math.min(to.ordinal(), other.to.ordinal());

        if (resToOrdinal < resFromOrdinal) {
            return null;
        }

        final E[] enumConstants = requireNonNull(((Class<E>) from.getClass()).getEnumConstants());


        final E resFrom = enumConstants[resFromOrdinal];
        final E resTo = enumConstants[resToOrdinal];

        return of(resFrom, resTo);
    }

    public E clamp(final E enumConstant) {
        if (enumConstant.ordinal() < from.ordinal()) {
            return from;
        }

        if (enumConstant.ordinal() > to.ordinal()) {
            return to;
        }

        return enumConstant;
    }

    public boolean isInRange(final E enumConstant) {
        return from.ordinal() <= enumConstant.ordinal()
                && to.ordinal() >= enumConstant.ordinal();
    }

    public E getFrom() {
        return from;
    }

    public E getTo() {
        return to;
    }
}
