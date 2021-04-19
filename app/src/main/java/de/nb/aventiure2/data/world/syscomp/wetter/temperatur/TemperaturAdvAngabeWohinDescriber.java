package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Temperatur} in Form von
 * {@link de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher}s.
 * <p>
 * Diese Phrasen sind für jede Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch"})
public class TemperaturAdvAngabeWohinDescriber {
    private final TemperaturPraepPhrDescriber praepPhrDescriber;
    private final TemperaturPraedikativumDescriber praedikativumDescriber;

    public TemperaturAdvAngabeWohinDescriber(
            final TemperaturPraedikativumDescriber praedikativumDescriber,
            final TemperaturPraepPhrDescriber praepPhrDescriber) {
        this.praepPhrDescriber = praepPhrDescriber;
        this.praedikativumDescriber = praedikativumDescriber;
    }

    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final Temperatur temperatur, final AvTime time) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        // "in die klirrend kalte Luft", "in die Eiseskälte"
        alt.addAll(mapToList(praepPhrDescriber.altInAkk(temperatur, time),
                AdvAngabeSkopusVerbWohinWoher::new));

        return alt.build();
    }

}
