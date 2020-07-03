package de.nb.aventiure2.data.world.gameobject.player;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.ScAutomaticReactionsComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.NoSCTalkActionsTalkingComp;
import de.nb.aventiure2.data.world.time.AvDateTime;

import static de.nb.aventiure2.data.world.gameobject.World.EINE_TASCHE_DES_SPIELER_CHARAKTERS;
import static de.nb.aventiure2.data.world.gameobject.World.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobject.World.HAENDE_DES_SPIELER_CHARAKTERS;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.KNOWN_FROM_LIGHT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.EINE_TASCHE;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;

/**
 * Der vom Spieler gesteuerte Charakter.
 */
public class SpielerCharakterFactory {
    private final AvDatabase db;
    private final World world;

    public SpielerCharakterFactory(final AvDatabase db,
                                   final World world) {
        this.db = db;
        this.world = world;
    }

    public SpielerCharakter create(final GameObjectId id) {
        final FeelingsComp feelingsComp = new FeelingsComp(id, db, Mood.NEUTRAL, SATT,
                new AvDateTime(1, oClock(8)),
                hours(6));
        return new SpielerCharakter(id,
                new LocationComp(id, db, world, SCHLOSS_VORHALLE, null,
                        // Ein NSC könnte den Spieler nicht so mir-nichts-dir-nichts mitnehmen.
                        false),
                new StoringPlaceComp(id, db, world, EINE_TASCHE, false),
                feelingsComp,
                new MemoryComp(id, db, createKnownMap()),
                new NoSCTalkActionsTalkingComp(SPIELER_CHARAKTER, db, world),
                new ScAutomaticReactionsComp(db, world, feelingsComp));
    }

    private static Map<GameObjectId, Known> createKnownMap() {
        return ImmutableMap.<GameObjectId, Known>builder()
                .put(SPIELER_CHARAKTER, KNOWN_FROM_LIGHT)
                .put(SCHLOSS_VORHALLE, KNOWN_FROM_LIGHT)
                .put(HAENDE_DES_SPIELER_CHARAKTERS, KNOWN_FROM_LIGHT)
                .put(EINE_TASCHE_DES_SPIELER_CHARAKTERS, KNOWN_FROM_LIGHT)
                .put(GOLDENE_KUGEL, KNOWN_FROM_LIGHT)
                .build();
    }
}
