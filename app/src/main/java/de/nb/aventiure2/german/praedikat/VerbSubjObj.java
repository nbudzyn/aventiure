package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.MIT_DAT;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt und einem (Präpositional-) Objekt steht.
 */
public enum VerbSubjObj implements PraedikatMitEinerObjektleerstelle {
    ABSETZEN("absetzen", AKK, "setzt", "ab"),
    AUFHEBEN("aufheben", AKK, "hebst", "auf"),
    BEENDEN("beenden", AKK, "beendest"),
    HERAUSKLAUBEN("herausklauben", AKK, "klaubst", "heraus"),
    HINLEGEN("hinlegen", AKK, "legst", "hin"),
    IGNORIEREN("ignorieren", AKK, "ignorierst"),
    MITNEHMEN("mitnehmen", AKK, "nimmst", "mit"),
    NEHMEN("nehmen", AKK, "nimmst"),
    POLIEREN("polieren", AKK, "polierst"),
    REDEN("reden", MIT_DAT, "redest");

    /**
     * Infinitiv des Verbs ("aufheben")
     */
    @NonNull
    private final String infinitiv;

    /**
     * Der Kasus (z.B. Akkusativ, "die Kugel nehmen") oder Präpositionalkasus
     * (z.B. "mit dem Frosch reden"), mit dem dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    /**
     * 2. Person Singular Präsens Indikativ des Verbs, ggf. ohne abgetrenntes Präfix
     * ("hebst")
     */
    @NonNull
    private final String duForm;

    /**
     * Ggf. das abgetrennte Präfix des Verbs ("auf").
     * <p>
     * Wird das Präfix <i>nicht</i> abgetrennt ("ver"), ist dieses Feld <code>null</code>.
     */
    @Nullable
    private final String abgetrenntesPraefix;

    private VerbSubjObj(@NonNull final String infinitiv,
                        @NonNull final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                        @NonNull final String duForm) {
        this(infinitiv, kasusOderPraepositionalkasus, duForm, null);
    }

    private VerbSubjObj(@NonNull final String infinitiv,
                        @NonNull final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                        @NonNull final String duForm,
                        @Nullable final String abgetrenntesPraefix) {
        this.infinitiv = infinitiv;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
        this.duForm = duForm;
        this.abgetrenntesPraefix = abgetrenntesPraefix;
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mitObj(
            final DeklinierbarePhrase describable) {
        return new PraedikatSubjObjOhneLeerstellen(infinitiv, duForm, abgetrenntesPraefix,
                kasusOderPraepositionalkasus, describable);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @NonNull
    String getInfinitiv() {
        return infinitiv;
    }

    @NonNull
    KasusOderPraepositionalkasus getKasusOderPraepositionalkasus() {
        return kasusOderPraepositionalkasus;
    }
}
