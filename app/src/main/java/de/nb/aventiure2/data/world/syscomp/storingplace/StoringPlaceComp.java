package de.nb.aventiure2.data.world.syscomp.storingplace;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.base.SpatialConnectionData;

import static de.nb.aventiure2.data.world.base.Lichtverhaeltnisse.HELL;

/**
 * Component für ein {@link GameObject}: Das Game Object
 * (z.B. ein Raum) bietet die Möglichkeit, etwas abzulegen - und zwar an einem bestimmten Ort
 * (z.B. "auf dem Boden" oder "auf einem Tisch").
 */
public class StoringPlaceComp extends AbstractStatelessComponent {
    private final AvDatabase db;

    private final StoringPlaceType locationMode;

    @Nullable
    private final SpatialConnectionData spatialConnectionInData;
    @Nullable
    private final SpatialConnectionData spatialConnectionOutData;

    /**
     * Ob dieses Game Object (z.B. dieser Raum) unabhängig von der
     * Tageszeit etc. beleuchtet ist.
     */
    private final boolean dauerhaftBeleuchtet;

    public StoringPlaceComp(final GameObjectId id,
                            final AvDatabase db,
                            final StoringPlaceType locationMode,
                            final boolean dauerhaftBeleuchtet) {
        this(id, db, locationMode, dauerhaftBeleuchtet, null, null);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final AvDatabase db,
                            final StoringPlaceType locationMode,
                            final boolean dauerhaftBeleuchtet,
                            @Nullable final SpatialConnectionData spatialConnectionInData,
                            @Nullable final SpatialConnectionData spatialConnectionOutData) {
        super(id);
        this.db = db;
        this.locationMode = locationMode;
        this.dauerhaftBeleuchtet = dauerhaftBeleuchtet;
        this.spatialConnectionInData = spatialConnectionInData;
        this.spatialConnectionOutData = spatialConnectionOutData;
    }

    public StoringPlaceType getLocationMode() {
        return locationMode;
    }

    /**
     * Gibt die Lichtverhältnisse an diesem Ort zurück (z.B. in diesem Raum,
     * auf diesem Tisch, in der Tasche o.Ä.)
     */
    public Lichtverhaeltnisse getLichtverhaeltnisse() {
        // STORY Der SC oder ein anderes Game Object, dass sich an
        //   dieser Location befindet, könnte eine Fackel dabei haben.

        if (isDauerhaftBeleuchtet()) {
            // STORY Es könnte Räume oder Locations geben, die auch tagsüber dunkel sind, z.B.
            //  weil sie keine Fenster haben, oder Taschen etc.. Man bräuchte quasi
            //  drei Möglichkeiten:
            //  - dauerhaft beleuchtet
            //  - dauerhaft dunkel
            //  - tageszeitenabhaengig beleuchtet
            return HELL;
        }

        return db.nowDao().now().getTageszeit().getLichtverhaeltnisseDraussen();
    }

    private boolean isDauerhaftBeleuchtet() {
        return dauerhaftBeleuchtet;
    }

    @Nullable
    public SpatialConnectionData getSpatialConnectionInData() {
        return spatialConnectionInData;
    }

    @Nullable
    public SpatialConnectionData getSpatialConnectionOutData() {
        return spatialConnectionOutData;
    }
}
