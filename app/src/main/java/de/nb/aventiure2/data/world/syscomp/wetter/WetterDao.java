package de.nb.aventiure2.data.world.syscomp.wetter;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO for the {@link WetterComp} component.
 */
@Dao
public abstract class WetterDao implements IComponentDao<WetterPCD> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(WetterPCD pcd);

    @Override
    @Query("SELECT * from WetterPCD where :id = gameObjectId")
    public abstract WetterPCD get(final GameObjectId id);
}
