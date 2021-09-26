package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb wie "... an sich nehmen" oder "sich von jdm verabschieden", das
 * <ul>
 *     <li>ein reflexives Objekt oder Präpositionalobjekt hat ("an sich" / "sich")
 *     <li>und dazu noch genau ein weiteres Objekt ("die Kugel [an mich nehmen]" /
 *     "[sich] von der Zauberin [verabschieden]"
 * </ul>
 */
public enum ReflVerbSubjObj implements VerbMitValenz, PraedikatMitEinerObjektleerstelle {
    AN_SICH_NEHMEN("nehmen", PraepositionMitKasus.AN_AKK, AKK,
            "nehme", "nimmst", "nimmt",
            "nehmt",
            Perfektbildung.HABEN, "genommen"),
    SICH_ERLAUBEN("erlauben", DAT, AKK,
            "erlaube", "erlaubst", "erlaubt",
            "erlaubt",
            Perfektbildung.HABEN, "erlaubt"),
    SICH_NEHMEN(VerbSubjObj.NEHMEN, DAT, AKK, Perfektbildung.HABEN),
    SICH_UNTERHALTEN("unterhalten", AKK, PraepositionMitKasus.MIT_DAT,
            "unterhalte", "unterhältst", "unterhält",
            "unterhaltet",
            Perfektbildung.HABEN, "unterhalten"),
    SICH_VERABSCHIEDEN("verabschieden", AKK, PraepositionMitKasus.VON,
            "verabschiede", "verabschiedest", "verabschiedet",
            "verabschiedet",
            Perfektbildung.HABEN, "verabschiedet");

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus mit dem das Verb reflexiv steht - z.B. Akkusativ ("sich von ... verabschieden")
     * oder ein Präpositionalkasus ("... an sich nehmen")
     */
    @NonNull
    private final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus;

    /**
     * Der Kasus ("die Kugel [an sich nehmen]") oder Präpositionalkasus
     * (z.B. "[sich] von der Zauberin [verabschieden]"), mit dem dieses Verb inhaltlich
     * steht (zusätzlich zum reflixiven Kasus)
     */
    @NonNull
    private final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus;

    ReflVerbSubjObj(
            final String infinitiv,
            final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus,
            final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus,
            final String ichForm,
            final String duForm,
            final String erSieEsForm,
            final String ihrForm,
            final Perfektbildung perfektbildung,
            final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm,
                        perfektbildung, partizipII),
                reflKasusOderPraepositionalKasus,
                objektKasusOderPraepositionalkasus);
    }

    ReflVerbSubjObj(final VerbMitValenz verb,
                    final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus,
                    final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus,
                    final Perfektbildung perfektbildung) {
        this(verb.getVerb().mitPerfektbildung(perfektbildung), reflKasusOderPraepositionalKasus,
                objektKasusOderPraepositionalkasus);
    }

    ReflVerbSubjObj(
            final Verb verb,
            final KasusOderPraepositionalkasus reflKasusOderPraepositionalKasus,
            final KasusOderPraepositionalkasus objektKasusOderPraepositionalkasus) {
        this.verb = verb;
        this.reflKasusOderPraepositionalKasus = reflKasusOderPraepositionalKasus;
        this.objektKasusOderPraepositionalkasus = objektKasusOderPraepositionalkasus;
    }

    @Override
    public PraedikatReflSubjObjOhneLeerstellen mit(
            final SubstantivischePhrase substPhr) {
        return new PraedikatReflSubjObjOhneLeerstellen(verb, reflKasusOderPraepositionalKasus,
                objektKasusOderPraepositionalkasus, substPhr);
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}