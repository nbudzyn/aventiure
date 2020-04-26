package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.MITNEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;

/**
 * Der Spieler(charakter) nimmt einen Gegenstand (oder in Ausnahmefällen eine
 * Creature) an sich.
 */
@ParametersAreNonnullByDefault
public class NehmenAction
        <GO extends IDescribableGO & ILocatableGO,
                LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
        extends AbstractScAction {
    private final IHasStoringPlaceGO room;
    @NonNull
    private final GO gameObject;

    public static <GO extends IDescribableGO & ILocatableGO>
    Collection<NehmenAction> buildObjectActions(final AvDatabase db,
                                                final StoryState initialStoryState,
                                                final IHasStoringPlaceGO room,
                                                final GO object) {
        final ImmutableList.Builder<NehmenAction> res = ImmutableList.builder();
        res.add(new NehmenAction<>(db, initialStoryState, room, object));
        return res.build();
    }

    public static <LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    Collection<NehmenAction> buildCreatureActions(
            final AvDatabase db,
            final StoryState initialStoryState, final IHasStoringPlaceGO room,
            final LIVGO creature) {
        final ImmutableList.Builder<NehmenAction> res = ImmutableList.builder();
        if (creature.is(FROSCHPRINZ) &&
                ((IHasStateGO) creature).stateComp()
                        .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)) {
            res.add(new NehmenAction<>(db, initialStoryState, room, creature));
        }
        return res.build();
    }

    private NehmenAction(final AvDatabase db, final StoryState initialStoryState,
                         final IHasStoringPlaceGO room, @NonNull final GO gameObject) {
        super(db, initialStoryState);
        this.room = room;
        this.gameObject = gameObject;
    }

    @Override
    public String getType() {
        return "actionNehmen";
    }

    @Override
    @NonNull
    public String getName() {
        final PraedikatMitEinerObjektleerstelle praedikat =
                gameObject instanceof ILivingBeingGO ? MITNEHMEN : NEHMEN;

        return praedikat.mitObj(getDescription(gameObject, true)).getDescriptionInfinitiv();
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        AvTimeSpan timeElapsed = narrate();
        removeFromRoomAndTakeCreatureOrObject();

        sc.memoryComp().setLastAction(buildMemorizedAction());

        timeElapsed =
                timeElapsed.plus(creatureReactionsCoordinator.onNehmen(room, gameObject));

        return timeElapsed;
    }

    private void removeFromRoomAndTakeCreatureOrObject() {
        if (gameObject instanceof ILivingBeingGO) {
            removeFromRoomAndTakeCreature((LIVGO) gameObject);
        } else {
            removeFromRoomAndTake();
        }
    }

    private void removeFromRoomAndTakeCreature(@NonNull final LIVGO creature) {
        checkArgument(creature.is(FROSCHPRINZ) &&
                        ((IHasStateGO) creature).stateComp()
                                .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS),
                "Unexpected creature: " + creature);

        removeFromRoomAndTake();
        sc.feelingsComp().setMood(Mood.NEUTRAL);
    }

    private void removeFromRoomAndTake() {
        sc.memoryComp().upgradeKnown(gameObject, Known.getKnown(getLichtverhaeltnisse(room)));
        gameObject.locationComp().setLocation(SPIELER_CHARAKTER);
    }


    private AvTimeSpan narrate() {
        if (gameObject instanceof ILivingBeingGO) {
            return narrateCreature((LIVGO) gameObject);
        }

        return narrateObject();
    }

    @NonNull
    private AvTimeSpan narrateObject() {
        final PraedikatMitEinerObjektleerstelle nehmenPraedikat =
                room.storingPlaceComp().getLocationMode().getNehmenPraedikat();
        if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
            if (sc.memoryComp().getLastAction().is(Action.Type.ABLEGEN)) {
                n.add(buildStoryStateObjectNachAblegen());
                return secs(5);
            }

            final Mood mood = sc.feelingsComp().getMood();

            if (sc.memoryComp().getLastAction().is(Action.Type.HOCHWERFEN) &&
                    mood.isEmotional()) {
                n.add(t(StoryState.StructuralElement.PARAGRAPH,
                        nehmenPraedikat
                                .getDescriptionDuHauptsatz(
                                        getDescription(gameObject, true),
                                        mood.getAdverbialeAngabe()))
                        .undWartest(
                                nehmenPraedikat
                                        .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                        .persPronKandidat(gameObject)
                        .dann());
                return secs(5);
            }
        }

        n.add(t(StoryState.StructuralElement.PARAGRAPH,
                nehmenPraedikat.getDescriptionDuHauptsatz(getDescription(gameObject, true)))
                .undWartest(
                        nehmenPraedikat
                                .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                .dann());
        return secs(5);
    }

    private StoryStateBuilder buildStoryStateObjectNachAblegen() {
        if (sc.memoryComp().getLastAction().hasObject(gameObject) &&
                (sc.memoryComp().getLastAction().is(
                        Action.Type.ABLEGEN, Action.Type.HOCHWERFEN))) {
            return t(StructuralElement.PARAGRAPH,
                    "Dann nimmst du " + getDescription(gameObject).akk() +
                            " erneut")
                    .undWartest()
                    .persPronKandidat(gameObject);
        }

        return t(StoryState.StructuralElement.SENTENCE,
                "Dann nimmst du " + getDescription(gameObject).akk())
                .undWartest()
                .persPronKandidat(gameObject);

    }


    @NonNull
    private AvTimeSpan narrateCreature(final LIVGO creature) {
        checkArgument(creature.is(FROSCHPRINZ) &&
                        ((IHasStateGO) creature).stateComp()
                                .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS),
                "Unexpected creature: " + creature);

        n.add(alt(
                t(StructuralElement.PARAGRAPH,
                        "Du ekelst dich sehr, aber mit einiger Überwindung nimmst du den Frosch in "
                                + "die Hand. "
                                + "Er ist glibschig und schleimig – pfui-bäh! – schnell lässt du ihn in "
                                + "eine Tasche gleiten. Sein gedämpftes Quaken könnte wohlig sein oder "
                                + "genauso gut vorwurfsvoll"),
                t(StoryState.StructuralElement.PARAGRAPH,
                        "Den Frosch in die Hand nehmen?? – Wer hat dir bloß solche Flausen "
                                + "in den Kopf gesetzt! Kräftig packst du den Frosch und versenkst "
                                + "ihn tief in deiner Tasche. Du versuchst, deine Hand an der "
                                + "Kleidung zu reinigen, aber der Schleim verteilt sich nur "
                                + "überall – igitt!")
                        .beendet(PARAGRAPH),
                t(StoryState.StructuralElement.PARAGRAPH,
                        "Du erbarmst dich und packst den Frosch in deine Tasche. Er fasst "
                                + "sich sehr eklig an und du bist glücklich, als die Prozedur "
                                + "vorbei ist.")
                        .dann()
        ));

        return secs(20);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.NEHMEN, gameObject);
    }
}
