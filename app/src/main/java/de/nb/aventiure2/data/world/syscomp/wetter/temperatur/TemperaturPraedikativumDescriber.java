package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.data.world.syscomp.wetter.base.WetterParamChange;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.adjektiv.ZweiAdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Praedikativum;
import de.nb.aventiure2.german.base.ZweiPraedikativa;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusSatz;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.MORGENS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.data.time.Tageszeit.TAGSUEBER;
import static de.nb.aventiure2.data.world.base.Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT;
import static de.nb.aventiure2.data.world.base.Temperatur.WARM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.ANGENEHM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.AUFGEHEIZT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEISSEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BITTERKALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BRUELLEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EISEKALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EISIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EISKALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FLIRREND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FROSTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GLUEHEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KUEHL;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KUEHLER;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.NAECHTLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SCHOEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNANGENEHM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNERTRAEGLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.UNERWARTET;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WAERMER;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKELHEIT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.EISESKAELTE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KAELTE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KUEHLE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WAERME;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WETTER;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.util.StreamUtil.*;
import static java.util.stream.Collectors.toSet;

/**
 * Beschreibt die {@link Temperatur} als {@link Praedikativum}.
 * <p>
 * Diese Phrasen sind für jede Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TemperaturPraedikativumDescriber {

    ImmutableSet<EinzelneSubstantivischePhrase> altDraussenSubstPhr(
            final Temperatur temperatur, final AvTime time) {
        final ImmutableSet.Builder<EinzelneSubstantivischePhrase> alt = ImmutableSet.builder();

        // "die klirrend kalte Luft"
        alt.addAll(mapToList(altLuftAdjPhr(temperatur, time.getTageszeit()), LUFT::mit));

        if (time.getTageszeit() == NACHTS) {
            alt.addAll(altNaechtlicheDunkelheitNominalphrase(temperatur));
        }

        alt.addAll(altTageszeit(temperatur, time.getTageszeit()));

        switch (temperatur) {
            case KLIRREND_KALT:
                alt.add(EISESKAELTE, KAELTE.mit(BEISSEND));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                alt.add(KAELTE.mit(FROSTIG));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.addAll(altAkkKnappUeberGefrierpunktSubstPhr(time));
                break;
            case KUEHL:
                alt.add(KUEHLE);
                break;
            case WARM:
                alt.add(WAERME, WETTER.mit(AdjektivOhneErgaenzungen.WARM));
                break;
            case RECHT_HEISS:
                break;
            case SEHR_HEISS:
                alt.addAll(altSehrHeissSubstPhr(time));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private static ImmutableCollection<EinzelneSubstantivischePhrase>
    altAkkKnappUeberGefrierpunktSubstPhr(final AvTime time) {
        final ImmutableSet.Builder<EinzelneSubstantivischePhrase> alt =
                ImmutableSet.builder();

        alt.add(KAELTE);
        if (time.getTageszeit() == NACHTS) {
            alt.add(KAELTE.mit(NAECHTLICH));
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private static ImmutableCollection<EinzelneSubstantivischePhrase>
    altSehrHeissSubstPhr(final AvTime time) {
        final ImmutableSet.Builder<EinzelneSubstantivischePhrase> alt = ImmutableSet.builder();

        alt.add(HITZE);
        alt.add(HITZE);
        alt.add(HITZE.mit(GLUEHEND));
        alt.add(HITZE.mit(FLIRREND));
        if (time.gegenMittag()) {
            alt.add(MITTAGSHITZE);
        } else if (time.getTageszeit() == ABENDS) {
            alt.add(ABENDHITZE);
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private ImmutableCollection<Nominalphrase> altNaechtlicheDunkelheitNominalphrase(
            final Temperatur temperatur) {
        final ImmutableList.Builder<Nominalphrase> alt =
                ImmutableList.builder();

        // "die eiskalte Dunkelheit"
        alt.addAll(mapToList(altLuftAdjPhr(temperatur, NACHTS), DUNKELHEIT::mit));

        switch (temperatur) {
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
                alt.add(HITZE.mit(AdjektivOhneErgaenzungen.DUNKEL));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }


    /**
     * Erzeugt Nominalphrasen in der Art "der heiße Morgen".
     */
    @NonNull
    @CheckReturnValue
    private ImmutableCollection<Nominalphrase> altTageszeit(final Temperatur temperatur,
                                                            final Tageszeit tageszeit) {
        return altAdjPhr(temperatur, true).stream()
                .map(a -> tageszeit.getNomenFlexionsspalte().mit(a))
                .collect(toImmutableSet());
    }

    /**
     * Gibt alternative Prädikative zurück für eine Beschreibung in der Art
     * "Es ist (sehr kalt / ziemlich warm / warmes Wetter)". Es ist wichtig,
     * dass in den Beschreibungen, wenn sie ein expletives "es" enthalten -
     * dieses ausschließlich im Vorfeld steht - ansonsten könnte es zu
     * Sätzen wie ?"heute ist es warmes Wetter" oder ?"draußen ist es warmes Wetter"
     * führen.
     * <p>
     * Das Eregebnis von {@link #altAdjPhr(Temperatur, boolean)}} ist bereits enthalten
     */
    @NonNull
    @CheckReturnValue
    ImmutableList<Praedikativum> altSofernExpletivesEsHoechstensImVorfeldSteht(
            final Temperatur temperatur, final Tageszeit tageszeit,
            final boolean draussen) {
        final ImmutableList.Builder<Praedikativum> alt = ImmutableList.builder();

        alt.addAll(altAdjPhr(temperatur, false));

        switch (temperatur) {
            case KLIRREND_KALT:
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                break;
            case KUEHL:
                break;
            case WARM:
                if (draussen && tageszeit != NACHTS) {
                    // "es ist ein heißer Tag", aber nicht ?"heute ist es ein heißer Tag"
                    alt.add(npArtikellos(AdjektivOhneErgaenzungen.WARM,
                            NomenFlexionsspalte.WETTER));
                }
                break;
            case RECHT_HEISS:
                if (draussen && tageszeit == TAGSUEBER) {
                    // "es ist ein heißer Tag", aber nicht ?"heute ist es ein heißer Tag"
                    alt.add(np(INDEF, HEISS, TAG));
                }
                break;
            case SEHR_HEISS:
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Adjektivphrasen <i>für die Luft</i> zurück:
     * (Die Luft ist) "frostig kalt", "warm", "aufgeheizt" etc.
     */
    @NonNull
    @CheckReturnValue
    ImmutableList<AdjPhrOhneLeerstellen> altLuftAdjPhr(
            final Temperatur temperatur,
            final Tageszeit tageszeit) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> alt =
                ImmutableList.builder();

        alt.addAll(altAdjPhr(temperatur, true));

        switch (temperatur) {
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
                if (tageszeit != NACHTS) {
                    alt.add(FLIRREND);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }


    /**
     * Gibt alternative kombinierte Adjektivphrasen, die auf die
     * schöne Tageszeit referenzieren: "ein schöner Abend und noch ziemlich warm",
     * "ein schöner Morgen und schon ziemlich warm", "ein schöner Tag, aber eiskalt[,]" o.Ä.
     */
    @NonNull
    @CheckReturnValue
    ImmutableList<ZweiPraedikativa<Praedikativum>> altSchoneTageszeitUndAberSchonNochAdjPhr(
            final Temperatur temperatur, final Tageszeit tageszeit) {

        final boolean eherWarm = temperatur.compareTo(WARM) >= 0;
        final boolean eherKalt = temperatur.compareTo(KNAPP_UEBER_DEM_GEFRIERPUNKT) <= 0;

        final boolean konnektorErfordertKommata;
        final String konnektor;
        if (eherKalt && (tageszeit == MORGENS || tageszeit == ABENDS)) {
            konnektorErfordertKommata = true;
            konnektor = "aber"; // IDEA: doch
        } else {
            konnektorErfordertKommata = false;
            konnektor = "und";
        }

        @Nullable final AdvAngabeSkopusSatz advAngabe;
        if ((tageszeit == ABENDS && eherWarm) || (tageszeit == MORGENS && eherKalt)) {
            advAngabe = new AdvAngabeSkopusSatz("noch");
        } else {
            if ((tageszeit == ABENDS && eherKalt) || (tageszeit == MORGENS && eherWarm)) {
                advAngabe = new AdvAngabeSkopusSatz("schon");
            } else {
                advAngabe = null;
            }
        }

        return mapToList(altAdjPhr(temperatur, false),
                tempAdjPhr -> new ZweiPraedikativa<>(
                        np(INDEF, SCHOEN, tageszeit.getNomenFlexionsspalte()),
                        konnektorErfordertKommata, konnektor, // ", aber"
                        tempAdjPhr.mitAdvAngabe(advAngabe)));
    }


    /**
     * Gibt alternative Adjektivphrasen zurück für eine Beschreibung in der Art
     * "Hier ist es (angenehm warm)"
     */
    @NonNull
    @CheckReturnValue
    ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrDeutlicherUnterschiedZuVorLocation(
            final Temperatur temperatur, final int delta) {
        if (delta > 0) {
            return altAdjPhrDeutlichWaermerAlsVorLocation(temperatur);
        }

        return altAdjPhrDeutlichKaelterAlsVorLocation(temperatur);
    }

    /**
     * Gibt alternative Adjektivphrasen zurück für eine Beschreibung in der Art
     * "Hier ist es (angenehm warm)"
     */
    @NonNull
    @CheckReturnValue
    private ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrDeutlichWaermerAlsVorLocation(
            final Temperatur temperatur) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> alt = ImmutableList.builder();

        switch (temperatur) {
            case KLIRREND_KALT:  // Sollte eigentlich gar nicht sein
                // Fall-through
            case KNAPP_UNTER_DEM_GEFRIERPUNKT: // Sollte eigentlich gar nicht sein
                // Fall-through
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(AdjektivOhneErgaenzungen.WAERMER.mitGraduativerAngabe("etwas"),
                        AdjektivOhneErgaenzungen.WAERMER.mitGraduativerAngabe("ein wenig"),
                        AdjektivOhneErgaenzungen.KALT.mitGraduativerAngabe("nicht ganz so"),
                        AdjektivOhneErgaenzungen.KALT
                                .mitGraduativerAngabe("nicht ganz so elendig"),
                        AdjektivOhneErgaenzungen.KALT.mitGraduativerAngabe("etwas weniger"));
                break;
            case KUEHL:
                alt.add(AdjektivOhneErgaenzungen.KALT.mitGraduativerAngabe("längst nicht so"),
                        AdjektivOhneErgaenzungen.KALT.mitGraduativerAngabe("deutlich weniger"),
                        AdjektivOhneErgaenzungen.WARM.mitGraduativerAngabe("beinahe schon"),
                        AdjektivOhneErgaenzungen.WAERMER.mitGraduativerAngabe("deutlich"));
                break;
            case WARM:
                alt.add(AdjektivOhneErgaenzungen.WARM.mitGraduativerAngabe("angenehm"),
                        AdjektivOhneErgaenzungen.WARM.mitGraduativerAngabe("gemütlich"),
                        AdjektivOhneErgaenzungen.WARM.mitGraduativerAngabe("schön"),
                        AdjektivOhneErgaenzungen.WARM.mitGraduativerAngabe("richtig"),
                        AdjektivOhneErgaenzungen.WARM.mitGraduativerAngabe("kuschlig"),
                        AdjektivOhneErgaenzungen.WAERMER.mitGraduativerAngabe("deutlich"));
                break;
            case RECHT_HEISS:
                // Fall-through
            case SEHR_HEISS:
                alt.addAll(altAdjPhr(temperatur, false));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Adjektivphrasen zurück für eine Beschreibung in der Art
     * "Hier ist es (angenehm kühl)"
     */
    @NonNull
    @CheckReturnValue
    private ImmutableList<AdjPhrOhneLeerstellen> altAdjPhrDeutlichKaelterAlsVorLocation(
            final Temperatur temperatur) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> alt = ImmutableList.builder();

        switch (temperatur) {
            case KLIRREND_KALT:
                // Fall-through
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                // Fall-through
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.addAll(altAdjPhr(temperatur, false));
                break;
            case KUEHL:
                alt.add(AdjektivOhneErgaenzungen.KUEHL.mitGraduativerAngabe("angenehm"),
                        AdjektivOhneErgaenzungen.KUEHL.mitGraduativerAngabe("schön"),
                        AdjektivOhneErgaenzungen.KUEHL.mitGraduativerAngabe("erfreulich"),
                        AdjektivOhneErgaenzungen.KUEHLER.mitGraduativerAngabe("deutlich"));
                break;
            case WARM:
                // Fall-through
            case RECHT_HEISS: // Sollte eigentlich gar nicht sein
                // Fall-through
            case SEHR_HEISS: // Sollte eigentlich gar nicht sein
                alt.add(AdjektivOhneErgaenzungen.KUEHLER,
                        AdjektivOhneErgaenzungen.HEISS.mitGraduativerAngabe("nicht ganz so"),
                        AdjektivOhneErgaenzungen.HEISS
                                .mitGraduativerAngabe("nicht ganz so elendig"),
                        AdjektivOhneErgaenzungen.HEISS.mitGraduativerAngabe("etwas weniger"));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Adjektivphrasen für eine Temperaturänderungen
     * zurück (um eine oder mehrere Stufen, Abfall oder Anstieg), also einen
     * "Temperaturwechsel" oder einen "Temperatursprung".
     *
     * @param change Die Temperaturänderung
     */
    @NonNull
    @CheckReturnValue
    ImmutableCollection<AdjPhrOhneLeerstellen> altAdjPhrTemperaturaenderung(
            final WetterParamChange<Temperatur> change,
            final boolean fuerAttributiveVerwendung) {
        if (change.getNachher().compareTo(change.getVorher()) >= 0) {
            return altAdjPhrTemperaturanstieg(change.getNachher(), fuerAttributiveVerwendung);
        }

        return altAdjPhrTemperaturabfall(change.getNachher(), fuerAttributiveVerwendung);
    }

    /**
     * Gibt alternative Adjektivphrasen zurück, die beschreiben, wie die Temperatur gestiegen
     * ist.
     *
     * @param endTemperatur Die lokale Temperatur nach der Änderung
     */
    ImmutableCollection<AdjPhrOhneLeerstellen> altAdjPhrTemperaturanstieg(
            final Temperatur endTemperatur,
            final boolean fuerAttributiveVerwendung) {
        final ImmutableSet.Builder<AdjPhrOhneLeerstellen> alt = ImmutableSet.builder();

        if (endTemperatur.compareTo(Temperatur.KUEHL) <= 0) {
            // "etwas wärmer, aber immer noch ziemlich kühl"
            alt.addAll(
                    altAdjPhrEtwasWaermer().stream()
                            .flatMap(etwasWaermer -> altAdjPhr(
                                    endTemperatur, fuerAttributiveVerwendung).stream()
                                    .map(ziemlichKuehl ->
                                            ziemlichKuehl.mitAdvAngabe(
                                                    new AdvAngabeSkopusSatz("immer noch")))
                                    .map(immerNochZiemlichKuehl ->
                                            new ZweiAdjPhrOhneLeerstellen(
                                                    etwasWaermer,
                                                    true,
                                                    "aber",
                                                    immerNochZiemlichKuehl
                                            )))
                            .collect(toSet()));
        }

        switch (endTemperatur) {
            case KLIRREND_KALT:  // Kann gar nicht sein
                // fall-through
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                alt.add(EISEKALT.mitGraduativerAngabe("nicht mehr ganz so"),
                        BITTERKALT.mitGraduativerAngabe("nicht mehr ganz so"));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(new ZweiAdjPhrOhneLeerstellen(
                        KALT,
                        true,
                        "wenn auch",
                        FROSTIG.mitGraduativerAngabe("nicht länger")));
                break;
            case KUEHL:
                alt.add(KALT.mitGraduativerAngabe("längst nicht mehr so"));
                break;
            case WARM:
                alt.add(AdjektivOhneErgaenzungen.WARM
                                .mitAdvAngabe(new AdvAngabeSkopusSatz(UNERWARTET)),
                        AdjektivOhneErgaenzungen.WARM
                                .mitAdvAngabe(new AdvAngabeSkopusSatz(ANGENEHM)));
                break;
            case RECHT_HEISS:
                alt.add(HEISS.mitAdvAngabe(new AdvAngabeSkopusSatz(
                        UNANGENEHM.mitGraduativerAngabe("allmählich"))));
                break;
            case SEHR_HEISS:
                alt.add(HEISS.mitAdvAngabe(new AdvAngabeSkopusSatz(
                        BRUELLEND.mitGraduativerAngabe("nun"))));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + endTemperatur);
        }

        return alt.build();
    }

    /**
     * Gibt alternative Adjektivphrasen zurück, die beschreiben, wie die Temperatur gestiegen ist.
     *
     * @param endTemperatur Die lokale Temperatur nach der Änderung
     */
    ImmutableCollection<AdjPhrOhneLeerstellen> altAdjPhrTemperaturabfall(
            final Temperatur endTemperatur,
            final boolean fuerAttributiveVerwendung) {
        final ImmutableSet.Builder<AdjPhrOhneLeerstellen> alt = ImmutableSet.builder();

        if (endTemperatur.compareTo(Temperatur.WARM) >= 0) {
            // "etwas kühler, aber immer noch ziemlich warm"
            alt.addAll(
                    altAdjPhrEtwasKuehler().stream()
                            .flatMap(etwasKuehler -> altAdjPhr(
                                    endTemperatur, fuerAttributiveVerwendung).stream()
                                    .map(ziemlichWarm ->
                                            ziemlichWarm.mitAdvAngabe(
                                                    new AdvAngabeSkopusSatz("immer noch")))
                                    .map(immerNochZiemlichWarm ->
                                            new ZweiAdjPhrOhneLeerstellen(
                                                    etwasKuehler,
                                                    true,
                                                    "aber",
                                                    immerNochZiemlichWarm)))
                            .collect(toSet()));
        }

        switch (endTemperatur) {
            case KLIRREND_KALT:
                alt.add(KALT.mitAdvAngabe(
                        new AdvAngabeSkopusSatz(BEISSEND
                                .mitAdvAngabe(new AdvAngabeSkopusSatz(UNERTRAEGLICH)))));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                alt.add(FROSTIG.mitAdvAngabe(new AdvAngabeSkopusSatz("nun")));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(KUEHL.mitAdvAngabe(new AdvAngabeSkopusSatz("empfindlich")));
                break;
            case KUEHL:
                alt.add(KUEHL.mitAdvAngabe(new AdvAngabeSkopusSatz("mittlerweile")));
                break;
            case WARM:
                alt.add(HEISS.mitGraduativerAngabe("längst nicht mehr so"),
                        HEISS.mitGraduativerAngabe("nun nicht mehr so"));
                break;
            case RECHT_HEISS:
                // fall-through
            case SEHR_HEISS: // Kann gar nicht sein
                alt.add(HEISS.mitGraduativerAngabe("nicht mehr ganz so elendig"));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + endTemperatur);
        }

        return alt.build();
    }


    @NonNull
    ImmutableCollection<AdjPhrOhneLeerstellen> altAdjPhrEtwasWaermer() {
        final ImmutableSet.Builder<AdjPhrOhneLeerstellen> alt = ImmutableSet.builder();

        alt.add(WAERMER.mitGraduativerAngabe("etwas"));
        alt.add(WAERMER.mitGraduativerAngabe("wohl"));

        return alt.build();
    }

    @NonNull
    ImmutableCollection<AdjPhrOhneLeerstellen> altAdjPhrEtwasKuehler() {
        final ImmutableSet.Builder<AdjPhrOhneLeerstellen> alt = ImmutableSet.builder();

        alt.add(KUEHLER.mitGraduativerAngabe("etwas"));
        alt.add(KUEHLER.mitGraduativerAngabe("ein wenig"));

        return alt.build();
    }


    /**
     * Gibt alternative Adjektivphrasen zurück für eine Beschreibung in der Art
     * "Es ist (sehr kalt / ziemlich warm)" oder "Heute ist es ..." oder
     * "Draußen ist es ...".
     *
     * @param fuerAttributiveVerwendung ob nur Adjektivphrasen zurückgegeben werden sollen,
     *                                  die für attributive Verwendung geeignet sind
     */
    @NonNull
    @CheckReturnValue
    ImmutableList<AdjPhrOhneLeerstellen> altAdjPhr(
            final Temperatur temperatur, final boolean fuerAttributiveVerwendung) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> alt = ImmutableList.builder();

        switch (temperatur) {
            case KLIRREND_KALT:
                alt.add(KALT.mitGraduativerAngabe("klirrend"), EISKALT, BITTERKALT);
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                if (!fuerAttributiveVerwendung) {
                    // "der sehr kalte Morgen" ist sperrig
                    alt.add(KALT.mitGraduativerAngabe("sehr"));
                }
                alt.add(KALT.mitGraduativerAngabe("frostig"));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.add(KALT);
                break;
            case KUEHL:
                if (!fuerAttributiveVerwendung) {
                    // "der etwas kühle Morgen" ist sperrig
                    alt.add(AdjektivOhneErgaenzungen.KUEHL.mitGraduativerAngabe("etwas"));
                }

                alt.add(AdjektivOhneErgaenzungen.KUEHL,
                        AdjektivOhneErgaenzungen.KUEHL.mitGraduativerAngabe("ziemlich"));
                break;
            case WARM:
                alt.add(AdjektivOhneErgaenzungen.WARM);
                break;
            case RECHT_HEISS:
                if (!fuerAttributiveVerwendung) {
                    // "der recht heiße Morgen" ist sperrig
                    alt.add(AdjektivOhneErgaenzungen.HEISS.mitGraduativerAngabe("recht"));
                }

                alt.add(AdjektivOhneErgaenzungen.HEISS.mitGraduativerAngabe("ziemlich"));
                break;
            case SEHR_HEISS:
                alt.add(AdjektivOhneErgaenzungen.GLUTHEISS);
                if (!fuerAttributiveVerwendung) {
                    alt.add(AdjektivOhneErgaenzungen.HEISS.mitGraduativerAngabe("sehr"));
                }
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }
}
