package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
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
    ANBLICKEN("anblicken", AKK, "blicke", "blickst", "blickt",
            "blickt", "an", Perfektbildung.HABEN, "angeblickt"),
    ANSCHAUEN("anschauen", AKK, "schaue", "schaust", "schaut",
            "schaut", "an", Perfektbildung.HABEN, "angeschaut"),
    ANSEHEN("ansehen", AKK, "sehe", "siehst", "sieht",
            "seht", "an", Perfektbildung.HABEN, "angesehen"),
    ANSPRECHEN("ansprechen", AKK, "spreche", "sprichst", "spricht",
            "sprecht", "an", Perfektbildung.HABEN, "angesprochen"),
    ANSTRAHLEN("anstrahlen", AKK, "strahle", "strahlst",
            "strahlt", "strahlt", "an",
            Perfektbildung.HABEN, "angestrahlt"),
    AUFFANGEN("auffangen", AKK, "fange", "fängst", "fängt",
            "fangt", "auf",
            Perfektbildung.HABEN, "aufgefangen"),
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
    HINTERHERSCHAUEN("hinterherschauen", DAT, "schaue", "schaust", "schaut",
            "schaut", "hinterher", Perfektbildung.HABEN, "hinterhergeschaut"),
    HINTERHERSEHEN("hinterhersehen", DAT, "sehe", "siehst", "sieht",
            "seht", "hinterher", Perfektbildung.HABEN, "hinterhergesehen"),
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
    MUSTERN("mustern", AKK, "mustere", "musterst", "mustert",
            "mustert",
            Perfektbildung.HABEN, "gemustert"),
    NACHBLICKEN("nachblicken", DAT, "blicke", "blickst", "blickt",
            "blickt", "nach", Perfektbildung.HABEN, "nachgeblickt"),
    NACHSCHAUEN("nachschauen", DAT, "schaue", "schaust", "schaut",
            "schaut", "nach", Perfektbildung.HABEN, "nachgeschaut"),
    NACHSEHEN("nachsehen", DAT, "sehe", "siehst", "sieht",
            "seht", "nach", Perfektbildung.HABEN, "nachgesehen"),
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
    // Z.B. "jm. vor Augen stehen"
    STEHEN("stehen", DAT, "stehe", "stehst", "steht",
            "steht",
            Perfektbildung.HABEN, "gestanden"),
    WARTEN("warten", AUF_AKK,
            "warte", "wartest", "wartet", "wartet",
            Perfektbildung.HABEN, "gewartet"),
    WERFEN("werfen", AKK,
            "werfe", "wirfst", "wirft", "werft",
            Perfektbildung.HABEN, "geworfen"),
    WIEDERSEHEN("wiedersehen", AKK,
            "sehe", "siehst", "sieht", "seht",
            "wieder",
            Perfektbildung.HABEN, "wiedergesehen"),
    ZUSCHAUEN("zuschauen", DAT, "schaue", "schaust", "schaut",
            "schaut", "zu", Perfektbildung.HABEN, "zugeschaut"),
    ZUSEHEN("zusehen", DAT, "sehe", "siehst", "sieht",
            "seht", "zu", Perfektbildung.HABEN, "zugesehen");

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

    VerbSubjObj(final String infinitiv,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                final String ichForm,
                final String duForm,
                final String erSieEsForm,
                final String ihrForm,
                final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm,
                        perfektbildung, partizipII),
                kasusOderPraepositionalkasus);
    }

    VerbSubjObj(final String infinitiv,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                final String ichForm,
                final String duForm,
                final String erSieEsForm,
                final String ihrForm,
                @Nullable final String partikel,
                final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, partikel, perfektbildung,
                        partizipII),
                kasusOderPraepositionalkasus);
    }

    VerbSubjObj(final Verb verb,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
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
    public PraedikatSubjObjOhneLeerstellen mit(final SubstantivischePhrase substPhr) {
        return new PraedikatSubjObjOhneLeerstellen(verb,
                kasusOderPraepositionalkasus, substPhr);
    }

    public String getDuForm() {
        return verb.getDuFormOhnePartikel();
    }

    public String getPraesensOhnePartikel(final Person person, final Numerus numerus) {
        return verb.getPraesensOhnePartikel(person, numerus);
    }
}
