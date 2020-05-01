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
public class DescriptionComp extends AbstractStatelessComponent {
    private final Nominalphrase descriptionAtFirstSight;

    private final Nominalphrase normalDescriptionWhenKnown;

    private final Nominalphrase shortDescriptionWhenKnown;

    public DescriptionComp(final GameObjectId id,
                           final Nominalphrase descriptionAtFirstSight,
                           final Nominalphrase normalDescriptionWhenKnown,
                           final Nominalphrase shortDescriptionWhenKnown) {
        super(id);
        this.descriptionAtFirstSight = descriptionAtFirstSight;
        this.normalDescriptionWhenKnown = normalDescriptionWhenKnown;
        this.shortDescriptionWhenKnown = shortDescriptionWhenKnown;
    }

    public Nominalphrase getDescriptionAtFirstSight() {
        return descriptionAtFirstSight;
    }

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

    public Nominalphrase getNormalDescriptionWhenKnown() {
        return normalDescriptionWhenKnown;
    }

    public Nominalphrase getShortDescriptionWhenKnown() {
        return shortDescriptionWhenKnown;
    }

    @NonNull
    @Override
    public String toString() {
        return normalDescriptionWhenKnown.nom();
    }
}
