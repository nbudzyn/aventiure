package de.nb.aventiure2.data.world.syscomp.waiting;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO for the {@link WaitingComp} component.
 */
@Dao
public abstract class WaitingDao implements IComponentDao<WaitingPCD> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(WaitingPCD data);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Override
    @Query("SELECT * from WaitingPCD where :gameObjectId = gameObjectId")
    public abstract WaitingPCD get(GameObjectId gameObjectId);

    @Override
    @Query("DELETE from WaitingPCD where :gameObjectId = gameObjectId")
    public abstract void delete(GameObjectId gameObjectId);
}
