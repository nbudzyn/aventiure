package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;


import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen;
import de.nb.aventiure2.german.base.Praepositionalphrase;

import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.BEISSEND;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.FROSTIG;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.NAECHTLICH;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.WARM;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ABENDHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKELHEIT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.EISESKAELTE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.HITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KAELTE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KUEHLE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MITTAGSHITZE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WAERME;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.WETTER;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Temperatur} als {@link Praepositionalphrase}.
 * <p>
 * Diese Phrasen sind für jede Temperatur sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch"})
public class TemperaturPraepPhrDescriber {
    private final TemperaturPraedikativumDescriber praedikativumDescriber;

    public TemperaturPraepPhrDescriber(
            final TemperaturPraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }

    public ImmutableSet<Praepositionalphrase> altInAkk(
            final Temperatur temperatur, final AvTime time) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        // "in die klirrend kalte Luft"
        alt.addAll(mapToList(praedikativumDescriber
                        .altLuftAdjPhr(temperatur, time.getTageszeit()),
                a -> IN_AKK.mit(LUFT.mit(a))));

        if (time.getTageszeit() == NACHTS) {
            alt.addAll(altInAkkNaechtlicheDunkelheit(temperatur));
        }

        alt.addAll(altInTageszeitAkk(temperatur, time));

        switch (temperatur) {
            case KLIRREND_KALT:
                alt.add(IN_AKK.mit(EISESKAELTE),
                        IN_AKK.mit(KAELTE.mit(BEISSEND)));
                break;
            case KNAPP_UNTER_DEM_GEFRIERPUNKT:
                alt.add(IN_AKK.mit(KAELTE.mit(FROSTIG)));
                break;
            case KNAPP_UEBER_DEM_GEFRIERPUNKT:
                alt.addAll(altInAkkKnappUeberGefrierpunkt(time));
                break;
            case KUEHL:
                alt.add(IN_AKK.mit(KUEHLE));
                break;
            case WARM:
                alt.add(IN_AKK.mit(WAERME),
                        IN_AKK.mit(WETTER.mit(WARM)));
                break;
            case RECHT_HEISS:
                break;
            case SEHR_HEISS:
                alt.addAll(altInAkkSehrHeiss(time));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }


    private ImmutableCollection<Praepositionalphrase> altInTageszeitAkk(
            final Temperatur temperatur, final AvTime time) {
        final ImmutableList.Builder<Praepositionalphrase> alt = ImmutableList.builder();

        alt.addAll(mapToSet(praedikativumDescriber.altTageszeit(temperatur, time.getTageszeit()),
                IN_AKK::mit));

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private static ImmutableCollection<Praepositionalphrase> altInAkkKnappUeberGefrierpunkt(
            final AvTime time) {
        final ImmutableSet.Builder<Praepositionalphrase> alt =
                ImmutableSet.builder();

        alt.add(IN_AKK.mit(KAELTE));
        if (time.getTageszeit() == NACHTS) {
            alt.add(IN_AKK.mit(KAELTE.mit(NAECHTLICH)));
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private static ImmutableCollection<Praepositionalphrase> altInAkkSehrHeiss(
            final AvTime time) {
        final ImmutableSet.Builder<Praepositionalphrase> alt =
                ImmutableSet.builder();

        alt.add(IN_AKK.mit(HITZE));
        if (time.gegenMittag()) {
            alt.add(IN_AKK.mit(MITTAGSHITZE));
        } else if (time.getTageszeit() == ABENDS) {
            alt.add(IN_AKK.mit(ABENDHITZE));
        }

        return alt.build();
    }

    @NonNull
    @CheckReturnValue
    private ImmutableCollection<Praepositionalphrase> altInAkkNaechtlicheDunkelheit(
            final Temperatur temperatur) {
        final ImmutableList.Builder<Praepositionalphrase> alt =
                ImmutableList.builder();

        alt.addAll(mapToList(
                praedikativumDescriber.altLuftAdjPhr(temperatur, NACHTS),
                // "in die eiskalte Dunkelheit"
                a -> IN_AKK.mit(DUNKELHEIT.mit(a))));

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
                alt.add(IN_AKK.mit(HITZE.mit(AdjektivOhneErgaenzungen.DUNKEL)));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }

}
