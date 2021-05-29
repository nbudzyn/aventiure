package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.world.base.GameObjectId;

@Entity(primaryKeys = {"assumer", "assumee"})
@SuppressWarnings("WeakerAccess")
public class AssumedStateInfo {
    @NonNull
    private GameObjectId assumer;

    @NonNull
    private GameObjectId assumee;

    @NonNull
    private String assumedStateString;

    AssumedStateInfo(final GameObjectId assumer,
                     final GameObjectId assumee,
                     final String assumedStateString) {
        this.assumer = assumer;
        this.assumee = assumee;
        this.assumedStateString = assumedStateString;
    }

    static Map<GameObjectId, String> toMap(
            final List<AssumedStateInfo> aassumedStateInfos) {
        final HashMap<GameObjectId, String> res =
                new HashMap<>(aassumedStateInfos.size());

        for (final AssumedStateInfo assumedStateInfo : aassumedStateInfos) {
            res.put(assumedStateInfo.getAssumee(), assumedStateInfo.getAssumedStateString());
        }

        return res;
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
    String getAssumedStateString() {
        return assumedStateString;
    }

    public void setAssumedStateString(final String assumedStateString) {
        this.assumedStateString = assumedStateString;
    }
}
