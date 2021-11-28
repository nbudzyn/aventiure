package de.nb.aventiure2.german.base;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUS;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UEBER_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VON;

/**
 * Ein Fragewort, das nach Informationen fragt, die durch
 * ein Adverb ausgedrückt werden ("wohin", ...) und
 * nach der Richtung, dem Ziel oder der Herkunft fragen
 * (oder nach entsprecheden "Adverbialobjekten":
 * <i>Wohin stellst du die Vase? - Ich stelle die Vase auf den Tisch.</i>
 * <p>
 * Interrogativadverbien ersetzen also "normale" adverbiale Phrasen in Fragen und
 * indirekten Fragesätzen, z.B.:
 * <ul>
 * <li>Ich erkläre ihm, wohin sie das legt.
 * <li>Ich erkläre ihm, was sie wohin legt.
 * </ul>
 */
public enum InterrogativadverbWohinWoher
        implements IInterrogativadverb, IAdvAngabeOderInterrogativWohinWoher {
    WORUEBER(UEBER_AKK),
    WOHIN("wohin"),
    WOHER("woher"),
    WOREIN(IN_AKK), // "worein"
    WOVON(VON),
    WORAUS(AUS);

    private final String string;

    InterrogativadverbWohinWoher(final PraepositionMitKasus praepositionMitKasus) {
        this(requireNonNull(praepositionMitKasus.getPraepositionaladverbWo()));
    }

    InterrogativadverbWohinWoher(final String string) {
        this.string = string;
    }
    
    @Override
    public String getString() {
        return string;
    }
}
