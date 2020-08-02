package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;

/**
 * Component for {@link World#RAPUNZEL}: Der Spieler
 * kann mit Rapunzel im Gespräch sein (dann auch umgekehrt).
 */
public class RapunzelTalkingComp extends AbstractTalkingComp {
    private final AbstractDescriptionComp descriptionComp;
    private final RapunzelStateComp stateComp;

    public RapunzelTalkingComp(final AvDatabase db,
                               final World world,
                               final AbstractDescriptionComp descriptionComp,
                               final RapunzelStateComp stateComp) {
        super(RAPUNZEL, db, world);
        this.descriptionComp = descriptionComp;
        this.stateComp = stateComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        return ImmutableList.of();
    }

    public void reactToRapunzelruf(final GameObjectId gameObjectId) {
        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription(true);
            // extraTime = extraTime.plus(n.add(
//                    neuerSatz( "TODO", mins(1))
//                            .phorikKandidat(desc, getGameObjectId())));
        }

        // STORY Rapunzel hört auf zu singen, lässt die Haare herunter, Zauberin steigt hinauf
//                + "Gleich darauf fallen aus dem kleinen Fenster "
//                + "lange, goldene Haarzöpfe herab, sicher zwanzig Ellen tief herunter. "
    }
}
