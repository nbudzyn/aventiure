package de.nb.aventiure2.data.world.invisible;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.world.time.AvDateTime;

import static de.nb.aventiure2.data.world.invisible.Invisible.Key.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.invisible.Invisible.Key.TAGESZEIT;
import static de.nb.aventiure2.data.world.invisible.InvisibleState.BEGONNEN;
import static de.nb.aventiure2.data.world.invisible.InvisibleState.NOCH_NICHT_BEGONNEN;
import static de.nb.aventiure2.data.world.invisible.InvisibleState.NORMAL;
import static de.nb.aventiure2.data.world.invisible.InvisibleStateList.sl;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;

/**
 * All {@link Invisible}s in the world
 */
public class Invisibles {
    public static final List<Invisible> ALL =
            ImmutableList.of(
                    new Invisible(SCHLOSSFEST,
                            sl(NOCH_NICHT_BEGONNEN, BEGONNEN)),
                    new Invisible(TAGESZEIT,
                            sl(NORMAL))
            );

    public static final AvDateTime SCHLOSSFEST_BEGINN_DATE_TIME =
            new AvDateTime(2,
                    oClock(5, 30));

    public static final String COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN =
            "Invisibles_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN";
}
