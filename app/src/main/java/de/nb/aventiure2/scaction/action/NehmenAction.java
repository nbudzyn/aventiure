package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.entity.creature.Creature;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.player.stats.ScStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.praedikat.PraedikatMitEinerObjektleerstelle;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.world.entity.creature.Creature.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.MITNEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.NEHMEN;

/**
 * Der Spieler(charakter) nimmt einen Gegenstand (oder in Ausnahmefällen eine
 * Creature) an sich.
 */
public class NehmenAction extends AbstractEntityAction {
    private final AvRoom room;

    public static Collection<NehmenAction> buildObjectActions(
            final AvDatabase db, final StoryState initialStoryState, final AvRoom room,
            final ObjectData objectData) {
        final ImmutableList.Builder<NehmenAction> res = ImmutableList.builder();
        res.add(new NehmenAction(db, initialStoryState, objectData, room));
        return res.build();
    }

    public static Collection<NehmenAction> buildCreatureActions(
            final AvDatabase db,
            final StoryState initialStoryState, final AvRoom room,
            final CreatureData creatureData) {
        final ImmutableList.Builder<NehmenAction> res = ImmutableList.builder();
        if (creatureData.creatureIs(FROSCHPRINZ) &&
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
    public String getType() {
        return "actionNehmen";
    }

    @Override
    @NonNull
    public String getName() {
        final PraedikatMitEinerObjektleerstelle praedikat =
                getEntityData() instanceof CreatureData ? MITNEHMEN : NEHMEN;

        return praedikat.mitObj(getEntityData()).getDescriptionInfinitiv();
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        AvTimeSpan timeElapsed = narrate();
        removeFromRoomAndTake();

        timeElapsed =
                timeElapsed.plus(creatureReactionsCoordinator.onNehmen(room, getEntityData()));

        return timeElapsed;
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

    private void removeFromRoomAndTake(@NonNull final ObjectData objectData) {
        db.objectDataDao().update(objectData.getObject(), null, true, false);
        db.playerInventoryDao().take(objectData.getObject());
    }

    private void removeFromRoomAndTake(final CreatureData creatureData) {
        checkArgument(creatureData.creatureIs(FROSCHPRINZ) &&
                        creatureData.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS),
                "Unexpected creature data: " + creatureData);

        db.creatureDataDao().setRoom(FROSCHPRINZ, null);
        db.creatureDataDao()
                .setState(FROSCHPRINZ,
                        ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS_VON_SC_GETRAGEN);
        db.playerStatsDao().setStateOfMind(ScStateOfMind.NEUTRAL);
    }

    private AvTimeSpan narrate() {
        if (getEntityData() instanceof ObjectData) {
            return narrateObject((ObjectData) getEntityData());
        }
        if (getEntityData() instanceof CreatureData) {
            return narrateCreature((CreatureData) getEntityData());
        }
        throw new IllegalStateException("Unexpected entity data: " + getEntityData());
    }

    @NonNull
    private AvTimeSpan narrateObject(@NonNull final ObjectData objectData) {
        final PraedikatMitEinerObjektleerstelle nehmenPraedikat =
                room.getLocationMode().getNehmenPraedikat();
        if (initialStoryState.lastObjectWas(objectData.getObject())) {
            if (initialStoryState.lastActionWas(AblegenAction.class)) {
                n.add(buildStoryStateObjectNachAblegen(objectData));
                return secs(5);
            }

            final ScStateOfMind scStateOfMind =
                    db.playerStatsDao().getPlayerStats().getStateOfMind();

            if (initialStoryState.lastActionWas(HochwerfenAction.class) &&
                    scStateOfMind.isEmotional()) {
                n.add(t(StoryState.StructuralElement.PARAGRAPH,
                        nehmenPraedikat
                                .getDescriptionDuHauptsatz(
                                        objectData,
                                        scStateOfMind.getAdverbialeAngabe()))
                        .undWartest(
                                nehmenPraedikat
                                        .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                        .dann());
                return secs(5);
            }
        }

        n.add(t(StoryState.StructuralElement.PARAGRAPH,
                nehmenPraedikat.getDescriptionDuHauptsatz(objectData))
                .undWartest(
                        nehmenPraedikat
                                .duHauptsatzLaesstSichMitNachfolgendemDuHauptsatzZusammenziehen())
                .dann());
        return secs(5);
    }

    private StoryStateBuilder buildStoryStateObjectNachAblegen(final ObjectData objectData) {
        if (initialStoryState.lastObjectWas(objectData.getObject())) {
            return t(StructuralElement.PARAGRAPH,
                    "Dann nimmst du " + objectData.akk() +
                            " erneut")
                    .undWartest();
        }

        return t(StoryState.StructuralElement.SENTENCE,
                "Dann nimmst du " + objectData.akk())
                .undWartest();
    }


    private AvTimeSpan narrateCreature(final CreatureData creatureData) {
        checkArgument(creatureData.creatureIs(Creature.FROSCHPRINZ) &&
                        creatureData.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS),
                "Unexpected creature data: " + creatureData);

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
}
