package de.nb.aventiure2.data.world.syscomp.inspection;

import com.google.common.collect.ImmutableCollection;

import de.nb.aventiure2.data.world.base.IGameObjectDescriptionMixin;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.german.description.TimedDescription;


/**
 * Eine <code>IInspection</code> entspricht einer
 * {@link de.nb.aventiure2.scaction.AbstractScAction},
 * die der Benutzer (auf einem einzigen Game Object) durchführen kann. Die
 * <code>IInspection</code> kann zu verschiedenen, alternativen Beschreibungen führen.
 */
public interface IInspection extends
        // Mixins
        IWorldLoaderMixin, IWorldDescriptionMixin, IGameObjectDescriptionMixin {
    IInspectableGO getInspectable();

    String getActionName();

    ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions();
}
