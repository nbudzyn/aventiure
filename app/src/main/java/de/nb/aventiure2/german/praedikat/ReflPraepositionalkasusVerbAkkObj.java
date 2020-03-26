package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import de.nb.aventiure2.german.base.DescribableAsDeklinierbarePhrase;
import de.nb.aventiure2.german.base.PraepositionMitKasus;

/**
 * Ein Verb wie "... an sich nehmen", das mit einem "reflexiven Präpositionalkonstruktion" steht
 * und eine Leerstelle für ein Akkusativobjekt hat.
 */
public enum ReflPraepositionalkasusVerbAkkObj implements PraedikatMitEinerObjektleerstelle {
    AN_SICH_NEHMEN("nehmen", "nimmst", PraepositionMitKasus.AN_AKK);

    /**
     * Infinitiv des Verbs ("nehmen")
     */
    @NonNull
    private final String infinitiv;

    /**
     * 2. Person Singular Präsens Indikativ des Verbs
     * ("nimmst")
     */
    @NonNull
    private final String duForm;

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
        this.infinitiv = infinitiv;
        this.duForm = duForm;
        this.prapositionMitKasus = prapositionMitKasus;
    }

    @Override
    public PraedikatOhneLeerstellen mitObj(final DescribableAsDeklinierbarePhrase describable) {
        return new PraedikatSubjReflPraepositionalkasusAkkObjOhneLeerstellen(
                this, describable);
    }

    @NonNull
    public String getInfinitiv() {
        return infinitiv;
    }

    @NonNull
    public String getDuForm() {
        return duForm;
    }

    @NonNull
    public PraepositionMitKasus getPrapositionMitKasus() {
        return prapositionMitKasus;
    }
}