package de.nb.aventiure2.data.world.syscomp.reaction.interfaces;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.IReactions;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

/**
 * Reactions to a game object moving from one place to another (normally).
 * <p>
 * Covers actions like:
 * <ul>
 *     <li>Someone walks from one place to an adjacent one
 *     <li>An Object is taken by someone
 *     <li>An Object laid down by someone
 *     <li>An Object is thrown by someone and has fallen down
 *     <li>An Object is thrown by someone and caught again (<code>from</code> equals <code>to</code>}.
 *     <li>An Object is created (<code>from</code> is null)
 *     <li>An Object is destroyed (<code>to</code> is null)
 * </ul>
 */
public interface IMovementReactions extends IReactions {
    /**
     * The <code>locatable</code> leaves a place for some other place.
     * (This is called at the beginning of the movement.)
     */
    AvTimeSpan onLeave(ILocatableGO locatable,
                       IHasStoringPlaceGO from, @Nullable IHasStoringPlaceGO to);

    /**
     * The <code>locatable</code> enters a place (after having left some other place).
     * (This is called at the end of the movement.)
     */
    AvTimeSpan onEnter(ILocatableGO locatable,
                       @Nullable IHasStoringPlaceGO from, IHasStoringPlaceGO to);
}
