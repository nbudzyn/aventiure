package de.nb.aventiure2.data.world.syscomp.reaction.system;

import androidx.annotation.Nullable;

import java.util.Comparator;

import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IResponder;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Sortiert {@link IResponder}s in der Reihenfolge, in der Reaktionen
 * auf
 * {@link de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions#onEnter(ILocatableGO, ILocationGO, ILocationGO)}
 * ausgegeben werden sollen.
 * <p>
 * Note: This comparator imposes orderings that are inconsistent with equals.
 */
public class ReactionOrderComparator implements Comparator<IResponder> {
    @Override
    public int compare(final IResponder one, final IResponder other) {
        if (one.is(WETTER) != other.is(WETTER)) {
            // Erst Wetter und Tageszeiten
            if (one.is(WETTER)) {
                return -1;
            }

            return 1;
        }

        if (one.is(SPIELER_CHARAKTER) != other.is(SPIELER_CHARAKTER)) {
            // Dann Hunger, Müdigkeit etc.
            if (one.is(SPIELER_CHARAKTER)) {
                return -1;
            }

            return 1;
        }

        if ((one instanceof ILivingBeingGO) != (other instanceof ILivingBeingGO)) {
            // Erst unbelebte Dinge beschreiben, dann belebte (der SC wird eher auf die NSCs
            // reagieren).
            if (one instanceof ILivingBeingGO) {
                return 1;
            }

            return -1;
        }

        // Sonst ist es uns egal

        return 0;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        return getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
