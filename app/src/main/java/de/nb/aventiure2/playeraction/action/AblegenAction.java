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

import static de.nb.aventiure2.german.GermanUtil.capitalize;

/**
 * Der Benutzer legt einen Gegenstand ab.
 */
public class AblegenAction extends AbstractObjectAction {
    private final AvRoom room;

    public AblegenAction(final AvDatabase db,
                         final ObjectData objectData,
                         final AvRoom room) {
        super(db, objectData);
        this.room = room;
    }

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final AvRoom room, final ObjectData objectData) {
        // TODO Nicht jedes Object lässt sich hochwerfen...
        return ImmutableList.of(new AblegenAction(db, objectData, room));
    }

    @Override
    @NonNull
    public String getName() {
        return capitalize(getObjectData().akk()) + " hinlegen";
    }

    @Override
    public void narrateAndDo(final StoryState currentStoryState) {
        narrate(currentStoryState);
        playerInventoryDao.letGo(getObject());
        objectDataDao.setRoom(getObject(), room);
    }

    private void narrate(final StoryState currentStoryState) {
        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            if (currentStoryState.lastObjectWas(getObject())) {
                if (currentStoryState.lastActionWas(NehmenAction.class)) {
                    n.add(t(StartsNew.WORD,
                            "- und legst sie sogleich wieder hin"));
                    return;
                }

                n.add(t(StartsNew.WORD,
                        ", dann legst du sie hin")
                        .undWartest());
                return;
            }

            String text = "und legst " + getObjectData().akk();
            if (currentStoryState.lastActionWas(BewegenAction.class)) {
                text += " dort";
            }

            text += " hin";

            n.add(t(StartsNew.WORD, text));
            return;
        }

        n.add(t(StartsNew.PARAGRAPH,
                "Du legst " + getObjectData().akk() + " hin")
                .undWartest()
                .dann());
    }
}
