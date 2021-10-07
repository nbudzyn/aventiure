package de.nb.aventiure2.german.praedikat;

import static de.nb.aventiure2.german.base.Konstituente.k;
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
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt steht (ohne Objekte).
 */
public enum VerbSubj implements VerbOhneLeerstellen, PraedikatOhneLeerstellen {
    // Verben ohne Partikel
    BEGINNEN("beginnen",
            "beginne", "beginnst", "beginnt", "beginnt",
            Perfektbildung.HABEN, "begonnen"),
    BLASEN("blasen", "blase", "bläst", "bläst", "blast",
            Perfektbildung.HABEN, "geblasen"),
    BRAUSEN("brausen", "brause", "braust", "braust", "braust",
            Perfektbildung.HABEN, "gebraust"),
    BRECHEN("brechen",
            "breche", "brichst", "bricht", "brecht",
            Perfektbildung.HABEN, "gebrochen"),
    BRENNEN("brennen",
            "brenne", "brennst", "brennt", "brennt",
            Perfektbildung.HABEN, "gebrannt"),
    FALLEN("fallen", "falle", "fällst", "fällt", "fallt",
            Perfektbildung.SEIN, "gefallen"),
    FRIEREN("frieren",
            "friere", "frierst", "friert", "friert",
            Perfektbildung.HABEN, "gefroren"),
    FROESTELN("frösteln",
            "fröstele", "fröstelst", "fröstelt", "fröstelt",
            Perfektbildung.HABEN, "gefröstelt"),
    GEHEN("gehen",
            "gehe", "gehst", "geht", "geht",
            Perfektbildung.SEIN, "gegangen"),
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
            Perfektbildung.HABEN, "geleuchtet"),
    LIEGEN("liegen",
            "liege", "liegst", "liegt", "liegt",
            Perfektbildung.HABEN, "gelegen"),
    KRIECHEN("kriechen",
            "krieche", "kriechst", "kriecht", "kriecht",
            Perfektbildung.SEIN, "gekrochen"),
    RAUSCHEN("rauschen", "rausche", "rauschst", "rauscht",
            "rauscht",
            Perfektbildung.HABEN, "gerauscht"),
    ROLLEN("rollen", "rolle", "rollst", "rollt", "rollt",
            Perfektbildung.SEIN, "gerollt"),
    SAUSEN("sausen", "sause", "saust", "saust", "saust",
            Perfektbildung.HABEN, "gesaust"),
    SCHEINEN("scheinen",
            "scheine", "scheinst", "scheint", "scheint",
            Perfektbildung.HABEN, "geschienen"),
    SCHLAGEN("schlagen", "schlage", "schlägst", "schlägt",
            "schlagt", Perfektbildung.HABEN,
            "geschlagen"),
    SINKEN("sinken",
            "sinke", "sinkst", "sinkt", "sinkt",
            Perfektbildung.SEIN, "gesunken"),
    STECHEN("stechen",
            "steche", "stichst", "sticht", "stecht",
            Perfektbildung.HABEN, "gestochen"),
    STEHEN("stehen",
            "stehe", "stehst", "steht", "steht",
            Perfektbildung.HABEN, "gestanden"),
    STRAHLEN("strahlen",
            "strahle", "strahlst", "strahlt", "strahlt",
            Perfektbildung.HABEN, "gestrahlt"),
    STUERMEN("stürmen", "stürme", "stürmst", "stürmt", "stürmt",
            Perfektbildung.HABEN, "gestürmt"),
    TROCKNEN("trocknen", "trockne", "trocknest", "trocknet",
            "trocknet", Perfektbildung.SEIN, "getrocknet"),
    VERGEHEN("vergehen", "vergehe", "vergehst", "vergeht", "vergeht",
            Perfektbildung.SEIN, "vergangen"),
    VERSCHWINDEN("verschwinden", "verschwinde", "verschwindest",
            "verschwindet", "verschwindet",
            Perfektbildung.SEIN, "verschwunden"),
    WACHEN("wachen",
            "wache", "wachst", "wacht", "wacht",
            Perfektbildung.HABEN, "gewacht"),
    WEHEN("wehen", "wehe", "wehst", "weht", "weht",
            Perfektbildung.HABEN, "geweht"),
    ZIEHEN("ziehen",
            "ziehe", "ziehst", "zieht", "zieht",
            Perfektbildung.SEIN, "gezogen"),
    ZIRPEN("zirpen",
            "zirpe", "zirpst", "zirpt", "zirpt",
            Perfektbildung.HABEN, "gezirpt"),

    // Partikelverben
    ABFLAUEN("abflauen", "flaue", "flaust", "flaut", "flaut",
            "ab", Perfektbildung.HABEN, "abgeflaut"),
    ABKUEHLEN(KUEHLEN, "ab", Perfektbildung.HABEN),
    ANBRECHEN(BRECHEN, "an", Perfektbildung.SEIN),
    ANGEBEN(GEBEN, "an", Perfektbildung.HABEN),
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
    EINSETZEN(SETZEN, "ein", Perfektbildung.HABEN),
    EINTRETEN(TRETEN_AUF, "ein", Perfektbildung.SEIN),
    EMPORSTEIGEN(STEIGEN_AUF, "empor", Perfektbildung.SEIN),
    HERABSCHEINEN(SCHEINEN, "herab", Perfektbildung.HABEN),
    HERANKOMMEN(KOMMEN, "heran", Perfektbildung.SEIN),
    HERAUFDRINGEN("heraufdringen",
            "dringe", "dringst", "dringt", "dringt",
            "herauf", Perfektbildung.SEIN, "heraufgedrungen"),
    HERUNTERSCHEINEN(SCHEINEN, "herunter", Perfektbildung.HABEN),
    HEREINKOMMEN(KOMMEN, "herein", Perfektbildung.SEIN),
    HERVORBRECHEN(BRECHEN, "hervor", Perfektbildung.SEIN),
    HERVORLUGEN("hervorlugen",
            "luge", "lugst", "lugt", "lugt",
            "hervor", Perfektbildung.HABEN, "hervorgelugt"),
    HINABKLETTERN(KLETTERN, "hinab", Perfektbildung.SEIN),
    HINABSTEIGEN(STEIGEN_AUF, "hinab", Perfektbildung.SEIN),
    NACHLASSEN("nachlassen", "lasse", "lässt", "lässt", "lasst",
            "nach", Perfektbildung.HABEN, "nachgelassen"),
    UNTERGEHEN(GEHEN, "unter", Perfektbildung.SEIN),
    VORBEISCHLAGEN(SCHLAGEN, "vorbei", Perfektbildung.HABEN),
    ZUNEHMEN(NEHMEN, "zu", Perfektbildung.HABEN);

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
    public Konstituentenfolge getVerbzweit(final Person person, final Numerus numerus) {
        return toPraedikat().getVerbzweit(person, numerus);
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        return toPraedikat().getVerbzweitMitSubjektImMittelfeld(subjekt);
    }

    @Override
    public Konstituentenfolge getVerbletzt(final Person person, final Numerus numerus) {
        return toPraedikat().getVerbletzt(person, numerus);
    }

    @Override
    @CheckReturnValue
    public ImmutableList<PartizipIIPhrase> getPartizipIIPhrasen(final Person person,
                                                                final Numerus numerus) {
        return ImmutableList.of(verb.getPartizipIIPhrase());
    }

    @Override
    public Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(verb.getInfinitiv());
    }

    @Override
    @CheckReturnValue
    public Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus) {
        return new Konstituentenfolge(k(verb.getZuInfinitiv()));
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person person,
                                                           final Numerus numerus,
                                                           final boolean nachAnschlusswort) {
        return toPraedikat().getSpeziellesVorfeldSehrErwuenscht(person, numerus,
                nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                                   final Numerus numerus) {
        return toPraedikat().getSpeziellesVorfeldAlsWeitereOption(person, numerus
        );
    }

    @Override
    public PraedikatSubOhneLeerstellen neg(
            @Nullable final Negationspartikelphrase negationspartikelphrase) {
        return toPraedikat().neg(negationspartikelphrase);
    }

    @Override
    public PerfektPraedikatOhneLeerstellen perfekt() {
        return toPraedikat().perfekt();
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Bei "weggehen" ist ein Bezug auf den Nachzustand des Aktanten gegeben, bei
        // "gehen" nicht
        return verb.isPartikelverb();
    }

    @Override
    public PraedikatSubOhneLeerstellen toPraedikat() {
        return new PraedikatSubOhneLeerstellen(verb);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final Person person, final Numerus numerus) {
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
