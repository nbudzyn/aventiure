package de.nb.aventiure2.data.world.syscomp.description.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionTriple;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;

/**
 * Einfache Implementierung von {@link AbstractDescriptionComp}: Das Game Object
 * (z.B. ein Objekt oder eine Kreatur) hat eine oder mehrere Beschreibungen.
 */
public class SimpleDescriptionComp extends AbstractDescriptionComp {
    private final DescriptionTriple descriptionTriple;

    public SimpleDescriptionComp(final CounterDao counterDao,
                                 final GameObjectId id,
                                 final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                                 final EinzelneSubstantivischePhrase normalDescriptionWhenKnown,
                                 final EinzelneSubstantivischePhrase shortDescriptionWhenKnown) {
        this(id, new DescriptionTriple(counterDao,
                descriptionAtFirstSight, normalDescriptionWhenKnown, shortDescriptionWhenKnown));
    }

    public SimpleDescriptionComp(final GameObjectId id,
                                 final DescriptionTriple descriptionTriple) {
        super(id);
        this.descriptionTriple = descriptionTriple;
    }

    @Override
    public final ImmutableList<EinzelneSubstantivischePhrase> altDescriptionsAtFirstSight() {
        return descriptionTriple.altAtFirstSight();
    }

    @Override
    public EinzelneSubstantivischePhrase getDescriptionAtFirstSight() {
        return descriptionTriple.getAtFirstSight();
    }

    @Override
    public final ImmutableList<EinzelneSubstantivischePhrase> altNormalDescriptionsWhenKnown() {
        return descriptionTriple.altNormalWhenKnown();
    }

    @Override
    public EinzelneSubstantivischePhrase getNormalDescriptionWhenKnown() {
        return descriptionTriple.getNormalWhenKnown();
    }

    @Override
    public ImmutableList<EinzelneSubstantivischePhrase> altShortDescriptionsWhenKnown() {
        return descriptionTriple.altShortWhenKnown();
    }

    @Override
    public EinzelneSubstantivischePhrase getShortDescriptionWhenKnown() {
        return descriptionTriple.getShortWhenKnown();
    }

    public DescriptionTriple getDescriptionTriple() {
        return descriptionTriple;
    }
}
