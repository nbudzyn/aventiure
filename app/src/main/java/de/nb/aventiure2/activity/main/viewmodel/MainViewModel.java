package de.nb.aventiure2.activity.main.viewmodel;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.ScActionService;
import de.nb.aventiure2.scaction.devhelper.chooser.ExpectedActionNotFoundException;
import de.nb.aventiure2.scaction.devhelper.chooser.IActionChooser;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.Walkthrough;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.WalkthroughActionChooser;
import de.nb.aventiure2.score.ScoreService;

import static de.nb.aventiure2.data.database.AvDatabase.getDatabase;
import static de.nb.aventiure2.util.StreamUtil.*;

public class MainViewModel extends AndroidViewModel {
    private static final Logger LOGGER = Logger.getLogger();

    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private final MutableLiveData<String> narration = new MutableLiveData<>();
    private final MutableLiveData<List<GuiAction>> playerActionHandlers = new MutableLiveData<>();

    private final ScoreService scoreService;
    private final ScActionService scActionService;
    private final TimeTaker timeTaker;
    private final Narrator n;
    private final AvDatabase db;

    @UiThread
    public MainViewModel(final Application application) {
        super(application);

        db = getDatabase(application);
        // After installing the app, this call also initializes the game and fills the
        // database.

        timeTaker = TimeTaker.getInstance(db);
        n = Narrator.getInstance(db, timeTaker);

        scoreService = new ScoreService(application);
        scActionService = new ScActionService(application);

        score.setValue(0);
        narration.setValue("");
        playerActionHandlers.setValue(ImmutableList.of());

        postLiveUpdateLater();
    }

    @WorkerThread
    private ImmutableList<GuiAction> buildGuiActions() {
        final List<AbstractScAction> playerActions = scActionService.getPlayerActions();

        return mapToList(playerActions, this::toGuiAction);

    }

    @UiThread
    public void walkActions(final Walkthrough walkthrough) {
        walkActions(new WalkthroughActionChooser(walkthrough));
    }


    @UiThread
    private void walkActions(final IActionChooser actionChooser) {
        // Aktionen aus der GUI entfernen
        playerActionHandlers.setValue(ImmutableList.of());

        AvDatabase.databaseWriteExecutor.execute(
                new Runnable() {
                    @WorkerThread
                    @Override
                    public void run() {
                        walkActionsWork(actionChooser);
                    }
                });
    }

    @WorkerThread
    private void walkActionsWork(final IActionChooser actionChooser) {
        while (true) {
            try {
                @Nullable final AbstractScAction playerAction =
                        actionChooser.chooseAction(
                                scActionService.getPlayerActions());
                // ExpectedActionNotFoundException

                if (playerAction == null) {
                    break;
                }

                LOGGER.d("Action: " + playerAction.getName() + " [" +
                        timeTaker.now() + "]");
                db.runInTransaction(playerAction::doAndPassTime);
            } catch (final ExpectedActionNotFoundException e) {
                LOGGER.i(e.getMessage());
                // Im Moment wird die Action einfach übersprungen.
                // Um Fehler im Walkthrough zu finden gibt es ja, den WalkerTest
            }
        }

        postLiveDataUpdate();
    }

    @WorkerThread
    private GuiAction toGuiAction(final AbstractScAction playerAction) {
        return new GuiAction() {
            @Override
            public String getDisplayName() {
                return playerAction.getName();
            }

            @Override
            public String getActionType() {
                return playerAction.getType();
            }

            @Nullable
            @Override
            public CardinalDirection getCardinalDirection() {
                return playerAction.getCardinalDirection();
            }

            @Override
            @UiThread
            public void execute() {
                // Aktionen aus der GUI entfernen
                playerActionHandlers.setValue(ImmutableList.of());

                AvDatabase.databaseWriteExecutor.execute(
                        new Runnable() {
                            @WorkerThread
                            @Override
                            public void run() {
                                LOGGER.d("Action: " + playerAction.getName() + " [" +
                                        timeTaker.now() + "]");
                                db.runInTransaction(playerAction::doAndPassTime);
                                postLiveDataUpdate();
                            }
                        });
            }
        };
    }

    private void postLiveUpdateLater() {
        db.databaseWriteExecutor.execute(
                new Runnable() {
                    @WorkerThread
                    @Override
                    public void run() {
                        postLiveDataUpdate();
                    }
                });
    }

    @WorkerThread
    private void postLiveDataUpdate() {
        @Nullable final String narrationText = n.getNarrationText();
        if (narrationText == null) {
            postLiveUpdateLater();
            return;
        }
        narration.postValue(narrationText);

        score.postValue(scoreService.getScore());

        playerActionHandlers.postValue(buildGuiActions());
    }

    @UiThread
    public MutableLiveData<Integer> getScore() {
        return score;
    }

    @UiThread
    public LiveData<String> getNarration() {
        return narration;
    }

    @UiThread
    public LiveData<List<GuiAction>> getGuiActions() {
        return playerActionHandlers;
    }
}
