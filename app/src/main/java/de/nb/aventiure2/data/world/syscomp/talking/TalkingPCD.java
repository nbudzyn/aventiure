package de.nb.aventiure2.data.world.syscomp.talking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.AbstractPersistentComponentData;
import de.nb.aventiure2.data.world.base.GameObjectId;

/**
 * Mutable - and therefore persistent - data of the {@link AbstractTalkingComp} component.
 */
@Entity
public
class TalkingPCD extends AbstractPersistentComponentData {
    @Nullable
    private GameObjectId talkingToId;

    TalkingPCD(@NonNull final GameObjectId gameObjectId,
               @Nullable final GameObjectId talkingToId) {
        super(gameObjectId);
        this.talkingToId = talkingToId;
    }

    @Nullable
    GameObjectId getTalkingToId() {
        return talkingToId;
    }

    void setTalkingToId(@Nullable final GameObjectId talkingTo) {
        setChanged();
        talkingToId = talkingTo;
    }
}
