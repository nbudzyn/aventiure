package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.util.StreamUtil.*;

public class WindstaerkeAdvAngabeWohinDescriber {
    private final WindstaerkePraepPhrDescriber praepPhrDescriber;

    public WindstaerkeAdvAngabeWohinDescriber(
            final WindstaerkePraepPhrDescriber praepPhrDescriber) {
        this.praepPhrDescriber = praepPhrDescriber;
    }

    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final Windstaerke windstaerke, final AvTime time) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbWohinWoher> alt =
                ImmutableList.builder();

        // "in den sausenden Wind"
        alt.addAll(mapToList(praepPhrDescriber.altWohinHinaus(windstaerke, time),
                AdvAngabeSkopusVerbWohinWoher::new));

        return alt.build();
    }
}
