package de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbWohinWoher;

import static de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.Bewoelkung.LEICHT_BEWOELKT;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Bewoelkung} als
 * {@link AdvAngabeSkopusVerbWohinWoher}.
 * <p>
 * Diese Phrasen sind für jede Temperatur sinnvoll (wobei manchmal die Temperatur
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
public class BewoelkungAdvAngabeWoDescriber {

    private final BewoelkungPraepPhrDescriber praepPhrDescriber;

    public BewoelkungAdvAngabeWoDescriber(
            final BewoelkungPraepPhrDescriber praepPhrDescriber) {
        this.praepPhrDescriber = praepPhrDescriber;
    }

    public ImmutableCollection<AdvAngabeSkopusVerbAllg> altUnterOffenemHimmel(
            final Bewoelkung bewoelkung,
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableSet.Builder<AdvAngabeSkopusVerbAllg> alt = ImmutableSet.builder();

        alt.addAll(altUnterOffenemHimmel(bewoelkung, time.getTageszeit()));

        if (time.kurzNachSonnenaufgang()
                && bewoelkung.compareTo(LEICHT_BEWOELKT) <= 0
                && auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
            alt.add(new AdvAngabeSkopusVerbAllg("in den ersten Sonnenstrahlen"));
        }

        return alt.build();
    }

    private ImmutableCollection<AdvAngabeSkopusVerbAllg> altUnterOffenemHimmel(
            final Bewoelkung bewoelkung,
            final Tageszeit tageszeit) {
        // "unter dem nachtschwarzen Himmel", "in der Morgensonne", "im Sonnenschein"
        return mapToSet(praepPhrDescriber.altUnterOffenemHimmelDat(bewoelkung, tageszeit),
                AdvAngabeSkopusVerbAllg::new);
    }
}
