package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.german.base.Konstituente.k;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt steht (ohne Objekte).
 */
public enum VerbSubj implements PraedikatOhneLeerstellen {
    ANKOMMEN("ankommen",
            "komme", "kommst", "kommt", "kommt", "an",
            Perfektbildung.SEIN, "angekommen"),
    AUFSTEHEN("aufstehen",
            "stehe", "stehst", "steht", "steht", "auf",
            Perfektbildung.SEIN, "aufgestanden"),
    AUFWACHEN("aufwachen",
            "wache", "wachst", "wacht", "wacht", "auf",
            Perfektbildung.SEIN, "aufgewacht"),
    EINTRETEN("eintreten",
            "trete", "trittst", "tritt", "tretet",
            "ein",
            Perfektbildung.SEIN, "eingetreten"),
    HEREINKOMMEN("hereinkommen",
            "komme", "kommst", "kommt", "kommt",
            "herein",
            Perfektbildung.SEIN, "hereingekommen"),
    HINABSTEIGEN("hinabsteigen",
            "steige", "steigst", "steigt", "steigt",
            "hinab",
            Perfektbildung.SEIN, "hinabgestiegen"),
    STRAHLEN("strahlen",
            "strahle", "strahlst", "strahlt", "strahlt",
            Perfektbildung.HABEN, "gestrahlt");

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

    VerbSubj(@NonNull final String infinitiv,
             @NonNull final String ichForm,
             @NonNull final String duForm,
             @NonNull final String erSieEsForm,
             @NonNull final String ihrForm,
             @Nullable final String partikel,
             final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, ichForm, duForm, erSieEsForm, ihrForm, partikel, perfektbildung,
                partizipII));
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
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        return toPraedikatSubj().mitAdverbialerAngabe(adverbialeAngabe);
    }

    @Override
    public PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return toPraedikatSubj().mitAdverbialerAngabe(adverbialeAngabe);
    }

    @Override
    public PraedikatOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return toPraedikatSubj().mitAdverbialerAngabe(adverbialeAngabe);
    }

    @Override
    public boolean hauptsatzLaesstSichBeiGleichemSubjektMitNachfolgendemVerbzweitsatzZusammenziehen() {
        return true;
    }

    @Override
    public Iterable<Konstituente> getVerbzweit(final Person person, final Numerus numerus) {
        return toPraedikatSubj().getVerbzweit(person, numerus);
    }

    @Override
    public Iterable<Konstituente> getVerbzweitMitSubjektImMittelfeld(
            final SubstantivischePhrase subjekt) {
        return toPraedikatSubj().getVerbzweitMitSubjektImMittelfeld(subjekt);
    }

    @Override
    public Iterable<Konstituente> getVerbletzt(final Person person, final Numerus numerus) {
        return toPraedikatSubj().getVerbletzt(person, numerus);
    }

    @Override
    public Iterable<Konstituente> getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return ImmutableList.of(k(verb.getPartizipII()));
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Override
    public Iterable<Konstituente> getInfinitiv(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(verb.getInfinitiv());
    }

    @Override
    public Iterable<Konstituente> getZuInfinitiv(final Person person, final Numerus numerus) {
        return ImmutableList.of(k(verb.getZuInfinitiv()));
    }

    @Override
    public boolean umfasstSatzglieder() {
        return false;
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldSehrErwuenscht(final Person person,
                                                           final Numerus numerus) {
        return toPraedikatSubj().getSpeziellesVorfeldSehrErwuenscht(person, numerus);
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeldAlsWeitereOption(final Person person,
                                                             final Numerus numerus) {
        return toPraedikatSubj().getSpeziellesVorfeldAlsWeitereOption(person, numerus);
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
    public static Konstituente getSpeziellesVorfeld(final Person person,
                                                    final Numerus numerus) {
        return null;
    }

    @Nullable
    @Override
    public Iterable<Konstituente> getNachfeld(final Person person, final Numerus numerus) {
        return ImmutableList.of();
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        return null;
    }
}
