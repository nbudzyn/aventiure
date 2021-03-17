package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdverbialeAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbOhneSubjAusserOptionalemExpletivemEs;
import de.nb.aventiure2.german.praedikat.Witterungsverb;
import de.nb.aventiure2.german.satz.Satz;

import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.base.Nominalphrase.KLIRRENDE_KAELTE_OHNE_ART;
import static de.nb.aventiure2.german.base.Nominalphrase.WARMES_WETTER_OHNE_ART;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FROESTELN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LIEGEN;
import static java.util.stream.Collectors.toList;

public enum Temperatur {
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

    @CheckReturnValue
    ImmutableCollection<Satz> altScKommtNachDraussenSaetze() {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        alt.addAll(altAdjektivphrase().stream()
                .map(Praedikativum::alsEsIstSatz)
                .map(s -> s.mitAdverbialerAngabe(new AdverbialeAngabeSkopusSatz("draußen")))
                .collect(toList()));
        switch (this) {
            case KLIRREND_KALT:
                alt.add(LIEGEN.mitAdverbialerAngabe(
                        new AdverbialeAngabeSkopusVerbAllg("in der Luft"))
                        .alsSatzMitSubjekt(KLIRRENDE_KAELTE_OHNE_ART)
                        // FIXME Vielleich könnte es eine Methode geben, die diese Sätze
                        //  OHNE das "draußen" zurückgibt. Hier könnte das einfach das
                        //  "draußen" hinzugefügt werden.
                        .mitAdverbialerAngabe(new AdverbialeAngabeSkopusSatz("draußen")));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                alt.add(Witterungsverb.FRIEREN
                        .alsSatz()
                        .mitAdverbialerAngabe(new AdverbialeAngabeSkopusSatz("draußen")));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen
                        .dativPraedikativumWerdenMitDat(Personalpronomen.get(P2, M)) // "wird dir"
                        .mit(KALT)
                        .mitAdverbialerAngabe(new AdverbialeAngabeSkopusSatz("draußen"))
                        .alsSatz());
                break;
            case KUEHL:
                alt.add(VerbOhneSubjAusserOptionalemExpletivemEs.FROESTELN
                                .mit(Personalpronomen.get(P3, M))
                                .alsSatzMitSubjekt(null)
                                .mitAdverbialerAngabe(
                                        new AdverbialeAngabeSkopusVerbAllg("draußen")),
                        VerbOhneSubjAusserOptionalemExpletivemEs.FROESTELN
                                .mit(Personalpronomen.get(P3, M))
                                .alsSatzMitSubjekt(null)
                                .mitAdverbialerAngabe(
                                        new AdverbialeAngabeSkopusVerbAllg("ein wenig")));
                alt.add(FROESTELN.mitAdverbialerAngabe(
                        new AdverbialeAngabeSkopusVerbAllg("ein wenig"))
                        .mitAdverbialerAngabe(new AdverbialeAngabeSkopusSatz("draußen"))
                        .alsSatzMitSubjekt(Personalpronomen.get(P2, M)));
                break;
            case WARM:
                break;
            case RECHT_HEISS:
                // FIXME "heute" macht eigentlich nur bei einem Maximalwert
                // (über Tag heiß) oder Minimalwert (nachts kalt?!) Sinn - und
                //  eigentlich auch nur einmal am Tag?!
                // "heute ist es (recht heiß)";
                break;
            case SEHR_HEISS:
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + this);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Adjektivphrasen zurück für eine Beschreibung in der Art
     * "Es ist (sehr kalt / ziemlich warm / warmes Wetter)" oder "Heute ist es ..." oder
     * "Draußen ist es ...".
     */
    @NonNull
    private ImmutableList<Praedikativum> altAdjektivphrase() {
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
                return ImmutableList.of(AdjektivOhneErgaenzungen.WARM,
                        WARMES_WETTER_OHNE_ART);
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

    // FIXME wohin damit? Die ganze Zeit über ist dir kalt // du frierst...
    //  du("schmachtest", "in der Hitze"), "dir ist heiß"
    //  Konzept am Hunger orientieren? Als "Erinnerung"?
    //  Vor allem auch bedenken, dass es ja über den Tag immer wärmer (und auch wieder
    //  kälter) wird. Die Änderungen müssen also beschrieben werden.
    //  Vielleicht muss man doch - wie bei der Müdigkeit - den letzten
    //  Temperatur-Wert speichern?
}
