package de.nb.aventiure2.data.world.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;

/**
 * Changeable Data that has a key.
 */
public abstract class AbstractKeyData<K> {
    public static <K> boolean contains(
            final List<? extends AbstractKeyData<K>> keyDataList,
            final K key) {
        for (final AbstractKeyData<K> keyData : keyDataList) {
            if (keyData.getKey().equals(key)) {
                return true;
            }
        }

        return false;
    }

    public abstract K getKey();

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        final AbstractKeyData<?> other = (AbstractKeyData<?>) obj;

        return getKey().equals(((AbstractEntityData) obj).getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return getKey().toString();
    }
}