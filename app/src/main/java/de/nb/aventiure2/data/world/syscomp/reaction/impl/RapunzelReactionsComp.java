package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * "Reaktionen" von Rapunzel, z.B. darauf, dass Zeit vergeht
 */
public class RapunzelReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, ITimePassedReactions {

    private final AbstractDescriptionComp descriptionComp;
    private final RapunzelStateComp stateComp;
    private final LocationComp locationComp;

    public RapunzelReactionsComp(final AvDatabase db,
                                 final World world,
                                 final AbstractDescriptionComp descriptionComp,
                                 final RapunzelStateComp stateComp,
                                 final LocationComp locationComp) {
        super(RAPUNZEL, db, world);
        this.descriptionComp = descriptionComp;
        this.stateComp = stateComp;
        this.locationComp = locationComp;
    }

    @Override
    public AvTimeSpan onLeave(final ILocatableGO locatable,
                              final ILocationGO from,
                              @Nullable final ILocationGO to) {
        return noTime();
    }

    @Override
    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final ILocationGO from,
                              final ILocationGO to) {
        return noTime();
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        return noTime();
    }
}