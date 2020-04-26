package de.nb.aventiure2.data.world.syscomp.state;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO for the {@link StateComp} component.
 */
@Dao
public abstract class StateDao implements IComponentDao<StatePCD> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(StatePCD data);

    @Override
    @Query("SELECT * from StatePCD where :gameObjectId = gameObjectId")
    public abstract StatePCD get(GameObjectId gameObjectId);
}
