package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Ein Verb (ggf. mit Präfix), das genau mit einem Subjekt steht (ohne Objekte).
 */
public enum VerbSubj implements PraedikatOhneLeerstellen {
    ANKOMMEN("ankommen", "kommst", "an",
            Perfektbildung.SEIN, "angekommen"),
    // FIXME Suchen, ersetzen durch Praedikats-DuDesc, Anschluss testen
    AUFWACHEN("aufwachen", "wachst", "auf",
            Perfektbildung.SEIN, "aufgewacht"),
    // FIXME Suchen, ersetzen durch Praedikats-DuDesc, Anschluss testen
    EINTRETEN("eintreten", "trittst", "ein",
            Perfektbildung.SEIN, "eingetreten"),
    // FIXME Suchen, ersetzen durch Praedikats-DuDesc, Anschluss testen
    HEREINKOMMEN("hereinkommen", "kommst", "herein",
            Perfektbildung.SEIN, "hereingekommen"),

    ;

    /**
     * Das Verb an sich, ohne Informationen zur Valenz, ohne Ergänzungen, ohne
     * Angaben
     */
    @NonNull
    private final Verb verb;

    VerbSubj(@NonNull final String infinitiv,
             @NonNull final String duForm,
             final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, perfektbildung, partizipII));
    }

    VerbSubj(@NonNull final String infinitiv,
             @NonNull final String duForm,
             @Nullable final String partikel,
             final Perfektbildung perfektbildung, final String partizipII) {
        this(new Verb(infinitiv, duForm, partikel, perfektbildung, partizipII));
    }

    VerbSubj(@NonNull final Verb verb) {
        this.verb = verb;
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
    public String getDuSatzanschlussOhneSubjekt(final Collection<Modalpartikel> modalpartikeln) {
        return toPraedikatSubj().getDuSatzanschlussOhneSubjekt(modalpartikeln);
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        return true;
    }

    @Override
    public String getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return verb.getPartizipII();
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
    }

    @Override
    public String getInfinitiv(final Person person, final Numerus numerus) {
        return verb.getInfinitiv();
    }

    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus) {
        return verb.getZuInfinitiv();
    }

    @Nullable
    @Override
    public String getSpeziellesVorfeld() {
        return null;
    }

    @Override
    public boolean umfasstSatzglieder() {
        return false;
    }

    @Override
    public String getDuHauptsatzMitVorfeld(final String vorfeld) {
        return toPraedikatSubj().getDuHauptsatzMitVorfeld(vorfeld);
    }

    @Override
    public String getDuHauptsatzMitSpeziellemVorfeld() {
        return toPraedikatSubj().getDuHauptsatzMitSpeziellemVorfeld();
    }

    @Override
    public boolean isPartikelverbMitSeinPerfektOhneAkkusativobjekt() {
        return verb.isPartikelverbMitSeinPerfekt();
    }

    private PraedikatSubOhneLeerstellen toPraedikatSubj() {
        return new PraedikatSubOhneLeerstellen(verb);
    }
}
