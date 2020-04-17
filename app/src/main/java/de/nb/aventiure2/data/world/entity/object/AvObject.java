package de.nb.aventiure2.data.world.entity.object;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.base.AbstractEntity;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.data.world.entity.object.AvObject.Key.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.room.AvRoom.Key.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;

/**
 * An object in the world.
 */
public class AvObject extends AbstractEntity {
    // TODO Entity-Component-System-Pattern verwenden:
    //  Auch Räume erhalten eine GameObjectId.

    // TODO Components werden mit der Game-Object-ID verknüpft (Schlüssel für diesen Component-Typ)
    //  und nur Components speichern ihren State.
    //  ObjectData etc. zu Components umbauen, Gemeinsamkeiten zu separaten
    //  Components zusammenfassen.
    //  Interfaces für die Components verwenden?
    //  Idee:
    //  RoomFactory roomFactory = new Assemblage(component1::new, component2::new);
    //  Room schloss = RoomFactory.create();

    // TODO Dinge / Frösche etc. könnten collectible sein.

    // TODO Eine der Components ist das Inventory / ContainerComponent. Der Player
    //  - aber vielleicht auch Räume oder bisherige AvObjects - können ein Inventory haben.

    public enum Key {
        GOLDENE_KUGEL(10_000);
        // STORY Spieler kauft Lampe (z.B. für Hütte) auf Schlossfest

        private final GameObjectId gameObjectId;

        private Key(final int gameObjectId) {
            this(new GameObjectId(gameObjectId));
        }

        Key(final GameObjectId gameObjectId) {
            this.gameObjectId = gameObjectId;
        }

        public GameObjectId getGameObjectId() {
            return gameObjectId;
        }
    }

    public static final List<AvObject> ALL =
            ImmutableList.of(
                    new AvObject(GOLDENE_KUGEL,
                            np(F, "eine goldene Kugel",
                                    "einer goldenen Kugel"),
                            np(F, "die goldene Kugel", "der goldenen Kugel"),
                            np(F, "die Kugel", "der Kugel"),
                            SCHLOSS_VORHALLE)
                    // STORY Die goldene Kugel kann verloren gehen, zum Beispiel wenn man sie im
                    //  Sumpf ablegt. Dann gibt es eine art Reset und eine ähnliche goldene
                    //  Kugel erscheint wieder im Schloss. Der Text dort sagt so dann etwas wie
                    //  "eine goldene kugel wie du sie schon einmal gesehen hast, nur etwas
                    //  kleiner".
            );

    private final Key key;

    /**
     * The initial room where this object can be found.
     */
    private final AvRoom initialRoom;

    public static AvObject get(final Key key) {
        for (final AvObject object : ALL) {
            if (object.key == key) {
                return object;
            }
        }

        throw new IllegalStateException("Unexpected key: " + key);
    }

    public static boolean isObject(final AbstractEntityData entityData,
                                   final AvObject.Key objectKey) {
        @Nullable final AvObject object = extractObject(entityData);
        if (object == null) {
            return false;
        }

        return object.getKey() == objectKey;
    }

    @Nullable
    public static AvObject extractObject(final AbstractEntityData entityData) {
        if (!(entityData instanceof ObjectData)) {
            return null;
        }

        return ((ObjectData) entityData).getObject();
    }

    AvObject(final Key key,
             final NumerusGenus numerusGenus,
             final String descriptionAtFirstSightNomDatAkk,
             final String normalDescriptionWhenKnownNomDatAkk,
             final String shortDescriptionWhenKnownNomDatAkk,
             final AvRoom.Key initialRoom) {
        this(key, numerusGenus, descriptionAtFirstSightNomDatAkk,
                normalDescriptionWhenKnownNomDatAkk, shortDescriptionWhenKnownNomDatAkk,
                AvRoom.get(initialRoom));
    }

    AvObject(final Key key,
             final NumerusGenus numerusGenus,
             final String descriptionAtFirstSightNomDatAkk,
             final String normalDescriptionWhenKnownNomDatAkk,
             final String shortDescriptionWhenKnownNomDatAkk,
             final AvRoom initialRoom) {
        this(key,
                np(numerusGenus, descriptionAtFirstSightNomDatAkk),
                np(numerusGenus, normalDescriptionWhenKnownNomDatAkk),
                np(numerusGenus, shortDescriptionWhenKnownNomDatAkk),
                initialRoom);
    }

    AvObject(final Key key,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase normalDescriptionWhenKnown,
             final Nominalphrase shortDescriptionWhenKnown,
             final AvRoom.Key initialRoom) {
        this(key, descriptionAtFirstSight, normalDescriptionWhenKnown, shortDescriptionWhenKnown,
                AvRoom.get(initialRoom));
    }

    AvObject(final Key key,
             final Nominalphrase descriptionAtFirstSight,
             final Nominalphrase normalDescriptionWhenKnown,
             final Nominalphrase shortDescriptionWhenKnown,
             final AvRoom initialRoom) {
        super(key.getGameObjectId(), descriptionAtFirstSight, normalDescriptionWhenKnown,
                shortDescriptionWhenKnown);
        this.key = key;
        this.initialRoom = initialRoom;
    }

    public Key getKey() {
        return key;
    }

    public AvRoom getInitialRoom() {
        return initialRoom;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AvObject avObject = (AvObject) o;
        return key == avObject.key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
