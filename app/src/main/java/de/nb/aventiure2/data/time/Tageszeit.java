package de.nb.aventiure2.data.time;

import static java.util.stream.Collectors.toSet;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ALLMAEHLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BLAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLAR;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LANGSAM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.MORGENDLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ROETLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WOLKENLOS;
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
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STERNENZELT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ANBRECHEN;
import static de.nb.aventiure2.util.StreamUtil.*;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.stream.Stream;

import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.satz.EinzelnerSemSatz;

public enum Tageszeit {
    // Reihenfolge ist relevant, nicht ändern!
    // Wenn weitere Tageszeiten eingeführt werden müssen alle Methoden in diesem Enum
    // überprüft werden - einige gehen davon aus, dass es genau 4 Tageszeiten gibt.
    NACHTS(NACHT,
            DUNKEL,
            ImmutableList.of(MOND),
            ImmutableList.of(MONDSCHEIN),
            ImmutableList.of(WOLKENLOS),
            ImmutableList.of(STERNENHIMMEL, NACHTHIMMEL, STERNENZELT),
            ImmutableList.of(), // "Gute Nacht" etc. sind nur Verabschiedungen!
            ImmutableList.of() // "Gute Nacht" etc. sagt man eher abends
    ),

    MORGENS(MORGEN,
            HELL,
            ImmutableList.of(MORGENSONNE),
            ImmutableList.of(np(MORGENDLICH, SONNENSCHEIN), SONNENSCHEIN, MORGENSONNE),
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
            ImmutableList.of(WOLKENLOS),
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
    private final ImmutableList<EinzelneSubstantivischePhrase> altSpWolkenloserHimmelErgaenzungen;

    /**
     * Ggf. alternative tageszeitspezifische Begrüßungen, jeweils beginnend mit Kleinbuchstaben und
     * ohne Satzschlusszeichen
     */
    private final ImmutableList<String> altSpBegruessungen;

    /**
     * Ggf. alternative tageszeitspezifische Verabschiedungen, jeweils beginnend mit
     * Kleinbuchstaben und ohne Satzschlusszeichen
     */
    private final ImmutableList<String> altSpVerabschiedungen;

    Tageszeit(final NomenFlexionsspalte nomenFlexionsspalte,
              final Lichtverhaeltnisse lichtverhaeltnisseDraussen,
              final ImmutableList<EinzelneSubstantivischePhrase> altGestirn,
              final ImmutableList<EinzelneSubstantivischePhrase> altGestirnschein,
              final ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrWolkenloserHimmel,
              final ImmutableList<EinzelneSubstantivischePhrase> altSpWolkenloserHimmelErgaenzungen,
              final Collection<String> altSpBegruessungen,
              final Collection<String> altSpVerabschiedungen) {
        this.nomenFlexionsspalte = nomenFlexionsspalte;
        this.lichtverhaeltnisseDraussen = lichtverhaeltnisseDraussen;
        this.altGestirn = altGestirn;
        this.altGestirnschein = altGestirnschein;
        this.altAdjPhrWolkenloserHimmel = altAdjPhrWolkenloserHimmel;
        this.altSpWolkenloserHimmelErgaenzungen = altSpWolkenloserHimmelErgaenzungen;
        this.altSpBegruessungen = ImmutableList.copyOf(altSpBegruessungen);
        this.altSpVerabschiedungen = ImmutableList.copyOf(altSpVerabschiedungen);
    }

    /**
     * Gibt Sätze zurück wie "langsam wird es Morgen", "der Tag bricht an",
     * "langsam beginnt der Abend" o. Ä.
     */
    public ImmutableSet<EinzelnerSemSatz> altLangsamBeginntSaetze() {
        final ImmutableSet.Builder<EinzelnerSemSatz> alt = ImmutableSet.builder();

        if (this == TAGSUEBER) {
            // "Langsam beginnt der Tag" ist missverständlich (= der Morgen? Der Vormittag?)
            alt.addAll(Stream.of(LANGSAM, ALLMAEHLICH)
                    .map(a -> VerbSubjObj.UEBERGEHEN.mit(TAG)
                            .alsSatzMitSubjekt(MORGEN)
                            .mitAdvAngabe(new AdvAngabeSkopusSatz(a)))
                    .collect(toSet()));
        } else {
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
        }

        return alt.build();
    }

    /**
     * Gibt einen SemSatz zurück wie "es wird Morgen" oder "es wird Tag".
     */
    private EinzelnerSemSatz esWirdSatz() {
        return npArtikellos(nomenFlexionsspalte).alsWerdenPraedikativumPraedikat()
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
                .addAll(altSpWolkenloserHimmelErgaenzungen)
                .build();
    }

    /**
     * Gibt alternative Adjektivphrasen zurück, die einen wolkenlosen Himmel zu dieser
     * Tageszeit beschreiben.
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
    public ImmutableList<String> altSpTagezeitabhaengigeBegruessungen() {
        return altSpBegruessungen;
    }

    /**
     * Gibt evtl. alternative tageszeitspezifische Verabschiedungen zurück, jeweils beginnend mit
     * Kleinbuchstaben und ohne Satzschlusszeichen - könnte leer sein!
     */
    public ImmutableList<String> altSpTagezeitabhaengigeVerabschiedungen() {
        return altSpVerabschiedungen;
    }

    public NomenFlexionsspalte getNomenFlexionsspalte() {
        return nomenFlexionsspalte;
    }

    public boolean hasNachfolger(final Tageszeit other) {
        return getNachfolger() == other;
    }

    public Tageszeit getVorgaenger() {
        if (ordinal() == 0) {
            return values()[values().length - 1];
        }

        return values()[ordinal() - 1];
    }

    public Tageszeit getNachfolger() {
        if (ordinal() == values().length - 1) {
            return values()[0];
        }

        return values()[ordinal() + 1];
    }
}
