package de.nb.aventiure2.playeraction.action.creature.reaction;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.IPlayerAction;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryStateBuilder;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.AbstractEntityData;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;

import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.SENTENCE;
import static de.nb.aventiure2.data.storystate.StoryState.StartsNew.WORD;
import static de.nb.aventiure2.data.world.creature.Creature.Key.SCHLOSSWACHE;
import static de.nb.aventiure2.data.world.creature.CreatureState.AUFMERKSAM;
import static de.nb.aventiure2.data.world.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.object.AvObject.Key.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.object.AvObject.extractObject;
import static de.nb.aventiure2.data.world.object.AvObject.isObject;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.playeraction.action.util.PlayerActionUtil.random;

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
            if (random(2) == 1) {
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
                        .letzterRaum(newRoom),
                t(PARAGRAPH,
                        "„Heho, was wird das?“, tönt dir eine laute Stimme entgegen. "
                                + "„Als ob hier ein jeder "
                                + "nach Belieben hereinspazieren könnt. Das würde dem König so "
                                + "passen. Und "
                                + "seinem Kerkermeister auch.“ "
                                + "Du bleibst besser draußen")
                        .letzterRaum(newRoom)
                // TODO Markierung in der Art .kommma(), dass nach einem Satz ein Absatz
                // sinnvoll wäre.
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
                wacheWirdAufmerksam(wacheInRoom, genommenData);
                return;
            case AUFMERKSAM:
                if (isObject(genommenData, GOLDENE_KUGEL)) {
                    wacheAufmerksam_goldeneKugelGenommen(room, wacheInRoom,
                            (ObjectData) genommenData, currentStoryState);
                    return;
                }
        }
    }

    private void wacheWirdAufmerksam(final CreatureData wache,
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
    }

    private void wacheAufmerksam_goldeneKugelGenommen(final AvRoom room,
                                                      final CreatureData wache,
                                                      final ObjectData goldeneKugelData,
                                                      final StoryState currentStoryState) {
        if (random(2) == 1) {
            wacheAufmerksam_beimNehmenGoldenerKugelErwischt(room, wache, goldeneKugelData,
                    currentStoryState);
            return;
        }

        wacheAufmerksam_beimNehmenGoldenerKugelNichtErwischt(wache, goldeneKugelData,
                currentStoryState);
    }

    private void wacheAufmerksam_beimNehmenGoldenerKugelErwischt(final AvRoom room,
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
                "Da legst du doch glatt die schöne goldene Kugel "
                        + "wieder an ihren Platz")
                .undWartest()
                .letztesObject(goldeneKugelData.getObject()));

        db.playerInventoryDao().letGo(goldeneKugelData.getObject());
        db.objectDataDao().setRoom(goldeneKugelData.getObject(), room);
    }

    private void wacheAufmerksam_beimNehmenGoldenerKugelNichtErwischt(
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
                wacheBeobachtetAblegen(wacheInRoom, abgelegtData, currentStoryState);
                return;
        }
    }

    private void wacheBeobachtetAblegen(
            final CreatureData wache, final AbstractEntityData abgelegtData,
            final StoryState currentStoryState) {
        if (random(3) == 1) {
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
                    wacheAufmerksam_goldeneKugelHochgeworfen(room, wacheInRoom,
                            objectData, currentStoryState);
                    return;
                }
                return;
        }
    }

    private void wacheAufmerksam_goldeneKugelHochgeworfen(final AvRoom room,
                                                          final CreatureData wacheInRoom,
                                                          final ObjectData goldeneKugelData,
                                                          final StoryState currentStoryState) {

        final boolean scHatKugelAufgefangen =
                db.playerInventoryDao().getInventory().stream().map(o -> o.getKey())
                        .anyMatch(k -> k == goldeneKugelData.getObject().getKey());

        if (scHatKugelAufgefangen) {
            wacheAufmerksam_goldeneKugelHochgeworfenUndWiederGefangen(room, wacheInRoom,
                    goldeneKugelData, currentStoryState);
            return;
        }

        wacheAufmerksam_goldeneKugelHochgeworfenUndNICHTWiederGefangen(room, wacheInRoom,
                goldeneKugelData, currentStoryState);
    }

    private void wacheAufmerksam_goldeneKugelHochgeworfenUndWiederGefangen(final AvRoom room,
                                                                           final CreatureData wacheInRoom,
                                                                           final ObjectData goldeneKugelData,
                                                                           final StoryState currentStoryState) {
        n.add(t(PARAGRAPH, "„Was treibt Ihr für einen Unfug, legt sofort das "
                + "Schmuckstück wieder hin!“, "
                + "ruft dir "
                + wacheInRoom.nom(false)
                + " zu")
                .letztesObject(goldeneKugelData.getObject()));

        // TODO Geschichte ausspinnen: Spieler muss die Kugel selbst
        // ablegen bzw. kommt ggf. in den Kerker
        n.add(t(PARAGRAPH,
                "Da legst du doch glatt die schöne goldene Kugel "
                        + "wieder an ihren Platz")
                .undWartest()
                .letztesObject(goldeneKugelData.getObject()));

        db.playerInventoryDao().letGo(goldeneKugelData.getObject());
        db.objectDataDao().setRoom(goldeneKugelData.getObject(), room);
    }

    private void wacheAufmerksam_goldeneKugelHochgeworfenUndNICHTWiederGefangen(
            final AvRoom room,
            final CreatureData wacheInRoom,
            final ObjectData goldeneKugelData,
            final StoryState currentStoryState) {
        n.add(t(PARAGRAPH, "Die Wache sieht sehr missbilligend zu")
                .letztesObject(goldeneKugelData.getObject()));
    }
}
