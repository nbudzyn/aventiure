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

import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.narration.NarrationSourceConverters;
import de.nb.aventiure2.data.narration.StructuralElementConverters;
import de.nb.aventiure2.data.world.base.GameObjectIdConverters;
import de.nb.aventiure2.data.world.counter.Counter;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsDao;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsPCD;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsTowardsInfo;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsTowardsTypeConverters;
import de.nb.aventiure2.data.world.syscomp.feelings.HungerConverters;
import de.nb.aventiure2.data.world.syscomp.feelings.MoodConverters;
import de.nb.aventiure2.data.world.syscomp.location.LocationDao;
import de.nb.aventiure2.data.world.syscomp.location.LocationPCD;
import de.nb.aventiure2.data.world.syscomp.memory.ActionTypeConverters;
import de.nb.aventiure2.data.world.syscomp.memory.KnownConverters;
import de.nb.aventiure2.data.world.syscomp.memory.KnownInfo;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryDao;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryPCD;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.AssumedLocationInfo;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelDao;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelPCD;
import de.nb.aventiure2.data.world.syscomp.movement.MovementDao;
import de.nb.aventiure2.data.world.syscomp.movement.MovementPCD;
import de.nb.aventiure2.data.world.syscomp.movement.MovementStepPhaseConverters;
import de.nb.aventiure2.data.world.syscomp.movement.PauseForSCActionConverters;
import de.nb.aventiure2.data.world.syscomp.state.StateDao;
import de.nb.aventiure2.data.world.syscomp.state.StatePCD;
import de.nb.aventiure2.data.world.syscomp.story.InternalReachedStoryNodeData;
import de.nb.aventiure2.data.world.syscomp.story.InternalStoryData;
import de.nb.aventiure2.data.world.syscomp.story.StoryConverters;
import de.nb.aventiure2.data.world.syscomp.story.StoryStateConverters;
import de.nb.aventiure2.data.world.syscomp.story.StoryWebDao;
import de.nb.aventiure2.data.world.syscomp.story.StoryWebPCD;
import de.nb.aventiure2.data.world.syscomp.talking.TalkingDao;
import de.nb.aventiure2.data.world.syscomp.talking.TalkingPCD;
import de.nb.aventiure2.data.world.time.*;
import de.nb.aventiure2.german.base.NumerusGenusConverters;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCount;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTime.*;

@Database(entities = {
        Counter.class,
        Narration.class,
        AvNow.class,
        SCActionStepCount.class,
        StatePCD.class,
        MovementPCD.class,
        LocationPCD.class,
        FeelingsPCD.class,
        FeelingsTowardsInfo.class,
        MemoryPCD.class,
        KnownInfo.class,
        MentalModelPCD.class,
        AssumedLocationInfo.class,
        TalkingPCD.class,
        StoryWebPCD.class,
        InternalStoryData.class,
        InternalReachedStoryNodeData.class},
        version = 1,
        exportSchema = false)
@TypeConverters({
        NumerusGenusConverters.class,
        AvDateTimeConverters.class,
        AvTimeSpanConverters.class,
        StructuralElementConverters.class,
        NarrationSourceConverters.class,
        ActionTypeConverters.class,
        KnownConverters.class,
        PauseForSCActionConverters.class,
        GameObjectIdConverters.class,
        MoodConverters.class,
        FeelingsTowardsTypeConverters.class,
        MovementStepPhaseConverters.class,
        HungerConverters.class,
        StoryConverters.class,
        StoryStateConverters.class})
// TODO Database migrations, exportSchema = true?
//  "In a real app, you should consider setting a directory for Room to [...] export the
//  schema so you can check the current schema into your version control system."
public abstract class AvDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "aventiureDatabase";

    @VisibleForTesting
    public static void resetDatabase() {
        INSTANCE = null;
    }

    public abstract CounterDao counterDao();

    public abstract AvNowDao nowDao();

    public abstract SCActionStepCountDao scActionStepCountDao();

    public abstract NarrationDao narrationDao();

    public abstract StateDao stateDao();

    public abstract MovementDao movementDao();

    public abstract LocationDao locationDao();

    public abstract FeelingsDao feelingsDao();

    public abstract MemoryDao memoryDao();

    public abstract MentalModelDao mentalModelDao();

    public abstract StoryWebDao storyWebDao();

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
                        INSTANCE.scActionStepCountDao().resetStepCount();
                        INSTANCE.nowDao().setNow(
                                1, oClock(12, 30));

                        final World world = World.getInstance(INSTANCE);
                        world.saveAllInitialState();

                        // Save initial state for all game objects
                        INSTANCE.narrationDao().insert(buildInitialNarration(world));
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
    private static Narration buildInitialNarration(final World world) {
        final StringBuilder text = new StringBuilder();

        text.append(
                "Diese Geschichte spielt in den alten Zeiten, wo das Wünschen noch geholfen hat. "
                        +
                        "Sie beginnt im königlichen Schloss, in einer prächtigen "
                        + "Vorhalle, Marmor und Brokat überall.\n" );
        final List<IDescribableGO> objectsInRoom = ImmutableList.of(
                (IDescribableGO) world.load(GOLDENE_KUGEL));
        text.append(buildObjectsInRoomDescription(objectsInRoom));

        return new Narration(Narration.NarrationSource.INITIALIZATION,
                StructuralElement.PARAGRAPH,
                text.toString(), false, false, false,
                null);
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
                res.append(" und " );
            }
            if (i < objectsInRoom.size() - 2) {
                // more than one after this
                res.append(", " );
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
                                throw new IllegalStateException("roomDatabaseCallback not called" );
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