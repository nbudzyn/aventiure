package de.nb.aventiure2;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.ScActionService;
import de.nb.aventiure2.scaction.devhelper.chooser.ExpectedActionNotFoundException;
import de.nb.aventiure2.scaction.devhelper.chooser.IActionChooser;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.RandomActionChooser;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.Walkthrough;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.WalkthroughActionChooser;
import de.nb.aventiure2.score.ScoreService;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.fail;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WalkerTest {
    private static final int STEP_SIZE = 5;
    // See https://proandroiddev.com/testing-the-un-testable-and-beyond-with-android-architecture-components-part-1-testing-room-4d97dec0f451
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static final Logger LOGGER = Logger.getLogger();

    private AvDatabase db;
    private ScoreService scoreService;
    private ScActionService scActionService;
    private Context appContext;

    @Before
    public void createDb() {
        appContext = ApplicationProvider.getApplicationContext();

        AvDatabase.setInMemory(true);
        resetDatabase();
    }

    @Test
    public void aaa_doStrictWalkthrough() {
        // Will be executed first for a fast feedback whether
        // the basic walkthrough still works
        doWalkthrough(Walkthrough.FULL);

        assertEquals(100, scoreService.getScore());
        assertFalse(db.narrationDao().requireNarration().getText().contains("@"));
    }

    @Test
    public void baa_doStrictWalkthrough_separat_bis_mit_frosch_bei_rapunzel() {
        doWalkthrough(Walkthrough.SEP_ANFANG_BIS_MIT_FROSCH_BEI_RAPUNZEL);

        assertFalse(db.narrationDao().requireNarration().getText().contains("@"));
    }

    @Test
    public void walkActionsWithRandomAdditions() {
        int maxSteps = 0;
        while (maxSteps < Walkthrough.FULL.numSteps()) {
            LOGGER.d("--- Neuer Durchlauf: " + maxSteps + " vorgegebene Schritte ---");

            doWalkthrough(Walkthrough.FULL.truncate(maxSteps));
            walkRandomly();
            assertFalse(db.narrationDao().requireNarration().getText().contains("@"));

            resetDatabase();

            maxSteps = maxSteps + STEP_SIZE;
        }
    }

    private void resetDatabase() {
        World.reset();
        AvDatabase.resetDatabase();

        db = AvDatabase.getDatabase(appContext);

        scoreService = new ScoreService(appContext);
        scActionService = new ScActionService(appContext);
    }

    @After
    public void closeDatabase() {
        db.close();
    }

    private void walkRandomly() {
        final RandomActionChooser actionChooser = new RandomActionChooser();
        // TODO Nicht nur zufällig sondern nach konfigurierbaren Vorgaben. Z.B.
        //  Aktionen gemäß Primzahlen, immer abwechselnd, bestimmte Festlegungen, sonst
        //  zufällig o.Ä.

        walkActions(actionChooser);
    }

    private void doWalkthrough(final Walkthrough walkthrough) {
        final WalkthroughActionChooser actionChooser =
                new WalkthroughActionChooser(walkthrough);

        walkActions(actionChooser);
    }

    private void walkActions(final IActionChooser actionChooser) {
        while (true) {
            final List<AbstractScAction> playerActions = scActionService.getPlayerActions();
            if (playerActions.isEmpty()) {
                // For easier debugging
                scActionService.getPlayerActions();
                fail("No actions");
            }

            try {
                @Nullable final AbstractScAction playerAction =
                        actionChooser.chooseAction(
                                scActionService
                                        .getPlayerActions()); // ExpectedActionNotFoundException
                if (playerAction == null) {
                    return;
                }

                doAction(playerAction);
            } catch (final ExpectedActionNotFoundException e) {
                fail(e.getMessage());
            }
        }
    }

    private void doAction(final AbstractScAction playerAction) {
        LOGGER.d("Action: " + playerAction.getName());
        db.runInTransaction(playerAction::doAndPassTime);
    }

    public static void deleteDatabaseFile(final Context context, final String databaseName) {
        final File databases = new File(context.getApplicationInfo().dataDir + "/databases");
        final File db = new File(databases, databaseName);
        if (db.delete()) {
            System.out.println("Database deleted");
        } else {
            System.out.println("Failed to delete database");
        }

        final File journal = new File(databases, databaseName + "-journal");
        if (journal.exists()) {
            if (journal.delete()) {
                System.out.println("Database journal deleted");
            } else {
                System.out.println("Failed to delete database journal");
            }
        }
    }

}


