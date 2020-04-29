package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.storystate.StoryStateBuilder.t;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P1;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ABSETZEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINLEGEN;

/**
 * Der Benutzer legt einen Gegenstand ab.
 */
public class AblegenAction
        <GO extends IDescribableGO & ILocatableGO,
                LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
        extends AbstractScAction {
    private final IHasStoringPlaceGO room;
    @NonNull
    private final GO gameObject;

    public static <GO extends IDescribableGO & ILocatableGO,
            LIVGO extends IDescribableGO & ILocatableGO & ILivingBeingGO>
    Collection<AblegenAction<GO, LIVGO>> buildActions(
            final AvDatabase db, final StoryState initialStoryState,
            final GO gameObject,
            final IHasStoringPlaceGO room) {
        if (gameObject instanceof ILivingBeingGO &&
                !(gameObject.is(FROSCHPRINZ))) {
            return ImmutableList.of();
        }

        return ImmutableList
                .of(new AblegenAction<>(db, initialStoryState, gameObject, room));
    }

    private AblegenAction(final AvDatabase db,
                          final StoryState initialStoryState,
                          final @NonNull GO gameObject,
                          final IHasStoringPlaceGO room) {
        super(db, initialStoryState);
        this.room = room;
        this.gameObject = gameObject;
    }

    @Override
    @NonNull
    public String getName() {
        final PraedikatMitEinerObjektleerstelle praedikat =
                gameObject instanceof ILivingBeingGO ? ABSETZEN : HINLEGEN;

        return capitalize(
                praedikat.mitObj(getDescription(gameObject, true))
                        .getDescriptionInfinitiv(P1, SG));
    }

    @Override
    public String getType() {
        return "actionAblegen";
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        AvTimeSpan timeElapsed = narrate();
        letGoAndAddToRoomCreatureOrObject();

        timeElapsed =
                timeElapsed.plus(creatureReactionsCoordinator.onAblegen(room, gameObject));

        return timeElapsed;
    }

    private void letGoAndAddToRoomCreatureOrObject() {
        if (gameObject instanceof ILivingBeingGO) {
            letGoAndAddToRoomCreature((LIVGO) gameObject);
        } else {
            letGoAndAddToRoom();
        }
    }

    private void letGoAndAddToRoomCreature(final LIVGO creature) {
        checkArgument(creature.is(FROSCHPRINZ),
                "Unexpected creature: " + creature);

        letGoAndAddToRoom();
        sc.feelingsComp().setMood(Mood.NEUTRAL);
    }

    private void letGoAndAddToRoom() {
        sc.memoryComp().upgradeKnown(gameObject, Known.getKnown(getLichtverhaeltnisse(room)));
        gameObject.locationComp().setLocation(room);
        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    private AvTimeSpan narrate() {
        if (gameObject instanceof ILivingBeingGO) {
            return narrateCreature((LIVGO) gameObject);
        }

        return narrateObject();
    }

    private AvTimeSpan narrateObject() {
        final Nominalphrase objDesc = getDescription(gameObject, false);

        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            if (initialStoryState.persPronKandidatIs(gameObject)) {
                if (sc.memoryComp().lastActionWas(Action.Type.NEHMEN, gameObject)) {
                    return n.add(satzanschluss(
                            "- und legst "
                                    + objDesc.persPron().akk()
                                    + " sogleich wieder hin", secs(3)));
                }

                if (sc.memoryComp().getLastAction().hasObject(gameObject)) {
                    return n.add(satzanschluss(", dann legst du "
                            + objDesc.persPron().akk()
                            + " hin", secs(5))
                            .undWartest());
                }

                String text = "und legst " + objDesc.akk();
                if (sc.memoryComp().getLastAction().is(Action.Type.BEWEGEN)) {
                    text += " dort";
                }

                text += " hin";

                n.add(t(StructuralElement.WORD, text));
                return secs(3);
            }
        }

        if (sc.memoryComp().lastActionWas(Action.Type.NEHMEN, gameObject)) {
            return n.add(
                    du(PARAGRAPH, "legst", objDesc.akk() + " wieder hin", secs(5))
                            .undWartest()
                            .dann());
        }

        return n.add(
                du(PARAGRAPH, "legst", objDesc.akk() + " hin", secs(3))
                        .undWartest()
                        .dann());
    }

    private AvTimeSpan narrateCreature(final LIVGO creature) {
        checkArgument(creature.is(FROSCHPRINZ),
                "Unexpected creature data: " + creature);

        n.add(alt(
                t(StructuralElement.PARAGRAPH,
                        "Du wühlst in deiner Tasche und auf einmal schauert's dich und "
                                + "der nasse Frosch sitzt in deiner Hand. Schnell "
                                + room.storingPlaceComp().getLocationMode().getWohin()
                                + " mit ihm!")
                        .dann()
                        .beendet(PARAGRAPH),
                t(StructuralElement.PARAGRAPH,
                        "Du schüttest deine Tasche aus, bis der Frosch endlich " +
                                room.storingPlaceComp().getLocationMode().getWohin() +
                                " fällt. Puh.")
                        .dann()
                        .beendet(PARAGRAPH),
                t(StructuralElement.PARAGRAPH,
                        "Du wühlst in deiner Tasche. Da quakt es erbost, auf einmal "
                                + "springt der Fosch heraus und direkt "
                                + room.storingPlaceComp().getLocationMode().getWohin()
                )
        ));

        return secs(7);
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.ABLEGEN, gameObject);
    }
}
