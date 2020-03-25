package de.nb.aventiure2.playeraction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.world.creature.CreatureData;
import de.nb.aventiure2.data.world.object.AvObject;
import de.nb.aventiure2.data.world.object.ObjectData;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.german.DuDescription;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.playeraction.AbstractPlayerAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.player.stats.PlayerStateOfMind.UNTROESTLICH;
import static de.nb.aventiure2.data.world.room.AvRoom.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.german.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

/**
 * Der Spieler(charakter) wirft einen Gegenstand hoch.
 */
public class HochwerfenAction extends AbstractObjectAction {
    private final AvRoom room;
    private final CreatureData froschprinzCreatureData;

    public static Collection<AbstractPlayerAction> buildActions(
            final AvDatabase db, final StoryState initialStoryState,
            final AvRoom room, final ObjectData objectData,
            final CreatureData froschprinzCreatureData) {
        // TODO Nicht jedes Object lässt sich hochwerfen...
        return ImmutableList.of(
                new HochwerfenAction(db, initialStoryState,
                        objectData, room, froschprinzCreatureData));
    }

    private HochwerfenAction(final AvDatabase db,
                             final StoryState initialStoryState,
                             final ObjectData objectData,
                             final AvRoom room,
                             final CreatureData froschprinzCreatureData) {
        super(db, initialStoryState, objectData);

        this.room = room;
        this.froschprinzCreatureData = froschprinzCreatureData;
    }

    @Override
    @NonNull
    public String getName() {
        return capitalize(getObjectData().akk()) + " hochwerfen";
    }

    @Override
    public void narrateAndDo() {
        if (initialStoryState.lastObjectWas(getObject()) &&
                initialStoryState.lastActionWas(HochwerfenAction.class)) {
            narrateAndDoWiederholung();
        } else {
            narrateAndDoErstesMal();
        }

        creatureReactionsCoordinator.onHochwerfen(room, getObjectData());
    }

    private void narrateAndDoErstesMal() {
        if (room == IM_WALD_BEIM_BRUNNEN && !froschprinzCreatureData.hasState(UNAUFFAELLIG)) {
            narrateAndDoFroschBekannt();
            return;
        }

        final Nominalphrase objectDesc = getObjectData().getDescription(false);

        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            n.add(t(StoryState.StructuralElement.WORD,
                    ", wirfst " +
                            objectDesc.akk() +
                            " in die Höhe und fängst " +
                            objectDesc.persPron().akk() +
                            " wieder auf")
                    .dann());
            return;
        }

        n.add(t(StoryState.StructuralElement.PARAGRAPH,
                vorfeldEmotionFuersHochwerfen()
                        + " wirfst " +
                        objectDesc.akk() +
                        " in die Höhe und fängst " +
                        objectDesc.persPron().akk() +
                        " wieder auf")
                .dann());
    }

    private String vorfeldEmotionFuersHochwerfen() {
        switch (db.playerStatsDao().getPlayerStats().getStateOfMind()) {
            case ANGESPANNT:
                return "Aus Trotz";
            case VOLLER_FREUDE:
                return "Fröhlich";
            default:
                return "Aus Langeweile";
        }
    }

    private void narrateAndDoFroschBekannt() {
        if (froschprinzCreatureData.hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                HAT_NACH_BELOHNUNG_GEFRAGT,
                HAT_FORDERUNG_GESTELLT)) {
            narrateAndDoObjectFaelltSofortInDenBrunnen();
            // Der Spieler hat ein weiteres Objekt in den Brunnen fallen
            // lassen, obwohl er noch mit dem Frosch verhandelt.
            return;
        }

        // Der Frosch ist nicht mehr in Stimmung, Dinge aus dem Brunnen zu holen.
        if (getObject().getKey() != AvObject.Key.GOLDENE_KUGEL) {
            final Nominalphrase objectDesc = getObjectData().getDescription(false);

            n.add(t(StoryState.StructuralElement.PARAGRAPH,
                    "Du wirfst " +
                            objectDesc.akk() +
                            " hoch in die Luft und fängst " +
                            objectDesc.persPron().akk() +
                            " geschickt wieder auf")
                    .dann());
            return;
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.

        narrateAndDoObjectFaelltSofortInDenBrunnen();
        if (froschprinzCreatureData.getRoom() == room) {
            return;
        }

        n.add(t(StoryState.StructuralElement.SENTENCE,
                "Weit und breit kein Frosch zu sehen… Das war vielleicht etwas ungeschickt, " +
                        "oder?"));
    }

    private void narrateAndDoObjectFaelltSofortInDenBrunnen() {
        final Nominalphrase objectDesc = getObjectData().getDescription(false);

        final DuDescription duDesc = du("wirfst",
                objectDesc.akk() +
                        " nur ein einziges Mal in die Höhe, " +
                        "aber wie das Unglück es will, fällt " +
                        objectDesc.persPron().akk() +
                        " sofort in den Brunnen: " +
                        "Platsch! – weg " +
                        SeinUtil.istSind(objectDesc.getNumerusGenus()) +
                        " " +
                        objectDesc.persPron().akk(), false,
                false, !initialStoryState.dann());

        if (initialStoryState.dann()) {
            n.add(t(StructuralElement.PARAGRAPH,
                    duDesc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("dann"))
                    .beendet(PARAGRAPH));
        } else {
            n.add(t(StructuralElement.PARAGRAPH,
                    duDesc.getDescriptionHauptsatz())
                    .beendet(PARAGRAPH));
        }

        db.playerInventoryDao().letGo(getObject());
        db.objectDataDao().setDemSCInDenBrunnenGefallen(getObject(),
                true);
    }

    private void narrateAndDoWiederholung() {
        if (db.counterDao()
                .isFirstTime("SchlosswacheReactions_HochwerfenAction_Wiederholung") ||
                (room == IM_WALD_BEIM_BRUNNEN && !froschprinzCreatureData
                        .hasState(UNAUFFAELLIG))) {
            n.add(alt(t(StoryState.StructuralElement.SENTENCE,
                    "Und noch einmal – was ein schönes Spiel!")
                            .dann(),
                    t(StoryState.StructuralElement.SENTENCE,
                            "So ein Spaß!")
                            .dann(),
                    t(StoryState.StructuralElement.SENTENCE,
                            "Und in die Höhe damit – juchei!")
                            .dann()));
            return;
        }

        if (room == IM_WALD_BEIM_BRUNNEN) {
            n.add(t(StoryState.StructuralElement.SENTENCE,
                    "Noch einmal wirfst du " +
                            getObjectData().akk() +
                            " in die Höhe… doch oh nein, " +
                            getObjectData().nom(true) +
                            " fällt dir nicht in die Hände, sondern schlägt vorbei " +
                            "auf den Brunnenrand und rollt geradezu ins Wasser hinein." +
                            " Du folgst ihr mit den Augen nach, aber " +
                            getObjectData().nom(true) +
                            " verschwindet, und der Brunnen ist tief, so tief, dass " +
                            "man keinen Grund sieht.")
                    .beendet(PARAGRAPH));

            db.playerInventoryDao().letGo(getObject());
            db.objectDataDao().setDemSCInDenBrunnenGefallen(getObject(), true);
            db.playerStatsDao().setStateOfMind(UNTROESTLICH);
            return;
        }

        n.add(t(StructuralElement.SENTENCE,
                "Übermütig schleuderst du " +
                        getObjectData().akk() +
                        " noch einmal in die Luft, aber sie wieder aufzufangen will dir " +
                        "dieses Mal nicht gelingen. " +
                        capitalize(getObjectData().nom(true)) +
                        " landet " +
                        room.getLocationMode().getWo()));

        db.playerInventoryDao().letGo(getObject());
        db.objectDataDao().setRoom(getObject(), room);
    }
}
