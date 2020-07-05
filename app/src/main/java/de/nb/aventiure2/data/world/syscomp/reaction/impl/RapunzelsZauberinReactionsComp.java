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
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;

/**
 * "Reaktionen" von Rapunzels Zauberin, z.B. darauf, dass Zeit vergeht
 */
public class RapunzelsZauberinReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, ITimePassedReactions {

    private final AbstractDescriptionComp descriptionComp;
    private final RapunzelsZauberinStateComp stateComp;
    private final LocationComp locationComp;

    public RapunzelsZauberinReactionsComp(final AvDatabase db,
                                          final World world,
                                          final AbstractDescriptionComp descriptionComp,
                                          final RapunzelsZauberinStateComp stateComp,
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
        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCEnter(from, to);
        }

        return noTime();
    }

    private AvTimeSpan onSCEnter(final ILocationGO from, final ILocationGO to) {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            return onSCEnter_VorDemAltenTurm(from);
        }

        return noTime();
    }

    private AvTimeSpan onSCEnter_VorDemAltenTurm(final ILocationGO from) {
        switch (stateComp.getState()) {
            case BESUCHT_RAPUNZEL_REGELMAESSIG:
                return onSCEnter_VorDemAltenTurm_WillRapunzelBesuchen(from);
            default:
                return noTime();
        }
    }

    private AvTimeSpan onSCEnter_VorDemAltenTurm_WillRapunzelBesuchen(final ILocationGO from) {
        if (!world.isOrHasRecursiveLocation(from, IM_WALD_NAHE_DEM_SCHLOSS)) {
            return noTime();
        }

        // STORY Zauberin besucht Rapunzel
        return noTime();
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        // STORY Zauberin besucht Rapunzel
        return noTime();
    }
}