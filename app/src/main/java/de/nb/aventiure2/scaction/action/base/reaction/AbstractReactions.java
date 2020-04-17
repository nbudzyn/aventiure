package de.nb.aventiure2.scaction.action.base.reaction;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableCollection;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.Tageszeit;

public abstract class AbstractReactions {
    protected final AvDatabase db;
    protected final StoryStateDao n;
    protected final Class<? extends IPlayerAction> scActionClass;

    public AbstractReactions(final AvDatabase db,
                             final Class<? extends IPlayerAction> scActionClass) {
        this.db = db;
        n = db.storyStateDao();
        this.scActionClass = scActionClass;
    }

    protected StoryStateBuilder alt(
            final ImmutableCollection.Builder<StoryStateBuilder> alternatives) {
        return alt(alternatives.build());
    }

    private StoryStateBuilder alt(final Collection<StoryStateBuilder> alternatives) {
        return alt(alternatives.toArray(new StoryStateBuilder[alternatives.size()]));
    }

    protected StoryStateBuilder alt(final StoryStateBuilder... alternatives) {
        return n.chooseNextFrom(alternatives);
    }

    protected StoryStateBuilder t(
            @NonNull final StoryState.StructuralElement startsNew,
            @NonNull final String text) {

        return StoryStateBuilder.t(scActionClass, startsNew, text)
                // Sensible default - caller may override this setting
                .letzterRaum(db.playerLocationDao().getPlayerLocation().getRoom());
    }

    protected Lichtverhaeltnisse getLichtverhaeltnisse(final AvRoom.Key room) {
        return Lichtverhaeltnisse.getLichtverhaeltnisse(getTageszeit(), room);
    }

    protected Tageszeit getTageszeit() {
        return db.dateTimeDao().now().getTageszeit();
    }
}
