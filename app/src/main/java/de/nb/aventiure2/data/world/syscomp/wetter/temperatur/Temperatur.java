package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.syscomp.wetter.base.Betweenable;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.VerbOhneSubjAusserOptionalemExpletivemEs;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.praedikat.Witterungsverb;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.AUFGEHEIZT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EISIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EISKALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLIRREND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LAU;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKELHEIT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KAELTE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LEIB;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumMitDat;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumWerdenMitDat;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FROESTELN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LIEGEN;
import static de.nb.aventiure2.util.StreamUtil.*;

@SuppressWarnings("DuplicateBranchesInSwitch")
public enum Temperatur implements Betweenable<Temperatur> {
    // Reihenfolge ist relevant, nicht ändern!
    KLIRREND_KALT,
    KNAPP_UNTER_DEM_GEFRIERPUNKT,
    KNAPP_UEBER_DEM_GEFRIERPUNKT,
    KUEHL,
    WARM,
    RECHT_HEISS,
    SEHR_HEISS;

    public static Temperatur interpolate(
            final Temperatur value1, final Temperatur value2, final float anteil) {
        int resOrdinal = Math.round(
                (value2.ordinal() - value1.ordinal()) * anteil
                        + value1.ordinal());
        final Temperatur[] values = Temperatur.values();
        // Rundungsfehler abfangen!
        if (resOrdinal < 0) {
            resOrdinal = 0;
        }

        if (resOrdinal >= values.length) {
            resOrdinal = values.length - 1;
        }

        return values[resOrdinal];
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    public ImmutableCollection<Satz> altScKommtNachDraussenSaetze(final Tageszeit tageszeit) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        alt.addAll(mapToList(altStatischeBeschreibungSaetze(tageszeit, true),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        if (isBetweenIncluding(KNAPP_UEBER_DEM_GEFRIERPUNKT, RECHT_HEISS)) {
            alt.addAll(altStatischeBeschreibungSaetze(tageszeit, true));
        }

        switch (this) {
            case KLIRREND_KALT:
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(dativPraedikativumWerdenMitDat(duSc()) //
                        // "dir wird"
                        .mit(KALT)
                        .alsSatz());
                break;
            case KUEHL:
                break;
            case WARM:
                break;
            case RECHT_HEISS:
                break;
            case SEHR_HEISS:
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }

        return alt.build();
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    public ImmutableCollection<EinzelnerSatz> altStatischeBeschreibungSaetze(
            final Tageszeit tageszeit,
            final boolean draussen) {
        final ImmutableList.Builder<EinzelnerSatz> alt = ImmutableList.builder();

        alt.addAll(mapToList(altPraedikativa(draussen), Praedikativum::alsEsIstSatz));

        if (draussen) {
            // "die Luft ist kalt"
            alt.addAll(mapToList(
                    altAdjektivphrasenLuft(tageszeit),
                    a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(LUFT)));
        }

        switch (this) {
            case KLIRREND_KALT:
                alt.add(LIEGEN.mitAdvAngabe(
                        new AdvAngabeSkopusVerbAllg("in der Luft"))
                                .alsSatzMitSubjekt(npArtikellos(KLIRREND, KAELTE)),
                        // "du frierst am ganzen Leibe"
                        VerbSubj.FRIEREN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(AN_DAT.mit(LEIB)))
                                .alsSatzMitSubjekt(duSc()));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                alt.add(
                        // "es friert"
                        Witterungsverb.FRIEREN.alsSatz(),
                        // "du frierst"
                        VerbSubj.FRIEREN.alsSatzMitSubjekt(duSc()),
                        // "dir ist kalt"
                        dativPraedikativumMitDat(duSc()).mit(KALT).alsSatz());
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(// "dich friert"
                        VerbOhneSubjAusserOptionalemExpletivemEs.FRIEREN
                                .mit(duSc())
                                .alsSatzMitSubjekt(null));
                break;
            case KUEHL:
                alt.add(VerbOhneSubjAusserOptionalemExpletivemEs.FROESTELN
                                .mit(duSc())
                                .alsSatzMitSubjekt(null),
                        VerbOhneSubjAusserOptionalemExpletivemEs.FROESTELN
                                .mit(duSc())
                                .alsSatzMitSubjekt(null)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("ein wenig")));
                alt.add(FROESTELN.mitAdvAngabe(
                        new AdvAngabeSkopusVerbAllg("ein wenig"))
                        .alsSatzMitSubjekt(duSc()));
                break;
            case WARM:
                break;
            case RECHT_HEISS:
                break;
            case SEHR_HEISS:
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }

        return alt.build();
    }

    // IDEA Beschreibungen, die erst nach einer Weile Sinn ergeben:
    //  - Die ganze Zeit über ist dir kalt
    //  - du("schmachtest", "in der Hitze")

    /**
     * Gibt zurück, ob bei dieser Temperatur zu dieser Uhrzeit Sätze über "heute" oder "den Tag"
     * sinnvoll sind. - Generell wird es noch von anderen Kriterien abhängen, wann solche
     * Sätze sinnvoll sind, z.B. wohl nur draußen.
     */
    @CheckReturnValue
    public boolean saetzeUeberHeuteOderDenTagSinnvoll(final AvTime time) {
        return
                // Abends zu sagen "der Tag ist recht heiß" wäre unnatürlich
                TagestemperaturverlaufUtil.saetzeUeberHeuteOderDenTagVonDerUhrzeitHerSinnvoll(time)
                        // Zu sagen "der Tag so warm oder kalt wie jeder andere auch" wäre
                        // unnatürlich
                        && !isBetweenIncluding(
                        Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT, Temperatur.WARM);
    }

    /**
     * Gibt alternative Sätze zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen. Solche Sätze sind nur in gewissen Kontexten sinnvoll, insbesondere
     * nur draußen.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<Satz> altDraussenSaetzeUeberHeuteOderDenTag() {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // "Heute ist es heiß / schönes Wetter."
        alt.addAll(
                mapToList(altPraedikativa(true // Drinnen sind solche Sätze
                        // nicht sinnvoll
                ), a -> a.alsEsIstSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("heute"))));

        alt.addAll(altDerTagIstSaetze());

        return alt.build();
    }

    /**
     * Erzeugt Sätze in der Art "Der Tag ist sehr heiß" - nur unter gewissen
     * Umständen sinnvoll, z.B. nur draußen.
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<Satz> altDerTagIstSaetze() {
        return altPraedikativa(true).stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(TAG))
                .collect(toImmutableList());
    }

    /**
     * Gibt alternative Sätze zurück in der Art
     * "Es ist noch (sehr kalt / ziemlich warm / heißes Wetter)".
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<Satz> altIstNochSaetzeDraussen() {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // "Es ist (noch (sehr kalt))."
        alt.addAll(altPraedikativa(true).stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> praedikativumPraedikatMit(
                        ((AdjPhrOhneLeerstellen) a)
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("noch")))
                        .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES))
                .collect(toImmutableList()));

        // "Es ist noch schönes Wetter."
        alt.addAll(altPraedikativa(true).stream()
                .filter(obj -> !(obj instanceof AdjPhrOhneLeerstellen))
                .map(a -> a.alsEsIstSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("noch")))
                .collect(toImmutableList()));

        return alt.build();
    }

    /**
     * Gibt alternative Prädikative zurück für eine Beschreibung in der Art
     * "Es ist (sehr kalt / ziemlich warm / warmes Wetter)" oder "Heute ist es ..." oder
     * "Draußen ist es ...".
     * <p>
     * Das Eregebnis von {@link #altAdjektivphrasen()} ist bereits enthalten
     */
    @SuppressWarnings("DuplicateBranchesInSwitch")
    @NonNull
    @CheckReturnValue
    public ImmutableList<Praedikativum> altPraedikativa(final boolean draussen) {
        final ImmutableList.Builder<Praedikativum> alt = ImmutableList.builder();

        alt.addAll(altAdjektivphrasen());

        switch (this) {
            case KLIRREND_KALT:
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                break;
            case KUEHL:
                break;
            case WARM:
                if (draussen) {
                    alt.add(npArtikellos(AdjektivOhneErgaenzungen.WARM,
                            NomenFlexionsspalte.WETTER));
                }
                break;
            case RECHT_HEISS:
                if (draussen) {
                    alt.add(np(INDEF, HEISS, TAG));
                }
                break;
            case SEHR_HEISS:
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Adjektivphrasen <i>für die Luft</i> zurück:
     * (Die Luft ist) "frostig kalt", "warm", "aufgeheizt" etc.
     */
    @NonNull
    @CheckReturnValue
    private ImmutableList<AdjPhrOhneLeerstellen> altAdjektivphrasenLuft(final Tageszeit tageszeit) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> alt =
                ImmutableList.builder();

        alt.addAll(altAdjektivphrasen());

        switch (this) {
            case KLIRREND_KALT:
                alt.add(EISIG);
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                break;
            case KUEHL:
                break;
            case WARM:
                if (tageszeit == ABENDS || tageszeit == NACHTS) {
                    alt.add(LAU);
                }
                break;
            case RECHT_HEISS:
                alt.add(AUFGEHEIZT);
                break;
            case SEHR_HEISS:
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Adjektivphrasen zurück für eine Beschreibung in der Art
     * "Es ist (sehr kalt / ziemlich warm)" oder "Heute ist es ..." oder
     * "Draußen ist es ...".
     */
    @NonNull
    @CheckReturnValue
    public ImmutableList<AdjPhrOhneLeerstellen> altAdjektivphrasen() {
        switch (this) {
            case KLIRREND_KALT:
                return ImmutableList.of(
                        KALT.mitGraduativerAngabe("klirrend"),
                        EISKALT);
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                return ImmutableList.of(KALT.mitGraduativerAngabe("sehr"),
                        KALT.mitGraduativerAngabe("frostig"));
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                return ImmutableList.of(KALT);
            case KUEHL:
                return ImmutableList.of(
                        AdjektivOhneErgaenzungen.KUEHL,
                        AdjektivOhneErgaenzungen.KUEHL.mitGraduativerAngabe("etwas"),
                        AdjektivOhneErgaenzungen.KUEHL.mitGraduativerAngabe("ziemlich"));
            case WARM:
                return ImmutableList.of(AdjektivOhneErgaenzungen.WARM);
            case RECHT_HEISS:
                return ImmutableList.of(
                        AdjektivOhneErgaenzungen.HEISS.mitGraduativerAngabe("recht"),
                        AdjektivOhneErgaenzungen.HEISS.mitGraduativerAngabe("ziemlich"));
            case SEHR_HEISS:
                return ImmutableList.of(
                        AdjektivOhneErgaenzungen.HEISS.mitGraduativerAngabe("sehr"));
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }
    }

    @CheckReturnValue
    public boolean isUnauffaellig(final Tageszeit tageszeit) {
        if (tageszeit == NACHTS) {
            return this == KUEHL;
        }

        return isBetweenIncluding(KUEHL, WARM);
    }

    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(final AvTime time) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        // "in die klirrend kalte Luft"
        alt.addAll(mapToList(altAdjektivphrasenLuft(time.getTageszeit()),
                a -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(LUFT.mit(a)))));

        switch (this) {
            case KLIRREND_KALT:
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Eiseskälte"),
                        new AdvAngabeSkopusVerbWohinWoher("beißende Kälte"));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in die frostige Kälte"));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.addAll(altWohinHinausKnappUeberGefrierpunkt(time));
                break;
            case KUEHL:
                alt.add(new AdvAngabeSkopusVerbWohinWoher("ins Kühle"));
                break;
            case WARM:
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Wärme"));
                break;
            case RECHT_HEISS:
                break;
            case SEHR_HEISS:
                alt.addAll(altWohinHinausSehrHeiss(time));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private static ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausKnappUeberGefrierpunkt(
            final AvTime time) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Kälte"));
        if (time.getTageszeit() == NACHTS) {
            alt.add(new AdvAngabeSkopusVerbWohinWoher("in die nächtliche Kälte"));
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private static ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausSehrHeiss(
            final AvTime time) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Hitze"));
        if (time.gegenMittag()) {
            alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Mittagshitze"));
        } else if (time.getTageszeit() == ABENDS) {
            alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Abendhitze"));
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausDunkelheit(
            final Tageszeit tageszeit) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        alt.addAll(mapToList(
                altAdjektivphrasenLuft(tageszeit),
                // "in die eiskalte Dunkelheit"
                a -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(DUNKELHEIT.mit(a)))));

        switch (this) {
            case KLIRREND_KALT:
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                break;
            case KUEHL:
                break;
            case WARM:
                break;
            case RECHT_HEISS:
                break;
            case SEHR_HEISS:
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in die dunkle Hitze"));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }

        return alt.build();
    }
}
