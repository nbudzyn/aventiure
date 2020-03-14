package de.nb.aventiure2.data.world.creature;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.creature.CreatureStateList.sm;
import static de.nb.aventiure2.german.Nominalphrase.np;
import static de.nb.aventiure2.german.NumerusGenus.M;

/**
 * All creatures in the world
 */
public class Creatures {
    public static final List<Creature> ALL =
            ImmutableList.of(
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
                            sm(UNAUFFAELLIG, HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                                    HAT_NACH_BELOHNUNG_GEFRAGT, HAT_FORDERUNG_GESTELLT))
            );
}
