package de.nb.aventiure2.data.world.feelings;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO for the {@link FeelingsComp} component.
 */
@Dao
public abstract class FeelingsDao implements IComponentDao<FeelingsPCD> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(FeelingsPCD pcd);

    @Override
    @Query("SELECT * from FeelingsPCD where :gameObjectId = gameObjectId")
    public abstract FeelingsPCD get(GameObjectId gameObjectId);
}
