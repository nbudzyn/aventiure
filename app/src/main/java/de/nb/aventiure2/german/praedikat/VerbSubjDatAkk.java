package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.DescribableAsDeklinierbarePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt, einem Dativobjekt und
 * einem Akkusativ-Objekt steht.
 */
public enum VerbSubjDatAkk implements Praedikat {
    // "dem Frosch Angebote machen"
    MACHEN("machen", "machst"),
    VERSPRECHEN("versprechen", "versprichst"),
    ;

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

    private VerbSubjDatAkk(@NonNull final String infinitiv,
                           @NonNull final String duForm) {
        this(infinitiv, duForm, null);
    }

    private VerbSubjDatAkk(@NonNull final String infinitiv,
                           @NonNull final String duForm,
                           @Nullable final String abgetrenntesPraefix) {
        this.infinitiv = infinitiv;
        this.duForm = duForm;
        this.abgetrenntesPraefix = abgetrenntesPraefix;
    }

    /**
     * Gibt einen Satz zurück mit diesem Verb und diesen Objekten zurück.
     * ("Du machst dem Frosch Angebote")
     */
    public String getDescriptionHauptsatz(
            final DescribableAsDeklinierbarePhrase describableDat,
            final DescribableAsDeklinierbarePhrase describableAkk) {
        return mitDat(describableDat).getDescriptionDuHauptsatz(describableAkk);
    }

    /**
     * Gibt eine Infinitivkonstruktion zurück mit diesem Verb und diesen Objekten.
     * ("Dem Frosch Angebote machen")
     */
    public String getDescriptionInfinitiv(final DescribableAsDeklinierbarePhrase describableDat,
                                          final DescribableAsDeklinierbarePhrase describableAkk) {
        return mitDat(describableDat).getDescriptionInfinitiv(describableAkk);
    }

    public PraedikatMitEinerObjektleerstelle mitDat(
            final DescribableAsDeklinierbarePhrase describableDat) {
        return new PraedikatDatMitEinerAkkLeerstelle(infinitiv, duForm, abgetrenntesPraefix,
                describableDat);
    }

    public PraedikatMitEinerObjektleerstelle mitAkk(
            final DescribableAsDeklinierbarePhrase describableAkk) {
        return new PraedikatAkkMitEinerDatLeerstelle(infinitiv, duForm, abgetrenntesPraefix,
                describableAkk);
    }
}
