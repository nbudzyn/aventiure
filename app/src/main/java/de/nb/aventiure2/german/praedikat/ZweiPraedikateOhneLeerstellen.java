package de.nb.aventiure2.german.praedikat;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Zwei Prädikate mit Objekt ohne Leerstellen, erzeugen einen
 * <i>zusammengezogenen Satz</i>, in dem das Subjekt im zweiten Teil
 * <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] nimmst ein Bad").
 */
class ZweiPraedikateOhneLeerstellen
        implements PraedikatOhneLeerstellen {
    private final PraedikatOhneLeerstellen ersterSatz;
    private final PraedikatOhneLeerstellen zweiterSatz;

    ZweiPraedikateOhneLeerstellen(
            final PraedikatOhneLeerstellen ersterSatz,
            final PraedikatOhneLeerstellen zweiterSatz) {
        this.ersterSatz = ersterSatz;
        this.zweiterSatz = zweiterSatz;
    }

    @Override
    public String getDuHauptsatzMitVorfeld(final String vorfeld) {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return ersterSatz.getDuHauptsatzMitVorfeld(vorfeld)
                    // "Dann hebst du die goldene Kugel auf"
                    + " und "
                    + zweiterSatz.getDuSatzanschlussOhneSubjekt(); // "nimmst ein Bad"
        }

        return ersterSatz.getDuHauptsatzMitVorfeld(vorfeld)
                // "Dann hebst du die goldene Kugel auf"
                + "; "
                + zweiterSatz.getDuHauptsatz(); // "du nimmst ein Bad"
    }

    @Override
    public String getDuHauptsatzMitSpeziellemVorfeld() {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return ersterSatz.getDuHauptsatzMitSpeziellemVorfeld()
                    // "Den Frosch nimmst du in die Hand"
                    + " und "
                    + zweiterSatz.getDuSatzanschlussOhneSubjekt(); // "nimmst ein Bad"
        }

        return ersterSatz.getDuHauptsatzMitSpeziellemVorfeld()
                // "Den Frosch nimmst du in die Hand"
                + "; "
                + zweiterSatz.getDuHauptsatz(); // "du nimmst ein Bad"
    }

    @Override
    public String getDuSatzanschlussOhneSubjekt(
            final Collection<Modalpartikel> modalpartikeln) {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return ersterSatz.getDuSatzanschlussOhneSubjekt(modalpartikeln)
                    // "hebst die goldene Kugel auf"
                    + " und "
                    + zweiterSatz
                    .getDuSatzanschlussOhneSubjekt(
                            modalpartikeln); // "nimmst ein Bad"
        }

        return ersterSatz.getDuSatzanschlussOhneSubjekt(modalpartikeln)
                // "hebst die goldene Kugel auf"
                + "; "
                + zweiterSatz.getDuHauptsatz(); // "du nimmst ein Bad"
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
    public String getVerbzweit(final Person person, final Numerus numerus) {
        return ersterSatz.getVerbzweit(person, numerus)
                + " und "
                + zweiterSatz.getVerbzweit(person, numerus);
    }

    @Override
    public String getVerbletzt(final Person person, final Numerus numerus) {
        return ersterSatz.getVerbletzt(person, numerus)
                + " und "
                + zweiterSatz.getVerbletzt(person, numerus);
    }

    @Override
    public String getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return ersterSatz.getPartizipIIPhrase(person, numerus)
                + " und "
                + zweiterSatz.getPartizipIIPhrase(person, numerus);
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return ersterSatz.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() &&
                zweiterSatz.kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden();
    }

    @Override
    public String getInfinitiv(final Person person, final Numerus numerus) {
        return ersterSatz.getInfinitiv(person, numerus)
                + " und "
                + zweiterSatz.getInfinitiv(person, numerus);
    }

    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus) {
        return ersterSatz.getZuInfinitiv(person, numerus)
                + " und "
                + zweiterSatz.getZuInfinitiv(person, numerus);
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
    public String getSpeziellesVorfeld() {
        return ersterSatz.getSpeziellesVorfeld();
    }

    @Nullable
    @Override
    public String getNachfeld(final Person person, final Numerus numerus) {
        return zweiterSatz.getNachfeld(person, numerus);
    }

    @Nullable
    @Override
    public String getErstesInterrogativpronomenAlsString() {
        // Das hier ist etwas tricky.
        // Denkbar wäre so etwas wie "Sie ist gespannt, was du aufhebst und mitnimmmst."
        // Dazu müsste sowohl im aufheben- als auch im mitnehmen-Prädikat dasselbe
        // Interrogativpronomen angegeben sein.
        final String erstesInterrogativpronomenErsterSatz =
                ersterSatz.getErstesInterrogativpronomenAlsString();
        final String erstesInterrogativpronomenZweiterSatz =
                zweiterSatz.getErstesInterrogativpronomenAlsString();

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
