package de.nb.aventiure2.data.world.entity.object;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.Indefinitpronomen;
import de.nb.aventiure2.german.base.Nominalphrase;

/**
 * Changeable data for an object in the world.
 */
@Entity
public class ObjectData extends AbstractEntityData {
    @PrimaryKey
    @NonNull
    private final AvObject object;

    @Nullable
    private final AvRoom room;

    private final boolean known;

    /**
     * Ob das Objekt dem Spieler (-Charakter) in den Brunnen gefallen ist (und nach Kenntnis des
     * Spielers immer noch dort liegt)
     */
    // TODO Allgemeineres Konzept, das nicht speziell mit der Spielwelt zusammenhängt?
    //  Vielleicht so etwas wie Tags, Status oder Ähnliches?
    private final boolean demSCInDenBrunnenGefallen;

    public static List<ObjectData> filterInDenBrunnenGefallen(
            final Map<GameObjectId, ObjectData> objectsById) {
        return filterInDenBrunnenGefallen(objectsById.values());
    }

    private static List<ObjectData> filterInDenBrunnenGefallen(
            final Collection<ObjectData> objects) {
        return objects.stream()
                .filter(ObjectData::isDemSCInDenBrunnenGefallen)
                .collect(Collectors.toList());
    }

    /**
     * Gibt eine Beschreibung dieses Objekts zurück - wenn es nur eines ist - sonst
     * etwas wie "die Dinge".
     */
    public static DeklinierbarePhrase getDescriptionSingleOrCollective(
            final List<ObjectData> objects) {
        if (objects.isEmpty()) {
            return Indefinitpronomen.NICHTS;
        }

        if (objects.size() == 1) {
            final ObjectData objectInDenBrunnenGefallen =
                    objects.iterator().next();

            return objectInDenBrunnenGefallen.getDescription(false);
        }

        return Nominalphrase.DINGE;
    }

    public static String getAkkShort(final List<ObjectData> objectDatas) {
        if (objectDatas.size() == 1) {
            return objectDatas.iterator().next().akk(true);
        }

        return "sie";
    }

    public ObjectData(@NonNull final AvObject object, @Nullable final AvRoom room,
                      final boolean known, final boolean demSCInDenBrunnenGefallen) {
        super(object.getId());
        this.object = object;
        this.room = room;
        this.known = known;
        this.demSCInDenBrunnenGefallen = demSCInDenBrunnenGefallen;
    }

    @Override
    public Nominalphrase getDescription(final boolean shortIfKnown) {
        if (isKnown()) {
            return shortIfKnown ? object.getShortDescriptionWhenKnown() :
                    object.getNormalDescriptionWhenKnown();
        }

        return object.getDescriptionAtFirstSight();
    }

    @NonNull
    public AvObject getObject() {
        return object;
    }

    @Nullable
    public AvRoom getRoom() {
        return room;
    }

    public boolean isKnown() {
        return known;
    }

    public boolean isDemSCInDenBrunnenGefallen() {
        return demSCInDenBrunnenGefallen;
    }
}
