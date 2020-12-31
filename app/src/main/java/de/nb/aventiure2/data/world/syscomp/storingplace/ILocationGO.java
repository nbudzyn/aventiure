package de.nb.aventiure2.data.world.syscomp.storingplace;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object (z.B. ein Raum) das die Möglichkeit bietet, etwas abzulegen / zu platzieren - und zwar
 * an einem bestimmten Platz (z.B. "auf dem Boden" oder "auf einem Tisch"). Die Dinge, die man dort
 * platziert, müssen {@link de.nb.aventiure2.data.world.syscomp.location.ILocatableGO}s sein.
 */
public interface ILocationGO extends IGameObject {
    @Nonnull
    StoringPlaceComp storingPlaceComp();
}
