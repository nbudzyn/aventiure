package de.nb.aventiure2.german.praedikat;

public enum Perfektbildung {
    /**
     * Das Verb bildet sein Perfekt mit "haben": "Ich habe gegessen"
     */
    HABEN(HabenUtil.VERB),
    /**
     * Das Verb bildet sein Perfekt mit "sein": "Ich bin gelaufen"
     */
    SEIN(SeinUtil.VERB);

    private final Verb hilfsverb;

    Perfektbildung(final Verb hilfsverb) {
        this.hilfsverb = hilfsverb;
    }

    public Verb getHilfsverb() {
        return hilfsverb;
    }
}
