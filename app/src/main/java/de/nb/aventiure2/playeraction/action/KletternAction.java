package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;

/**
 * Der Spielercharakter klettert.
 */
public class KletternAction extends AbstractPlayerAction {
    private final AvRoom room;

    public static Collection<KletternAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState, final AvRoom room) {
        final ImmutableList.Builder<KletternAction> res = ImmutableList.builder();
        if (room == AvRoom.HINTER_DER_HUETTE) {
            res.add(new KletternAction(db, initialStoryState, room));
        }

        return res.build();
    }

    /**
     * Creates a new <code>KletternAction</code>.
     */
    private KletternAction(final AvDatabase db,
                           final StoryState initialStoryState,
                           final AvRoom room) {
        super(db, initialStoryState);
        this.room = room;
    }

    @Override
    public String getType() {
        return "actionKlettern";
    }

    @Override
    @NonNull
    public String getName() {
        switch (room) {
            case HINTER_DER_HUETTE:
                return "Auf den Baum klettern";
        }

        throw new IllegalStateException("Unexpected room: " + room);
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        switch (room) {
            case HINTER_DER_HUETTE:
                return narrateAndDoKletternBaumHinterHuette();
        }

        throw new IllegalStateException("Unexpected room: " + room);
    }

    public AvTimeSpan narrateAndDoKletternBaumHinterHuette() {
        final int count = db.counterDao().incAndGet("KletternAction_BaumHinterHuette");
        switch (count) {
            case 1:
                return narrateAndDoKletternBaumHinterHuetteErstesMal();
            case 2:
                return narrateAndDoKletternBaumHinterHuetteZweitesMal();
            default:
                return narrateAndDoKletternBaumHinterHuetteNtesMal();
        }
    }

    private AvTimeSpan narrateAndDoKletternBaumHinterHuetteErstesMal() {
        n.add(t(PARAGRAPH,
                "Vom Stamm geht in Hüfthöhe ein kräftiger Ast ab, den kannst du "
                        + "ohne Mühe "
                        + "ersteigen. Danach wird es schwieriger. Du ziehst dich eine "
                        + "Ebene höher, "
                        + "und der Stamm gabelt sich. Ja, der rechte Ast müsste halten. "
                        + "Du versuchst, zu "
                        + "balancieren, aber dann kletterst du doch auf allen Vieren den "
                        + "Ast entlang, "
                        + "der immer dünner wird und gefährlich schwankt. Irgendeine "
                        + "Aussicht hast du "
                        + "nicht, und Äpfel sind auch keine zu finden. Vielleicht doch kein "
                        + "Apfelbaum. "
                        + "Mit einiger Mühe drehst du auf dem Ast um und hangelst dich "
                        + "vorsichtig "
                        + "wieder herab auf den Boden. Das war anstrengend!")
                .beendet(PARAGRAPH));

        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.ERSCHOEPFT);

        return mins(10);
    }

    private AvTimeSpan narrateAndDoKletternBaumHinterHuetteZweitesMal() {
        n.add(t(PARAGRAPH, "Noch einmal kletterst du eine, zwei Etagen den Baum hinauf. "
                + "Du schaust ins Blattwerk und bist stolz auf dich, dann geht es vorsichtig "
                + "wieder hinunter")
                .beendet(PARAGRAPH));
        return mins(10);
    }

    private AvTimeSpan narrateAndDoKletternBaumHinterHuetteNtesMal() {
        n.add(alt(
                t(PARAGRAPH,
                        "Ein weiteres Mal kletterst du auf den Baum. Ein zurückschwingender "
                                + "Ast verpasst dir beim Abstieg ein Schramme")
                        .dann(),
                t(PARAGRAPH, "Es ist anstrengend, aber du kletterst noch einmal "
                        + "auf den Baum. Neues gibt es hier oben nicht zu erleben und unten bist "
                        + "du ziemlich erschöpft. Ein Nickerchen täte dir gut")
        ));

        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.ERSCHOEPFT);

        return mins(15);
    }
}
