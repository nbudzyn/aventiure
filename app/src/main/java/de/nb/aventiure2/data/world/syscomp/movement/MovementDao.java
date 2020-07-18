package de.nb.aventiure2.data.world.syscomp.movement;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;

/**
 * Android ROOM DAO for the {@link LocationComp} component.
 */
@Dao
public abstract class MovementDao implements IComponentDao<MovementPCD> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(MovementPCD pcd);

    @Override
    @Query("SELECT * from MovementPCD where :gameObjectId = gameObjectId")
    public abstract MovementPCD get(GameObjectId gameObjectId);
}
