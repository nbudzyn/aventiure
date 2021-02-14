package de.nb.aventiure2.data.world.syscomp.talking;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

public abstract class AbstractFrageMitAntworten {
    protected CounterDao counterDao;
    protected final Narrator n;
    protected final World world;
    protected final RapunzelStateComp stateComp;
    protected final FeelingsComp feelingsComp;
    protected final IScBegruessable scBegruessable;
    protected final GameObjectId gameObjectId;

    public AbstractFrageMitAntworten(
            final GameObjectId gameObjectId,
            final CounterDao counterDao,
            final Narrator n, final World world, final RapunzelStateComp stateComp,
            final FeelingsComp feelingsComp, final IScBegruessable scBegruessable) {
        this.gameObjectId = gameObjectId;
        this.counterDao = counterDao;
        this.n = n;
        this.world = world;
        this.stateComp = stateComp;
        this.feelingsComp = feelingsComp;
        this.scBegruessable = scBegruessable;
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
    protected final SubstantivischePhrase anaph(final boolean descShortIfKnown) {
        return world.anaph(gameObjectId, descShortIfKnown);
    }

    /**
     * Gibt eine Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    protected final Nominalphrase getDescription() {
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
    protected final Nominalphrase getDescription(
            final boolean shortIfKnown) {
        return world.getDescription(gameObjectId, shortIfKnown);
    }

    @NonNull
    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }

    public abstract void forgetAll();
}
