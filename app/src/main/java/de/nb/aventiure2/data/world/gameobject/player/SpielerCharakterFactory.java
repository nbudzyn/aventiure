package de.nb.aventiure2.data.world.gameobject.player;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Known;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingTowardsType;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.feelings.HungerData;
import de.nb.aventiure2.data.world.syscomp.feelings.MenschlicherMuedigkeitsBiorhythmus;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.feelings.MuedigkeitsData;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelComp;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.ScAutomaticReactionsComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.NoSCTalkActionsTalkingComp;
import de.nb.aventiure2.data.world.syscomp.waiting.WaitingComp;

import static de.nb.aventiure2.data.time.AvTime.oClock;
import static de.nb.aventiure2.data.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.base.Known.KNOWN_FROM_LIGHT;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceType.EINE_TASCHE;

/**
 * Der vom Spieler gesteuerte Charakter.
 */
public class SpielerCharakterFactory {
    private final AvDatabase db;
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final World world;

    public SpielerCharakterFactory(final AvDatabase db,
                                   final TimeTaker timeTaker, final Narrator n,
                                   final World world) {
        this.db = db;
        this.timeTaker = timeTaker;
        this.n = n;
        this.world = world;
    }

    public SpielerCharakter create(final GameObjectId id) {
        final WaitingComp waitingComp = new WaitingComp(id, db, timeTaker);
        final MemoryComp memoryComp =
                new MemoryComp(id, db, world, createKnownMap());
        final MenschlicherMuedigkeitsBiorhythmus muedigkeitsBiorhythmus =
                new MenschlicherMuedigkeitsBiorhythmus();
        final LocationComp locationComp = new LocationComp(id, db, world, SCHLOSS_VORHALLE,
                null,
                // Ein NSC könnte den Spieler nicht so mir-nichts-dir-nichts mitnehmen.
                false);
        final FeelingsComp feelingsComp = new FeelingsComp(id, db, timeTaker, n,
                world, waitingComp,
                memoryComp,
                locationComp, Mood.NEUTRAL,
                muedigkeitsBiorhythmus,
                MuedigkeitsData.createFromBiorhythmusFuerMenschen(
                        muedigkeitsBiorhythmus, timeTaker.now()),
                createInitialHungerData(),
                hours(6),
                createDefaultFeelingsTowards(),
                createInitialFeelingsTowards());
        final MentalModelComp mentalModelComp =
                new MentalModelComp(id, db, world, ImmutableMap.of());
        return new SpielerCharakter(id,
                locationComp,
                mentalModelComp,
                new StoringPlaceComp(id, timeTaker, locationComp, EINE_TASCHE,
                        false, null),
                waitingComp,
                feelingsComp,
                memoryComp,
                new NoSCTalkActionsTalkingComp(SPIELER_CHARAKTER, db, timeTaker, n, world),
                new ScAutomaticReactionsComp(db, n, world,
                        locationComp, mentalModelComp,
                        waitingComp,
                        feelingsComp));
    }

    private static HungerData createInitialHungerData() {
        return new HungerData(Hunger.SATT,
                new AvDateTime(1, oClock(17)));
    }

    private static ImmutableMap<FeelingTowardsType, Float> createDefaultFeelingsTowards() {
        return ImmutableMap.of(FeelingTowardsType.ZUNEIGUNG_ABNEIGUNG, 0f);
    }

    private static ImmutableMap<GameObjectId, Map<FeelingTowardsType, Float>>
    createInitialFeelingsTowards() {
        return ImmutableMap.of();
    }

    private static ImmutableMap<GameObjectId, Known> createKnownMap() {
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
