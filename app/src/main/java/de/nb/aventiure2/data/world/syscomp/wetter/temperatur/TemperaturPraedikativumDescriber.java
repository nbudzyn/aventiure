package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
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
import static de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur.KNAPP_UEBER_DEM_GEFRIERPUNKT;
import static de.nb.aventiure2.data.world.syscomp.wetter.temperatur.Temperatur.WARM;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.AUFGEHEIZT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEISSEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EISIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EISKALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FLIRREND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FROSTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.GLUEHEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LAU;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.NAECHTLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.SCHOEN;
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
     * "Es ist (sehr kalt / ziemlich warm / warmes Wetter)" oder "Heute ist es ..." oder
     * "Draußen ist es ...".
     * <p>
     * Das Eregebnis von {@link #altAdjPhr(Temperatur, boolean)}} ist bereits enthalten
     */
    @NonNull
    @CheckReturnValue
    ImmutableList<Praedikativum> alt(
            final Temperatur temperatur, final boolean draussen) {
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
     * "Es ist (sehr kalt / ziemlich warm)" oder "Heute ist es ..." oder
     * "Draußen ist es ...".
     *
     * @param fuerAttributiveVerwendung ob nur Adjektivphrasen zurückgegeben werden sollen,
     *                                  die für attributive Verwendung geeignet sind
     */
    @NonNull
    @CheckReturnValue
    private ImmutableList<AdjPhrOhneLeerstellen> altAdjPhr(
            final Temperatur temperatur, final boolean fuerAttributiveVerwendung) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> alt = ImmutableList.builder();

        switch (temperatur) {
            case KLIRREND_KALT:
                alt.add(KALT.mitGraduativerAngabe("klirrend"), EISKALT);
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
