package de.nb.aventiure2.data.world.syscomp.description;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;

/**
 * Component für ein {@link GameObject}: Das Game Object
 * (z.B. ein Objekt oder eine Kreatur) hat eine oder mehrere Beschreibungen.
 */
public abstract class AbstractDescriptionComp extends AbstractStatelessComponent {
    protected AbstractDescriptionComp(
            final GameObjectId gameObjectId) {
        super(gameObjectId);
    }

    public abstract ImmutableList<EinzelneSubstantivischePhrase> altDescriptionsAtFirstSight();

    public abstract EinzelneSubstantivischePhrase getDescriptionAtFirstSight();

    public final ImmutableList<EinzelneSubstantivischePhrase> altDescriptions(
            final boolean known,
            final boolean shortIfKnown) {
        if (known) {
            return altDescriptionsWhenKnown(shortIfKnown);
        }

        return altDescriptionsAtFirstSight();
    }

    public final EinzelneSubstantivischePhrase getDescription(
            final boolean known,
            final boolean shortIfKnown) {
        if (known) {
            return getDescriptionWhenKnown(shortIfKnown);
        }

        return getDescriptionAtFirstSight();
    }

    protected final ImmutableList<EinzelneSubstantivischePhrase> altDescriptionsWhenKnown(
            final boolean shortIfKnown) {
        return shortIfKnown ?
                altShortDescriptionsWhenKnown() :
                altNormalDescriptionsWhenKnown();
    }

    protected final EinzelneSubstantivischePhrase getDescriptionWhenKnown(
            final boolean shhort) {
        return shhort ?
                getShortDescriptionWhenKnown() :
                getNormalDescriptionWhenKnown();
    }

    public abstract ImmutableList<EinzelneSubstantivischePhrase> altNormalDescriptionsWhenKnown();

    public abstract EinzelneSubstantivischePhrase getNormalDescriptionWhenKnown();

    public abstract ImmutableList<EinzelneSubstantivischePhrase> altShortDescriptionsWhenKnown();

    public abstract EinzelneSubstantivischePhrase getShortDescriptionWhenKnown();

    @NonNull
    @Override
    public String toString() {
        return getNormalDescriptionWhenKnown().nomStr();
    }
}
