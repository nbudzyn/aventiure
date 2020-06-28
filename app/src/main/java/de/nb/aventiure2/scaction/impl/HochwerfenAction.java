package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
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

    private final ILocationGO room;

    public static <OBJ extends IDescribableGO & ILocatableGO>
    Collection<HochwerfenAction> buildActions(
            final AvDatabase db, final StoryState initialStoryState,
            final ILocationGO room, @NonNull final OBJ gameObject) {
        if (gameObject instanceof ILivingBeingGO) {
            return ImmutableList.of();
        }

        // STORY Nicht jedes Object lässt sich hochwerfen...
        return ImmutableList.of(new HochwerfenAction<>(db, initialStoryState, gameObject, room));
    }

    private HochwerfenAction(final AvDatabase db,
                             final StoryState initialStoryState,
                             @NonNull final OBJ object,
                             final ILocationGO room) {
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

        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoErstesMal() {
        final IHasStateGO froschprinz = (IHasStateGO) load(db, FROSCHPRINZ);

        if (room.is(IM_WALD_BEIM_BRUNNEN) &&
                !froschprinz.stateComp().hasState(UNAUFFAELLIG)) {
            return narrateAndDoFroschBekannt((IHasStateGO & ILocatableGO) froschprinz);
        }

        final SubstantivischePhrase objectDesc =
                getAnaphPersPronWennMglSonstDescription(object, false);

        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            return narrateAndDoHochwerfenAuffangen(
                    satzanschluss(", wirfst " +
                            objectDesc.akk() +
                            " in die Höhe und fängst " +
                            objectDesc.persPron().akk() +
                            " wieder auf", secs(3))
                            .dann());
        }

        final String emotionSatzglied = emotionSatzgliedFuersHochwerfen();

        return narrateAndDoHochwerfenAuffangen(
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
        // TODO Für jede State Machine ein eigenes State Enum. Nur im PCD
        //  sind es Strings.

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

            return narrateAndDoHochwerfenAuffangen(
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
        if (froschprinz.locationComp().hasRecursiveLocation(room)) {
            return timeElapsed;
        }

        if (!sc.feelingsComp().getMood().isTraurigerAls(ETWAS_GEKNICKT)) {
            sc.feelingsComp().setMood(ETWAS_GEKNICKT);
        }

        final String praefix =
                room.storingPlaceComp().getLichtverhaeltnisse() == HELL ? "Weit und breit" :
                        "Im Dunkeln ist";

        return n.add(
                neuerSatz(praefix + " kein Frosch zu sehen… Das war vielleicht etwas "
                        + "ungeschickt, oder?", timeElapsed));
    }

    private AvTimeSpan narrateAndDoHochwerfenAuffangen(final AbstractDescription<?> desc) {
        AvTimeSpan timeElapsed = n.add(desc);

        timeElapsed = timeElapsed.plus(
                object.locationComp().narrateAndDoLeaveReactions(SPIELER_CHARAKTER)
        );

        return timeElapsed.plus(GameObjects.narrateAndDoReactions()
                // Hier wird das onLeave() und onEnter() etwas missbraucht, um Reaktionen auf
                // das Hochwerfen zu provozieren. Da from und to gleich sind, müssen wir
                // from explizit angebeben, es darf nicht die lastLocation verwendet werden,
                // wie es sonst automatisch passiert.
                .onEnter(object,
                        // from
                        loadSC(db),
                        // to
                        SPIELER_CHARAKTER));
    }

    private AvTimeSpan narrateAndDoObjectFaelltSofortInDenBrunnen() {
        final Nominalphrase objectDesc = getDescription(object, false);

        final AvTimeSpan timeSpan = n.add(
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

        return timeSpan.plus(
                object.locationComp().narrateAndSetLocation(UNTEN_IM_BRUNNEN));
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
                    room.storingPlaceComp().getLichtverhaeltnisse() == DUNKEL ?
                            "– bei dieser Dunkelheit schon gar nicht" : "";

            final AvTimeSpan timeSpan = n.add(du("wirfst",
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

            sc.feelingsComp().setMood(UNTROESTLICH);

            return timeSpan.plus(object.locationComp().narrateAndSetLocation(UNTEN_IM_BRUNNEN));
        }

        final AvTimeSpan timeSpan = n.add(du("schleuderst",
                getDescription(object).akk() +
                        " übermütig noch einmal in die Luft, aber sie wieder aufzufangen will dir "
                        +
                        "dieses Mal nicht gelingen. " +
                        capitalize(getDescription(object, true).nom()) +
                        " landet " +
                        room.storingPlaceComp().getLocationMode().getWo(),
                "übermütig",
                secs(5)));

        if (!sc.feelingsComp().getMood().isTraurigerAls(ETWAS_GEKNICKT)) {
            sc.feelingsComp().setMood(ETWAS_GEKNICKT);
        }

        return timeSpan.plus(object.locationComp().narrateAndSetLocation(room));
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return false;
    }

    @NonNull
    private Action buildMemorizedAction() {
        return new Action(Action.Type.HOCHWERFEN, object);
    }
}
