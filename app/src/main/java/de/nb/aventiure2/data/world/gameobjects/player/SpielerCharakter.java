package de.nb.aventiure2.data.world.gameobjects.player;

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
import de.nb.aventiure2.data.world.syscomp.memory.Memory;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.StoringPlaceComp;

public class SpielerCharakter extends GameObject
        implements ILocatableGO, IHasStoringPlaceGO, IFeelingBeingGO, IHasMemoryGO, ILivingBeingGO {
    private final LocationComp locationComp;
    private final StoringPlaceComp storingPlaceComp;
    private final FeelingsComp feelingsComp;
    private final Memory memory;
    private final AliveComp alive;

    public SpielerCharakter(final GameObjectId id,
                            final LocationComp locationComp,
                            final StoringPlaceComp storingPlaceComp,
                            final FeelingsComp feelingsComp,
                            final Memory memory) {
        super(id);
        // Jede Komponente muss registiert werden!
        this.locationComp = addComponent(locationComp);
        this.storingPlaceComp = addComponent(storingPlaceComp);
        this.feelingsComp = addComponent(feelingsComp);
        this.memory = addComponent(memory);
        alive = addComponent(new AliveComp(id));
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
    public Memory memoryComp() {
        return memory;
    }

    @Nonnull
    @Override
    public AliveComp aliveComp() {
        return alive;
    }
}
