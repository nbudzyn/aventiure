package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.GameObjectId;

@Entity(primaryKeys = {"knower", "knowee"})
public class KnownInfo {
    @NonNull
    private GameObjectId knower;

    @NonNull
    private GameObjectId knowee;

    private Known known;

    public KnownInfo(final GameObjectId knower,
                     final GameObjectId knowee, final Known known) {
        this.knower = knower;
        this.knowee = knowee;
        this.known = known;
    }

    @NonNull
    public GameObjectId getKnower() {
        return knower;
    }

    public void setKnower(final GameObjectId knower) {
        this.knower = knower;
    }

    @NonNull
    public GameObjectId getKnowee() {
        return knowee;
    }

    public void setKnowee(final GameObjectId knowee) {
        this.knowee = knowee;
    }

    public Known getKnown() {
        return known;
    }

    public void setKnown(final Known known) {
        this.known = known;
    }
}
