package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen;
import de.nb.aventiure2.data.world.syscomp.wetter.tageszeit.TageszeitPraedikativumDescriber;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.ZweiPraedikativa;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.VerbOhneSubjAusserOptionalemExpletivemEs;
import de.nb.aventiure2.german.praedikat.VerbSubj;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.german.praedikat.Witterungsverb;
import de.nb.aventiure2.german.satz.EinzelnerSatz;
import de.nb.aventiure2.german.satz.Satz;
import de.nb.aventiure2.german.satz.ZweiSaetze;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.base.Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT;
import static de.nb.aventiure2.data.world.base.Temperatur.RECHT_HEISS;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_GESCHUETZT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.DrinnenDraussen.DRAUSSEN_UNTER_OFFENEM_HIMMEL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ANGENEHM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.AUSZUHALTEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEISSEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ERTRAEGLICHER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FROSTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GLUEHEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEFTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KLIRREND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.NAECHTLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.STARK;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNANGENEHM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNERWARTET;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WARM;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.FROST;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KAELTE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LEIB;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MORGEN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.SONNENHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumMitDat;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumMitPraedikativum;
import static de.nb.aventiure2.german.praedikat.DativPraedikativumPraedikatOhneSubjAusserOptionalemExpletivemEsOhneLeerstellen.dativPraedikativumWerdenMitDat;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatMit;
import static de.nb.aventiure2.german.praedikat.PraedikativumPraedikatOhneLeerstellen.praedikativumPraedikatWerdenMit;
import static de.nb.aventiure2.german.praedikat.VerbSubj.ANBRECHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BEGINNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BRENNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.EINSETZEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FRIEREN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.FROESTELN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.HERUNTERSCHEINEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.LIEGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.NACHLASSEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.SCHEINEN;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

/**
 * Beschreibt die {@link Temperatur} jeweils als {@link de.nb.aventiure2.german.satz.Satz}.
 * <p>
 * Diese Sätze sind für jede Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TemperaturSatzDescriber {
    private final TemperaturPraedikativumDescriber praedikativumDescriber;
    private final TageszeitPraedikativumDescriber tageszeitPraedikativumDescriber;

    public TemperaturSatzDescriber(
            final TageszeitPraedikativumDescriber tageszeitPraedikativumDescriber,
            final TemperaturPraedikativumDescriber praedikativumDescriber) {
        this.tageszeitPraedikativumDescriber = tageszeitPraedikativumDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
    }

    /**
     * Gibt Alternativen zurück, die einen Tageszeitenwechsel <i>draußen</i> in Kombination mit
     * einer Temperaturänderung beschreiben.
     *
     * @param currentTemperatur Die Temperatur nach  der Änderung.
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<EinzelnerSatz> altTemperaturSprungOderWechselUndTageszeitenwechselDraussen(
            final Tageszeit newTageszeit,
            final Temperatur currentTemperatur) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        final ImmutableCollection<AdjPhrOhneLeerstellen>
                altAdjPhrTemperaturaenderungAuchAttributiv =
                praedikativumDescriber.altAdjPhrTemperaturanstieg(
                        currentTemperatur,
                        true);

        if (newTageszeit == TAGSUEBER) {
            // "Ein kühlerer Tag beginnt" ist missverständlich (= der Morgen? Der
            // Vormittag?)
            // "Der Morgen geht in einen kühleren Tag über"
            alt.addAll(mapToSet(altAdjPhrTemperaturaenderungAuchAttributiv,
                    a -> VerbSubjObj.UEBERGEHEN.mit(np(INDEF, a, TAG))
                            .alsSatzMitSubjekt(MORGEN)
                            .mitAdvAngabe(new AdvAngabeSkopusSatz(a))));
        } else {
            // "Ein kühlerer Morgen bricht an"
            alt.addAll(mapToSet(altAdjPhrTemperaturaenderungAuchAttributiv,
                    a -> ANBRECHEN.alsSatzMitSubjekt(
                            np(INDEF, a, newTageszeit.getNomenFlexionsspalte()))));

            alt.addAll(mapToSet(altAdjPhrTemperaturaenderungAuchAttributiv,
                    a -> BEGINNEN.alsSatzMitSubjekt(
                            np(INDEF, a, newTageszeit.getNomenFlexionsspalte()))));

            final ImmutableCollection<AdjPhrOhneLeerstellen>
                    altAdjPhrTemperaturaenderungAuchNichtAttributiv =
                    praedikativumDescriber.altAdjPhrTemperaturanstieg(
                            currentTemperatur,
                            false);

            // "Der Morgen beginnt kühler"
            alt.addAll(mapToSet(altAdjPhrTemperaturaenderungAuchNichtAttributiv,
                    a -> BEGINNEN.alsSatzMitSubjekt(newTageszeit.getNomenFlexionsspalte())
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(a))));

        }

        return alt.build();
    }


    /**
     * Gibt alternative Sätze zurück, die beschreiben, wie die Temperatur sich eine Stufe
     * (Temperaturwechsel) oder mehrere Stufen (Temperaturspung) verändert hat.
     *
     * @param lastTemperatur    Die lokale Temperatur vor der Änderung. Es kann hier zu
     *                          seltenen Fällen kommen, dass der SC diese vorherige Temperatur an
     *                          diesem Ort noch gar nicht erlebt und auch gar nicht erwartet hat
     *                          - z.B. wenn der SC den ganzen heißen Tag an einem kühlen Ort
     *                          verbringt den kühlen Ort genau in dem Moment verlässt, wenn der
     *                          Tag sich wieder abkühlt. Zurzeit berücksichtigen wir diese Fälle
     *                          nicht.
     * @param currentTemperatur Die lokale Temperatur nach  der Änderung; muss von
     *                          der lastTemperatur unterschiedlich sein
     */
    ImmutableCollection<Satz> altSprungOderWechsel(
            final AvDateTime time,
            final Temperatur lastTemperatur,
            final Temperatur currentTemperatur,
            final DrinnenDraussen drinnenDraussen) {
        checkArgument(lastTemperatur != currentTemperatur,
                "Kein Temperatursprung oder -wechsel: %s", lastTemperatur);

        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        final int delta = currentTemperatur.minus(lastTemperatur);
        if (Math.abs(delta) <= 1) {
            // Die Temperatur hat sich nur um eine Stufe verändert
            // ("Temperaturwechsel").

            if ((delta == -1 && currentTemperatur.compareTo(Temperatur.KUEHL) <= 0)
                    || (delta == 1 && currentTemperatur.compareTo(Temperatur.WARM) >= 0)) {
                // "es wird kalt"
                alt.addAll(mapToList(praedikativumDescriber.alt(
                        currentTemperatur, time.getTageszeit(),
                        drinnenDraussen.isDraussen()),
                        Praedikativum::alsEsWirdSatz));

                // "eben wird es kalt"
                alt.addAll(mapToList(praedikativumDescriber.alt(
                        currentTemperatur, time.getTageszeit(),
                        drinnenDraussen.isDraussen()),
                        praedikativum -> praedikativum.alsEsWirdSatz()
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("eben"))));

                if (drinnenDraussen.isDraussen()) {
                    // "die Luft wird kalt"
                    alt.addAll(mapToList(
                            praedikativumDescriber
                                    .altLuftAdjPhr(currentTemperatur, time.getTageszeit()),
                            a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(LUFT)));
                }

                // "mit einem Mal friert dich"
                alt.addAll(mapToList(
                        alt(currentTemperatur, time.getTageszeit(), drinnenDraussen),
                        dichFriert -> dichFriert
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("mit einem Mal"))));
            }

            if ((delta == -1
                    && currentTemperatur.compareTo(Temperatur.KNAPP_UNTER_DEM_GEFRIERPUNKT) <= 0)
                    || (delta == 1 && currentTemperatur.compareTo(Temperatur.SEHR_HEISS) >= 0)) {
                // "es ist jetzt wirklich sehr kalt"
                alt.addAll(mapToList(praedikativumDescriber.alt(
                        currentTemperatur, time.getTageszeit(),
                        drinnenDraussen.isDraussen()),
                        praedikativum -> praedikativum.alsEsIstSatz()
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wirklich"))));
            }

            if (lastTemperatur.hasNachfolger(currentTemperatur)) {
                // Die Temperatur ist um eine Stufe angestiegen

                return altTemperaturwechselAnstieg(
                        time.getTageszeit(), currentTemperatur, drinnenDraussen);
            }

            // Die Temperatur ist um eine Stufe gesunken
            return altTemperaturwechselAbfall(
                    time.getTageszeit(), currentTemperatur, drinnenDraussen);
        }

        // Es gab weitere Temperaturen dazwischen ("Temperatursprung")
        alt.addAll(mapToSet(praedikativumDescriber.altAdjPhrTemperaturaenderung(
                lastTemperatur, currentTemperatur, false),
                Praedikativum::alsEsIstSatz));

        if ((delta < 0 && currentTemperatur.compareTo(Temperatur.KUEHL) <= 0)
                || (delta > 0 && currentTemperatur.compareTo(Temperatur.WARM) >= 0)) {
            // "jetzt ist es kalt"
            alt.addAll(mapToList(alt(currentTemperatur, time.getTageszeit(), drinnenDraussen),
                    satz -> satz.mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt"))));

            // "mittlerweile frierst du"
            alt.addAll(mapToList(alt(currentTemperatur, time.getTageszeit(), drinnenDraussen),
                    satz -> satz.mitAdvAngabe(new AdvAngabeSkopusSatz("mittlerweile"))));

            // "längst frierst du"
            alt.addAll(mapToList(alt(currentTemperatur, time.getTageszeit(), drinnenDraussen),
                    satz -> satz.mitAdvAngabe(new AdvAngabeSkopusSatz("längst"))));
        }

        return alt.build();
    }

    /**
     * Gibt alternative Sätze zurück, die beschreiben, wie die Temperatur um eine Stufe
     * gestiegen ist.
     *
     * @param endTemperatur Die lokale Temperatur nach der Änderung
     */
    private ImmutableCollection<Satz> altTemperaturwechselAnstieg(
            final Tageszeit tageszeit,
            final Temperatur endTemperatur, final DrinnenDraussen drinnenDraussen) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(praedikativumDescriber.altAdjPhrTemperaturanstieg(
                endTemperatur, false),
                Praedikativum::alsEsIstSatz));

        if (endTemperatur.compareTo(Temperatur.KUEHL) <= 0) {
            // "es wird etwas wärmer, aber es ist immer noch ziemlich kühl"
            alt.addAll(
                    altWirdEtwasWaermer().stream()
                            .flatMap(esWirdEtwasWaermer -> praedikativumDescriber.alt(
                                    endTemperatur, tageszeit,
                                    drinnenDraussen.isDraussen()).stream()
                                    .map(praedikativum ->
                                            praedikativum.alsEsWirdSatz()
                                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                                            "immer noch")))
                                    .map(esIstImmerNochZiemlichKuehl ->
                                            new ZweiSaetze(
                                                    esWirdEtwasWaermer,
                                                    ", aber",
                                                    esIstImmerNochZiemlichKuehl)))
                            .collect(toSet()));
        }

        switch (endTemperatur) {
            case KLIRREND_KALT:  // Kann gar nicht sein
                // fall-through
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(FRIEREN.alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES)
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wohl nicht mehr")));
                alt.add(NACHLASSEN.alsSatzMitSubjekt(FROST)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("allmählich")));
                break;
            case KUEHL:
                alt.add(NACHLASSEN.alsSatzMitSubjekt(KAELTE.mit(UNANGENEHM)),
                        KALT.mitGraduativerAngabe("längst nicht mehr so")
                                .alsEsIstSatz()
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt")),
                        praedikativumPraedikatWerdenMit(
                                ERTRAEGLICHER.mitGraduativerAngabe("wieder"))
                                .alsSatzMitSubjekt(KAELTE.mit(UNANGENEHM)));
                break;
            case WARM:
                alt.add(WARM.mitAdvAngabe(new AdvAngabeSkopusSatz(UNERWARTET)).alsEsWirdSatz(),
                        WARM.mitAdvAngabe(new AdvAngabeSkopusSatz(ANGENEHM)).alsEsWirdSatz(),
                        dativPraedikativumMitPraedikativum(WARM.mitGraduativerAngabe("wieder"))
                                .mit(duSc()).alsSatz(),
                        dativPraedikativumMitPraedikativum(WARM.mitGraduativerAngabe("wohlig"))
                                .mit(duSc())
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("wieder")).alsSatz(),
                        dativPraedikativumMitPraedikativum(WARM.mitGraduativerAngabe("wieder"))
                                .mit(duSc())
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("endlich"))
                                .alsSatz());
                break;
            case RECHT_HEISS:
                alt.add(praedikativumPraedikatWerdenMit(UNANGENEHM)
                        .alsSatzMitSubjekt(HITZE)
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("allmählich")));
                break;
            case SEHR_HEISS:
                alt.add(praedikativumPraedikatWerdenMit(HEFTIG).alsSatzMitSubjekt(HITZE),
                        praedikativumPraedikatMit(AUSZUHALTEN.mitGraduativerAngabe("kaum mehr"))
                                .alsSatzMitSubjekt(HITZE.mit(GLUEHEND))
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("jetzt")));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + endTemperatur);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Sätze zurück, die beschreiben, wie die Temperatur um eine Stufe
     * gesunken ist.
     *
     * @param endTemperatur Die lokale Temperatur nach der Änderung
     */
    private ImmutableCollection<Satz> altTemperaturwechselAbfall(
            final Tageszeit tageszeit,
            final Temperatur endTemperatur, final DrinnenDraussen drinnenDraussen) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(praedikativumDescriber.altAdjPhrTemperaturabfall(
                endTemperatur, false),
                Praedikativum::alsEsIstSatz));

        if (endTemperatur.compareTo(Temperatur.WARM) >= 0) {
            // "es wird etwas kühler, aber es ist immer noch ziemlich warm"
            alt.addAll(
                    altWirdEtwasKuehler().stream()
                            .flatMap(esWirdEtwasKuehler -> praedikativumDescriber.alt(
                                    endTemperatur, tageszeit,
                                    drinnenDraussen.isDraussen()).stream()
                                    .map(praedikativum ->
                                            praedikativum.alsEsWirdSatz()
                                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                                            "immer noch")))
                                    .map(esIstImmerNochZiemlichWarm ->
                                            new ZweiSaetze(
                                                    esWirdEtwasKuehler,
                                                    ", aber",
                                                    esIstImmerNochZiemlichWarm)))
                            .collect(toSet()));
        }

        switch (endTemperatur) {
            case KLIRREND_KALT:
                // "eine beißende Kälte setzt ein"
                alt.add(EINSETZEN.alsSatzMitSubjekt(np(INDEF, BEISSEND, KAELTE)));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                // "eine frostige Kälte setzt ein"
                alt.add(EINSETZEN.alsSatzMitSubjekt(np(INDEF, FROSTIG, KAELTE)));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(VerbSubj.ABKUEHLEN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("empfindlich"))
                                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES),
                        dativPraedikativumWerdenMitDat(duSc()) //
                                // "dir wird"
                                .mit(KALT)
                                .alsSatz(),
                        // "Kälte setzt ein"
                        EINSETZEN.alsSatzMitSubjekt(npArtikellos(KAELTE)));
                if (tageszeit == NACHTS) {
                    // "Die nächtliche Kälte setzt ein"
                    alt.add(EINSETZEN.alsSatzMitSubjekt(KAELTE.mit(NAECHTLICH)));
                }
                break;
            case KUEHL:
                alt.add(VerbSubj.ABKUEHLEN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("deutlich"))
                                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES),
                        VerbSubj.ABKUEHLEN
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("spürbar"))
                                .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES));
                break;
            case WARM:
                alt.add(NACHLASSEN.alsSatzMitSubjekt(HITZE)
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt")),
                        NACHLASSEN.alsSatzMitSubjekt(HITZE)
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("allmählich")),
                        HEISS.mitGraduativerAngabe("längst nicht mehr so")
                                .alsEsIstSatz()
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt")),
                        HEISS.mitGraduativerAngabe("nicht mehr so")
                                .alsEsIstSatz()
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("jetzt"))
                );
                break;
            case RECHT_HEISS:
                // fall-through
            case SEHR_HEISS: // Kann gar nicht sein
                alt.add(NACHLASSEN.alsSatzMitSubjekt(HITZE)
                                .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("ein wenig")),
                        praedikativumPraedikatWerdenMit(
                                ERTRAEGLICHER.mitGraduativerAngabe("wieder"))
                                .alsSatzMitSubjekt(HITZE)
                );
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + endTemperatur);
        }

        return alt.build();
    }

    private ImmutableCollection<EinzelnerSatz> altWirdEtwasWaermer() {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(praedikativumDescriber.altAdjPhrEtwasWaermer(),
                Praedikativum::alsEsWirdSatz));

        return alt.build();
    }

    private ImmutableCollection<EinzelnerSatz> altWirdEtwasKuehler() {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        alt.addAll(mapToSet(praedikativumDescriber.altAdjPhrEtwasKuehler(),
                Praedikativum::alsEsWirdSatz));

        alt.add(VerbSubj.ABKUEHLEN.alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES));

        return alt.build();
    }

    /**
     * Gibt Sätze zurück wie "draußen ist es kalt" - es in jedem Fall auch mindesten ein
     * {@link EinzelnerSatz} dabei.
     */
    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    public ImmutableCollection<Satz> altKommtNachDraussen(
            final Temperatur temperatur,
            final AvTime time,
            final boolean unterOffenenHimmel,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        final DrinnenDraussen kommtNachDrinnenDraussen =
                unterOffenenHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL :
                        DRAUSSEN_GESCHUETZT;
        alt.addAll(
                mapToList(alt(temperatur, time,
                        kommtNachDrinnenDraussen,
                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                        s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("draußen"))));

        if (temperatur.isBetweenIncluding(KNAPP_UEBER_DEM_GEFRIERPUNKT, RECHT_HEISS)) {
            alt.addAll(alt(temperatur, time, kommtNachDrinnenDraussen,
                    auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben));
        }

        switch (temperatur) {
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
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

    /**
     * Gibt Sätze zurück wie "es ist kalt" - es in jedem Fall auch mindesten ein
     * {@link EinzelnerSatz} dabei.
     */
    @CheckReturnValue
    public ImmutableCollection<Satz> alt(
            final Temperatur temperatur,
            final AvTime time,
            final DrinnenDraussen drinnenDraussen,
            final boolean auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben) {
        final ImmutableSet.Builder<Satz> alt = ImmutableSet.builder();

        alt.addAll(alt(temperatur, time.getTageszeit(), drinnenDraussen));
        if (drinnenDraussen.isDraussen()) {
            alt.addAll(altMitTageszeitLichtverhaeltnissen(temperatur, time,
                    drinnenDraussen == DRAUSSEN_UNTER_OFFENEM_HIMMEL,
                    auchEinmaligeErlebnisseDraussenNachTageszeitenwechselBeschreiben));
        }

        return alt.build();
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    @CheckReturnValue
    private ImmutableCollection<EinzelnerSatz> alt(
            final Temperatur temperatur,
            final Tageszeit tageszeit,
            final DrinnenDraussen drinnenDraussen) {
        final ImmutableSet.Builder<EinzelnerSatz> alt = ImmutableSet.builder();

        alt.addAll(mapToList(praedikativumDescriber.alt(
                temperatur, tageszeit,
                drinnenDraussen.isDraussen()),
                Praedikativum::alsEsIstSatz));

        if (drinnenDraussen.isDraussen()) {
            // "die Luft ist kalt"
            alt.addAll(mapToList(
                    praedikativumDescriber.altLuftAdjPhr(temperatur, tageszeit),
                    a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(LUFT)));

            if (tageszeit == ABENDS
                    && drinnenDraussen != DRAUSSEN_UNTER_OFFENEM_HIMMEL
                    && temperatur.isBetweenIncluding(Temperatur.WARM, Temperatur.RECHT_HEISS)) {
                alt.addAll(altDraussenNoch(temperatur, ABENDS));
            }
        }

        switch (temperatur) {
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
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

    /**
     * Gibt Alternativen zurück wie "es ist schon dunkel und ziemlich kühl" - oder eine leere
     * {@link java.util.Collection}.
     */
    private ImmutableCollection<Satz> altMitTageszeitLichtverhaeltnissen(
            final Temperatur temperatur,
            final AvTime time,
            final boolean unterOffenemHimmel,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableCollection.Builder<Satz> alt = ImmutableSet.builder();

        // "schon dunkel" / "dunkel"
        final ImmutableCollection<AdjPhrOhneLeerstellen> altSchonBereitsNochDunkelAdjPhr =
                tageszeitPraedikativumDescriber.altSchonBereitsNochDunkelHellAdjPhr(
                        time,
                        auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben
                                && (time.getTageszeit() == MORGENS || time.getTageszeit() == NACHTS)
                );

        if (!altSchonBereitsNochDunkelAdjPhr.isEmpty()) {
            alt.addAll(
                    alt(temperatur, time.getTageszeit(),
                            unterOffenemHimmel ? DRAUSSEN_UNTER_OFFENEM_HIMMEL :
                                    DRAUSSEN_UNTER_OFFENEM_HIMMEL)
                            .stream()
                            .flatMap(zweiterSatz ->
                                    altSchonBereitsNochDunkelAdjPhr.stream()
                                            .map(schonDunkel ->
                                                    new ZweiSaetze(
                                                            schonDunkel.alsEsIstSatz(),
                                                            ";",
                                                            zweiterSatz)))
                            .collect(toSet()));

            // "es ist schon dunkel und ziemlich kühl"
            alt.addAll(praedikativumDescriber.alt(
                    temperatur, time.getTageszeit(), true).stream()
                    .flatMap(tempAdjPhr ->
                            altSchonBereitsNochDunkelAdjPhr.stream()
                                    .map(schonDunkel ->
                                            new ZweiPraedikativa<>(
                                                    schonDunkel, tempAdjPhr)
                                                    .alsEsIstSatz()))
                    .collect(toSet()));
        }

        return alt.build();
    }

    // IDEA Beschreibungen, die erst nach einer Weile Sinn ergeben:
    //  - Die ganze Zeit über ist dir kalt
    //  - du("schmachtest", "in der Hitze")

    /**
     * Gibt alternative Sätze zurück, die sich auf "heute", "den Tag" o.Ä.
     * beziehen - sofern sinnvoll - sonst leer.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<Satz> altDraussenHeuteDerTagSofernSinnvoll(
            final Temperatur temperatur,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final AvTime time,
            final boolean sonneNichtErwaehnen) {
        if (!heuteOderDerTagSinnvoll(
                temperatur, generelleTemperaturOutsideLocationTemperaturRange, time)) {
            return ImmutableSet.of();
        }

        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        // "Heute ist es heiß / warmes Wetter."
        alt.addAll(
                mapToList(praedikativumDescriber.alt(
                        temperatur, time.getTageszeit(),
                        true // Drinnen sind solche Sätze
                        // nicht sinnvoll
                ), a -> a.alsEsIstSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("heute"))));

        alt.addAll(altDerTag(time.getTageszeit(), temperatur));

        if (!sonneNichtErwaehnen) {
            alt.addAll(mapToList(altSonnenhitzeWennHeissUndNichtNachts(temperatur, time,
                    false),
                    s -> s.mitAdvAngabe(new AdvAngabeSkopusSatz("heute"))));
        }

        return alt.build();
    }

    /**
     * Erzeugt Sätze in der Art "Der Tag ist sehr heiß" - nur unter gewissen
     * Umständen sinnvoll, z.B. nur draußen,
     * vgl. {@link #heuteOderDerTagSinnvoll(Temperatur, boolean, AvTime)}.
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<Satz> altDerTag(final Tageszeit tageszeit,
                                                final Temperatur temperatur) {
        return praedikativumDescriber.alt(temperatur, tageszeit, true).stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> praedikativumPraedikatMit(a).alsSatzMitSubjekt(TAG))
                .collect(toImmutableList());
    }

    /**
     * Gibt zurück, ob bei dieser Temperatur zu dieser Uhrzeit Sätze über "heute" oder "den Tag"
     * sinnvoll sind. - Generell wird es noch von anderen Kriterien abhängen, wann solche
     * Sätze sinnvoll sind, z.B. wohl nur draußen.
     */
    @CheckReturnValue
    private boolean heuteOderDerTagSinnvoll(
            final Temperatur temperatur,
            final boolean generelleTemperaturOutsideLocationTemperaturRange,
            final AvTime time) {
        return // Wenn man das volle Extrem der Temperatur nicht spürt, kein "der Tag ist"!
                !generelleTemperaturOutsideLocationTemperaturRange
                        // Abends zu sagen "der Tag ist recht heiß" wäre unnatürlich
                        && TagestemperaturverlaufUtil
                        .saetzeUeberHeuteOderDenTagVonDerUhrzeitHerSinnvoll(time)
                        // Zu sagen "der Tag so warm oder kalt wie jeder andere auch" wäre
                        // unnatürlich
                        && !temperatur.isBetweenIncluding(
                        KNAPP_UEBER_DEM_GEFRIERPUNKT, Temperatur.WARM);
    }

    /**
     * Gibt alternative Sätze zurück mit kombinierte Adjektivphrasen, die auf die
     * schöne Tageszeit referenzieren: "Es ist ein schöner Abend und noch ziemlich warm",
     * "Es ist ein schöner Tag, aber eiskalt[,]" o.Ä.
     */
    @NonNull
    @CheckReturnValue
    public ImmutableList<EinzelnerSatz> altSchoneTageszeitUndAberSchonNochAdjPhr(
            final Temperatur temperatur, final Tageszeit tageszeit) {
        return mapToList(praedikativumDescriber.altSchoneTageszeitUndAberSchonNochAdjPhr(
                temperatur, tageszeit),
                Praedikativum::alsEsIstSatz);
    }

    /**
     * Gibt alternative Sätze für draußen zurück in der Art
     * "Es ist noch (sehr kalt / ziemlich warm / heißes Wetter)".
     */
    @NonNull
    @CheckReturnValue
    public ImmutableCollection<EinzelnerSatz> altDraussenNoch(final Temperatur temperatur,
                                                              final Tageszeit tageszeit) {
        final ImmutableList.Builder<EinzelnerSatz> alt = ImmutableList.builder();

        // "Es ist (noch (sehr kalt))."
        alt.addAll(praedikativumDescriber.alt(temperatur, tageszeit, true).stream()
                .filter(AdjPhrOhneLeerstellen.class::isInstance)
                .map(a -> praedikativumPraedikatMit(
                        ((AdjPhrOhneLeerstellen) a)
                                .mitAdvAngabe(new AdvAngabeSkopusSatz("noch")))
                        .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES))
                .collect(toImmutableList()));

        // "Es ist noch warmes Wetter."
        alt.addAll(praedikativumDescriber.alt(temperatur, tageszeit, true).stream()
                .filter(obj -> !(obj instanceof AdjPhrOhneLeerstellen))
                .map(a -> a.alsEsIstSatz()
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg("noch")))
                .collect(toImmutableList()));

        return alt.build();
    }

    /**
     * Gibt alternative Sätze zurück über die Sonnenhitze - wenn es entsprechend
     * heiß ist (und nicht nachts).
     * <p>
     * (Solche Sätze sind nur bei geringer Bewölkung sinnvoll, nur draußen und sicher
     * nicht nachts).
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<Satz> altSonnenhitzeWennHeissUndNichtNachts(
            final Temperatur temperatur,
            final AvTime time, final boolean auchMitBezugAufKonkreteTageszeit) {
        if (time.getTageszeit() == NACHTS) {
            return ImmutableSet.of();
        }

        final ImmutableList.Builder<Satz> alt = ImmutableList.builder();

        if (temperatur.compareTo(Temperatur.RECHT_HEISS) == 0) {
            alt.add(SCHEINEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                    WARM.mitGraduativerAngabe("sehr"))),
                    SCHEINEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)),
                    HERUNTERSCHEINEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)));
        }

        if (temperatur.compareTo(Temperatur.SEHR_HEISS) == 0) {
            alt.add(BRENNEN.alsSatzMitSubjekt(SONNE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)),
                    BRENNEN.alsSatzMitSubjekt(SONNENHITZE)
                            .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(STARK))
            );

            if (auchMitBezugAufKonkreteTageszeit && time.gegenMittag()) {
                alt.add(BRENNEN.alsSatzMitSubjekt(SONNE)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("zu Mittag"))
                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(HEISS)));
            }
        }

        return alt.build();
    }


    /**
     * Gibt Sätze zurück wie "hier ist es angenehm kühl".
     */
    @CheckReturnValue
    public ImmutableCollection<Satz> altDeutlicherUnterschiedZuVorLocation(
            final Temperatur temperatur, final int delta) {
        return mapToList(
                praedikativumDescriber
                        .altAdjPhrDeutlicherUnterschiedZuVorLocation(temperatur, delta),
                adjPhr -> praedikativumPraedikatMit(adjPhr)
                        .alsSatzMitSubjekt(Personalpronomen.EXPLETIVES_ES)
                        .mitAdvAngabe(new AdvAngabeSkopusSatz("hier"))
                        .mitAnschlusswort(null));
    }
}
