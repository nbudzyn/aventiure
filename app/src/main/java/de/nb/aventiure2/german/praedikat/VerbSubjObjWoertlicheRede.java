package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.Contract;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.base.WoertlicheRede;

import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.RUFEN;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Objekt und wörtlicher Rede
 * steht.
 */
public enum VerbSubjObjWoertlicheRede implements VerbMitValenz {
    // Verben ohne Präfix
    ANTWORTEN("antworten", DAT, "antworte", "antwortest",
            "antwortet", "antwortett",
            Perfektbildung.HABEN, "geantwortet"),

    // Präfixverben
    ENTGEGENBLAFFEN("entgegenblaffen", DAT, "blaffe", "blaffst",
            "blafft", "blafft", "entgegen",
            Perfektbildung.HABEN, "entgegengeblafft"),
    ENTGEGENRUFEN(RUFEN, DAT, "entgegen", Perfektbildung.HABEN);

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
                              final Perfektbildung perfektbildung, final String partizipII) {
        this(infinitiv, kasusOderPraepositionalkasus, ichForm, duForm, erSieEsForm,
                ihrForm, null, perfektbildung, partizipII);
    }

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

    VerbSubjObjWoertlicheRede(final VerbMitValenz verbMitValenz,
                              final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                              final String partikel,
                              final Perfektbildung perfektbildung) {
        this(verbMitValenz.getVerb(), kasusOderPraepositionalkasus, partikel, perfektbildung);
    }

    VerbSubjObjWoertlicheRede(final Verb verbOhnePartikel,
                              final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                              final String partikel,
                              final Perfektbildung perfektbildung) {
        this(verbOhnePartikel.mitPartikel(partikel).mitPerfektbildung(perfektbildung),
                kasusOderPraepositionalkasus);
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

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
