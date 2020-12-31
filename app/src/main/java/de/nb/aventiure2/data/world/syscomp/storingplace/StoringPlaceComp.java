package de.nb.aventiure2.data.world.syscomp.storingplace;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.time.TimeTaker;
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
    private final TimeTaker timeTaker;
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

    // FIXME: Ob der Raum "draußen" ist (-> Tageszeiten / Wetterphönomene...)

    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            final StoringPlaceType locationMode,
                            final boolean dauerhaftBeleuchtet) {
        this(id, timeTaker, locationMode, dauerhaftBeleuchtet, null, null);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final TimeTaker timeTaker,
                            final StoringPlaceType locationMode,
                            final boolean dauerhaftBeleuchtet,
                            @Nullable final SpatialConnectionData spatialConnectionInData,
                            @Nullable final SpatialConnectionData spatialConnectionOutData) {
        super(id);
        this.timeTaker = timeTaker;
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
        // FIXME Der SC oder ein anderes Game Object, dass sich an
        //   dieser Location befindet, könnte eine Fackel dabei haben.

        if (isDauerhaftBeleuchtet()) {
            // FIXME Es könnte Räume oder Locations geben (die der Spieler betreten kann und)
            //  die auch tagsüber dunkel sind, z.B.
            //  weil sie keine Fenster haben, oder Taschen etc.. Man bräuchte quasi
            //  drei Möglichkeiten:
            //  - dauerhaft beleuchtet
            //  - dauerhaft dunkel
            //  - tageszeitenabhaengig beleuchtet
            return HELL;
        }

        return timeTaker.now().getTageszeit().getLichtverhaeltnisseDraussen();
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
