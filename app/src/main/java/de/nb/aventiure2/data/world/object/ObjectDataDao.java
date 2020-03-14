package de.nb.aventiure2.data.world.object;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.nb.aventiure2.data.world.room.AvRoom;

/**
 * Room DAO for {@link ObjectData}s.
 */
@Dao
public abstract class ObjectDataDao {
    public void insertInitial() {
        for (final AvObject object : AvObject.ALL) {
            insertInitial(object);
        }
    }

    public void insertInitial(final AvObject object) {
        insert(new ObjectData(object, object.getInitialRoom(), false,
                false));
    }

    public void setDemSCInDenBrunnenGefallen(
            final AvObject object,
            final boolean demSCInDenBrunnenGefallen) {
        setDemSCInDenBrunnenGefallenInternal(object, demSCInDenBrunnenGefallen);

        if (demSCInDenBrunnenGefallen) {
            setRoom(object, AvRoom.UNTEN_IM_BRUNNEN);
        }
    }

    @Query("UPDATE ObjectData SET room = :room WHERE object = :object")
    public abstract void setRoom(AvObject object, AvRoom room);

    public void setKnown(final AvObject.Key objectKey) {
        setKnown(AvObject.get(objectKey));
    }

    @Query("UPDATE ObjectData SET known = 1 WHERE object = :object")
    public abstract void setKnown(AvObject object);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(ObjectData objectData);

    @Query("UPDATE ObjectData SET demSCInDenBrunnenGefallen = :demSCInDenBrunnenGefallen " +
            "WHERE object = :object")
    protected abstract void setDemSCInDenBrunnenGefallenInternal(AvObject object,
                                                                 boolean demSCInDenBrunnenGefallen);

    public void update(final AvObject object, @Nullable final AvRoom room,
                       final boolean known, final boolean demSCInDenBrunnenGefallen) {
        update(new ObjectData(object, room, known, demSCInDenBrunnenGefallen));
    }

    @Update
    public abstract void update(ObjectData objectData);

    @Query("SELECT * from ObjectData where :room = room")
    public abstract List<ObjectData> getObjectsInRoomSync(AvRoom room);

    @Query("SELECT * from ObjectData where :room = room")
    public abstract LiveData<List<ObjectData>> getObjectsInRoom(AvRoom room);

    @Query("SELECT * from ObjectData")
    public abstract LiveData<List<ObjectData>> getAll();
}
