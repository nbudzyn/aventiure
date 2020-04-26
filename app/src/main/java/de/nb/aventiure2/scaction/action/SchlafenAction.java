package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.CHAPTER;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.BETT_IN_DER_HUETTE_IM_WALD;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ERSCHOEPFT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.time.AvTime.oClock;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.AllgDescription.allg;
import static de.nb.aventiure2.german.base.DuDescription.du;

/**
 * Der Spielercharakter legt sich schlafen.
 */
public class SchlafenAction extends AbstractScAction {
    public static Collection<SchlafenAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState,
            @Nullable final IGameObject room) {
        final ImmutableList.Builder<SchlafenAction> res = ImmutableList.builder();
        if (room != null && room.is(BETT_IN_DER_HUETTE_IM_WALD)) {
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
        if (sc.feelingsComp().hasMood(ERSCHOEPFT)) {
            return narrateAndDoSchlaeftEin();
        }

        return narrateAndDoSchlaeftNichtEin();
    }

    @NonNull
    private AvTimeSpan narrateAndDoSchlaeftNichtEin() {
        sc.memoryComp().setLastAction(buildMemorizedAction());

        final ImmutableList.Builder<AbstractDescription> alt = ImmutableList.builder();
        if (!isDefinitivWiederholung()) {
            alt.add(du("schließt", "kurz die Augen. Die Aufregung der letzten Stunden "
                    + "steckt dir noch in den Knochen – an Einschlafen ist "
                    + "nicht zu denken", mins(1)));
        }

        alt.add(allg("Müde bist du noch nicht", mins(1))
                .dann());

        alt.add(allg("Gibt es hier eigentlich Spinnen?", mins(1)));

        alt.add(du("drehst dich von einer Seite auf die andere", "von einer Seite", mins(1)));

        return n.addAlt(alt);
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

        sc.memoryComp().setLastAction(buildMemorizedAction());

        n.add(alt(
                t(CHAPTER,
                        "Nach einem langen Schlaf wachst du gut erholt wieder auf"),
                t(CHAPTER,
                        "Du schläfst tief und fest und wachst erst nach einigen Stunden "
                                + "wieder auf")
        ));

        sc.feelingsComp().setMood(NEUTRAL);

        return timeElapsed;
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        // Gilt auch als Wiederholung, wenn der Spielercharakter nicht
        // einschläft - schließlich kann man es, wenn die Entscheidung, ob
        // es sich um eine Wiederholung handelt, relevant ist, noch gar nicht
        // wissen.
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.SCHLAFEN_ODER_VERGEBLICHER_EINSCHLAF_VERSUCH,
                (GameObjectId) null);
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
