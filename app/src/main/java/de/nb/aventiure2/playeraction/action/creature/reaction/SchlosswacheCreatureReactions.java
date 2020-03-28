package de.nb.aventiure2.playeraction.action.creature.reaction;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.playeraction.action.AblegenAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.WORD;
import static de.nb.aventiure2.data.world.creature.Creature.Key.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.creature.CreatureState.AUFMERKSAM;
import static de.nb.aventiure2.data.world.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.object.AvObject.Key.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.object.AvObject.extractObject;
import static de.nb.aventiure2.data.world.object.AvObject.isObject;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

class SchlosswacheCreatureReactions extends AbstractCreatureReactions {
    public SchlosswacheCreatureReactions(final AvDatabase db,
                                         final Class<? extends IPlayerAction> playerActionClass) {
        super(db, playerActionClass);
    }

    @Override
    public void onLeaveRoom(final AvRoom oldRoom, final CreatureData wache,
                            final StoryState currentStoryState) {
    }

    @Override
    public void onEnterRoom(final AvRoom oldRoom, final AvRoom newRoom, final CreatureData wache,
                            final StoryState currentStoryState) {
        if (newRoom != AvRoom.SCHLOSS_VORHALLE) {
            return;
        }

        if (wache.hasState(UNAUFFAELLIG)) {
            return;
        }

        final ObjectData goldeneKugel = db.objectDataDao().get(GOLDENE_KUGEL);
        if (goldeneKugel.getRoom() == AvRoom.SCHLOSS_VORHALLE) {
            if (db.counterDao().incAndGet(
                    "SchlosswacheReactions_onEnterRoom_SchlossVorhalle") > 1) {
                n.add(t(SENTENCE,
                        capitalize(wache.nom(true))
                                + " scheint dich nicht zu bemerken")
                        .letzterRaum(oldRoom));
                return;
            }
        }

        scMussDasSchlossVerlassen(oldRoom, newRoom);
    }

    private void scMussDasSchlossVerlassen(final AvRoom oldRoom, final AvRoom newRoom) {
        // TODO Ausspinnen: Der Spieler sollte selbst entscheiden,
        // ob der das Schloss wieder verlässt - oder ggf. im Kerker landet.
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
    }

    @Override
    public void onNehmen(final AvRoom room, final CreatureData wacheInRoom,
                         final AbstractEntityData genommenData,
                         final StoryState currentStoryState) {
        if (room != AvRoom.SCHLOSS_VORHALLE) {
            return;
        }

        switch (wacheInRoom.getState()) {
            case UNAUFFAELLIG:
                nehmen_wacheWirdAufmerksam(wacheInRoom, genommenData);
                return;
            case AUFMERKSAM:
                if (isObject(genommenData, GOLDENE_KUGEL)) {
                    nehmenGoldeneKugel_wacheIstAufmerksam(room, wacheInRoom,
                            (ObjectData) genommenData, currentStoryState);
                    return;
                }
        }
    }

    private void nehmen_wacheWirdAufmerksam(final CreatureData wache,
                                            final AbstractEntityData genommenData) {
        n.add(t(PARAGRAPH,
                "Da wird eine Wache auf dich aufmerksam. "
                        + "„Wie seid Ihr hier hereingekommen?“, fährt sie dich "
                        // TODO Ausspinnen: Nach einer Übernachtung (z.B. in einer Hütte im
                        // Wald?) ist Sonntag, es gibt ein Fest mit Essen, und der
                        // Frosch kriegt seinen Willen.
                        + "scharf an. „Das Fest ist erst am Sonntag. Heute "
                        + "ist Samstag und Ihr habt hier nichts zu suchen!“ "
                        + "Mit kräftiger Hand klopft die Wache auf ihre Hellebarde")
                .letztesObject(extractObject(genommenData)));
        db.creatureDataDao().setState(SCHLOSSWACHE, AUFMERKSAM);
        db.creatureDataDao().setKnown(SCHLOSSWACHE);
        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.ANGESPANNT);
    }

    private void nehmenGoldeneKugel_wacheIstAufmerksam(final AvRoom room,
                                                       final CreatureData wache,
                                                       final ObjectData goldeneKugelData,
                                                       final StoryState currentStoryState) {
        if (db.counterDao().incAndGet(
                "SchlosswacheReactions_nehmenGoldeneKugel_wacheIstAufmerksam") > 1) {
            nehmenGoldeneKugel_wacheIstAufmerksam_erwischt(room, wache, goldeneKugelData,
                    currentStoryState);
            return;
        }

        nehmenGoldeneKugel_wacheIstAufmerksam_nichtErwischt(wache, goldeneKugelData,
                currentStoryState);
    }

    private void nehmenGoldeneKugel_wacheIstAufmerksam_erwischt(final AvRoom room,
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

        // TODO Geschichte ausspinnen: Spieler muss die Kugel selbst
        // ablegen bzw. kommt ggf. in den Kerker
        n.add(t(PARAGRAPH,
                "Da legst du doch besser die schöne goldene Kugel "
                        + "wieder an ihren Platz")
                .undWartest()
                .letzteAktion(AblegenAction.class)
                .letztesObject(goldeneKugelData.getObject()));

        db.playerInventoryDao().letGo(goldeneKugelData.getObject());
        db.objectDataDao().setRoom(goldeneKugelData.getObject(), room);
    }

    private void nehmenGoldeneKugel_wacheIstAufmerksam_nichtErwischt(
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
    }

    @Override
    public void onAblegen(final AvRoom room, final CreatureData wacheInRoom,
                          final AbstractEntityData abgelegtData,
                          final StoryState currentStoryState) {
        if (room != AvRoom.SCHLOSS_VORHALLE) {
            return;
        }

        switch (wacheInRoom.getState()) {
            case AUFMERKSAM:
                ablegen_wacheIstAufmerksam(wacheInRoom, abgelegtData, currentStoryState);
                return;
        }
    }

    private void ablegen_wacheIstAufmerksam(
            final CreatureData wache, final AbstractEntityData abgelegtData,
            final StoryState currentStoryState) {
        if (db.counterDao()
                .incAndGet("SchlosswacheReactions_ablegen_wacheIstAufmerksam") > 2) {
            return;
        }

        if (currentStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            t(WORD, ", von der kopfschüttelnden Wache beobachtet")
                    .dann()
                    .letztesObject(extractObject(abgelegtData));
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
    }

    @Override
    public void onHochwerfen(final AvRoom room, final CreatureData wacheInRoom,
                             final ObjectData objectData,
                             final StoryState currentStoryState) {
        if (room != AvRoom.SCHLOSS_VORHALLE) {
            return;
        }

        switch (wacheInRoom.getState()) {
            case AUFMERKSAM:
                if (objectData.getObject().getKey() == GOLDENE_KUGEL) {
                    hochwerfenGoldeneKugel_wacheIstAufmerksam(room, wacheInRoom,
                            objectData, currentStoryState);
                    return;
                }
                return;
        }
    }

    private void hochwerfenGoldeneKugel_wacheIstAufmerksam(final AvRoom room,
                                                           final CreatureData wacheInRoom,
                                                           final ObjectData goldeneKugelData,
                                                           final StoryState currentStoryState) {

        final boolean scHatKugelAufgefangen =
                db.playerInventoryDao().getInventory().stream().map(o -> o.getKey())
                        .anyMatch(k -> k == goldeneKugelData.getObject().getKey());

        if (scHatKugelAufgefangen) {
            hochwerfenGoldeneKugel_wacheIstAufmerksam_wiederGefangen(room, wacheInRoom,
                    goldeneKugelData, currentStoryState);
            return;
        }

        hochwerfenGoldeneKugel_wacheIstAufmerksam_nichtWiederGefangen(room, wacheInRoom,
                goldeneKugelData, currentStoryState);
    }

    private void hochwerfenGoldeneKugel_wacheIstAufmerksam_wiederGefangen(final AvRoom room,
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
        // ablegen bzw. kommt ggf. in den Kerker
        n.add(t(PARAGRAPH,
                "Eingeschüchtert legst du die schöne goldene Kugel "
                        + "wieder an ihren Platz")
                .undWartest()
                .letzteAktion(AblegenAction.class)
                .letztesObject(goldeneKugelData.getObject()));

        db.playerInventoryDao().letGo(goldeneKugelData.getObject());
        db.objectDataDao().setRoom(goldeneKugelData.getObject(), room);
        db.playerStatsDao().setStateOfMind(PlayerStateOfMind.ANGESPANNT);
    }

    private void hochwerfenGoldeneKugel_wacheIstAufmerksam_nichtWiederGefangen(
            final AvRoom room,
            final CreatureData wacheInRoom,
            final ObjectData goldeneKugelData,
            final StoryState currentStoryState) {
        n.add(t(PARAGRAPH, "Die Wache sieht sehr missbilligend zu")
                .letztesObject(goldeneKugelData.getObject()));
    }
}
