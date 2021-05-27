package de.nb.aventiure2.german.base;

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
    WORUEBER("worüber"),
    WOHIN("wohin"),
    WOHER("woher"),
    WOVON("wovon"),
    WORAUS("woraus");

    private final String string;

    InterrogativadverbWohinWoher(final String string) {
        this.string = string;
    }

    @Override
    public Konstituente getDescription(final Person personSubjekt, final Numerus numerusSubjekt) {
        return IInterrogativadverb.super.getDescription();
    }

    @Override
    public String getString() {
        return string;
    }
}
