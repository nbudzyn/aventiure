package de.nb.aventiure2.data.world.syscomp.mentalmodel;

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
 * Android ROOM DAO for the {@link MentalModelComp} component.
 */
@Dao
public abstract class MentalModelDao implements IComponentDao<MentalModelPCD> {
    @Override
    public void insert(final MentalModelPCD pcd) {
        insertInternal(pcd);

        insertAssumedLocations(pcd);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertInternal(MentalModelPCD pcd);

    /**
     * Wer das hier aufruft, muss auch lokale Informationen verwerfen!
     */
    private void insertAssumedLocations(final MentalModelPCD pcd) {
        deleteAllLocationsHeAssumes(pcd.getGameObjectId());

        for (final Map.Entry<GameObjectId, GameObjectId> entry :
                pcd.getAssumedLocations().entrySet()) {
            insert(pcd.getGameObjectId(), entry.getKey(), entry.getValue());
        }
    }

    private void insert(final GameObjectId assumer, final GameObjectId assumee,
                        final GameObjectId assumedLocation) {
        insert(new AssumedLocationInfo(assumer, assumee, assumedLocation));
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(AssumedLocationInfo assumedLocationInfo);

    /**
     * Wer das hier aufruft, muss auch lokale Informationen verwerfen!
     */
    @Query("DELETE FROM AssumedLocationInfo WHERE :assumer = assumer" )
    abstract void deleteAllLocationsHeAssumes(GameObjectId assumer);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an
     * dem Game Object gespeichert sind!
     */
    @Override
    public MentalModelPCD get(final GameObjectId assumer) {
        final MentalModelPCD res = getInternal(assumer);
        res.initAssumedLocations(toMap(getAssumedLocationInfos(assumer)));

        return res;
    }

    @Query("SELECT * from MentalModelPCD where :assumer = gameObjectId" )
    public abstract MentalModelPCD getInternal(final GameObjectId assumer);

    private static Map<GameObjectId, GameObjectId> toMap(
            final List<AssumedLocationInfo> assumedLocationInfos) {
        final HashMap<GameObjectId, GameObjectId> res =
                new HashMap<>(assumedLocationInfos.size());

        for (final AssumedLocationInfo assumedLocationInfo : assumedLocationInfos) {
            res.put(assumedLocationInfo.getAssumee(), assumedLocationInfo.getAssumedLocationId());
        }

        return res;
    }

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Query("SELECT * from AssumedLocationInfo where :assumer = assumer" )
    abstract List<AssumedLocationInfo> getAssumedLocationInfos(GameObjectId assumer);
}
