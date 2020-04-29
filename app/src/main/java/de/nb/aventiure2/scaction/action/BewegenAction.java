package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.DuDescription;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.action.room.connection.RoomConnection;
import de.nb.aventiure2.scaction.action.room.connection.RoomConnections;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.storystate.StoryStateBuilder.t;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadDescribableLivingInventory;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadDescribableNonLivingInventory;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.uncapitalize;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.scaction.action.BewegenAction.NumberOfPossibilities.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.scaction.action.BewegenAction.NumberOfPossibilities.ONLY_WAY;
import static de.nb.aventiure2.scaction.action.BewegenAction.NumberOfPossibilities.SEVERAL_WAYS;

/**
 * Der Spielercharakter bewegt sich in einen anderen Raum.
 */
public class BewegenAction<R extends ISpatiallyConnectedGO & IHasStoringPlaceGO,
        LOC_DESC extends ILocatableGO & IDescribableGO,
        LIV extends ILocatableGO & IDescribableGO & ILivingBeingGO>
        extends AbstractScAction {
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

    private final R oldRoom;

    private final RoomConnection roomConnection;
    private final NumberOfPossibilities numberOfPossibilities;

    public static <R extends ISpatiallyConnectedGO & IHasStoringPlaceGO>
    ImmutableList<AbstractScAction> buildActions(
            final AvDatabase db,
            final StoryState currentStoryState,
            final R room) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();

        final List<RoomConnection> roomConnections =
                RoomConnections.getFrom(db, room);

        final BewegenAction.NumberOfPossibilities numberOfPossibilities =
                calcNumberOfPossibilities(roomConnections.size());

        for (final RoomConnection roomConnection : roomConnections) {
            res.add(new BewegenAction<>(db, currentStoryState, room,
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
                          final R oldRoom,
                          final RoomConnection roomConnection,
                          final NumberOfPossibilities numberOfPossibilities) {
        super(db, initialStoryState);
        this.numberOfPossibilities = numberOfPossibilities;

        checkArgument(!oldRoom.is(roomConnection.getTo()), "newRoom == oldRoom)");

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
        final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom =
                getLichtverhaeltnisse(roomConnection.getTo());

        final ImmutableList<LIV> creaturesInOldRoom = loadDescribableLivingInventory(db, oldRoom);
        final ImmutableList<LOC_DESC> objectsInNewRoom =
                loadDescribableNonLivingInventory(db, roomConnection.getTo());
        final ImmutableList<LIV> creaturesInNewRoom =
                loadDescribableLivingInventory(db, roomConnection.getTo());

        AvTimeSpan elapsedTime = narrateRoomOnly(lichtverhaeltnisseInNewRoom);

        if (scWirdMitEssenKonfrontiert()) {
            elapsedTime = elapsedTime.plus(scAutomaticReactions
                    .onWirdMitEssenKonfrontiert());
        }

        //  hier prüfen, ob der TO-Raum etwas zu essen enthält

        updatePlayerStateOfMind();

        elapsedTime = elapsedTime.plus(creatureReactionsCoordinator
                .onLeaveRoom(oldRoom, creaturesInOldRoom));

        if (!objectsInNewRoom.isEmpty()) {
            elapsedTime = elapsedTime.plus(narrateObjects(objectsInNewRoom));
        }

        sc.locationComp().setLocation(roomConnection.getTo());
        sc.memoryComp().setLastAction(buildMemorizedAction());

        setRoomAndObjectsKnown(objectsInNewRoom, lichtverhaeltnisseInNewRoom);

        final GameObject toGameObject = load(db, roomConnection.getTo());
        if (toGameObject instanceof IHasStoringPlaceGO) {
            elapsedTime = elapsedTime.plus(creatureReactionsCoordinator
                    .onEnterRoom(oldRoom,
                            (IHasStoringPlaceGO) toGameObject, creaturesInNewRoom));

        }
        return elapsedTime;
    }

    private boolean scWirdMitEssenKonfrontiert() {
        final GameObject newRoom = load(db, roomConnection.getTo());

        if (((IHasStateGO) load(db, SCHLOSSFEST)).stateComp().hasState(BEGONNEN)) {
            if (oldRoom.is(DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    newRoom.is(SCHLOSS_VORHALLE)) {
                return true;
            }
            if (newRoom.is(SCHLOSS_VORHALLE_TISCH_BEIM_FEST)) {
                return true;
            }
        }

        if (newRoom.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            // STORY Im Dunkeln kann man keine Früchte sehen
            return true;
        }

        return false;
    }

    /**
     * Aktualisiert den Gemütszustand des Spielercharakters. "Zeit heilt alle Wunden" - oder so
     * ähnlich.
     */
    private void updatePlayerStateOfMind() {
        final Mood mood = sc.feelingsComp().getMood();
        if (oldRoom.is(SCHLOSS_VORHALLE)
                && roomConnection.getTo().equals(DRAUSSEN_VOR_DEM_SCHLOSS)
                && mood == Mood.ANGESPANNT) {
            sc.feelingsComp().setMood(Mood.NEUTRAL);
        } else if (mood == Mood.ETWAS_GEKNICKT) {
            sc.feelingsComp().setMood(Mood.NEUTRAL);
        }
    }

    @NonNull
    private AvTimeSpan narrateObjects(final List<? extends IDescribableGO> objectsInNewRoom) {
        n.add(t(StructuralElement.SENTENCE,
                buildObjectsInRoomDescription(objectsInNewRoom))
                .persPronKandidat(objectsInNewRoom.get(objectsInNewRoom.size() - 1)));

        return secs(objectsInNewRoom.size() * 2);
    }

    private AvTimeSpan narrateRoomOnly(final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        final AbstractDescription description = getMovementDescription(initialStoryState,
                lichtverhaeltnisseInNewRoom);

        if (description instanceof DuDescription && initialStoryState
                .allowsAdditionalDuSatzreihengliedOhneSubjekt() && sc.memoryComp().getLastAction()
                .is(Action.Type.BEWEGEN) &&
                sc.locationComp().lastLocationWas(roomConnection.getTo())) {
            return n.add(
                    satzanschluss(", besinnst dich aber und "
                                    + ((DuDescription) description)
                                    .getDescriptionSatzanschlussOhneSubjekt(),
                            description.getTimeElapsed())
                            .dann(description.isDann())
                            .komma(description.isKommaStehtAus()));
        }

        if (description.getStartsNew() == WORD &&
                initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                description instanceof DuDescription) {
            return n.add(description);
        }

        if (sc.memoryComp().lastActionWas(Action.Type.BEWEGEN, (GameObjectId) null)) {
            if (sc.locationComp().lastLocationWas(roomConnection.getTo()) &&
                    numberOfPossibilities != NumberOfPossibilities.ONLY_WAY) {
                if (sc.memoryComp().getLastAction().is(Action.Type.BEWEGEN)) {
                    final ImmutableList.Builder<StoryStateBuilder> alt = ImmutableList.builder();
                    alt.add(t(StructuralElement.SENTENCE,
                            "Was willst du hier eigentlich? "
                                    + description.getDescriptionHauptsatz()));
                    if (description instanceof DuDescription) {
                        alt.add(t(StructuralElement.SENTENCE,
                                "Was willst du hier eigentlich? "
                                        + ((DuDescription) description)
                                        .getDescriptionHauptsatzMitSpeziellemVorfeld()));
                    }

                    alt.add(t(StructuralElement.SENTENCE,
                            "Aber dir kommt ein Gedanke und "
                                    + uncapitalize(description.getDescriptionHauptsatz())));


                    n.add(alt(alt));

                    return description.getTimeElapsed();
                } else {
                    return n.add(
                            du("schaust", "dich nur kurz um, dann "
                                            + uncapitalize(description.getDescriptionHauptsatz()),
                                    description.getTimeElapsed())
                                    .komma(description.isKommaStehtAus())
                                    .undWartest(
                                            description
                                                    .isAllowsAdditionalDuSatzreihengliedOhneSubjekt()));
                }
            } else if (initialStoryState.dann()) {
                // "Du stehst wieder vor dem Schloss; dann gehst du wieder hinein in das Schloss."
                return n.add(
                        satzanschluss(
                                "; " +
                                        uncapitalize(
                                                description
                                                        .getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
                                                                "dann")),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus()));
            } else {
                return n.add(description);
            }
        } else {
            if (initialStoryState.dann()) {
                return n.add(
                        neuerSatz(StructuralElement.PARAGRAPH, description
                                        .getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("danach"),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus())
                                .undWartest(description
                                        .isAllowsAdditionalDuSatzreihengliedOhneSubjekt()));
            }

            return n.add(description);
        }
    }

    private AbstractDescription getMovementDescription(final StoryState currentStoryState,
                                                       final Lichtverhaeltnisse
                                                               lichtverhaeltnisseInNewRoom) {
        final Known newRoomKnown = sc.memoryComp().getKnown(roomConnection.getTo());

        final AbstractDescription standardDescription =
                roomConnection.getDescription(newRoomKnown, lichtverhaeltnisseInNewRoom);

        if (newRoomKnown == Known.KNOWN_FROM_LIGHT) {
            if (numberOfPossibilities == NumberOfPossibilities.ONLY_WAY) {
                if (sc.feelingsComp().hasMood(Mood.VOLLER_FREUDE) &&
                        lichtverhaeltnisseInNewRoom == HELL &&
                        currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                        sc.memoryComp().getLastAction().is(Action.Type.NEHMEN)) {
                    return du("springst", "damit fort", "damit",
                            standardDescription.getTimeElapsed().times(0.8))
                            .undWartest()
                            .dann();
                }

                if (sc.feelingsComp().hasMood(Mood.UNTROESTLICH)) {
                    return du("trottest", "tieftraurig von dannen", "tieftraurig",
                            standardDescription.getTimeElapsed().times(2))
                            .undWartest();
                }
            } else if (numberOfPossibilities == NumberOfPossibilities.ONE_IN_ONE_OUT
                    && sc.memoryComp().getLastAction().is(Action.Type.BEWEGEN) &&
                    !sc.locationComp().lastLocationWas(roomConnection.getTo()) &&
                    sc.feelingsComp().hasMood(Mood.VOLLER_FREUDE) &&
                    lichtverhaeltnisseInNewRoom ==
                            HELL &&
                    currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
                return du("eilst", "weiter", standardDescription.getTimeElapsed().times(0.8))
                        .undWartest();
            }
        }

        return standardDescription;
    }

    /**
     * @return Something similar to <code>Auf dem Boden liegt eine goldene Kugel.</code>
     */
    @NonNull
    private String buildObjectsInRoomDescription(
            @NonNull final List<? extends IDescribableGO> objectsInRoom) {
        return buildObjectInRoomDescriptionPrefix(objectsInRoom.size())
                + " "
                + buildEntitiesInRoomDescriptionList(objectsInRoom);
    }

    /**
     * @return Something similar to <code>Hier liegt</code>
     */
    @NonNull
    private String buildObjectInRoomDescriptionPrefix(final int numberOfObjects) {
        final String res =
                capitalize(
                        ((IHasStoringPlaceGO) load(db, roomConnection.getTo()))
                                .storingPlaceComp().getLocationMode().getWo());

        if (numberOfObjects == 1) {
            return res + " liegt";
        }

        return res + " liegen";
    }

    /**
     * @return Something similar to <code>der hässliche Frosch</code>
     */
    @NonNull
    private String buildEntitiesInRoomDescriptionList(
            @NonNull final List<? extends IDescribableGO> entities) {
        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < entities.size(); i++) {
            res.append(getDescription(entities.get(i), false).nom());
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
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.BEWEGEN, (IGameObject) null);
    }

    private void setRoomAndObjectsKnown(
            @NonNull final List<? extends IGameObject> objectsInNewRoom,
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        final Known known = Known.getKnown(lichtverhaeltnisse);

        sc.memoryComp().upgradeKnown(roomConnection.getTo(), known);

        for (final IGameObject objectInNewRoom : objectsInNewRoom) {
            sc.memoryComp().upgradeKnown(objectInNewRoom, known);
        }
    }
}