package de.nb.aventiure2.data.world.base;

/**
 * Interface für eine Component im Sinne des Entity-Component-System-Patterns.
 * <p>
 * Im Grundsatz verfolgen wir diese Architektur:
 * <ul>
 * <li>Jedes relevante Wesen, Ding oder Konzept in der Welt ist ein {@link GameObject}.
 * <li>Jedes Game Object hat eine eindeutig ID, sein {@link GameObjectId}.
 * <li>Game Objects an sich haben keine änderbaren Daten.
 * <li>Ein Game Object eine Reihe von Komponenten, die {@link IComponent}s.
 * <li>Eine {@code IComponent} beschäftigt sich mit einem bestimmten fachlichen
 * Ausschnitt für das Game Objekt, z.B. mit seinem Ort, den Dingen, die es enthält,
 * seiner Bezeichnung, seinen Reaktionen auf die Welt etc.
 * <li>Komponenten kännen Daten halten, vgl. {@link AbstractStatefulComponent}.
 * <li>Eine Komponente darf andere Komponenten desselben Game Objects referenzieren.
 * Diese Referenzen sind unveränderlich und dürfen keine Zyklen enthalten. Die Komponente
 * kann also diese Komponenten (desselben Game Objects) aufrufen und auch modifizieren.
 * <li>Game Objects werden immer im Ganzen geladen und gespeichert.
 * <li>Eine Komponente darf andere Game Objects laden und einfache Abfragen oder Modifikationen
 * durchführen.
 * Sie sollte sich dabei allerdings möglichst auf ihren eigenen Komponententyp
 * beschränken - sowie auf die Typen der Komponenten, die sie referenziert.
 * Eine Komponente kann ihren Referenzen zu anderen Game Objects folgen und auch diese
 * laden - sie soll aber keine Suchen anhand allgemeiner Kriterien durchführen.
 * </ul>Zu einem Komponententyp kann es ein System geben wie
 * z.b. das {@link de.nb.aventiure2.data.world.syscomp.location.LocationSystem}.
 * Eine solches System enthält den Code, der sich mit den gleichartigen Componenten
 * mehrerer Game Objects beschäftigt - z.B. Game Object Queries.
 * <li>Komponenten können indirekt über Events miteinander kommunizieren.
 * Vgl. die Ableitungen und Implementierungen von
 * {@link de.nb.aventiure2.data.world.syscomp.reaction.IReactions}.
 * <li>Funktionalität, die mehrere (einander nicht referenzierende) Komponenten
 * (ggf. auch mehrerer Game Objects) umfasst, findet sich in
 * {@link de.nb.aventiure2.data.world.gameobject.World}. Dort ist die gesamte
 * Welt repräsentiert, und hier finden sich vor allem Abfragen, die verschiedene
 * fachliche Ausschnitte miteinder kombinieren.
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
