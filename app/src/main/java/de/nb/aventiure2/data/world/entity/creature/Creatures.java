package de.nb.aventiure2.data.world.entity.creature;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.world.base.GameObjectId;

import static de.nb.aventiure2.data.world.entity.creature.CreatureState.AUFMERKSAM;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.AUF_DEM_WEG_ZUM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.entity.creature.CreatureStateList.sl;
import static de.nb.aventiure2.data.world.room.Rooms.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.room.Rooms.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;

/**
 * All creatures in the world
 */
public class Creatures {
    public static final GameObjectId FROSCHPRINZ = new GameObjectId(20_001);
    public static final GameObjectId SCHLOSSWACHE = new GameObjectId(20_000);

    public static final List<Creature> ALL =
            ImmutableList.of(
                    new Creature(SCHLOSSWACHE,
                            np(F, "eine Schlosswache mit langer Hellebarde",
                                    "einer Schlosswache mit langer Hellebarde"),
                            np(F, "die Schlosswache mit ihrer langen Hellebarde",
                                    "der Schlosswache mit ihrer langen Hellebarde"),
                            np(F, "die Schlosswache",
                                    "der Schlosswache"),
                            SCHLOSS_VORHALLE,
                            sl(UNAUFFAELLIG, AUFMERKSAM
                            )),
                    new Creature(FROSCHPRINZ,
                            np(M, "ein dicker, hässlicher Frosch",
                                    "einem dicken, hässlichen Frosch",
                                    "einen dicken, hässlichen Frosch"),
                            np(M, "der hässliche Frosch",
                                    "dem hässlichen Frosch",
                                    "den hässlichen Frosch"),
                            np(M, "der Frosch",
                                    "dem Frosch",
                                    "den Frosch"),
                            IM_WALD_BEIM_BRUNNEN,
                            sl(UNAUFFAELLIG, HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                                    HAT_NACH_BELOHNUNG_GEFRAGT, HAT_FORDERUNG_GESTELLT,
                                    AUF_DEM_WEG_ZUM_BRUNNEN_UM_DINGE_HERAUSZUHOLEN,
                                    ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS,
                                    ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN,
                                    AUF_DEM_WEG_ZUM_SCHLOSSFEST,
                                    HAT_HOCHHEBEN_GEFORDERT
                            ))
                    // STORY Wölfe hetzen Spieler nachts
            );

    public static Creature get(final GameObjectId id) {
        for (final Creature creature : ALL) {
            if (creature.is(id)) {
                return creature;
            }
        }

        throw new IllegalStateException("Unexpected game object ID: " + id);
    }
}
