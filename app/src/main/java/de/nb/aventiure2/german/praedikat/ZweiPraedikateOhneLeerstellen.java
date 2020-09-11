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
    public String getDuHauptsatzMitKonjunktionaladverb(final String konjunktionaladverb) {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return ersterSatz.getDuHauptsatzMitKonjunktionaladverb(konjunktionaladverb)
                    // "Dann hebst du die goldene Kugel auf"
                    + " und "
                    + zweiterSatz.getDuSatzanschlussOhneSubjekt(); // "nimmst ein Bad"
        }

        return ersterSatz.getDuHauptsatzMitKonjunktionaladverb(konjunktionaladverb)
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

    /**
     * Gibt eine Infinitivkonstruktion mit dem Infinitiv mit diesem
     * Prädikat zurück. Die adverbiale Angabe wird im ersten
     * Teilsatz verwendet.
     */
    @Override
    public String getInfinitiv(final Person person, final Numerus numerus) {
        return ersterSatz.getInfinitiv(person, numerus)
                + " und "
                + zweiterSatz.getInfinitiv(person, numerus);
    }

    /**
     * Gibt eine Infinitivkonstruktion mit dem zu-Infinitiv mit diesem
     * Prädikat zurück. Die adverbiale Angabe wird im ersten
     * Teilsatz verwendet.
     */
    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus) {
        return ersterSatz.getZuInfinitiv(person, numerus)
                + " und "
                + zweiterSatz.getZuInfinitiv(person, numerus);
    }
}
