package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StartsNew;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.german.GermanUtil.capitalize;

/**
 * Der Spieler(charakter) nimmt einen Gegenstand an sich.
 */
public class NehmenAction extends AbstractObjectAction {
    private final AvRoom room;

    public NehmenAction(final AvDatabase db, final ObjectData objectData, final AvRoom room) {
        super(db, objectData);
        this.room = room;
    }

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final AvRoom room,
            final ObjectData objectData) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        res.add(new NehmenAction(db, objectData, room));
        return res.build();
    }

    @Override
    @NonNull
    public String getName() {
        return capitalize(getObjectData().akk()) + " nehmen";
    }

    @Override
    public void narrateAndDo(final StoryState currentStoryState) {
        n.add(buildStoryState(currentStoryState));
        objectDataDao.update(getObject(), null, true, false);
        playerInventoryDao.take(getObject());
    }

    private StoryStateBuilder buildStoryState(final StoryState currentStoryState) {
        if (currentStoryState.lastActionWas(AblegenAction.class)) {
            if (currentStoryState.lastObjectWas(getObject())) {
                return t(StartsNew.PARAGRAPH,
                        "Dann nimmst du " + getObjectData().akk() +
                                " erneut")
                        .undWartest();
            }

            return t(StartsNew.SENTENCE,
                    "Dann nimmst du als nächstes "
                            + getObjectData().akk())
                    .undWartest();
        }

        return t(StartsNew.PARAGRAPH,
                room.getLocationMode().getNehmenVerb().getDescriptionHauptsatz(getObjectData()))
                .undWartest()
                .dann();
    }
}
