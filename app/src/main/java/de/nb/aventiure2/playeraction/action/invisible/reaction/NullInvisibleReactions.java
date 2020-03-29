package de.nb.aventiure2.playeraction.action.invisible.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

public class NullInvisibleReactions extends AbstractInvisibleReactions {
    public NullInvisibleReactions(final AvDatabase db,
                                  final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        return noTime();
    }
}
