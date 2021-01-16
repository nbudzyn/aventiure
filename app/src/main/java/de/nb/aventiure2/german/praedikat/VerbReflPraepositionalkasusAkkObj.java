package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb wie "... an sich nehmen", das mit einem "reflexiven Präpositionalkonstruktion" steht
 * und eine Leerstelle für ein Akkusativobjekt hat.
 */
public enum VerbReflPraepositionalkasusAkkObj implements PraedikatMitEinerObjektleerstelle {
    AN_SICH_NEHMEN("nehmen",
            "nehme", "nimmst", "nimmt",
            "nehmt",
            PraepositionMitKasus.AN_AKK,
            Perfektbildung.HABEN, "genommen");

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Die Präposition und der Kasus, mit dem sie steht (z.B.
     * "an sich [Akk]")
     */
    @NonNull
    private final PraepositionMitKasus prapositionMitKasus;

    private VerbReflPraepositionalkasusAkkObj(@NonNull final String infinitiv,
                                              @NonNull final String ichForm,
                                              @NonNull final String duForm,
                                              @NonNull final String erSieEsForm,
                                              @NonNull final String ihrForm,
                                              @NonNull
                                              final PraepositionMitKasus prapositionMitKasus,
                                              @NonNull final Perfektbildung perfektbildung,
                                              final String partizipII) {
        verb = new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm,
                perfektbildung, partizipII);
        this.prapositionMitKasus = prapositionMitKasus;
    }

    @Override
    public AbstractAngabenfaehigesPraedikatOhneLeerstellen mit(
            final SubstantivischePhrase substPhr) {
        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                this, substPhr);
    }

    @NonNull
    public Verb getVerb() {
        return verb;
    }

    @NonNull
    public PraepositionMitKasus getPrapositionMitKasus() {
        return prapositionMitKasus;
    }
}