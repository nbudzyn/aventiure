package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.adjektiv.AdjPhrOhneLeerstellen;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.NomenFlexionsspalte;
import de.nb.aventiure2.german.base.Praedikativum;

import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.AUFGEHEIZT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EISIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.EISKALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HEISS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.KALT;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.LAU;
import static de.nb.aventiure2.german.base.Artikel.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.TAG;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;

/**
 * Beschreibt die {@link Temperatur} als {@link Praedikativum}.
 * <p>
 * Diese Phrasen sind für jede Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TemperaturPraedikativumDescriber {

    /**
     * Gibt alternative Prädikative zurück für eine Beschreibung in der Art
     * "Es ist (sehr kalt / ziemlich warm / warmes Wetter)" oder "Heute ist es ..." oder
     * "Draußen ist es ...".
     * <p>
     * Das Eregebnis von {@link #altStatischAdjPhr(Temperatur)} ist bereits enthalten
     */
    @NonNull
    @CheckReturnValue
    public ImmutableList<Praedikativum> altStatisch(
            final Temperatur temperatur, final boolean draussen) {
        final ImmutableList.Builder<Praedikativum> alt = ImmutableList.builder();

        alt.addAll(altStatischAdjPhr(temperatur));

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
    ImmutableList<AdjPhrOhneLeerstellen> altStatischLuftAdjPhr(
            final Temperatur temperatur,
            final Tageszeit tageszeit) {
        final ImmutableList.Builder<AdjPhrOhneLeerstellen> alt =
                ImmutableList.builder();

        alt.addAll(altStatischAdjPhr(temperatur));

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
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
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
    public ImmutableList<AdjPhrOhneLeerstellen> altStatischAdjPhr(
            final Temperatur temperatur) {
        switch (temperatur) {
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
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }
    }

}
