package de.nb.aventiure2.data.time;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.german.base.Nominalphrase.ABENDSONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.MITTAGSSONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.MOND;
import static de.nb.aventiure2.german.base.Nominalphrase.MORGENSONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.SONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.VOLLMOND;

public enum Tageszeit {
    NACHTS(DUNKEL,
            ImmutableList.of(MOND, VOLLMOND),
            ImmutableList.of(), // "Gute Nacht" etc. sind nur Verabschiedungen!
            ImmutableList.of() // "Gute Nacht" etc. sagt man eher abends
    ),
    MORGENS(HELL,
            ImmutableList.of(MORGENSONNE),
            ImmutableList.of("Morgen", "guten Morgen", "schönen guten Morgen",
                    "einen schönen guten Morgen"),
            ImmutableList.of("schönen Tag noch", "einen schönten Tag noch")),
    TAGSUEBER(HELL,
            ImmutableList.of(SONNE, MITTAGSSONNE),
            ImmutableList.of("guten Tag", "schönen guten Tag", "einen schönen guten Tag"),
            ImmutableList.of("schönen Tag noch", "einen schönten Tag noch")),
    ABENDS(HELL,
            ImmutableList.of(ABENDSONNE),
            ImmutableList.of("guten Abend", "schönen guten Abend"),
            ImmutableList.of("gute Nacht"));

    private final Lichtverhaeltnisse lichtverhaeltnisseDraussen;

    /**
     * Das "Gestirn" dieser Tagezeit: die Morgensonne, der Mond, ...
     */
    private final ImmutableList<Nominalphrase> altGestirn;

    /**
     * Ggf. alternative tageszeitspezifische Begrüßungen, jeweils beginnend mit Kleinbuchstaben und
     * ohne Satzschlusszeichen
     */
    private final ImmutableList<String> begruessungen;

    /**
     * Ggf. alternative tageszeitspezifische Verabschiedungen, jeweils beginnend mit
     * Kleinbuchstaben und ohne Satzschlusszeichen
     */
    private final ImmutableList<String> verabschiedungen;

    Tageszeit(final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
              final ImmutableList<Nominalphrase> altGestirn,
              final Collection<String> begruessungen,
              final Collection<String> verabschiedungen) {
        this.lichtverhaeltnisseDraussen = lichtverhaeltnisseDraussen;
        this.altGestirn = altGestirn;
        this.begruessungen = ImmutableList.copyOf(begruessungen);
        this.verabschiedungen = ImmutableList.copyOf(verabschiedungen);
    }

    public ImmutableList<Nominalphrase> altGestirn() {
        return altGestirn;
    }

    public Lichtverhaeltnisse getLichtverhaeltnisseDraussen() {
        return lichtverhaeltnisseDraussen;
    }

    /**
     * Gibt evtl. alternative tageszeitspezifische Begruessungen zurück, jeweils beginnend mit
     * Kleinbuchstaben und ohne Satzschlusszeichen - könnte leer sein!
     */
    public ImmutableList<String> altTagezeitabhaengigeBegruessungen() {
        return begruessungen;
    }

    /**
     * Gibt evtl. alternative tageszeitspezifische Verabschiedungen zurück, jeweils beginnend mit
     * Kleinbuchstaben und ohne Satzschlusszeichen - könnte leer sein!
     */
    public ImmutableList<String> altTagezeitabhaengigeVerabschiedungen() {
        return verabschiedungen;
    }
}
