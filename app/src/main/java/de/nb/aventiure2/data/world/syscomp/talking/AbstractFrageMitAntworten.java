package de.nb.aventiure2.data.world.syscomp.talking;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

public abstract class AbstractFrageMitAntworten
        implements IWorldLoaderMixin, IWorldDescriptionMixin {
    protected final Narrator n;
    protected final World world;
    protected final ITalkContext talkContext;
    protected final GameObjectId gameObjectId;

    public AbstractFrageMitAntworten(
            final GameObjectId gameObjectId, final Narrator n, final World world,
            final ITalkContext talkContext) {
        this.n = n;
        this.world = world;
        this.talkContext = talkContext;
        this.gameObjectId = gameObjectId;
    }

    public abstract void nscStelltFrage();

    public abstract Iterable<SCTalkAction> getAntwortActions();

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
    protected final SubstantivischePhrase anaph() {
        return anaph(true);
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
    private SubstantivischePhrase anaph(final boolean descShortIfKnown) {
        return anaph(gameObjectId, descShortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    protected final EinzelneSubstantivischePhrase getDescription() {
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
    private EinzelneSubstantivischePhrase getDescription(
            final boolean shortIfKnown) {
        return getDescription(gameObjectId, shortIfKnown);
    }

    @Override
    public World getWorld() {
        return world;
    }
}
