package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Kasus.AKK;
import static de.nb.aventiure2.german.base.Kasus.DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AN_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.AUF_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.MIT_DAT;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.VON;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.ZU;
import static de.nb.aventiure2.german.praedikat.Perfektbildung.SEIN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.BLASEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.GEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.GLOTZEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.TROCKNEN;
import static de.nb.aventiure2.german.praedikat.VerbSubj.WEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.GEBEN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt und einem (Präpositional-) Objekt steht.
 */
public enum VerbSubjObj implements VerbMitValenz, PraedikatMitEinerObjektleerstelle {
    // Verben ohne Partikel
    ANTWORTEN(VerbSubjObjWoertlicheRede.ANTWORTEN, DAT, Perfektbildung.HABEN),
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
    BRECHEN("brechen", AKK, "breche", "brichst", "bricht",
            "brecht", Perfektbildung.HABEN, "gebrochen"),
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
    FLECHTEN("flechten", AKK, "flechte", "flichst", "flicht",
            "flechtet", Perfektbildung.HABEN, "geflochten"),
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
    KLAPPERN_MIT("klappern", MIT_DAT, "klappere", "klapperst", "klappert",
            "klappert", Perfektbildung.HABEN, "geklappert"),
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
    REAGIEREN("reagieren", AUF_AKK, "reagiere", "reagierst",
            "reagiert", "reagiert", Perfektbildung.HABEN, "reagiert"),
    REDEN("reden", MIT_DAT, "rede", "redest", "redet",
            "redet",
            Perfektbildung.HABEN, "geredet"),
    RUFEN("rufen", AKK, "rufe", "rufst", "ruft", "ruft",
            Perfektbildung.HABEN, "gerufen"),
    RUPFEN("rupfen", AKK, "rupfe", "rupfst", "rupft", "rupft",
            Perfektbildung.HABEN, "gerupft"),
    SAMMELN("sammeln", AKK, "sammle", "sammelst", "sammelt",
            "sammelt", Perfektbildung.HABEN, "gesammelt"),
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
            SEIN, "gestiegen"),
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
            SEIN, "getreten"),
    VERKAUFEN("verkaufen", AKK, "verkaufe", "verkaufst", "verkauft", "verkauft",
            Perfektbildung.HABEN, "verkauft"),
    VERLASSEN("verlassen", AKK, "verlasse", "verlässt", "verlässt",
            "verlasst", Perfektbildung.HABEN, "verlassen"),
    VERLIEREN_AN("verlieren", AN_DAT, "verliere", "verlierst", "verliert", "verliert",
            Perfektbildung.HABEN, "verloren"),
    VERTREIBEN("verteiben", AKK,
            "vertreibe", "vertreibst", "vertreibt", "vertreibt",
            Perfektbildung.HABEN, "vertrieben"),
    WARTEN("warten", AUF_AKK,
            "warte", "wartest", "wartet", "wartet",
            Perfektbildung.HABEN, "gewartet"),
    WENDEN("wenden", AKK, "wende", "wendest", "wendet",
            "wendet", Perfektbildung.HABEN, "gewendet"),
    WERFEN("werfen", AKK,
            "werfe", "wirfst", "wirft", "werft",
            Perfektbildung.HABEN, "geworfen"),
    ZAUSEN("zausen", AKK,
            "zause", "zaust", "zaust", "zaust",
            Perfektbildung.HABEN, "gezaust"),

    // Partikelverben
    ABKUEHLEN(KUEHLEN, AKK, "ab", Perfektbildung.HABEN),
    ABSETZEN(SETZEN, AKK, "ab", Perfektbildung.HABEN),
    ABTROCKNEN(TROCKNEN, AKK, "ab", Perfektbildung.HABEN),
    ANBLICKEN("anblicken", AKK, "blicke", "blickst", "blickt",
            "blickt", "an", Perfektbildung.HABEN, "angeblickt"),
    ANGLOTZEN(GLOTZEN, AKK, "an", Perfektbildung.HABEN),
    ANGUCKEN(GUCKEN, AKK, "an", Perfektbildung.HABEN),
    ANKUENDIGEN(KUENDIGEN, AKK, "an", Perfektbildung.HABEN),
    ANSCHAUEN(SCHAUEN, AKK, "an", Perfektbildung.HABEN),
    ANSEHEN(SEHEN, AKK, "an", Perfektbildung.HABEN),
    ANSPRECHEN(SPRECHEN, AKK, "an", Perfektbildung.HABEN),
    ANSTRAHLEN("anstrahlen", AKK,
            "strahle", "strahlst", "strahlt", "strahlt",
            "an", Perfektbildung.HABEN, "angestrahlt"),
    AUFBAUEN(BAUEN, AKK, "auf", Perfektbildung.HABEN),
    AUFFANGEN(FANGEN, AKK, "auf", Perfektbildung.HABEN),
    AUFHEBEN(HEBEN, AKK, "auf", Perfektbildung.HABEN),
    AUFKLAUBEN("aufklauben", AKK,
            "klaube", "klaubst", "klaubt",
            "klaubt",
            "auf",
            Perfektbildung.HABEN, "aufgeklaubt"),
    AUFSAMMELN(SAMMELN, AKK, "auf", Perfektbildung.HABEN),
    AUFSTEHEN_VON(STEHEN, VON, "auf", SEIN),
    AUSRUPFEN(RUPFEN, AKK, "aus", Perfektbildung.HABEN),
    EINSAMMELN(SAMMELN, AKK, "ein", Perfektbildung.HABEN),
    ENTGEGENBLASEN(BLASEN, DAT, "entgegen", Perfektbildung.HABEN),
    ENTGEGENWEHEN(WEHEN, DAT, "entgegen", Perfektbildung.HABEN),
    FREIGEBEN(GEBEN, AKK, "frei", Perfektbildung.HABEN),
    HERVORHOLEN(HOLEN, AKK, "hervor", Perfektbildung.HABEN),
    HERVORSUCHEN(SUCHEN, AKK, "hervor", Perfektbildung.HABEN),
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
    UEBERGEHEN(GEHEN, IN_AKK, "über", SEIN),
    WIEDERSEHEN(SEHEN, AKK, "wieder", Perfektbildung.HABEN),
    ZUGUCKEN(GUCKEN, DAT, "zu", Perfektbildung.HABEN),
    ZUSAMMENSAMMELN(SAMMELN, AKK, "zusammen", Perfektbildung.HABEN),
    ZUSAMMENSUCHEN(SUCHEN, AKK, "zusammen", Perfektbildung.HABEN),
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

    VerbSubjObj(final VerbMitValenz verbMitValenz,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                final Perfektbildung perfektbildung) {
        this(verbMitValenz.getVerb(), kasusOderPraepositionalkasus, perfektbildung);
    }

    VerbSubjObj(final Verb verbOhnePartikel,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                final String partikel,
                final Perfektbildung perfektbildung) {
        this(verbOhnePartikel.mitPartikel(partikel).mitPerfektbildung(perfektbildung),
                kasusOderPraepositionalkasus);
    }

    VerbSubjObj(final Verb verb,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                final Perfektbildung perfektbildung) {
        this(verb.mitPerfektbildung(perfektbildung), kasusOderPraepositionalkasus);
    }

    VerbSubjObj(final Verb verb,
                final KasusOderPraepositionalkasus kasusOderPraepositionalkasus) {
        this.verb = verb;
        this.kasusOderPraepositionalkasus = kasusOderPraepositionalkasus;
    }

    public PraedikatMitEinerObjLeerstelle mitAdvAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz advAngabe) {
        return new PraedikatMitEinerObjLeerstelle(
                verb, kasusOderPraepositionalkasus, advAngabe, null);
    }

    public PraedikatMitEinerObjLeerstelle mitAdvAngabe(
            @Nullable final AdvAngabeSkopusVerbWohinWoher advAngabe) {
        return new PraedikatMitEinerObjLeerstelle(
                verb, kasusOderPraepositionalkasus, null, advAngabe);
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
