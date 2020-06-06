package de.nb.aventiure2.data.world.gameobjects.player;

import androidx.annotation.NonNull;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.alive.AliveComp;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.feelings.IFeelingBeingGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.memory.IHasMemoryGO;
import de.nb.aventiure2.data.world.syscomp.memory.MemoryComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.ScAutomaticReactionsComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;

public class SpielerCharakter extends GameObject
        implements ILocatableGO, ILocationGO, IFeelingBeingGO,
        ITalkerGO, IHasMemoryGO, ILivingBeingGO, IResponder {
    private final LocationComp locationComp;
    private final StoringPlaceComp storingPlaceComp;
    private final FeelingsComp feelingsComp;
    private final AbstractTalkingComp talkingComp;
    private final MemoryComp memoryComp;
    private final AliveComp aliveComp;
    private final ScAutomaticReactionsComp reactionsComp;

    SpielerCharakter(final GameObjectId id,
                     final LocationComp locationComp,
                     final StoringPlaceComp storingPlaceComp,
                     final FeelingsComp feelingsComp,
                     final MemoryComp memoryComp,
                     final AbstractTalkingComp talkingComp,
                     final ScAutomaticReactionsComp reactionsComp) {
        super(id);
        // Jede Komponente muss registiert werden!
        this.locationComp = addComponent(locationComp);
        this.storingPlaceComp = addComponent(storingPlaceComp);
        this.feelingsComp = addComponent(feelingsComp);
        this.memoryComp = addComponent(memoryComp);
        aliveComp = addComponent(new AliveComp(id));
        this.talkingComp = addComponent(talkingComp);
        this.reactionsComp = reactionsComp;
    }

    @Nonnull
    @Override
    public LocationComp locationComp() {
        return locationComp;
    }

    @Nonnull
    @Override
    public StoringPlaceComp storingPlaceComp() {
        return storingPlaceComp;
    }

    @Nonnull
    @Override
    public FeelingsComp feelingsComp() {
        return feelingsComp;
    }

    @Nonnull
    @Override
    public AbstractTalkingComp talkingComp() {
        return talkingComp;
    }

    @Nonnull
    @Override
    public MemoryComp memoryComp() {
        return memoryComp;
    }

    @Nonnull
    @Override
    public AliveComp aliveComp() {
        return aliveComp;
    }

    @NonNull
    @Override
    public ScAutomaticReactionsComp reactionsComp() {
        return reactionsComp;
    }
}
