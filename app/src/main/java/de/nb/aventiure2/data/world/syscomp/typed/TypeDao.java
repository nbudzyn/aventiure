package de.nb.aventiure2.data.world.syscomp.typed;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IComponentDao;

/**
 * Android ROOM DAO for the {@link TypeComp} component.
 */
@Dao
public abstract class TypeDao implements IComponentDao<TypePCD> {
    @Override
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(TypePCD pcd);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass alle {@link ITypedGO}s
     * gespeichert sind!
     */
    @Query("SELECT gameObjectId from TypePCD where :type = type")
    // FIXME Nötig? Aufrufen wie findByLocation, sonst löschen.
    abstract List<GameObjectId> findByType(GameObjectType type);

    /**
     * Vor jedem Aufruf muss sichergestellt sein, dass das {@link ITypedGO}s
     * gespeichert ist!
     */
    @Override
    @Query("SELECT * from TypePCD where :gameObjectId = gameObjectId")
    public abstract TypePCD get(GameObjectId gameObjectId);

    @Override
    @Query("DELETE from TypePCD where :gameObjectId = gameObjectId")
    public abstract void delete(GameObjectId gameObjectId);
}
