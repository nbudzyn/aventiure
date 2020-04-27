package de.nb.aventiure2.data.world.syscomp.feelings;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object mit Bedürfnissen und Gefühlen
 */
public interface IFeelingBeingGO extends IGameObject {
    @Nonnull
    FeelingsComp feelingsComp();
}