package de.nb.aventiure2.data.world.syscomp.talking;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;

public abstract class RapunzelFrageMitAntworten extends AbstractFrageMitAntworten {
    protected CounterDao counterDao;
    protected final RapunzelStateComp stateComp;
    protected final FeelingsComp feelingsComp;

    protected RapunzelFrageMitAntworten(
            final GameObjectId gameObjectId,
            final CounterDao counterDao,
            final Narrator n, final World world, final RapunzelStateComp stateComp,
            final FeelingsComp feelingsComp, final ITalkContext talkContext) {
        super(gameObjectId, n, world, talkContext);
        this.counterDao = counterDao;
        this.stateComp = stateComp;
        this.feelingsComp = feelingsComp;
    }

    public abstract void forgetAll();
}
