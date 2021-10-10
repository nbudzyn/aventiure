package de.nb.aventiure2.data.world.syscomp.inspection;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;

/**
 * Component für ein {@link GameObject}: Der SC kann dieses Game Object untersuchen.
 */
public class InspectionComp extends AbstractStatelessComponent
        implements
        // Mixins
        IWorldLoaderMixin, IWorldDescriptionMixin {
    private final World world;
    protected final TimeTaker timeTaker;
    private final ImmutableList<IInspection> inspections;

    public InspectionComp(final GameObjectId gameObjectId,
                          final TimeTaker timeTaker,
                          final World world,
                          final IInspection inspection) {
        this(gameObjectId, timeTaker, world, ImmutableList.of(inspection));
    }

    private InspectionComp(final GameObjectId gameObjectId,
                           final TimeTaker timeTaker,
                           final World world,
                           final Collection<IInspection> inspections) {
        super(gameObjectId);
        this.timeTaker = timeTaker;
        this.world = world;
        this.inspections = ImmutableList.copyOf(inspections);
    }

    @Override
    public World getWorld() {
        return world;
    }

    public ImmutableList<IInspection> getInspections() {
        return inspections;
    }
}
