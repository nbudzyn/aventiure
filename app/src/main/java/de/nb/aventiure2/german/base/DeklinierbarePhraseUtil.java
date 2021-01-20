package de.nb.aventiure2.german.base;

import java.util.Collection;
import java.util.function.BinaryOperator;

import de.nb.aventiure2.german.leseratte.FederkielUtil;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.IndefinitpronomenFlektierer;
import de.nb.federkiel.deutsch.grammatik.wortart.flexion.SubstantivPronomenUtil;
import de.nb.federkiel.deutsch.lexikon.GermanPOS;
import de.nb.federkiel.interfaces.IWordForm;

import static de.nb.aventiure2.german.base.Flexionsreihe.fr;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.NumerusGenus.N;

/**
 * Hilfsmethoden für verschiedene Arten von {@link DeklinierbarePhrase}-Objekten.
 */
public class DeklinierbarePhraseUtil {
    /**
     * "einer" als "unbestimmtes Zahladjektiv","substantiviert" (Duden 446), Singular maskulinum
     */
    private static final Nominalphrase EINER;

    /**
     * "eine" als "unbestimmtes Zahladjektiv", "substantiviert" (Duden 446), Singular femininum
     */
    private static final Nominalphrase EINE;

    /**
     * "eines" als "unbestimmtes Zahladjektiv", "substantiviert" (Duden 446), Singular neutrum
     */
    private static final Nominalphrase EINES;

//    /**
//     * "die einen" (vs. "die anderen") als "unbestimmtes Zahladjektiv",
//     * "substantiviert" (Duden 446), Plural
//     */
//    public static Nominalphrase DIE_EINEN;

    static {
        final IndefinitpronomenFlektierer flekt = new IndefinitpronomenFlektierer();
        final String POS = GermanPOS.PIS.toString();

        final Collection<IWordForm> wortformen = flekt.einerIrgendeiner(
                SubstantivPronomenUtil.createIndefinitpronomen(POS, "einer"),
                POS);

        EINER = Nominalphrase.np(M, null, extractFlexionsreihe(wortformen, M));
        EINE = Nominalphrase.np(F, null, extractFlexionsreihe(wortformen, F));
        EINES = Nominalphrase.np(N, null, extractFlexionsreihe(wortformen, N));

        //        adjStarkPl(lexeme, VorgabeFuerNachfolgendesAdjektiv.NICHT_ERZEUGEN, pos,
        //                stamm,
        //                buildFeatureMap(komparation, STAERKE_STARK,
        //                        valenzFuerImplizitesSubjekt.buildErgaenzungenUndAngabenSlots("3", null,
        //                                // (IHRER selbst gedenkende) Männer /
        //                                // Frauen / Kinder,
        //                                // -> alle Genera möglich!
        //                                PLURAL, StringFeatureLogicUtil.FALSE, true)),
        //                // Die ihrer selbst gedenkenden Männer,
        //                // ABER NICHT die Ihrer selbst gedenkenden Männer!
        //                buildFeatureTypeMap(komparation, STAERKE_STARK));
    }

    private DeklinierbarePhraseUtil() {
    }

    private static Flexionsreihe extractFlexionsreihe(
            final Collection<IWordForm> wortformen, final NumerusGenus numerusGenus) {
        final BinaryOperator<IWordForm> chooseBest = DeklinierbarePhraseUtil::chooseBest;
        return fr(
                FederkielUtil.extractFlextionsreihe(wortformen, numerusGenus, chooseBest));
    }

    private static IWordForm chooseBest(final IWordForm one, final IWordForm other) {
        if (one.getString().equals("eines")) {
            // "Ich möchte auch eines." > "Ich möchte auch eins."
            return one;
        }

        throw new IllegalStateException("Unklar, welche Wortform zu wählen ist: " +
                one + " oder " + other);
    }

    /**
     * Liefert für ein <i>zählbares</i> Bezugsobjekt eine Art "Indefinitanapher" zurück:
     * <ul>
     * <li>das Schwert -> eines
     * <li>der Wein -> einen
     * <li>die Waffen -> welche
     * </ul>
     */
    public static SubstantivischePhrase getIndefinitAnapherZaehlbar(
            final NumerusGenus numerusGenus) {
        switch (numerusGenus) {
            case M:
                return EINER;
            case F:
                return EINE;
            case N:
                return EINES;
            case PL_MFN:
                return Indefinitpronomen.WELCHER3.get(numerusGenus);
            default:
                throw new IllegalArgumentException("Unexpected numerusGenus: " + numerusGenus);
        }
    }
}
