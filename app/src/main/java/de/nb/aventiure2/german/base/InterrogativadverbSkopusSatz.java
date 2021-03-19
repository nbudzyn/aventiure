package de.nb.aventiure2.german.base;

/**
 * Ein Fragewort, das nach Informationen fragt, die durch
 * ein Adverb ausgedrückt werden ("wann", ...) und sich
 * eher auf den gesamten Satz beziehen.
 * <p>
 * Interrogativadverbien ersetzen also "normale" adverbiale Phrasen in Fragen und
 * indirekten Fragesätzen, z.B.:
 * <ul>
 * <li>Ich erkläre ihm, wann sie das macht.
 * <li>Ich erkläre ihm, wer das wann macht.
 * </ul>
 * <p>
 * Hierher gehören Fragen, Frageworte W, bei denen folgende Paraphrase
 * eines Satzes s, der k enthält, möglich ist (Test):
 * W ist der Fall, dass s?
 */
public enum InterrogativadverbSkopusSatz
        implements IInterrogativadverb, IAdvAngabeOderInterrogativSkopusSatz {
    WANN("wann"), WARUM("warum"),
    WESHALB("weshalb"),
    WESWEGEN("weswegen"),
    WIESO("wieso"), WO("wo"),
    WOBEI("wobei"),
    WODURCH("wodurch"),
    WOFUER("wofür"),
    WORIN("worin"),
    WORUNTER("worunter"),
    WOVOR("wovor"),
    WOHINTER("wohinter"),
    WOZU("wozu");

    private final String string;

    InterrogativadverbSkopusSatz(final String string) {
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
    public boolean enthaeltZuInfinitivOderAngabensatzOderErgaenzungssatz() {
        return false;
    }

    @Override
    public String getString() {
        return string;
    }
}
