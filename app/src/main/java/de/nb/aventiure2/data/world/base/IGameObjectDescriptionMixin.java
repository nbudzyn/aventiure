package de.nb.aventiure2.data.world.base;

import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.description.PossessivDescriptionVorgabe;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.description.ITextContext;

/**
 * Mixin with helper methods for descriptions, mainly for {@link AbstractComponent}s,
 * based on a {@link World}.
 */
public interface IGameObjectDescriptionMixin extends IWorldDescriptionMixin {

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
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe) {
        return anaph(textContext, possessivDescriptionVorgabe, true);
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
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean descShortIfKnown) {
        return anaph(textContext, getGameObjectId(), possessivDescriptionVorgabe, descShortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    default EinzelneSubstantivischePhrase getDescription(
            final ITextContext textContext,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe) {
        return getDescription(textContext, possessivDescriptionVorgabe, false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     */
    default EinzelneSubstantivischePhrase getDescription(
            final ITextContext textContext,
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown) {
        return getDescription(textContext, getGameObjectId(), possessivDescriptionVorgabe,
                shortIfKnown);
    }

    GameObjectId getGameObjectId();
}
