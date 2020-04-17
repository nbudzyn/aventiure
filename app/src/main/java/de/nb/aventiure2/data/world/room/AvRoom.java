package de.nb.aventiure2.data.world.room;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.world.base.AbstractGameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;

import static de.nb.aventiure2.data.world.room.AvRoom.Key.ABZWEIG_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.HINTER_DER_HUETTE;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.IM_WALD_NAHE_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.SCHLOSS_VORHALLE_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.VOR_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.WALDWILDNIS_HINTER_DEM_BRUNNEN;

/**
 * A room in the world
 */
public class AvRoom extends AbstractGameObject {

    public enum Key {
        SCHLOSS_VORHALLE(30_000),
        SCHLOSS_VORHALLE_TISCH_BEIM_FEST(30_001),
        DRAUSSEN_VOR_DEM_SCHLOSS(30_002),
        IM_WALD_NAHE_DEM_SCHLOSS(30_003),
        ABZWEIG_IM_WALD(30_004),
        VOR_DER_HUETTE_IM_WALD(30_005),
        HUETTE_IM_WALD(30_006),
        BETT_IN_DER_HUETTE_IM_WALD(30_007),
        HINTER_DER_HUETTE(30_008),
        IM_WALD_BEIM_BRUNNEN(30_009),
        UNTEN_IM_BRUNNEN(30_010),
        WALDWILDNIS_HINTER_DEM_BRUNNEN(30_011);

        private final GameObjectId gameObjectId;

        private Key(final int gameObjectId) {
            this(new GameObjectId(gameObjectId));
        }

        Key(final GameObjectId gameObjectId) {
            this.gameObjectId = gameObjectId;
        }

        public GameObjectId getGameObjectId() {
            return gameObjectId;
        }
    }

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

    private final AvRoom.Key key;

    private final ObjectLocationMode locationMode;

    public static AvRoom get(final AvRoom.Key key) {
        for (final AvRoom room : ALL) {
            if (room.key == key) {
                return room;
            }
        }

        throw new IllegalStateException("Unexpected key: " + key);
    }

    private AvRoom(final Key key) {
        this(key, ObjectLocationMode.BODEN);

    }

    private AvRoom(final Key key, final ObjectLocationMode locationMode) {
        super(key.getGameObjectId());
        this.key = key;
        this.locationMode = locationMode;
    }

    public ObjectLocationMode getLocationMode() {
        return locationMode;
    }

    public AvRoom.Key getKey() {
        return key;
    }
}
