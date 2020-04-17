package de.nb.aventiure2.data.world.room;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * All rooms
 */
public class Rooms {
    public static final GameObjectId SCHLOSS_VORHALLE = new GameObjectId(30_000);
    public static final GameObjectId SCHLOSS_VORHALLE_TISCH_BEIM_FEST = new GameObjectId(30_001);
    public static final GameObjectId DRAUSSEN_VOR_DEM_SCHLOSS = new GameObjectId(30_002);
    public static final GameObjectId IM_WALD_NAHE_DEM_SCHLOSS = new GameObjectId(30_003);
    public static final GameObjectId ABZWEIG_IM_WALD = new GameObjectId(30_004);
    public static final GameObjectId VOR_DER_HUETTE_IM_WALD = new GameObjectId(30_005);
    public static final GameObjectId HUETTE_IM_WALD = new GameObjectId(30_006);
    public static final GameObjectId BETT_IN_DER_HUETTE_IM_WALD = new GameObjectId(30_007);
    public static final GameObjectId HINTER_DER_HUETTE = new GameObjectId(30_008);
    public static final GameObjectId IM_WALD_BEIM_BRUNNEN = new GameObjectId(30_009);
    public static final GameObjectId UNTEN_IM_BRUNNEN = new GameObjectId(30_010);
    public static final GameObjectId WALDWILDNIS_HINTER_DEM_BRUNNEN = new GameObjectId(30_011);

    public static final List<GameObject> ALL;

    static {
        final RoomFactory f = new RoomFactory();

        ALL = ImmutableList.of(
                f.create(SCHLOSS_VORHALLE, ObjectLocationMode.EIN_TISCH),
                f.create(SCHLOSS_VORHALLE_TISCH_BEIM_FEST, ObjectLocationMode.HOLZTISCH),
                f.create(DRAUSSEN_VOR_DEM_SCHLOSS),
                f.create(IM_WALD_NAHE_DEM_SCHLOSS, ObjectLocationMode.WALDWEG),
                f.create(ABZWEIG_IM_WALD, ObjectLocationMode.WALDWEG),
                f.create(VOR_DER_HUETTE_IM_WALD, ObjectLocationMode.VOR_DER_HUETTE),
                f.create(HUETTE_IM_WALD, ObjectLocationMode.HOLZTISCH),
                f.create(BETT_IN_DER_HUETTE_IM_WALD, ObjectLocationMode.NEBEN_DIR_IM_BETT),
                f.create(HINTER_DER_HUETTE, ObjectLocationMode.UNTER_DEM_BAUM),
                f.create(IM_WALD_BEIM_BRUNNEN, ObjectLocationMode.GRAS_NEBEN_DEM_BRUNNEN),
                f.create(UNTEN_IM_BRUNNEN, ObjectLocationMode.AM_GRUNDE_DES_BRUNNENS),
                f.create(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                        ObjectLocationMode.MATSCHIGER_WALDBODENN)
        );
    }

    private Rooms() {
    }

    public static GameObject get(final GameObjectId id) {
        for (final GameObject room : ALL) {
            if (room.is(id)) {
                return room;
            }
        }

        throw new IllegalStateException("Unexpected game object ID: " + id);
    }
}
