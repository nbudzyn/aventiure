package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.function.Function;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.ISpatiallyConnectedGO;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.impl.SpatialConnection;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.DuDescription;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.scaction.AbstractScAction;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadDescribableNonLivingMovableRecursiveInventory;
import static de.nb.aventiure2.data.world.syscomp.memory.Action.Type.BEWEGEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.GermanUtil.uncapitalize;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.scaction.impl.BewegenAction.NumberOfPossibilities.ONE_IN_ONE_OUT;
import static de.nb.aventiure2.scaction.impl.BewegenAction.NumberOfPossibilities.ONLY_WAY;
import static de.nb.aventiure2.scaction.impl.BewegenAction.NumberOfPossibilities.SEVERAL_WAYS;

/**
 * Der Spielercharakter bewegt sich in einen anderen Raum.
 */
public class BewegenAction<R extends ISpatiallyConnectedGO & ILocationGO,
        LOC_DESC extends ILocatableGO & IDescribableGO>
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

    private final SpatialConnection spatialConnection;
    private final NumberOfPossibilities numberOfPossibilities;

    public static <R extends ISpatiallyConnectedGO & ILocationGO>
    ImmutableList<AbstractScAction> buildActions(
            final AvDatabase db,
            final StoryState currentStoryState,
            final R room) {
        final ImmutableList.Builder<AbstractScAction> res = ImmutableList.builder();


        final List<SpatialConnection> spatialConnections =
                room.spatialConnectionComp().getConnections();

        final NumberOfPossibilities numberOfPossibilities =
                calcNumberOfPossibilities(spatialConnections.size());

        for (final SpatialConnection spatialConnection : spatialConnections) {
            res.add(new BewegenAction<>(db, currentStoryState, room,
                    spatialConnection, numberOfPossibilities));
        }
        return res.build();
    }

    private static NumberOfPossibilities calcNumberOfPossibilities(
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
                          final SpatialConnection spatialConnection,
                          final NumberOfPossibilities numberOfPossibilities) {
        super(db, initialStoryState);
        this.numberOfPossibilities = numberOfPossibilities;

        checkArgument(!oldRoom.is(spatialConnection.getTo()), "newRoom == oldRoom)");

        this.oldRoom = oldRoom;
        this.spatialConnection = spatialConnection;
    }

    @Override
    public String getType() {
        return "actionBewegen";
    }

    @Override
    @NonNull
    public String getName() {
        return spatialConnection.getActionName();
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        final ILocationGO to =
                (ILocationGO) load(db, spatialConnection.getTo());

        final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom =
                to.storingPlaceComp().getLichtverhaeltnisse();

        // Unbewegliche Objekte sollen in der Raumbeschreibung mitgenannt werden!
        final ImmutableList<LOC_DESC> movableObjectsInNewRoom =
                loadDescribableNonLivingMovableRecursiveInventory(db, spatialConnection.getTo());

        AvTimeSpan elapsedTime = narrateAndDoRoomOnly(lichtverhaeltnisseInNewRoom);

        if (scWirdMitEssenKonfrontiert()) {
            elapsedTime = elapsedTime.plus(narrateAndDoSCMitEssenKonfrontiert());
        }

        updatePlayerStateOfMind();

        return elapsedTime.plus(sc.locationComp()
                .narrateAndSetLocation(spatialConnection.getTo(),
                        () -> {
                            AvTimeSpan elapsedTimeOnEnter = noTime();
                            if (!movableObjectsInNewRoom.isEmpty()) {
                                elapsedTimeOnEnter = elapsedTimeOnEnter.plus(
                                        narrateObjects(movableObjectsInNewRoom));
                            }

                            sc.memoryComp().setLastAction(buildMemorizedAction());

                            setRoomAndObjectsKnown(movableObjectsInNewRoom,
                                    lichtverhaeltnisseInNewRoom);
                            return elapsedTimeOnEnter;
                        }));
    }

    private boolean scWirdMitEssenKonfrontiert() {
        final GameObject newRoom = load(db, spatialConnection.getTo());

        if (((IHasStateGO) load(db, SCHLOSSFEST)).stateComp().hasState(BEGONNEN)) {
            if (oldRoom.is(DRAUSSEN_VOR_DEM_SCHLOSS) &&
                    newRoom.is(SCHLOSS_VORHALLE)) {
                return true;
            }
            if (newRoom.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
                return true;
            }
        }

        if (newRoom.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            // STORY Im Dunkeln kann man keine Früchte sehen
            return true;
        }

        return false;
    }

    private AvTimeSpan narrateAndDoSCMitEssenKonfrontiert() {
        final Hunger hunger = sc.feelingsComp().getHunger();
        switch (hunger) {
            case SATT:
                return noTime();
            case HUNGRIG:
                return narrateAnDoSCMitEssenKonfrontiertReagiertHungrig();
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private AvTimeSpan narrateAnDoSCMitEssenKonfrontiertReagiertHungrig() {
        return n.addAlt(
                neuerSatz("Mmh!", noTime()),
                neuerSatz("Dir läuft das Wasser im Munde zusammen", noTime()),
                du(SENTENCE, "hast", "Hunger", noTime())
                        .undWartest(),
                du(SENTENCE, "bist", "hungrig", noTime())
                        .undWartest(),
                neuerSatz("Dir fällt auf, wie hungrig du bist", noTime())
                        .komma()
        );
    }

    /**
     * Aktualisiert den Gemütszustand des Spielercharakters. "Zeit heilt alle Wunden" - oder so
     * ähnlich.
     */
    private void updatePlayerStateOfMind() {
        final Mood mood = sc.feelingsComp().getMood();
        if (oldRoom.is(SCHLOSS_VORHALLE)
                && spatialConnection.getTo().equals(DRAUSSEN_VOR_DEM_SCHLOSS)
                && mood == Mood.ANGESPANNT) {
            sc.feelingsComp().setMood(Mood.NEUTRAL);
        } else if (mood == Mood.ETWAS_GEKNICKT) {
            sc.feelingsComp().setMood(Mood.NEUTRAL);
        }
    }

    @NonNull
    private AvTimeSpan narrateObjects(final List<? extends IDescribableGO> objectsInNewRoom) {
        final IDescribableGO lastObject = objectsInNewRoom.get(objectsInNewRoom.size() - 1);

        // TODO Objekte, die rekursiv enthalten sind (Kugel auf einem Tisch),
        //  mit ihrer korrekten detaillierten Location beschreiben!

        return n.add(
                neuerSatz(
                        buildObjectsInRoomDescription(objectsInNewRoom,
                                describable -> getDescription(describable, false).nom()),
                        secs(objectsInNewRoom.size() * 2))
                        .phorikKandidat(
                                getDescription(lastObject, false)
                                        .getNumerusGenus(),
                                lastObject.getId()));
    }

    private AvTimeSpan narrateAndDoRoomOnly(final Lichtverhaeltnisse lichtverhaeltnisseInNewRoom) {
        final AbstractDescription description = getNormalDescriptionAndDo(initialStoryState,
                lichtverhaeltnisseInNewRoom);

        if (description instanceof DuDescription && initialStoryState
                .allowsAdditionalDuSatzreihengliedOhneSubjekt() && sc.memoryComp().getLastAction()
                .is(BEWEGEN) &&
                sc.locationComp().lastLocationWas(spatialConnection.getTo())) {


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

        if (sc.memoryComp().getLastAction().is(BEWEGEN)) {
            if (sc.locationComp().lastLocationWas(spatialConnection.getTo()) &&
                    numberOfPossibilities != ONLY_WAY) {
                // FIXME Diese Prüfung ist Unfug - wurde ja eben schon geprüft?!
                if (sc.memoryComp().getLastAction().is(BEWEGEN)) {
                    final ImmutableList.Builder<AbstractDescription<?>> alt =
                            ImmutableList.builder();
                    alt.add(neuerSatz(
                            "Was willst du hier eigentlich? "
                                    + description.getDescriptionHauptsatz(),
                            description.getTimeElapsed()));
                    if (description instanceof DuDescription) {
                        alt.add(neuerSatz(
                                "Was willst du hier eigentlich? "
                                        + ((DuDescription) description)
                                        .getDescriptionHauptsatzMitSpeziellemVorfeld(),
                                description.getTimeElapsed()));
                    }

                    alt.add(neuerSatz("Aber dir kommt ein Gedanke und "
                                    + uncapitalize(description.getDescriptionHauptsatz()),
                            description.getTimeElapsed()));

                    return n.addAlt(alt);
                } else {
                    // FIXME Wann ist dieser Fall gewünscht?
                    return n.add(
                            du("schaust", "dich nur kurz um, dann "
                                            + uncapitalize(description.getDescriptionHauptsatz()),
                                    description.getTimeElapsed())
                                    .komma(description.isKommaStehtAus())
                                    .undWartest(
                                            description
                                                    .isAllowsAdditionalDuSatzreihengliedOhneSubjekt()));
                }
            } else if (initialStoryState.getEndsThis() == StructuralElement.WORD &&
                    initialStoryState.dann()) {
                // "Du stehst wieder vor dem Schloss; dann gehst du wieder hinein in das Schloss."
                final String satzEvtlMitDann = description
                        .getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
                                "dann");
                return n.add(
                        satzanschluss(
                                "; " +
                                        uncapitalize(
                                                satzEvtlMitDann),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus())
                                .dann(description.isDann()
                                        && !satzEvtlMitDann.startsWith("Dann")));
            } else {
                return n.add(description);
            }
        } else {
            if (initialStoryState.dann()) {
                return n.add(
                        neuerSatz(PARAGRAPH, description
                                        .getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("danach"),
                                description.getTimeElapsed())
                                .komma(description.isKommaStehtAus())
                                .undWartest(description
                                        .isAllowsAdditionalDuSatzreihengliedOhneSubjekt()));
            }

            return n.add(description);
        }
    }

    private AbstractDescription getNormalDescriptionAndDo(final StoryState currentStoryState,
                                                          final Lichtverhaeltnisse
                                                                  lichtverhaeltnisseInNewRoom) {
        final Known newRoomKnown = sc.memoryComp().getKnown(spatialConnection.getTo());

        final boolean alternativeDescriptionAllowed =
                oldRoom.spatialConnectionComp().isAlternativeMovementDescriptionAllowed(
                        spatialConnection.getTo(),
                        newRoomKnown, lichtverhaeltnisseInNewRoom);

        final AbstractDescription standardDescription =
                spatialConnection
                        .getSCMoveDescription(newRoomKnown, lichtverhaeltnisseInNewRoom);

        if (!alternativeDescriptionAllowed) {
            return standardDescription;
        }

        if (newRoomKnown == Known.KNOWN_FROM_LIGHT) {
            if (numberOfPossibilities == ONLY_WAY) {
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
            } else if (numberOfPossibilities == ONE_IN_ONE_OUT
                    && sc.memoryComp().getLastAction().is(BEWEGEN) &&
                    !sc.locationComp().lastLocationWas(spatialConnection.getTo()) &&
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
            @NonNull final List<? extends IDescribableGO> objectsInRoom,
            final Function<IDescribableGO, String> describer) {
        return buildObjectInRoomDescriptionPrefix(objectsInRoom.size())
                + " "
                + buildEntitiesInRoomDescriptionList(objectsInRoom, describer);
    }

    /**
     * @return Something similar to <code>Hier liegt</code>
     */
    @NonNull
    private String buildObjectInRoomDescriptionPrefix(final int numberOfObjects) {
        final String res =
                capitalize(
                        ((ILocationGO) load(db, spatialConnection.getTo()))
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
    private static String buildEntitiesInRoomDescriptionList(
            @NonNull final List<? extends IDescribableGO> entities,
            final Function<IDescribableGO, @Nullable String> describer) {
        final StringBuilder res = new StringBuilder();
        for (int i = 0; i < entities.size(); i++) {
            res.append(describer.apply(entities.get(i)));
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
    private Action buildMemorizedAction() {
        return new Action(BEWEGEN, spatialConnection.getTo());
    }

    private void setRoomAndObjectsKnown(
            @NonNull final List<? extends IGameObject> objectsInNewRoom,
            final Lichtverhaeltnisse lichtverhaeltnisse) {
        final Known known = Known.getKnown(lichtverhaeltnisse);

        sc.memoryComp().upgradeKnown(spatialConnection.getTo(), known);

        for (final IGameObject objectInNewRoom : objectsInNewRoom) {
            sc.memoryComp().upgradeKnown(objectInNewRoom, known);
        }
    }
}