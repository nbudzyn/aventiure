package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;

/**
 * Der Spielercharakter legt sich schlafen.
 */
public class SchlafenAction extends AbstractPlayerAction {
    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState, final AvRoom room) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        // vorher ist er nicht müde!
        if (room == AvRoom.BETT_IN_DER_HUETTE_IM_WALD
            // TODO  Der SC trinkt erst einen Schnaps, der irgendwo rumsteht,
            //  stats.getStateOfMind() == PlayerStateOfMind.UNTROESTLICH
        ) {
            res.add(new SchlafenAction(db, initialStoryState));
        }

        return res.build();
    }

    private SchlafenAction(final AvDatabase db,
                           final StoryState initialStoryState) {
        super(db, initialStoryState);
    }

    @Override
    public String getType() {
        return "actionSchlafen";
    }

    @Override
    @NonNull
    public String getName() {
        return "Ein Nickerchen machen";
    }

    @Override
    public void narrateAndDo() {
        // TODO: Wenn der SC müde ist (z.B. Schnaps getrunken),
        // dann schläft er eine Nacht durch. An nächsten Morgen ist das Fest.

        n.add(alt(
                t(PARAGRAPH,
                        "Du schließt kurz die Augen. Die Aufregung der letzten Stunden "
                                + "steckt dir noch in den Knochen – an Einschlafen ist "
                                + "nicht zu denken"),
                t(PARAGRAPH,
                        "Müde bist du allerdings nicht")
                        .dann(),
                t(PARAGRAPH,
                        "Gibt es hier eigentlich Spinnen?")
        ));
    }

}
