package de.nb.aventiure2.data.time;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;

public enum Tageszeit {
    NACHTS(DUNKEL
            // "Gute Nacht" etc. sind nur Verabschiedungen!
    ),
    MORGENS(HELL, "Morgen", "guten Morgen", "schönen guten Morgen",
            "einen schönen guten Morgen"),
    TAGSUEBER(HELL, "guten Tag", "schönen guten Tag",
            "einen schönen guten Tag"),
    ABENDS(HELL, "guten Abend", "schönen guten Abend");

    private final Lichtverhaeltnisse lichtverhaeltnisseDraussen;

    /**
     * Ggf. alternative tageszeitspezifische Grüße, jeweils beginnend mit Kleinbuchstaben und ohne
     * Satzschlusszeichen
     */
    private final ImmutableList<String> gruesse;

    Tageszeit(final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
              final String... gruesse) {
        this.lichtverhaeltnisseDraussen = lichtverhaeltnisseDraussen;
        this.gruesse = ImmutableList.copyOf(gruesse);
    }

    public Lichtverhaeltnisse getLichtverhaeltnisseDraussen() {
        return lichtverhaeltnisseDraussen;
    }

    /**
     * Gibt evtl. alternative tageszeitspezifische Grüße zurück, jeweils beginnend mit
     * Kleinbuchstaben und ohne Satzschlusszeichen - könnte leer sein!
     */
    public ImmutableList<String> altTagezeitabhaengigeGruesse() {
        return gruesse;
    }
}
