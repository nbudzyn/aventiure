package de.nb.aventiure2.activity.main.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;
import de.nb.aventiure2.playeraction.PlayerActionService;

import static de.nb.aventiure2.data.database.AvDatabase.getDatabase;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<StoryState> storyText;
    private final LiveData<List<AbstractPlayerAction>> playerActions;

    public MainViewModel(final Application application) {
        super(application);
        final AvDatabase db = getDatabase(application);

        final PlayerActionService playerActionService = new PlayerActionService(application);

        // TODO Only publish new values AFTER EACH ACTION!
        storyText = db.storyStateDao().getStoryState();
        playerActions = playerActionService.getPlayerActions();
    }

    public LiveData<StoryState> getStoryText() {
        return storyText;
    }

    public LiveData<List<AbstractPlayerAction>> getPlayerActions() {
        return playerActions;
    }
}
