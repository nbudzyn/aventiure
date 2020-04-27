package de.nb.aventiure2.data.world.syscomp.talking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.AbstractStatefulComponent;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;

/**
 * Component for a {@link GameObject}: Das Game Object kann mit einem anderen
 * im Gespräch sein (dann ist diese Beziehung reflexiv).
 */
public class TalkingComp extends AbstractStatefulComponent<TalkingPCD> {
    private final AvDatabase db;

    /**
     * Constructor for {@link TalkingComp}.
     */
    public TalkingComp(final GameObjectId gameObjectId,
                       final AvDatabase db) {
        super(gameObjectId, db.talkingDao());
        this.db = db;
    }

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
    public void unsetTalkingTo() {
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
    public ITalkerGO getTalkingTo() {
        @Nullable final GameObjectId talkingToId = getTalkingToId();
        if (talkingToId == null) {
            return null;
        }

        return (ITalkerGO) GameObjects.load(db, talkingToId);
    }

    @Nullable
    public GameObjectId getTalkingToId() {
        return getPcd().getTalkingToId();
    }
}
