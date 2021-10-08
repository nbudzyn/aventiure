package de.nb.aventiure2.data.world.gameobject;


import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Mixin with helper methods for descriptions, based on a {@link World}.
 */
public interface IWorldDescriptionMixin {
    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine kurze
     * Beschreibung des Game Objects.
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die mysteriöse Lampe" oder "die Lampe" zurück.
     */
    default SubstantivischePhrase anaph(final IDescribableGO describableGO) {
        return getWorld().anaph(describableGO);
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine kurze
     * Beschreibung des Game Objects.
     * <br/>
     * Es muss sich um eine {@link IDescribableGO} handeln!
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die Lampe" zurück.
     */
    default SubstantivischePhrase anaph(final GameObjectId describableId) {
        return getWorld().anaph(describableId);
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine
     * Beschreibung des Game Objects.
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die mysteriöse Lampe" zurück.
     */
    default SubstantivischePhrase anaph(
            final GameObjectId describableId,
            final boolean descShortIfKnown) {
        return getWorld().anaph(describableId, descShortIfKnown);
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine
     * Beschreibung des Game Objects.
     * <br/>
     * Beispiel 1: "Du hebst die Lampe auf..." - jetzt ist ein anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "sie" zurück.
     * <br/>
     * Beispiel 2: "Du zündest das Feuer an..." - jetzt ist <i>kein</i> anaphorischer Bezug
     * auf die Lampe möglich und diese Methode gibt "die mysteriöse Lampe" zurück.
     */
    default SubstantivischePhrase anaph(
            final IDescribableGO describableGO,
            final boolean descShortIfKnown) {
        return getWorld().anaph(describableGO, descShortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    default EinzelneSubstantivischePhrase getDescription(final GameObjectId gameObjectId) {
        return getWorld().getDescription(gameObjectId);
    }

    /**
     * Gibt eine (evtl. auch etwas längere) Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * der Spieler das Game Object schon kennt oder nicht.
     */
    default EinzelneSubstantivischePhrase getDescription(final IDescribableGO gameObject) {
        return getWorld().getDescription(gameObject);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    default EinzelneSubstantivischePhrase getDescription(final GameObjectId gameObjectId,
                                                         final boolean shortIfKnown) {
        return getWorld().getDescription(gameObjectId, shortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    default EinzelneSubstantivischePhrase getDescription(final IDescribableGO gameObject,
                                                         final boolean shortIfKnown) {
        return getWorld().getDescription(gameObject, shortIfKnown);
    }

    World getWorld();
}
