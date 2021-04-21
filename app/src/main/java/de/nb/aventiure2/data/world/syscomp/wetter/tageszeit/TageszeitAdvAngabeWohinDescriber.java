package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import com.google.common.collect.ImmutableCollection;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Tageszeit} als {@link AdvAngabeSkopusVerbWohinWoher}.
 */
@SuppressWarnings({"DuplicateBranchesInSwitch", "MethodMayBeStatic"})
public class TageszeitAdvAngabeWohinDescriber {
    private final TageszeitPraedikativumDescriber praedikativumDescriber;
    private final TageszeitPraepPhrDescriber praepPhrDescriber;

    public TageszeitAdvAngabeWohinDescriber(
            final TageszeitPraedikativumDescriber praedikativumDescriber,
            final TageszeitPraepPhrDescriber praepPhrDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
        this.praepPhrDescriber = praepPhrDescriber;
    }

    /**
     * Gibt Alternativen zurück wie "in den Tag"
     */
    public ImmutableCollection<AdvAngabeSkopusVerbWohinWoher> altWohinHinaus(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        // "in die beginnende Nacht", "ins Helle"
        return mapToSet(praepPhrDescriber.altWohinHinaus(time,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                AdvAngabeSkopusVerbWohinWoher::new);
    }
}
