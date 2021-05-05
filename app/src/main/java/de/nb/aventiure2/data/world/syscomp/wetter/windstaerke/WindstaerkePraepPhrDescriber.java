package de.nb.aventiure2.data.world.syscomp.wetter.windstaerke;

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.german.base.Praepositionalphrase;

import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.util.StreamUtil.*;

public class WindstaerkePraepPhrDescriber {
    private final WindstaerkePraedikativumDescriber praedikativumDescriber;

    public WindstaerkePraepPhrDescriber(
            final WindstaerkePraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }


    ImmutableSet<Praepositionalphrase> altWohinHinaus(
            final Windstaerke windstaerke, final AvTime time) {
        // "in den sausenden Wind"
        return mapToSet(praedikativumDescriber.altDraussenSubstPhr(windstaerke, time), IN_AKK::mit);
    }

    ImmutableSet<Praepositionalphrase> altWoDraussen(
            final Windstaerke windstaerke, final AvTime time) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();

        // "im sausenden Wind"
        alt.addAll(mapToSet(praedikativumDescriber.altDraussenSubstPhr(windstaerke, time),
                IN_DAT::mit));
        if (windstaerke.compareTo(Windstaerke.KRAEFTIGER_WIND) >= 0) {
            // "mitten im tosenden Sturm", "mitten in Wind und Wetter"
            alt.addAll(mapToSet(praedikativumDescriber.altDraussenSubstPhr(windstaerke, time),
                    s -> IN_DAT.mit(s).mitModAdverbOderAdjektiv("mitten")));
        }

        return alt.build();
    }
}
