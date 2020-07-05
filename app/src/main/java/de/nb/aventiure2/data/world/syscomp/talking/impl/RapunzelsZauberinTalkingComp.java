package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelsZauberinStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;

import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZELS_ZAUBERIN;

/**
 * Component for {@link World#RAPUNZELS_ZAUBERIN}: Der Spieler
 * kann versuchen, mit Rapunzels Zauberin ein Gespräch zu führen.
 */
public class RapunzelsZauberinTalkingComp extends AbstractTalkingComp {
    private final AbstractDescriptionComp descriptionComp;
    private final RapunzelsZauberinStateComp stateComp;

    public RapunzelsZauberinTalkingComp(final AvDatabase db,
                                        final World world,
                                        final AbstractDescriptionComp descriptionComp,
                                        final RapunzelsZauberinStateComp stateComp) {
        super(RAPUNZELS_ZAUBERIN, db, world);
        this.descriptionComp = descriptionComp;
        this.stateComp = stateComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        return ImmutableList.of();
    }
}
