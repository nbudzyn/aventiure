package de.nb.aventiure2.data.world.syscomp.description.impl;

import androidx.annotation.NonNull;

import java.util.Objects;

import de.nb.aventiure2.german.base.Nominalphrase;

public class DescriptionTriple {
    @NonNull
    private final Nominalphrase descriptionAtFirstSight;
    @NonNull
    private final Nominalphrase normalDescriptionWhenKnown;
    @NonNull
    private final Nominalphrase shortDescriptionWhenKnown;

    public DescriptionTriple(@NonNull final Nominalphrase descriptionAtFirstSight,
                             @NonNull final Nominalphrase descriptionWhenKnown) {
        this(descriptionAtFirstSight, descriptionWhenKnown, descriptionWhenKnown);
    }

    public DescriptionTriple(@NonNull final Nominalphrase descriptionAtFirstSight,
                             @NonNull final Nominalphrase normalDescriptionWhenKnown,
                             @NonNull final Nominalphrase shortDescriptionWhenKnown) {
        this.descriptionAtFirstSight = descriptionAtFirstSight;
        this.normalDescriptionWhenKnown = normalDescriptionWhenKnown;
        this.shortDescriptionWhenKnown = shortDescriptionWhenKnown;
    }

    @NonNull
    public Nominalphrase getDescriptionAtFirstSight() {
        return descriptionAtFirstSight;
    }

    @NonNull
    public Nominalphrase getNormalDescriptionWhenKnown() {
        return normalDescriptionWhenKnown;
    }

    @NonNull
    public Nominalphrase getShortDescriptionWhenKnown() {
        return shortDescriptionWhenKnown;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DescriptionTriple that = (DescriptionTriple) o;
        return descriptionAtFirstSight.equals(that.descriptionAtFirstSight) &&
                normalDescriptionWhenKnown.equals(that.normalDescriptionWhenKnown) &&
                shortDescriptionWhenKnown.equals(that.shortDescriptionWhenKnown);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(descriptionAtFirstSight, normalDescriptionWhenKnown,
                        shortDescriptionWhenKnown);
    }
}