package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativSkopusSatz;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativVerbAllg;
import de.nb.aventiure2.german.base.IAdvAngabeOderInterrogativWohinWoher;
import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Konstituentenfolge;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Konstituente.k;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.STEHEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.STEIGEN_AUF;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.TRETEN_AUF;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt steht (ohne Objekte).
 */
public enum VerbSubj implements VerbMitValenz, PraedikatOhneLeerstellen {
    // Verben ohne Partikel
    KOMMEN("kommen",
            "komme", "kommst", "kommt", "kommt",
            Perfektbildung.SEIN, "gekommen"),
    STRAHLEN("strahlen",
            "strahle", "strahlst", "strahlt", "strahlt",
            Perfektbildung.HABEN, "gestrahlt"),
    WACHEN("wachen",
            "wache", "wachst", "wacht", "wacht",
            Perfektbildung.HABEN, "gewacht"),

    // Partikelverben
    ANKOMMEN(KOMMEN, "an", Perfektbildung.SEIN),
    AUFSTEHEN(STEHEN, "auf", Perfektbildung.SEIN),
    AUFWACHEN(WACHEN, "auf", Perfektbildung.SEIN),
    EINTRETEN(TRETEN_AUF, "ein", Perfektbildung.SEIN),
    HEREINKOMMEN(KOMMEN, "herein", Perfektbildung.SEIN),
    HINABSTEIGEN(STEIGEN_AUF, "hinab", Perfektbildung.SEIN);

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
        this(verbOhnePartikel.mitPartikel(partikel, perfektbildung));
    }

    VerbSubj(@NonNull final Verb verb) {
        this.verb = verb;
    }

    @Override
    public PraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return toPraedikatSubj().mitModalpartikeln(modalpartikeln);
    }

    @Override
    public PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativSkopusSatz adverbialeAngabe) {
        return toPraedikatSubj().mitAdverbialerAngabe(adverbialeAngabe);
    }

    @Override
    public PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativVerbAllg adverbialeAngabe) {
        return toPraedikatSubj().mitAdverbialerAngabe(adverbialeAngabe);
    }

    @Override
    public PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final IAdvAngabeOderInterrogativWohinWoher adverbialeAngabe) {
        return toPraedikatSubj().mitAdverbialerAngabe(adverbialeAngabe);
    }

    @Override
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        return true;
    }

    @Override
    public Konstituentenfolge getVerbzweit(final Person person, final Numerus numerus) {
        return toPraedikatSubj().getVerbzweit(person, numerus);
    }

    @Override
    public Konstituentenfolge getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        return toPraedikatSubj().getVerbzweitMitSubjektImMittelfeld(subjekt);
    }

    @Override
    public Konstituentenfolge getVerbletzt(final Person person, final Numerus numerus) {
        return toPraedikatSubj().getVerbletzt(person, numerus);
    }

    @Override
    public Konstituentenfolge getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return new Konstituentenfolge(k(verb.getPartizipII()));
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Override
    public Konstituentenfolge getInfinitiv(final Person person, final Numerus numerus) {
        return Konstituentenfolge.joinToKonstituentenfolge(verb.getInfinitiv());
    }

    @Override
    public Konstituentenfolge getZuInfinitiv(final Person person, final Numerus numerus) {
        return new Konstituentenfolge(k(verb.getZuInfinitiv()));
    }

    @Override
    public boolean umfasstSatzglieder() {
        return false;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person person,
                                                           final Numerus numerus,
                                                           final boolean nachAnschlusswort) {
        return toPraedikatSubj().getSpeziellesVorfeldSehrErwuenscht(person, numerus,
                nachAnschlusswort);
    }

    @Nullable
    @Override
    public Konstituentenfolge getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                                   final Numerus numerus) {
        return toPraedikatSubj().getSpeziellesVorfeldAlsWeitereOption(person, numerus
        );
    }

    @Override
    public boolean bildetPerfektMitSein() {
        return verb.getPerfektbildung() == Perfektbildung.SEIN;
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return false;
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        // Bei "weggehen" ist ein Bezug auf den Nachzustand des Aktanten gegeben, bei
        // "gehen" nicht
        return verb.isPartikelverb();
    }

    private PraedikatSubOhneLeerstellen toPraedikatSubj() {
        return new PraedikatSubOhneLeerstellen(verb);
    }

    @Nullable
    @Override
    public Konstituentenfolge getNachfeld(final Person person, final Numerus numerus) {
        return null;
    }

    @Nullable
    @Override
    public Konstituentenfolge getErstesInterrogativwort() {
        return null;
    }

    @Override
    @NonNull
    public Verb getVerb() {
        return verb;
    }
}
