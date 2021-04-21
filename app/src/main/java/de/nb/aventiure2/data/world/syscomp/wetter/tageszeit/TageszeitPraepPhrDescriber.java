package de.nb.aventiure2.data.world.syscomp.wetter.tageszeit;

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.Tageszeit;
import de.nb.aventiure2.german.base.Praepositionalphrase;

import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Tageszeit} als {@link Praepositionalphrase}.
 * <p>
 * Diese Phrasen sind für jede Tageszeit sinnvoll (wobei manchmal die Temperatur
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
public class TageszeitPraepPhrDescriber {
    private final TageszeitPraedikativumDescriber praedikativumDescriber;

    public TageszeitPraepPhrDescriber(
            final TageszeitPraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }

    /**
     * Gibt Alternativen zurück wie "in den Tag"
     */
    ImmutableSet<Praepositionalphrase> altWohinHinaus(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();
        // "in die beginnende Nacht", "ins Helle"
        return mapToSet(praedikativumDescriber.altSubstPhr(time,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben, true),
                IN_AKK::mit);
    }

    /**
     * Gibt Alternativen zurück wie "im Hellen", "in der Dunkelheit", "im nächtlichen Dunkel" -
     * eventuell leer.
     */
    ImmutableSet<Praepositionalphrase> altWoDraussen(
            final AvTime time,
            final boolean auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben) {
        final ImmutableSet.Builder<Praepositionalphrase> alt = ImmutableSet.builder();
        // "im Hellen", "in der Dunkelheit", "im nächtlichen Dunkel"
        return mapToSet(praedikativumDescriber.altSubstPhr(time,
                auchEinmaligeErlebnisseNachTageszeitenwechselBeschreiben, false),
                IN_DAT::mit);
    }
}