package de.nb.aventiure2.data.world.syscomp.description.impl;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;

/**
 * Einfache Implementierung von {@link AbstractDescriptionComp}: Das Game Object
 * (z.B. ein Objekt oder eine Kreatur) hat eine oder mehrere Beschreibungen.
 */
public class SimpleDescriptionComp extends AbstractDescriptionComp {
    private final DescriptionTriple descriptionTriple;

    public SimpleDescriptionComp(final GameObjectId id,
                                 final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                                 final EinzelneSubstantivischePhrase normalDescriptionWhenKnown,
                                 final EinzelneSubstantivischePhrase shortDescriptionWhenKnown) {
        this(id, new DescriptionTriple(
                descriptionAtFirstSight, normalDescriptionWhenKnown, shortDescriptionWhenKnown));
    }

    private SimpleDescriptionComp(final GameObjectId id,
                                  final DescriptionTriple descriptionTriple) {
        super(id);
        this.descriptionTriple = descriptionTriple;
    }

    @Override
    public EinzelneSubstantivischePhrase getDescriptionAtFirstSight() {
        return descriptionTriple.getDescriptionAtFirstSight();
    }

    @Override
    public EinzelneSubstantivischePhrase getNormalDescriptionWhenKnown() {
        return descriptionTriple.getNormalDescriptionWhenKnown();
    }

    @Override
    public EinzelneSubstantivischePhrase getShortDescriptionWhenKnown() {
        return descriptionTriple.getShortDescriptionWhenKnown();
    }
}
