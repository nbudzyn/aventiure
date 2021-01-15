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

    /**
     * <code>true</code> bedeutet, dass das {@link ITalkerGO} sein letztes Gespräch
     * selbst aktiv beendet hat - <code>false</code> bedeutet, dass der Gesprächspartner
     * das Gespräch beendet hat (oder der Talker noch nie ein Gespräch hatte).
     */
    private boolean talkerHatletztesGespraechSelbstBeendet;

    private boolean schonBegruesstMitSC;

    TalkingPCD(@NonNull final GameObjectId gameObjectId,
               @Nullable final GameObjectId talkingToId,
               final boolean talkerHatletztesGespraechSelbstBeendet,
               final boolean schonBegruesstMitSC) {
        super(gameObjectId);
        this.talkingToId = talkingToId;
        this.talkerHatletztesGespraechSelbstBeendet = talkerHatletztesGespraechSelbstBeendet;
        this.schonBegruesstMitSC = schonBegruesstMitSC;
    }

    @Nullable
    GameObjectId getTalkingToId() {
        return talkingToId;
    }

    void setTalkingToId(@Nullable final GameObjectId talkingTo) {
        setChanged();
        talkingToId = talkingTo;
    }

    boolean isTalkerHatletztesGespraechSelbstBeendet() {
        return talkerHatletztesGespraechSelbstBeendet;
    }

    void setTalkerHatletztesGespraechSelbstBeendet(
            final boolean talkerHatletztesGespraechSelbstBeendet) {
        setChanged();
        this.talkerHatletztesGespraechSelbstBeendet = talkerHatletztesGespraechSelbstBeendet;
    }

    void setSchonBegruesstMitSC(final boolean schonBegruesstMitSC) {
        setChanged();
        this.schonBegruesstMitSC = schonBegruesstMitSC;
    }

    boolean isSchonBegruesstMitSC() {
        return schonBegruesstMitSC;
    }
}
