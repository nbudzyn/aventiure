package de.nb.aventiure2;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;
import java.util.Random;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.ScActionService;

import static junit.framework.TestCase.fail;

@RunWith(AndroidJUnit4.class)
public class WalkerTest {
    // See https://proandroiddev.com/testing-the-un-testable-and-beyond-with-android-architecture-components-part-1-testing-room-4d97dec0f451
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static final Logger LOGGER = Logger.getLogger();

    private AvDatabase db;
    private ScActionService scActionService;
    private Context appContext;

    @Before
    public void createDb() {
        appContext = ApplicationProvider.getApplicationContext();

        AvDatabase.setInMemory(true);
        resetDatabase();
    }

    @Test
    public void walkActions() {
        for (int maxSteps = 0; maxSteps < WalkthroughActionChooser.WALKTHROUGH.length; maxSteps++) {
            LOGGER.d("--- Neuer Durchlauf: " + maxSteps + " Schritte ---");

            doWalkthrough(maxSteps);
            walkRandomly();

            resetDatabase();
        }
    }

    private void resetDatabase() {
        AvDatabase.resetDatabase();

        db = AvDatabase.getDatabase(appContext);
        GameObjects.reset(db);

        scActionService = new ScActionService(appContext);
    }

    @After
    public void closeDatabase() {
        db.close();
    }

    private void walkRandomly() {
        final RandomActionChooser actionChooser = new RandomActionChooser();

        walkActions(actionChooser);
    }

    private void doWalkthrough(final int maxSteps) {
        final WalkthroughActionChooser actionChooser = new WalkthroughActionChooser(maxSteps);

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

            @Nullable final AbstractScAction playerAction =
                    actionChooser.chooseAction(scActionService.getPlayerActions());
            if (playerAction == null) {
                return;
            }

            doAction(playerAction);
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

    /**
     * Interface für Klassen, die aus einer Liste von {@link AbstractScAction}s eine auswählen.
     */
    private interface IActionChooser {
        @Nullable
        AbstractScAction chooseAction(
                final List<? extends AbstractScAction> actionAlternatives);
    }

    /**
     * Geht einen vordefinierten Pfad von Aktionen
     */
    private static class WalkthroughActionChooser implements IActionChooser {
        private static final String[] WALKTHROUGH = {
                "Die Kugel nehmen", "Das Schloss verlassen", "In den Wald gehen",
                "Tiefer in den Wald hineingehen", "Auf dem Hauptpfad tiefer in den Wald gehen",
                "Die goldene Kugel hochwerfen", "Die goldene Kugel hochwerfen",
                "Die goldene Kugel hochwerfen", "Heulen", "Heulen",
                "Mit dem Frosch reden", "Dem Frosch Angebote machen",
                "Dem Frosch alles versprechen", "Den Frosch mitnehmen",
                "Die Kugel nehmen",
                "Hinter dem Brunnen in die Wildnis schlagen",
                "Früchte essen", "Die Kugel hinlegen", "Den Frosch absetzen",
                "Mit dem Frosch reden",
                "Den Frosch mitnehmen", "Die Kugel nehmen",
                "Zum Brunnen gehen",
                "Den Weg Richtung Schloss gehen",
                "Den überwachsenen Abzweig nehmen",
                "Um die Hütte herumgehen",
                "Auf den Baum klettern",
                "Zur Vorderseite der Hütte gehen",
                "Die Hütte betreten", "In das Bett legen", "Ein Nickerchen machen",
                "Ein Nickerchen machen",
                "Aufstehen",
                "Die Hütte verlassen",
                "Um die Hütte herumgehen",
                "Auf den Baum klettern",
                "Auf den Baum klettern",
                "Zur Vorderseite der Hütte gehen",
                "Die Hütte betreten", "In das Bett legen", "Ein Nickerchen machen",
                "Aufstehen",
                "Die Hütte verlassen",
                "Auf den Hauptpfad zurückkehren",
                "In Richtung Schloss gehen",
                "Den Wald verlassen",
                "Das Schloss betreten",
                "An einen Tisch setzen",
                "Die Kugel auf den Tisch legen",
                "Die Kugel nehmen",
                "Eintopf essen",
                "Den Frosch mitnehmen", // TODO "auf die Hand nehmen?"
                "Den Frosch auf den Tisch setzen"
        };

        private int index = -1;
        private final int maxSteps;

        public WalkthroughActionChooser(final int maxSteps) {
            this.maxSteps = maxSteps;
        }

        @Override
        @Nullable
        public AbstractScAction chooseAction(
                final List<? extends AbstractScAction> actionAlternatives) {
            index++;

            if (index >= maxSteps || index >= WALKTHROUGH.length) {
                return null;
            }

            return findAction(actionAlternatives, WALKTHROUGH[index]);
        }

        @NonNull
        private static AbstractScAction findAction(
                final List<? extends AbstractScAction> actionAlternatives,
                final String actionName) {
            return actionAlternatives.stream()
                    .filter(a -> a.getName().equals(actionName))
                    .findAny()
                    .orElseGet(() -> {
                                fail("Action missing: " + actionName + ". "
                                        + "Options are: " + actionAlternatives);
                                return null;
                            }
                    );
        }
    }

    /**
     * Wählt Aktionen zufällig aus
     */
    private static class RandomActionChooser implements IActionChooser {
        private final Random rand;
        private int count = 0;

        public RandomActionChooser() {
            rand = new Random();
        }

        @Override
        @Nullable
        public AbstractScAction chooseAction(
                final List<? extends AbstractScAction> actionAlternatives) {
            count++;
            if (count > 100) {
                return null;
            }

            return actionAlternatives.get(
                    rand.nextInt(actionAlternatives.size()));
        }
    }
}


