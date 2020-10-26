package de.nb.aventiure2.german.praedikat;

import java.util.Collection;

import javax.annotation.Nullable;

import de.nb.aventiure2.german.base.Numerus;
import de.nb.aventiure2.german.base.Person;

/**
 * Zwei Prädikate mit Subjekt (du) und Objekt ohne Leerstellen, erzeugen einen
 * <i>zusammengezogenen Satz</i>, im dem das Subjekt im zweiten Teil
 * <i>eingespart</i> ist ("Du hebst die Kugel auf und [du] nimmst ein Bad").
 */
class ZweiPraedikateOhneLeerstellen implements PraedikatOhneLeerstellen {
    private final PraedikatOhneLeerstellen ersterSatz;
    private final PraedikatOhneLeerstellen zweiterSatz;

    public ZweiPraedikateOhneLeerstellen(
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
    public String getPartizipIIPhrase(final Person person, final Numerus numerus) {
        return ersterSatz.getPartizipIIPhrase(person, numerus)
                + " und "
                + zweiterSatz.getPartizipIIPhrase(person, numerus);
    }

    @Override
    public boolean kannPartizipIIPhraseAmAnfangOderMittenImSatzVerwendetWerden() {
        return true;
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

    @Nullable
    @Override
    public String getSpeziellesVorfeld() {
        return ersterSatz.getSpeziellesVorfeld();
    }

    @Override
    public boolean umfasstSatzglieder() {
        return ersterSatz.umfasstSatzglieder() || zweiterSatz.umfasstSatzglieder();
    }

    @Override
    public boolean isPartikelverbMitSeinPerfektOhneAkkusativobjekt() {
        return ersterSatz.isPartikelverbMitSeinPerfektOhneAkkusativobjekt() &&
                zweiterSatz.isPartikelverbMitSeinPerfektOhneAkkusativobjekt();
    }
}
