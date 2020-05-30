package de.nb.aventiure2.data.world.gameobjects.player;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.ScAutomaticReactionsComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.NoSCTalkActionsTalkingComp;
import de.nb.aventiure2.data.world.time.AvDateTime;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.syscomp.memory.Known.KNOWN_FROM_LIGHT;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.IN_EINER_TASCHE;
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
        final FeelingsComp feelingsComp = new FeelingsComp(id, db, Mood.NEUTRAL, SATT,
                new AvDateTime(1, oClock(8)),
                hours(6));
        return new SpielerCharakter(id,
                new LocationComp(id, db, SCHLOSS_VORHALLE, null),
                new StoringPlaceComp(id, db, IN_EINER_TASCHE, false),
                feelingsComp,
                new MemoryComp(id, db, createKnownMap()),
                new NoSCTalkActionsTalkingComp(SPIELER_CHARAKTER, db),
                new ScAutomaticReactionsComp(db, feelingsComp));
    }

    private static Map<GameObjectId, Known> createKnownMap() {
        return ImmutableMap.<GameObjectId, Known>builder()
                .put(SPIELER_CHARAKTER, KNOWN_FROM_LIGHT)
                .put(SCHLOSS_VORHALLE, KNOWN_FROM_LIGHT)
                .put(GOLDENE_KUGEL, KNOWN_FROM_LIGHT)
                .build();
    }
}
