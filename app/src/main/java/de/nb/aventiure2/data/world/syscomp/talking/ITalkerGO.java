package de.nb.aventiure2.data.world.syscomp.talking;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, das mit einem anderen {@link ITalkerGO} im Gespräch sein kann
 */
public interface ITalkerGO extends IGameObject {
    @Nonnull
    TalkingComp talkingComp();
}