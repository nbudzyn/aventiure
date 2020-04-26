package de.nb.aventiure2.data.world.player;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.feelings.Mood;
import de.nb.aventiure2.data.world.location.LocationComp;
import de.nb.aventiure2.data.world.memory.Known;
import de.nb.aventiure2.data.world.memory.Memory;
import de.nb.aventiure2.data.world.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.time.AvDateTime;

import static de.nb.aventiure2.data.world.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.memory.Known.KNOWN_FROM_LIGHT;
import static de.nb.aventiure2.data.world.storingplace.StoringPlaceType.IN_EINER_TASCHE;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;

/**
 * Der vom Spieler gesteuerte Charakter.
 */
public class SpielerCharakterFactory {
    private final AvDatabase db;

    public SpielerCharakterFactory(final AvDatabase db) {
        this.db = db;
    }

    public SpielerCharakter create(final GameObjectId id) {
        return new SpielerCharakter(id,
                new LocationComp(id, db, SCHLOSS_VORHALLE),
                new StoringPlaceComp(id, IN_EINER_TASCHE),
                new FeelingsComp(id, db, Mood.NEUTRAL, SATT,
                        new AvDateTime(1, oClock(8)),
                        hours(6)),
                new Memory(id, db, createKnownMap()));
    }

    private static Map<GameObjectId, Known> createKnownMap() {
        return ImmutableMap.<GameObjectId, Known>builder()
                .put(SPIELER_CHARAKTER, KNOWN_FROM_LIGHT)
                .put(SCHLOSS_VORHALLE, KNOWN_FROM_LIGHT)
                .put(GOLDENE_KUGEL, KNOWN_FROM_LIGHT)
                .build();
    }
}
