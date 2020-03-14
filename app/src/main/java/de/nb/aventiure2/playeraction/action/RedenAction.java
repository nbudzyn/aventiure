package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.creature.Creature;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.creature.CreatureState;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.Nominalphrase;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.SENTENCE;
import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.object.ObjectData.getDescriptionSingleOrCollective;
import static de.nb.aventiure2.german.GermanUtil.capitalize;
import static de.nb.aventiure2.german.SeinUtil.istSind;

/**
 * Der Spieler(charakter) redet mit einem Wesen.
 */
public class RedenAction extends AbstractPlayerAction {
    enum Inhalt {
        Neutral, Angebote_machen
    }

    private final AvRoom room;
    private final Map<AvObject.Key, ObjectData> allObjectsByKey;

    @NonNull
    private final CreatureData creatureData;

    @NonNull
    private final Inhalt inhalt;

    public RedenAction(final AvDatabase db, final CreatureData creatureData, final AvRoom room,
                       final Map<AvObject.Key, ObjectData> allObjectsByKey,
                       final Inhalt inhalt) {
        super(db);
        this.creatureData = creatureData;
        this.room = room;
        this.allObjectsByKey = allObjectsByKey;
        this.inhalt = inhalt;
    }

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final AvRoom room, final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final CreatureData creatureData) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        if (creatureData.getCreature().getKey() == FROSCHPRINZ) {
            res.addAll(buildFroschprinzActions(db, room, allObjectsByKey, creatureData));
        }

        return res.build();
    }

    private static Collection<AbstractPlayerAction> buildFroschprinzActions(
            final AvDatabase db, final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final CreatureData creatureData) {
        switch (creatureData.getState()) {
            case HAT_SC_HILFSBEREIT_ANGESPROCHEN:
                return ImmutableList.of(
                        new RedenAction(db, creatureData, room, allObjectsByKey, Inhalt.Neutral));
            case HAT_NACH_BELOHNUNG_GEFRAGT:
                if (filterInDenBrunnenGefallen(allObjectsByKey).isEmpty()) {
                    return ImmutableList.of();
                }

                return ImmutableList.of(
                        new RedenAction(db, creatureData, room, allObjectsByKey,
                                Inhalt.Angebote_machen));
        }

        return ImmutableList.of();
    }

    @Override
    @NonNull
    public String getName() {
        switch (inhalt) {
            case Angebote_machen:
                return capitalize(creatureData.dat()) + " Angebote machen";
            default:
                return "Mit " + creatureData.dat() + " reden";
        }
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
        final List<ObjectData> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        switch (inhalt) {
            case Angebote_machen:
                narrateAndDoFroschprinzAngeboteMachen(currentStoryState,
                        objectsInDenBrunnenGefallen);
                return;
            default:
                narrateAndDoFroschprinzAllg(currentStoryState,
                        objectsInDenBrunnenGefallen);
        }
    }

    private void narrateAndDoFroschprinzAngeboteMachen(
            final StoryState currentStoryState,
            final List<ObjectData> objectsInDenBrunnenGefallen) {
        if (!currentStoryState.talkingTo(FROSCHPRINZ)) {
            n.add(t(PARAGRAPH,
                    "„Frosch“, sprichst du ihn an, „steht dein Angebot noch?“"));
            n.add(t(PARAGRAPH,
                    "„Sicher“, antwortet der Frosch, „ich kann dir alles aus dem Brunnen " +
                            "holen, was hineingefallen ist. Was gibst du mir dafür?“"));
        }

        n.add(t(SENTENCE,
                "„Was du haben willst, lieber Frosch“, sagst du, „meine Kleider, " +
                        "Perlen oder Edelsteine?“"));

        n.add(t(PARAGRAPH,
                "Der Frosch antwortet: „Deine Kleider, Perlen oder Edelsteine, die mag " +
                        "ich nicht. " +
                        "Aber wenn ich am Tischlein neben dir sitzen soll, von deinem Tellerlein " +
                        "essen und " +
                        "aus deinem Becherlein trinken: Wenn du mir das versprichst, so will ich " +
                        "hinuntersteigen und dir " +
                        // die goldene Kugel / die Dinge
                        getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen).akk() +
                        " wieder herauf holen.“"));

        creatureDataDao.setState(FROSCHPRINZ, HAT_FORDERUNG_GESTELLT);
    }

    private void narrateAndDoFroschprinzAllg(final StoryState currentStoryState,
                                             final List<ObjectData> objectsInDenBrunnenGefallen) {
        n.add(t(SENTENCE, "„Ach, du bist's, alter Wasserpatscher“, sagst du")
                .undWartest()
                .dann());

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            return;
        }

        narrateAndDoFroschprinzInDenBrunnenGefallenErklaerung(currentStoryState, objectsInDenBrunnenGefallen);

        if (creatureData.hasState(CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN)) {
            narrateAndDoFroschprinzHerausholenAngebot(objectsInDenBrunnenGefallen);
            return;
        }

        throw new IllegalStateException("Unexpected state: " +
                creatureData.getState());
    }

    private void narrateAndDoFroschprinzInDenBrunnenGefallenErklaerung(
            final StoryState currentStoryState, final List<ObjectData> objectsInDenBrunnenGefallen) {
        if (currentStoryState.lastActionWas(HeulenAction.class)) {
            final Nominalphrase objectsDesc =
                    getDescriptionSingleOrCollective(objectsInDenBrunnenGefallen);

            n.add(t(SENTENCE, "„Ich weine über "
                    + objectsDesc.akk() // die goldene Kugel
                    + ", "
                    + objectsDesc.relPron().akk() // die
                    + " mir in den Brunnen hinabgefallen " +
                    istSind(objectsDesc.getNumerusGenus()) +
                    ".“"));
            return;
        }

        if (objectsInDenBrunnenGefallen.size() == 1) {
            final ObjectData objectInDenBrunnenGefallen =
                    objectsInDenBrunnenGefallen.iterator().next();

            n.add(t(SENTENCE, "„"
                    + capitalize(objectInDenBrunnenGefallen.nom())
                    + " ist mir in den Brunnen hinabgefallen.“"));
            return;
        }

        n.add(t(SENTENCE, "„Mir sind Dinge in den Brunnen hinabgefallen.“"));
    }

    private void narrateAndDoFroschprinzHerausholenAngebot(
            final List<ObjectData> objectsInDenBrunnenGefallen) {
        final String objectsInDenBrunnenGefallenShortAkk =
                ObjectData.getAkkShort(objectsInDenBrunnenGefallen);

        n.add(t(PARAGRAPH, "„Sei still und weine nicht“, antwortet "
                + creatureData.nom(true)
                + ", „ich kann wohl Rat schaffen, aber was gibst du mir, wenn ich "
                + objectsInDenBrunnenGefallenShortAkk
                + " wieder heraufhole?“"));

        creatureDataDao.setState(FROSCHPRINZ, HAT_NACH_BELOHNUNG_GEFRAGT);
    }

    private List<ObjectData> getObjectsInDenBrunnenGefallen() {
        return filterInDenBrunnenGefallen(allObjectsByKey);
    }

    private static List<ObjectData> filterInDenBrunnenGefallen(
            final Map<AvObject.Key, ObjectData> objectsByKey) {
        return filterInDenBrunnenGefallen(objectsByKey.values());
    }

    private static List<ObjectData> filterInDenBrunnenGefallen(final Collection<ObjectData> objects) {
        return objects.stream()
                .filter(ObjectData::isDemSCInDenBrunnenGefallen)
                .collect(Collectors.toList());
    }

    private boolean creatureIs(final Creature.Key key) {
        return creatureData.creatureIs(key);
    }

    @Override
    protected StoryStateBuilder t(
            @NonNull final StoryState.StartsNew startsNew,
            @NonNull final String text) {
        return super.t(startsNew, text)
                .imGespraechMit(creatureData.getCreature());
    }
}
