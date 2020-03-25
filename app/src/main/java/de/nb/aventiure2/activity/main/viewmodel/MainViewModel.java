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
import de.nb.aventiure2.playeraction.AbstractPlayerAction;
import de.nb.aventiure2.playeraction.PlayerActionService;

import static de.nb.aventiure2.data.database.AvDatabase.getDatabase;

public class MainViewModel extends AndroidViewModel {
    private final MutableLiveData<String> storyText = new MutableLiveData<>();
    private final MutableLiveData<List<GuiAction>> playerActionHandlers = new MutableLiveData<>();

    private final PlayerActionService playerActionService;
    private final AvDatabase db;

    @UiThread
    public MainViewModel(final Application application) {
        super(application);
        db = getDatabase(application);

        playerActionService = new PlayerActionService(application);

        storyText.setValue("");
        playerActionHandlers.setValue(ImmutableList.of());

        postLiveUpdateLater();
    }

    @UiThread
    private List<GuiAction> buildGuiActions() {
        final List<AbstractPlayerAction> playerActions =
                playerActionService.getPlayerActions();

        return playerActions.stream()
                .map(this::toGuiAction)
                .collect(Collectors.toList());

    }

    @UiThread
    private GuiAction toGuiAction(final AbstractPlayerAction playerAction) {
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

                db.databaseWriteExecutor.execute(
                        new Runnable() {
                            @WorkerThread
                            @Override
                            public void run() {
                                db.runInTransaction(playerAction::narrateAndDo);
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
