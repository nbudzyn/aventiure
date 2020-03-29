package de.nb.aventiure2.data.world.base;

import java.util.List;

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
}