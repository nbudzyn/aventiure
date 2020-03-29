package de.nb.aventiure2.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.nb.aventiure2.data.storystate.AvStoryStateConverters;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.counter.Counter;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.entity.creature.CreatureConverters;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.creature.CreatureDataDao;
import de.nb.aventiure2.data.world.entity.creature.CreatureStateConverters;
import de.nb.aventiure2.data.world.entity.object.AvObject;
import de.nb.aventiure2.data.world.entity.object.AvObjectConverters;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.entity.object.ObjectDataDao;
import de.nb.aventiure2.data.world.invisible.InvisibleConverters;
import de.nb.aventiure2.data.world.invisible.InvisibleData;
import de.nb.aventiure2.data.world.invisible.InvisibleDataDao;
import de.nb.aventiure2.data.world.invisible.InvisibleStateConverters;
import de.nb.aventiure2.data.world.player.inventory.PlayerInventoryDao;
import de.nb.aventiure2.data.world.player.inventory.PlayerInventoryItem;
import de.nb.aventiure2.data.world.player.location.PlayerLocation;
import de.nb.aventiure2.data.world.player.location.PlayerLocationDao;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMindConverters;
import de.nb.aventiure2.data.world.player.stats.PlayerStats;
import de.nb.aventiure2.data.world.player.stats.PlayerStatsDao;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.AvRoomConverters;
import de.nb.aventiure2.data.world.room.KnownRoom;
import de.nb.aventiure2.data.world.room.RoomDao;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvDateTimeDao;

import static de.nb.aventiure2.data.storystate.StoryStateBuilder.t;
import static de.nb.aventiure2.data.world.entity.object.AvObject.Key.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;

@Database(entities = {
        Counter.class,
        StoryState.class,
        AvDateTime.class,
        KnownRoom.class,
        InvisibleData.class,
        ObjectData.class,
        CreatureData.class,
        PlayerStats.class,
        PlayerLocation.class, PlayerInventoryItem.class},
        version = 1,
        exportSchema = false)
@TypeConverters({AvStoryStateConverters.class, AvRoomConverters.class,
        InvisibleConverters.class,
        InvisibleStateConverters.class,
        AvObjectConverters.class,
        CreatureConverters.class,
        CreatureStateConverters.class,
        PlayerStateOfMindConverters.class})
// TODO Database migrations, exportSchema = true?
// "In a real app, you should consider setting a directory for Room to use to export the
// schema so you can check the current schema into your version control system."
public abstract class AvDatabase extends RoomDatabase {
    public abstract CounterDao counterDao();

    public abstract AvDateTimeDao dateTimeDao();

    public abstract StoryStateDao storyStateDao();

    public abstract RoomDao roomDao();

    public abstract InvisibleDataDao invisibleDataDao();

    public abstract ObjectDataDao objectDataDao();

    public abstract CreatureDataDao creatureDataDao();

    public abstract PlayerLocationDao playerLocationDao();

    public abstract PlayerStatsDao playerStatsDao();

    public abstract PlayerInventoryDao playerInventoryDao();

    private static volatile AvDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull final SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background:
                // Set date and time in the game
                INSTANCE.dateTimeDao().setDateTime(
                        1, oClock(14, 30));
                // Invisibles have their initial state
                INSTANCE.invisibleDataDao().insertInitial();

                // Objects are placed at their respective initial location.
                INSTANCE.objectDataDao().insertInitial();
                INSTANCE.creatureDataDao().insertInitial();

                // The player starts in the castle:
                INSTANCE.playerStatsDao().insert(buildInitialPlayerStats());
                INSTANCE.playerLocationDao().setRoom(AvRoom.SCHLOSS_VORHALLE);
                INSTANCE.storyStateDao().add(buildInitialStoryState());
                INSTANCE.roomDao().setKnown(AvRoom.SCHLOSS_VORHALLE);
                INSTANCE.objectDataDao().setKnown(GOLDENE_KUGEL);
            });
        }
    };

    private static PlayerStats buildInitialPlayerStats() {
        return new PlayerStats(PlayerStateOfMind.NEUTRAL);
    }

    /**
     * @return Something similar to <code>Du befindest dich in einem Schloss. Hier liegt eine goldene Kugel.</code>
     */
    private static StoryStateBuilder buildInitialStoryState() {
        final StringBuilder res = new StringBuilder();

        res.append(
                "Diese Geschichte spielt in den alten Zeiten, wo das Wünschen noch geholfen hat. " +
                        "Sie beginnt im königlichen Schloss, in einer prächtigen "
                        + "Vorhalle, Marmor und Brokat überall.\n");
        final List<AvObject> objectsInRoom = ImmutableList.of(AvObject.get(GOLDENE_KUGEL));
        res.append(buildObjectsInRoomDescription(objectsInRoom));

        return t((IPlayerAction) null,
                StructuralElement.WORD,
                res.toString())
                .letzterRaum(AvRoom.SCHLOSS_VORHALLE);
    }

    /**
     * @return Something similar to <code>Hier liegt eine goldene Kugel.</code>
     */
    private @Nullable
    static String buildObjectsInRoomDescription(final List<AvObject> objectsInRoom) {
        if (objectsInRoom.isEmpty()) {
            return null;
        }

        return buildObjectInRoomDescriptionPrefix(objectsInRoom.size())
                + " "
                + buildObjectsInRoomDescriptionList(objectsInRoom);
    }

    /**
     * @return Something similar to <code>Hier liegt</code>
     */
    private static String buildObjectInRoomDescriptionPrefix(final int numberOfObjects) {
        if (numberOfObjects == 1) {
            // TODO Not everything fits on a table
            return "Auf einem Tisch liegt";
        }

        return "Auf einem Tisch liegen";
    }

    /**
     * @return Something similar to <code>eine goldene Kugel</code>
     */
    private static String buildObjectsInRoomDescriptionList(final List<AvObject> objectsInRoom) {
        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < objectsInRoom.size(); i++) {
            res.append(objectsInRoom.get(i).getDescriptionAtFirstSight().nom());
            if (i == objectsInRoom.size() - 2) {
                // one before the last
                res.append(" und ");
            }
            if (i < objectsInRoom.size() - 2) {
                // more than one after this
                res.append(", ");
            }
        }

        return res.toString();
    }

    public static AvDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AvDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AvDatabase.class, "aventiureDatabase")
                            .addCallback(roomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}