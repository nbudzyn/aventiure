package de.nb.aventiure2.scaction.action.invisible.reaction;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.invisible.InvisibleData;
import de.nb.aventiure2.data.world.invisible.Invisibles;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public final class InvisibleReactionsCoordinator {
    private final AvDatabase db;
    private final StoryStateDao n;

    private final Map<GameObjectId, AbstractInvisibleReactions> allInvisibleReactions;

    private final NullInvisibleReactions nullInvisibleReactions;

    public InvisibleReactionsCoordinator(final AvDatabase db,
                                         final Class<? extends IPlayerAction> scActionClass) {
        this.db = db;
        n = db.storyStateDao();

        allInvisibleReactions = ImmutableMap.<GameObjectId, AbstractInvisibleReactions>builder()
                .put(Invisibles.SCHLOSSFEST, new SchlossfestReactions(db, scActionClass))
                .put(Invisibles.TAGESZEIT, new TageszeitReactions(db, scActionClass))
                .build();

        nullInvisibleReactions = new NullInvisibleReactions(db, scActionClass);
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

    private AbstractInvisibleReactions getReactions(final InvisibleData invisibleData) {
        @Nullable final AbstractInvisibleReactions res =
                allInvisibleReactions.get(invisibleData.getGameObjectId());
        if (res != null) {
            return res;
        }

        return nullInvisibleReactions;
    }
}
