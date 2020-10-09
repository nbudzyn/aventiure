package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.MIT_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt und einem (Präpositional-) Objekt steht.
 */
public enum VerbSubjObj implements PraedikatMitEinerObjektleerstelle {
    ABSETZEN("absetzen", AKK, "setzt", "ab"),
    AUFHEBEN("aufheben", AKK, "hebst", "auf"),
    BEENDEN("beenden", AKK, "beendest"),
    BEGRUESSEN("begrüßen", AKK, "begrüßt"),
    DISKUTIEREN("diskutieren", MIT_DAT, "diskutierst"),
    HEBEN("heben", AKK, "hebst"),
    HERAUSKLAUBEN("herausklauben", AKK, "klaubst", "heraus"),
    HINAUFRUFEN("hinaufrufen", ZU, "rufst", "rufen"),
    HINLEGEN("hinlegen", AKK, "legst", "hin"),
    HINUNTERLASSEN("hinunterlassen", AKK, "lässt", "hinunter"),
    IGNORIEREN("ignorieren", AKK, "ignorierst"),
    LEGEN("legen", AKK, "legst"),
    MITNEHMEN("mitnehmen", AKK, "nimmst", "mit"),
    NEHMEN("nehmen", AKK, "nimmst"),
    POLIEREN("polieren", AKK, "polierst"),
    REDEN("reden", MIT_DAT, "redest"),
    RUFEN("rufen", AKK, "rufst"),
    SETZEN("setzen", AKK, "setzt");

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

    public PraedikatMitEinerObjLeerstelle mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return new PraedikatMitEinerObjLeerstelle(
                verb, kasusOderPraepositionalkasus, adverbialeAngabe
        );
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mitObj(
            final SubstantivischePhrase describable) {
        return new PraedikatSubjObjOhneLeerstellen(verb,
                kasusOderPraepositionalkasus, describable);
    }
}
