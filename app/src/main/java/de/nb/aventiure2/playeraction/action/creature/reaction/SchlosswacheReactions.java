package de.nb.aventiure2.playeraction.action.creature.reaction;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.entity.base.AbstractEntityData;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.playeraction.action.AblegenAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.WORD;
import static de.nb.aventiure2.data.world.entity.creature.Creature.Key.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.AUFMERKSAM;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.entity.object.AvObject.Key.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.entity.object.AvObject.extractObject;
import static de.nb.aventiure2.data.world.entity.object.AvObject.isObject;
import static de.nb.aventiure2.data.world.invisible.Invisible.Key.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.invisible.InvisibleState.BEGONNEN;
import static de.nb.aventiure2.data.world.invisible.Invisibles.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.invisible.Invisibles.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.room.AvRoom.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.room.AvRoom.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.time.AvDateTime.isWithin;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

class SchlosswacheReactions extends AbstractCreatureReactions {
    public SchlosswacheReactions(final AvDatabase db,
                                 final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public AvTimeSpan onLeaveRoom(final AvRoom oldRoom, final CreatureData wache,
                                  final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onEnterRoom(final AvRoom oldRoom, final AvRoom newRoom,
                                  final CreatureData wache,
                                  final StoryState currentStoryState) {
        if (newRoom != AvRoom.SCHLOSS_VORHALLE) {
            return noTime();
        }

        if (wache.hasState(UNAUFFAELLIG)) {
            return noTime();
        }

        final ObjectData goldeneKugel = db.objectDataDao().get(GOLDENE_KUGEL);
        if (goldeneKugel.getRoom() == AvRoom.SCHLOSS_VORHALLE) {
            if (db.counterDao().incAndGet(
                    "SchlosswacheReactions_onEnterRoom_SchlossVorhalle") > 1) {
                n.add(t(SENTENCE,
                        capitalize(wache.nom(true))
                                + " scheint dich nicht zu bemerken")
                        .letzterRaum(oldRoom));
                return secs(3);
            }
        }

        return scMussDasSchlossVerlassen(oldRoom, newRoom);
    }

    private AvTimeSpan scMussDasSchlossVerlassen(final AvRoom oldRoom, final AvRoom newRoom) {
        // STORY Ausspinnen: Der Spieler sollte selbst entscheiden,
        //  ob der das Schloss wieder verlässt - oder ggf. im Kerker landet.
        n.add(alt(
                t(SENTENCE,
                        "Die Wache spricht dich sofort an und macht dir unmissverständlich "
                                + "klar, dass du hier "
                                + "am Tag vor dem großen Fest nicht erwünscht bist. Du bist "
                                + "leicht zu "
                                + "überzeugen und trittst wieder in den Sonnenschein hinaus")
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

        db.playerLocationDao().setRoom(oldRoom);

        return secs(10);
    }

    @Override
    public AvTimeSpan onNehmen(final AvRoom room, final CreatureData wacheInRoom,
                               final AbstractEntityData genommenData,
                               final StoryState currentStoryState) {
        if (room != AvRoom.SCHLOSS_VORHALLE) {
            return noTime();
        }

        if (db.invisibleDataDao().getInvisible(SCHLOSSFEST).hasState(BEGONNEN)) {
            return noTime();
        }

        switch (wacheInRoom.getState()) {
            case UNAUFFAELLIG:
                return nehmen_wacheWirdAufmerksam(wacheInRoom, genommenData);
            case AUFMERKSAM:
                if (isObject(genommenData, GOLDENE_KUGEL)) {
                    return nehmenGoldeneKugel_wacheIstAufmerksam(room, wacheInRoom,
                            (ObjectData) genommenData, currentStoryState);
                }
            default:
                return noTime();
        }
    }

    private AvTimeSpan nehmen_wacheWirdAufmerksam(final CreatureData wache,
                                                  final AbstractEntityData genommenData) {
        n.add(t(PARAGRAPH,
                "Da wird eine Wache auf dich aufmerksam. "
                        + "„Wie seid Ihr hier hereingekommen?“, fährt sie dich "
                        // STORY Ausspinnen: Auf dem Fest kriegt der Frosch beim Essen
                        //  seinen Willen.
                        + "scharf an. „Das Fest ist erst am Sonntag. Heute "
                        + "ist Samstag und Ihr habt hier nichts zu suchen!“ "
                        + "Mit kräftiger Hand klopft die Wache auf ihre Hellebarde")
                .letztesObject(extractObject(genommenData)));
        db.creatureDataDao().setState(SCHLOSSWACHE, AUFMERKSAM);
        db.creatureDataDao().setKnown(SCHLOSSWACHE);
        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.ANGESPANNT);
        return secs(20);
    }

    private AvTimeSpan nehmenGoldeneKugel_wacheIstAufmerksam(final AvRoom room,
                                                             final CreatureData wache,
                                                             final ObjectData goldeneKugelData,
                                                             final StoryState currentStoryState) {
        if (db.counterDao().incAndGet(
                "SchlosswacheReactions_nehmenGoldeneKugel_wacheIstAufmerksam") == 1) {
            return nehmenGoldeneKugel_wacheIstAufmerksam_erwischt(room, wache, goldeneKugelData,
                    currentStoryState);
        }

        return nehmenGoldeneKugel_wacheIstAufmerksam_nichtErwischt(wache, goldeneKugelData,
                currentStoryState);
    }

    private AvTimeSpan nehmenGoldeneKugel_wacheIstAufmerksam_erwischt(final AvRoom room,
                                                                      final CreatureData wache,
                                                                      final ObjectData goldeneKugelData,
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
                .letzteAktion(AblegenAction.class)
                .letztesObject(goldeneKugelData.getObject()));

        db.playerInventoryDao().letGo(goldeneKugelData.getObject());
        db.objectDataDao().setRoom(goldeneKugelData.getObject(), room);
        return secs(20);
    }

    private AvTimeSpan nehmenGoldeneKugel_wacheIstAufmerksam_nichtErwischt(
            final CreatureData wache,
            final ObjectData goldeneKugelData,
            final StoryState currentStoryState) {
        final ImmutableList.Builder<StoryStateBuilder> alt = ImmutableList.builder();

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            alt.add(t(WORD,
                    ", während "
                            + wache.nom(false)
                            + " gerade damit beschäftigt ist, ihre Waffen zu polieren")
                    .dann()
                    .letztesObject(extractObject(goldeneKugelData)));
        } else {
            alt.add(t(SENTENCE,
                    "Du hast großes Glück, denn "
                            + wache.nom(false)
                            + " ist gerade damit beschäftigt, ihre Waffen zu polieren")
                    .dann()
                    .letztesObject(extractObject(goldeneKugelData)));
        }

        alt.add(t(SENTENCE,
                capitalize(wache.dat())
                        + " ist anscheinend nichts aufgefallen")
                .dann()
                .letztesObject(extractObject(goldeneKugelData)));

        n.add(alt(alt));

        return secs(3);
    }

    @Override
    public AvTimeSpan onAblegen(final AvRoom room, final CreatureData wacheInRoom,
                                final AbstractEntityData abgelegtData,
                                final StoryState currentStoryState) {
        if (room != AvRoom.SCHLOSS_VORHALLE) {
            return noTime();
        }

        switch (wacheInRoom.getState()) {
            case AUFMERKSAM:
                return ablegen_wacheIstAufmerksam(wacheInRoom, abgelegtData, currentStoryState);
            default:
                return noTime();
        }
    }

    private AvTimeSpan ablegen_wacheIstAufmerksam(
            final CreatureData wache, final AbstractEntityData abgelegtData,
            final StoryState currentStoryState) {
        if (db.counterDao()
                .incAndGet("SchlosswacheReactions_ablegen_wacheIstAufmerksam") > 2) {
            return noTime();
        }

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            t(WORD, ", von der kopfschüttelnden Wache beobachtet")
                    .dann()
                    .letztesObject(extractObject(abgelegtData));
            return secs(5);
        }

        n.add(alt(
                t(SENTENCE,
                        capitalize(wache.nom(false))
                                + " beoabachtet dich dabei")
                        .dann()
                        .letztesObject(extractObject(abgelegtData)),
                t(SENTENCE,
                        capitalize(wache.nom())
                                + " sieht dir belustig dabei zu")
                        .dann()
                        .letztesObject(extractObject(abgelegtData))
        ));
        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.ANGESPANNT);
        return secs(5);
    }

    @Override
    public AvTimeSpan onHochwerfen(final AvRoom room, final CreatureData wacheInRoom,
                                   final ObjectData objectData,
                                   final StoryState currentStoryState) {
        if (room != AvRoom.SCHLOSS_VORHALLE) {
            return noTime();
        }

        switch (wacheInRoom.getState()) {
            case AUFMERKSAM:
                if (objectData.getKey() == GOLDENE_KUGEL) {
                    return hochwerfenGoldeneKugel_wacheIstAufmerksam(room, wacheInRoom,
                            objectData, currentStoryState);
                }
            default:
                return noTime();
        }
    }

    private AvTimeSpan hochwerfenGoldeneKugel_wacheIstAufmerksam(final AvRoom room,
                                                                 final CreatureData wacheInRoom,
                                                                 final ObjectData goldeneKugelData,
                                                                 final StoryState currentStoryState) {

        final boolean scHatKugelAufgefangen =
                db.playerInventoryDao().getInventory().stream().map(o -> o.getKey())
                        .anyMatch(k -> k == goldeneKugelData.getKey());

        if (scHatKugelAufgefangen) {
            return hochwerfenGoldeneKugel_wacheIstAufmerksam_wiederGefangen(room, wacheInRoom,
                    goldeneKugelData, currentStoryState);
        }

        return hochwerfenGoldeneKugel_wacheIstAufmerksam_nichtWiederGefangen(room, wacheInRoom,
                goldeneKugelData, currentStoryState);
    }

    private AvTimeSpan hochwerfenGoldeneKugel_wacheIstAufmerksam_wiederGefangen(final AvRoom room,
                                                                                final CreatureData wacheInRoom,
                                                                                final ObjectData goldeneKugelData,
                                                                                final StoryState currentStoryState) {
        n.add(t(PARAGRAPH, "„Was treibt Ihr für einen Unfug, legt sofort das "
                + "Schmuckstück wieder hin!“, "
                + "ruft dir "
                + wacheInRoom.nom(true)
                + " zu")
                .letztesObject(goldeneKugelData.getObject()));

        // TODO Geschichte ausspinnen: Spieler muss die Kugel selbst
        //  ablegen bzw. kommt ggf. in den Kerker
        n.add(t(PARAGRAPH,
                "Eingeschüchtert legst du die schöne goldene Kugel "
                        + "wieder an ihren Platz")
                .undWartest()
                .letzteAktion(AblegenAction.class)
                .letztesObject(goldeneKugelData.getObject()));

        db.playerInventoryDao().letGo(goldeneKugelData.getObject());
        db.objectDataDao().setRoom(goldeneKugelData.getObject(), room);
        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.ANGESPANNT);

        return secs(10);
    }

    private AvTimeSpan hochwerfenGoldeneKugel_wacheIstAufmerksam_nichtWiederGefangen(
            final AvRoom room,
            final CreatureData wacheInRoom,
            final ObjectData goldeneKugelData,
            final StoryState currentStoryState) {
        n.add(t(PARAGRAPH, "Die Wache sieht sehr missbilligend zu")
                .letztesObject(goldeneKugelData.getObject()));

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
        final AvRoom currentRoom = db.playerLocationDao().getPlayerLocation().getRoom();

        if (currentRoom == SCHLOSS_VORHALLE) {
            return schlossfestBeginnt_Vorhalle(currentStoryState);
        }

        // Beim Fest ist die Schlosswache beschäftigt
        db.creatureDataDao().setState(SCHLOSSWACHE, UNAUFFAELLIG);
        return noTime(); // Passiert nebenher und braucht KEINE zusätzliche Zeit
    }

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

        db.playerLocationDao().setRoom(DRAUSSEN_VOR_DEM_SCHLOSS);
        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.NEUTRAL);

        // Der Spieler weiß jetzt, dass das Schlossfest läuft
        db.counterDao().inc(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN);

        // Beim Fest ist die Schlosswache mit anderen Dingen beschäftigt
        db.creatureDataDao().setState(SCHLOSSWACHE, UNAUFFAELLIG);
        return mins(45);
    }
}