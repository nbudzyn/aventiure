package de.nb.aventiure2.data.world.invisible;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static de.nb.aventiure2.data.world.invisible.InvisibleState.BEGONNEN;
import static de.nb.aventiure2.data.world.invisible.InvisibleState.NOCH_NICHT_BEGONNEN;
import static de.nb.aventiure2.data.world.invisible.InvisibleStateList.sl;

/**
 * All {@link Invisible}s in the world
 */
public class Invisibles {
    public static final List<Invisible> ALL =
            ImmutableList.of(
                    new Invisible(Invisible.Key.SCHLOSSFEST,
                            sl(NOCH_NICHT_BEGONNEN, BEGONNEN))
            );
}
