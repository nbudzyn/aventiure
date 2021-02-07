package de.nb.aventiure2.german.base;

/**
 * Ein Fragewort, das nach Informationen fragt, die durch
 * ein Adverb ausgedrückt werden ("wie", ...) und sich
 * eher auf das Verb allein beziehen, und zwar <i>nicht</i>
 * auf Richtung / Ziel und <i>nicht</i> auf die Herkunft.
 * <p>
 * Interrogativadverbien ersetzen also "normale" adverbiale Phrasen in Fragen und
 * indirekten Fragesätzen, z.B.:
 * <ul>
 * <li>Ich erkläre ihm, wie sie das macht.
 * <li>Ich erkläre ihm, wer das wie macht.
 * </ul>
 * <p>
 * Hierher gehören <i>keine</i> Frageworte W, bei denen folgende Paraphrase
 * eines Satzes s, der k enthält, möglich ist (Test):
 * W ist der Fall, dass s?
 */
public enum InterrogativadverbVerbAllg
        implements IInterrogativadverb, IAdvAngabeOderInterrogativVerbAllg {
    WIE("wie"),
    WOMIT("womit"),
    WORAN("woran");

    private final String string;

    InterrogativadverbVerbAllg(final String string) {
        this.string = string;
    }

    @Override
    public Konstituente getDescription(final Person personSubjekt, final Numerus numerusSubjekt) {
        return IInterrogativadverb.super.getDescription(personSubjekt, numerusSubjekt);
    }

    @Override
    public boolean imMittelfeldErlaubt() {
        return true;
    }

    @Override
    public String getString() {
        return string;
    }
}
