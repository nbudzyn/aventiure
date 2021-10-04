package de.nb.aventiure2.data.world.syscomp.feelings;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO for the {@link FeelingsComp} component.
 */
@Dao
public abstract class FeelingsDao implements IComponentDao<FeelingsPCD> {
    @Override
    public void insert(final FeelingsPCD pcd) {
        insertInternal(pcd);

        insertFeelingTowardsInfos(pcd);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertInternal(FeelingsPCD pcd);

    /**
     * Wer das hier aufruft, muss auch lokale Informationen verwerfen!
     */
    private void insertFeelingTowardsInfos(final FeelingsPCD pcd) {
        deleteFeelingTowardsInfos(pcd.getGameObjectId());

        for (final Map.Entry<GameObjectId, Map<FeelingTowardsType, Float>> entry :
                pcd.getFeelingsMap().entrySet()) {
            insert(pcd.getGameObjectId(), entry.getKey(), entry.getValue());
        }
    }

    private void insert(
            final GameObjectId feelingBeing,
            final GameObjectId target,
            final Map<FeelingTowardsType, Float> feelings) {
        for (final Map.Entry<FeelingTowardsType, Float> entry : feelings.entrySet()) {
            insert(feelingBeing, target, entry.getKey(), entry.getValue().floatValue());
        }
    }

    private void insert(final GameObjectId feelingBeing, final GameObjectId target,
                        final FeelingTowardsType feelingTowardsType, final float intensity) {
        insert(new FeelingsTowardsInfo(feelingBeing, target, feelingTowardsType, intensity));
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FeelingsTowardsInfo feelingsTowardsInfo);

    /**
     * Wer das hier aufruft, muss auch lokale Informationen verwerfen!
     */
    @Query("DELETE FROM FeelingsTowardsInfo WHERE :feelingBeing = feelingBeing")
    abstract void deleteFeelingTowardsInfos(GameObjectId feelingBeing);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an
     * dem Game Object gespeichert sind!
     */
    @Override
    public FeelingsPCD get(final GameObjectId feelingBeing) {
        final FeelingsPCD res = getInternal(feelingBeing);
        res.initFeelingTowardsInfos(toMap(getFeelingTowardsInfos(feelingBeing)));

        return res;
    }

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Query("SELECT * from FeelingsPCD where :feelingBeing = gameObjectId")
    public abstract FeelingsPCD getInternal(GameObjectId feelingBeing);

    private static Map<GameObjectId, Map<FeelingTowardsType, Float>> toMap(
            final List<FeelingsTowardsInfo> feelingTowardsInfos) {
        final HashMap<GameObjectId, Map<FeelingTowardsType, Float>> res =
                new HashMap<>(feelingTowardsInfos.size()
                        / FeelingTowardsType.values().length + 2);

        for (final FeelingsTowardsInfo feelingsTowardsInfo : feelingTowardsInfos) {
            Map<FeelingTowardsType, Float> innerMap =
                    res.get(feelingsTowardsInfo.getTarget());

            if (innerMap == null) {
                innerMap = new HashMap<>(FeelingTowardsType.values().length);
                res.put(feelingsTowardsInfo.getTarget(), innerMap);
            }

            innerMap.put(feelingsTowardsInfo.getType(),
                    feelingsTowardsInfo.getIntensity());
        }

        return res;
    }

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Query("SELECT * from FeelingsTowardsInfo where :feelingBeing = feelingBeing")
    abstract List<FeelingsTowardsInfo> getFeelingTowardsInfos(GameObjectId feelingBeing);

    @Override
    public void delete(final GameObjectId feelingBeing) {
        deleteInternal(feelingBeing);

        deleteFeelingTowardsInfos(feelingBeing);
    }

    @Query("DELETE from FeelingsPCD where :feelingBeing = gameObjectId")
    abstract void deleteInternal(GameObjectId feelingBeing);
}
