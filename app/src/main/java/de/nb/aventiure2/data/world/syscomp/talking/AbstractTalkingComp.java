package de.nb.aventiure2.data.world.syscomp.talking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryStateDao;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.SCTalkAction;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;

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

    protected final StoryStateDao n;

    /**
     * Constructor for {@link AbstractTalkingComp}.
     */
    public AbstractTalkingComp(final GameObjectId gameObjectId,
                               final AvDatabase db) {
        super(gameObjectId, db.talkingDao());
        this.db = db;

        n = db.storyStateDao();
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
        return new TalkingPCD(getGameObjectId(), null);
    }

    public void setTalkingTo(@Nullable final GameObjectId talkingToId) {
        if (talkingToId == null) {
            unsetTalkingTo();
            return;
        }

        setTalkingTo((ITalkerGO) GameObjects.load(db, talkingToId));
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
        return GameObjects.getPOVDescription(
                db, SPIELER_CHARAKTER, gameObject, shortIfKnown);
    }

    public void setTalkingTo(@Nullable final ITalkerGO otherTalker) {
        @Nullable final GameObjectId talkingToId = otherTalker != null ? otherTalker.getId() : null;

        if (getGameObjectId().equals(talkingToId)) {
            throw new IllegalStateException("A game object cannot talk to itself.");
        }

        if (Objects.equals(getTalkingToId(), talkingToId)) {
            return;
        }

        getPcd().setTalkingToId(talkingToId);
        if (otherTalker != null) {
            otherTalker.talkingComp().setTalkingTo(getGameObjectId());
        }
    }

    /**
     * Setzt den Gesprächspartner auf <code>null</code>.
     */
    protected void unsetTalkingTo() {
        @Nullable final ITalkerGO talkingTo = getTalkingTo();
        if (talkingTo == null) {
            return;
        }

        getPcd().setTalkingToId(null);
        talkingTo.talkingComp().unsetTalkingTo();
    }

    public boolean isInConversation() {
        return getTalkingTo() != null;
    }

    public boolean isTalkingTo(final @NonNull ITalkerGO other) {
        return isTalkingTo(other.getId());
    }

    public boolean isTalkingTo(final GameObjectId otherId) {
        return Objects.equals(getTalkingToId(), otherId);
    }

    @Nullable
    ITalkerGO getTalkingTo() {
        @Nullable final GameObjectId talkingToId = getTalkingToId();
        if (talkingToId == null) {
            return null;
        }

        return (ITalkerGO) GameObjects.load(db, talkingToId);
    }

    @Nullable
    GameObjectId getTalkingToId() {
        return getPcd().getTalkingToId();
    }
}
