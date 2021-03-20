package de.nb.aventiure2.data.world.base;

import com.google.common.collect.ImmutableSet;

import java.util.function.Supplier;

import de.nb.aventiure2.german.description.AbstractDescription;

import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

public enum Lichtverhaeltnisse {
    HELL("ins Helle"), DUNKEL("in die Dunkelheit");

    public static Supplier<Lichtverhaeltnisse> DAUERHAFT_HELL = () -> HELL;
    public static Supplier<Lichtverhaeltnisse> DAUERHAFT_DUNKEL = () -> DUNKEL;

    /**
     * Beschreibt, wohin der SC sich bewegt, wenn er sich in diese Lichtverhältnisse
     * bewegt, also etwas wie "in die Dunkelheit".
     */
    private final String wohin;

    Lichtverhaeltnisse(final String wohin) {
        this.wohin = wohin;
    }

    public static ImmutableSet<AbstractDescription<?>> altSCKommtNachDraussenInDunkelheit() {
        return alt().add(neuerSatz("Draußen ist es dunkel")).build();
    }


    /**
     * Gibt eine Beschreibung zurück, wohin der SC sich bewegt, wenn er sich in diese
     * Lichtverhältnisse bewegt, also etwas wie "in die Dunkelheit".
     */
    public String getWohin() {
        return wohin;
    }
}
