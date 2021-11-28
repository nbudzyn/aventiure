package de.nb.aventiure2.androidtest;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.SpatialConnection;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.util.logger.Logger;

public abstract class AndroidTestBase {
    // See https://proandroiddev.com/testing-the-un-testable-and-beyond-with-android-architecture
    // -components-part-1-testing-room-4d97dec0f451
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static final Logger LOGGER = Logger.getLogger();

    protected Context appContext;
    protected AvDatabase db;
    protected TimeTaker timeTaker;
    protected Narrator n;
    protected World world;

    @Before
    public void createDb() {
        appContext = ApplicationProvider.getApplicationContext();

        AvDatabase.setInMemory(true);
        resetDatabase();
    }

    protected void resetDatabase() {
        World.reset();
        Narrator.reset();
        TimeTaker.reset();
        AvDatabase.resetDatabase();

        db = AvDatabase.getDatabase(appContext);
        timeTaker = TimeTaker.getInstance(db);
        n = Narrator.getInstance(db, timeTaker);
        world = World.getInstance(db, timeTaker, n);
    }

    @After
    public void closeDatabase() {
        db.close();
    }

    @Nullable
    protected SpatialConnection loadCon(final GameObjectId fromId, final GameObjectId toId) {
        final ISpatiallyConnectedGO from =
                (ISpatiallyConnectedGO) world.load(fromId);
        return from.spatialConnectionComp().getConnection(toId);
    }

    protected void doAction(final AbstractScAction playerAction) {
        LOGGER.d("Action: " + playerAction.getName() + " [" + timeTaker.now() + "]");
        db.runInTransaction(playerAction::doAndPassTime);
    }

    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }
}
