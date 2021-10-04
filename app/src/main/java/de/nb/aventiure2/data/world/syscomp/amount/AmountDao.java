package de.nb.aventiure2.data.world.syscomp.amount;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO für die Mengen von Game Objects.
 */
@Dao
public abstract class AmountDao implements IComponentDao<AmountPCD> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(AmountPCD data);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle Änderungen an dem Game Object
     * gespeichert sind!
     */
    @Override
    @Query("SELECT * from AmountPCD where :gameObjectId = gameObjectId")
    public abstract AmountPCD get(GameObjectId gameObjectId);

    @Override
    @Query("DELETE from AmountPCD where :gameObjectId = gameObjectId")
    public abstract void delete(final GameObjectId gameObjectId);
}
