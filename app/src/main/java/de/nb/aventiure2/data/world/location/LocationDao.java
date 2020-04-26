package de.nb.aventiure2.data.world.location;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO for the {@link LocationComp} component.
 */
@Dao
public abstract class LocationDao implements IComponentDao<LocationPCD> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(LocationPCD pcd);

    @Query("SELECT gameObjectId from LocationPCD where :locationId = locationId")
    abstract List<GameObjectId> findByLocation(GameObjectId locationId);

    @Override
    @Query("SELECT * from LocationPCD where :gameObjectId = gameObjectId")
    public abstract LocationPCD get(GameObjectId gameObjectId);
}
