package de.nb.aventiure2.data.world.entity.object;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.world.entity.base.AbstractEntity;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.NumerusGenus;

import static de.nb.aventiure2.data.world.entity.object.AvObject.Key.GOLDENE_KUGEL;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;

/**
 * An object in the world.
 */
public class AvObject extends AbstractEntity {
    public enum Key {
        GOLDENE_KUGEL
    }

    public static final List<AvObject> ALL =
            ImmutableList.of(
                    new AvObject(GOLDENE_KUGEL,
                            np(F, "eine goldene Kugel",
                                    "einer goldenen Kugel"),
                            np(F, "die goldene Kugel", "der goldenen Kugel"),
                            np(F, "die Kugel", "der Kugel"),
                            AvRoom.SCHLOSS_VORHALLE)
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
             final AvRoom initialRoom) {
        super(descriptionAtFirstSight, normalDescriptionWhenKnown, shortDescriptionWhenKnown);
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
