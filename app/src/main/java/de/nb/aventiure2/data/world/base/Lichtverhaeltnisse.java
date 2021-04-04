package de.nb.aventiure2.data.world.base;

import com.google.common.collect.ImmutableSet;

import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

public enum Lichtverhaeltnisse {
    HELL, DUNKEL;

    public static ImmutableSet<AbstractDescription<?>> altSCKommtNachDraussenInDunkelheit() {
        return alt().add(neuerSatz("Draußen ist es dunkel"))
                .schonLaenger()
                .build();
    }
}
