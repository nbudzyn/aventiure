package de.nb.aventiure2.data.world.syscomp.inspection;

import com.google.common.collect.ImmutableCollection;

import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.TimedDescription;


/**
 * Eine <code>IInspection</code> entspricht einer
 * {@link de.nb.aventiure2.scaction.AbstractScAction},
 * die der Benutzer (auf einem einzigen Game Object) durchführen kann. Die
 * <code>IInspection</code> kann zu verschiedenen, alternativen Beschreibungen führen.
 */
public interface IInspection extends
        // Mixins
        IWorldLoaderMixin, IWorldDescriptionMixin {
    IInspectableGO getInspectable();

    String getActionName();

    ImmutableCollection<? extends TimedDescription<?>> altTimedDescriptions();
    
    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine kurze
     * Beschreibung des Game Objects.
     */
    default SubstantivischePhrase anaph() {
        return anaph(true);
    }

    /**
     * Gibt das Personalpronomen zurück, mit dem ein
     * anaphorischer Bezug auf dieses
     * Game Object möglich ist - wenn das nicht möglich ist, dann eine
     * Beschreibung des Game Objects.
     */
    default SubstantivischePhrase anaph(final boolean descShortIfKnown) {
        return anaph(getInspectable().getId(), descShortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    default EinzelneSubstantivischePhrase getDescription() {
        return getDescription(false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> das
     *                     Game Object schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    default EinzelneSubstantivischePhrase getDescription(final boolean shortIfKnown) {
        return getDescription(getInspectable().getId(), shortIfKnown);
    }
}
