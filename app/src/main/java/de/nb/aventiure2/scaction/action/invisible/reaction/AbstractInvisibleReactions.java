package de.nb.aventiure2.scaction.action.invisible.reaction;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.action.base.reaction.AbstractReactions;

abstract class AbstractInvisibleReactions extends AbstractReactions {
    public AbstractInvisibleReactions(final AvDatabase db,
                                      final Class<? extends IPlayerAction> scActionClass) {
        super(db, scActionClass);
    }

    public abstract AvTimeSpan onTimePassed(AvDateTime lastTime, AvDateTime now);
}
