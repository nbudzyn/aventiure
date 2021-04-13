package de.nb.aventiure2.data.time;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.satz.EinzelnerSatz;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ALLMAEHLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BLAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLAR;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANGSAM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.MORGENDLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ROETLICH;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABEND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDHIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDSONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDSONNENSCHEIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSSONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MOND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MONDSCHEIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MORGEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MORGENSONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.NACHTHIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENSCHEIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STERNENHIMMEL;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.ERSTE_SONNENSTRAHLEN;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatWerdenMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ANBRECHEN;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

public enum Tageszeit {
    // Reihenfolge ist relevant, nicht ändern!
    NACHTS(NACHT,
            DUNKEL,
            ImmutableList.of(MOND),
            ImmutableList.of(MONDSCHEIN),
            ImmutableList.of(),
            ImmutableList.of(STERNENHIMMEL, NACHTHIMMEL),
            ImmutableList.of(), // "Gute Nacht" etc. sind nur Verabschiedungen!
            ImmutableList.of() // "Gute Nacht" etc. sagt man eher abends
    ),

    MORGENS(MORGEN,
            HELL,
            ImmutableList.of(MORGENSONNE),
            ImmutableList.of(ERSTE_SONNENSTRAHLEN,
                    np(MORGENDLICH, SONNENSCHEIN),
                    SONNENSCHEIN, MORGENSONNE),
            ImmutableList.of(KLAR),
            ImmutableList.of(),
            ImmutableList.of("Morgen", "guten Morgen", "schönen guten Morgen",
                    "einen schönen guten Morgen"),
            ImmutableList.of("schönen Tag noch", "einen schönten Tag noch")),

    TAGSUEBER(TAG,
            HELL,
            ImmutableList.of(SONNE, MITTAGSSONNE),
            ImmutableList.of(SONNENSCHEIN,
                    // "du legst dich in die Sonne"
                    SONNE, MITTAGSSONNE),
            ImmutableList.of(BLAU, BLAU.mitAdvAngabe(new AdvAngabeSkopusSatz("strahlend"))),
            ImmutableList.of(),
            ImmutableList.of("guten Tag", "schönen guten Tag", "einen schönen guten Tag"),
            ImmutableList.of("schönen Tag noch", "einen schönten Tag noch")),

    ABENDS(ABEND,
            HELL,
            ImmutableList.of(ABENDSONNE),
            ImmutableList.of(ABENDSONNENSCHEIN, ABENDSONNE),
            ImmutableList.of(),
            ImmutableList.of(np(ROETLICH, ABENDHIMMEL), ABENDHIMMEL),
            ImmutableList.of("guten Abend", "schönen guten Abend"),
            ImmutableList.of("gute Nacht"));

    private final NomenFlexionsspalte nomenFlexionsspalte;

    private final Lichtverhaeltnisse lichtverhaeltnisseDraussen;

    /**
     * Alternative "Gestirne" dieser Tagezeit: die Morgensonne, der Mond, ...
     */
    private final ImmutableList<EinzelneSubstantivischePhrase> altGestirn;

    /**
     * ALternativen für den "Gestirnschein" dieser Tagezeit: der Sonnenschein,
     * der Abendsonnenschein, ...
     */
    private final ImmutableList<EinzelneSubstantivischePhrase> altGestirnschein;

    /**
     * Alternative Adjektivphrasen, die einen wolkenlosen Himmel zu dieser
     * Tageszeit beschreiben (z.B. "blau") - <i>kann leer sein</i>.
     */
    private final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrWolkenloserHimmel;

    /**
     * Weitere Alternativen für einen wolkenlosen Himmel über
     * {@link #altAdjPhrWolkenloserHimmel} hinaus:  "der Sternenhimmel" etc.
     * <p>
     * Kann leer sein, aber nicht in Kombination mit {@link #altAdjPhrWolkenloserHimmel}.
     */
    private final ImmutableList<EinzelneSubstantivischePhrase> altWolkenloserHimmelErgaenzungen;

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

    Tageszeit(final NomenFlexionsspalte nomenFlexionsspalte,
              final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
              final ImmutableList<EinzelneSubstantivischePhrase> altGestirn,
              final ImmutableList<EinzelneSubstantivischePhrase> altGestirnschein,
              final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrWolkenloserHimmel,
              final ImmutableList<EinzelneSubstantivischePhrase> altWolkenloserHimmelErgaenzungen,
              final Collection<String> begruessungen,
              final Collection<String> verabschiedungen) {
        this.nomenFlexionsspalte = nomenFlexionsspalte;
        this.lichtverhaeltnisseDraussen = lichtverhaeltnisseDraussen;
        this.altGestirn = altGestirn;
        this.altGestirnschein = altGestirnschein;
        this.altAdjPhrWolkenloserHimmel = altAdjPhrWolkenloserHimmel;
        this.altWolkenloserHimmelErgaenzungen = altWolkenloserHimmelErgaenzungen;
        this.begruessungen = ImmutableList.copyOf(begruessungen);
        this.verabschiedungen = ImmutableList.copyOf(verabschiedungen);
    }
    
    /**
     * Gibt Sätze zurück wie "langsam wird es Morgen", "der Tag bricht an",
     * "langsam beginnt der Abend" o. Ä.
     */
    public ImmutableSet<EinzelnerSatz> altLangsamBeginntSaetze() {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        alt.addAll(Stream.of(LANGSAM, ALLMAEHLICH)
                .map(a -> esWirdSatz().mitAdvAngabe(new AdvAngabeSkopusSatz(a)))
                .collect(toSet()));

        alt.add(
                // "der Tag bricht an"
                ANBRECHEN.alsSatzMitSubjekt(nomenFlexionsspalte),
                ANBRECHEN.alsSatzMitSubjekt(nomenFlexionsspalte)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz(ALLMAEHLICH)),
                VerbSubj.BEGINNEN.alsSatzMitSubjekt(nomenFlexionsspalte)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz(LANGSAM))
        );

        return alt.build();
    }

    /**
     * Gibt einen Satz zurück wie "es wird Morgen" oder "es wird Tag".
     */
    public EinzelnerSatz esWirdSatz() {
        return esWirdSatz(null);
    }

    /**
     * Gibt einen Satz zurück wie "und es wird Morgen" oder "und es wird Tag".
     */
    public EinzelnerSatz esWirdSatz(final @Nullable String anschlusswort) {
        return praedikativumPraedikatWerdenMit(nomenFlexionsspalte)
                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES);
    }

    public ImmutableList<EinzelneSubstantivischePhrase> altGestirn() {
        return altGestirn;
    }

    public ImmutableList<EinzelneSubstantivischePhrase> altGestirnschein() {
        return altGestirnschein;
    }

    /**
     * Gibt alternative substantivische Phrasen zurück, die einen wolkenlosen Himmel zu dieser
     * Tageszeit beschreiben. Ergebnis ist nie leer.
     */
    public ImmutableCollection<EinzelneSubstantivischePhrase> altWolkenloserHimmel() {
        return ImmutableSet.<EinzelneSubstantivischePhrase>builder()
                .addAll(mapToSet(altAdjPhrWolkenloserHimmel, HIMMEL::mit))
                .addAll(altWolkenloserHimmelErgaenzungen)
                .build();
    }

    /**
     * Gibt alternative Adjektivphrasen zurück, die einen wolkenlosen Himmel zu dieser
     * Tageszeit beschreiben - <i>Ergebnis kann leer sein</i>.
     */
    public ImmutableCollection<AdjPhrOhneLeerstellen> altAltAdjPhrWolkenloserHimmel() {
        return altAdjPhrWolkenloserHimmel;
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

    public NomenFlexionsspalte getNomenFlexionsspalte() {
        return nomenFlexionsspalte;
    }

    public boolean hasFolgetageszeit(final Tageszeit other) {
        final int myIndex = Arrays.asList(values()).indexOf(this);
        final int otherIndex = Arrays.asList(values()).indexOf(other);
        if (otherIndex == myIndex + 1) {
            return true;
        }
        if (myIndex == values().length - 1 && otherIndex == 0) {
            return true;
        }
        return false;
    }

}
