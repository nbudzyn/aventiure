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
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    /**
     * Der Kasus (z.B. Akkusativ, "die Kugel nehmen") oder Präpositionalkasus
     * (z.B. "mit dem Frosch reden"), mit dem dieses Verb steht (den dieses Verb regiert).
     */
    @NonNull
    private final KasusOderPraepositionalkasus kasusOderPraepositionalkasus;

    VerbSubjObj(@NonNull final String infinitiv,
                @NonNull final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                @NonNull final String duForm) {
        this(new Verb(infinitiv, duForm), kasusOderPraepositionalkasus);
    }

    VerbSubjObj(@NonNull final String infinitiv,
                @NonNull final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                @NonNull final String duForm,
                @Nullable final String partikel) {
        this(new Verb(infinitiv, duForm, partikel), kasusOderPraepositionalkasus);
    }

    VerbSubjObj(@NonNull final Verb verb,
                @NonNull final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        this.verb = verb;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mitObj(
            final DeklinierbarePhrase describable) {
        return new PraedikatSubjObjOhneLeerstellen(verb,
                kasusOderPraepositionalkasus, describable);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }
}
