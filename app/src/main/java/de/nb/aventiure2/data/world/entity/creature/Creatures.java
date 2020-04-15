package de.nb.aventiure2.data.world.entity.creature;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.world.room.AvRoom;

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
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.base.NumerusGenus.M;

/**
 * All creatures in the world
 */
public class Creatures {
    public static final List<Creature> ALL =
            ImmutableList.of(
                    new Creature(Creature.Key.SCHLOSSWACHE,
                            np(F, "eine Schlosswache mit langer Hellebarde",
                                    "einer Schlosswache mit langer Hellebarde"),
                            np(F, "die Schlosswache mit ihrer langen Hellebarde",
                                    "der Schlosswache mit ihrer langen Hellebarde"),
                            np(F, "die Schlosswache",
                                    "der Schlosswache"),
                            AvRoom.SCHLOSS_VORHALLE,
                            sl(UNAUFFAELLIG, AUFMERKSAM
                            )),
                    new Creature(Creature.Key.FROSCHPRINZ,
                            np(M, "ein dicker, hässlicher Frosch",
                                    "einem dicken, hässlichen Frosch",
                                    "einen dicken, hässlichen Frosch"),
                            np(M, "der hässliche Frosch",
                                    "dem hässlichen Frosch",
                                    "den hässlichen Frosch"),
                            np(M, "der Frosch",
                                    "dem Frosch",
                                    "den Frosch"),
                            AvRoom.IM_WALD_BEIM_BRUNNEN,
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
}
