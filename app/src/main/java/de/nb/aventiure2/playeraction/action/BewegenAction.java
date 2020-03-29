package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.AbstractDescription;
import de.nb.aventiure2.german.DuDescription;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;
import de.nb.aventiure2.playeraction.action.room.connection.RoomConnection;
import de.nb.aventiure2.playeraction.action.room.connection.RoomConnections;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.room.AvRoom.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.DuDescription.du;
import static de.nb.aventiure2.german.base.AllgDescription.allg;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.uncapitalize;
import static de.nb.aventiure2.playeraction.action.BewegenAction.NumberOfPossibilities.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.playeraction.action.BewegenAction.NumberOfPossibilities.ONLY_WAY;
import static de.nb.aventiure2.playeraction.action.BewegenAction.NumberOfPossibilities.SEVERAL_WAYS;

/**
 * Der Spielercharakter bewegt sich in einen anderen Raum.
 */
public class BewegenAction extends AbstractPlayerAction {
    enum NumberOfPossibilities {
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

    private final RoomConnection roomConnection;
    private final NumberOfPossibilities numberOfPossibilities;

    public static ImmutableList<AbstractPlayerAction> buildActions(
            final AvDatabase db,
            final StoryState currentStoryState,
            final AvRoom room) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();

        final List<RoomConnection> roomConnections = RoomConnections.getFrom(room);

        final BewegenAction.NumberOfPossibilities numberOfPossibilities =
                calcNumberOfPossibilities(roomConnections.size());

        for (final RoomConnection roomConnection : roomConnections) {
            res.add(new BewegenAction(db, currentStoryState, room,
                    roomConnection, numberOfPossibilities));
        }
        return res.build();
    }

    private static BewegenAction.NumberOfPossibilities calcNumberOfPossibilities(
            final int numericalNumber) {
        switch (numericalNumber) {
            case 1:
                return ONLY_WAY;
            case 2:
                return ONE_IN_ONE_OUT;
            default:
                return SEVERAL_WAYS;
        }
    }

    /**
     * Creates a new {@link BewegenAction}.
     */
    private BewegenAction(final AvDatabase db,
                          final StoryState initialStoryState,
                          final AvRoom oldRoom,
                          final RoomConnection roomConnection,
                          final NumberOfPossibilities numberOfPossibilities) {
        super(db, initialStoryState);
        this.numberOfPossibilities = numberOfPossibilities;

        checkArgument(roomConnection.getTo() != oldRoom,
                "newRoom == oldRoom)");

        this.oldRoom = oldRoom;
        this.roomConnection = roomConnection;
    }

    @Override
    public String getType() {
        return "actionBewegen";
    }

    @Override
    @NonNull
    public String getName() {
        return roomConnection.getActionName();
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        final List<CreatureData> creaturesInOldRoom =
                db.creatureDataDao().getCreaturesInRoom(oldRoom);

        final List<ObjectData> objectsInNewRoom =
                db.objectDataDao().getObjectsInRoom(roomConnection.getTo());
        final List<CreatureData> creaturesInNewRoom =
                db.creatureDataDao().getCreaturesInRoom(roomConnection.getTo());

        AvTimeSpan elapsedTime = narrateRoomOnly();

        if (oldRoom == SCHLOSS_VORHALLE && roomConnection.getTo() == DRAUSSEN_VOR_DEM_SCHLOSS
                && db.playerStatsDao().getPlayerStats().getStateOfMind()
                == PlayerStateOfMind.ANGESPANNT) {
            db.playerStatsDao().setStateOfMind(PlayerStateOfMind.NEUTRAL);
        }

        elapsedTime = elapsedTime.plus(creatureReactionsCoordinator
                .onLeaveRoom(oldRoom, creaturesInOldRoom));

        if (!objectsInNewRoom.isEmpty()) {
            elapsedTime = elapsedTime.plus(narrateObjects(objectsInNewRoom));
        }

        db.playerLocationDao().setRoom(roomConnection.getTo());

        setRoomAndObjectsKnown(objectsInNewRoom);

        return elapsedTime.plus(creatureReactionsCoordinator
                .onEnterRoom(oldRoom, roomConnection.getTo(), creaturesInNewRoom));
    }

    private AvTimeSpan narrateObjects(final List<ObjectData> objectsInNewRoom) {
        n.add(t(StructuralElement.SENTENCE,
                buildObjectsInRoomDescription(objectsInNewRoom))
                .letztesObject(objectsInNewRoom.get(objectsInNewRoom.size() - 1).getObject()));

        return secs(objectsInNewRoom.size() * 2);
    }

    private AvTimeSpan narrateRoomOnly() {
        final AbstractDescription description = getMovementDescription(initialStoryState);
        n.add(buildNewStoryStateRoomOnly(initialStoryState, description));

        return description.getTimeElapsed();
    }

    private AbstractDescription getMovementDescription(final StoryState currentStoryState) {
        final boolean newRoomKnown = db.roomDao().isKnown(roomConnection.getTo());

        final AbstractDescription standardDescription =
                roomConnection.getDescription(newRoomKnown);

        if (newRoomKnown) {
            if (numberOfPossibilities == NumberOfPossibilities.ONLY_WAY) {
                if (db.playerStatsDao().getPlayerStats().getStateOfMind() ==
                        PlayerStateOfMind.VOLLER_FREUDE &&
                        currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                        currentStoryState.lastActionWas(NehmenAction.class)) {
                    return du("springst", "damit fort",
                            false, true,
                            true, standardDescription.getTimeElapsed().times(0.8));
                }

                if (db.playerStatsDao().getPlayerStats().getStateOfMind() ==
                        PlayerStateOfMind.UNTROESTLICH) {
                    return allg("Tieftraurig trottest du von dannen",
                            false, true,
                            false, standardDescription.getTimeElapsed().times(2));
                }
            } else if (numberOfPossibilities == NumberOfPossibilities.ONE_IN_ONE_OUT
                    && currentStoryState.lastActionWas(BewegenAction.class) &&
                    !lastRoomWasNewRoom(currentStoryState) &&
                    db.playerStatsDao().getPlayerStats().getStateOfMind() ==
                            PlayerStateOfMind.VOLLER_FREUDE &&
                    currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                return du("eilst", "weiter",
                        false, true,
                        false, standardDescription.getTimeElapsed().times(0.8));
            }
        }

        return standardDescription;
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
                lastRoomWasNewRoom(currentStoryState)) {
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
        if (lastRoomWasNewRoom(currentStoryState) &&
                numberOfPossibilities != NumberOfPossibilities.ONLY_WAY) {
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

    private boolean lastRoomWasNewRoom(final StoryState currentStoryState) {
        return currentStoryState.lastRoomWas(roomConnection.getTo());
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
        final String res = capitalize(roomConnection.getTo().getLocationMode().getWo());

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
        db.roomDao().setKnown(roomConnection.getTo());
        for (final ObjectData objectInNewRoom : objectsInNewRoom) {
            db.objectDataDao().setKnown(objectInNewRoom.getObject());
        }
    }
}