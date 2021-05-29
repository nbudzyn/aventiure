package de.nb.aventiure2.data.world.syscomp.mentalmodel;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
        insertAssumedStates(pcd);
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
            insert(new AssumedLocationInfo(pcd.getGameObjectId(), entry.getKey(),
                    entry.getValue()));
        }
    }


    /**
     * Speicher alle angenommenen States in die Datenbank, das hier aufruft, muss auch lokale
     * Informationen verwerfen!
     */
    private void insertAssumedStates(final MentalModelPCD pcd) {
        deleteAllStatesHeAssumes(pcd.getGameObjectId());

        for (final Map.Entry<GameObjectId, String> entry :
                pcd.getAssumedStateStrings().entrySet()) {
            insert(new AssumedStateInfo(pcd.getGameObjectId(), entry.getKey(),
                    entry.getValue()));
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(AssumedLocationInfo assumedLocationInfo);

    /**
     * Wer das hier aufruft, muss auch lokale Informationen verwerfen!
     */
    @Query("DELETE FROM AssumedLocationInfo WHERE :assumer = assumer")
    abstract void deleteAllLocationsHeAssumes(GameObjectId assumer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(AssumedStateInfo assumedStateInfo);

    /**
     * Wer das hier aufruft, muss auch lokale Informationen verwerfen!
     */
    @Query("DELETE FROM AssumedStateInfo WHERE :assumer = assumer")
    abstract void deleteAllStatesHeAssumes(GameObjectId assumer);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an
     * dem Game Object gespeichert sind!
     */
    @Override
    public MentalModelPCD get(final GameObjectId assumer) {
        final MentalModelPCD res = getInternal(assumer);
        res.initAssumedLocations(AssumedLocationInfo.toMap(getAssumedLocationInfos(assumer)));
        res.initAssumedStateStrings(AssumedStateInfo.toMap(getAssumedStateInfos(assumer)));

        return res;
    }

    @Query("SELECT * from MentalModelPCD where :assumer = gameObjectId")
    public abstract MentalModelPCD getInternal(final GameObjectId assumer);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Query("SELECT * from AssumedLocationInfo where :assumer = assumer")
    abstract List<AssumedLocationInfo> getAssumedLocationInfos(GameObjectId assumer);


    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Query("SELECT * from AssumedStateInfo where :assumer = assumer")
    abstract List<AssumedStateInfo> getAssumedStateInfos(GameObjectId assumer);
}
