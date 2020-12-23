package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
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
    ABSETZEN("absetzen", AKK, "setze", "setzt", "setzt",
            "setzt",
            "ab",
            Perfektbildung.HABEN, "abgesetzt"),
    AUFHEBEN("aufheben", AKK, "hebe", "hebst", "hebt",
            "hebt", "auf",
            Perfektbildung.HABEN, "aufgehoben"),
    AUFSTEHEN_VON("aufstehen", VON, "stehe", "stehst", "steht",
            "steht", "auf",
            Perfektbildung.SEIN, "aufgestanden"),
    BEENDEN("beenden", AKK, "beende", "beendest", "beendet",
            "beendet", Perfektbildung.HABEN, "beendet"),
    BEGRUESSEN("begrüßen", AKK, "begrüße", "begrüßt", "begrüßt",
            "begrüßt", Perfektbildung.HABEN, "begrüßt"),
    BERICHTEN("berichten", AKK,
            "berichte", "berichtest", "berichtet",
            "berichtet",
            Perfektbildung.HABEN, "berichtet"),
    DISKUTIEREN("diskutieren", MIT_DAT,
            "diskutiere", "diskutierst", "diskutiert",
            "diskutiert",
            Perfektbildung.HABEN, "diskutiert"),
    HABEN(HabenUtil.VERB, AKK),
    HEBEN("heben", AKK,
            "hebe", "hebst", "hebt",
            "hebt",
            Perfektbildung.HABEN, "gehoben"),
    HERAUSKLAUBEN("herausklauben", AKK,
            "klaube", "klaubst", "klaubt",
            "klaubt",
            "heraus",
            Perfektbildung.HABEN, "herausgeklaubt"),
    HINAUFRUFEN("hinaufrufen", ZU,
            "rufe", "rufst", "ruft",
            "ruft",
            "hinauf",
            Perfektbildung.HABEN, "hinaufgerufen"),
    HINLEGEN("hinlegen", AKK, "lege", "legst", "legt",
            "legt", "hin",
            Perfektbildung.HABEN, "hingelegt"),
    HINUNTERLASSEN("hinunterlassen", AKK,
            "lasse", "lässt", "lässt",
            "lasst",
            "hinunter",
            Perfektbildung.HABEN, "hinuntergelassen"),
    IGNORIEREN("ignorieren", AKK, "ignoriere", "ignorierst", "ignoriert",
            "ignoriert",
            Perfektbildung.HABEN, "ignoriert"),
    LEGEN("legen", AKK, "lege", "legst", "legt",
            "legt",
            Perfektbildung.HABEN, "gelegt"),
    MITNEHMEN("mitnehmen", AKK, "nehme", "nimmst", "nimmt",
            "nehmt",
            "mit",
            Perfektbildung.HABEN, "mitgenommen"),
    NEHMEN("nehmen", AKK, "nehme", "nimmst", "nimmt",
            "nehmt", Perfektbildung.HABEN, "genommen"),
    POLIEREN("polieren", AKK,
            "poliere", "polierst", "poliert",
            "poliert",
            Perfektbildung.HABEN, "poliert"),
    REDEN("reden", MIT_DAT, "rede", "redest", "redet",
            "redet",
            Perfektbildung.HABEN, "geredet"),
    RUFEN("rufen", AKK, "rufe", "rufst", "ruft", "ruft",
            Perfektbildung.HABEN, "gerufen"),
    SEHEN("sehen", AKK, "sehe", "siehst", "sieht", "seht",
            Perfektbildung.HABEN, "gesehen"),
    SETZEN("setzen", AKK, "setze", "setzt", "setzt",
            "setzt", Perfektbildung.HABEN, "gesetzt"),
    WARTEN("warten", AUF_AKK,
            "warte", "wartest", "wartet", "wartet",
            Perfektbildung.HABEN, "gewartet"),
    WIEDERSEHEN("wiedersehen", AKK,
            "sehe", "siehst", "sieht", "seht",
            "wieder",
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
                @NonNull final String ichForm,
                @NonNull final String duForm,
                @NonNull final String erSieEsForm,
                @NonNull final String ihrForm,
                @NonNull final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm,
                        perfektbildung, partizipII),
                kasusOderPraepositionalkasus);
    }

    VerbSubjObj(@NonNull final String infinitiv,
                @NonNull final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                @NonNull final String ichForm,
                @NonNull final String duForm,
                @NonNull final String erSieEsForm,
                @NonNull final String ihrForm,
                @Nullable final String partikel,
                @NonNull final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, partikel, perfektbildung,
                        partizipII),
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
    public PraedikatSubjObjOhneLeerstellen mit(
            final SubstantivischePhrase describable) {
        return new PraedikatSubjObjOhneLeerstellen(verb,
                kasusOderPraepositionalkasus, describable);
    }

    public String getDuForm() {
        return verb.getDuFormOhnePartikel();
    }

    public String getPraesensOhnePartikel(final Person person, final Numerus numerus) {
        return verb.getPraesensOhnePartikel(person, numerus);
    }
}
