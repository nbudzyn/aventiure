package de.nb.aventiure2;

import android.content.Context;

import androidx.annotation.NonNull;
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
import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.ScActionService;

import static junit.framework.TestCase.fail;

@RunWith(AndroidJUnit4.class)
public class RandomWalkerTest {
    // See https://proandroiddev.com/testing-the-un-testable-and-beyond-with-android-architecture-components-part-1-testing-room-4d97dec0f451
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static final Logger LOGGER = Logger.getLogger();

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

    private Random rand;
    private AvDatabase db;
    private ScActionService scActionService;

    @Before
    public void createDb() {
        rand = new Random();
        final Context context = ApplicationProvider.getApplicationContext();

        AvDatabase.setInMemory(true);

        // context.deleteDatabase(AvDatabase.DATABASE_NAME);
        db = AvDatabase.getDatabase(context);

        scActionService = new ScActionService(context);
    }

    @After
    public void closeDb() {
        if (db != null) {
            db.close();
        }
    }

    @Test
    public void walk() {
        // TODO: Erst einen Schritt gehen, dann random
        //  Dann zwei Schritte, dann random
        //  Dann drei Schritte, dann random
        //  Dazwischen immer Datenbank resetten!

        walkActions(WALKTHROUGH);
        walkRandomly();
    }

    private void walkRandomly() {
        for (int i = 0; i < 500; i++) {
            final List<AbstractScAction> playerActions = scActionService.getPlayerActions();
            final AbstractScAction playerAction = choose(playerActions);

            doAction(playerAction);
        }
    }

    private void walkActions(final String... actionNames) {
        for (int i = 0; i < actionNames.length; i++) {
            final AbstractScAction playerAction = getAction(actionNames[i]);

            doAction(playerAction);
        }
    }

    private void doAction(final AbstractScAction playerAction) {
        LOGGER.d("Action: " + playerAction.getName());
        db.runInTransaction(playerAction::doAndPassTime);
    }

    @NonNull
    private AbstractScAction getAction(final String actionName) {
        final List<AbstractScAction> playerActions = scActionService.getPlayerActions();
        return playerActions.stream()
                .filter(a -> a.getName().equals(actionName))
                .findAny()
                .orElseGet(() -> {
                            fail("Action missing: " + actionName + ". Options are: " + playerActions);
                            return null;
                        }
                );
    }

    private AbstractScAction choose(final List<AbstractScAction> playerActions) {
        if (playerActions.isEmpty()) {
            // TODO For Debugging
            scActionService.getPlayerActions();
            fail("No actions");
        }

        final int i = rand.nextInt(playerActions.size());

        return playerActions.get(i);
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


