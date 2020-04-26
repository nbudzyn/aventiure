package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.storystate.StoryState.StructuralElement;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.DeklinierbarePhrase;
import de.nb.aventiure2.german.base.DuDescription;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.praedikat.SeinUtil;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.DUNKEL;
import static de.nb.aventiure2.data.world.lichtverhaeltnisse.Lichtverhaeltnisse.HELL;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ETWAS_GEKNICKT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.UNTROESTLICH;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;

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
            timeElapsed = timeElapsed.plus(narrateAndDoErstesMal(initialStoryState));
        } else {
            timeElapsed = timeElapsed.plus(narrateAndDoWiederholung());
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());

        return timeElapsed.plus(creatureReactionsCoordinator.onHochwerfen(room, object));
    }

    private AvTimeSpan narrateAndDoErstesMal(final StoryState currentStoryState) {
        final IHasStateGO froschprinz = (IHasStateGO) load(db, FROSCHPRINZ);

        if (room.is(IM_WALD_BEIM_BRUNNEN) && !froschprinz.stateComp()
                .hasState(UNAUFFAELLIG)) {
            return narrateAndDoFroschBekannt((IHasStateGO & ILocatableGO) froschprinz);
        }

        final Nominalphrase objectDesc = getDescription(object, false);

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
     * <p>
     * Hiermit lassen sich Wiederholungen vermeiden: "Du hebst die goldene Kugel auf, wirfts
     * <i>sie</i> in die Höhe..."
     */
    private DeklinierbarePhrase getObjektNominalphraseOderWennSoebenErwaehntPersPron(
            final StoryState currentStoryState, final Nominalphrase objectDesc) {
        return currentStoryState.persPronKandidatIs(object) ? objectDesc.persPron() :
                objectDesc;
    }

    @NonNull
    private String vorfeldEmotionFuersHochwerfen() {
        return capitalize(sc.feelingsComp().getMood().getAdverbialeAngabe().getText());
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

            return n.add(PARAGRAPH,
                    du("wirfst", objectDesc.akk() +
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

        final String praefix =
                getLichtverhaeltnisse(room) == HELL ? "Weit und breit" : "Im Dunkeln ist";

        n.add(t(StoryState.StructuralElement.SENTENCE,
                praefix + " kein Frosch zu sehen… Das war vielleicht etwas ungeschickt, " +
                        "oder?"));
        if (!sc.feelingsComp().getMood().isTraurigerAls(ETWAS_GEKNICKT)) {
            sc.feelingsComp().setMood(ETWAS_GEKNICKT);
        }
        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoObjectFaelltSofortInDenBrunnen() {
        final Nominalphrase objectDesc = getDescription(object, false);

        final boolean dann = !initialStoryState.dann();
        final DuDescription duDesc = du("wirfst", objectDesc.akk() +
                " nur ein einziges Mal in die Höhe, " +
                "aber wie das Unglück es will, fällt " +
                objectDesc.persPron().akk() +
                " sofort in den Brunnen: " +
                "Platsch! – weg " +
                SeinUtil.istSind(objectDesc.getNumerusGenus()) +
                " " +
                objectDesc.persPron().akk(), "nur ein einziges Mal", secs(10))
                .dann(dann);

        if (initialStoryState.dann()) {
            n.add(t(StructuralElement.PARAGRAPH,
                    duDesc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("dann"))
                    .beendet(PARAGRAPH));
        } else {
            n.add(t(StructuralElement.PARAGRAPH,
                    duDesc.getDescriptionHauptsatz())
                    .beendet(PARAGRAPH));
        }

        object.locationComp().setLocation(UNTEN_IM_BRUNNEN);

        return duDesc.getTimeElapsed();
    }

    @NonNull
    private AvTimeSpan narrateAndDoWiederholung() {
        final IHasStateGO froschprinz = (IHasStateGO) load(db, FROSCHPRINZ);

        if (db.counterDao()
                .incAndGet("HochwerfenAction_Wiederholung") == 1 ||
                (room.is(IM_WALD_BEIM_BRUNNEN) && !froschprinz.stateComp()
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
                            getDescription(object).akk() +
                            " in die Höhe… doch oh nein, " +
                            getDescription(object, true).nom() +
                            " fällt dir nicht in die Hände, sondern schlägt vorbei " +
                            "auf den Brunnenrand und rollt geradezu ins Wasser hinein." +
                            " Du folgst ihr mit den Augen nach, aber " +
                            getDescription(object, true).nom() +
                            " verschwindet, und der Brunnen ist tief, so tief, dass " +
                            "man keinen Grund sieht"
                            + dunkelheitNachsatz
                            + ".")
                    .beendet(PARAGRAPH));

            object.locationComp().setLocation(UNTEN_IM_BRUNNEN);
            sc.feelingsComp().setMood(UNTROESTLICH);
            return secs(10);
        }

        n.add(t(StructuralElement.SENTENCE,
                "Übermütig schleuderst du " +
                        getDescription(object).akk() +
                        " noch einmal in die Luft, aber sie wieder aufzufangen will dir " +
                        "dieses Mal nicht gelingen. " +
                        capitalize(getDescription(object, true).nom()) +
                        " landet " +
                        room.storingPlaceComp().getLocationMode().getWo()));

        object.locationComp().setLocation(room);
        if (!sc.feelingsComp().getMood().isTraurigerAls(ETWAS_GEKNICKT)) {
            sc.feelingsComp().setMood(ETWAS_GEKNICKT);
        }
        return secs(5);
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
