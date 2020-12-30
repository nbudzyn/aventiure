package de.nb.aventiure2.data.world.syscomp.taking;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.AbstractStatelessComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

/**
 * Component for a {@link GameObject}: The game object can take something -
 * or at least react to an offer.
 */
public abstract class AbstractTakingComp extends AbstractStatelessComponent {
    protected final AvDatabase db;
    protected Narrator n;
    protected final World world;

    public AbstractTakingComp(
            final GameObjectId id,
            final AvDatabase db, final Narrator n, final World world) {
        super(id);

        this.db = db;
        this.n = n;
        this.world = world;
    }

    /**
     * Das {@link ITakerGO} bekommt etwas angeboten.
     */
    public abstract <GIVEN extends IDescribableGO & ILocatableGO>
    SCTakeAction<GIVEN> getAction(GIVEN offered);

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
    protected SubstantivischePhrase getAnaphPersPronWennMglSonstShortDescription(
            final GameObjectId describableId) {
        // IDEA Auch andere "Anaphern" (im weitesten Sinne) erzeugen, nicht nur Personalpronomen:
        //  Auch Synonyme, Überbegriffe oder schlicht Wiederholung könnten Anaphern sein.
        //  Dann am besten Alternativen zurückgeben!

        return getAnaphPersPronWennMglSonstDescription(
                (IDescribableGO) world.load(describableId), true);
    }

    // REFACTOR Code ist doppelt oder mehrfache. Vielleicht sollte der
    //  Narrator zumindest auch die knownComp des
    //  SCs kennen, z.B. über ein Interface?? Das wäre quasi das
    //  "Adressatenmodell".

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
    protected SubstantivischePhrase getAnaphPersPronWennMglSonstShortDescription(
            final IDescribableGO describableGO) {
        return getAnaphPersPronWennMglSonstDescription(
                describableGO, true);
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
    protected SubstantivischePhrase getAnaphPersPronWennMglSonstDescription(
            final IDescribableGO describableGO,
            final boolean descShortIfKnown) {
        @Nullable final Personalpronomen anaphPersPron =
                n.getAnaphPersPronWennMgl(describableGO);
        if (anaphPersPron != null) {
            return anaphPersPron;
        }

        return world.getDescription(describableGO, descShortIfKnown);
    }
}
