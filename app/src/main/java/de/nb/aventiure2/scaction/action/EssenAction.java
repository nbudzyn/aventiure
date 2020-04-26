package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.AllgDescription.allg;
import static de.nb.aventiure2.german.base.DuDescription.du;

/**
 * Der SC möchte etwas essen.
 */
public class EssenAction extends AbstractScAction {
    public static final String COUNTER_FELSENBIRNEN = "EssenAction_Felsenbirnen";
    private final IHasStoringPlaceGO room;

    public static Collection<EssenAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState, final IHasStoringPlaceGO room) {
        final ImmutableList.Builder<EssenAction> res = ImmutableList.builder();
        if (essenMoeglich(db, initialStoryState, room)) {
            res.add(new EssenAction(db, initialStoryState, room));
        }

        return res.build();
    }

    private static boolean essenMoeglich(final AvDatabase db, final StoryState initialStoryState,
                                         final IHasStoringPlaceGO room) {
        if (loadSC(db).memoryComp().getLastAction().is(Action.Type.ESSEN)) {
            // TODO Es könnten sich verschiedene essbare Dinge am selben Ort befinden!
            //  Das zweite sollte man durchaus essen können, wenn man schon das
            //  erste gegessen hat!
            return false;
        }

        return raumEnthaeltEtwasEssbares(db, room);
    }

    private static boolean raumEnthaeltEtwasEssbares(final AvDatabase db,
                                                     final IHasStoringPlaceGO room) {
        if (room.is(SCHLOSS_VORHALLE_TISCH_BEIM_FEST) &&
                ((IHasStateGO) load(db, SCHLOSSFEST)).stateComp().hasState(BEGONNEN)) {
            return true;
        }

        if (room.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            // STORY Früchte sind im Dunkeln kaum zu sehen, selbst wenn man den Weg
            // schon kennt
            return true;
        }

        return false;
    }

    private EssenAction(final AvDatabase db,
                        final StoryState initialStoryState,
                        final IHasStoringPlaceGO room) {
        super(db, initialStoryState);
        this.room = room;
    }

    @Override
    public String getType() {
        return "actionEssen";
    }

    @Override
    @NonNull
    public String getName() {
        if (room.is(SCHLOSS_VORHALLE_TISCH_BEIM_FEST)) {
            return "Eintopf essen";
        }

        if (room.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            return "Früchte essen";
        }

        throw new IllegalStateException("Unexpected room: " + room);
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        AvTimeSpan timeElapsed;
        if (room.is(SCHLOSS_VORHALLE_TISCH_BEIM_FEST)) {
            timeElapsed = narrateAndDoSchlossfest();
        } else if (room.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            timeElapsed = narrateAndDoFelsenbirnen();
        } else {
            throw new IllegalStateException("Unexpected room: " + room);
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());

        timeElapsed = timeElapsed.plus(creatureReactionsCoordinator.onEssen(room));

        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoSchlossfest() {
        final Hunger hunger = getHunger();
        switch (hunger) {
            case HUNGRIG:
                return narrateAndDoSchlossfestHungrig();
            case SATT:
                return narrateAndDoSchlossfestSatt();
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private AvTimeSpan narrateAndDoSchlossfestHungrig() {
        n.add(alt(
                t(SENTENCE,
                        "Du füllst dir von dem Eintopf ein und langst kräftig zu")
                        .beendet(PARAGRAPH),
                t(SENTENCE, "Du nimmst dir eine gute Kelle von dem Eintopf und isst")
                        .dann(),
                t(SENTENCE, "Du nimmst dir vom Eintopf und isst, als wenn du vier Wochen "
                        + "hungern solltest")
                        .dann(),
                t(SENTENCE, "Du greifst zu und isst mit Lust, bis du deinen Hunger gestillt "
                        + "hast")
                        .komma()
                        .dann(),
                t(SENTENCE, "Du isst vom Eintopf, bis dein Hunger gestillt ist")
                        .komma()
                        .undWartest()
                        .dann(),
                t(SENTENCE, "Du bedienst dich am Eintopf und löffelst los")
                        .beendet(PARAGRAPH)
                        .dann(),
                t(SENTENCE, "Du isst vom Eintopf und stillst deinen Hunger")
                        .dann()
        ));

        saveSatt();

        return mins(10);
    }

    private AvTimeSpan narrateAndDoSchlossfestSatt() {
        return n.addAlt(
                allg("Hunger hast du zwar keinen mehr, aber eine Kleinigkeit… – du "
                                + "nimmst dir "
                                + "eine halbe Kelle "
                                + "von dem Eintopf und isst",
                        false,
                        false,
                        true,
                        mins(2)),
                du("isst ein paar Löffel vom Eintopf",
                        false,
                        true,
                        true,
                        mins(2)),
                du("bist", "eigentlich satt, aber einen oder zwei Löffel Eintopf "
                                + "lässt du "
                                + "dir trotzdem schmecken",
                        "eigentlich",
                        false,
                        false,
                        true,
                        mins(2)));
    }

    private AvTimeSpan narrateAndDoFelsenbirnen() {
        final Hunger hunger = getHunger();

        final Collection<AbstractDescription> descAlternatives = getDescFelsenbirnen(hunger);

        final AvTimeSpan timeElapsed = narrateStartsNewWordOrSentence(descAlternatives);

        saveSatt();

        return timeElapsed;
    }

    private Collection<AbstractDescription> getDescFelsenbirnen(final Hunger hunger) {
        switch (hunger) {
            case HUNGRIG:
                return getDescFelsenbirnenHungrig();
            case SATT:
                return getDescFelsenbirnenSatt();
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private Collection<AbstractDescription> getDescFelsenbirnenHungrig() {
        if (db.counterDao().incAndGet(COUNTER_FELSENBIRNEN) == 1) {
            return ImmutableList.of(
                    du("nimmst", "eine von den Früchten, "
                                    + "schaust sie kurz an, dann "
                                    + "beißt du hinein… – "
                                    + "Mmh! Die Frucht ist saftig und schmeckt süß wie Marzipan!\n"
                                    + "Du isst dich an den Früchten satt",
                            false,
                            true,
                            true,
                            mins(10)));
        }

        return ImmutableList.of(
                du("isst", "dich an den süßen Früchten satt",
                        false,
                        true,
                        true,
                        mins(10)),
                du("bedienst", "dich an den süßen Früchten, als ob es kein Morgen "
                                + "gäbe. Schließlich bist du vollkommen satt",
                        false,
                        true,
                        false,
                        mins(10)),
                du("isst", "so lange von den Früchten, bis dein Hunger gestillt "
                                + "ist",
                        false,
                        false,
                        true,
                        mins(10))
        );
    }

    private Collection<AbstractDescription> getDescFelsenbirnenSatt() {
        if (db.counterDao().incAndGet(COUNTER_FELSENBIRNEN) == 1) {
            return ImmutableList.of(
                    du("nimmst", "eine von den Früchten und beißt hinein. "
                                    + "Sie ist überrasschend süß und saftig. Du isst die Frucht auf",
                            false,
                            true,
                            true,
                            mins(3)));
        }

        return ImmutableList.of(
                du("hast", "nur wenig Hunger und beißt lustlos in eine der Früchte",
                        "Hunger",
                        false,
                        false,
                        true,
                        mins(3)),
                du("lässt", "dir die süßen Früchte nicht entgehen, auch wenn du kaum Hunger "
                                + "hast",
                        "die süßen Früchte",
                        true,
                        true,
                        false,
                        mins(3))
        );
    }

    private AvTimeSpan narrateStartsNewWordOrSentence(final Collection<AbstractDescription>
                                                              descAlternatives) {
        return n.addAlt(descAlternatives);
    }

    private Hunger getHunger() {
        return sc.feelingsComp().getHunger();
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        return buildMemorizedAction().equals(sc.memoryComp().getLastAction());
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.ESSEN, (IGameObject) null);
    }

    private void saveSatt() {
        sc.feelingsComp().setHunger(SATT);
        // TODO NOW auch zu einem GameObject machen mit einer entsprechenden Stateful Component
        // TODO Regel aufstellen: Die Aktionen dürfen nicht auf die DAOs zugreifen.
        //  Z.B. von DB nur ein Interface definieren, das durchgereicht wird?!
        sc.feelingsComp().setZuletztGegessen(db.dateTimeDao().now());
    }
}
