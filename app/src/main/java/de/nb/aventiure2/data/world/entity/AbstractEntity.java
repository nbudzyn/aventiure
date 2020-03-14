package de.nb.aventiure2.data.world.entity;

import de.nb.aventiure2.german.Nominalphrase;

/**
 * An entity in the world (not the player character, not a room).
 */
public class AbstractEntity {
    private final Nominalphrase descriptionAtFirstSight;

    private final Nominalphrase normalDescriptionWhenKnown;

    private final Nominalphrase shortDescriptionWhenKnown;

    public AbstractEntity(final Nominalphrase descriptionAtFirstSight,
                          final Nominalphrase normalDescriptionWhenKnown,
                          final Nominalphrase shortDescriptionWhenKnown) {
        this.descriptionAtFirstSight = descriptionAtFirstSight;
        this.normalDescriptionWhenKnown = normalDescriptionWhenKnown;
        this.shortDescriptionWhenKnown = shortDescriptionWhenKnown;
    }

    public Nominalphrase getDescriptionAtFirstSight() {
        return descriptionAtFirstSight;
    }

    public Nominalphrase getNormalDescriptionWhenKnown() {
        return normalDescriptionWhenKnown;
    }

    public Nominalphrase getShortDescriptionWhenKnown() {
        return shortDescriptionWhenKnown;
    }
}
