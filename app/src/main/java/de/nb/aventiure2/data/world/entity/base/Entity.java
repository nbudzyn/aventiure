package de.nb.aventiure2.data.world.entity.base;

import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * An entity in the world (not the player character, not a room).
 */
public abstract class Entity extends GameObject {
    private final Nominalphrase descriptionAtFirstSight;

    private final Nominalphrase normalDescriptionWhenKnown;

    private final Nominalphrase shortDescriptionWhenKnown;

    public Entity(final GameObjectId id,
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

    public Nominalphrase getNormalDescriptionWhenKnown() {
        return normalDescriptionWhenKnown;
    }

    public Nominalphrase getShortDescriptionWhenKnown() {
        return shortDescriptionWhenKnown;
    }
}
