package de.nb.aventiure2.data.world.syscomp.description.impl;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * Einfache Implementierung von {@link AbstractDescriptionComp}: Das Game Object
 * (z.B. ein Objekt oder eine Kreatur) hat eine oder mehrere Beschreibungen.
 */
public class SimpleDescriptionComp extends AbstractDescriptionComp {
    private final DescriptionTriple descriptionTriple;

    public SimpleDescriptionComp(final GameObjectId id,
                                 final Nominalphrase descriptionAtFirstSight,
                                 final Nominalphrase normalDescriptionWhenKnown,
                                 final Nominalphrase shortDescriptionWhenKnown) {
        this(id, new DescriptionTriple(
                descriptionAtFirstSight, normalDescriptionWhenKnown, shortDescriptionWhenKnown));
    }

    public SimpleDescriptionComp(final GameObjectId id,
                                 final DescriptionTriple descriptionTriple) {
        super(id);
        this.descriptionTriple = descriptionTriple;
    }

    @Override
    public Nominalphrase getDescriptionAtFirstSight() {
        return descriptionTriple.getDescriptionAtFirstSight();
    }

    @Override
    public Nominalphrase getNormalDescriptionWhenKnown() {
        return descriptionTriple.getNormalDescriptionWhenKnown();
    }

    @Override
    public Nominalphrase getShortDescriptionWhenKnown() {
        return descriptionTriple.getShortDescriptionWhenKnown();
    }
}
