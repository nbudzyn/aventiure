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
class ZweiPraedikateSubjObjOhneLeerstellen implements PraedikatOhneLeerstellen {
    private final PraedikatSubjObjOhneLeerstellen ersterSatz;
    private final PraedikatSubjObjOhneLeerstellen zweiterSatz;

    public ZweiPraedikateSubjObjOhneLeerstellen(
            final PraedikatSubjObjOhneLeerstellen ersterSatz,
            final PraedikatSubjObjOhneLeerstellen zweiterSatz) {
        this.ersterSatz = ersterSatz;
        this.zweiterSatz = zweiterSatz;
    }

    /**
     * Gibt den <i>zusammengezogenen Satz</i> zurück mit dem Subjekt "du", das im
     * zweiten Teilsatz <i>eingespart</i> ist
     * ("Du hebst die goldene Kugel auf und nimmst ein Bad").
     * <p>
     * Sollte das nicht erlaubt sein, gibt die Methode eine Satzverbindung zurück.
     * <p>
     * Die Modalpartikeln werden jedenfalls nur für den ersten Teil berücksichtigt.
     */
    @Override
    public String getDescriptionDuHauptsatz(
            final Collection<Modalpartikel> modalpartikeln) {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return ersterSatz.getDescriptionDuHauptsatz(modalpartikeln)
                    // "Du hebst die goldene Kugel auf"
                    + " und "
                    + zweiterSatz
                    .getDescriptionHauptsatzMitEingespartemVorfeldSubj(
                            modalpartikeln); // "nimmst ein Bad"
        }

        return ersterSatz.getDescriptionDuHauptsatz(modalpartikeln)
                // "Du hebst die goldene Kugel auf"
                + "; "
                + zweiterSatz.getDescriptionDuHauptsatz(); // "du nimmst ein Bad"
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
    public String getDescriptionDuHauptsatz(@NonNull final AdverbialeAngabe adverbialeAngabe) {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return ersterSatz.getDescriptionDuHauptsatz(adverbialeAngabe)
                    // "Widerwillig hebst du die goldene Kugel auf"
                    + " und "
                    + zweiterSatz
                    .getDescriptionHauptsatzMitEingespartemVorfeldSubj();
            // "nimmst ein Bad"
        }

        return ersterSatz.getDescriptionDuHauptsatz(adverbialeAngabe)
                // "Widerwillig hebst du die goldene Kugel auf"
                + "; "
                + zweiterSatz.getDescriptionDuHauptsatz(); // "du nimmst ein Bad"
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
    public String getDescriptionInfinitiv(final Person person, final Numerus numerus,
                                          @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return ersterSatz.getDescriptionInfinitiv(person, numerus, adverbialeAngabe)
                + " und "
                + zweiterSatz.getDescriptionInfinitiv(person, numerus);
    }

    /**
     * Gibt eine Infinitivkonstruktion mit dem zu-Infinitiv mit diesem
     * Prädikat zurück. Die adverbiale Angabe wird im ersten
     * Teilsatz verwendet.
     */
    @Override
    public String getDescriptionZuInfinitiv(final Person person, final Numerus numerus,
                                            @Nullable final AdverbialeAngabe adverbialeAngabe) {
        return ersterSatz.getDescriptionZuInfinitiv(person, numerus, adverbialeAngabe)
                + " und "
                + zweiterSatz.getDescriptionZuInfinitiv(person, numerus);
    }
}
