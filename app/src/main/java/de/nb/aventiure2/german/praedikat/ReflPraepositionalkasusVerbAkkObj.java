package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.PraepositionMitKasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb wie "... an sich nehmen", das mit einem "reflexiven Präpositionalkonstruktion" steht
 * und eine Leerstelle für ein Akkusativobjekt hat.
 */
public enum ReflPraepositionalkasusVerbAkkObj implements PraedikatMitEinerObjektleerstelle {
    AN_SICH_NEHMEN("nehmen", "nimmst", PraepositionMitKasus.AN_AKK);

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

    private ReflPraepositionalkasusVerbAkkObj(@NonNull final String infinitiv,
                                              @NonNull final String duForm,
                                              @NonNull
                                              final PraepositionMitKasus prapositionMitKasus) {
        verb = new Verb(infinitiv, duForm);
        this.prapositionMitKasus = prapositionMitKasus;
    }

    @Override
    public PraedikatOhneLeerstellen mitObj(final SubstantivischePhrase describable) {
        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                this, describable);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
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