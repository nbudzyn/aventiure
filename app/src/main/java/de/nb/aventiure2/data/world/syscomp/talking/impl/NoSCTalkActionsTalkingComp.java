package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjectService;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;

/**
 * Component for a {@link GameObject}: Das Game Object kann mit einem anderen
 * im Gespräch sein (dann ist diese Beziehung reflexiv), der Spieler kann allerdings
 * keine Redebeiträge an das {@link ITalkerGO} richten.
 */
public class NoSCTalkActionsTalkingComp extends AbstractTalkingComp {
    public NoSCTalkActionsTalkingComp(final GameObjectId gameObjectId,
                                      final AvDatabase db,
                                      final GameObjectService gos) {
        super(gameObjectId, db, gos);
    }

    @Override
    protected Iterable<? extends SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        // Der Spieler kann sich nicht mit sich selbst unterhalten.
        return ImmutableList.of();
    }
}
