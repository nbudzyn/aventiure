package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;

/**
 * Ein Prädikat, in dem ein Dativobjekt schon gesetzt ist und genau nur noch für
 * ein Akkusativ-Objekt eine Leerstelle besteht.
 */
class PraedikatDatMitEinerAkkLeerstelle implements PraedikatMitEinerObjektleerstelle {
    /**
     * Infinitiv des Verbs
     */
    @NonNull
    private final String infinitiv;

    /**
     * 2. Person Singular Präsens Indikativ des Verbs, ggf. ohne abgetrenntes Präfix
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
     * Das (Objekt / Wesen / Konzept für das) Dativobjekt
     */
    @NonNull
    private final DeklinierbarePhrase describableDat;

    public PraedikatDatMitEinerAkkLeerstelle(final String infinitiv, final String duForm,
                                             final String abgetrenntesPraefix,
                                             final DeklinierbarePhrase describableDat) {
        this.infinitiv = infinitiv;
        this.duForm = duForm;
        this.abgetrenntesPraefix = abgetrenntesPraefix;
        this.describableDat = describableDat;
    }

    @Override
    public PraedikatOhneLeerstellen mitObj(final DeklinierbarePhrase describable) {
        return mitAkk(describable);
    }

    public PraedikatOhneLeerstellen mitAkk(
            final DeklinierbarePhrase describableAkk) {
        return new PraedikatDatAkkOhneLeerstellen(infinitiv, duForm, abgetrenntesPraefix,
                describableDat, describableAkk);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }
}
