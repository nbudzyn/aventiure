package de.nb.aventiure2.playeraction.action.invisible.reaction;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.invisible.Invisible;
import de.nb.aventiure2.data.world.invisible.InvisibleData;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public final class InvisibleReactionsCoordinator {
    private final AvDatabase db;
    private final StoryStateDao n;

    private final Map<Invisible.Key, AbstractInvisibleReactions> allInvisibleReactions;

    private final NullInvisibleReactions nullInvisibleReactions;

    public InvisibleReactionsCoordinator(final AvDatabase db,
                                         final Class<? extends IPlayerAction> playerActionClass) {
        this.db = db;
        n = db.storyStateDao();

        allInvisibleReactions = ImmutableMap.<Invisible.Key, AbstractInvisibleReactions>builder()
                .put(Invisible.Key.SCHLOSSFEST, new SchlossfestReactions(db, playerActionClass))
                .build();

        nullInvisibleReactions = new NullInvisibleReactions(db, playerActionClass);
    }

    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan timeElapsed = noTime();

        for (final InvisibleData invisibleData : getAllInvisibles()) {
            timeElapsed = timeElapsed.plus(getReactions(invisibleData)
                    .onTimePassed(lastTime, now));
        }

        return timeElapsed;
    }

    private List<InvisibleData> getAllInvisibles() {
        return db.invisibleDataDao().getAll();
    }

    private StoryState getCurrentStoryState() {
        return n.getStoryState();
    }

    private AbstractInvisibleReactions getReactions(final InvisibleData Invisible) {
        @Nullable final AbstractInvisibleReactions res = allInvisibleReactions.get(Invisible.getKey());
        if (res != null) {
            return res;
        }

        return nullInvisibleReactions;
    }
}
