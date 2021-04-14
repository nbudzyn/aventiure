package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.data.time.Tageszeit.ABENDS;
import static de.nb.aventiure2.data.time.Tageszeit.NACHTS;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.DUNKELHEIT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.LUFT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Temperatur} in Form von
 * {@link de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher}s.
 * <p>
 * Diese Phrasen sind für jede Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TemperaturAdvAngabeSkopusVerbWohinWoherDescriber {
    private final TemperaturPraedikativumDescriber praedikativumDescriber;

    public TemperaturAdvAngabeSkopusVerbWohinWoherDescriber(
            final TemperaturPraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }

    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final Temperatur temperatur, final AvTime time) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        // "in die klirrend kalte Luft"
        alt.addAll(
                mapToList(praedikativumDescriber
                                .altStatischLuftAdjPhr(temperatur, time.getTageszeit()),
                        a -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(LUFT.mit(a)))));

        switch (temperatur) {
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
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
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
            final Temperatur temperatur, final Tageszeit tageszeit) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        alt.addAll(mapToList(
                praedikativumDescriber.altStatischLuftAdjPhr(temperatur, tageszeit),
                // "in die eiskalte Dunkelheit"
                a -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(DUNKELHEIT.mit(a)))));

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
                alt.add(new AdvAngabeSkopusVerbWohinWoher("in die dunkle Hitze"));
                break;
            default:
                throw new IllegalStateException("Unexpected Temperatur: " + temperatur);
        }

        return alt.build();
    }
}
