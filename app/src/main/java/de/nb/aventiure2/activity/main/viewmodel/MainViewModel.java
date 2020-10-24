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
import java.util.stream.Collectors;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narration;
import de.nb.aventiure2.logger.Logger;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.ScActionService;
import de.nb.aventiure2.scaction.devhelper.chooser.ExpectedActionNotFoundException;
import de.nb.aventiure2.scaction.devhelper.chooser.IActionChooser;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.Walkthrough;
import de.nb.aventiure2.scaction.devhelper.chooser.impl.WalkthroughActionChooser;
import de.nb.aventiure2.score.ScoreService;

import static de.nb.aventiure2.data.database.AvDatabase.getDatabase;

public class MainViewModel extends AndroidViewModel {
    private static final Logger LOGGER = Logger.getLogger();

    private final MutableLiveData<Integer> score = new MutableLiveData<>();
    private final MutableLiveData<String> narration = new MutableLiveData<>();
    private final MutableLiveData<List<GuiAction>> playerActionHandlers = new MutableLiveData<>();

    private final ScoreService scoreService;
    private final ScActionService scActionService;
    private final AvDatabase db;

    @UiThread
    public MainViewModel(final Application application) {
        super(application);

        db = getDatabase(application);
        // After installing the app, this call also initializes the game and fills the
        // database.

        scoreService = new ScoreService(application);
        scActionService = new ScActionService(application);

        score.setValue(0);
        narration.setValue("");
        playerActionHandlers.setValue(ImmutableList.of());

        postLiveUpdateLater();
    }

    @WorkerThread
    private List<GuiAction> buildGuiActions() {
        final List<AbstractScAction> playerActions = scActionService.getPlayerActions();

        return playerActions.stream()
                .map(this::toGuiAction)
                .collect(Collectors.toList());

    }

    @UiThread
    public void walkActions(final Walkthrough walkthrough) {
        walkActions(new WalkthroughActionChooser(walkthrough));
    }


    @UiThread
    public void walkActions(final IActionChooser actionChooser) {
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

                LOGGER.d("Action: " + playerAction.getName() + " [" + db.nowDao().now() + "]");
                db.runInTransaction(playerAction::doAndPassTime);
            } catch (final ExpectedActionNotFoundException e) {
                LOGGER.i(e.getMessage());
                // Im Moment wird die Action einfach übersprungen.
                // Um Fehler im Walkthrough zu finden gibt es ja, den WalkerTest

                // TODO Fehlermeldung zeigen: e.getMessage
                //  - oder nachfragen, ob der Schritt oder alle
                //  Schritte übersprungen werden sollen...
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
                                LOGGER.d("Action: " + playerAction.getName() + " [" + db.nowDao()
                                        .now() + "]");
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
        @Nullable final Narration narration = db.narrationDao().getNarration();
        if (narration == null) {
            postLiveUpdateLater();
            return;
        }
        this.narration.postValue(narration.getText());

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
