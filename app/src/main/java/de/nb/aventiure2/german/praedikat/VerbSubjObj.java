package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUF_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.MIT_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VON;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt und einem (Präpositional-) Objekt steht.
 */
public enum VerbSubjObj implements PraedikatMitEinerObjektleerstelle {
    ABSETZEN("absetzen", AKK, "setzt", "ab",
            Perfektbildung.HABEN, "abgesetzt"),
    AUFHEBEN("aufheben", AKK, "hebst", "auf",
            Perfektbildung.HABEN, "aufgehoben"),
    AUFSTEHEN_VON("aufstehen", VON, "stehst", "auf",
            Perfektbildung.SEIN, "aufgestanden"),
    BEENDEN("beenden", AKK, "beendest", Perfektbildung.HABEN, "beendet"),
    BEGRUESSEN("begrüßen", AKK, "begrüßt", Perfektbildung.HABEN, "begrüßt"),
    DISKUTIEREN("diskutieren", MIT_DAT, "diskutierst",
            Perfektbildung.HABEN, "diskutiert"),
    HABEN("haben", AKK, "hast", Perfektbildung.HABEN, "gehabt"),
    HEBEN("heben", AKK, "hebst", Perfektbildung.HABEN, "gehoben"),
    HERAUSKLAUBEN("herausklauben", AKK, "klaubst", "heraus",
            Perfektbildung.HABEN, "herausgeklaubt"),
    HINAUFRUFEN("hinaufrufen", ZU, "rufst", "hinauf",
            Perfektbildung.HABEN, "hinaufgerufen"),
    HINLEGEN("hinlegen", AKK, "legst", "hin",
            Perfektbildung.HABEN, "hingelegt"),
    HINUNTERLASSEN("hinunterlassen", AKK, "lässt", "hinunter",
            Perfektbildung.HABEN, "hinuntergelassen"),
    IGNORIEREN("ignorieren", AKK, "ignorierst",
            Perfektbildung.HABEN, "ignoriert"),
    LEGEN("legen", AKK, "legst", Perfektbildung.HABEN, "gelegt"),
    MITNEHMEN("mitnehmen", AKK, "nimmst", "mit",
            Perfektbildung.HABEN, "mitgenommen"),
    NEHMEN("nehmen", AKK, "nimmst", Perfektbildung.HABEN, "genommen"),
    POLIEREN("polieren", AKK, "polierst", Perfektbildung.HABEN, "poliert"),
    REDEN("reden", MIT_DAT, "redest", Perfektbildung.HABEN, "geredet"),
    RUFEN("rufen", AKK, "rufst", Perfektbildung.HABEN, "gerufen"),
    SEHEN("sehen", AKK, "siehst", Perfektbildung.HABEN, "gesehen"),
    SETZEN("setzen", AKK, "setzt", Perfektbildung.HABEN, "gesetzt"),
    WARTEN("warten", AUF_AKK, "wartest", Perfektbildung.HABEN, "gewartet"),
    WIEDERSEHEN("wiedersehen", AKK, "siehst", "wieder",
            Perfektbildung.HABEN, "wiedergesehen");

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
                @NonNull final String duForm,
                @NonNull final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, perfektbildung, partizipII), kasusOderPraepositionalkasus);
    }

    VerbSubjObj(@NonNull final String infinitiv,
                @NonNull final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                @NonNull final String duForm,
                @Nullable final String partikel,
                @NonNull final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, partikel, perfektbildung, partizipII),
                kasusOderPraepositionalkasus);
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

    public String getDuForm() {
        return verb.getDuForm();
    }
}
