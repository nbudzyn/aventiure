package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.DescribableAsDeklinierbarePhrase;

/**
 * Ein Prädikat, in dem ein Akkusative schon gesetzt ist und genau nur noch für
 * ein Dativ-Objekt eine Leerstelle besteht. Beispiel:
 * "... Angebote machen" (z.B. "dem Frosch Angebote machen)
 */
class PraedikatAkkMitEinerDatLeerstelle implements PraedikatMitEinerObjektleerstelle {
    /**
     * Infinitiv des Verbs ("machen")
     */
    @NonNull
    private final String infinitiv;

    /**
     * 2. Person Singular Präsens Indikativ des Verbs, ggf. ohne abgetrenntes Präfix
     * ("machst")
     */
    @NonNull
    private final String duForm;

    /**
     * Ggf. das abgetrennte Präfix des Verbs.
     * <p>
     * Wird das Präfix <i>nicht</i> abgetrennt ("ver"), ist dieses Feld <code>null</code>.
     */
    @Nullable
    private final String abgetrenntesPraefix;

    /**
     * Das (Objekt / Wesen / Konzept für das) Akkusativobjekt (z.B. "Angebote")
     */
    @NonNull
    private final DescribableAsDeklinierbarePhrase describableAkk;

    public PraedikatAkkMitEinerDatLeerstelle(final String infinitiv, final String duForm,
                                             final String abgetrenntesPraefix,
                                             final DescribableAsDeklinierbarePhrase describableAkk) {
        this.infinitiv = infinitiv;
        this.duForm = duForm;
        this.abgetrenntesPraefix = abgetrenntesPraefix;
        this.describableAkk = describableAkk;
    }

    @Override
    public PraedikatOhneLeerstellen mitObj(final DescribableAsDeklinierbarePhrase describable) {
        return mitDat(describable);
    }

    public PraedikatOhneLeerstellen mitDat(
            final DescribableAsDeklinierbarePhrase describableDat) {
        return new PraedikatDatAkkOhneLeerstellen(infinitiv, duForm, abgetrenntesPraefix,
                describableDat, describableAkk);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }
}
