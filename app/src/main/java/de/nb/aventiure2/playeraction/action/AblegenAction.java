package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.ABSETZEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.HINLEGEN;

/**
 * Der Benutzer legt einen Gegenstand ab.
 */
public class AblegenAction extends AbstractEntityAction {
    private final AvRoom room;

    public static Collection<AblegenAction> buildObjectActions(
            final AvDatabase db, final StoryState initialStoryState, final AvRoom room,
            final ObjectData objectData) {
        return ImmutableList.of(new AblegenAction(db, initialStoryState, objectData, room));
    }

    public static Collection<AbstractPlayerAction> buildCreatureActions(
            final AvDatabase db,
            final StoryState initialStoryState, final AvRoom room,
            final CreatureData creatureData) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        if (creatureData.creatureIs(Creature.Key.FROSCHPRINZ) &&
                creatureData
                        .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN)) {
            res.add(new AblegenAction(db, initialStoryState, creatureData, room));
        }
        return res.build();
    }


    private AblegenAction(final AvDatabase db,
                          final StoryState initialStoryState,
                          final AbstractEntityData entityData,
                          final AvRoom room) {
        super(db, initialStoryState, entityData);
        this.room = room;
    }

    @Override
    @NonNull
    public String getName() {
        final PraedikatMitEinerObjektleerstelle praedikat =
                getEntityData() instanceof CreatureData ? ABSETZEN : HINLEGEN;

        return praedikat.mitObj(getEntityData()).getDescriptionInfinitiv();
    }

    @Override
    public String getType() {
        return "actionAblegen";
    }

    @Override
    public void narrateAndDo() {
        narrate();
        letGoAndAddToRoom();

        creatureReactionsCoordinator.onAblegen(room, getEntityData());
    }

    private void letGoAndAddToRoom() {
        if (getEntityData() instanceof ObjectData) {
            letGoAndAddToRoom((ObjectData) getEntityData());
        } else if (getEntityData() instanceof CreatureData) {
            letGoAndAddToRoom((CreatureData) getEntityData());
        } else {
            throw new IllegalStateException("Unexpected entity data: " + getEntityData());
        }
    }

    private void letGoAndAddToRoom(final ObjectData objectData) {
        db.playerInventoryDao().letGo(objectData.getObject());
        db.objectDataDao().setRoom(objectData.getObject(), room);
    }

    private void letGoAndAddToRoom(final CreatureData creatureData) {
        checkArgument(creatureData.creatureIs(Creature.Key.FROSCHPRINZ) &&
                        creatureData.hasState(
                                ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN),
                "Unexpected creature data: " + creatureData);

        db.creatureDataDao().setRoom(FROSCHPRINZ, room);
        db.creatureDataDao()
                .setState(FROSCHPRINZ,
                        ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS);
        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.NEUTRAL);
    }

    private void narrate() {
        if (getEntityData() instanceof ObjectData) {
            narrateObject((ObjectData) getEntityData());
            return;
        }
        if (getEntityData() instanceof CreatureData) {
            narrateCreature((CreatureData) getEntityData());
            return;
        }
        throw new IllegalStateException("Unexpected entity data: " + getEntityData());
    }


    private void narrateObject(final ObjectData objectData) {
        final Nominalphrase objDesc = objectData.getDescription(false);

        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            if (initialStoryState.lastObjectWas(objectData.getObject())) {
                if (initialStoryState.lastActionWas(NehmenAction.class)) {
                    n.add(t(StoryState.StructuralElement.WORD,
                            "- und legst "
                                    + objDesc.persPron().akk()
                                    + " sogleich wieder hin"));
                    return;
                }

                n.add(t(StoryState.StructuralElement.WORD,
                        ", dann legst du sie hin")
                        .undWartest());
                return;
            }

            String text = "und legst " + objDesc.akk();
            if (initialStoryState.lastActionWas(BewegenAction.class)) {
                text += " dort";
            }

            text += " hin";

            n.add(t(StoryState.StructuralElement.WORD, text));
            return;
        }

        if (initialStoryState.lastActionWas(NehmenAction.class)) {
            if (initialStoryState.lastObjectWas(objectData.getObject())) {
                n.add(t(StoryState.StructuralElement.PARAGRAPH,
                        "Du legst " + objDesc.akk() + " wieder hin")
                        .undWartest()
                        .dann());
                return;
            }
        }

        n.add(t(StoryState.StructuralElement.PARAGRAPH,
                "Du legst " + objDesc.akk() + " hin")
                .undWartest()
                .dann());
    }

    private void narrateCreature(final CreatureData creatureData) {
        checkArgument(creatureData.creatureIs(Creature.Key.FROSCHPRINZ) &&
                        creatureData
                                .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN),
                "Unexpected creature data: " + creatureData);

        n.add(alt(
                t(StoryState.StructuralElement.PARAGRAPH,
                        "Du wühlst in deiner Tasche und auf einmal schauert's dich und "
                                + "der nasse Frosch sitzt in deiner Hand. Schnell "
                                + room.getLocationMode().getWohin()
                                + " mit ihm!")
                        .dann()
                        .beendet(PARAGRAPH),
                t(StoryState.StructuralElement.PARAGRAPH,
                        "Du schüttest deine Tasche aus, bis der Frosch endlich " +
                                room.getLocationMode().getWohin() +
                                " fällt. Puh.")
                        .dann()
                        .beendet(PARAGRAPH),
                t(StoryState.StructuralElement.PARAGRAPH,
                        "Du wühlst in deiner Tasche. Da quakt es erbost, auf einmal "
                                + "springt der Fosch heraus und direkt "
                                + room.getLocationMode().getWohin()
                )
        ));
    }
}
