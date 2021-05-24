package de.nb.aventiure2.data.world.syscomp.wetter.base;

import de.nb.aventiure2.data.world.base.Change;

/**
 * Änderung eines Wetter-Parameters (z.B. Temperatur)
 */
public class WetterParamChange<E extends Enum<?>> extends Change<E> {
    public WetterParamChange(final E vorher, final E nachher) {
        super(vorher, nachher);
    }

    public int delta() {
        return getNachher().ordinal() - getVorher().ordinal();
    }
}
