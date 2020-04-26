package de.nb.aventiure2.data.world.base;

/**
 * Interface für eine Component im Sinne des Entity-Component-System-Patterns.
 */
interface IComponent {
    /**
     * Hier registriert sich das Game Object bei der Component. Bei Bedarf kann die
     * Komponente hier dem Game Object "Nachrichten schicken" oder "Fragen stellen",
     * die ggf. auch andere Components treffen können.
     */
    void setContext(ComponentContext componentContext);

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

    /**
     * Speichert die Daten der Komponente in die Datenbank und löscht veränderliche Daten
     * aus dem Speicher. Wenn das Objekt gar nicht geladen wurde, passiert nichts.
     */
    void save();
}
