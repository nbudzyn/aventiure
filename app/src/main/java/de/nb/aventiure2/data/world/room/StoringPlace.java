package de.nb.aventiure2.data.world.room;

import de.nb.aventiure2.data.world.base.AbstractComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Component für ein {@link GameObject}: Das Game Object
 * (z.B. ein Raum) bietet die Möglichkeit, etwas abzulegen - und zwar an einem bestimmten Ort
 * (z.B. "auf dem Boden" oder "auf einem Tisch").
 */
public class StoringPlace extends AbstractComponent {
    private final ObjectLocationMode locationMode;

    public StoringPlace(final GameObjectId id) {
        this(id, ObjectLocationMode.BODEN);
    }

    StoringPlace(final GameObjectId id, final ObjectLocationMode locationMode) {
        super(id);
        this.locationMode = locationMode;
    }

    public ObjectLocationMode getLocationMode() {
        return locationMode;
    }
}
