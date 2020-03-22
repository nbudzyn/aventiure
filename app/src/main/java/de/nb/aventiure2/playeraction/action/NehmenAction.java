package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StartsNew;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.MITNEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;

/**
 * Der Spieler(charakter) nimmt einen Gegenstand (oder in Ausnahmefällen eine
 * Creature) an sich.
 */
public class NehmenAction extends AbstractEntityAction {
    private final AvRoom room;

    public static Collection<AbstractPlayerAction> buildObjectActions(
            final AvDatabase db, final StoryState initialStoryState, final AvRoom room,
            final ObjectData objectData) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        res.add(new NehmenAction(db, initialStoryState, objectData, room));
        return res.build();
    }

    public static Collection<AbstractPlayerAction> buildCreatureActions(
            final AvDatabase db,
            final StoryState initialStoryState, final AvRoom room,
            final CreatureData creatureData) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        if (creatureData.creatureIs(Creature.Key.FROSCHPRINZ) &&
                creatureData.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)) {
            res.add(new NehmenAction(db, initialStoryState, creatureData, room));
        }
        return res.build();
    }

    private NehmenAction(final AvDatabase db, final StoryState initialStoryState,
                         final AbstractEntityData entityData, final AvRoom room) {
        super(db, initialStoryState, entityData);
        this.room = room;
    }

    @Override
    @NonNull
    public String getName() {
        final PraedikatMitEinerObjektleerstelle praedikat =
                getEntityData() instanceof CreatureData ? MITNEHMEN : NEHMEN;

        return praedikat.mitObj(getEntityData()).getDescriptionInfinitiv();
    }

    @Override
    public void narrateAndDo() {
        narrate();
        removeFromRoomAndTake();
    }

    private void removeFromRoomAndTake() {
        if (getEntityData() instanceof ObjectData) {
            removeFromRoomAndTake((ObjectData) getEntityData());
        } else if (getEntityData() instanceof CreatureData) {
            removeFromRoomAndTake((CreatureData) getEntityData());
        } else {
            throw new IllegalStateException("Unexpected entity data: " + getEntityData());
        }
    }

    private void removeFromRoomAndTake(final ObjectData objectData) {
        db.objectDataDao().update(objectData.getObject(), null, true, false);
        db.playerInventoryDao().take(objectData.getObject());
    }

    private void removeFromRoomAndTake(final CreatureData creatureData) {
        checkArgument(creatureData.creatureIs(Creature.Key.FROSCHPRINZ) &&
                        creatureData.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS),
                "Unexpected creature data: " + creatureData);

        db.creatureDataDao().setRoom(FROSCHPRINZ, null);
        db.creatureDataDao()
                .setState(FROSCHPRINZ,
                        ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN);
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
        if (initialStoryState.lastActionWas(AblegenAction.class)) {
            n.add(buildStoryStateObjectNachAblegen(objectData));
            return;
        }

        n.add(t(StartsNew.PARAGRAPH,
                room.getLocationMode().getNehmenVerb().getDescriptionHauptsatz(objectData))
                .undWartest()
                .dann());
    }

    private StoryStateBuilder buildStoryStateObjectNachAblegen(final ObjectData objectData) {
        if (initialStoryState.lastObjectWas(objectData.getObject())) {
            return t(StartsNew.PARAGRAPH,
                    "Dann nimmst du " + objectData.akk() +
                            " erneut")
                    .undWartest();
        }

        return t(StartsNew.SENTENCE,
                "Dann nimmst du " + objectData.akk())
                .undWartest();
    }


    private void narrateCreature(final CreatureData creatureData) {
        checkArgument(creatureData.creatureIs(Creature.Key.FROSCHPRINZ) &&
                        creatureData.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS),
                "Unexpected creature data: " + creatureData);

        n.add(alt(
                t(StartsNew.PARAGRAPH,
                        "Du ekelst dich sehr, aber mit einiger Überwindung nimmst du den Frosch in "
                                + "die Hand. "
                                + "Er ist glibschig und schleimig – pfui-bäh! – schnell lässt du ihn in "
                                + "eine Tasche gleiten. Sein gedämpftes Quaken könnte wohlig sein oder "
                                + "genauso gut vorwurfsvoll"),
                t(StartsNew.PARAGRAPH,
                        "Den Frosch in die Hand nehmen?? – Wer hat dir bloß solche Flausen "
                                + "in den Kopf gesetzt! Kräftig packst du den Frosch und versenkst "
                                + "ihn tief in deiner Tasche. Du versuchst, deine Hand an der "
                                + "Kleidung zu reinigen, aber der Schleim verteilt sich nur "
                                + "überall - igitt!"),
                t(StartsNew.PARAGRAPH,
                        "Du erbarmst dich und packst den Frosch in deine Tasche. Er fasst "
                                + "sich sehr eklig an und du bist glücklich, als die Prozedur "
                                + "vorbei ist.")
                        .dann()
        ));
    }
}
