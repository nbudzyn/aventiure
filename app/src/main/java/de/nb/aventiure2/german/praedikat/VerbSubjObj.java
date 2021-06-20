package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUF_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.MIT_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VON;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BLASEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.GEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.WEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.GEBEN;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt und einem (Präpositional-) Objekt steht.
 */
public enum VerbSubjObj implements VerbMitValenz, PraedikatMitEinerObjektleerstelle {
    // Verben ohne Partikel
    BAUEN("bauen", AKK, "baue", "baust", "baut", "baut",
            Perfektbildung.HABEN, "gebaut"),
    BEDECKEN("bedecken", AKK, "bedecke", "bedeckst", "bedeckt",
            "bedeckt", Perfektbildung.HABEN, "bedeckt"),
    BEENDEN("beenden", AKK, "beende", "beendest", "beendet",
            "beendet", Perfektbildung.HABEN, "beendet"),
    BEGRUESSEN("begrüßen", AKK, "begrüße", "begrüßt", "begrüßt",
            "begrüßt", Perfektbildung.HABEN, "begrüßt"),
    BEKOMMEN("bekommen", AKK, "bekomme", "bekommst", "bekommt",
            "bekommt", Perfektbildung.HABEN, "bekommen"),
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
    FINDEN("finden", AKK,
            "finde", "findest", "findet", "findet",
            Perfektbildung.HABEN, "gefunden"),
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
    KUEHLEN("kühlen", AKK, "kühle", "kühlst", "kühlt",
            "kühlt", Perfektbildung.HABEN, "gekühlt"),
    KUENDIGEN("kündigen", AKK, "kündige", "kündigst", "kündigt",
            "kündigt", Perfektbildung.HABEN, "gekündigt"),
    LEGEN("legen", AKK, "lege", "legst", "legt",
            "legt",
            Perfektbildung.HABEN, "gelegt"),
    MUSTERN("mustern", AKK, "mustere", "musterst", "mustert",
            "mustert",
            Perfektbildung.HABEN, "gemustert"),
    NEHMEN("nehmen", AKK, "nehme", "nimmst", "nimmt",
            "nehmt", Perfektbildung.HABEN, "genommen"),
    PFEIFEN("pfeifen", DAT,
            "pfeife", "pfeifst", "pfeift", "pfeift",
            Perfektbildung.HABEN, "gepfiffen"),
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
    SCHEINEN("scheinen", DAT,
            "scheine", "scheinst", "scheint", "scheint",
            Perfektbildung.HABEN, "geschienen"),
    SCHIEBEN("schieben", AKK,
            "schiebe", "schiebst", "schiebt", "schiebt",
            Perfektbildung.HABEN, "geschoben"),
    SETZEN("setzen", AKK, "setze", "setzt", "setzt",
            "setzt", Perfektbildung.HABEN, "gesetzt"),
    SPRECHEN("sprechen", MIT_DAT, "spreche", "sprichst", "spricht",
            "sprecht", Perfektbildung.HABEN, "gesprochen"),
    SPUEREN("spüren", AKK, "spüre", "spürst", "spürt", "spürt",
            Perfektbildung.HABEN, "gespürt"),
    // Z.B. "jm. vor Augen stehen"
    STEHEN("stehen", DAT, "stehe", "stehst", "steht",
            "steht",
            Perfektbildung.HABEN, "gestanden"),
    STEIGEN_AUF("steigen", AUF_AKK,
            "steige", "steigst", "steigt", "steigt",
            Perfektbildung.SEIN, "gestiegen"),
    STREICHEN("streichen", DAT, "streiche",
            "streichst", "streicht", "streicht",
            Perfektbildung.HABEN, "gestrichen"),
    SUCHEN("suchen", AKK, "suche", "suchst", "sucht", "sucht",
            Perfektbildung.HABEN, "gesucht"),
    TREIBEN("treiben", AKK, "treibe", "treibst", "treibt",
            "treibt",
            Perfektbildung.HABEN, "getrieben"),
    TRETEN_AUF("treten", AUF_AKK,
            "trete", "trittst", "tritt", "tretet",
            Perfektbildung.SEIN, "getreten"),
    VERLIEREN_AN("verlieren", AN_DAT, "verliere", "verlierst", "verliert", "verliert",
            Perfektbildung.HABEN, "verloren"),
    VERTREIBEN("verteiben", AKK,
            "vertreibe", "vertreibst", "vertreibt", "vertreibt",
            Perfektbildung.HABEN, "vertrieben"),
    WARTEN("warten", AUF_AKK,
            "warte", "wartest", "wartet", "wartet",
            Perfektbildung.HABEN, "gewartet"),
    WERFEN("werfen", AKK,
            "werfe", "wirfst", "wirft", "werft",
            Perfektbildung.HABEN, "geworfen"),
    ZAUSEN("zausen", AKK,
            "zause", "zaust", "zaust", "zaust",
            Perfektbildung.HABEN, "gezaust"),

    // Partikelverben
    ABKUEHLEN(KUEHLEN, AKK, "ab", Perfektbildung.HABEN),
    ABSETZEN(SETZEN, AKK, "ab", Perfektbildung.HABEN),
    ANBLICKEN("anblicken", AKK, "blicke", "blickst", "blickt",
            "blickt", "an", Perfektbildung.HABEN, "angeblickt"),
    ANGUCKEN(GUCKEN, AKK, "an", Perfektbildung.HABEN),
    ANKUENDIGEN(KUENDIGEN, AKK, "an", Perfektbildung.HABEN),
    ANSCHAUEN(SCHAUEN, AKK, "an", Perfektbildung.HABEN),
    ANSEHEN(SEHEN, AKK, "an", Perfektbildung.HABEN),
    ANSPRECHEN(SPRECHEN, AKK, "an", Perfektbildung.HABEN),
    ANSTRAHLEN("anstrahlen", AKK,
            "strahle", "strahlst", "strahlt", "strahlt",
            "an", Perfektbildung.HABEN, "angestrahlt"),
    AUFFANGEN(FANGEN, AKK, "auf", Perfektbildung.HABEN),
    AUFHEBEN(HEBEN, AKK, "auf", Perfektbildung.HABEN),
    AUFSTEHEN_VON(STEHEN, VON, "auf", Perfektbildung.SEIN),
    ENTGEGENBLASEN(BLASEN, DAT, "entgegen", Perfektbildung.HABEN),
    ENTGEGENWEHEN(WEHEN, DAT, "entgegen", Perfektbildung.HABEN),
    FREIGEBEN(GEBEN, AKK, "frei", Perfektbildung.HABEN),
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
    UEBERGEHEN(GEHEN, IN_AKK, "über", Perfektbildung.SEIN),
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
        this(verbOhnePartikel.mitPartikel(partikel).mitPerfektbildung(perfektbildung),
                kasusOderPraepositionalkasus);
    }

    VerbSubjObj(final Verb verb,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        this.verb = verb;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
    }

    public PraedikatMitEinerObjLeerstelle mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbWohinWoher advAngabe) {
        return new PraedikatMitEinerObjLeerstelle(
                verb, kasusOderPraepositionalkasus, advAngabe
        );
    }

    @Override
    public PraedikatSubjObjOhneLeerstellen mit(final SubstantivischePhrase substPhr) {
        return new PraedikatSubjObjOhneLeerstellen(verb, kasusOderPraepositionalkasus, substPhr);
    }

    @Nullable
    public String getDuForm() {
        return verb.getDuFormOhnePartikel();
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
