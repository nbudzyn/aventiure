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
     * <p>
     * Als Textkontext wird implizit der aktuelle Narrator genommen.
     * Das kann zu missverständlichen oder falschen Personalpronomen führen ("sie") - oder
     * auch zu falschen oder missverständnlichen Possessivpronomen ("ihre Haare"), wenn das
     * Ergebnis nicht  gleich als Nächstes am Anfang narratet wird, sondern davor andere Dinge zu
     * stehen kommen!
     *
     * @see #anaph(ITextContext, PossessivDescriptionVorgabe)
     */
    default EinzelneSubstantivischePhrase anaph(
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe) {
        return getWorld().anaph(getGameObjectId(), possessivDescriptionVorgabe,
                true);
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
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe) {
        return getWorld().anaph(textContext, getGameObjectId(), possessivDescriptionVorgabe,
                true);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     * <p>
     * Als Textkontext wird implizit der aktuelle Narrator genommen.
     * Das kann zu missverständlichen oder falschen <i>Possessivpronomen</i> (!) führen
     * ("ihre Haare"), wenn
     * <ol>
     * <li> die Beschreibung einen possessiven Anteil haben kann ("ihre Haare")
     * <li> und die {@link PossessivDescriptionVorgabe} <i>etwas anderes als</i>
     * {@link PossessivDescriptionVorgabe#NICHT_POSSESSIV} ist
     * <li>und das Ergebnis nicht gleich als Nächstes am Anfang narratet wird, sondern davor
     * andere Dinge zu stehen      kommen!
     * </ol>
     *
     * @see #getDescription(ITextContext, PossessivDescriptionVorgabe)
     */
    default EinzelneSubstantivischePhrase getDescription(
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe) {
        return getDescription(possessivDescriptionVorgabe, false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * <p>
     * Als Textkontext wird implizit der aktuelle Narrator genommen.
     * Das kann zu missverständlichen oder falschen <i>Possessivpronomen</i> (!) führen
     * ("ihre Haare"), wenn
     * <ol>
     * <li> die Beschreibung einen possessiven Anteil haben kann ("ihre Haare")
     * <li> und die {@link PossessivDescriptionVorgabe} <i>etwas anderes als</i>
     * {@link PossessivDescriptionVorgabe#NICHT_POSSESSIV} ist
     * <li>und das Ergebnis nicht gleich als Nächstes am Anfang narratet wird, sondern davor
     * andere Dinge zu stehen      kommen!
     * </ol>
     *
     * @see #getDescription(ITextContext, PossessivDescriptionVorgabe, boolean)
     */
    default EinzelneSubstantivischePhrase getDescription(
            final PossessivDescriptionVorgabe possessivDescriptionVorgabe,
            final boolean shortIfKnown) {
        return getWorld().getDescription(getGameObjectId(),
                possessivDescriptionVorgabe, shortIfKnown);
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
