package de.nb.aventiure2.data.world.time;

import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;

import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;

public enum Tageszeit {
    NACHTS(DUNKEL), MORGENS(HELL), TAGSUEBER(HELL), ABENDS(HELL);

    private final Lichtverhaeltnisse lichtverhaeltnisseDraussen;

    Tageszeit(final Lichtverhaeltnisse lichtverhaeltnisseDraussen) {
        this.lichtverhaeltnisseDraussen = lichtverhaeltnisseDraussen;
    }

    public Lichtverhaeltnisse getLichtverhaeltnisseDraussen() {
        return lichtverhaeltnisseDraussen;
    }
}
