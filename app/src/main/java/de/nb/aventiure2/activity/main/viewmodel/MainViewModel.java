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
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.ScActionService;

import static de.nb.aventiure2.data.database.AvDatabase.getDatabase;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<String> storyText = new MutableLiveData<>();
    private final MutableLiveData<List<GuiAction>> playerActionHandlers = new MutableLiveData<>();

    private final ScActionService scActionService;
    private final AvDatabase db;

    @UiThread
    public MainViewModel(final Application application) {
        super(application);

        db = getDatabase(application);
        // After installing the app, this call also initializes the game and fills the
        // database.

        scActionService = new ScActionService(application);

        storyText.setValue("");
        playerActionHandlers.setValue(ImmutableList.of());

        postLiveUpdateLater();
    }

    @WorkerThread
    private List<GuiAction> buildGuiActions() {
        final List<AbstractScAction> playerActions =
                scActionService.getPlayerActions();

        return playerActions.stream()
                .map(this::toGuiAction)
                .collect(Collectors.toList());

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
            public void execute() {
                // Aktionen aus der GUI entfernen
                playerActionHandlers.setValue(ImmutableList.of());

                AvDatabase.databaseWriteExecutor.execute(
                        new Runnable() {
                            @WorkerThread
                            @Override
                            public void run() {
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
        @Nullable final StoryState storyState = db.storyStateDao().getStoryState();
        if (storyState == null) {
            postLiveUpdateLater();
            return;
        }

        storyText.postValue(storyState.getText());
        playerActionHandlers.postValue(buildGuiActions());
    }

    @UiThread
    public LiveData<String> getStoryText() {
        return storyText;
    }

    @UiThread
    public LiveData<List<GuiAction>> getGuiActions() {
        return playerActionHandlers;
    }
}
