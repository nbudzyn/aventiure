package de.nb.aventiure2.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
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
import de.nb.aventiure2.data.storystate.NumerusGenusConverters;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObjectIdConverters;
import de.nb.aventiure2.data.world.counter.Counter;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobjects.GameObjectService;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsDao;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsPCD;
import de.nb.aventiure2.data.world.syscomp.feelings.HungerConverters;
import de.nb.aventiure2.data.world.syscomp.feelings.MoodConverters;
import de.nb.aventiure2.data.world.syscomp.location.LocationDao;
import de.nb.aventiure2.data.world.syscomp.location.LocationPCD;
import de.nb.aventiure2.data.world.syscomp.memory.ActionTypeConverters;
import de.nb.aventiure2.data.world.syscomp.memory.KnownConverters;
import de.nb.aventiure2.data.world.syscomp.memory.KnownInfo;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryDao;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryPCD;
import de.nb.aventiure2.data.world.syscomp.state.GameObjectStateConverters;
import de.nb.aventiure2.data.world.syscomp.state.StateDao;
import de.nb.aventiure2.data.world.syscomp.state.StatePCD;
import de.nb.aventiure2.data.world.syscomp.talking.TalkingDao;
import de.nb.aventiure2.data.world.syscomp.talking.TalkingPCD;
import de.nb.aventiure2.data.world.time.AvDateTimeConverters;
import de.nb.aventiure2.data.world.time.AvNow;
import de.nb.aventiure2.data.world.time.AvNowDao;
import de.nb.aventiure2.german.base.StructuralElement;

import static de.nb.aventiure2.data.storystate.StoryStateBuilder.t;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;

@Database(entities = {
        Counter.class,
        StoryState.class,
        AvNow.class,
        StatePCD.class,
        LocationPCD.class,
        FeelingsPCD.class,
        MemoryPCD.class,
        TalkingPCD.class,
        KnownInfo.class},
        version = 1,
        exportSchema = false)
@TypeConverters({
        NumerusGenusConverters.class,
        AvDateTimeConverters.class,
        AvStoryStateConverters.class,
        ActionTypeConverters.class,
        KnownConverters.class,
        GameObjectStateConverters.class,
        GameObjectIdConverters.class,
        MoodConverters.class,
        HungerConverters.class})
// TODO Database migrations, exportSchema = true?
// "In a real app, you should consider setting a directory for Room to [...] export the
// schema so you can check the current schema into your version control system."
public abstract class AvDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "aventiureDatabase";

    @VisibleForTesting
    public static void resetDatabase() {
        INSTANCE = null;
    }

    public abstract CounterDao counterDao();

    public abstract AvNowDao nowDao();

    public abstract StoryStateDao storyStateDao();

    public abstract StateDao stateDao();

    public abstract LocationDao locationDao();

    public abstract FeelingsDao feelingsDao();

    public abstract MemoryDao memoryDao();

    public abstract TalkingDao talkingDao();

    private static volatile AvDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 1;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static boolean inMemory = false;

    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull final SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() ->
                    INSTANCE.runInTransaction(() -> {
                        // Populate the database in the background:

                        // Set date and time in the game
                        INSTANCE.nowDao().setNow(
                                1, oClock(14, 30));

                        final GameObjectService gos = GameObjectService.getInstance(INSTANCE);
                        gos.saveAllInitialState();

                        // Save initial state for all game objects
                        INSTANCE.storyStateDao().add(buildInitialStoryState(gos));
                    }));
        }
    };

    public static void setInMemory(final boolean inMemory) {
        AvDatabase.inMemory = inMemory;
    }

    /*
    public static void resetDatabase(Context context) {
        if (!inMemory) {
            throw new IllegalStateException(
                    "Resetting non-in-memory database? Why would you do that?");
        }
        context.deleteDatabase(DATABASE_NAME);
        ...
    }
    */

    /**
     * @return Something similar to <code>Du befindest dich in einem Schloss. Hier liegt eine goldene Kugel.</code>
     */
    private static StoryStateBuilder buildInitialStoryState(final GameObjectService gos) {
        final StringBuilder res = new StringBuilder();

        res.append(
                "Diese Geschichte spielt in den alten Zeiten, wo das Wünschen noch geholfen hat. "
                        +
                        "Sie beginnt im königlichen Schloss, in einer prächtigen "
                        + "Vorhalle, Marmor und Brokat überall.\n");
        final List<IDescribableGO> objectsInRoom = ImmutableList.of(
                (IDescribableGO) gos.load(GOLDENE_KUGEL));
        res.append(buildObjectsInRoomDescription(objectsInRoom));

        return t(StructuralElement.WORD, res.toString());
    }

    /**
     * @return Something similar to <code>Hier liegt eine goldene Kugel.</code>
     */
    private @Nullable
    static String buildObjectsInRoomDescription(
            final List<? extends IDescribableGO> objectsInRoom) {
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
            return "Auf einem Tisch liegt";
        }

        return "Auf einem Tisch liegen";
    }

    /**
     * @return Something similar to <code>eine goldene Kugel</code>
     */
    private static String buildObjectsInRoomDescriptionList(
            final List<? extends IDescribableGO> objectsInRoom) {
        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < objectsInRoom.size(); i++) {
            res.append(objectsInRoom.get(i).descriptionComp().getDescriptionAtFirstSight().nom());
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
                    if (inMemory) {
                        INSTANCE = Room.inMemoryDatabaseBuilder(context, AvDatabase.class)
                                .allowMainThreadQueries()
                                .addCallback(roomDatabaseCallback)
                                .build();
                        if (INSTANCE.nowDao().now() == null) {
                            try {
                                Thread.sleep(5000);
                            } catch (final InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }

                            if (INSTANCE.nowDao().now() == null) {
                                throw new IllegalStateException("roomDatabaseCallback not called");
                            }
                        }

                    } else {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AvDatabase.class, DATABASE_NAME)
                                .addCallback(roomDatabaseCallback)
                                .build();
                    }
                }
            }
        }
        return INSTANCE;
    }
}