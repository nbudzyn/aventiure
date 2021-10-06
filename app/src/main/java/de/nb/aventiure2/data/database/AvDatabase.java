package de.nb.aventiure2.data.database;

import static de.nb.aventiure2.data.world.gameobject.World.*;

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

import de.nb.aventiure2.data.narration.ConsumedNarrationAlternativeInfo;
import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.data.narration.NarrationDao;
import de.nb.aventiure2.data.narration.NarrationSourceConverters;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.narration.StructuralElementConverters;
import de.nb.aventiure2.data.time.AvDateTimeConverters;
import de.nb.aventiure2.data.time.AvNowDao;
import de.nb.aventiure2.data.time.AvTimeSpanConverters;
import de.nb.aventiure2.data.time.NowEntity;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectIdConverters;
import de.nb.aventiure2.data.world.counter.Counter;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.amount.AmountDao;
import de.nb.aventiure2.data.world.syscomp.amount.AmountPCD;
import de.nb.aventiure2.data.world.syscomp.amount.IAmountableGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.description.impl.AmountDescriptionComp;
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
import de.nb.aventiure2.data.world.syscomp.mentalmodel.AssumedStateInfo;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelDao;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelPCD;
import de.nb.aventiure2.data.world.syscomp.movement.MovementDao;
import de.nb.aventiure2.data.world.syscomp.movement.MovementPCD;
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
import de.nb.aventiure2.data.world.syscomp.typed.GameObjectTypeConverters;
import de.nb.aventiure2.data.world.syscomp.typed.TypeDao;
import de.nb.aventiure2.data.world.syscomp.typed.TypePCD;
import de.nb.aventiure2.data.world.syscomp.waiting.WaitingDao;
import de.nb.aventiure2.data.world.syscomp.waiting.WaitingPCD;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterDao;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterPCD;
import de.nb.aventiure2.data.world.syscomp.wetter.bewoelkung.BewoelkungConverters;
import de.nb.aventiure2.data.world.syscomp.wetter.blitzunddonner.BlitzUndDonnerConverters;
import de.nb.aventiure2.data.world.syscomp.wetter.temperatur.TemperaturConverters;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.WindstaerkeConverters;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.NumerusGenusConverters;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCount;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

@Database(entities = {
        Counter.class,
        ConsumedNarrationAlternativeInfo.class,
        Narration.class,
        SCActionStepCount.class,
        NowEntity.class,
        TypePCD.class,
        AmountPCD.class,
        StatePCD.class,
        WetterPCD.class,
        WaitingPCD.class,
        MovementPCD.class,
        LocationPCD.class,
        FeelingsPCD.class,
        FeelingsTowardsInfo.class,
        MemoryPCD.class,
        KnownInfo.class,
        MentalModelPCD.class,
        AssumedLocationInfo.class,
        AssumedStateInfo.class,
        TalkingPCD.class,
        StoryWebPCD.class,
        InternalStoryData.class,
        InternalReachedStoryNodeData.class},
        version = 1,
        exportSchema = false)
@TypeConverters({
        GameObjectTypeConverters.class,
        BewoelkungConverters.class,
        BlitzUndDonnerConverters.class,
        TemperaturConverters.class,
        WindstaerkeConverters.class,
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
        HungerConverters.class,
        StoryConverters.class,
        StoryStateConverters.class})
// IDEA Database migrations, exportSchema = true?
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

    public abstract TypeDao typeDao();

    public abstract AmountDao amountDao();

    public abstract StateDao stateDao();

    public abstract WetterDao wetterDao();

    public abstract WaitingDao waitingDao();

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

                        final TimeTaker timeTaker = TimeTaker.getInstance(INSTANCE);
                        timeTaker.saveInitialState();

                        final Narrator narrator = Narrator.getInstance(INSTANCE, timeTaker);
                        final World world = World.getInstance(INSTANCE, timeTaker, narrator);
                        world.saveAllInitialState();

                        narrator.saveInitialNarration(buildInitialNarration(world));
                        // Lädt einige Objekte

                        world.saveAll(true);
                        narrator.saveAll(); // Vermutlich unnötig - egal
                        timeTaker.save(); // Vermutlich unnötig - egal
                        // Jetzt ist alles sauber!
                    }));
        }
    };

    public static void setInMemory(final boolean inMemory) {
        AvDatabase.inMemory = inMemory;
    }

    /**
     * @return Something similar to <code>Du befindest dich in einem Schloss. Hier liegt eine
     * goldene Kugel.</code>
     */
    private static Narration buildInitialNarration(final World world) {
        final StringBuilder text = new StringBuilder(200);

        text.append(
                "Diese Geschichte spielt in den alten Zeiten, wo das Wünschen noch geholfen hat. "
                        +
                        "Sie beginnt im königlichen Schloss, in einer prächtigen "
                        + "Vorhalle, Marmor und Brokat überall.\n");
        final List<IDescribableGO> objectsInRoom =
                ImmutableList.of(world.loadRequired(GOLDENE_KUGEL));
        text.append(buildObjectsInRoomDescription(objectsInRoom));

        return new Narration(Narration.NarrationSource.INITIALIZATION,
                StructuralElement.PARAGRAPH,
                text.toString(), false, false,
                false, false,
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
        final StringBuilder res = new StringBuilder(100);
        for (int i = 0; i < objectsInRoom.size(); i++) {
            res.append(getDescriptionAtFirstSight(objectsInRoom.get(i)).nomStr());
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

    private static EinzelneSubstantivischePhrase getDescriptionAtFirstSight(
            final IDescribableGO describable) {
        if ((describable instanceof IAmountableGO)
                && describable.descriptionComp() instanceof AmountDescriptionComp) {
            return ((AmountDescriptionComp) describable.descriptionComp())
                    .getDescriptionAtFirstSight(
                            ((IAmountableGO) describable).amountComp().getAmount());
        }

        return describable.descriptionComp().getDescriptionAtFirstSight();
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
                            } catch (final InterruptedException e1) {
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