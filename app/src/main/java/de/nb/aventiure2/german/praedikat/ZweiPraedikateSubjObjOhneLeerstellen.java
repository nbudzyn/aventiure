package de.nb.aventiure2.german.praedikat;

import androidx.annotation.NonNull;

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
     */
    @Override
    public String getDescriptionDuHauptsatz() {
        if (ersterSatz.duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen()) {
            return ersterSatz.getDescriptionDuHauptsatz() // "Du hebst die goldene Kugel auf"
                    + " und "
                    + zweiterSatz
                    .getDescriptionHauptsatzMitEingespartemVorfeldSubj(); // "nimmst ein Bad"
        }

        return ersterSatz.getDescriptionDuHauptsatz() // "Du hebst die goldene Kugel auf"
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
                    .getDescriptionHauptsatzMitEingespartemVorfeldSubj(); // "nimmst ein Bad"
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
     * Gibt eine Reihung von Infinitivphrasen zurück.
     * ("Die goldene Kugel aufheben und ein Bad nehmen")
     */
    @Override
    public String getDescriptionInfinitiv() {
        return ersterSatz.getDescriptionInfinitiv()
                + " und "
                + zweiterSatz.getDescriptionInfinitiv();
    }
}
