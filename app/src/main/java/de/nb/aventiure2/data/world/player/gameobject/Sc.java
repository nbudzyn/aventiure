package de.nb.aventiure2.data.world.player.gameobject;

import de.nb.aventiure2.data.world.base.AbstractGameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Der vom Spieler gesteuerte Charakter.
 */
public class Sc extends AbstractGameObject {
    public static GameObjectId SPIELER_CHARACTER = new GameObjectId(40_000);

    public Sc() {
        super(SPIELER_CHARACTER);
    }
}
