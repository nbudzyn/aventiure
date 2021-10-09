package de.nb.aventiure2.data.world.syscomp.inspection;

import com.google.common.collect.ImmutableCollection;

import de.nb.aventiure2.german.description.TimedDescription;

public interface IInspection {
    IInspectableGO getInspectable();

    String getActionName();

    ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions();
}
