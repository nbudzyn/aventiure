package de.nb.aventiure2.playeraction.action.reden;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.VerbSubjDatAkk;
import de.nb.aventiure2.german.praedikat.VerbSubjObj;
import de.nb.aventiure2.playeraction.action.HeulenAction;

import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.WORD;
import static de.nb.aventiure2.data.world.creature.Creature.Key.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.object.ObjectData.filterInDenBrunnenGefallen;
import static de.nb.aventiure2.data.world.object.ObjectData.getDescriptionSingleOrCollective;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.praedikat.SeinUtil.istSind;

/**
 * Erzeugt {@link CreatureTalkStep}s für den
 * {@link de.nb.aventiure2.data.world.creature.Creature.Key#FROSCHPRINZ}en.
 */
class FroschprinzTalkStepBuilder extends AbstractCreatureTalkStepBuilder {
    private final List<ObjectData> objectsInDenBrunnenGefallen;

    FroschprinzTalkStepBuilder(final AvDatabase db, final StoryState initialStoryState,
                               final Class<? extends IPlayerAction> currentActionClass,
                               final AvRoom room,
                               final Map<AvObject.Key, ObjectData> allObjectsByKey,
                               @NonNull final CreatureData creatureData) {
        super(db, initialStoryState, currentActionClass, room, allObjectsByKey, creatureData);

        objectsInDenBrunnenGefallen = filterInDenBrunnenGefallen(allObjectsByKey);
    }

    @Override
    List<CreatureTalkStep> getAllStepsForCurrentState() {
        switch (creatureData.getState()) {
            case UNAUFFAELLIG:
                return ImmutableList.of();
            case HAT_SC_HILFSBEREIT_ANGESPROCHEN:
                return
                        ImmutableList.of(
                                st(VerbSubjObj.REDEN,
                                        this::narrateAndDo_FroschHatAngesprochen_antworten),
                                exitSt(VerbSubjObj.IGNORIEREN,
                                        this::narrateAndDo_FroschHatAngesprochen_Exit),
                                immReEntrySt(this::narrateAndDo_FroschHatAngesprochen_ImmReEntry),
                                entrySt(this::narrateAndDo_FroschHatAngesprochen_ReEntry)
                        );
            case HAT_NACH_BELOHNUNG_GEFRAGT:
                return
                        ImmutableList.of(
                                st(this::etwasIstInDenBrunnenGefallen,
                                        VerbSubjDatAkk.MACHEN.mitAkk(Nominalphrase.ANGEBOTE),
                                        this::narrateAndDo_FroschHatNachBelohnungGefragt_AngeboteMachen),
                                exitSt(this::narrateAndDo_FroschHatNachBelohnungGefragt_Exit),
                                immReEntrySt(VerbSubjDatAkk.MACHEN.mitAkk(Nominalphrase.ANGEBOTE),
                                        this::narrateAndDo_FroschHatNachBelohnungGefragt_ImmReEntry),
                                entrySt(VerbSubjDatAkk.MACHEN.mitAkk(Nominalphrase.ANGEBOTE),
                                        this::narrateAndDo_FroschHatNachBelohnungGefragt_ReEntry)
                        );
            case HAT_FORDERUNG_GESTELLT:
                return ImmutableList.of(
                        // TODO     HAT_FORDERUNG_GESTELLT - Zusagen
                        exitSt(this::narrateAndDo_FroschHatForderungGestellt_Exit)
                );
            default:
                throw new IllegalStateException("Unexpected Froschprinz state: " +
                        creatureData.getState());
        }
    }

    // -------------------------------------------------------------------------------
    // .. HAT_SC_HILFSBEREIT_ANGESPROCHEN
    // -------------------------------------------------------------------------------
    private void narrateAndDo_FroschHatAngesprochen_antworten() {
        n.add(t(SENTENCE, "„Ach, du bist's, alter Wasserpatscher“, sagst du")
                .undWartest()
                .dann());

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            narrateFroschReagiertNicht();
            return;
        }

        narrateAndDoInDenBrunnenGefallenErklaerung();
        narrateAndDoHerausholenAngebot();
    }

    private void narrateAndDo_FroschHatAngesprochen_ImmReEntry() {
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

        narrateAndDo_FroschHatAngesprochen_ReEntry();
    }

    private void narrateAndDo_FroschHatAngesprochen_ReEntry() {
        n.add(t(SENTENCE, "„Hallo, du hässlicher Frosch!“, redest du ihn an")
                .undWartest()
                .dann());

        if (objectsInDenBrunnenGefallen.isEmpty()) {
            narrateFroschReagiertNicht();
            return;
        }

        narrateAndDoInDenBrunnenGefallenErklaerung();
        narrateAndDoHerausholenAngebot();
    }

    private void narrateAndDoInDenBrunnenGefallenErklaerung() {
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

    private void narrateAndDoHerausholenAngebot() {
        final String objectsInDenBrunnenGefallenShortAkk =
                ObjectData.getAkkShort(objectsInDenBrunnenGefallen);

        final String ratschlag;
        if (initialStoryState.lastActionWas(HeulenAction.class)) {
            ratschlag = "weine nicht";
        } else {
            ratschlag = "sorge dich nicht";
        }
        n.add(t(PARAGRAPH, "„Sei still und " +
                ratschlag +
                "“, antwortet "
                + creatureData.nom(true)
                + ", „ich kann wohl Rat schaffen, aber was gibst du mir, wenn ich "
                + objectsInDenBrunnenGefallenShortAkk
                + " wieder heraufhole?“"));

        db.creatureDataDao().setState(FROSCHPRINZ, HAT_NACH_BELOHNUNG_GEFRAGT);
    }

    private void narrateAndDo_FroschHatAngesprochen_Exit() {
        n.add(t(SENTENCE,
                "Du tust, als hättest du nichts gehört")
                .komma()
                .undWartest()
                .dann()
                .imGespraechMit(null));
    }

    // -------------------------------------------------------------------------------
    // .. HAT_NACH_BELOHNUNG_GEFRAGT
    // -------------------------------------------------------------------------------

    private void narrateAndDo_FroschHatNachBelohnungGefragt_AngeboteMachen() {
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

    private void narrateAndDo_FroschHatNachBelohnungGefragt_ImmReEntry() {
        if (objectsInDenBrunnenGefallen.isEmpty()) {
            n.add(t(SENTENCE, "„Hallo nochmal, Meister Frosch!“"));

            narrateFroschReagiertNicht();
            return;
        }

        n.add(t(PARAGRAPH, "Dann gehst du kurz in dich…"));

        narrateAndDo_FroschHatNachBelohnungGefragt_Nachfrage();
    }


    private void narrateAndDo_FroschHatNachBelohnungGefragt_ReEntry() {
        if (objectsInDenBrunnenGefallen.isEmpty()) {
            n.add(t(SENTENCE, "„Hallo, Kollege Frosch!“"));

            narrateFroschReagiertNicht();
            return;
        }

        narrateAndDo_FroschHatNachBelohnungGefragt_Nachfrage();
    }

    private void narrateAndDo_FroschHatNachBelohnungGefragt_Nachfrage() {
        n.add(t(PARAGRAPH,
                "„Frosch“, sprichst du ihn an, „steht dein Angebot noch?“"));

        n.add(t(PARAGRAPH,
                "„Sicher“, antwortet der Frosch, „ich kann dir alles aus dem Brunnen " +
                        "holen, was hineingefallen ist. Was gibst du mir dafür?“ " +
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

    private void narrateAndDo_FroschHatNachBelohnungGefragt_Exit() {
        n.add(t(SENTENCE,
                "„Denkst du etwa, ich überschütte dich mit Gold und Juwelen? - Vergiss es!“")
                .imGespraechMit(null));
    }

    // -------------------------------------------------------------------------------
    // .. HAT_NACH_BELOHNUNG_GEFRAGT
    // -------------------------------------------------------------------------------

    private void narrateAndDo_FroschHatForderungGestellt_Exit() {
        n.add(alt(
                t(SENTENCE,
                        "„Na, bei dir piept's wohl!“ – Entrüstet wendest du dich ab")
                        .undWartest()
                        .imGespraechMit(null),
                t(SENTENCE,
                        "„Wenn ich darüber nachdenke… – nein!“")
                        .imGespraechMit(null),
                t(SENTENCE,
                        "„Am Ende willst du noch in meinem Bettchen schlafen! Schäm dich, " +
                                "Frosch!“")
                        .imGespraechMit(null)));
    }

    // -------------------------------------------------------------------------------
    // .. ALLGEMEINES
    // -------------------------------------------------------------------------------

    private void narrateFroschReagiertNicht() {
        n.add(t(SENTENCE, "Der Frosch reagiert nicht")
                .imGespraechMit(null));
    }

    private boolean etwasIstInDenBrunnenGefallen() {
        return !objectsInDenBrunnenGefallen.isEmpty();
    }
}
