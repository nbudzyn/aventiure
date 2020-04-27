package de.nb.aventiure2.data.world.syscomp.talking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO for the {@link TalkingComp} component.
 */
@Dao
public abstract class TalkingDao implements IComponentDao<TalkingPCD> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(TalkingPCD pcd);

    @Override
    @Query("SELECT * from TalkingPCD where :gameObjectId = gameObjectId")
    public abstract TalkingPCD get(GameObjectId gameObjectId);
}
