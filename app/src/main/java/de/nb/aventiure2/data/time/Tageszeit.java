package de.nb.aventiure2.data.time;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.german.base.Nominalphrase.ABEND;
import static de.nb.aventiure2.german.base.Nominalphrase.ABENDSONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.ABENDSONNENSCHEIN;
import static de.nb.aventiure2.german.base.Nominalphrase.ERSTE_SONNENSTRAHLEN;
import static de.nb.aventiure2.german.base.Nominalphrase.MITTAGSSONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.MOND;
import static de.nb.aventiure2.german.base.Nominalphrase.MONDSCHEIN;
import static de.nb.aventiure2.german.base.Nominalphrase.MORGEN;
import static de.nb.aventiure2.german.base.Nominalphrase.MORGENDLICHER_SONNENSCHEIN;
import static de.nb.aventiure2.german.base.Nominalphrase.MORGENSONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.NACHT;
import static de.nb.aventiure2.german.base.Nominalphrase.SONNE;
import static de.nb.aventiure2.german.base.Nominalphrase.SONNENSCHEIN;
import static de.nb.aventiure2.german.base.Nominalphrase.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.VOLLMOND;

public enum Tageszeit {
    NACHTS(NACHT,
            DUNKEL,
            ImmutableList.of(MOND, VOLLMOND),
            ImmutableList.of(MONDSCHEIN),
            ImmutableList.of(), // "Gute Nacht" etc. sind nur Verabschiedungen!
            ImmutableList.of() // "Gute Nacht" etc. sagt man eher abends
    ),
    MORGENS(MORGEN,
            HELL,
            ImmutableList.of(MORGENSONNE),
            ImmutableList.of(ERSTE_SONNENSTRAHLEN, MORGENDLICHER_SONNENSCHEIN, SONNENSCHEIN),
            ImmutableList.of("Morgen", "guten Morgen", "schönen guten Morgen",
                    "einen schönen guten Morgen"),
            ImmutableList.of("schönen Tag noch", "einen schönten Tag noch")),
    TAGSUEBER(TAG,
            HELL,
            ImmutableList.of(SONNE, MITTAGSSONNE),
            ImmutableList.of(SONNENSCHEIN),
            ImmutableList.of("guten Tag", "schönen guten Tag", "einen schönen guten Tag"),
            ImmutableList.of("schönen Tag noch", "einen schönten Tag noch")),
    ABENDS(ABEND,
            HELL,
            ImmutableList.of(ABENDSONNE),
            ImmutableList.of(ABENDSONNENSCHEIN),
            ImmutableList.of("guten Abend", "schönen guten Abend"),
            ImmutableList.of("gute Nacht"));

    private final Nominalphrase nominalphrase;

    private final Lichtverhaeltnisse lichtverhaeltnisseDraussen;

    /**
     * Alternative "Gestirne" dieser Tagezeit: die Morgensonne, der Mond, ...
     */
    private final ImmutableList<Nominalphrase> altGestirn;

    /**
     * ALternativen für den "Gestirnschein" dieser Tagezeit: der Sonnenschein,
     * der Abendsonnenschein, ...
     */
    private final ImmutableList<Nominalphrase> altGestirnschein;

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

    Tageszeit(final Nominalphrase nominalphrase,
              final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
              final ImmutableList<Nominalphrase> altGestirn,
              final ImmutableList<Nominalphrase> altGestirnschein,
              final Collection<String> begruessungen,
              final Collection<String> verabschiedungen) {
        this.nominalphrase = nominalphrase;
        this.lichtverhaeltnisseDraussen = lichtverhaeltnisseDraussen;
        this.altGestirn = altGestirn;
        this.altGestirnschein = altGestirnschein;
        this.begruessungen = ImmutableList.copyOf(begruessungen);
        this.verabschiedungen = ImmutableList.copyOf(verabschiedungen);
    }

    public ImmutableList<Nominalphrase> altGestirn() {
        return altGestirn;
    }

    public ImmutableList<Nominalphrase> altGestirnschein() {
        return altGestirnschein;
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

    public Nominalphrase getNominalphrase() {
        return nominalphrase;
    }
}
