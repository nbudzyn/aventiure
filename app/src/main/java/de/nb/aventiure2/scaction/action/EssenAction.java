package de.nb.aventiure2.scaction.action;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableList;

import java.util.Collection;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.invisible.Invisible;
import de.nb.aventiure2.data.world.player.stats.ScHunger;
import de.nb.aventiure2.data.world.room.AvRoom;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.AbstractDescription;
import de.nb.aventiure2.german.base.DuDescription;
import de.nb.aventiure2.scaction.AbstractScAction;

import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.data.storystate.StoryState.StructuralElement.SENTENCE;
import static de.nb.aventiure2.data.world.invisible.InvisibleState.BEGONNEN;
import static de.nb.aventiure2.data.world.player.stats.ScHunger.SATT;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.german.base.AllgDescription.allg;
import static de.nb.aventiure2.german.base.DuDescription.du;

/**
 * Der SC möchte etwas essen.
 */
public class EssenAction extends AbstractScAction {
    public static final String COUNTER_FELSENBIRNEN = "EssenAction_Felsenbirnen";
    private final AvRoom room;

    public static Collection<EssenAction> buildActions(
            final AvDatabase db,
            final StoryState initialStoryState, final AvRoom room) {
        final ImmutableList.Builder<EssenAction> res = ImmutableList.builder();
        if (essenMoeglich(db, initialStoryState, room)) {
            res.add(new EssenAction(db, initialStoryState, room));
        }

        return res.build();
    }

    private static boolean essenMoeglich(final AvDatabase db, final StoryState initialStoryState,
                                         final AvRoom room) {
        if (initialStoryState.lastActionWas(EssenAction.class)) {
            return false;
        }

        return raumEnthaeltEtwasEssbares(db, room);
    }

    private static boolean raumEnthaeltEtwasEssbares(final AvDatabase db, final AvRoom room) {
        if (room == AvRoom.SCHLOSS_VORHALLE &&
                db.invisibleDataDao().getInvisible(Invisible.Key.SCHLOSSFEST).getState()
                        == BEGONNEN) {
            return true;
        }

        if (room == AvRoom.WALDWILDNIS_HINTER_DEM_BRUNNEN) {
            // STORY Früchte sind im Dunkeln kaum zu sehen
            return true;
        }

        return false;
    }

    private EssenAction(final AvDatabase db,
                        final StoryState initialStoryState,
                        final AvRoom room) {
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
        switch (room) {
            case SCHLOSS_VORHALLE:
                return "Eintopf essen";
            case WALDWILDNIS_HINTER_DEM_BRUNNEN:
                return "Früchte essen";
        }

        throw new IllegalStateException("Unexpected room: " + room);
    }

    @Override
    public AvTimeSpan narrateAndDo() {
        AvTimeSpan timeElapsed;
        switch (room) {
            case SCHLOSS_VORHALLE:
                timeElapsed = narrateAndDoSchlossfest();
                break;
            case WALDWILDNIS_HINTER_DEM_BRUNNEN:
                timeElapsed = narrateAndDoFelsenbirnen();
                break;
            default:
                throw new IllegalStateException("Unexpected room: " + room);
        }

        timeElapsed = timeElapsed.plus(creatureReactionsCoordinator.onEssen(room));

        return timeElapsed;
    }

    private AvTimeSpan narrateAndDoSchlossfest() {
        final ScHunger hunger = getHunger();
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
        n.add(alt(
                t(SENTENCE,
                        "Hunger hast du zwar keinen, aber eine Kleinigkeit… – du nimmst dir "
                                + "eine halbe Kelle "
                                + "von dem Eintopf und isst")
                        .dann(),
                t(SENTENCE, "Du isst ein paar Löffel vom Eintopf")
                        .dann(),
                t(SENTENCE, "Du bist eigentlich satt, aber einen oder zwei Löffel Eintopf "
                        + "lässt du "
                        + "dir trotzdem schmecken")
                        .dann())
        );
        return mins(2);
    }

    private AvTimeSpan narrateAndDoFelsenbirnen() {
        final ScHunger hunger = getHunger();

        final AbstractDescription desc = getDescFelsenbirnen(hunger);

        final AvTimeSpan timeElapsed = narrateStartsNewWordOrSentence(desc);

        saveSatt();

        return timeElapsed;
    }

    private AbstractDescription getDescFelsenbirnen(final ScHunger hunger) {
        switch (hunger) {
            case HUNGRIG:
                return getDescFelsenbirnenHungrig();
            case SATT:
                return getDescFelsenbirnenSatt();
            default:
                throw new IllegalStateException("Unerwarteter Hunger-Wert: " + hunger);
        }
    }

    private AbstractDescription getDescFelsenbirnenHungrig() {
        if (db.counterDao().incAndGet(COUNTER_FELSENBIRNEN) == 1) {
            return du("nimmst", "eine von den Früchten, "
                            + "schaust sie kurz an, dann "
                            + "beißt du hinein… – "
                            + "Mmh! Die Frucht ist saftig und schmeckt süß wie Marzipan!\n"
                            + "Du isst dich an den Früchten satt",
                    false,
                    true,
                    true,
                    mins(10));
        }

        return alt(
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

    private AbstractDescription getDescFelsenbirnenSatt() {
        if (db.counterDao().incAndGet(COUNTER_FELSENBIRNEN) == 1) {
            return du("nimmst", "eine von den Früchten und beißt hinein. "
                            + "Sie ist überrasschend süß und saftig. Du isst die Frucht auf",
                    false,
                    true,
                    true,
                    mins(3));
        }

        return alt(
                du("hast", "nur wenig Hunger und beißt lustlos in eine der Früchte",
                        false,
                        false,
                        true,
                        mins(3)),
                allg("Die süßen Früchte lässt du dich nicht entgehen, auch wenn du kaum Hunger "
                                + "hast",
                        true,
                        true,
                        false,
                        mins(3))
        );
    }

    private AvTimeSpan narrateStartsNewWordOrSentence(final AbstractDescription desc) {
        if (initialStoryState.allowsAdditionalDuSatzreihengliedOhneSubjekt() &&
                desc instanceof DuDescription) {
            final DuDescription duDesc = (DuDescription) desc;
            n.add(t(StoryState.StructuralElement.WORD,
                    "und " +
                            duDesc.getDescriptionSatzanschlussOhneSubjekt())
                    .komma(duDesc.kommaStehtAus())
                    .dann(duDesc.dann()));
        } else if (initialStoryState.dann()) {
            n.add(t(SENTENCE,
                    desc.getDescriptionHauptsatzMitKonjunktionaladverbWennNoetig("dann"))
                    .komma(desc.kommaStehtAus())
                    .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                    .dann(false));
        } else {
            n.add(t(SENTENCE,
                    desc.getDescriptionHauptsatz())
                    .komma(desc.kommaStehtAus())
                    .undWartest(desc.allowsAdditionalDuSatzreihengliedOhneSubjekt())
                    .dann(desc.dann()));
        }

        return desc.getTimeElapsed();
    }

    private ScHunger getHunger() {
        return db.playerStatsDao().getPlayerStats().getHunger();
    }

    private void saveSatt() {
        db.playerStatsDao().setHunger(SATT);
        db.playerStatsDao().setZuletztGegessen(db.dateTimeDao().now());
    }
}
