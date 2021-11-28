package de.nb.aventiure2.data.world.syscomp.description.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionTriple;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;

public abstract class MultiDescriptionComp extends AbstractDescriptionComp {
    MultiDescriptionComp(final GameObjectId gameObjectId) {
        super(gameObjectId);
    }

    @Override
    @NonNull
    public ImmutableList<EinzelneSubstantivischePhrase> altDescriptionsAtFirstSight() {
        return chooseDescriptionTriple().altAtFirstSight();
    }

    @Override
    public EinzelneSubstantivischePhrase getDescriptionAtFirstSight() {
        return chooseDescriptionTriple().getAtFirstSight();
    }

    @Override
    @NonNull
    public ImmutableList<EinzelneSubstantivischePhrase> altNormalDescriptionsWhenKnown() {
        return chooseDescriptionTriple().altNormalWhenKnown();
    }

    @Override
    public EinzelneSubstantivischePhrase getNormalDescriptionWhenKnown() {
        return chooseDescriptionTriple().getNormalWhenKnown();
    }

    @Override
    @NonNull
    public ImmutableList<EinzelneSubstantivischePhrase> altShortDescriptionsWhenKnown() {
        return chooseDescriptionTriple().altShortWhenKnown();
    }

    @Override
    public EinzelneSubstantivischePhrase getShortDescriptionWhenKnown() {
        return chooseDescriptionTriple().getShortWhenKnown();
    }

    abstract DescriptionTriple chooseDescriptionTriple();
}
