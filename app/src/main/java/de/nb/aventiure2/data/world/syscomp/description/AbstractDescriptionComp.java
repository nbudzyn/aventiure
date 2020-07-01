package de.nb.aventiure2.data.world.syscomp.description;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * Component für ein {@link GameObject}: Das Game Object
 * (z.B. ein Objekt oder eine Kreatur) hat eine oder mehrere Beschreibungen.
 */
public abstract class AbstractDescriptionComp extends AbstractStatelessComponent {
    protected AbstractDescriptionComp(
            final GameObjectId gameObjectId) {
        super(gameObjectId);
    }

    public abstract Nominalphrase getDescriptionAtFirstSight();

    public Nominalphrase getDescription(final boolean known,
                                        final boolean shortIfKnown) {
        if (known) {
            return getDescriptionWhenKnown(shortIfKnown);
        }

        return getDescriptionAtFirstSight();
    }

    public Nominalphrase getDescriptionWhenKnown(final boolean shortIfKnown) {
        return shortIfKnown ?
                getShortDescriptionWhenKnown() :
                getNormalDescriptionWhenKnown();
    }

    public abstract Nominalphrase getNormalDescriptionWhenKnown();

    public abstract Nominalphrase getShortDescriptionWhenKnown();


    @NonNull
    @Override
    public String toString() {
        return getNormalDescriptionWhenKnown().nom();
    }
}
