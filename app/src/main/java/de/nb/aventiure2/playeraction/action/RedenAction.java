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
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.Nominalphrase;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.WORD;
import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.object.ObjectData.getDescriptionSingleOrCollective;
import static de.nb.aventiure2.german.GermanUtil.capitalize;
import static de.nb.aventiure2.german.SeinUtil.istSind;

/**
 * Der Spieler(charakter) redet mit einem Wesen.
 */
public class RedenAction extends AbstractPlayerAction {
    enum Inhalt {
        Neutral, Angebote_machen, Gespraech_beenden
    }

    private final AvRoom room;
    private final Map<AvObject.Key, ObjectData> allObjectsByKey;

    @NonNull
    private final CreatureData creatureData;

    @NonNull
    private final Inhalt inhalt;

    private final String name;

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final StoryState initialStoryState, final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final CreatureData creatureData) {
        final ImmutableList.Builder<AbstractPlayerAction> res = ImmutableList.builder();
        if (creatureData.getCreature().getKey() == FROSCHPRINZ) {
            res.addAll(buildFroschprinzActions(db, initialStoryState, room, allObjectsByKey,
                    creatureData));
        }

        return res.build();
    }

    private static Collection<AbstractPlayerAction> buildFroschprinzActions(
            final AvDatabase db,
            final StoryState initialStoryState,
            final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey,
            final CreatureData creatureData) {
        switch (creatureData.getState()) {
            case HAT_SC_HILFSBEREIT_ANGESPROCHEN:
                return buildFroschprinzHatSCHillfbereitAngesprochenActions(db, initialStoryState,
                        room, allObjectsByKey,
                        creatureData);
            case HAT_NACH_BELOHNUNG_GEFRAGT:
                return buildFroschprinzHatNachBelohnungGefragtActions(
                        db, initialStoryState, room, allObjectsByKey, creatureData);
        }

        return ImmutableList.of();
    }

    private static Collection<AbstractPlayerAction> buildFroschprinzHatSCHillfbereitAngesprochenActions(
            final AvDatabase db, final StoryState initialStoryState, final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey, final CreatureData creatureData) {
        final ImmutableList.Builder<AbstractPlayerAction> res =
                ImmutableList.builder();

        res.add(new RedenAction(db, initialStoryState, creatureData, room,
                allObjectsByKey, Inhalt.Neutral,
                "Mit " + creatureData.dat() + " reden"));

        if (initialStoryState.talkingTo(FROSCHPRINZ)) {
            res.add(new RedenAction(db, initialStoryState, creatureData, room,
                    allObjectsByKey,
                    Inhalt.Gespraech_beenden,
                    "Den Frosch ignorieren"));
        }
        return res.build();
    }

    private static Collection<AbstractPlayerAction> buildFroschprinzHatNachBelohnungGefragtActions(
            final AvDatabase db, final StoryState initialStoryState, final AvRoom room,
            final Map<AvObject.Key, ObjectData> allObjectsByKey, final CreatureData creatureData) {
        if (filterInDenBrunnenGefallen(allObjectsByKey).isEmpty()) {
            return ImmutableList.of();
        }

        final ImmutableList.Builder<AbstractPlayerAction> res =
                ImmutableList.builder();

        res.add(new RedenAction(db, initialStoryState, creatureData, room, allObjectsByKey,
                Inhalt.Angebote_machen,
                capitalize(creatureData.dat()) + " Angebote machen"));

        if (initialStoryState.talkingTo(FROSCHPRINZ)) {
            res.add(new RedenAction(db, initialStoryState, creatureData, room,
                    allObjectsByKey,
                    Inhalt.Gespraech_beenden,
                    "Das Gespräch beenden"));
        }

        return res.build();
    }

    private RedenAction(final AvDatabase db,
                        final StoryState initialStoryState,
                        @NonNull final CreatureData creatureData, final AvRoom room,
                        final Map<AvObject.Key, ObjectData> allObjectsByKey,
                        @NonNull final Inhalt inhalt,
                        @NonNull final String name) {
        super(db, initialStoryState);
        this.creatureData = creatureData;
        this.room = room;
        this.allObjectsByKey = allObjectsByKey;
        this.inhalt = inhalt;
        this.name = name;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public void narrateAndDo() {
        if (creatureIs(FROSCHPRINZ)) {
            narrateAndDoFroschprinz();
            return;
        }

        throw new IllegalStateException("Unexpected creature: " +
                creatureData.getCreature().getKey());
    }

    private void narrateAndDoFroschprinz() {
        final List<ObjectData> objectsInDenBrunnenGefallen =
                getObjectsInDenBrunnenGefallen();

        switch (inhalt) {
            case Angebote_machen:
                narrateAndDoFroschprinzAngeboteMachen(
                        objectsInDenBrunnenGefallen);
                return;
            case Gespraech_beenden:
                switch (creatureData.getState()) {
                    case HAT_SC_HILFSBEREIT_ANGESPROCHEN:
                        narrateAndDoFroschprinzAngesprochenGespraechBeenden();
                        return;
                    case HAT_NACH_BELOHNUNG_GEFRAGT:
                        narrateAndDoFroschprinzNachBelohnungGefragtGespraechBeenden();
                        return;
                    case HAT_FORDERUNG_GESTELLT:
                        // TODO
                        return;
                    default:
                        throw new IllegalStateException(
                                "Unexpected froschprinz state for Gespraech beenden: " +
                                        creatureData.getState());
                }

            default:
                narrateAndDoFroschprinzAllg(
                        objectsInDenBrunnenGefallen);
        }
    }

    private void narrateAndDoFroschprinzAngesprochenGespraechBeenden() {
        n.add(t(SENTENCE,
                "Du tust, als hättest du nichts gehört")
                .komma()
                .undWartest()
                .dann()
                .imGespraechMit(null));
    }

    private void narrateAndDoFroschprinzAllg(final List<ObjectData> objectsInDenBrunnenGefallen) {
        if (initialStoryState.talkingTo(FROSCHPRINZ)) {
            n.add(t(SENTENCE, "„Ach, du bist's, alter Wasserpatscher“, sagst du")
                    .undWartest()
                    .dann());
        } else {
            if (initialStoryState.lastActionWas(RedenAction.class)) {
                if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()
                        && initialStoryState.dann()) {
                    n.add(t(WORD,
                            "– aber dann gibst du dir einen Ruck:"));
                } else if (initialStoryState.dann()) {
                    n.add(t(SENTENCE,
                            "Aber dann gibst du dir einen Ruck:"));

                } else {
                    n.add(t(SENTENCE,
                            "Du gibst dir einen Ruck:"));
                }
            }

            n.add(t(SENTENCE, "„Hallo, du hässlicher Frosch!“, redest du ihn an")
                    .undWartest()
                    .dann());
        }

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            return;
        }

        narrateAndDoFroschprinzInDenBrunnenGefallenErklaerung(
                objectsInDenBrunnenGefallen);

        if (creatureData.hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN)) {
            narrateAndDoFroschprinzHerausholenAngebot(objectsInDenBrunnenGefallen);
            return;
        }

        throw new IllegalStateException("Unexpected state: " +
                creatureData.getState());
    }

    private void narrateAndDoFroschprinzInDenBrunnenGefallenErklaerung(
            final List<ObjectData> objectsInDenBrunnenGefallen) {
        if (initialStoryState.lastActionWas(HeulenAction.class)) {
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

        db.creatureDataDao().setState(FROSCHPRINZ, HAT_NACH_BELOHNUNG_GEFRAGT);
    }

    private void narrateAndDoFroschprinzNachBelohnungGefragtGespraechBeenden() {
        n.add(t(SENTENCE,
                "„Denkst du etwa, man überschüttet dich mit Gold und Juwelen? - Vergiss es!“")
                .imGespraechMit(null));
    }

    private void narrateAndDoFroschprinzAngeboteMachen(
            final List<ObjectData> objectsInDenBrunnenGefallen) {
        if (!initialStoryState.talkingTo(FROSCHPRINZ)) {
            if (initialStoryState.lastActionWas(RedenAction.class)) {
                n.add(t(PARAGRAPH,
                        "Dann gehst du kurz in dich…"));
            }

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

        db.creatureDataDao().setState(FROSCHPRINZ, HAT_FORDERUNG_GESTELLT);
    }

    private List<ObjectData> getObjectsInDenBrunnenGefallen() {
        return filterInDenBrunnenGefallen(allObjectsByKey);
    }

    private static List<ObjectData> filterInDenBrunnenGefallen(
            final Map<AvObject.Key, ObjectData> objectsByKey) {
        return filterInDenBrunnenGefallen(objectsByKey.values());
    }

    private static List<ObjectData> filterInDenBrunnenGefallen(
            final Collection<ObjectData> objects) {
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
