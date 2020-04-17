package de.nb.aventiure2.data.world.entity.object;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.Rooms;

import static de.nb.aventiure2.data.world.room.Rooms.UNTEN_IM_BRUNNEN;

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
            setRoom(object, UNTEN_IM_BRUNNEN);
        }
    }

    public void setRoom(final AvObject object, final GameObjectId room) {
        setRoom(object, Rooms.get(room));
    }

    @Query("UPDATE ObjectData SET room = :room WHERE object = :object")
    public abstract void setRoom(AvObject object, AvRoom room);

    public void setKnown(final GameObjectId id) {
        setKnown(AvObject.get(id));
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
    public abstract List<ObjectData> getObjectsInRoom(AvRoom room);

    @Query("SELECT * from ObjectData")
    public abstract List<ObjectData> getAll();

    public ObjectData get(final GameObjectId id) {
        return get(AvObject.get(id));
    }

    @Query("SELECT * from ObjectData where :object = object")
    public abstract ObjectData get(AvObject object);
}
