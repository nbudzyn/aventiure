package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.entity.creature.CreatureData;
import de.nb.aventiure2.data.world.entity.object.ObjectData;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.DuDescription;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.SeinUtil;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.entity.creature.CreatureState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.entity.object.AvObjects.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.player.stats.ScStateOfMind.ETWAS_GEKNICKT;
import static de.nb.aventiure2.data.world.player.stats.ScStateOfMind.UNTROESTLICH;
import static de.nb.aventiure2.data.world.room.Rooms.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

/**
 * Der Spieler(charakter) wirft einen Gegenstand hoch.
 */
public class HochwerfenAction extends AbstractObjectAction {
    private final GameObject room;
    private final CreatureData froschprinzCreatureData;

    public static Collection<HochwerfenAction> buildActions(
            final AvDatabase db, final StoryState initialStoryState,
            final GameObject room, final ObjectData objectData,
            final CreatureData froschprinzCreatureData) {
        // TODO Nicht jedes Object lässt sich hochwerfen...
        return ImmutableList.of(
                new HochwerfenAction(db, initialStoryState,
                        objectData, room, froschprinzCreatureData));
    }

    private HochwerfenAction(final AvDatabase db,
                             final StoryState initialStoryState,
                             final ObjectData objectData,
                             final GameObject room,
                             final CreatureData froschprinzCreatureData) {
        super(db, initialStoryState, objectData);

        this.room = room;
        this.froschprinzCreatureData = froschprinzCreatureData;
    }

    @Override
    public String getType() {
        return "actionHochwerfen";
    }

    @Override
    @NonNull
    public String getName() {
        return capitalize(getObjectData().akk()) + " hochwerfen";
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        AvTimeSpan timeElapsed = noTime();

        if (!initialStoryState.lastActionWas(HochwerfenAction.class) ||
                !initialStoryState.lastObjectWas(getObject())) {
            timeElapsed = timeElapsed.plus(narrateAndDoErstesMal(initialStoryState));
        } else {
            timeElapsed = timeElapsed.plus(narrateAndDoWiederholung());
        }

        return timeElapsed.plus(creatureReactionsCoordinator.onHochwerfen(room, getObjectData()));
    }

    private AvTimeSpan narrateAndDoErstesMal(final StoryState currentStoryState) {
        if (room.is(IM_WALD_BEIM_BRUNNEN) && !froschprinzCreatureData
                .hasState(UNAUFFAELLIG)) {
            return narrateAndDoFroschBekannt();
        }

        final Nominalphrase objectDesc = getObjectData().getDescription(false);

        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            n.add(t(StoryState.StructuralElement.WORD,
                    ", wirfst " +
                            getObjektNominalphraseOderWennSoebenErwaehntPersPron(currentStoryState,
                                    objectDesc)
                                    .akk() +
                            " in die Höhe und fängst " +
                            objectDesc.persPron().akk() +
                            " wieder auf")
                    .dann());
            return secs(3);
        }

        n.add(t(StoryState.StructuralElement.PARAGRAPH,
                vorfeldEmotionFuersHochwerfen()
                        + " wirfst du " +
                        getObjektNominalphraseOderWennSoebenErwaehntPersPron(currentStoryState,
                                objectDesc)
                                .akk() +
                        " in die Höhe und fängst " +
                        objectDesc.persPron().akk() +
                        " wieder auf")
                .dann());
        return secs(3);
    }

    /**
     * Gibt etwas wie "die goldene Kugel" zurück - oder "sie", wenn die goldene Kugel
     * das letzte Objekt war.
     * <p>>
     * Hiermit lassen sich Wiederholungen vermeiden: "Du hebst die goldene Kugel auf, wirfts
     * <i>sie</i>> in die Höhe..."
     */
    private DeklinierbarePhrase getObjektNominalphraseOderWennSoebenErwaehntPersPron(
            final StoryState currentStoryState, final Nominalphrase objectDesc) {
        return currentStoryState.lastObjectWas(getObject()) ? objectDesc.persPron() :
                objectDesc;
    }

    private String vorfeldEmotionFuersHochwerfen() {
        return capitalize(db.playerStatsDao().getPlayerStats().getStateOfMind()
                .getAdverbialeAngabe().getText());
    }

    private AvTimeSpan narrateAndDoFroschBekannt() {
        if (froschprinzCreatureData.hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                HAT_NACH_BELOHNUNG_GEFRAGT,
                HAT_FORDERUNG_GESTELLT)) {
            return narrateAndDoObjectFaelltSofortInDenBrunnen();
            // Der Spieler hat ein weiteres Objekt in den Brunnen fallen
            // lassen, obwohl er noch mit dem Frosch verhandelt.
        }

        // Der Frosch ist nicht mehr in Stimmung, Dinge aus dem Brunnen zu holen.
        if (getObject().is(GOLDENE_KUGEL)) {
            final Nominalphrase objectDesc = getObjectData().getDescription(false);

            n.add(t(StoryState.StructuralElement.PARAGRAPH,
                    "Du wirfst " +
                            objectDesc.akk() +
                            " hoch in die Luft und fängst " +
                            objectDesc.persPron().akk() +
                            " geschickt wieder auf")
                    .dann());
            return secs(3);
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.
        final AvTimeSpan timeElapsed = narrateAndDoObjectFaelltSofortInDenBrunnen();
        if (froschprinzCreatureData.getRoom() == room) {
            return timeElapsed;
        }

        final String praefix =
                getLichtverhaeltnisse(room) == HELL ? "Weit und breit" : "Im Dunkeln ist";

        n.add(t(StoryState.StructuralElement.SENTENCE,
                praefix + " kein Frosch zu sehen… Das war vielleicht etwas ungeschickt, " +
                        "oder?"));
        if (!db.playerStatsDao().getPlayerStats().getStateOfMind().isTraurigerAls(ETWAS_GEKNICKT)) {
            db.playerStatsDao().setStateOfMind(ETWAS_GEKNICKT);
        }
        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoObjectFaelltSofortInDenBrunnen() {
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
                false, !initialStoryState.dann(),
                secs(10));

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

        return duDesc.getTimeElapsed();
    }

    private AvTimeSpan narrateAndDoWiederholung() {
        if (db.counterDao()
                .incAndGet("HochwerfenAction_Wiederholung") == 1 ||
                (room.is(IM_WALD_BEIM_BRUNNEN) && !froschprinzCreatureData
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
            return secs(3);
        }

        if (room.is(IM_WALD_BEIM_BRUNNEN)) {
            final String dunkelheitNachsatz =
                    getLichtverhaeltnisse(room) == DUNKEL ?
                            "– bei dieser Dunkelheit schon gar nicht" : "";

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
                            "man keinen Grund sieht"
                            + dunkelheitNachsatz
                            + ".")
                    .beendet(PARAGRAPH));

            db.playerInventoryDao().letGo(getObject());
            db.objectDataDao().setDemSCInDenBrunnenGefallen(getObject(), true);
            db.playerStatsDao().setStateOfMind(UNTROESTLICH);
            return secs(10);
        }

        n.add(t(StructuralElement.SENTENCE,
                "Übermütig schleuderst du " +
                        getObjectData().akk() +
                        " noch einmal in die Luft, aber sie wieder aufzufangen will dir " +
                        "dieses Mal nicht gelingen. " +
                        capitalize(getObjectData().nom(true)) +
                        " landet " +
                        room.getStoringPlace().getLocationMode().getWo()));

        db.playerInventoryDao().letGo(getObject());
        db.objectDataDao().setRoom(getObject(), room);
        if (!db.playerStatsDao().getPlayerStats().getStateOfMind().isTraurigerAls(ETWAS_GEKNICKT)) {
            db.playerStatsDao().setStateOfMind(ETWAS_GEKNICKT);
        }
        return secs(5);
    }
}
