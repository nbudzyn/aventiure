package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;
import de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbOhneSubjAusserOptionalemExpletivemEs;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.praedikat.Witterungsverb;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLIRREND;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KAELTE_OHNE_ART;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LEIB;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG_EIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WETTER_OHNE_ART;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FROESTELN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LIEGEN;
import static de.nb.aventiure2.util.StreamUtil.*;

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
    ImmutableCollection<Satz> altScKommtNachDraussenSaetze() {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        alt.addAll(mapToList(altStatischeBeschreibungSaetze(),
                s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        switch (this) {
            case KLIRREND_KALT:
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                break;
            case KUEHL:
                alt.add(VerbOhneSubjAusserOptionalemExpletivemEs.FROESTELN
                        .mit(duSc())
                        .alsSatzMitSubjekt(null)
                        .mitAdvAngabe(
                                new AdvAngabeSkopusVerbAllg("ein wenig")));
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
    ImmutableCollection<Satz> altStatischeBeschreibungSaetze() {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        alt.addAll(mapToList(altPraedikativa(), Praedikativum::alsEsIstSatz));
        switch (this) {
            case KLIRREND_KALT:
                alt.add(LIEGEN.mitAdvAngabe(
                        new AdvAngabeSkopusVerbAllg("in der Luft"))
                                .alsSatzMitSubjekt(Nominalphrase.np(KLIRREND, KAELTE_OHNE_ART)),
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
                        VerbSubj.FRIEREN.alsSatzMitSubjekt(duSc()));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen
                                .dativPraedikativumWerdenMitDat(duSc()) //
                                // "wird dir"
                                .mit(KALT)
                                .alsSatz(),
                        // "dich friert"
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

    /**
     * Gibt zurück, ob bei dieser Temperatur zu dieser Uhrzeit Sätze über "heute" oder "den Tag"
     * sinnvoll sind.
     */
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
     * beziehen.
     */
    @NonNull
    public ImmutableCollection<Satz> altSaetzeUeberHeuteOderDenTag() {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // "Heute ist es heiß / schönes Wetter."
        alt.addAll(
                mapToList(altPraedikativa(), a -> a.alsEsIstSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("heute"))));

        alt.addAll(altDerTagIstSaetze());

        return alt.build();
    }

    @NonNull
    private ImmutableCollection<Satz> altDerTagIstSaetze() {
        return altPraedikativa().stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(TAG))
                .collect(toImmutableList());
    }

    /**
     * Gibt alternative Sätze zurück in der Art
     * "Es ist noch (sehr kalt / ziemlich warm / heißes Wetter)".
     */
    @NonNull
    ImmutableCollection<Satz> altIstNochSaetze() {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // "Es ist (noch (sehr kalt))."
        alt.addAll(altPraedikativa().stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> praedikativumPraedikatMit(
                        ((AdjPhrOhneLeerstellen) a)
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("noch")))
                        .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES))
                .collect(toImmutableList()));

        // "Es ist noch schönes Wetter."
        alt.addAll(altPraedikativa().stream()
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
    ImmutableList<Praedikativum> altPraedikativa() {
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
                alt.add(Nominalphrase.np(AdjektivOhneErgaenzungen.WARM, WETTER_OHNE_ART));
                break;
            case RECHT_HEISS:
                alt.add(Nominalphrase.np(HEISS, TAG_EIN));
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
    ImmutableList<AdjPhrOhneLeerstellen> altAdjektivphrasen() {
        switch (this) {
            case KLIRREND_KALT:
                return ImmutableList.of(KALT.mitGraduativerAngabe("klirrend"));
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                return ImmutableList.of(KALT.mitGraduativerAngabe("sehr"));
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                return ImmutableList.of(KALT);
            case KUEHL:
                return ImmutableList.of(
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

    boolean isUnauffaellig(final Tageszeit tageszeit) {
        if (tageszeit == NACHTS) {
            return this == KUEHL;
        }

        return isBetweenIncluding(KUEHL, WARM);
    }

    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(final AvTime time) {
        switch (this) {
            case KLIRREND_KALT:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die Eiseskälte"));
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die frostige Kälte"));
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                return altWohinHinausKnappUeberGefrierpunkt(time);
            case KUEHL:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("ins Kühle"));
            case WARM:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die Wärme"),
                        new AdvAngabeSkopusVerbWohinWoher("in die warme Luft"));
            case RECHT_HEISS:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die aufgeheizte Luft"));
            case SEHR_HEISS:
                return altWohinHinausSehrHeiss(time);
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }
    }

    public static ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausKnappUeberGefrierpunkt(
            final AvTime time) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableSet.builder();

        alt.add(new AdvAngabeSkopusVerbWohinWoher("in die Kälte"));
        if (time.getTageszeit() == NACHTS) {
            alt.add(new AdvAngabeSkopusVerbWohinWoher("in die nächtliche Kälte"));
        }

        return alt.build();
    }

    public static ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausSehrHeiss(
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

    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinausDunkelheit() {
        switch (this) {
            case KLIRREND_KALT:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die eiskalte Dunkelheit"),
                        new AdvAngabeSkopusVerbWohinWoher(
                                "in die klirrend kalte Dunkelheit"));
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die frostige Dunkelheit"));
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die kalte Dunkelheit"));
            case KUEHL:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die kühle Dunkelheit"));
            case WARM:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die warme Dunkelheit"));
            case RECHT_HEISS:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die aufgeheizte Dunkelheit"));
            case SEHR_HEISS:
                return ImmutableList.of(
                        new AdvAngabeSkopusVerbWohinWoher("in die dunkle Hitze"));
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }
    }

    // FIXME wohin damit? Die ganze Zeit über ist dir kalt // du frierst...
    //  du("schmachtest", "in der Hitze"), "dir ist heiß"
    //  Konzept am Hunger orientieren? Als "Erinnerung"?
    //  Vor allem auch bedenken, dass es ja über den Tag immer wärmer (und auch wieder
    //  kälter) wird. Die Änderungen müssen also beschrieben werden.
    //  Vielleicht muss man doch - wie bei der Müdigkeit - den letzten
    //  Temperatur-Wert speichern?
}
