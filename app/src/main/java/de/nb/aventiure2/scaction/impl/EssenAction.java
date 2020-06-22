package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.WALDWILDNIS_HINTER_DEM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * Der SC möchte etwas essen.
 */
public class EssenAction extends AbstractScAction {
    private static final String COUNTER_FELSENBIRNEN = "EssenAction_Felsenbirnen";
    private final ILocationGO room;

    public static Collection<EssenAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState, final ILocationGO room) {
        final ImmutableList.Builder<EssenAction> res = ImmutableList.builder();
        if (essenMoeglich(db, room)) {
            res.add(new EssenAction(db, initialStoryState, room));
        }

        return res.build();
    }

    private static <LOC_STAT extends ILocatableGO & IHasStateGO>
    boolean essenMoeglich(final AvDatabase db,
                          final ILocationGO room) {
        if (loadSC(db).memoryComp().getLastAction().is(Action.Type.ESSEN)) {
            // TODO Es könnten sich verschiedene essbare Dinge am selben Ort befinden!
            //  Das zweite sollte man durchaus essen können, wenn man schon das
            //  erste gegessen hat!
            return false;
        }

        final LOC_STAT froschprinz = (LOC_STAT) load(db, FROSCHPRINZ);
        if (room.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                froschprinz.locationComp().hasRecursiveLocation(SPIELER_CHARAKTER) &&
                froschprinz.stateComp().hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            // SC hat gerade den Frosch in der Hand
            return false;
        }

        return raumEnthaeltEtwasEssbares(db, room);
    }

    private static boolean raumEnthaeltEtwasEssbares(final AvDatabase db,
                                                     final ILocationGO room) {
        if (room.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
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
                        final ILocationGO room) {
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
        if (room.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
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
        if (room.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            timeElapsed = narrateAndDoSchlossfest();
        } else if (room.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            timeElapsed = narrateAndDoFelsenbirnen();
        } else {
            throw new IllegalStateException("Unexpected room: " + room);
        }

        sc.memoryComp().setLastAction(buildMemorizedAction());

        timeElapsed = timeElapsed.plus(GameObjects.narrateAndDoReactions()
                .onEssen(sc));

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
        saveSatt();

        // TODO Danach passt "Aus Langeweile wirfst du..." nicht mehr gut.
        //  Laune verbessern zu "Aufgedreht" oder so?!

        return n.addAlt(
                du(PARAGRAPH,
                        "füllst", "dir von dem Eintopf ein und langst kräftig zu",
                        mins(10))
                        .beendet(PARAGRAPH),
                du(PARAGRAPH, "nimmst", "dir eine gute Kelle von dem Eintopf und isst",
                        mins(10))
                        .dann(),
                du(PARAGRAPH, "nimmst", "dir vom Eintopf und isst, als wenn du vier Wochen "
                                + "hungern solltest",
                        mins(10))
                        .komma()
                        .dann(),
                du(PARAGRAPH, "greifst", "zu und isst mit Lust, bis du "
                                + "deinen Hunger gestillt hast",
                        mins(10))
                        .komma()
                        .dann(),
                du(PARAGRAPH, "isst", "vom Eintopf, bis dein Hunger gestillt ist",
                        mins(10))
                        .komma()
                        .undWartest()
                        .dann(),
                du(PARAGRAPH, "bedienst", "dich am Eintopf und löffelst los",
                        mins(10))
                        .beendet(PARAGRAPH)
                        .dann(),
                du(PARAGRAPH, "isst", "vom Eintopf und stillst deinen Hunger",
                        mins(10))
                        .dann()
        );
    }

    private AvTimeSpan narrateAndDoSchlossfestSatt() {
        return n.addAlt(
                neuerSatz("Hunger hast du zwar keinen mehr, aber eine Kleinigkeit… – du "
                        + "nimmst dir "
                        + "eine halbe Kelle "
                        + "von dem Eintopf und isst", mins(2))
                        .dann(),
                du("isst", "ein paar Löffel vom Eintopf", mins(2))
                        .undWartest()
                        .dann(),
                du(SENTENCE, "bist", "eigentlich satt, aber einen oder zwei Löffel Eintopf "
                        + "lässt du "
                        + "dir trotzdem schmecken", "eigentlich", mins(2))
                        .dann());
    }

    private AvTimeSpan narrateAndDoFelsenbirnen() {
        final Hunger hunger = getHunger();

        final AvTimeSpan timeElapsed = narrateFelsenbirnen(hunger);

        saveSatt();

        return timeElapsed;
    }

    private AvTimeSpan narrateFelsenbirnen(final Hunger hunger) {
        switch (hunger) {
            case HUNGRIG:
                return narrateFelsenbirnenHungrig();
            case SATT:
                return narrateFelsenbirnenSatt();
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private AvTimeSpan narrateFelsenbirnenHungrig() {
        if (db.counterDao().incAndGet(COUNTER_FELSENBIRNEN) == 1) {
            return n.add(du(SENTENCE, "nimmst", "eine von den Früchten, "
                    + "schaust sie kurz an, dann "
                    + "beißt du hinein… – "
                    + "Mmh! Die Frucht ist saftig und schmeckt süß wie Marzipan!\n"
                    + "Du isst dich an den Früchten satt", mins(10))
                    .undWartest()
                    .dann());
        }

        return n.addAlt(
                du("isst", "dich an den süßen Früchten satt", mins(10))
                        .undWartest()
                        .dann(),
                du("bedienst", "dich an den süßen Früchten, als ob es kein Morgen "
                        + "gäbe. Schließlich bist du vollkommen satt", mins(10))
                        .undWartest()
                        .dann(),
                du("isst", "so lange von den Früchten, bis dein Hunger gestillt "
                        + "ist", mins(10))
                        .dann()
        );
    }

    private AvTimeSpan narrateFelsenbirnenSatt() {
        if (db.counterDao().incAndGet(COUNTER_FELSENBIRNEN) == 1) {
            return n.add(
                    du("nimmst", "eine von den Früchten und beißt hinein. "
                                    + "Sie ist überraschend süß und saftig. Du isst die Frucht auf",
                            mins(3))
                            .undWartest()
                            .dann());
        }

        return n.addAlt(
                du(SENTENCE, "hast", "nur wenig Hunger und beißt lustlos in eine der Früchte",
                        "Hunger",
                        mins(3))
                        .dann(),
                // TODO Das mit dem "entgehen lassen" macht keinen Sinn, wenn man sich gerade erst
                //  satt gegessen hat. Umformulieren? Nur beim ersten Mal, wenn man
                //  außerdem satt ist?
                du(SENTENCE, "lässt",
                        "dir die süßen Früchte nicht entgehen, auch wenn du kaum Hunger "
                                + "hast", "die süßen Früchte", mins(3))
                        .komma()
                        .undWartest()
                        .dann()
        );
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
        return new Action(Action.Type.ESSEN);
    }

    private void saveSatt() {
        sc.feelingsComp().setHunger(SATT);
        // TODO NOW auch zu einem GameObject machen mit einer entsprechenden Stateful Component
        // TODO Regel aufstellen: Die Aktionen dürfen nicht auf die DAOs zugreifen.
        //  Z.B. von DB nur ein Interface definieren, das durchgereicht wird?!
        sc.feelingsComp().setZuletztGegessen(db.nowDao().now());
    }
}
