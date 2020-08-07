package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import de.nb.aventiure2.data.world.base.GameObjectId;

@Entity(primaryKeys = {"assumer", "assumee"})
public class AssumedLocationInfo {
    @NonNull
    private GameObjectId assumer;

    @NonNull
    private GameObjectId assumee;

    @NonNull
    private GameObjectId assumedLocationId;

    public AssumedLocationInfo(final GameObjectId assumer,
                               final GameObjectId assumee,
                               final GameObjectId assumedLocationId) {
        this.assumer = assumer;
        this.assumee = assumee;
        this.assumedLocationId = assumedLocationId;
    }

    @NonNull
    public GameObjectId getAssumer() {
        return assumer;
    }

    public void setAssumer(final GameObjectId assumer) {
        this.assumer = assumer;
    }

    @NonNull
    public GameObjectId getAssumee() {
        return assumee;
    }

    public void setAssumee(final GameObjectId assumee) {
        this.assumee = assumee;
    }

    @NonNull
    public GameObjectId getAssumedLocationId() {
        return assumedLocationId;
    }

    public void setAssumedLocationId(final GameObjectId assumedLocationId) {
        this.assumedLocationId = assumedLocationId;
    }
}
