package de.nb.aventiure2.german.base;

import static java.util.Objects.requireNonNull;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.BEI_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.DURCH;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.FUER;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.HINTER_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.UNTER_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VOR;

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
    WANN("wann"),
    WARUM("warum"),
    WESHALB("weshalb"),
    WESWEGEN("weswegen"),
    WIESO("wieso"),
    WO("wo"),
    WOBEI(BEI_DAT),
    WODURCH(DURCH),
    WOFUER(FUER),
    WORIN(IN_DAT), // "worin"
    WORUNTER(UNTER_AKK),
    WOVOR(VOR),
    WOHINTER(HINTER_DAT),
    WOZU("wozu");

    private final String string;

    InterrogativadverbSkopusSatz(final PraepositionMitKasus praepositionMitKasus) {
        this(requireNonNull(praepositionMitKasus.getPraepositionaladverbWo()));
    }

    InterrogativadverbSkopusSatz(final String string) {
        this.string = string;
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
