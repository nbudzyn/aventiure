package de.nb.aventiure2.data.world.room;

import com.google.common.collect.ImmutableList;

import java.util.List;

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

    public static final List<AvRoom> ALL =
            ImmutableList.of(
                    new AvRoom(SCHLOSS_VORHALLE, ObjectLocationMode.EIN_TISCH),
                    new AvRoom(SCHLOSS_VORHALLE_TISCH_BEIM_FEST, ObjectLocationMode.HOLZTISCH),
                    new AvRoom(DRAUSSEN_VOR_DEM_SCHLOSS),
                    new AvRoom(IM_WALD_NAHE_DEM_SCHLOSS, ObjectLocationMode.WALDWEG),
                    new AvRoom(ABZWEIG_IM_WALD, ObjectLocationMode.WALDWEG),
                    new AvRoom(VOR_DER_HUETTE_IM_WALD, ObjectLocationMode.VOR_DER_HUETTE),
                    new AvRoom(HUETTE_IM_WALD, ObjectLocationMode.HOLZTISCH),
                    new AvRoom(BETT_IN_DER_HUETTE_IM_WALD, ObjectLocationMode.NEBEN_DIR_IM_BETT),
                    new AvRoom(HINTER_DER_HUETTE, ObjectLocationMode.UNTER_DEM_BAUM),
                    new AvRoom(IM_WALD_BEIM_BRUNNEN, ObjectLocationMode.GRAS_NEBEN_DEM_BRUNNEN),
                    new AvRoom(UNTEN_IM_BRUNNEN, ObjectLocationMode.AM_GRUNDE_DES_BRUNNENS),
                    new AvRoom(WALDWILDNIS_HINTER_DEM_BRUNNEN,
                            ObjectLocationMode.MATSCHIGER_WALDBODENN)
            );

    private Rooms() {
    }

    public static AvRoom get(final GameObjectId id) {
        for (final AvRoom room : ALL) {
            if (room.is(id)) {
                return room;
            }
        }

        throw new IllegalStateException("Unexpected game object ID: " + id);
    }
}
