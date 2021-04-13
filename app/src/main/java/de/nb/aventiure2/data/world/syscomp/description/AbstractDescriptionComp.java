package de.nb.aventiure2.data.world.syscomp.description;

import androidx.annotation.NonNull;

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

    public abstract EinzelneSubstantivischePhrase getDescriptionAtFirstSight();

    public EinzelneSubstantivischePhrase getDescription(final boolean known,
                                                        final boolean shortIfKnown) {
        if (known) {
            return getDescriptionWhenKnown(shortIfKnown);
        }

        return getDescriptionAtFirstSight();
    }

    private EinzelneSubstantivischePhrase getDescriptionWhenKnown(final boolean shortIfKnown) {
        return shortIfKnown ?
                getShortDescriptionWhenKnown() :
                getNormalDescriptionWhenKnown();
    }

    public abstract EinzelneSubstantivischePhrase getNormalDescriptionWhenKnown();

    protected abstract EinzelneSubstantivischePhrase getShortDescriptionWhenKnown();


    @NonNull
    @Override
    public String toString() {
        return getNormalDescriptionWhenKnown().nomStr();
    }
}
