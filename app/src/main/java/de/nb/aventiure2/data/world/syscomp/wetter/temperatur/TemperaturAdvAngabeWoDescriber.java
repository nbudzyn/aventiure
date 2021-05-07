package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.Temperatur;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Temperatur} in Form von
 * {@link AdvAngabeSkopusVerbWohinWoher}s.
 * <p>
 * Diese Phrasen sind für jede Bewölkung sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
public class TemperaturAdvAngabeWoDescriber {
    private final TemperaturPraepPhrDescriber praepPhrDescriber;

    public TemperaturAdvAngabeWoDescriber(
            final TemperaturPraepPhrDescriber praepPhrDescriber) {
        this.praepPhrDescriber = praepPhrDescriber;
    }

    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AdvAngabeSkopusVerbAllg> altWoDraussen(
            final Temperatur temperatur, final AvTime time) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbAllg> alt =
                ImmutableList.builder();

        // "in der klirrend kalten Luft", "in der Eiseskälte"
        alt.addAll(mapToList(praepPhrDescriber.altWoDraussen(temperatur, time),
                AdvAngabeSkopusVerbAllg::new));

        return alt.build();
    }

}
