package de.nb.aventiure2.data.world.syscomp.storingplace;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;

/**
 * Component für ein {@link GameObject}: Das Game Object
 * (z.B. ein Raum) bietet die Möglichkeit, etwas abzulegen - und zwar an einem bestimmten Ort
 * (z.B. "auf dem Boden" oder "auf einem Tisch").
 */
public class StoringPlaceComp extends AbstractStatelessComponent {
    private final AvDatabase db;
    private final StoringPlaceType locationMode;

    /**
     * Ob dieses Game Object (z.B. dieser Raum) unabhängig von der
     * Tageszeit etc. beleuchtet ist.
     */
    private final boolean dauerhaftBeleuchtet;

    public StoringPlaceComp(final GameObjectId id,
                            final AvDatabase db,
                            final boolean dauerhaftBeleuchtet) {
        this(id, db, StoringPlaceType.BODEN, dauerhaftBeleuchtet);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final AvDatabase db,
                            final StoringPlaceType locationMode,
                            final boolean dauerhaftBeleuchtet) {
        super(id);
        this.db = db;
        this.locationMode = locationMode;
        this.dauerhaftBeleuchtet = dauerhaftBeleuchtet;
    }

    public StoringPlaceType getLocationMode() {
        return locationMode;
    }

    public Lichtverhaeltnisse getLichtverhaeltnisseInside() {
        // STORY Der SC oder ein anderes Game Object, dass sich IN
        //   diesem IHasStoringPlaceGO befindet, könnte eine Fackel dabei haben.

        if (isDauerhaftBeleuchtet()) {
            // STORY Es könnte Räume geben, die auch tagsüber dunkel sind, z.B.
            //  weil sie keine Fenster haben. Man bräuchte quasi drei Möglichkeiten:
            //  - dauerhaft beleuchtet
            //  - dauerheft dunkel
            //  - tageszeitenabhaengig beleuchtet
            return HELL;
        }

        return db.dateTimeDao().now().getTageszeit().getLichtverhaeltnisseDraussen();
    }

    private boolean isDauerhaftBeleuchtet() {
        return dauerhaftBeleuchtet;
    }
}
