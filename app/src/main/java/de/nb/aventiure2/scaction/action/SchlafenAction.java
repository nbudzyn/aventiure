package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.CHAPTER;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.world.player.stats.ScStateOfMind.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.player.stats.ScStateOfMind.NEUTRAL;
import static de.nb.aventiure2.data.world.room.Rooms.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;

/**
 * Der Spielercharakter legt sich schlafen.
 */
public class SchlafenAction extends AbstractScAction {
    public static Collection<SchlafenAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState, final GameObject room) {
        final ImmutableList.Builder<SchlafenAction> res = ImmutableList.builder();
        if (room.is(BETT_IN_DER_HUETTE_IM_WALD)) {
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
        // STORY Nachts ist man immer müde oder nachts wird man leichter müde oder
        //  nachts kann man immer einschlafen...
        // STORY "Vor Hunger kannst du nicht einschlafen"
        if (db.playerStatsDao().getPlayerStats().getStateOfMind() == ERSCHOEPFT) {
            return narrateAndDoSchlaeftEin();
        }

        return narrateAndDoSchlaeftNichtEin();
    }

    @NonNull
    private AvTimeSpan narrateAndDoSchlaeftNichtEin() {
        final ImmutableList.Builder<StoryStateBuilder> alt = ImmutableList.builder();

        if (!initialStoryState.lastActionWas(SchlafenAction.class)) {
            alt.add(t(PARAGRAPH,
                    "Du schließt kurz die Augen. Die Aufregung der letzten Stunden "
                            + "steckt dir noch in den Knochen – an Einschlafen ist "
                            + "nicht zu denken"));
        }

        alt.add(t(PARAGRAPH,
                "Müde bist du noch nicht")
                .dann());
        alt.add(t(PARAGRAPH,
                "Gibt es hier eigentlich Spinnen?"));
        alt.add(t(PARAGRAPH,
                "Du drehst dich von einer Seite auf die andere")
                .dann());

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

        final AvTimeSpan timeElapsed = schlafen();

        n.add(alt(
                t(CHAPTER,
                        "Nach einem langen Schlaf wachst du gut erholt wieder auf"),
                t(CHAPTER,
                        "Du schläfst tief und fest und wachst erst nach einigen Stunden "
                                + "wieder auf")
        ));

        db.playerStatsDao().setStateOfMind(NEUTRAL);

        return timeElapsed;
    }

    private AvTimeSpan schlafen() {
        final AvDateTime now = db.dateTimeDao().now();

        if (now.getTime().isBefore(oClock(16, 30))) {
            return hours(8);
        } else {
            return now.timeSpanUntil(oClock(7));
        }
    }
}
