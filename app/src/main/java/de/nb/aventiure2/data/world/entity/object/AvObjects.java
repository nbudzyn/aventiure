package de.nb.aventiure2.data.world.entity.object;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.world.base.GameObjectId;

import static de.nb.aventiure2.data.world.room.Rooms.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;

/**
 * All {@link AvObjects} in the world.
 */
public class AvObjects {
    public static GameObjectId GOLDENE_KUGEL = new GameObjectId(10_000);
    // STORY Spieler kauft Lampe (z.B. für Hütte) auf Schlossfest


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

    private AvObjects() {
    }

    public static AvObject get(final GameObjectId id) {
        for (final AvObject object : ALL) {
            if (object.is(id)) {
                return object;
            }
        }

        throw new IllegalStateException("Unexpected game object id: " + id);
    }
}
