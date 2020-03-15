package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StartsNew;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.creature.CreatureState;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.room.connection.RoomConnection;
import de.nb.aventiure2.german.AbstractDescription;
import de.nb.aventiure2.german.DuDescription;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.german.GermanUtil.capitalize;
import static de.nb.aventiure2.german.GermanUtil.uncapitalize;
import static java.util.stream.Collectors.toList;

/**
 * Der Spieler(charakter) bewegt sich in einen anderen Raum.
 */
public class BewegenAction extends AbstractPlayerAction {
    private final AvRoom oldRoom;
    private final AvRoom newRoom;

    private BewegenAction(final AvDatabase db,
                          final AvRoom oldRoom,
                          final AvRoom newRoom) {
        super(db);

        checkArgument(newRoom != oldRoom, "newRoom == oldRoom)");

        this.oldRoom = oldRoom;
        this.newRoom = newRoom;
    }

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final AvRoom room, final AvRoom connectedRoom) {
        return ImmutableList.of(new BewegenAction(db, room, connectedRoom));
    }

    @Override
    @NonNull
    public String getName() {
        return RoomConnection.getFrom(oldRoom).get(newRoom).getActionName();
    }

    @Override
    public void narrateAndDo(final StoryState currentStoryState) {
        final List<ObjectData> objectsInNewRoom = objectDataDao.getObjectsInRoom(newRoom);
        final List<CreatureData> creaturesInNewRoom =
                creatureDataDao.getCreaturesInRoom(newRoom);

        narrate(currentStoryState, objectsInNewRoom, creaturesInNewRoom);

        playerLocationDao.setRoom(newRoom);
        setRoomAndObjectsKnown(objectsInNewRoom);
    }

    private void narrate(final StoryState currentStoryState,
                         final List<ObjectData> objectsInNewRoom,
                         final List<CreatureData> creaturesInNewRoom) {
        n.add(buildNewStoryStateRoomOnly(currentStoryState));

        if (!objectsInNewRoom.isEmpty()) {
            n.add(buildObjectsStoryState(objectsInNewRoom));
        }

        final List<CreatureData> visibleCreatures = filterVisible(creaturesInNewRoom);
        if (!visibleCreatures.isEmpty()) {
            n.add(buildCreaturesStoryState(visibleCreatures));
        }
    }

    private static List<CreatureData> filterVisible(final List<CreatureData> creatures) {
        return creatures.stream()
                .filter(cd -> cd.getState() != CreatureState.UNAUFFAELLIG)
                .collect(toList());
    }

    private StoryStateBuilder buildNewStoryStateRoomOnly(final StoryState currentStoryState) {
        final AbstractDescription description =
                RoomConnection.getFrom(oldRoom).get(newRoom)
                        .getDescription(roomDao.isKnown(newRoom));
        return buildNewStoryStateRoomOnly(currentStoryState, description);
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
            return t(StartsNew.WORD,
                    ", besinnst dich aber und "
                            + duDesc.getDescriptionSatzanschlussOhneSubjekt())
                    .dann(duDesc.dann());
        }

        return t(StartsNew.WORD,
                "und " +
                        duDesc.getDescriptionSatzanschlussOhneSubjekt())
                .dann(duDesc.dann());
    }

    private StoryStateBuilder buildNewStoryStateRoomOnlyNewSentence(
            final StoryState currentStoryState, final AbstractDescription desc) {
        if (currentStoryState.lastActionWas(BewegenAction.class)) {
            return buildNewStoryStateNewRoomOnlyNewSentenceLastActionBewegen(currentStoryState, desc);
        }

        return buildNewStoryStateNewRoomOnlyNewSentenceLastActionNichtBewegen(currentStoryState, desc);
    }

    private StoryStateBuilder buildNewStoryStateNewRoomOnlyNewSentenceLastActionBewegen(
            final StoryState currentStoryState, final AbstractDescription desc) {
        if (currentStoryState.lastRoomWas(newRoom)) {
            if (currentStoryState.noLastObject()) {
                return t(StartsNew.SENTENCE, "Aber dann kommt dir ein Gedanke und "
                        + uncapitalize(desc.getDescriptionHauptsatz()));
            }

            return t(StartsNew.PARAGRAPH,
                    "Du schaust dich nur kurz um, dann "
                            + uncapitalize(desc.getDescriptionHauptsatz()))
                    .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt());
        }

        if (currentStoryState.dann()) {
            // "Du stehst wieder vor dem Schloss; dann gehst du wieder hinein in das Schloss."
            return t(StartsNew.WORD,
                    "; " +
                            uncapitalize(
                                    desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig(
                                            "dann")));
        }

        return t(StartsNew.PARAGRAPH,
                desc.getDescriptionHauptsatz())
                .undWartest(
                        desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.dann());
    }

    private StoryStateBuilder buildNewStoryStateNewRoomOnlyNewSentenceLastActionNichtBewegen(
            final StoryState currentStoryState, final AbstractDescription desc) {
        if (currentStoryState.dann()) {
            return t(StartsNew.PARAGRAPH,
                    desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("danach"))
                    .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                    .dann(false);
        }

        return t(StartsNew.PARAGRAPH,
                desc.getDescriptionHauptsatz())
                .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                .dann(desc.dann());
    }

    private StoryStateBuilder buildObjectsStoryState(final List<ObjectData> objectsInNewRoom) {
        return t(StartsNew.SENTENCE, buildObjectsInRoomDescription(objectsInNewRoom))
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

    private StoryStateBuilder buildCreaturesStoryState(final List<CreatureData> creatures) {
        return t(StartsNew.SENTENCE, buildCreaturesInRoomDescription(creatures));
    }

    /**
     * @return Something similar to <code>Hier sitzt der hässliche Frosch.</code>
     */
    private static String buildCreaturesInRoomDescription(final List<CreatureData> creatures) {
        return buildCreaturesInRoomDescriptionPrefix(creatures.size())
                + " "
                + buildEntitiesInRoomDescriptionList(creatures);
    }

    /**
     * @return Something similar to <code>Hier sitzt</code>
     */
    private static String buildCreaturesInRoomDescriptionPrefix(final int numberOfCreatures) {
        final String res = "Hier";

        if (numberOfCreatures == 1) {
            return res + " sitzt";
        }

        return res + " sitzen";
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
            @NonNull final StartsNew startsNew,
            @NonNull final String text) {
        return super.t(startsNew, text)
                .letzterRaum(oldRoom);
    }

    private void setRoomAndObjectsKnown(final List<ObjectData> objectsInNewRoom) {
        roomDao.setKnown(newRoom);
        for (final ObjectData objectInNewRoom : objectsInNewRoom) {
            objectDataDao.setKnown(objectInNewRoom.getObject());
        }
    }
}