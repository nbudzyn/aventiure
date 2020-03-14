package de.nb.aventiure2.data.storystate;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Android Room DAO for {@link StoryState}s.
 */
@Dao
public abstract class StoryStateDao {
    public void add(final StoryStateBuilder text) {
        add(text.build());
    }

    @Transaction
    public void add(final StoryState text) {
        checkNotNull(text, "text is null");

        @Nullable final StoryState currentStoryState = getStoryStateSync();

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

    @Query("SELECT * from StoryState")
    public abstract LiveData<StoryState> getStoryState();

    @Query("SELECT * from StoryState")
    public abstract StoryState getStoryStateSync();
}
