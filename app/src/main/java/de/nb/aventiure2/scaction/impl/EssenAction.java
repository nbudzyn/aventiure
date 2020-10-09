package de.nb.aventiure2.scaction.impl;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.Hunger;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.SATT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ZUFRIEDEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_IN_VORHALLE;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;

/**
 * Der SC möchte etwas essen.
 */
public class EssenAction extends AbstractScAction {
    private static final String COUNTER_FELSENBIRNEN = "EssenAction_Felsenbirnen";
    private final ILocationGO location;

    public static Collection<EssenAction> buildActions(
            final AvDatabase db,
            final World world,
            final ILocationGO location) {
        final ImmutableList.Builder<EssenAction> res = ImmutableList.builder();
        if (essenMoeglich(db, world, location)) {
            res.add(new EssenAction(db, world, location));
        }

        return res.build();
    }

    private static <F extends ILocatableGO & IHasStateGO<FroschprinzState>>
    boolean essenMoeglich(final AvDatabase db,
                          final World world,
                          final ILocationGO location) {
        if (world.loadSC().memoryComp().getLastAction().is(Action.Type.ESSEN)) {
            // TODO Es könnten sich verschiedene essbare Dinge am selben Ort befinden!
            //  Das zweite sollte man durchaus essen können, wenn man schon das
            //  erste gegessen hat!
            return false;
        }

        final F froschprinz = (F) world.load(FROSCHPRINZ);
        if (location.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                froschprinz.locationComp().hasRecursiveLocation(SPIELER_CHARAKTER) &&
                froschprinz.stateComp().hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            // SC hat gerade den Frosch in der Hand
            return false;
        }

        return locationEnthaeltEtwasEssbares(world, location);
    }

    private static boolean locationEnthaeltEtwasEssbares(final World world,
                                                         final ILocationGO location) {
        if (location.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                ((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST)).stateComp()
                        .hasState(BEGONNEN)) {
            return true;
        }

        if (location.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            // STORY Früchte sind im Dunkeln kaum zu sehen, selbst wenn man den Weg
            //  schon kennt
            return true;
        }

        return false;
    }

    private EssenAction(final AvDatabase db,
                        final World world,
                        final ILocationGO location) {
        super(db, world);
        this.location = location;
    }

    @Override
    public String getType() {
        return "actionEssen";
    }

    @Override
    @NonNull
    public String getName() {
        if (location.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            return "Eintopf essen";
        }

        if (location.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            return "Früchte essen";
        }

        throw new IllegalStateException("Unexpected location: " + location);
    }

    @Override
    public void narrateAndDo() {
        if (location.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            narrateAndDoSchlossfest();
        } else if (location.is(WALDWILDNIS_HINTER_DEM_BRUNNEN)) {
            narrateAndDoFelsenbirnen();
        } else {
            throw new IllegalStateException("Unexpected location: " + location);
        }

        //    STORY Essen sollte stärkere Auswirkungen haben. Märchen mit Essen?

        sc.memoryComp().setLastAction(buildMemorizedAction());

        world.narrateAndDoReactions().onEssen(sc);
    }

    private <F extends ILocatableGO & IHasStateGO<FroschprinzState>>
    void narrateAndDoSchlossfest() {
        final F froschprinz = (F) world.load(FROSCHPRINZ);
        if (froschprinz.locationComp()
                .hasRecursiveLocation(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST) &&
                froschprinz.stateComp().hasState(BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN)) {
            narrateAndDoSchlossfestEssenMitFrosch(froschprinz);
            return;
        }
        final Hunger hunger = getHunger();

        switch (hunger) {
            case HUNGRIG:
                narrateAndDoSchlossfestHungrig();
                return;
            case SATT:
                narrateAndDoSchlossfestSatt();
                return;
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private <F extends ILocatableGO & IHasStateGO<FroschprinzState>>
    void narrateAndDoSchlossfestEssenMitFrosch(final F froschprinz) {
        n.narrate(neuerSatz(PARAGRAPH,
                "Was hatte deine Großmutter immer gesagt? „Wer dir geholfen in der "
                        + "Not, den sollst du hernach nicht verachten.” Du füllst deine Schale "
                        + "neu mit Eintopf, steckst deinen Holzlöffel hinein... aber was ist das? "
                        + "Auch ein goldener Löffel fährt mit in die Schale. Du schaust "
                        + "verwirrt auf - kein Frosch mehr auf dem Tisch, doch neben dir auf der "
                        + "Bank sitzt ein junger Mann mit schönen freundlichen Augen. In Samt und "
                        + "Seide ist er gekleidet, mit goldenen Ketten um den Hals",
                secs(10)));
        n.narrate(neuerSatz(PARAGRAPH,
                "Er schaut an sich herab – „Ihr habt mich erlöst”, sagt er, „ich "
                        + "danke euch!” Eine böse Hexe "
                        + "habe ihn verwünscht. „Ich werde euch nicht vergessen!”",
                secs(10)));
        n.narrate(neuerSatz(PARAGRAPH,
                "Am Tisch um euch herum entsteht Aufregung. Der junge Mann erhebt "
                        + "sich und schickt sich an, die Halle zu verlassen",
                secs(10)));

        world.loadSC().feelingsComp().setMoodMin(ZUFRIEDEN);
        froschprinz.stateComp().narrateAndSetState(ZURUECKVERWANDELT_IN_VORHALLE);
        froschprinz.locationComp().narrateAndSetLocation(SCHLOSS_VORHALLE);
    }

    private void narrateAndDoSchlossfestHungrig() {
        saveSatt();

        world.loadSC().feelingsComp().setMoodMin(ZUFRIEDEN);

        n.narrateAlt(
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

    private void narrateAndDoSchlossfestSatt() {
        n.narrateAlt(
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

    private void narrateAndDoFelsenbirnen() {
        final Hunger hunger = getHunger();

        narrateFelsenbirnen(hunger);

        saveSatt();
    }

    private void narrateFelsenbirnen(final Hunger hunger) {
        switch (hunger) {
            case HUNGRIG:
                narrateFelsenbirnenHungrig();
                return;
            case SATT:
                narrateFelsenbirnenSatt();
                return;
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private void narrateFelsenbirnenHungrig() {
        if (db.counterDao().incAndGet(COUNTER_FELSENBIRNEN) == 1) {
            n.narrate(du(SENTENCE, "nimmst", "eine von den Früchten, "
                    + "schaust sie kurz an, dann "
                    + "beißt du hinein… – "
                    + "Mmh! Die Frucht ist saftig und schmeckt süß wie Marzipan!\n"
                    + "Du isst dich an den Früchten satt", mins(10))
                    .undWartest()
                    .dann());
            return;
        }

        n.narrateAlt(
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

    private void narrateFelsenbirnenSatt() {
        if (db.counterDao().incAndGet(COUNTER_FELSENBIRNEN) == 1) {
            n.narrate(
                    du(SENTENCE, "nimmst",
                            "eine von den Früchten und beißt hinein. "
                                    + "Sie ist überraschend süß und saftig. Du isst die Frucht auf",
                            mins(3))
                            .undWartest()
                            .dann());
            return;
        }

        n.narrateAlt(
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

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        if (n.lastNarrationWasFromReaction()) {
            return false;
        }

        // Höchstens, wenn man sich gerade zuvor an etwas satt gegessen hat?
        return false;
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.ESSEN);
    }

    private void saveSatt() {
        sc.feelingsComp().setHunger(SATT);
        // TODO NOW auch zu einem GameObject machen mit einer entsprechenden Stateful Component??
        // TODO Regel aufstellen: Die Aktionen dürfen nicht auf die DAOs zugreifen.
        //  Z.B. von DB nur ein Interface definieren, das durchgereicht wird?
        //  Oder alles über die world machen?
        sc.feelingsComp().setZuletztGegessen(db.nowDao().now());
    }
}
