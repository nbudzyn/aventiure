package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.HINTER_DER_HUETTE;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

/**
 * Der Spielercharakter klettert.
 */
public class KletternAction extends AbstractScAction {
    private final ILocationGO room;

    public static Collection<KletternAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState, final ILocationGO room) {
        final ImmutableList.Builder<KletternAction> res = ImmutableList.builder();
        if (room.is(HINTER_DER_HUETTE)) {
            res.add(new KletternAction(db, initialStoryState, room));
        }

        return res.build();
    }

    /**
     * Creates a new <code>KletternAction</code>.
     */
    private KletternAction(final AvDatabase db,
                           final StoryState initialStoryState,
                           final ILocationGO room) {
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
        if (room.is(HINTER_DER_HUETTE)) {
            return "Auf den Baum klettern";
        }

        throw new IllegalStateException("Unexpected room: " + room);
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        if (room.is(HINTER_DER_HUETTE)) {
            final AvTimeSpan avTimeSpan = narrateAndDoBaumHinterHuette();
            sc.memoryComp().setLastAction(buildMemorizedAction());

            return avTimeSpan;
        }

        throw new IllegalStateException("Unexpected room: " + room);
    }

    private AvTimeSpan narrateAndDoBaumHinterHuette() {
        final int count = db.counterDao().incAndGet("KletternAction_BaumHinterHuette");
        switch (count) {
            case 1:
                return narrateAndDoBaumHinterHuetteErstesMal();
            case 2:
                return narrateAndDoBaumHinterHuetteZweitesMal();
            default:
                return narrateAndDoBaumHinterHuetteNtesMal();
        }
    }

    @NonNull
    private AvTimeSpan narrateAndDoBaumHinterHuetteErstesMal() {
        final String dunkelNachsatz =
                room.storingPlaceComp().getLichtverhaeltnisse() == DUNKEL ?
                        " Und das alles im Dunkeln!" : "";

        sc.feelingsComp().setMood(Mood.ERSCHOEPFT);

        return n.add(neuerSatz(PARAGRAPH,
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
                        + "wieder herab auf den Boden. Das war anstrengend!"
                        + dunkelNachsatz, mins(10))
                .beendet(PARAGRAPH));
    }

    private AvTimeSpan narrateAndDoBaumHinterHuetteZweitesMal() {
        return n.add(
                neuerSatz(PARAGRAPH, "Noch einmal kletterst du eine, zwei Etagen den Baum hinauf. "
                        + "Du schaust ins Blattwerk und bist stolz auf dich, dann geht es vorsichtig "
                        + "wieder hinunter", mins(10))
                        .beendet(PARAGRAPH));
    }

    private AvTimeSpan narrateAndDoBaumHinterHuetteNtesMal() {
        sc.feelingsComp().setMood(Mood.ERSCHOEPFT);

        final String erschoepftMuedeNachsatz =
                room.storingPlaceComp().getLichtverhaeltnisse() == DUNKEL ?
                        "Ein Nickerchen täte dir gut" : "Und müde";

        return n.addAlt(
                du(PARAGRAPH, "kletterst",
                        "ein weiteres Mal auf den Baum. Ein zurückschwingender "
                                + "Ast verpasst dir beim Abstieg ein Schramme",
                        "ein weiteres Mal", mins(15))
                        .dann(),
                neuerSatz(PARAGRAPH,
                        "Es ist anstrengend, aber du kletterst noch einmal "
                                + "auf den Baum. Neues gibt es hier oben nicht zu erleben und unten bist "
                                + "du ziemlich erschöpft. " + erschoepftMuedeNachsatz, mins(15)
                ));
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return false;
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.KLETTERN);
    }
}
