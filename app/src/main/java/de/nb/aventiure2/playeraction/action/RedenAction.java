package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.creature.CreatureState;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.GermanUtil;
import de.nb.aventiure2.german.Nominalphrase;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.SENTENCE;
import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;

/**
 * Der Spieler(charakter) redet mit einem Wesen.
 */
public class RedenAction extends AbstractPlayerAction {
    private final AvRoom room;
    private final Map<AvObject.Key, ObjectData> allObjectsByKey;

    @NonNull
    private final CreatureData creatureData;

    public RedenAction(final AvDatabase db, final CreatureData creatureData, final AvRoom room,
                       final Map<AvObject.Key, ObjectData> allObjectsByKey) {
        super(db);
        this.creatureData = creatureData;
        this.room = room;
        this.allObjectsByKey = allObjectsByKey;
    }

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final AvRoom room, final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final CreatureData creatureData) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        if (creatureData.getCreature().getKey() == FROSCHPRINZ) {
            if (creatureData.getState() == CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN) {
                res.add(new RedenAction(db, creatureData, room, allObjectsByKey));
            }
        }

        return res.build();
    }

    @Override
    @NonNull
    public String getName() {
        return "Mit " + creatureData.dat() + " reden";
    }

    @Override
    public void narrateAndDo(final StoryState currentStoryState) {
        if (creatureIs(FROSCHPRINZ)) {
            narrateAndDoFroschprinz(currentStoryState);
            return;
        }

        throw new IllegalStateException("Unexpected creature: " +
                creatureData.getCreature().getKey());
    }

    private void narrateAndDoFroschprinz(final StoryState currentStoryState) {
        n.add(t(SENTENCE, "\"Ach, du bist's, alter Wasserpatscher\", sagst du")
                .undWartest()
                .dann());

        final List<ObjectData> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();
        if (objectsInDenBrunnenGefallen.isEmpty()) {
            return;
        }

        narrateAndDoFroschprinzInDenBrunnenGefallenErklaerung(currentStoryState, objectsInDenBrunnenGefallen);

        if (creatureData.hasState(CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN)) {
            narrateAndDoFroschprinzWiederholenAngebot(objectsInDenBrunnenGefallen);
            return;
        }

        throw new IllegalStateException("Unexpected state: " +
                creatureData.getState());
    }

    private void narrateAndDoFroschprinzInDenBrunnenGefallenErklaerung(
            final StoryState currentStoryState, final List<ObjectData> objectsInDenBrunnenGefallen) {
        if (objectsInDenBrunnenGefallen.size() == 1) {
            final ObjectData objectInDenBrunnenGefallen =
                    objectsInDenBrunnenGefallen.iterator().next();

            if (currentStoryState.lastActionWas(HeulenAction.class)) {
                final Nominalphrase objectDesc =
                        objectInDenBrunnenGefallen.getDescription(false);

                n.add(t(SENTENCE, "\"Ich weine über "
                        + objectDesc.akk() // die goldene Kugel
                        + ", "
                        + objectDesc.relPron().akk() // die
                        + " mir in den Brunnen hinabgefallen ist.\""));
                return;
            }

            n.add(t(SENTENCE, "\""
                    + GermanUtil.capitalize(objectInDenBrunnenGefallen.nom())
                    + " ist mir in den Brunnen hinabgefallen.\""));
            return;
        }

        if (currentStoryState.lastActionWas(HeulenAction.class)) {
            n.add(t(SENTENCE, "\"Ich weine über die Dinge, die mir in den Brunnen " +
                    "hinabgefallen sind.\""));
            return;
        }

        n.add(t(SENTENCE, "\"Mir sind Dinge in den Brunnen hinabgefallen.\""));
    }

    private void narrateAndDoFroschprinzWiederholenAngebot(
            final List<ObjectData> objectsInDenBrunnenGefallen) {
        final String objectsInDenBrunnenGefallenShortAkk =
                ObjectData.getAkkShort(objectsInDenBrunnenGefallen);

        n.add(t(PARAGRAPH, "\"Sei still und weine nicht\", antwortet "
                + creatureData.nom(true)
                + ", \"ich kann wohl Rat schaffen, aber was gibst du mir, wenn ich "
                + objectsInDenBrunnenGefallenShortAkk
                + " wieder heraufhole?\""));

        creatureDataDao.setState(FROSCHPRINZ, HAT_NACH_BELOHNUNG_GEFRAGT);
    }

    private List<ObjectData> getObjectsInDenBrunnenGefallen() {
        return allObjectsByKey.values().stream()
                .filter(ObjectData::isDemSCInDenBrunnenGefallen)
                .collect(Collectors.toList());
    }

    private boolean creatureIs(final Creature.Key key) {
        return creatureData.creatureIs(key);
    }
}
