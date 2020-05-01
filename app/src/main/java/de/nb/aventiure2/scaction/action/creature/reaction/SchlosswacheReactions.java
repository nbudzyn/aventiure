package de.nb.aventiure2.scaction.action.creature.reaction;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.feelings.Mood;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.memory.Known;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.NumerusGenus;

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
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

class SchlosswacheReactions
        <W extends IDescribableGO & ILivingBeingGO & IHasStateGO & ILocatableGO>
        extends AbstractCreatureReactions<W> {
    SchlosswacheReactions(final AvDatabase db) {
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
                return n.add(neuerSatz(capitalize(
                        getReactorDescription(true).nom())
                        + " scheint dich nicht zu bemerken", secs(3)));
            }
        }

        return scMussDasSchlossVerlassen(oldRoom);
    }

    private AvTimeSpan scMussDasSchlossVerlassen(final IHasStoringPlaceGO oldRoom) {
        // STORY Ausspinnen: Der Spieler sollte selbst entscheiden,
        //  ob der das Schloss wieder verlässt - oder ggf. im Kerker landet.

        sc.locationComp().setLocation(oldRoom);
        sc.memoryComp().setLastAction(new Action(Action.Type.BEWEGEN, (GameObject) null));

        return n.addAlt(
                neuerSatz("Die Wache spricht dich sofort an und macht dir unmissverständlich "
                                + "klar, dass du hier "
                                + "vor dem großen Fest nicht erwünscht bist. Du bist "
                                + "leicht zu "
                                + "überzeugen und trittst wieder "
                                + schlossVerlassenWohinDescription(
                        ((IHasStoringPlaceGO) load(db, SCHLOSS_VORHALLE)), oldRoom)
                                // "in den Sonnenschein"
                                + " hinaus",
                        secs(10))
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "„Heho, was wird das?“, tönt dir eine laute Stimme entgegen. "
                                + "„Als ob hier ein jeder "
                                + "nach Belieben hereinspazieren könnt. Das würde dem König so "
                                + "passen. Und "
                                + "seinem Kerkermeister auch.“ "
                                + "Du bleibst besser draußen",
                        secs(10))
                        .beendet(PARAGRAPH)
        );
    }

    private static String schlossVerlassenWohinDescription(final IHasStoringPlaceGO schlossRoom,
                                                           final IHasStoringPlaceGO wohinRoom) {
        final Lichtverhaeltnisse lichtverhaeltnisseImSchloss =
                schlossRoom.storingPlaceComp().getLichtverhaeltnisseInside();
        final Lichtverhaeltnisse lichtverhaeltnisseDraussen =
                wohinRoom.storingPlaceComp().getLichtverhaeltnisseInside();
        if (lichtverhaeltnisseImSchloss  // Im Schloss ist es immer hell, wenn es also draußen
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
                return nehmen_wacheWirdAufmerksam();
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

    private AvTimeSpan nehmen_wacheWirdAufmerksam() {
        getReactor().stateComp().setState(AUFMERKSAM);
        sc.memoryComp().upgradeKnown(SCHLOSSWACHE,
                Known.getKnown(sc.locationComp().getLocation().storingPlaceComp()
                        .getLichtverhaeltnisseInside()));
        sc.feelingsComp().setMood(Mood.ANGESPANNT);

        return n.add(
                neuerSatz(PARAGRAPH, "Da wird eine Wache auf dich aufmerksam. "
                                + "„Wie seid Ihr hier hereingekommen?“, fährt sie dich "
                                // STORY Ausspinnen: Auf dem Fest kriegt der Frosch beim Essen
                                //  seinen Willen.
                                + "scharf an. „Das Fest ist erst am Sonntag. Heute "
                                + "ist Samstag und Ihr habt hier nichts zu suchen!“ "
                                + "Mit kräftiger Hand klopft die Wache auf ihre Hellebarde",
                        secs(20)));
    }

    private AvTimeSpan nehmenGoldeneKugel_wacheIstAufmerksam(final IHasStoringPlaceGO room,
                                                             final ILocatableGO goldeneKugel,
                                                             final StoryState currentStoryState) {
        if (db.counterDao().incAndGet(
                "SchlosswacheReactions_nehmenGoldeneKugel_wacheIstAufmerksam") == 1) {
            return nehmenGoldeneKugel_wacheIstAufmerksam_erwischt(room, goldeneKugel,
                    currentStoryState);
        }

        return nehmenGoldeneKugel_wacheIstAufmerksam_nichtErwischt(
                currentStoryState);
    }

    private AvTimeSpan nehmenGoldeneKugel_wacheIstAufmerksam_erwischt(final IHasStoringPlaceGO room,
                                                                      final ILocatableGO goldeneKugel,
                                                                      final StoryState currentStoryState) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss(", doch keine Sekunde später baut sich die Wache vor dir auf. "
                            + "„Wir haben hier sehr gute Verliese, Ihr dürftet "
                            + "überrascht sein“, sagt sie und schaut dich "
                            + "durchdringend an",
                    secs(15)));
        }

        alt.add(neuerSatz("„Ihr habt da wohl etwas, das nicht Euch gehört“, "
                        + "wirst du von hinten angesprochen.",
                secs(15)));

        final AvTimeSpan timeElapsed = n.addAlt(alt);

        // STORY Geschichte ausspinnen: Spieler muss die Kugel selbst
        //  ablegen bzw. kommt ggf. in den Kerker
        sc.memoryComp().setLastAction(Action.Type.ABLEGEN, goldeneKugel);

        goldeneKugel.locationComp().setLocation(room);

        return timeElapsed.plus(n.add(neuerSatz(PARAGRAPH,
                "Da legst du doch besser die schöne goldene Kugel "
                        + "wieder an ihren Platz",
                secs(5))
                .undWartest()
                .phorikKandidat(NumerusGenus.F, goldeneKugel.getId())));
    }

    private AvTimeSpan nehmenGoldeneKugel_wacheIstAufmerksam_nichtErwischt(
            final StoryState currentStoryState) {
        final ImmutableList.Builder<AbstractDescription<?>> alt = ImmutableList.builder();

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(satzanschluss(
                    ", während "
                            + getReactorDescription().nom()
                            + " gerade damit beschäftigt ist, ihre Waffen zu polieren",
                    secs(3))
                    .dann());
        } else {
            alt.add(du(
                    "hast", "großes Glück, denn "
                            + getReactorDescription().nom()
                            + " ist gerade damit beschäftigt, ihre Waffen zu polieren", secs(3))
                    .komma(true)
                    .dann());
        }

        alt.add(neuerSatz(
                capitalize(getDescription(getReactor()).dat())
                        + " ist anscheinend nichts aufgefallen",
                secs(3))
                .dann());

        return n.addAlt(alt);
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
                return ablegen_wacheIstAufmerksam(currentStoryState);
            default:
                return noTime();
        }
    }

    private AvTimeSpan ablegen_wacheIstAufmerksam(
            final StoryState currentStoryState) {
        if (db.counterDao()
                .incAndGet("SchlosswacheReactions_ablegen_wacheIstAufmerksam") > 2) {
            return noTime();
        }

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            return n.add(satzanschluss(", von der kopfschüttelnden Wache beobachtet",
                    secs(5))
                    .dann());
        }

        sc.feelingsComp().setMood(Mood.ANGESPANNT);
        return n.addAlt(
                neuerSatz(capitalize(getReactorDescription().nom())
                        + " beoabachtet dich dabei", secs(5))
                        .dann(),
                neuerSatz(capitalize(getReactorDescription().nom())
                        + " sieht dir belustig dabei zu", secs(5))
                        .dann());
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
                    return hochwerfenGoldeneKugel_wacheIstAufmerksam(room, object
                    );
                }
            default:
                return noTime();
        }

    }

    private AvTimeSpan hochwerfenGoldeneKugel_wacheIstAufmerksam(final IHasStoringPlaceGO room,
                                                                 final ILocatableGO goldeneKugel) {

        final boolean scHatKugelAufgefangen =
                goldeneKugel.locationComp().hasLocation(SPIELER_CHARAKTER);

        if (scHatKugelAufgefangen) {
            return hochwerfenGoldeneKugel_wacheIstAufmerksam_wiederGefangen(room,
                    goldeneKugel);
        }

        return hochwerfenGoldeneKugel_wacheIstAufmerksam_nichtWiederGefangen(
        );
    }

    private AvTimeSpan hochwerfenGoldeneKugel_wacheIstAufmerksam_wiederGefangen(
            final IHasStoringPlaceGO room,
            final ILocatableGO goldeneKugel) {
        final AvTimeSpan timeSpan = n.add(
                neuerSatz(PARAGRAPH, "„Was treibt Ihr für einen Unfug, legt sofort das "
                        + "Schmuckstück wieder hin!“, "
                        + "ruft dir "
                        + getReactorDescription(true).nom()
                        + " zu", secs(5)));

        // TODO Geschichte ausspinnen: Spieler muss die Kugel selbst
        //  ablegen bzw. kommt ggf. in den Kerker
        goldeneKugel.locationComp().setLocation(room);
        sc.memoryComp().setLastAction(Action.Type.ABLEGEN, goldeneKugel);
        sc.feelingsComp().setMood(Mood.ANGESPANNT);

        return timeSpan.plus(n.add(du(PARAGRAPH,
                "legst", "die schöne goldene Kugel eingeschüchtert wieder an ihren Platz",
                "eingeschüchtert",
                secs(5))
                .undWartest()
                .phorikKandidat(NumerusGenus.F, goldeneKugel.getId())));
    }

    private AvTimeSpan hochwerfenGoldeneKugel_wacheIstAufmerksam_nichtWiederGefangen() {
        return n.add(
                neuerSatz(PARAGRAPH, "Die Wache sieht sehr missbilligend zu", secs(3)));
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

        sc.locationComp().setLocation(DRAUSSEN_VOR_DEM_SCHLOSS);
        sc.feelingsComp().setMood(NEUTRAL);

        // Der Spieler weiß jetzt, dass das Schlossfest läuft
        db.counterDao().incAndGet(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN);

        // Beim Fest ist die Schlosswache mit anderen Dingen beschäftigt
        getReactor().stateComp().setState(UNAUFFAELLIG);

        return n.add(neuerSatz(PARAGRAPH,
                text + " „Wenn ich Euch dann hinausbitten dürfte? Wer wollte "
                        + " denn den Vorbereitungen für das große Fest im Wege stehen?“ – Nein, "
                        + "das willst du sicher nicht.\n"
                        + "Draußen sind Handwerker dabei, im ganzen Schlossgarten kleine bunte "
                        + "Pagoden aufzubauen. Du schaust eine Zeitlang zu.\n"
                        + "Zunehmend strömen von allen Seiten Menschen herzu und wie es scheint, ist auch "
                        + "der Zugang zum Schloss für alle geöffnet. Aus dem Schloss weht dich der "
                        + "Geruch von Gebratenem an.", mins(45))
                .beendet(PARAGRAPH));
    }
}