package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.WoertlicheRede;

import static de.nb.aventiure2.german.base.Kasus.DAT;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Objekt und wörtlicher Rede
 * steht.
 */
public enum VerbSubjObjWoertlicheRede implements Praedikat {
    ENTGEGENBLAFFEN("entgegenblaffen", DAT, "blaffe", "blaffst",
            "blafft", "blafft", "entgegen",
            Perfektbildung.HABEN, "entgegengeblafft"),
    ENTGEGENRUFEN("entgegenrufen", DAT, "rufe", "rufst",
            "ruft", "ruft", "entgegen",
            Perfektbildung.HABEN, "entgegengerufen");

    /**
     * Das Verb an sich, ohne Ergänzungen, ohne Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus (z.B. Dativ, "ihr ... entgegenflaffen") oder Präpositionalkasus,
     * mit dem dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    VerbSubjObjWoertlicheRede(final String infinitiv,
                              final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                              final String ichForm,
                              final String duForm,
                              final String erSieEsForm,
                              final String ihrForm,
                              @Nullable final String partikel,
                              final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, partikel, perfektbildung,
                partizipII), kasusOderPraepositionalkasus);
    }

    VerbSubjObjWoertlicheRede(final Verb verb,
                              final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        this.verb = verb;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public PraedikatObjWoertlicheRedeMitEinerWoertlicheRedeLeerstelle mitObjekt(
            final SubstantivischePhrase objekt) {
        return new PraedikatObjWoertlicheRedeMitEinerWoertlicheRedeLeerstelle(verb,
                kasusOderPraepositionalkasus, objekt);
    }

    /**
     * Füllt die Leerstelle mit dieser Woertlichen Rede.
     */
    public PraedikatObjWoertlicheRedeMitEinerObjektLeerstelle mitWoertlicheRede(
            final String woertlicheRede) {
        return mitWoertlicheRede(new WoertlicheRede(woertlicheRede));
    }

    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public PraedikatObjWoertlicheRedeMitEinerObjektLeerstelle mitWoertlicheRede(
            final WoertlicheRede woertlicheRede) {
        return new PraedikatObjWoertlicheRedeMitEinerObjektLeerstelle(verb,
                kasusOderPraepositionalkasus, woertlicheRede);
    }
}
