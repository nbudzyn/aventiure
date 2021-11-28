package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.praedikat.Perfektbildung.HABEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.GEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.KUEHLEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.SETZEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.STEIGEN_AUF;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.TRETEN_AUF;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Negationspartikelphrase;
import de.nb.aventiure2.german.base.PraedRegMerkmale;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt steht (ohne Objekte).
 */
public enum VerbSubj implements VerbOhneLeerstellenSem, SemPraedikatOhneLeerstellen {
    // Verben ohne Partikel
    BEGINNEN("beginnen",
            "beginne", "beginnst", "beginnt", "beginnt",
            HABEN, "begonnen"),
    BLASEN("blasen", "blase", "bläst", "bläst", "blast",
            HABEN, "geblasen"),
    BRAUSEN("brausen", "brause", "braust", "braust", "braust",
            HABEN, "gebraust"),
    BRECHEN("brechen",
            "breche", "brichst", "bricht", "brecht",
            HABEN, "gebrochen"),
    BRENNEN("brennen",
            "brenne", "brennst", "brennt", "brennt",
            HABEN, "gebrannt"),
    FALLEN("fallen", "falle", "fällst", "fällt", "fallt",
            Perfektbildung.SEIN, "gefallen"),
    FRIEREN("frieren",
            "friere", "frierst", "friert", "friert",
            HABEN, "gefroren"),
    FROESTELN("frösteln",
            "fröstele", "fröstelst", "fröstelt", "fröstelt",
            HABEN, "gefröstelt"),
    GEHEN("gehen",
            "gehe", "gehst", "geht", "geht",
            Perfektbildung.SEIN, "gegangen"),
    GLOTZEN("glotzen", "glotze", "glotzt", "glotzt",
            "glotzt", HABEN, "geglotzt"),
    KLETTERN("klettern",
            "klettere", "kletterst", "klettert", "klettert",
            Perfektbildung.SEIN, "geklettert"),
    KOMMEN("kommen",
            "komme", "kommst", "kommt", "kommt",
            Perfektbildung.SEIN, "gekommen"),
    LANDEN("landen", "lande", "landest", "landet", "landet",
            Perfektbildung.SEIN, "gelandet"),
    LEUCHTEN("leuchten",
            "leuchte", "leuchtest", "leuchtet", "leuchtet",
            HABEN, "geleuchtet"),
    LIEGEN("liegen",
            "liege", "liegst", "liegt", "liegt",
            HABEN, "gelegen"),
    KLAPPERN("klappern", "klappere", "klapperst", "klappert", "klappert",
            HABEN, "geklappert"),
    KRIECHEN("kriechen",
            "krieche", "kriechst", "kriecht", "kriecht",
            Perfektbildung.SEIN, "gekrochen"),
    RAUSCHEN("rauschen", "rausche", "rauschst", "rauscht",
            "rauscht",
            HABEN, "gerauscht"),
    ROLLEN("rollen", "rolle", "rollst", "rollt", "rollt",
            Perfektbildung.SEIN, "gerollt"),
    SAUSEN("sausen", "sause", "saust", "saust", "saust",
            HABEN, "gesaust"),
    SCHEINEN("scheinen",
            "scheine", "scheinst", "scheint", "scheint",
            HABEN, "geschienen"),
    SCHLAGEN("schlagen", "schlage", "schlägst", "schlägt",
            "schlagt", HABEN,
            "geschlagen"),
    SINKEN("sinken",
            "sinke", "sinkst", "sinkt", "sinkt",
            Perfektbildung.SEIN, "gesunken"),
    STARREN("starren", "starre", "starrst", "starrt",
            "starrt", HABEN, "gestarrt"),
    STECHEN("stechen",
            "steche", "stichst", "sticht", "stecht",
            HABEN, "gestochen"),
    STEHEN("stehen",
            "stehe", "stehst", "steht", "steht",
            HABEN, "gestanden"),
    STRAHLEN("strahlen",
            "strahle", "strahlst", "strahlt", "strahlt",
            HABEN, "gestrahlt"),
    STUERMEN("stürmen", "stürme", "stürmst", "stürmt", "stürmt",
            HABEN, "gestürmt"),
    TROCKNEN("trocknen", "trockne", "trocknest", "trocknet",
            "trocknet", Perfektbildung.SEIN, "getrocknet"),
    VERGEHEN("vergehen", "vergehe", "vergehst", "vergeht", "vergeht",
            Perfektbildung.SEIN, "vergangen"),
    VERSCHWINDEN("verschwinden", "verschwinde", "verschwindest",
            "verschwindet", "verschwindet",
            Perfektbildung.SEIN, "verschwunden"),
    WACHEN("wachen",
            "wache", "wachst", "wacht", "wacht",
            HABEN, "gewacht"),
    WEHEN("wehen", "wehe", "wehst", "weht", "weht",
            HABEN, "geweht"),
    ZIEHEN("ziehen",
            "ziehe", "ziehst", "zieht", "zieht",
            Perfektbildung.SEIN, "gezogen"),
    ZIRPEN("zirpen",
            "zirpe", "zirpst", "zirpt", "zirpt",
            HABEN, "gezirpt"),

    // Partikelverben
    ABFLAUEN("abflauen", "flaue", "flaust", "flaut", "flaut",
            "ab", HABEN, "abgeflaut"),
    ABKUEHLEN(KUEHLEN, "ab", HABEN),
    ANBRECHEN(BRECHEN, "an", Perfektbildung.SEIN),
    ANGEBEN(GEBEN, "an", HABEN),
    ANKOMMEN(KOMMEN, "an", Perfektbildung.SEIN),
    AUFGEHEN(GEHEN, "auf", Perfektbildung.SEIN),
    AUFSTEHEN(STEHEN, "auf", Perfektbildung.SEIN),
    AUFSTEIGEN(STEIGEN_AUF, "auf", Perfektbildung.SEIN),
    AUFREISSEN("aufreißen",
            "reiße", "reißt", "reißt", "reißt",
            "auf", Perfektbildung.SEIN, "aufgerissen"),
    AUFWACHEN(WACHEN, "auf", Perfektbildung.SEIN),
    AUFZIEHEN(ZIEHEN, "auf", Perfektbildung.SEIN),
    EINBRECHEN(BRECHEN, "ein", Perfektbildung.SEIN),
    EINSETZEN(SETZEN, "ein", HABEN),
    EINTRETEN(TRETEN_AUF, "ein", Perfektbildung.SEIN),
    EMPORSTEIGEN(STEIGEN_AUF, "empor", Perfektbildung.SEIN),
    HERABSCHEINEN(SCHEINEN, "herab", HABEN),
    HERANKOMMEN(KOMMEN, "heran", Perfektbildung.SEIN),
    HERAUFDRINGEN("heraufdringen",
            "dringe", "dringst", "dringt", "dringt",
            "herauf", Perfektbildung.SEIN, "heraufgedrungen"),
    HERUNTERSCHEINEN(SCHEINEN, "herunter", HABEN),
    HEREINKOMMEN(KOMMEN, "herein", Perfektbildung.SEIN),
    HERVORBRECHEN(BRECHEN, "hervor", Perfektbildung.SEIN),
    HERVORLUGEN("hervorlugen",
            "luge", "lugst", "lugt", "lugt",
            "hervor", HABEN, "hervorgelugt"),
    HINABKLETTERN(KLETTERN, "hinab", Perfektbildung.SEIN),
    HINABSTEIGEN(STEIGEN_AUF, "hinab", Perfektbildung.SEIN),
    HINSTARREN(STARREN, "hin", HABEN),
    NACHLASSEN("nachlassen", "lasse", "lässt", "lässt", "lasst",
            "nach", HABEN, "nachgelassen"),
    UNTERGEHEN(GEHEN, "unter", Perfektbildung.SEIN),
    VORBEISCHLAGEN(SCHLAGEN, "vorbei", HABEN),
    ZUNEHMEN(NEHMEN, "zu", HABEN);

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubj(@NonNull final String infinitiv,
             @NonNull final String ichForm,
             @NonNull final String duForm,
             @NonNull final String erSieEsForm,
             @NonNull final String ihrForm,
             final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, perfektbildung,
                partizipII));
    }

    VerbSubj(final String infinitiv,
             final String ichForm,
             final String duForm,
             final String erSieEsForm,
             final String ihrForm,
             @Nullable final String partikel,
             final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, partikel, perfektbildung,
                partizipII));
    }

    VerbSubj(final VerbMitValenz verbMitValenz,
             final String partikel,
             final Perfektbildung perfektbildung) {
        this(verbMitValenz.getVerb(), partikel, perfektbildung);
    }

    VerbSubj(final Verb verbOhnePartikel,
             final String partikel,
             final Perfektbildung perfektbildung) {
        this(verbOhnePartikel.mitPartikel(partikel).mitPerfektbildung(perfektbildung));
    }

    VerbSubj(@NonNull final Verb verb) {
        this.verb = verb;
    }

    @Override
    public Konstituentenfolge getVerbzweit(final PraedRegMerkmale praedRegMerkmale) {
        return toPraedikat().getVerbzweit(praedRegMerkmale);
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        return toPraedikat().getVerbzweitMitSubjektImMittelfeld(subjekt);
    }

    @Override
    public Konstituentenfolge getVerbletzt(final PraedRegMerkmale praedRegMerkmale) {
        return toPraedikat().getVerbletzt(praedRegMerkmale);
    }

    @Override
    @CheckReturnValue
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(
            final PraedRegMerkmale praedRegMerkmale) {
        return ImmutableList.of(verb.getPartizipIIPhrase());
    }

    @Override
    public Konstituentenfolge getInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        return Konstituentenfolge.joinToKonstituentenfolge(verb.getInfinitiv());
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge getZuInfinitiv(final PraedRegMerkmale praedRegMerkmale) {
        return new Konstituentenfolge(k(verb.getZuInfinitiv()));
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final PraedRegMerkmale praedRegMerkmale,
                                                           final boolean nachAnschlusswort) {
        return toPraedikat().getSpeziellesVorfeldSehrErwuenscht(praedRegMerkmale,
                nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(
            final PraedRegMerkmale praedRegMerkmale) {
        return toPraedikat().getSpeziellesVorfeldAlsWeitereOption(praedRegMerkmale);
    }

    @Override
    public SemPraedikatSubOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        return toPraedikat().neg(negationspartikelphrase);
    }

    @Override
    public PerfektSemPraedikatOhneLeerstellen perfekt() {
        return toPraedikat().perfekt();
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Bei "weggehen" ist ein Bezug auf den Nachzustand des Aktanten gegeben, bei
        // "gehen" nicht
        return verb.isPartikelverb();
    }

    @Override
    public SemPraedikatSubOhneLeerstellen toPraedikat() {
        return new SemPraedikatSubOhneLeerstellen(verb);
    }

    @Override
    public Konstituentenfolge getNachfeld(final PraedRegMerkmale praedRegMerkmale) {
        return null;
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }

    @Override
    public boolean inDerRegelKeinSubjektAberAlternativExpletivesEsMoeglich() {
        return false;
    }
}
