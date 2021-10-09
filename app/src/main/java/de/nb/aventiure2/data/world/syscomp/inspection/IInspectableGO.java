package de.nb.aventiure2.data.world.syscomp.inspection;

import javax.annotation.Nonnull;

import de.nb.aventiure2.data.world.base.IGameObject;

/**
 * Game Object, dass der SC untersuchen kann.
 */
public interface IInspectableGO extends IGameObject {
    @Nonnull
    InspectionComp inspectionComp();
}