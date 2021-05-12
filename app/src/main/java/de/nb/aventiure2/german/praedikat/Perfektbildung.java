package de.nb.aventiure2.german.praedikat;

public enum Perfektbildung {
    /**
     * Das Verb bildet sein Perfekt mit "haben": "Ich habe gegessen"
     */
    HABEN,
    /**
     * Das Verb bildet sein Perfekt mit "sein": "Ich bin gelaufen"
     */
    SEIN;

    // Leider können wir hier keine Refererenz zum Hilfsverb haben -
    // das gibt sonst zirkuläre Abhängigkeiten zum SeinUtil und HabenUtil,
    // die beim Laden der Klassen (oder später zu NullPointerExceptions)
    // führen.
}
