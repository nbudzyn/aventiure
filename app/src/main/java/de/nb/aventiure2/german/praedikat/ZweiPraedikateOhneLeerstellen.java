package de.nb.aventiure2.german.praedikat;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Konstituente;
import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Zwei Prädikate mit Objekt ohne Leerstellen, erzeugen einen
 * <i>zusammengezogenen Satz</i>, in dem das Subjekt im zweiten Teil
 * <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] nimmst ein Bad").
 */
public class ZweiPraedikateOhneLeerstellen
        implements PraedikatOhneLeerstellen {
    private final PraedikatOhneLeerstellen ersterSatz;
    private final PraedikatOhneLeerstellen zweiterSatz;

    public ZweiPraedikateOhneLeerstellen(
            final PraedikatOhneLeerstellen ersterSatz,
            final PraedikatOhneLeerstellen zweiterSatz) {
        this.ersterSatz = ersterSatz;
        this.zweiterSatz = zweiterSatz;
    }

    @Override
    public Iterable<Konstituente> getDuHauptsatzMitVorfeld(final String vorfeld) {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return Konstituente.joinToKonstituenten(
                    ersterSatz.getDuHauptsatzMitVorfeld(vorfeld),
                    // "Dann hebst du die goldene Kugel auf"
                    "und",
                    zweiterSatz.getDuSatzanschlussOhneSubjekt() // "nimmst ein Bad"
            );
        }

        return Konstituente.joinToKonstituenten(
                ersterSatz.getDuHauptsatzMitVorfeld(vorfeld),
                // "Dann hebst du die goldene Kugel auf"
                ";",
                zweiterSatz.getDuHauptsatz() // "du nimmst ein Bad"
        );
    }

    @Override
    public Iterable<Konstituente> getDuHauptsatzMitSpeziellemVorfeld() {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return Konstituente.joinToKonstituenten(
                    ersterSatz.getDuHauptsatzMitSpeziellemVorfeld(),
                    // "Den Frosch nimmst du in die Hand"
                    "und",
                    zweiterSatz.getDuSatzanschlussOhneSubjekt()); // "nimmst ein Bad"
        }

        return Konstituente.joinToKonstituenten(
                ersterSatz.getDuHauptsatzMitSpeziellemVorfeld(),
                // "Den Frosch nimmst du in die Hand"
                ";",
                zweiterSatz.getDuHauptsatz()); // "du nimmst ein Bad"
    }

    @Override
    public Iterable<Konstituente> getDuSatzanschlussOhneSubjekt() {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return Konstituente.joinToKonstituenten(
                    ersterSatz.getDuSatzanschlussOhneSubjekt(),
                    // "hebst die goldene Kugel auf"
                    "und",
                    zweiterSatz.getDuSatzanschlussOhneSubjekt()); // "nimmst ein Bad"
        }

        return Konstituente.joinToKonstituenten(
                ersterSatz.getDuSatzanschlussOhneSubjekt(),
                // "hebst die goldene Kugel auf"
                ";",
                zweiterSatz.getDuHauptsatz()); // "du nimmst ein Bad"
    }

    @Override
    public PraedikatOhneLeerstellen mitModalpartikeln(
            final Collection<Modalpartikel> modalpartikeln) {
        return new ZweiPraedikateOhneLeerstellen(
                ersterSatz.mitModalpartikeln(modalpartikeln),
                zweiterSatz
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusSatz adverbialeAngabe) {
        return new ZweiPraedikateOhneLeerstellen(
                ersterSatz.mitAdverbialerAngabe(adverbialeAngabe),
                zweiterSatz
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbAllg adverbialeAngabe) {
        return new ZweiPraedikateOhneLeerstellen(
                ersterSatz.mitAdverbialerAngabe(adverbialeAngabe),
                zweiterSatz
        );
    }

    @Override
    public ZweiPraedikateOhneLeerstellen mitAdverbialerAngabe(
            @Nullable final AdverbialeAngabeSkopusVerbWohinWoher adverbialeAngabe) {
        return new ZweiPraedikateOhneLeerstellen(
                ersterSatz.mitAdverbialerAngabe(adverbialeAngabe),
                zweiterSatz
        );
    }

    @Override
    public boolean duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen() {
        // Etwas vermeiden wie "Du hebst die Kugel auf und polierst sie und nimmst eine
        // von den Früchten"
        return false;
    }

    @Override
    public Iterable<Konstituente> getVerbzweit(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                ersterSatz.getVerbzweit(person, numerus),
                "und",
                zweiterSatz.getVerbzweit(person, numerus));
    }

    @Override
    public Iterable<Konstituente> getVerbletzt(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                ersterSatz.getVerbletzt(person, numerus),
                "und",
                zweiterSatz.getVerbletzt(person, numerus));
    }

    @Override
    public Iterable<Konstituente> getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                ersterSatz.getPartizipIIPhrase(person, numerus),
                "und",
                zweiterSatz.getPartizipIIPhrase(person, numerus)
        );
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return ersterSatz.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() &&
                zweiterSatz.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public Iterable<Konstituente> getInfinitiv(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                ersterSatz.getInfinitiv(person, numerus),
                "und",
                zweiterSatz.getInfinitiv(person, numerus));
    }

    @Override
    public Iterable<Konstituente> getZuInfinitiv(final Person person, final Numerus numerus) {
        return Konstituente.joinToKonstituenten(
                ersterSatz.getZuInfinitiv(person, numerus),
                "und",
                zweiterSatz.getZuInfinitiv(person, numerus));
    }

    @Override
    public boolean umfasstSatzglieder() {
        return ersterSatz.umfasstSatzglieder() || zweiterSatz.umfasstSatzglieder();
    }

    @Override
    public boolean bildetPerfektMitSein() {
        return ersterSatz.bildetPerfektMitSein() && zweiterSatz.bildetPerfektMitSein();
    }

    @Override
    public boolean hatAkkusativobjekt() {
        return ersterSatz.hatAkkusativobjekt() || zweiterSatz.hatAkkusativobjekt();
    }

    @Override
    public boolean isBezugAufNachzustandDesAktantenGegeben() {
        return ersterSatz.isBezugAufNachzustandDesAktantenGegeben() &&
                zweiterSatz.isBezugAufNachzustandDesAktantenGegeben();
    }

    @Nullable
    @Override
    public Konstituente getSpeziellesVorfeld(final Person person,
                                             final Numerus numerus) {
        return ersterSatz.getSpeziellesVorfeld(person, numerus);
    }

    @Nullable
    @Override
    public Iterable<Konstituente> getNachfeld(final Person person, final Numerus numerus) {
        return zweiterSatz.getNachfeld(person, numerus);
    }

    @Nullable
    @Override
    public Konstituente getErstesInterrogativpronomen() {
        // Das hier ist etwas tricky.
        // Denkbar wäre so etwas wie "Sie ist gespannt, was du aufhebst und mitnimmmst."
        // Dazu müsste sowohl im aufheben- als auch im mitnehmen-Prädikat dasselbe
        // Interrogativpronomen angegeben sein.
        final Konstituente erstesInterrogativpronomenErsterSatz =
                ersterSatz.getErstesInterrogativpronomen();
        final Konstituente erstesInterrogativpronomenZweiterSatz =
                zweiterSatz.getErstesInterrogativpronomen();

        if (Objects.equals(
                erstesInterrogativpronomenErsterSatz, erstesInterrogativpronomenZweiterSatz)) {
            return erstesInterrogativpronomenErsterSatz;
        }

        // Verhindern müssen wir so etwas wie *"Sie ist gespannt, was du aufhebst und die Kugel
        // mitnimmmst." - In dem Fall wäre nur eine indirekte ob-Frage gültig:
        // "Sie ist gespannt, ob du was aufhebst und die Kugel mitnimmst."

        return null;
    }
}
