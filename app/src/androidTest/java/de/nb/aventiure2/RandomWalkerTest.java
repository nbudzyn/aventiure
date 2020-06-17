package de.nb.aventiure2;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.ScActionService;

import static junit.framework.TestCase.fail;

@RunWith(AndroidJUnit4.class)
public class RandomWalkerTest {
    private static final Logger LOGGER = Logger.getLogger();

    private Random rand;
    private AvDatabase db;
    private ScActionService scActionService;

    @Before
    public void createDb() {
        rand = new Random();
        final Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AvDatabase.class).build();
        scActionService = new ScActionService(context);
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void walk() {
        for (int i = 0; i < 500; i++) {
            final List<AbstractScAction> playerActions = scActionService.getPlayerActions();
            final AbstractScAction playerAction = choose(playerActions);

            LOGGER.d("Action: " + playerAction.getName());

            db.runInTransaction(playerAction::doAndPassTime);
        }
    }

    private AbstractScAction choose(final List<AbstractScAction> playerActions) {
        if (playerActions.isEmpty()) {
            fail("No actions");
        }

        final int i = rand.nextInt(playerActions.size());

        return playerActions.get(i);
    }
}
