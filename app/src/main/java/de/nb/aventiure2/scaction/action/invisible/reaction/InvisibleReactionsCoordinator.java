package de.nb.aventiure2.scaction.action.invisible.reaction;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.TAGESZEIT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public final class InvisibleReactionsCoordinator {
    private final AvDatabase db;
    private final StoryStateDao n;

    private final Map<GameObjectId, AbstractInvisibleReactions> allInvisibleReactions;

    public InvisibleReactionsCoordinator(final AvDatabase db) {
        this.db = db;
        n = db.storyStateDao();

        allInvisibleReactions = ImmutableMap.<GameObjectId, AbstractInvisibleReactions>builder()
                .put(SCHLOSSFEST, new SchlossfestReactions(db))
                .put(TAGESZEIT, new TageszeitReactions(db))
                .build();
    }

    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan timeElapsed = noTime();

        for (final AbstractInvisibleReactions reactions : allInvisibleReactions.values()) {
            timeElapsed = timeElapsed.plus(reactions
                    .onTimePassed(lastTime, now));
        }

        return timeElapsed;
    }
}
