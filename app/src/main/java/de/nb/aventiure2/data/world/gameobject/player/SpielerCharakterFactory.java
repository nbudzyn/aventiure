package de.nb.aventiure2.data.world.gameobject.player;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.Biorhythmus;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.feelings.HungerData;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.feelings.MuedigkeitsData;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.ScAutomaticReactionsComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.NoSCTalkActionsTalkingComp;
import de.nb.aventiure2.data.world.time.*;

import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_LIGHT;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.EINE_TASCHE;
import static de.nb.aventiure2.data.world.time.AvTime.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;

/**
 * Der vom Spieler gesteuerte Charakter.
 */
public class SpielerCharakterFactory {
    private final AvDatabase db;
    private final Narrator n;
    private final World world;

    public SpielerCharakterFactory(final AvDatabase db,
                                   final Narrator n,
                                   final World world) {
        this.db = db;
        this.n = n;
        this.world = world;
    }

    public SpielerCharakter create(final GameObjectId id) {
        final FeelingsComp feelingsComp = new FeelingsComp(id, db, n, Mood.NEUTRAL,
                createMuedigkeitsBiorhythmus(),
                createInitialMuedigkeitsData(),
                createInitialHungerData(),
                hours(6),
                createDefaultFeelingsTowards(),
                createInitialFeelingsTowards());
        final LocationComp locationComp = new LocationComp(id, db, world, SCHLOSS_VORHALLE,
                null,
                // Ein NSC könnte den Spieler nicht so mir-nichts-dir-nichts mitnehmen.
                false);
        return new SpielerCharakter(id,
                locationComp,
                new StoringPlaceComp(id, db, EINE_TASCHE, false),
                feelingsComp,
                new MemoryComp(id, db, world, world.getLocationSystem(), createKnownMap()),
                new NoSCTalkActionsTalkingComp(SPIELER_CHARAKTER, db, n, world),
                new ScAutomaticReactionsComp(db, n, world, feelingsComp));
    }

    private static Biorhythmus createMuedigkeitsBiorhythmus() {
        return new Biorhythmus(
                oClock(2, 30), FeelingIntensity.SEHR_STARK,
                oClock(5), FeelingIntensity.STARK,
                oClock(7), FeelingIntensity.NUR_LEICHT,
                oClock(7, 30), FeelingIntensity.NEUTRAL,
                // Mittagstief
                oClock(12, 30), FeelingIntensity.MERKLICH,
                oClock(14, 0), FeelingIntensity.NEUTRAL,
                oClock(17, 30), FeelingIntensity.NUR_LEICHT,
                oClock(18, 30), FeelingIntensity.DEUTLICH,
                oClock(22), FeelingIntensity.STARK
        );
    }

    private static MuedigkeitsData createInitialMuedigkeitsData() {
        return new MuedigkeitsData(
                FeelingIntensity.NEUTRAL,
                Integer.MAX_VALUE,
                new AvDateTime(1, oClock(7)),
                new AvDateTime(1, oClock(11)),
                new AvDateTime(0, oClock(13,
                        30)),
                FeelingIntensity.NUR_LEICHT);
    }

    private static HungerData createInitialHungerData() {
        return new HungerData(Hunger.SATT,
                new AvDateTime(1, oClock(14)));
    }

    private static Map<FeelingTowardsType, Float> createDefaultFeelingsTowards() {
        return ImmutableMap.of(FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG, 0f);
    }

    private static Map<GameObjectId, Map<FeelingTowardsType, Float>> createInitialFeelingsTowards() {
        return ImmutableMap.of();
    }

    private static Map<GameObjectId, Known> createKnownMap() {
        return ImmutableMap.<GameObjectId, Known>builder()
                .put(SPIELER_CHARAKTER, KNOWN_FROM_LIGHT)
                .put(SCHLOSS_VORHALLE, KNOWN_FROM_LIGHT)
                .put(HAENDE_DES_SPIELER_CHARAKTERS, KNOWN_FROM_LIGHT)
                .put(EINE_TASCHE_DES_SPIELER_CHARAKTERS, KNOWN_FROM_LIGHT)
                .put(GOLDENE_KUGEL, KNOWN_FROM_LIGHT)
                .put(TAGESZEIT, KNOWN_FROM_LIGHT)
                .build();
    }
}
