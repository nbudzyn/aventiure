package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind.NEUTRAL;

/**
 * Der Spielercharakter legt sich schlafen.
 */
public class SchlafenAction extends AbstractPlayerAction {
    public static Collection<SchlafenAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState, final AvRoom room) {
        final ImmutableList.Builder<SchlafenAction> res = ImmutableList.builder();
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
        if (db.playerStatsDao().getPlayerStats().getStateOfMind() == ERSCHOEPFT) {
            narrateAndDoSchlaeftEin();
            return;
        }

        narrateAndDoSchlaeftNichtEin();
    }

    private void narrateAndDoSchlaeftNichtEin() {
        final ImmutableList.Builder<StoryStateBuilder> alt = ImmutableList.builder();

        if (!initialStoryState.lastActionWas(SchlafenAction.class)) {
            alt.add(t(PARAGRAPH,
                    "Du schließt kurz die Augen. Die Aufregung der letzten Stunden "
                            + "steckt dir noch in den Knochen – an Einschlafen ist "
                            + "nicht zu denken"));
        }

        alt.add(t(PARAGRAPH,
                "Müde bist du allerdings nicht")
                .dann());
        alt.add(t(PARAGRAPH,
                "Gibt es hier eigentlich Spinnen?"));

        n.add(alt(alt));
    }

    private void narrateAndDoSchlaeftEin() {
        n.add(alt(
                t(SENTENCE,
                        "Du schließt nur kurz die Augen. Die Erlebnisse der letzten Stunden "
                                + "gehen dir durch den Kopf. Was wäre wohl passiert, wenn du… – "
                                + "kaum hast due die Augen geschlossen, bist du auch schon "
                                + "eingeschlafen."),
                t(SENTENCE,
                        "Jetzt, da du liegst, fällt dir erst auf, wir erschöpft du "
                                + "eigentlich bist. Nur ganz kurz die Augen schließen."),
                t(SENTENCE,
                        "Du fühlst dich auf einmal warm und schwer. Du kuschelst dich an "
                                + "das harte Holz und schon bist du eingeschlafen.")
        ));

        n.add(alt(
                t(PARAGRAPH,
                        "Du schläfst tief und fest und wachst erst am nächsten Morgen "
                                + "wieder auf."),
                t(PARAGRAPH,
                        "Am nächsten Morgen wachst du gut erholt wieder auf.")
        ));

        db.playerStatsDao().setStateOfMind(NEUTRAL);
    }
}
