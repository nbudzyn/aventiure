package de.nb.aventiure2.data.world.syscomp.talking;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, das mit einem anderen {@link ITalkerGO} im Gespräch sein kann
 */
public interface ITalkerGO<C extends AbstractTalkingComp> extends IGameObject {
    @Nonnull
    C talkingComp();
}