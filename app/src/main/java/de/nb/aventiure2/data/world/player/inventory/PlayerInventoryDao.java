package de.nb.aventiure2.data.world.player.inventory;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.nb.aventiure2.data.world.object.AvObject;

/**
 * Room DAO for {@link de.nb.aventiure2.data.world.player.inventory.PlayerInventoryItem}s.
 */
@Dao
public abstract class PlayerInventoryDao {
    public void take(final AvObject object) {
        insert(new PlayerInventoryItem(object));
    }

    public void letGo(final AvObject object) {
        delete(object);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insert(PlayerInventoryItem playerInventoryItem);

    @Query("DELETE FROM PlayerInventoryItem WHERE :object = object")
    abstract void delete(AvObject object);

    @Query("SELECT object from PlayerInventoryItem")
    public abstract List<AvObject> getInventory();
}
