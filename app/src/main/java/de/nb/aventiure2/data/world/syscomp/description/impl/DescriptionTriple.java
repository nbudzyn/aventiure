package de.nb.aventiure2.data.world.syscomp.description.impl;

import androidx.annotation.NonNull;

import java.util.Objects;

import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;

public class DescriptionTriple {
    @NonNull
    private final EinzelneSubstantivischePhrase descriptionAtFirstSight;
    @NonNull
    private final EinzelneSubstantivischePhrase normalDescriptionWhenKnown;
    @NonNull
    private final EinzelneSubstantivischePhrase shortDescriptionWhenKnown;

    DescriptionTriple(@NonNull final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                      @NonNull final EinzelneSubstantivischePhrase descriptionWhenKnown) {
        this(descriptionAtFirstSight, descriptionWhenKnown, descriptionWhenKnown);
    }

    DescriptionTriple(@NonNull final EinzelneSubstantivischePhrase descriptionAtFirstSight,
                      @NonNull final EinzelneSubstantivischePhrase normalDescriptionWhenKnown,
                      @NonNull final EinzelneSubstantivischePhrase shortDescriptionWhenKnown) {
        this.descriptionAtFirstSight = descriptionAtFirstSight;
        this.normalDescriptionWhenKnown = normalDescriptionWhenKnown;
        this.shortDescriptionWhenKnown = shortDescriptionWhenKnown;
    }

    @NonNull
    EinzelneSubstantivischePhrase getDescriptionAtFirstSight() {
        return descriptionAtFirstSight;
    }

    @NonNull
    EinzelneSubstantivischePhrase getNormalDescriptionWhenKnown() {
        return normalDescriptionWhenKnown;
    }

    @NonNull
    EinzelneSubstantivischePhrase getShortDescriptionWhenKnown() {
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