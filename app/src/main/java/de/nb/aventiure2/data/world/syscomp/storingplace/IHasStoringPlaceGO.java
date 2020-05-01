package de.nb.aventiure2.data.world.syscomp.storingplace;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object (z.B. ein Raum) das die Möglichkeit bietet, etwas abzulegen - und zwar an einem
 * bestimmten Ort (z.B. "auf dem Boden" oder "auf einem Tisch").
 */
public interface IHasStoringPlaceGO extends IGameObject {
    Lichtverhaeltnisse getLichtverhaeltnisseInside();

    @Nonnull
    public StoringPlaceComp storingPlaceComp();
}
