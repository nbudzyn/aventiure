package de.nb.aventiure2.data.narration;

/**
 * Interface for an action the player could choose.
 */
public interface IPlayerAction {
    // STORY Farben: kein Rot!

    // STORY Bei Bewegungen : Farben je nach Raum. In Abfolge ähnlich? Möglichst verschieden?
    //  Farben nach Art des Raums?! Farben nach Hashcode?
    //  Vielleicht alle anderen Aktionen gleiche Farbe?

    String getType();
}
