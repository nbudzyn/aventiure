package de.nb.aventiure2.data.world.syscomp.description.impl;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;

public abstract class MultiDescriptionComp extends AbstractDescriptionComp {
    MultiDescriptionComp(final GameObjectId gameObjectId) {
        super(gameObjectId);
    }

    @Override
    public EinzelneSubstantivischePhrase getDescriptionAtFirstSight() {
        return chooseDescriptionTriple().getDescriptionAtFirstSight();
    }

    @Override
    public EinzelneSubstantivischePhrase getNormalDescriptionWhenKnown() {
        return chooseDescriptionTriple().getNormalDescriptionWhenKnown();
    }

    @Override
    public EinzelneSubstantivischePhrase getShortDescriptionWhenKnown() {
        return chooseDescriptionTriple().getShortDescriptionWhenKnown();
    }

    protected abstract DescriptionTriple chooseDescriptionTriple();
}
