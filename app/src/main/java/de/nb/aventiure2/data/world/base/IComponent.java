package de.nb.aventiure2.data.world.base;

/**
 * Interface für eine Component im Sinne des Entity-Component-System-Patterns.
 */
interface IComponent {
    /**
     * Speichert die initialen Daten der Komponente (sofern sie welche hat) in die
     * Datenbank.
     */
    void saveInitialState();

    /**
     * Lädt die Daten dieser Komponente aus der Datenbank - <i>es sei denn, sie
     * wurden schon geladen</i>.
     */
    void load();

    boolean isChanged();

    /**
     * Speichert die Daten der Komponente in die Datenbank und löscht evtl. veränderliche Daten
     * aus dem Speicher. Wenn das Objekt gar nicht geladen wurde, passiert nichts.
     */
    void save(boolean unload);
}
