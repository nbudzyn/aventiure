package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.CHAPTER;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind.NEUTRAL;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

/**
 * Der Spielercharakter legt sich schlafen.
 */
public class SchlafenAction extends AbstractPlayerAction {
    public static Collection<SchlafenAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState, final AvRoom room) {
        final ImmutableList.Builder<SchlafenAction> res = ImmutableList.builder();
        if (room == AvRoom.BETT_IN_DER_HUETTE_IM_WALD) {
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
    public AvTimeSpan narrateAndDo() {
        if (db.playerStatsDao().getPlayerStats().getStateOfMind() == ERSCHOEPFT) {
            return narrateAndDoSchlaeftEin();
        }

        return narrateAndDoSchlaeftNichtEin();
    }

    private AvTimeSpan narrateAndDoSchlaeftNichtEin() {
        final ImmutableList.Builder<StoryStateBuilder> alt = ImmutableList.builder();

        if (!initialStoryState.lastActionWas(SchlafenAction.class)) {
            alt.add(t(PARAGRAPH,
                    "Du schließt kurz die Augen. Die Aufregung der letzten Stunden "
                            + "steckt dir noch in den Knochen – an Einschlafen ist "
                            + "nicht zu denken"));
        }

        alt.add(t(PARAGRAPH,
                "Müde bist du nicht")
                .dann());
        alt.add(t(PARAGRAPH,
                "Gibt es hier eigentlich Spinnen?"));

        n.add(alt(alt));

        return mins(1);
    }

    private AvTimeSpan narrateAndDoSchlaeftEin() {
        n.add(alt(
                t(SENTENCE,
                        "Du schließt nur kurz die Augen. Die Erlebnisse der letzten Stunden "
                                + "gehen dir durch den Kopf. Was wäre wohl passiert, wenn du…\n"
                                + "Kaum hast du die Augen geschlossen, bist du auch schon "
                                + "eingeschlafen")
                        .beendet(CHAPTER),
                t(SENTENCE,
                        "Jetzt, da du liegst, fällt dir erst auf, wir erschöpft du "
                                + "eigentlich bist. Nur ganz kurz die Augen schließen…")
                        .beendet(CHAPTER),
                t(SENTENCE,
                        "Du fühlst dich auf einmal warm und schwer. Du kuschelst dich an "
                                + "das harte Holz und schon bist du eingeschlafen")
                        .beendet(CHAPTER)
        ));

        final AvTimeSpan timeSpanUntilNextMorning =
                // Wieviel Zeit ist bis 7 Uhr morgens?
                db.dateTimeDao().getDateTime().timeSpanUntil(
                        oClock(7, 0));

        final String wannDesc;
        final AvTimeSpan timeElapsed;
        if (timeSpanUntilNextMorning.getAsHours() < 1 ||
                timeSpanUntilNextMorning.getAsHours() > 18) {
            wannDesc = "nach einigen Stunden";
            timeElapsed = hours(9);
        } else {
            wannDesc = "am andern Tage";
            timeElapsed = timeSpanUntilNextMorning;
        }

        n.add(alt(
                t(CHAPTER,
                        "Du schläfst tief und fest und wachst erst "
                                + wannDesc
                                + " wieder auf"),
                t(CHAPTER,
                        capitalize(wannDesc)
                                + " wachst du gut erholt wieder auf")
        ));

        db.playerStatsDao().setStateOfMind(NEUTRAL);

        return timeElapsed;
    }
}
