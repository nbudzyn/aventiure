package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import com.google.common.collect.ImmutableCollection;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Tageszeit} als {@link AdvAngabeSkopusVerbWohinWoher}.
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TageszeitAdvAngabeWohinDescriber {
    private final TageszeitPraedikativumDescriber praedikativumDescriber;

    public TageszeitAdvAngabeWohinDescriber(
            final TageszeitPraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }

    /**
     * Gibt Alternativen zurück wie "in den Tag"
     */
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final AvTime time) {
        // "in die beginnende Nacht"
        return mapToSet(praedikativumDescriber.alt(time),
                substPhr -> new AdvAngabeSkopusVerbWohinWoher(IN_AKK.mit(substPhr)));
    }
}
