package de.nb.aventiure2.data.world.syscomp.location;

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

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an Game Objects
     * gespeichert sind!
     */
    @Query("SELECT gameObjectId from LocationPCD where :locationId = locationId")
    abstract List<GameObjectId> findByLocation(GameObjectId locationId);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Override
    @Query("SELECT * from LocationPCD where :gameObjectId = gameObjectId")
    public abstract LocationPCD get(GameObjectId gameObjectId);

    @Override
    @Query("DELETE from LocationPCD where :gameObjectId = gameObjectId")
    public abstract void delete(GameObjectId gameObjectId);
}
