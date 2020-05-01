package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ETWAS_GEKNICKT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.UNTROESTLICH;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.syscomp.storingplace.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

/**
 * Der Spieler(charakter) wirft einen Gegenstand hoch.
 */
@ParametersAreNonnullByDefault
public class HochwerfenAction<OBJ extends IDescribableGO & ILocatableGO>
        extends AbstractScAction {
    @NonNull
    private final OBJ object;

    private final IHasStoringPlaceGO room;

    public static <OBJ extends IDescribableGO & ILocatableGO>
    Collection<HochwerfenAction> buildActions(
            final AvDatabase db, final StoryState initialStoryState,
            final IHasStoringPlaceGO room, @NonNull final OBJ gameObject) {
        if (gameObject instanceof ILivingBeingGO) {
            // STORY Froschprinz o.Ä. hochwerfen?
            return ImmutableList.of();
        }

        // TODO Nicht jedes Object lässt sich hochwerfen...
        return ImmutableList.of(
                new HochwerfenAction<>(db, initialStoryState,
                        gameObject, room));
    }

    private HochwerfenAction(final AvDatabase db,
                             final StoryState initialStoryState,
                             @NonNull final OBJ object,
                             final IHasStoringPlaceGO room) {
        super(db, initialStoryState);
        this.object = object;
        this.room = room;
    }

    @Override
    public String getType() {
        return "actionHochwerfen";
    }

    @Override
    @NonNull
    public String getName() {
        return capitalize(getDescription(object).akk()) + " hochwerfen";
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        AvTimeSpan timeElapsed = noTime();

        if (!isDefinitivWiederholung()) {
            timeElapsed = timeElapsed.plus(narrateAndDoErstesMal());
        } else {
            timeElapsed = timeElapsed.plus(narrateAndDoWiederholung());
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());

        return timeElapsed.plus(creatureReactionsCoordinator.onHochwerfen(room, object));
    }

    private AvTimeSpan narrateAndDoErstesMal() {
        final IHasStateGO froschprinz = (IHasStateGO) load(db, FROSCHPRINZ);

        if (room.is(IM_WALD_BEIM_BRUNNEN) && !froschprinz.stateComp()
                .hasState(UNAUFFAELLIG)) {
            return narrateAndDoFroschBekannt((IHasStateGO & ILocatableGO) froschprinz);
        }

        final DeklinierbarePhrase objectDesc =
                getAnaphPersPronWennMglSonstDescription(object, false);

        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            return n.add(
                    satzanschluss(", wirfst " +
                            objectDesc.akk() +
                            " in die Höhe und fängst " +
                            objectDesc.persPron().akk() +
                            " wieder auf", secs(3))
                            .dann());
        }

        final String emotionSatzglied = emotionSatzgliedFuersHochwerfen();

        return n.add(
                du(PARAGRAPH, "wirfst",
                        objectDesc.akk()
                                + " "
                                + emotionSatzglied
                                + " in die Höhe und fängst " +
                                objectDesc.persPron().akk() +
                                " wieder auf",
                        emotionSatzglied, secs(3))
                        .dann());
    }

    @NonNull
    private String emotionSatzgliedFuersHochwerfen() {
        return sc.feelingsComp().getMood().getAdverbialeAngabe().getText();
    }

    private <F extends IHasStateGO & ILocatableGO> AvTimeSpan narrateAndDoFroschBekannt(
            final F froschprinz) {
        // TODO Eine eigene Frosch-Statemaschine-Component bauen - oder eine
        //  eine froschprinzComp, die die Frosch-KI beeinhaltet?
        if (froschprinz.stateComp().hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                HAT_NACH_BELOHNUNG_GEFRAGT,
                HAT_FORDERUNG_GESTELLT)) {
            return narrateAndDoObjectFaelltSofortInDenBrunnen();
            // Der Spieler hat ein weiteres Objekt in den Brunnen fallen
            // lassen, obwohl er noch mit dem Frosch verhandelt.
        }

        // Der Frosch ist nicht mehr in Stimmung, Dinge aus dem Brunnen zu holen.
        if (object.is(GOLDENE_KUGEL)) {
            final Nominalphrase objectDesc = getDescription(object);

            return n.add(
                    du(PARAGRAPH, "wirfst", objectDesc.akk() +
                            " hoch in die Luft und fängst " +
                            objectDesc.persPron().akk() +
                            " geschickt wieder auf", secs(3))
                            .dann());
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.
        final AvTimeSpan timeElapsed = narrateAndDoObjectFaelltSofortInDenBrunnen();
        if (froschprinz.locationComp().hasLocation(room)) {
            return timeElapsed;
        }

        if (!sc.feelingsComp().getMood().isTraurigerAls(ETWAS_GEKNICKT)) {
            sc.feelingsComp().setMood(ETWAS_GEKNICKT);
        }

        final String praefix =
                room.getLichtverhaeltnisseInside() == HELL ? "Weit und breit" : "Im Dunkeln ist";

        return n.add(
                neuerSatz(praefix + " kein Frosch zu sehen… Das war vielleicht etwas "
                        + "ungeschickt, oder?", timeElapsed));
    }

    private AvTimeSpan narrateAndDoObjectFaelltSofortInDenBrunnen() {
        final Nominalphrase objectDesc = getDescription(object, false);

        object.locationComp().setLocation(UNTEN_IM_BRUNNEN);

        return n.add(
                du(PARAGRAPH, "wirfst", objectDesc.akk() +
                        " nur ein einziges Mal in die Höhe, " +
                        "aber wie das Unglück es will, fällt " +
                        objectDesc.persPron().akk() +
                        " sofort in den Brunnen: " +
                        "Platsch! – weg " +
                        SeinUtil.istSind(objectDesc.getNumerusGenus()) +
                        " " +
                        objectDesc.persPron().akk(), "nur ein einziges Mal", secs(10))
                        .dann(!initialStoryState.dann())
                        .beendet(PARAGRAPH));
    }

    @NonNull
    private AvTimeSpan narrateAndDoWiederholung() {
        final IHasStateGO froschprinz = (IHasStateGO) load(db, FROSCHPRINZ);

        if (db.counterDao()
                .incAndGet("HochwerfenAction_Wiederholung") == 1 ||
                (room.is(IM_WALD_BEIM_BRUNNEN) && !froschprinz.stateComp()
                        .hasState(UNAUFFAELLIG))) {
            return n.addAlt(
                    neuerSatz("Und noch einmal – was ein schönes Spiel!", secs(3))
                            .dann(),
                    neuerSatz("So ein Spaß!", secs(3))
                            .dann(),
                    neuerSatz("Und in die Höhe damit – juchei!", secs(3))
                            .dann());
        }

        if (room.is(IM_WALD_BEIM_BRUNNEN)) {
            final String dunkelheitNachsatz =
                    room.getLichtverhaeltnisseInside() == DUNKEL ?
                            "– bei dieser Dunkelheit schon gar nicht" : "";

            object.locationComp().setLocation(UNTEN_IM_BRUNNEN);
            sc.feelingsComp().setMood(UNTROESTLICH);

            return n.add(du("wirfst",
                    getDescription(object).akk() +
                            " noch einmal in die Höhe… doch oh nein, " +
                            getDescription(object, true).nom() +
                            " fällt dir nicht in die Hände, sondern schlägt vorbei " +
                            "auf den Brunnenrand und rollt geradezu ins Wasser hinein." +
                            " Du folgst ihr mit den Augen nach, aber " +
                            getDescription(object, true).nom() +
                            " verschwindet, und der Brunnen ist tief, so tief, dass " +
                            "man keinen Grund sieht"
                            + dunkelheitNachsatz,
                    "noch einmal", secs(10))
                    .beendet(PARAGRAPH));
        }

        object.locationComp().setLocation(room);
        if (!sc.feelingsComp().getMood().isTraurigerAls(ETWAS_GEKNICKT)) {
            sc.feelingsComp().setMood(ETWAS_GEKNICKT);
        }

        return n.add(du("schleuderst",
                getDescription(object).akk() +
                        " übermütig noch einmal in die Luft, aber sie wieder aufzufangen will dir "
                        +
                        "dieses Mal nicht gelingen. " +
                        capitalize(getDescription(object, true).nom()) +
                        " landet " +
                        room.storingPlaceComp().getLocationMode().getWo(),
                "übermütig",
                secs(5)));
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.HOCHWERFEN, object);
    }
}
