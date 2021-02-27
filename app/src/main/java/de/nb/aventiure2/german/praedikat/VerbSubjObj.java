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
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.MIT_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VON;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;
import static de.nb.aventiure2.german.praedikat.VerbSubj.STRAHLEN;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt und einem (Präpositional-) Objekt steht.
 */
public enum VerbSubjObj implements VerbMitValenz, PraedikatMitEinerObjektleerstelle {
    // Verben ohne Partikel
    BEENDEN("beenden", AKK, "beende", "beendest", "beendet",
            "beendet", Perfektbildung.HABEN, "beendet"),
    BEGRUESSEN("begrüßen", AKK, "begrüße", "begrüßt", "begrüßt",
            "begrüßt", Perfektbildung.HABEN, "begrüßt"),
    BIETEN("bieten", DAT,
            "biete", "bietest", "bietet", "bietet",
            Perfektbildung.HABEN, "geboten"),
    BERICHTEN("berichten", AKK,
            "berichte", "berichtest", "berichtet",
            "berichtet",
            Perfektbildung.HABEN, "berichtet"),
    DISKUTIEREN("diskutieren", MIT_DAT,
            "diskutiere", "diskutierst", "diskutiert",
            "diskutiert",
            Perfektbildung.HABEN, "diskutiert"),
    FANGEN("fangen", AKK, "fange", "fängst", "fängt",
            "fangt",
            Perfektbildung.HABEN, "gefangen"),
    GUCKEN("gucken", IN_AKK, "gucke", "guckst", "guckt", "guckt",
            Perfektbildung.HABEN, "geguckt"),
    HABEN(HabenUtil.VERB, AKK),
    HEBEN("heben", AKK,
            "hebe", "hebst", "hebt",
            "hebt", Perfektbildung.HABEN, "gehoben"),
    HELFEN("helfen", DAT, "helfe", "hilfst", "hilft",
            "helft", Perfektbildung.HABEN, "geholfen"),
    HOLEN("holen", AKK,
            "hole", "holst", "holt",
            "holt", Perfektbildung.HABEN, "geholt"),
    IGNORIEREN("ignorieren", AKK, "ignoriere", "ignorierst", "ignoriert",
            "ignoriert",
            Perfektbildung.HABEN, "ignoriert"),
    LEGEN("legen", AKK, "lege", "legst", "legt",
            "legt",
            Perfektbildung.HABEN, "gelegt"),
    MUSTERN("mustern", AKK, "mustere", "musterst", "mustert",
            "mustert",
            Perfektbildung.HABEN, "gemustert"),
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
    SAGEN("sagen", AKK,
            "sage", "sagst", "sagt", "sagt",
            Perfektbildung.HABEN, "gesagt"),
    SEHEN("sehen", AKK, "sehe", "siehst", "sieht", "seht",
            Perfektbildung.HABEN, "gesehen"),
    SCHAUEN("schauen", IN_AKK, "schaue", "schaust", "schaut", "schaut",
            Perfektbildung.HABEN, "geschaut"),
    SETZEN("setzen", AKK, "setze", "setzt", "setzt",
            "setzt", Perfektbildung.HABEN, "gesetzt"),
    SPRECHEN("sprechen", MIT_DAT, "spreche", "sprichst", "spricht",
            "sprecht", Perfektbildung.HABEN, "gesprochen"),
    STEIGEN_AUF("steigen", AUF_AKK,
            "steige", "steigst", "steigt", "steigt",
            Perfektbildung.SEIN, "gestiegen"),
    // Z.B. "jm. vor Augen stehen"
    STEHEN("stehen", DAT, "stehe", "stehst", "steht",
            "steht",
            Perfektbildung.HABEN, "gestanden"),
    TRETEN_AUF("treten", AUF_AKK,
            "trete", "trittst", "tritt", "tretet",
            Perfektbildung.SEIN, "getreten"),
    WARTEN("warten", AUF_AKK,
            "warte", "wartest", "wartet", "wartet",
            Perfektbildung.HABEN, "gewartet"),
    WERFEN("werfen", AKK,
            "werfe", "wirfst", "wirft", "werft",
            Perfektbildung.HABEN, "geworfen"),

    // Partikelverben
    ABSETZEN(SETZEN, AKK, "ab", Perfektbildung.HABEN),
    ANBLICKEN("anblicken", AKK, "blicke", "blickst", "blickt",
            "blickt", "an", Perfektbildung.HABEN, "angeblickt"),
    ANGUCKEN(GUCKEN, AKK, "an", Perfektbildung.HABEN),
    ANSCHAUEN(SCHAUEN, AKK, "an", Perfektbildung.HABEN),
    ANSEHEN(SEHEN, AKK, "an", Perfektbildung.HABEN),
    ANSPRECHEN(SPRECHEN, AKK, "an", Perfektbildung.HABEN),
    ANSTRAHLEN(STRAHLEN, AKK, "an", Perfektbildung.HABEN),
    AUFFANGEN(FANGEN, AKK, "auf", Perfektbildung.HABEN),
    AUFHEBEN(HEBEN, AKK, "auf", Perfektbildung.HABEN),
    AUFSTEHEN_VON(STEHEN, VON, "auf", Perfektbildung.SEIN),
    HERVORHOLEN(HOLEN, AKK, "hervor", Perfektbildung.HABEN),
    HERAUSKLAUBEN("herausklauben", AKK,
            "klaube", "klaubst", "klaubt",
            "klaubt",
            "heraus",
            Perfektbildung.HABEN, "herausgeklaubt"),
    HINAUFRUFEN(RUFEN, ZU, "hinauf", Perfektbildung.HABEN),
    HINLEGEN(LEGEN, AKK, "hin", Perfektbildung.HABEN),
    HINTERHERSCHAUEN(SCHAUEN, DAT, "hinterher", Perfektbildung.HABEN),
    HINTERHERSEHEN(SEHEN, DAT, "hinterher", Perfektbildung.HABEN),
    HINUNTERLASSEN("hinunterlassen", AKK,
            "lasse", "lässt", "lässt",
            "lasst",
            "hinunter",
            Perfektbildung.HABEN, "hinuntergelassen"),
    MITNEHMEN(NEHMEN, AKK, "mit", Perfektbildung.HABEN),
    NACHBLICKEN(ANBLICKEN, DAT, "nach", Perfektbildung.HABEN),
    NACHSCHAUEN(SCHAUEN, DAT, "nach", Perfektbildung.HABEN),
    NACHSEHEN(SEHEN, DAT, "nach", Perfektbildung.HABEN),
    WIEDERSEHEN(SEHEN, AKK, "wieder", Perfektbildung.HABEN),
    ZUGUCKEN(GUCKEN, DAT, "zu", Perfektbildung.HABEN),
    ZUSCHAUEN(SCHAUEN, DAT, "zu", Perfektbildung.HABEN),
    ZUSEHEN(SEHEN, DAT, "zu", Perfektbildung.HABEN);

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

    VerbSubjObj(final VerbMitValenz verbMitValenz,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                final String partikel,
                final Perfektbildung perfektbildung) {
        this(verbMitValenz.getVerb(), kasusOderPraepositionalkasus, partikel, perfektbildung);
    }

    VerbSubjObj(final Verb verbOhnePartikel,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                final String partikel,
                final Perfektbildung perfektbildung) {
        this(verbOhnePartikel.mitPartikel(partikel, perfektbildung), kasusOderPraepositionalkasus);
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

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
