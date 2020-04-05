package de.nb.aventiure2.data.storystate;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Android Room DAO for {@link StoryState}s.
 */
@Dao
public abstract class StoryStateDao {
    public void add(final StoryStateBuilder text) {
        add(text.build());
    }

    public void add(final StoryState text) {
        checkNotNull(text, "text is null");

        @Nullable final StoryState currentStoryState = getStoryState();

        if (currentStoryState == null) {
            insert(text);
            return;
        }

        delete(currentStoryState);

        final StoryState res = currentStoryState.prependTo(text);
        insert(res);
    }

    @Delete
    abstract void delete(StoryState storyState);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(StoryState playerLocation);

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    public StoryStateBuilder chooseNextFrom(final StoryStateBuilder... alternatives) {
        return alternatives[chooseNextIndexFrom(alternatives)];
    }

    /**
     * Wählt einen {@link StoryStateBuilder} aus den Alternativen und gibt den Indes zurück -
     * versucht dabei vor allem, Wiederholgungen mit dem unmittelbar zuvor geschriebenen
     * Story-Text zu vermeiden.
     */
    public int chooseNextIndexFrom(final StoryStateBuilder... alternatives) {
        checkArgument(alternatives.length > 0,
                "No alternatives");

        final String currentText = getStoryState().getText();

        int bestIndex = -1;
        float bestScore = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < alternatives.length; i++) {
            final StoryStateBuilder alternative = alternatives[i];
            final float score =
                    TextAdditionEvaluator
                            .evaluateAddition(currentText, alternative.build().getText());
            if (score > bestScore) {
                bestScore = score;
                bestIndex = i;
            }
        }

        return bestIndex;
    }

    @Query("SELECT * from StoryState")
    public abstract StoryState getStoryState();
}
