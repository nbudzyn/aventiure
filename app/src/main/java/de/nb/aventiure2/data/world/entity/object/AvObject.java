package de.nb.aventiure2.data.world.entity.object;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.base.AbstractEntity;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.Rooms;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.german.base.Nominalphrase.np;

/**
 * An object in the world.
 */
public class AvObject extends AbstractEntity {
    // TODO Aus dem, was in den Subklassen on AbstractGameObject noch ist,
    //  components machen. Components werden mit der Game-Object-ID
    //  verknüpft (Schlüssel gemeinsam mit dem Component-Typ)
    //  und nur Components speichern ihren State (wenn sie einen haben).
    // TODO ObjectData etc. zu Components umbauen, Gemeinsamkeiten zu separaten
    //  Components zusammenfassen.
    //  Interfaces für die Components verwenden?
    //  Idee:
    //  RoomFactory roomFactory = new Assemblage(component1::new, component2::new);
    //  Room schloss = RoomFactory.create();

    // TODO Dinge / Frösche etc. könnten collectible sein.

    // TODO Eine der Components ist das Inventory / ContainerComponent. Der Player
    //  - aber vielleicht auch Räume oder bisherige AvObjects - können ein Inventory haben.

    /**
     * The initial room where this object can be found.
     */
    private final AvRoom initialRoom;

    public static boolean isObject(final AbstractEntityData entityData,
                                   final GameObjectId id) {
        @Nullable final AvObject object = extractObject(entityData);
        if (object == null) {
            return false;
        }

        return object.is(id);
    }

    @Nullable
    public static AvObject extractObject(final AbstractEntityData entityData) {
        if (!(entityData instanceof ObjectData)) {
            return null;
        }

        return ((ObjectData) entityData).getObject();
    }

    AvObject(final GameObjectId id,
             final NumerusGenus numerusGenus,
             final String descriptionAtFirstSightNomDatAkk,
             final String normalDescriptionWhenKnownNomDatAkk,
             final String shortDescriptionWhenKnownNomDatAkk,
             final GameObjectId initialRoom) {
        this(id, numerusGenus, descriptionAtFirstSightNomDatAkk,
                normalDescriptionWhenKnownNomDatAkk, shortDescriptionWhenKnownNomDatAkk,
                Rooms.get(initialRoom));
    }

    AvObject(final GameObjectId id,
             final NumerusGenus numerusGenus,
             final String descriptionAtFirstSightNomDatAkk,
             final String normalDescriptionWhenKnownNomDatAkk,
             final String shortDescriptionWhenKnownNomDatAkk,
             final AvRoom initialRoom) {
        this(id,
                np(numerusGenus, descriptionAtFirstSightNomDatAkk),
                np(numerusGenus, normalDescriptionWhenKnownNomDatAkk),
                np(numerusGenus, shortDescriptionWhenKnownNomDatAkk),
                initialRoom);
    }

    AvObject(final GameObjectId id,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase normalDescriptionWhenKnown,
             final Nominalphrase shortDescriptionWhenKnown,
             final GameObjectId initialRoom) {
        this(id, descriptionAtFirstSight, normalDescriptionWhenKnown, shortDescriptionWhenKnown,
                Rooms.get(initialRoom));
    }

    AvObject(final GameObjectId id,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase normalDescriptionWhenKnown,
             final Nominalphrase shortDescriptionWhenKnown,
             final AvRoom initialRoom) {
        super(id, descriptionAtFirstSight, normalDescriptionWhenKnown,
                shortDescriptionWhenKnown);
        this.initialRoom = initialRoom;
    }

    public AvRoom getInitialRoom() {
        return initialRoom;
    }
}
