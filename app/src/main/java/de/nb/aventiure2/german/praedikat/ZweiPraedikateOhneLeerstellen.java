package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

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
            final PraedikatSubjObjOhneLeerstellen ersterSatz,
            final PraedikatSubjObjOhneLeerstellen zweiterSatz) {
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


    /**
     * Gibt den <i>zusammengezogenen Satz</i> zurück mit dem Subjekt "du", das im
     * zweiten Teilsatz <i>eingespart</i> ist, und dieser adverbialen Angabe im Vorfeld, die
     * ebenfalls im zweiten Teilsatz <i>eingespart</i> ist (oder sich nur auf den ersten
     * Teilsatz bezieht). ("Widerwillig hebst du die goldene Kugel auf und nimmst ein Bad")
     * <p>
     * Sollte das nicht erlaubt sein, gibt die Methode eine Satzverbindung zurück, wobei
     * die adverbiale Angabe nur im ersten Teilsatz verwendet wird.
     */
    @Override
    public String getDuHauptsatz(@NonNull final AdverbialeAngabe adverbialeAngabe) {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return ersterSatz.getDuHauptsatz(adverbialeAngabe)
                    // "Widerwillig hebst du die goldene Kugel auf"
                    + " und "
                    + zweiterSatz
                    .getDuSatzanschlussOhneSubjekt();
            // "nimmst ein Bad"
        }

        return ersterSatz.getDuHauptsatz(adverbialeAngabe)
                // "Widerwillig hebst du die goldene Kugel auf"
                + "; "
                + zweiterSatz.getDuHauptsatz(); // "du nimmst ein Bad"
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
    public String getInfinitiv(final Person person, final Numerus numerus,
                               @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return ersterSatz.getInfinitiv(person, numerus, adverbialeAngabe)
                + " und "
                + zweiterSatz.getInfinitiv(person, numerus);
    }

    /**
     * Gibt eine Infinitivkonstruktion mit dem zu-Infinitiv mit diesem
     * Prädikat zurück. Die adverbiale Angabe wird im ersten
     * Teilsatz verwendet.
     */
    @Override
    public String getZuInfinitiv(final Person person, final Numerus numerus,
                                 @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return ersterSatz.getZuInfinitiv(person, numerus, adverbialeAngabe)
                + " und "
                + zweiterSatz.getZuInfinitiv(person, numerus);
    }
}
