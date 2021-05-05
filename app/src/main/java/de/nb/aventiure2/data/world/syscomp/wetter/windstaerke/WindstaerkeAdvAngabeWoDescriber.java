package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;

import static de.nb.aventiure2.util.StreamUtil.*;

@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class WindstaerkeAdvAngabeWoDescriber {
    private final WindstaerkePraepPhrDescriber praepPhrDescriber;

    public WindstaerkeAdvAngabeWoDescriber(
            final WindstaerkePraepPhrDescriber windstaerkePraepPhrDescriber) {
        praepPhrDescriber = windstaerkePraepPhrDescriber;
    }

    @NonNull
    @CheckReturnValue
    public ImmutableCollection<AdvAngabeSkopusVerbAllg> altWoDraussen(
            final Windstaerke windstaerke, final AvTime time) {
        final ImmutableList.Builder<AdvAngabeSkopusVerbAllg> alt =
                ImmutableList.builder();

        // "im sausenden Wind"
        alt.addAll(mapToList(praepPhrDescriber.altWoDraussen(windstaerke, time),
                AdvAngabeSkopusVerbAllg::new));

        return alt.build();
    }
}
