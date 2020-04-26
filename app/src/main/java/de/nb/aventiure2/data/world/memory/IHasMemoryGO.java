package de.nb.aventiure2.data.world.memory;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object (z.B. der Spieler-Charakter), das sich an Dinge (z.B. an Orte oder
 * Kreaturen) erinnert.
 */
public interface IHasMemoryGO extends IGameObject {
    @Nonnull
    public Memory memoryComp();
}