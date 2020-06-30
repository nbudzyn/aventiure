package de.nb.aventiure2.data.world.syscomp.storingplace;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjectService;

import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;

/**
 * Component für ein {@link GameObject}: Das Game Object
 * (z.B. ein Raum) bietet die Möglichkeit, etwas abzulegen - und zwar an einem bestimmten Ort
 * (z.B. "auf dem Boden" oder "auf einem Tisch").
 */
public class StoringPlaceComp extends AbstractStatelessComponent {
    private final AvDatabase db;
    private final GameObjectService gos;
    private final StoringPlaceType locationMode;

    /**
     * Ob dieses Game Object (z.B. dieser Raum) unabhängig von der
     * Tageszeit etc. beleuchtet ist.
     */
    private final boolean dauerhaftBeleuchtet;

    public StoringPlaceComp(final GameObjectId id,
                            final AvDatabase db,
                            final GameObjectService gos,
                            final boolean dauerhaftBeleuchtet) {
        this(id, db, gos, StoringPlaceType.BODEN, dauerhaftBeleuchtet);
    }

    public StoringPlaceComp(final GameObjectId id,
                            final AvDatabase db,
                            final GameObjectService gos,
                            final StoringPlaceType locationMode,
                            final boolean dauerhaftBeleuchtet) {
        super(id);
        this.db = db;
        this.gos = gos;
        this.locationMode = locationMode;
        this.dauerhaftBeleuchtet = dauerhaftBeleuchtet;
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
}
