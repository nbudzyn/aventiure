package de.nb.aventiure2.data.world.object;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.Nominalphrase;

import static de.nb.aventiure2.german.Nominalphrase.np;
import static de.nb.aventiure2.german.NumerusGenus.N;
import static de.nb.aventiure2.german.NumerusGenus.PL;

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
    private final boolean demSCInDenBrunnenGefallen;

    /**
     * Gibt eine Beschreibung dieses Objekts zurück - wenn es nur eines ist - sonst
     * etwas wie "die Dinge".
     */
    public static Nominalphrase getDescriptionSingleOrCollective(final List<ObjectData> objects) {
        if (objects.isEmpty()) {
            return np(N, "nichts");
        }

        if (objects.size() == 1) {
            final ObjectData objectInDenBrunnenGefallen =
                    objects.iterator().next();

            return objectInDenBrunnenGefallen.getDescription(false);
        }

        return np(PL, "die Dinge", "den Dingen");
    }

    public static String getAkkShort(final List<ObjectData> objectDatas) {
        if (objectDatas.size() == 1) {
            return objectDatas.iterator().next().akk(true);
        }

        return "sie";
    }

    public ObjectData(@NonNull final AvObject object, @Nullable final AvRoom room,
                      final boolean known, final boolean demSCInDenBrunnenGefallen) {
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
