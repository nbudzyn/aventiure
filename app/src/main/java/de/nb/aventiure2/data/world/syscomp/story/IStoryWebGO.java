package de.nb.aventiure2.data.world.syscomp.story;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, dass Stories managet, also die kleinen
 * Geschichten / Märchen, die der Spieler erlebt. Beginnt neue Stories, speichert den
 * Stand, erzeugt Tipps, wenn der Benutzer in einer Story nicht weiterkommt etc.
 */
public interface IStoryWebGO extends IGameObject {
    @Nonnull
    public StoryWebComp storyWebComp();
}
