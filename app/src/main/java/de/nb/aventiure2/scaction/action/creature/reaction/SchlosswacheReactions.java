package de.nb.aventiure2.scaction.action.creature.reaction;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

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
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.WORD;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUFMERKSAM;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.time.AvDateTime.isWithin;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

class SchlosswacheReactions
        <W extends IDescribableGO & ILivingBeingGO & IHasStateGO & ILocatableGO>
        extends AbstractCreatureReactions<W> {
    public SchlosswacheReactions(final AvDatabase db) {
        super(db, SCHLOSSWACHE);
    }

    @Override
    public AvTimeSpan onLeaveRoom(final IGameObject oldRoom, final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onEnterRoom(final IHasStoringPlaceGO oldRoom,
                                  final IHasStoringPlaceGO newRoom,
                                  final StoryState currentStoryState) {
        if (!newRoom.is(SCHLOSS_VORHALLE)) {
            return noTime();
        }

        if (getReactor().stateComp().hasState(UNAUFFAELLIG)) {
            return noTime();
        }

        final ILocatableGO goldeneKugel = (ILocatableGO) load(db, GOLDENE_KUGEL);
        if (goldeneKugel.locationComp().hasLocation(SCHLOSS_VORHALLE)) {
            if (db.counterDao().incAndGet(
                    "SchlosswacheReactions_onEnterRoom_SchlossVorhalle") > 1) {
                n.add(t(SENTENCE,
                        capitalize(getReactorDescription(true).nom())
                                + " scheint dich nicht zu bemerken")
                        .letzterRaum(oldRoom));
                return secs(3);
            }
        }

        return scMussDasSchlossVerlassen(oldRoom, newRoom);
    }

    private AvTimeSpan scMussDasSchlossVerlassen(final IHasStoringPlaceGO oldRoom,
                                                 final IGameObject newRoom) {
        // STORY Ausspinnen: Der Spieler sollte selbst entscheiden,
        //  ob der das Schloss wieder verlässt - oder ggf. im Kerker landet.
        n.add(alt(
                t(SENTENCE,
                        "Die Wache spricht dich sofort an und macht dir unmissverständlich "
                                + "klar, dass du hier "
                                + "vor dem großen Fest nicht erwünscht bist. Du bist "
                                + "leicht zu "
                                + "überzeugen und trittst wieder "
                                + schlossVerlassenWohinDescription(
                                SCHLOSS_VORHALLE, oldRoom.getId())
                                // "in den Sonnenschein"
                                + " hinaus")
                        .letzterRaum(newRoom)
                        .beendet(PARAGRAPH),
                t(PARAGRAPH,
                        "„Heho, was wird das?“, tönt dir eine laute Stimme entgegen. "
                                + "„Als ob hier ein jeder "
                                + "nach Belieben hereinspazieren könnt. Das würde dem König so "
                                + "passen. Und "
                                + "seinem Kerkermeister auch.“ "
                                + "Du bleibst besser draußen")
                        .letzterRaum(newRoom)
                        .beendet(PARAGRAPH)
        ));

        sc.locationComp().setLocation(oldRoom);
        sc.memoryComp().setLastAction(new Action(Action.Type.BEWEGEN, (GameObject) null));

        return secs(10);
    }

    private String schlossVerlassenWohinDescription(final GameObjectId schlossRoom,
                                                    final GameObjectId wohinRoom) {
        final Lichtverhaeltnisse lichtverhaeltnisseImSchloss =
                getLichtverhaeltnisse(schlossRoom);
        final Lichtverhaeltnisse lichtverhaeltnisseDraussen =
                getLichtverhaeltnisse(wohinRoom);
        if (lichtverhaeltnisseImSchloss // Im Schloss ist es immer hell, wenn es also draußen
                // auch hell ist...
                == lichtverhaeltnisseDraussen) {
            return "in den Sonnenschein";
        }

        // Draußen ist es (anders als im Schloss) dunkel
        return lichtverhaeltnisseDraussen.getWohin();
    }

    @Override
    public AvTimeSpan onNehmen(final IHasStoringPlaceGO room, final ILocatableGO genommen,
                               final StoryState currentStoryState) {
        if (!room.is(SCHLOSS_VORHALLE)) {
            return noTime();
        }

        if (((IHasStateGO) load(db, SCHLOSSFEST)).stateComp().hasState(BEGONNEN)) {
            return noTime();
        }

        switch (getReactor().stateComp().getState()) {
            case UNAUFFAELLIG:
                return nehmen_wacheWirdAufmerksam(genommen);
            case AUFMERKSAM:
                // TODO "collectibleComp.isCollectible()"?
                if (genommen.is(GOLDENE_KUGEL)) {
                    return nehmenGoldeneKugel_wacheIstAufmerksam(room,
                            genommen, currentStoryState);
                }
            default:
                return noTime();
        }
    }

    private AvTimeSpan nehmen_wacheWirdAufmerksam(final IGameObject genommen) {
        n.add(t(PARAGRAPH,
                "Da wird eine Wache auf dich aufmerksam. "
                        + "„Wie seid Ihr hier hereingekommen?“, fährt sie dich "
                        // STORY Ausspinnen: Auf dem Fest kriegt der Frosch beim Essen
                        //  seinen Willen.
                        + "scharf an. „Das Fest ist erst am Sonntag. Heute "
                        + "ist Samstag und Ihr habt hier nichts zu suchen!“ "
                        + "Mit kräftiger Hand klopft die Wache auf ihre Hellebarde"));
        getReactor().stateComp().setState(AUFMERKSAM);
        sc.memoryComp().upgradeKnown(SCHLOSSWACHE, Known.getKnown(getLichtverhaeltnisse(
                sc.locationComp().getLocationId())));
        sc.feelingsComp().setMood(Mood.ANGESPANNT);
        return secs(20);
    }

    private AvTimeSpan nehmenGoldeneKugel_wacheIstAufmerksam(final IHasStoringPlaceGO room,
                                                             final ILocatableGO goldeneKugel,
                                                             final StoryState currentStoryState) {
        if (db.counterDao().incAndGet(
                "SchlosswacheReactions_nehmenGoldeneKugel_wacheIstAufmerksam") == 1) {
            return nehmenGoldeneKugel_wacheIstAufmerksam_erwischt(room, goldeneKugel,
                    currentStoryState);
        }

        return nehmenGoldeneKugel_wacheIstAufmerksam_nichtErwischt(goldeneKugel,
                currentStoryState);
    }

    private AvTimeSpan nehmenGoldeneKugel_wacheIstAufmerksam_erwischt(final IHasStoringPlaceGO room,
                                                                      final ILocatableGO goldeneKugel,
                                                                      final StoryState currentStoryState) {
        final ImmutableList.Builder<StoryStateBuilder> alt = ImmutableList.builder();

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(
                    t(WORD,
                            ", doch keine Sekunde später baut sich die Wache vor dir auf. "
                                    + "„Wir haben hier sehr gute Verliese, Ihr dürftet "
                                    + "überrascht sein“, sagt sie und schaut dich "
                                    + "durchdringend an"));
        }

        alt.add(t(SENTENCE, "„Ihr habt da wohl etas, das nicht Euch gehört“, "
                + "wirst du von hinten angesprochen."));

        n.add(alt(alt));

        // STORY Geschichte ausspinnen: Spieler muss die Kugel selbst
        //  ablegen bzw. kommt ggf. in den Kerker
        n.add(t(PARAGRAPH,
                "Da legst du doch besser die schöne goldene Kugel "
                        + "wieder an ihren Platz")
                .undWartest()
                .persPronKandidat(goldeneKugel));

        sc.memoryComp().setLastAction(Action.Type.ABLEGEN, goldeneKugel);

        goldeneKugel.locationComp().setLocation(room);
        return secs(20);
    }

    private AvTimeSpan nehmenGoldeneKugel_wacheIstAufmerksam_nichtErwischt(
            final IGameObject goldeneKugel,
            final StoryState currentStoryState) {
        final ImmutableList.Builder<StoryStateBuilder> alt = ImmutableList.builder();

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(t(WORD,
                    ", während "
                            + getReactorDescription(false).nom()
                            + " gerade damit beschäftigt ist, ihre Waffen zu polieren")
                    .dann());
        } else {
            alt.add(t(SENTENCE,
                    "Du hast großes Glück, denn "
                            + getReactorDescription(false).nom()
                            + " ist gerade damit beschäftigt, ihre Waffen zu polieren")
                    .dann());
        }

        alt.add(t(SENTENCE,
                capitalize(getDescription(getReactor()).dat())
                        + " ist anscheinend nichts aufgefallen")
                .dann());

        n.add(alt(alt));

        return secs(3);
    }

    @Override
    public AvTimeSpan onEssen(final IHasStoringPlaceGO room, final StoryState currentStoryState) {
        // Der Schlosswache ist es egal, wenn der Spieler beim Fest etwas ist.
        // Und bei anderen Speisen ist es ihr erst recht egal.
        return noTime();
    }

    @Override
    public AvTimeSpan onAblegen(final IHasStoringPlaceGO room, final IGameObject abgelegt,
                                final StoryState currentStoryState) {
        if (!room.is(SCHLOSS_VORHALLE)) {
            return noTime();
        }

        switch (getReactor().stateComp().getState()) {
            case AUFMERKSAM:
                return ablegen_wacheIstAufmerksam(abgelegt, currentStoryState);
            default:
                return noTime();
        }
    }

    private AvTimeSpan ablegen_wacheIstAufmerksam(
            final IGameObject abgelegt,
            final StoryState currentStoryState) {
        if (db.counterDao()
                .incAndGet("SchlosswacheReactions_ablegen_wacheIstAufmerksam") > 2) {
            return noTime();
        }

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            t(WORD, ", von der kopfschüttelnden Wache beobachtet")
                    .dann();
            return secs(5);
        }

        n.add(alt(
                t(SENTENCE,
                        capitalize(getReactorDescription(false).nom())
                                + " beoabachtet dich dabei")
                        .dann(),
                t(SENTENCE,
                        capitalize(getReactorDescription().nom())
                                + " sieht dir belustig dabei zu")
                        .dann()
        ));
        sc.feelingsComp().setMood(Mood.ANGESPANNT);
        return secs(5);
    }

    @Override
    public AvTimeSpan onHochwerfen(final IHasStoringPlaceGO room, final ILocatableGO object,
                                   final StoryState currentStoryState) {

        if (!room.is(SCHLOSS_VORHALLE)) {
            return noTime();
        }

        switch (getReactor().stateComp().getState()) {
            case AUFMERKSAM:
                if (object.is(GOLDENE_KUGEL)) {
                    return hochwerfenGoldeneKugel_wacheIstAufmerksam(room, object,
                            currentStoryState);
                }
            default:
                return noTime();
        }

    }

    private AvTimeSpan hochwerfenGoldeneKugel_wacheIstAufmerksam(final IHasStoringPlaceGO room,
                                                                 final ILocatableGO goldeneKugel,
                                                                 final StoryState currentStoryState) {

        final boolean scHatKugelAufgefangen =
                goldeneKugel.locationComp().hasLocation(SPIELER_CHARAKTER);

        if (scHatKugelAufgefangen) {
            return hochwerfenGoldeneKugel_wacheIstAufmerksam_wiederGefangen(room,
                    goldeneKugel, currentStoryState);
        }

        return hochwerfenGoldeneKugel_wacheIstAufmerksam_nichtWiederGefangen(room,
                goldeneKugel, currentStoryState);
    }

    private AvTimeSpan hochwerfenGoldeneKugel_wacheIstAufmerksam_wiederGefangen(
            final IHasStoringPlaceGO room,
            final ILocatableGO goldeneKugel,
            final StoryState currentStoryState) {
        n.add(t(PARAGRAPH, "„Was treibt Ihr für einen Unfug, legt sofort das "
                + "Schmuckstück wieder hin!“, "
                + "ruft dir "
                + getReactorDescription(true).nom()
                + " zu"));

        // TODO Geschichte ausspinnen: Spieler muss die Kugel selbst
        //  ablegen bzw. kommt ggf. in den Kerker
        n.add(t(PARAGRAPH,
                "Eingeschüchtert legst du die schöne goldene Kugel wieder an ihren Platz")
                .undWartest()
                .persPronKandidat(goldeneKugel));

        goldeneKugel.locationComp().setLocation(room);
        sc.memoryComp().setLastAction(Action.Type.ABLEGEN, goldeneKugel);
        sc.feelingsComp().setMood(Mood.ANGESPANNT);

        return secs(10);
    }

    private AvTimeSpan hochwerfenGoldeneKugel_wacheIstAufmerksam_nichtWiederGefangen(
            final IHasStoringPlaceGO room,
            final IGameObject goldeneKugel,
            final StoryState currentStoryState) {
        n.add(t(PARAGRAPH, "Die Wache sieht sehr missbilligend zu"));

        return secs(3);
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now,
                                   final StoryState currentStoryState) {
        AvTimeSpan timeElapsed = noTime();

        if (isWithin(SCHLOSSFEST_BEGINN_DATE_TIME, lastTime, now)) {
            timeElapsed = timeElapsed.plus(schlossfestBeginnt(currentStoryState));
        }

        return timeElapsed;
    }

    private AvTimeSpan schlossfestBeginnt(final StoryState currentStoryState) {

        if (sc.locationComp().hasLocation(SCHLOSS_VORHALLE)) {
            return schlossfestBeginnt_Vorhalle(currentStoryState);
        }

        // Beim Fest ist die Schlosswache beschäftigt
        getReactor().stateComp().setState(UNAUFFAELLIG);
        return noTime(); // Passiert nebenher und braucht KEINE zusätzliche Zeit
    }

    @NonNull
    private AvTimeSpan schlossfestBeginnt_Vorhalle(final StoryState currentStoryState) {
        final String text;
        if (currentStoryState.dann()) {
            text = "Dann spricht dich die Wache an:";
        } else {
            text = "Die Wache spricht dich an:";
        }

        n.add(t(PARAGRAPH, text + " „Wenn ich Euch dann hinausbitten dürfte? Wer wollte "
                + " denn den Vorbereitungen für das große Fest im Wege stehen?“ – Nein, "
                + "das willst du sicher nicht.\n"
                + "Draußen sind Handwerker dabei, im ganzen Schlossgarten kleine bunte "
                + "Pagoden aufzubauen. Du schaust eine Zeitlang zu.\n"
                + "Zunehmend strömen von allen Seiten Menschen herzu und wie es scheint, ist auch "
                + "der Zugang zum Schloss für alle geöffnet. Aus dem Schloss weht dich der "
                + "Geruch von Gebratenem an.")
                .letzterRaum(SCHLOSS_VORHALLE)
                .beendet(PARAGRAPH));

        sc.locationComp().setLocation(DRAUSSEN_VOR_DEM_SCHLOSS);
        sc.feelingsComp().setMood(NEUTRAL);

        // Der Spieler weiß jetzt, dass das Schlossfest läuft
        db.counterDao().inc(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN);

        // Beim Fest ist die Schlosswache mit anderen Dingen beschäftigt
        getReactor().stateComp().setState(UNAUFFAELLIG);
        return mins(45);
    }
}