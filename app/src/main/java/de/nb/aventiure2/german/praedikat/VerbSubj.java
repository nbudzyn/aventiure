package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.praedikat.VerbSubjDatAkk.GEBEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.STEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.STEIGEN_AUF;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.TRETEN_AUF;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt steht (ohne Objekte).
 */
public enum VerbSubj implements VerbOhneLeerstellen, PraedikatOhneLeerstellen {
    // Verben ohne Partikel
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
    LIEGEN("liegen",
            "liege", "liegst", "liegt", "liegt",
            Perfektbildung.HABEN, "gelegen"),
    KRIECHEN("kriechen",
            "krieche", "kriechst", "kriecht", "kriecht",
            Perfektbildung.SEIN, "gekrochen"),
    SCHEINEN("scheinen",
            "scheine", "scheinst", "scheint", "scheint",
            Perfektbildung.HABEN, "geschienen"),
    STRAHLEN("strahlen",
            "strahle", "strahlst", "strahlt", "strahlt",
            Perfektbildung.HABEN, "gestrahlt"),
    WACHEN("wachen",
            "wache", "wachst", "wacht", "wacht",
            Perfektbildung.HABEN, "gewacht"),

    // Partikelverben
    ANGEBEN(GEBEN, "an", Perfektbildung.HABEN),
    ANKOMMEN(KOMMEN, "an", Perfektbildung.SEIN),
    AUFSTEHEN(STEHEN, "auf", Perfektbildung.SEIN),
    AUFWACHEN(WACHEN, "auf", Perfektbildung.SEIN),
    EINTRETEN(TRETEN_AUF, "ein", Perfektbildung.SEIN),
    HERABSCHEINEN(SCHEINEN, "herab", Perfektbildung.HABEN),
    HEREINKOMMEN(KOMMEN, "herein", Perfektbildung.SEIN),
    HINABKLETTERN(KLETTERN, "hinab", Perfektbildung.SEIN),
    HINABSTEIGEN(STEIGEN_AUF, "hinab", Perfektbildung.SEIN),
    UNTERGEHEN(GEHEN, "unter", Perfektbildung.SEIN);

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
    public Konstituentenfolge getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return new Konstituentenfolge(k(verb.getPartizipII()));
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
