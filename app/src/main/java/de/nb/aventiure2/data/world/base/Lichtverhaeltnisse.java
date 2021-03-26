package de.nb.aventiure2.data.world.base;

import com.google.common.collect.ImmutableSet;

import java.util.function.Supplier;

import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

public enum Lichtverhaeltnisse {
    HELL, DUNKEL;

    public static Supplier<Lichtverhaeltnisse> DAUERHAFT_HELL = () -> HELL;
    public static Supplier<Lichtverhaeltnisse> DAUERHAFT_DUNKEL = () -> DUNKEL;
    
    public static ImmutableSet<AbstractDescription<?>> altSCKommtNachDraussenInDunkelheit() {
        return alt().add(neuerSatz("Draußen ist es dunkel"))
                .schonLaenger()
                .build();
    }
}
