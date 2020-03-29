package de.nb.aventiure2.data.world.entity.base;

import de.nb.aventiure2.data.world.base.AbstractKeyData;
import de.nb.aventiure2.german.base.DescribableAsDeklinierbarePhrase;
import de.nb.aventiure2.german.base.KasusOderPraepositionalkasus;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * Changeable Data for some entity in the world.
 */
public abstract class AbstractEntityData<K> extends AbstractKeyData<K>
        implements DescribableAsDeklinierbarePhrase {
    /**
     * Gibt eine Beschreibung der Entity in diesem
     * Kasus ("die Tür") oder Präspositionalkasus ("mit der Tür") zurück.
     *
     * @param shortIfKnown wenn die Entity dem Spieler bereits bekannt ist, dann
     *                     wird eine kurze Beschreibung gewählt ("die Kugel" statt
     *                     "die goldene Kugel")
     */
    @Override
    public String im(final KasusOderPraepositionalkasus kasusOderPraepositionalkasus,
                     final boolean shortIfKnown) {
        return getDescription(shortIfKnown).im(kasusOderPraepositionalkasus);
    }

    /**
     * Returns the normal description as Nominativ.
     */
    @Override
    public String nom() {
        return nom(false);
    }

    /**
     * Returns the description as Nominativ - short, if the entity is already knwon to the player
     */
    @Override
    public String nom(final boolean shortIfKnown) {
        return getDescription(shortIfKnown).nom();
    }

    /**
     * Returns the normal description as Dativ
     */
    @Override
    public String dat() {
        return dat(false);
    }

    /**
     * Returns the description as Dativ - short, if the entity is already knwon to the player
     */
    @Override
    public String dat(final boolean shortIfKnown) {
        return getDescription(shortIfKnown).dat();
    }

    /**
     * Returns the normal description as Akkusativ.
     */
    @Override
    public String akk() {
        return akk(false);
    }

    /**
     * Returns the description as Akkusativ - short, if the entity is already knwon to the player
     */
    @Override
    public String akk(final boolean shortIfKnown) {
        return getDescription(shortIfKnown).akk();
    }

    @Override
    public abstract Nominalphrase getDescription(final boolean shortIfKnown);
}