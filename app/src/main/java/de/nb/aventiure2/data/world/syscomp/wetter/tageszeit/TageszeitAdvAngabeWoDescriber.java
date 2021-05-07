package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import com.google.common.collect.ImmutableCollection;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Tageszeit} als {@link AdvAngabeSkopusVerbWohinWoher}.
 */
public class TageszeitAdvAngabeWoDescriber {
    private final TageszeitPraepPhrDescriber praepPhrDescriber;

    public TageszeitAdvAngabeWoDescriber(
            final TageszeitPraepPhrDescriber praepPhrDescriber) {
        this.praepPhrDescriber = praepPhrDescriber;
    }

    /**
     * Gibt Alternativen zurück wie "in der nächtlichen Dunkelheit" - evtl. leer.
     */
    public ImmutableCollection<AdvAngabeSkopusVerbAllg> altWoDraussen(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        // "im Hellen", "in der Dunkelheit", "im nächtlichen Dunkel"
        return mapToSet(praepPhrDescriber.altWoDraussen(time,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben),
                AdvAngabeSkopusVerbAllg::new);
    }
}
