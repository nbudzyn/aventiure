package de.nb.aventiure2.playeraction;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;

/**
 * Der Spielercharakter klettert.
 */
class KletternAction extends AbstractPlayerAction {
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
    public void narrateAndDo() {
        switch (room) {
            case HINTER_DER_HUETTE:
                narrateAndDoKletternBaumHinterHuette();
                return;
        }

        throw new IllegalStateException("Unexpected room: " + room);
    }

    public void narrateAndDoKletternBaumHinterHuette() {
        final int count = db.counterDao().incAndGet("KletternAction_BaumHinterHuette");
        switch (count) {
            case 1:
                narrateAndDoKletternBaumHinterHuetteErstesMal();
                return;
            case 2:
                narrateAndDoKletternBaumHinterHuetteZweitesMal();
                return;
            default:
                narrateAndDoKletternBaumHinterHuetteNtesMal();
        }
    }

    private void narrateAndDoKletternBaumHinterHuetteErstesMal() {
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
                        + "nicht, und Äpfel sind auch keine zu sehen. Vielleicht doch kein "
                        + "Apfelbaum. "
                        + "Mit einiger Mühe drehst du auf dem Ast um und hangelst dich "
                        + "vorsichtig "
                        + "wieder herab auf den Boden. Das war anstrengend!")
                .beendet(PARAGRAPH));

        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.ERSCHOEPFT);
    }

    private void narrateAndDoKletternBaumHinterHuetteZweitesMal() {
        n.add(t(PARAGRAPH, "Noch einmal kletterst du eine, zwei Etagen den Baum hinauf. "
                + "Du schaust ins Blattwerk und bist stolz auf dich, dann vorsichtig "
                + "wieder herunter")
                .beendet(PARAGRAPH));
    }

    private void narrateAndDoKletternBaumHinterHuetteNtesMal() {
        n.add(alt(
                t(PARAGRAPH,
                        "Ein weites Mal kletterst du auf den Baum. Ein zurückschwingender "
                                + "Ast verpasst dir beim Abstieg ein Schramme")
                        .dann(),
                t(PARAGRAPH, "Es ist anstrengend, aber du kletterst noch einmal "
                        + "auf den Baum. Neues gibt es hier oben nicht zu erleben und unten bist "
                        + "du ziemlich erschöpft")
        ));

        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.ERSCHOEPFT);
    }
}
