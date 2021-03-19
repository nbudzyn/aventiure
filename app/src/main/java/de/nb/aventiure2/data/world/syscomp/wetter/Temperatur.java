package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen;
import de.nb.aventiure2.german.praedikat.VerbOhneSubjAusserOptionalemExpletivemEs;
import de.nb.aventiure2.german.praedikat.Witterungsverb;
import de.nb.aventiure2.german.satz.Satz;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.base.Nominalphrase.KLIRRENDE_KAELTE_OHNE_ART;
import static de.nb.aventiure2.german.base.Nominalphrase.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.WARMES_WETTER_OHNE_ART;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.Person.P3;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FROESTELN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LIEGEN;
import static java.util.stream.Collectors.toList;

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

    @CheckReturnValue
    ImmutableCollection<Satz> altScKommtNachDraussenSaetze() {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        alt.addAll(altPraedikativa().stream()
                .map(Praedikativum::alsEsIstSatz)
                .map(s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen")))
                .collect(toList()));
        switch (this) {
            case KLIRREND_KALT:
                alt.add(LIEGEN.mitAdvAngabe(
                        new AdvAngabeSkopusVerbAllg("in der Luft"))
                        .alsSatzMitSubjekt(KLIRRENDE_KAELTE_OHNE_ART)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("draußen")));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                alt.add(Witterungsverb.FRIEREN
                        .alsSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("draußen")));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen
                        .dativPraedikativumWerdenMitDat(Personalpronomen.get(P2, M)) // "wird dir"
                        .mit(KALT)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))
                        .alsSatz());
                break;
            case KUEHL:
                alt.add(VerbOhneSubjAusserOptionalemExpletivemEs.FROESTELN
                                .mit(Personalpronomen.get(P3, M))
                                .alsSatzMitSubjekt(null)
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusVerbAllg("draußen")),
                        VerbOhneSubjAusserOptionalemExpletivemEs.FROESTELN
                                .mit(Personalpronomen.get(P3, M))
                                .alsSatzMitSubjekt(null)
                                .mitAdvAngabe(
                                        new AdvAngabeSkopusVerbAllg("ein wenig")));
                alt.add(FROESTELN.mitAdvAngabe(
                        new AdvAngabeSkopusVerbAllg("ein wenig"))
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))
                        .alsSatzMitSubjekt(Personalpronomen.get(P2, M)));
                break;
            case WARM:
                break;
            case RECHT_HEISS:
                // FIXME "heute" macht eigentlich nur bei einem Maximalwert
                // (über Tag heiß) oder vielleicht Minimalwert (nachts kalt?!) Sinn - und
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
     * Gibt alternative Sätze zurück in der Art
     * "Der Tag ist (sehr kalt / ziemlich warm)".
     */
    @NonNull
    ImmutableCollection<Satz> altDerTagIstSaetze() {
        return altPraedikativa().stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(TAG))
                .collect(toImmutableList());
    }

    /**
     * Gibt alternative Prädikative zurück für eine Beschreibung in der Art
     * "Es ist (sehr kalt / ziemlich warm / warmes Wetter)" oder "Heute ist es ..." oder
     * "Draußen ist es ...".
     */
    @NonNull
    ImmutableList<Praedikativum> altPraedikativa() {
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
