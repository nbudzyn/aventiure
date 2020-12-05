package de.nb.aventiure2.data.world.syscomp.talking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.Personalpronomen;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobject.World.*;

/**
 * Component for a {@link GameObject}: Das Game Object kann mit einem anderen
 * im Gespräch sein (dann ist diese Beziehung reflexiv).
 * <p>
 * Möglicherweise gibt es {@link SCTalkAction}s, also mögliche Redebeiträge, die der
 * Spieler(-Charakter) an das {@link ITalkerGO} richten kann (und auf die das
 * {@link ITalkerGO} dann irgendwie reagiert).
 */
public abstract class AbstractTalkingComp extends AbstractStatefulComponent<TalkingPCD> {
    protected final AvDatabase db;
    protected final World world;

    protected final Narrator n;

    private final boolean initialSchonBegruesstMitSC;

    /**
     * Constructor for {@link AbstractTalkingComp}.
     */
    public AbstractTalkingComp(final GameObjectId gameObjectId,
                               final AvDatabase db,
                               final Narrator n,
                               final World world,
                               final boolean initialSchonBegruesstMitSC) {
        super(gameObjectId, db.talkingDao());
        this.db = db;
        this.n = n;
        this.world = world;
        this.initialSchonBegruesstMitSC = initialSchonBegruesstMitSC;
    }

    public List<SCTalkAction> getSCConversationSteps() {
        final ImmutableList.Builder<SCTalkAction> res =
                ImmutableList.builder();

        for (final SCTalkAction step : getSCTalkActionsWithoutCheckingConditions()) {
            if (step.isPossible()) {
                res.add(step);
            }
        }

        return res.build();
    }

    protected abstract Iterable<? extends SCTalkAction>
    getSCTalkActionsWithoutCheckingConditions();

    @Override
    @NonNull
    protected TalkingPCD createInitialState() {
        return new TalkingPCD(getGameObjectId(), null, initialSchonBegruesstMitSC);
    }

    public void updateSchonBegruesstMitSCOnLeave(
            final ILocatableGO locatable, final ILocationGO from,
            @Nullable final ILocationGO to) {
        if (!world.getLocationSystem().haveSameUpperMostLocation(from, to) &&
                (locatable.is(SPIELER_CHARAKTER) || locatable.is(getGameObjectId())) &&
                !isTalkingTo(SPIELER_CHARAKTER)) {
            // SC und das ITalkingBeing  verlassen einander. Ab jetzt können sie
            // einander wieder begrüßen.
            setSchonBegruesstMitSC(false);
        }
    }

    public void setTalkingTo(@Nullable final GameObjectId talkingToId) {
        if (talkingToId == null) {
            unsetTalkingTo();
            return;
        }

        setTalkingTo((ITalkerGO<?>) world.load(talkingToId));
    }

    public boolean isDefinitivDiskontinuitaet() {
        // Der SC hat das Gespräch mit der Creature GERADE EBEN beendet
        // und hat es sich ganz offenbar anders überlegt.
        // Oder die Creature hat das Gespräch beendet und der Benutzer möchte
        // sofort wieder ein Gespräch anknüpfen.

        return n.lastNarrationWasFromReaction() &&
                world.loadSC().memoryComp().lastActionWas(Action.Type.REDEN, getGameObjectId()) &&
                !isTalkingTo(SPIELER_CHARAKTER);
    }

    /**
     * Gibt eine (evtl. auch etwas längere) Nominalphrase zurück, die das Game Object beschreibt.
     * Die Phrase wird in der Regel unterschiedlich sein, je nachdem, ob
     * ob der Spieler das Game Object schon kennt oder nicht.
     */
    protected Nominalphrase getDescription(final IDescribableGO gameObject) {
        return getDescription(gameObject, false);
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
    protected Nominalphrase getDescription(final IDescribableGO gameObject,
                                           final boolean shortIfKnown) {
        return world.getPOVDescription(
                SPIELER_CHARAKTER, gameObject, shortIfKnown);
    }

    public void setTalkingTo(@Nullable final ITalkerGO<?> otherTalker) {
        @Nullable final GameObjectId talkingToId = otherTalker != null ? otherTalker.getId() : null;

        if (getGameObjectId().equals(talkingToId)) {
            throw new IllegalStateException("A game object cannot talk to itself.");
        }

        if (Objects.equals(getTalkingToId(), talkingToId)) {
            return;
        }

        getPcd().setTalkingToId(talkingToId);

        if (otherTalker != null &&
                otherTalker.is(SPIELER_CHARAKTER) &&
                !getGameObjectId().equals(SPIELER_CHARAKTER)) {
            setSchonBegruesstMitSC(true);
        }

        if (otherTalker != null) {
            otherTalker.talkingComp().setTalkingTo(getGameObjectId());
        }
    }

    /**
     * Setzt den Gesprächspartner auf <code>null</code>.
     */
    protected void unsetTalkingTo() {
        @Nullable final ITalkerGO<?> talkingTo = getTalkingTo();
        if (talkingTo == null) {
            return;
        }

        getPcd().setTalkingToId(null);
        talkingTo.talkingComp().unsetTalkingTo();
    }

    public boolean isInConversation() {
        return getTalkingTo() != null;
    }

    public boolean isTalkingTo(final @NonNull ITalkerGO<?> other) {
        return isTalkingTo(other.getId());
    }

    public boolean isTalkingTo(final GameObjectId otherId) {
        return Objects.equals(getTalkingToId(), otherId);
    }

    @Nullable
    private ITalkerGO<?> getTalkingTo() {
        @Nullable final GameObjectId talkingToId = getTalkingToId();
        if (talkingToId == null) {
            return null;
        }

        return (ITalkerGO<?>) world.load(talkingToId);
    }

    @Nullable
    private GameObjectId getTalkingToId() {
        return getPcd().getTalkingToId();
    }

    public void setSchonBegruesstMitSC(final boolean schonBegruesstMitSC) {
        if (getGameObjectId() == SPIELER_CHARAKTER) {
            throw new IllegalStateException("setSchonBegruesstMitSC() für SPIELER_CHARAKTER - "
                    + "ergibt keinen Sinn");
        }

        getPcd().setSchonBegruesstMitSC(schonBegruesstMitSC);
    }

    protected boolean isSchonBegruesstMitSC() {
        return getPcd().isSchonBegruesstMitSC();
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
    protected final SubstantivischePhrase getAnaphPersPronWennMglSonstShortDescription() {
        return getAnaphPersPronWennMglSonstDescription(true);
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
    protected final SubstantivischePhrase getAnaphPersPronWennMglSonstDescription(
            final boolean descShortIfKnown) {

        final IDescribableGO describableGO = (IDescribableGO) world.load(getGameObjectId());

        @javax.annotation.Nullable final Personalpronomen anaphPersPron =
                n.getAnaphPersPronWennMgl(describableGO);
        if (anaphPersPron != null) {
            return anaphPersPron;
        }

        return world.getDescription(describableGO, descShortIfKnown);
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
    protected final Nominalphrase getDescription(final boolean shortIfKnown) {
        return world.getDescription(getGameObjectId(), shortIfKnown);
    }

    @NonNull
    protected SpielerCharakter loadSC() {
        return world.loadSC();
    }
}
