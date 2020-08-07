package de.nb.aventiure2.data.world.syscomp.memory;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;
import de.nb.aventiure2.data.world.base.Known;

/**
 * Android ROOM DAO for the {@link MemoryComp} component.
 */
@Dao
public abstract class MemoryDao implements IComponentDao<MemoryPCD> {
    @Override
    public void insert(final MemoryPCD pcd) {
        insertInternal(pcd);

        insertKnownInfos(pcd);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertInternal(MemoryPCD pcd);

    private void insertKnownInfos(final MemoryPCD pcd) {
        deleteAllHeKnows(pcd.getGameObjectId());

        for (final Map.Entry<GameObjectId, Known> entry : pcd.getKnownMap().entrySet()) {
            insert(pcd.getGameObjectId(), entry.getKey(), entry.getValue());
        }
    }

    private void insert(final GameObjectId knower, final GameObjectId knowee,
                        final Known known) {
        insert(new KnownInfo(knower, knowee, known));
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(KnownInfo knownInfo);

    /**
     * Wer das hier aufruft, muss auch lokale Informationen verwerfen!
     */
    @Query("DELETE FROM KnownInfo WHERE :knower = knower")
    abstract void deleteAllHeKnows(GameObjectId knower);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Override
    public MemoryPCD get(final GameObjectId knower) {
        final MemoryPCD res = getInternal(knower);
        res.setKnown(toMap(getKnownInfos(knower)));

        return res;
    }

    @Query("SELECT * from MemoryPCD where :knower = gameObjectId")
    public abstract MemoryPCD getInternal(final GameObjectId knower);

    private static Map<GameObjectId, Known> toMap(final List<KnownInfo> knownInfos) {
        final HashMap<GameObjectId, Known> res =
                new HashMap<>(knownInfos.size() + 20);

        for (final KnownInfo knownInfo : knownInfos) {
            res.put(knownInfo.getKnowee(), knownInfo.getKnown());
        }

        return res;
    }

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Query("SELECT * from KnownInfo where :knower = knower")
    abstract List<KnownInfo> getKnownInfos(GameObjectId knower);
}
