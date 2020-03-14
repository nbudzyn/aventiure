package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StartsNew;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind.UNTROESTLICH;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.german.GermanUtil.capitalize;

/**
 * Der Spieler(charakter) wirft einen Gegenstand hoch.
 */
public class HochwerfenAction extends AbstractObjectAction {
    private final AvRoom room;

    public HochwerfenAction(final AvDatabase db,
                            final ObjectData objectData,
                            final AvRoom room) {
        super(db, objectData);

        this.room = room;
    }

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final AvRoom room, final ObjectData objectData) {
        // TODO Nicht jedes Object lässt sich hochwerfen...
        return ImmutableList.of(new HochwerfenAction(db, objectData, room));
    }

    @Override
    @NonNull
    public String getName() {
        return capitalize(getObjectData().akk()) + " hochwerfen";
    }

    @Override
    public void narrateAndDo(final StoryState currentStoryState) {
        if (currentStoryState.lastObjectWas(getObject())) {
            if (currentStoryState.lastActionWas(HochwerfenAction.class)) {
                narrateAndDoWiederholung();
                return;
            }
        }

        narrateAndDoErstesMal(currentStoryState);
    }

    private void narrateAndDoErstesMal(final StoryState currentStoryState) {
        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            n.add(t(StartsNew.WORD,
                    ", wirfst " +
                            getObjectData().akk() +
                            // TODO Maskulinum...
                            " in die Höhe und fängst sie wieder auf")
                    .dann());
            return;
        }

        n.add(t(StartsNew.PARAGRAPH,
                "Aus Langeweile wirfst " +
                        getObjectData().akk() +
                        // TODO Maskulinum...
                        " in die Höhe und fängst sie wieder auf")
                .dann());
    }

    private void narrateAndDoWiederholung() {
        if (PlayerActionUtil.random(2) == 1) {
            n.add(t(StartsNew.SENTENCE,
                    "Und noch einmal - was ein schönes Spiel!")
                    .dann());
            return;
        }

        if (room == IM_WALD_BEIM_BRUNNEN) {
            n.add(t(StartsNew.SENTENCE,
                    "Noch einmal wirfst du " +
                            getObjectData().akk() +
                            " in die Höhe... doch oh nein, " +
                            getObjectData().nom(true) +
                            " fällt dir nicht in die Hände, sondern schlägt vorbei " +
                            "auf den Brunnenrand und rollt geradezu ins Wasser hinein." +
                            " Du folgst ihr mit den Augen nach, aber " +
                            getObjectData().nom(true) +
                            " verschwindet, und der Brunnen ist tief, so tief, dass " +
                            "man keinen Grund sieht."));

            playerInventoryDao.letGo(getObject());
            objectDataDao.setDemSCInDenBrunnenGefallen(getObject(), true);
            playerStatsDao.setStateOfMind(UNTROESTLICH);
            return;
        }

        n.add(t(StartsNew.SENTENCE,
                "Übermütig schleuderst du " +
                        getObjectData().akk() +
                        " noch einmal in die Luft, aber sie wieder aufzufangen will dir " +
                        "dieses Mal nicht gelingen. " +
                        capitalize(getObjectData().nom(true)) +
                        " landet " +
                        room.getLocationMode().getWo()));

        playerInventoryDao.letGo(getObject());
        objectDataDao.setRoom(getObject(), room);

        return;
    }
}
