package de.nb.aventiure2.data.world.entity;

import de.nb.aventiure2.german.Nominalphrase;

/**
 * Changeable Data for some entity in the world.
 */
public abstract class AbstractEntityData {
    /**
     * Returns the normal description as Nominativ.
     */
    public String nom() {
        return nom(false);
    }

    /**
     * Returns the description as Nominativ - short, if the entity is already knwon to the player
     */
    public String nom(final boolean shortIfKnown) {
        return getDescription(shortIfKnown).nom();
    }

    /**
     * Returns the normal description as Dativ
     */
    public String dat() {
        return dat(false);
    }

    /**
     * Returns the description as Dativ - short, if the entity is already knwon to the player
     */
    public String dat(final boolean shortIfKnown) {
        return getDescription(shortIfKnown).dat();
    }

    /**
     * Returns the normal description as Akkusativ.
     */
    public String akk() {
        return akk(false);
    }

    /**
     * Returns the description as Akkusativ - short, if the entity is already knwon to the player
     */
    public String akk(final boolean shortIfKnown) {
        return getDescription(shortIfKnown).akk();
    }

    public abstract Nominalphrase getDescription(final boolean shortIfKnown);
}