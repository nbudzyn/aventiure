package de.nb.aventiure2.data.world.gameobject;


import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

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
    default EinzelneSubstantivischePhrase anaph(
            final ITextContext textContext,
            final IDescribableGO describableGO,
            final PossessivDescriptionVorgabe descPossessivDescriptionVorgabe) {
        return getWorld().anaph(textContext, describableGO, descPossessivDescriptionVorgabe);
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
    default EinzelneSubstantivischePhrase anaph(
            final ITextContext textContext,
            final GameObjectId describableId,
            final PossessivDescriptionVorgabe descPossessivDescriptionVorgabe) {
        return getWorld().anaph(textContext, describableId, descPossessivDescriptionVorgabe);
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
    default EinzelneSubstantivischePhrase anaph(
            final ITextContext textContext,
            final GameObjectId describableId,
            final PossessivDescriptionVorgabe descPossessivDescriptionVorgabe,
            final boolean descShortIfKnown) {
        return getWorld().anaph(textContext, describableId, descPossessivDescriptionVorgabe,
                descShortIfKnown);
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
    default EinzelneSubstantivischePhrase anaph(
            final ITextContext textContext,
            final IDescribableGO describableGO,
            final PossessivDescriptionVorgabe descPossessivDescriptionVorgabe,
            final boolean descShortIfKnown) {
        return getWorld().anaph(textContext, describableGO, descPossessivDescriptionVorgabe,
                descShortIfKnown);
    }

    /**
     * Gibt eine Beschribung für das Game Object zurück.
     */
    default SubstantivischePhrase getDescription(
            final ITextContext textContext,
            final GameObjectId gameObjectId,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe) {
        return getWorld().getDescription(
                textContext, gameObjectId, possessivDescriptionVorgabe);
    }

    /**
     * Gibt eine Beschribung für das Game Object zurück.
     */
    default SubstantivischePhrase getDescription(
            final ITextContext textContext,
            final IDescribableGO gameObject,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe) {
        return getWorld().getDescription(
                textContext,
                gameObject, possessivDescriptionVorgabe);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param textContext Game Object schon kennt, wird eher eine
     */
    default EinzelneSubstantivischePhrase getDescription(
            final ITextContext textContext,
            final GameObjectId gameObjectId,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown) {
        return getWorld().getDescription(textContext, gameObjectId,
                possessivDescriptionVorgabe, shortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     */
    default EinzelneSubstantivischePhrase getDescription(
            final ITextContext textContext,
            final IDescribableGO gameObject,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown) {
        // FIXME Alternative altDescriptions() bauen, auch Aufrufer prüfen und
        //  ergänzen oder auf alt...() umstellen.

        return getWorld().getDescription(textContext, gameObject, possessivDescriptionVorgabe,
                shortIfKnown);
    }

    World getWorld();
}
