package de.nb.aventiure2.data.world.gameobject.player;

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
import de.nb.aventiure2.data.world.syscomp.mentalmodel.IHasMentalModelGO;
import de.nb.aventiure2.data.world.syscomp.mentalmodel.MentalModelComp;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.ScAutomaticReactionsComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.NoSCTalkActionsTalkingComp;
import de.nb.aventiure2.data.world.syscomp.waiting.IWaitingGO;
import de.nb.aventiure2.data.world.syscomp.waiting.WaitingComp;

public class SpielerCharakter extends GameObject
        implements ILocatableGO, ILocationGO,
        IHasMentalModelGO, IWaitingGO, IFeelingBeingGO,
        ITalkerGO<NoSCTalkActionsTalkingComp>, IHasMemoryGO, ILivingBeingGO, IResponder {
    private final LocationComp locationComp;
    private final MentalModelComp mentalModelComp;
    private final StoringPlaceComp storingPlaceComp;
    private final WaitingComp waitingComp;
    private final FeelingsComp feelingsComp;
    private final NoSCTalkActionsTalkingComp talkingComp;
    private final MemoryComp memoryComp;
    private final AliveComp aliveComp;
    private final ScAutomaticReactionsComp reactionsComp;

    SpielerCharakter(final GameObjectId id,
                     final LocationComp locationComp,
                     final MentalModelComp mentalModelComp,
                     final StoringPlaceComp storingPlaceComp,
                     final WaitingComp waitingComp,
                     final FeelingsComp feelingsComp,
                     final MemoryComp memoryComp,
                     final NoSCTalkActionsTalkingComp talkingComp,
                     final ScAutomaticReactionsComp reactionsComp) {
        super(id);
        // Jede Komponente muss registiert werden!
        this.locationComp = addComponent(locationComp);
        this.mentalModelComp = addComponent(mentalModelComp);
        this.storingPlaceComp = addComponent(storingPlaceComp);
        this.waitingComp = addComponent(waitingComp);
        this.feelingsComp = addComponent(feelingsComp);
        this.memoryComp = addComponent(memoryComp);
        aliveComp = addComponent(new AliveComp(id));
        this.talkingComp = addComponent(talkingComp);
        this.reactionsComp = addComponent(reactionsComp);
    }

    @Nonnull
    @Override
    public LocationComp locationComp() {
        return locationComp;
    }

    @Nonnull
    @Override
    public MentalModelComp mentalModelComp() {
        return mentalModelComp;
    }

    @Nonnull
    @Override
    public StoringPlaceComp storingPlaceComp() {
        return storingPlaceComp;
    }

    @Nonnull
    @Override
    public WaitingComp waitingComp() {
        return waitingComp;
    }

    @Nonnull
    @Override
    public FeelingsComp feelingsComp() {
        return feelingsComp;
    }

    @Nonnull
    @Override
    public NoSCTalkActionsTalkingComp talkingComp() {
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
