package de.nb.aventiure2;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.common.collect.ImmutableList;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.util.List;

import de.nb.aventiure2.androidtest.AndroidTestBase;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.ScActionService;
import de.nb.aventiure2.scaction.devhelper.chooser.ExpectedActionNotFoundException;
import de.nb.aventiure2.scaction.devhelper.chooser.IActionChooser;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.RandomActionChooser;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.Walkthrough;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.WalkthroughActionChooser;
import de.nb.aventiure2.score.ScoreService;

import static com.google.common.truth.Truth.assertThat;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static junit.framework.TestCase.fail;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WalkerTest extends AndroidTestBase {
    private static final int STEP_SIZE = 1;
    private static final int NUM_RANDOM_STEPS = 50;

    private static final Logger LOGGER = Logger.getLogger();

    private ScoreService scoreService;
    private ScActionService scActionService;

    @Test
    public void aaa_doStrictWalkthrough() {
        // Will be executed first for a fast feedback whether
        // the basic walkthrough still works
        doWalkthrough(Walkthrough.FULL);

        assertThat(scoreService.getScore()).isEqualTo(100);
        assertNoVerbiddenContentInNarration();
    }

    @Test
    public void baa_doStrictWalkthrough_separat_nur_rapunzel() {
        doWalkthrough(Walkthrough.SEP_1_NUR_RAPUNZEL);

        assertNoVerbiddenContentInNarration();
    }

    @Test
    public void walkActionsWithRandomAdditions() {
        walkActionsWithRandomAdditions(Walkthrough.FULL);
    }

    @Test
    public void walkActions_separat_nur_rapunzel_WithRandomAdditions() {
        walkActionsWithRandomAdditions(Walkthrough.SEP_1_NUR_RAPUNZEL);
    }

    public void walkActionsWithRandomAdditions(final Walkthrough walkthrough) {
        int maxSteps = 0;
        while (maxSteps < walkthrough.numSteps()) {
            LOGGER.d("--- Neuer Durchlauf: " + maxSteps + " vorgegebene Schritte ---");

            doWalkthrough(walkthrough.truncate(maxSteps));
            walkRandomly();
            assertNoVerbiddenContentInNarration();

            resetDatabase();

            maxSteps = maxSteps + STEP_SIZE;
        }
    }

    public void assertNoVerbiddenContentInNarration() {
        final String narrationText = db.narrationDao().requireNarration().getText();
        assertThatDoesNotContainAnyOf(narrationText,
                "@", "  ", " \n", "\n ", "\n\n\n", ".!", "!.", "?.", ".?", "–.",
                "\"", // Falsche Anführungszeichen
                "„.", "„!", "„ ", "„?", ".„", "!„", "?„", "„,",
                "“.", "“!", "“?", ",“", ", “",
                "”", // Falsche Abführungszeichen
                "Und „", "Aber „", "Denn „"
        );
    }

    private static void assertThatDoesNotContainAnyOf(final String actual,
                                                      final String... notExpected) {
        for (final String notExcptectedString : notExpected) {
            assertThat(actual).doesNotContain(notExcptectedString);
        }
    }

    @Override
    protected void resetDatabase() {
        super.resetDatabase();

        scoreService = new ScoreService(appContext);
        scActionService = new ScActionService(appContext);
    }

    private void walkRandomly() {
        final RandomActionChooser actionChooser = new RandomActionChooser(NUM_RANDOM_STEPS);

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

                final SpielerCharakter spielerCharakter = world.loadSC();

                final ITalkerGO zauberin = (ITalkerGO) world.load(RAPUNZELS_ZAUBERIN);

                final ImmutableList<? extends ILivingBeingGO> livingBeings =
                        world.loadDescribableLocatableLivingBeings();

                fail("No actions. SC at " + scActionService.getSCLocation()
                        + ", livingBeings: " + livingBeings
                        + ", spielerCharakter.talkingComp().isInConversation(): "
                        + spielerCharakter.talkingComp().isInConversation()
                        + ", spielerCharakter.talkingComp().getTalkingTo(): "
                        + spielerCharakter.talkingComp().getTalkingTo()
                        + ", zauberin.talkingComp().isInConversation(): "
                        + zauberin.talkingComp().isInConversation()
                        + ", zauberin.talkingComp().getTalkingTo(): "
                        + zauberin.talkingComp().getTalkingTo()
                        + ", Text: " + n.getNarrationText());
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


