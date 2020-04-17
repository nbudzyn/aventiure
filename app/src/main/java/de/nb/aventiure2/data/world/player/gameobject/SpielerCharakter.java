package de.nb.aventiure2.data.world.player.gameobject;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Der vom Spieler gesteuerte Charakter.
 */
public class SpielerCharakter {
    public static GameObjectId SPIELER_CHARACTER = new GameObjectId(40_000);

    private static final GameObject SINGLETON;

    static {
        SINGLETON = new GameObject(SPIELER_CHARACTER);
    }

    private SpielerCharakter() {
    }

    public static GameObject get() {
        return SINGLETON;
    }
}
