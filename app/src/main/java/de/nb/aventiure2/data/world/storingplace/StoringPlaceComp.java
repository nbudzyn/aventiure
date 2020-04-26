package de.nb.aventiure2.data.world.storingplace;

import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Component für ein {@link GameObject}: Das Game Object
 * (z.B. ein Raum) bietet die Möglichkeit, etwas abzulegen - und zwar an einem bestimmten Ort
 * (z.B. "auf dem Boden" oder "auf einem Tisch").
 */
public class StoringPlaceComp extends AbstractStatelessComponent {
    private final StoringPlaceType locationMode;

    public StoringPlaceComp(final GameObjectId id) {
        this(id, StoringPlaceType.BODEN);
    }

    public StoringPlaceComp(final GameObjectId id, final StoringPlaceType locationMode) {
        super(id);
        this.locationMode = locationMode;
    }

    public StoringPlaceType getLocationMode() {
        return locationMode;
    }
}
