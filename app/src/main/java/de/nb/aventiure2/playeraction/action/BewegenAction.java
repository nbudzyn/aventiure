package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.connection.RoomConnection;
import de.nb.aventiure2.german.AbstractDescription;
import de.nb.aventiure2.german.DuDescription;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.room.AvRoom.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.german.DuDescription.du;
import static de.nb.aventiure2.german.base.AllgDescription.allg;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.uncapitalize;

/**
 * Der Spieler(charakter) bewegt sich in einen anderen Raum.
 */
public class BewegenAction extends AbstractPlayerAction {
    public enum NumberOfPossibilities {
        /**
         * Whether this is the only way the SC could take
         */
        ONLY_WAY,
        /**
         * There have been two movement possibilities for the player to choose
         * from
         */
        ONE_IN_ONE_OUT,
        /**
         * There have been several ways
         */
        SEVERAL_WAYS
    }

    private final AvRoom oldRoom;
    private final AvRoom newRoom;

    private final NumberOfPossibilities numberOfPossibilities;

    /**
     * Creates a new {@link BewegenAction}.
     */
    private BewegenAction(final AvDatabase db,
                          final StoryState initialStoryState,
                          final AvRoom oldRoom,
                          final AvRoom newRoom,
                          final NumberOfPossibilities numberOfPossibilities) {
        super(db, initialStoryState);
        this.numberOfPossibilities = numberOfPossibilities;

        checkArgument(newRoom != oldRoom, "newRoom == oldRoom)");

        this.oldRoom = oldRoom;
        this.newRoom = newRoom;
    }

    public static AbstractPlayerAction buildAction(
            final AvDatabase db,
            final StoryState initialStoryState,
            final AvRoom room, final AvRoom connectedRoom,
            final NumberOfPossibilities numberOfPossibilities) {
        return new BewegenAction(db, initialStoryState, room,
                connectedRoom, numberOfPossibilities);
    }

    @Override
    @NonNull
    public String getName() {
        return RoomConnection.getFrom(oldRoom).get(newRoom).getActionName();
    }

    @Override
    public void narrateAndDo() {
        final List<CreatureData> creaturesInOldRoom =
                db.creatureDataDao().getCreaturesInRoom(oldRoom);

        final List<ObjectData> objectsInNewRoom = db.objectDataDao().getObjectsInRoom(newRoom);
        final List<CreatureData> creaturesInNewRoom =
                db.creatureDataDao().getCreaturesInRoom(newRoom);

        n.add(buildNewStoryStateRoomOnly(initialStoryState));

        if (oldRoom == SCHLOSS_VORHALLE && newRoom == DRAUSSEN_VOR_DEM_SCHLOSS
                && db.playerStatsDao().getPlayerStats().getStateOfMind()
                == PlayerStateOfMind.ANGESPANNT) {
            db.playerStatsDao().setStateOfMind(PlayerStateOfMind.NEUTRAL);
        }

        creatureReactionsCoordinator.onLeaveRoom(oldRoom, creaturesInOldRoom);

        if (!objectsInNewRoom.isEmpty()) {
            n.add(buildObjectsStoryState(objectsInNewRoom));
        }

        db.playerLocationDao().

                setRoom(newRoom);

        setRoomAndObjectsKnown(objectsInNewRoom);

        creatureReactionsCoordinator.onEnterRoom(oldRoom, newRoom, creaturesInNewRoom);
    }

    private StoryStateBuilder buildNewStoryStateRoomOnly(final StoryState currentStoryState) {
        final AbstractDescription description = getMovementDescription(currentStoryState);
        return buildNewStoryStateRoomOnly(currentStoryState, description);
    }

    private AbstractDescription getMovementDescription(final StoryState currentStoryState) {
        final boolean newRoomKnown = db.roomDao().isKnown(newRoom);

        if (newRoomKnown) {
            if (numberOfPossibilities == NumberOfPossibilities.ONLY_WAY) {
                if (db.playerStatsDao().getPlayerStats().getStateOfMind() ==
                        PlayerStateOfMind.VOLLER_FREUDE &&
                        currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                        currentStoryState.lastActionWas(NehmenAction.class)) {
                    return du("springst", "damit fort",
                            false, true,
                            true);
                }

                if (db.playerStatsDao().getPlayerStats().getStateOfMind() ==
                        PlayerStateOfMind.UNTROESTLICH) {
                    return allg("Tieftraurig trottest du von dannen",
                            false, true, false);
                }
            } else if (numberOfPossibilities == NumberOfPossibilities.ONE_IN_ONE_OUT
                    && currentStoryState.lastActionWas(BewegenAction.class) &&
                    !currentStoryState.lastRoomWas(newRoom) &&
                    db.playerStatsDao().getPlayerStats().getStateOfMind() ==
                            PlayerStateOfMind.VOLLER_FREUDE &&
                    currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                return du("eilst", "weiter",
                        false, true,
                        false);
            }
        }

        return RoomConnection.getFrom(oldRoom).get(newRoom)
                .getDescription(db.roomDao().isKnown(newRoom));
    }

    /**
     * Gets the description for entering the new room.
     */
    private StoryStateBuilder buildNewStoryStateRoomOnly(
            final StoryState currentStoryState,
            final AbstractDescription description) {
        if (description instanceof DuDescription) {
            final DuDescription duDescription =
                    (DuDescription) description;

            if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                return buildNewStoryStateSatzanschluss(currentStoryState, duDescription);
            }
        }

        return buildNewStoryStateRoomOnlyNewSentence(currentStoryState, description);
    }

    private StoryStateBuilder buildNewStoryStateSatzanschluss(
            final StoryState currentStoryState, final DuDescription duDesc) {
        if (currentStoryState.lastActionWas(BewegenAction.class) &&
                currentStoryState.lastRoomWas(newRoom)) {
            return t(StructuralElement.WORD,
                    ", besinnst dich aber und "
                            + duDesc.getDescriptionSatzanschlussOhneSubjekt())
                    .dann(duDesc.dann());
        }

        return t(StoryState.StructuralElement.WORD,
                "und " +
                        duDesc.getDescriptionSatzanschlussOhneSubjekt())
                .dann(duDesc.dann());
    }

    private StoryStateBuilder buildNewStoryStateRoomOnlyNewSentence(
            final StoryState currentStoryState, final AbstractDescription desc) {
        if (currentStoryState.lastActionWas(BewegenAction.class)) {
            return buildNewStoryStateNewRoomOnlyNewSentenceLastActionBewegen(currentStoryState,
                    desc);
        }

        return buildNewStoryStateNewRoomOnlyNewSentenceLastActionNichtBewegen(currentStoryState,
                desc);
    }

    private StoryStateBuilder buildNewStoryStateNewRoomOnlyNewSentenceLastActionBewegen(
            final StoryState currentStoryState, final AbstractDescription desc) {
        if (currentStoryState.lastRoomWas(newRoom)) {
            if (currentStoryState.noLastObject()) {
                return alt(
                        t(StoryState.StructuralElement.SENTENCE, "Was willst du hier eigentlich? "
                                + desc.getDescriptionHauptsatz()),
                        t(StoryState.StructuralElement.SENTENCE, "Aber dir kommt ein Gedanke und "
                                + uncapitalize(desc.getDescriptionHauptsatz())));
            }

            return t(StoryState.StructuralElement.PARAGRAPH,
                    "Du schaust dich nur kurz um, dann "
                            + uncapitalize(desc.getDescriptionHauptsatz()))
                    .komma(desc.kommaStehtAus())
                    .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt());
        }

        if (currentStoryState.dann()) {
            // "Du stehst wieder vor dem Schloss; dann gehst du wieder hinein in das Schloss."
            return t(StoryState.StructuralElement.WORD,
                    "; " +
                            uncapitalize(
                                    desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
                                            "dann")));
        }

        return t(StoryState.StructuralElement.PARAGRAPH,
                desc.getDescriptionHauptsatz())
                .komma(desc.kommaStehtAus())
                .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.dann());
    }

    private StoryStateBuilder buildNewStoryStateNewRoomOnlyNewSentenceLastActionNichtBewegen(
            final StoryState currentStoryState, final AbstractDescription desc) {
        if (currentStoryState.dann()) {
            return t(StoryState.StructuralElement.PARAGRAPH,
                    desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("danach"))
                    .komma(desc.kommaStehtAus())
                    .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                    .dann(false);
        }

        return t(StoryState.StructuralElement.PARAGRAPH,
                desc.getDescriptionHauptsatz())
                .komma(desc.kommaStehtAus())
                .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.dann());
    }

    private StoryStateBuilder buildObjectsStoryState(final List<ObjectData> objectsInNewRoom) {
        return t(StoryState.StructuralElement.SENTENCE,
                buildObjectsInRoomDescription(objectsInNewRoom))
                .letztesObject(objectsInNewRoom.get(objectsInNewRoom.size() - 1).getObject());
    }

    /**
     * @return Something similar to <code>Auf dem Boden liegt eine goldene Kugel.</code>
     */
    private String buildObjectsInRoomDescription(final List<ObjectData> objectsInRoom) {
        return buildObjectInRoomDescriptionPrefix(objectsInRoom.size())
                + " "
                + buildEntitiesInRoomDescriptionList(objectsInRoom);
    }

    /**
     * @return Something similar to <code>Hier liegt</code>
     */
    private String buildObjectInRoomDescriptionPrefix(final int numberOfObjects) {
        final String res = capitalize(newRoom.getLocationMode().getWo());

        if (numberOfObjects == 1) {
            return res + " liegt";
        }

        return res + " liegen";
    }

    /**
     * @return Something similar to <code>der hässliche Frosch</code>
     */
    private static String buildEntitiesInRoomDescriptionList(
            final List<? extends AbstractEntityData> entities) {
        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < entities.size(); i++) {
            res.append(entities.get(i).getDescription(false).nom());
            if (i == entities.size() - 2) {
                // one before the last
                res.append(" und ");
            }
            if (i < entities.size() - 2) {
                // more than one after this
                res.append(", ");
            }
        }

        return res.toString();
    }

    @Override
    protected StoryStateBuilder t(
            @NonNull final StructuralElement startsNew,
            @NonNull final String text) {
        return super.t(startsNew, text)
                .letzterRaum(oldRoom);
    }

    private void setRoomAndObjectsKnown(final List<ObjectData> objectsInNewRoom) {
        db.roomDao().setKnown(newRoom);
        for (final ObjectData objectInNewRoom : objectsInNewRoom) {
            db.objectDataDao().setKnown(objectInNewRoom.getObject());
        }
    }
}