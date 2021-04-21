package de.nb.aventiure2.data.world.syscomp.wetter.temperatur;


import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.german.base.Praepositionalphrase;

import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.util.StreamUtil.*;

/**
 * Beschreibt die {@link Temperatur} als {@link Praepositionalphrase}.
 * <p>
 * Diese Phrasen sind für jede Temperatur sinnvoll (wobei manchmal die Bewölkung
 * oder andere Wetteraspekte wichtiger sind und man dann diese Sätze
 * vielleicht gar nicht erzeugen wird).
 */
@SuppressWarnings({"DuplicateBranchesInSwitch"})
public class TemperaturPraepPhrDescriber {
    private final TemperaturPraedikativumDescriber praedikativumDescriber;

    public TemperaturPraepPhrDescriber(
            final TemperaturPraedikativumDescriber praedikativumDescriber) {
        this.praedikativumDescriber = praedikativumDescriber;
    }

    public ImmutableSet<Praepositionalphrase> altWohinHinaus(
            final Temperatur temperatur, final AvTime time) {
        // "in die klirrend kalte Luft"
        return mapToSet(praedikativumDescriber.altDraussenSubstPhr(temperatur, time), IN_AKK::mit);
    }

    public ImmutableSet<Praepositionalphrase> altWoDraussen(
            final Temperatur temperatur, final AvTime time) {
        // "in der klirrend kalten Luft"
        return mapToSet(praedikativumDescriber.altDraussenSubstPhr(temperatur, time), IN_DAT::mit);
    }
}
