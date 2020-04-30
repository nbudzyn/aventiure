package de.nb.aventiure2.scaction.action.creature.reaction;

import javax.annotation.ParametersAreNonnullByDefault;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.storystate.StoryState;
import de.nb.aventiure2.data.world.base.GameObject;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.syscomp.alive.ILivingBeingGO;
import de.nb.aventiure2.data.world.syscomp.description.IDescribableGO;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.storingplace.IHasStoringPlaceGO;
import de.nb.aventiure2.data.world.syscomp.talking.ITalkerGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.StructuralElement;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.load;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

@ParametersAreNonnullByDefault
class FroschprinzReactions
        <F extends IDescribableGO & ILivingBeingGO & IHasStateGO & ILocatableGO & ITalkerGO>
        extends AbstractCreatureReactions<F> {
    private static final AvDateTime FROSCHPRINZ_LAEUFT_FRUEHESTENS_ZUM_SCHLOSSFEST_DATE_TIME =
            SCHLOSSFEST_BEGINN_DATE_TIME.minus(hours(12));

    FroschprinzReactions(final AvDatabase db) {
        super(db, FROSCHPRINZ);
    }

    @Override
    public AvTimeSpan onLeaveRoom(final IGameObject oldRoom,
                                  final StoryState currentStoryState) {
        if (getReactor().stateComp().hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && oldRoom.is(SCHLOSS_VORHALLE)
                && !oldRoom.is(SCHLOSS_VORHALLE_TISCH_BEIM_FEST)) {
            return n.add(
                    neuerSatz(" „Warte, warte“, ruft der Frosch, „nimm mich mit, ich kann nicht so "
                            + "laufen wie du.“ Aber was hilft ihm, dass er dir "
                            + "sein „Quak, quak!“ so laut nachschreit, "
                            + "als er kann, du hörst nicht darauf", noTime())
                            .undWartest());
        }

        return noTime();
    }

    @Override
    public AvTimeSpan onEnterRoom(final IHasStoringPlaceGO oldRoom,
                                  final IHasStoringPlaceGO newRoom,
                                  final StoryState currentStoryState) {
        switch (getReactor().stateComp().getState()) {
            case UNAUFFAELLIG:
                return noTime();
            // STORY Bei einem Status dazwischen könnte der Froschprinz den SC ansprechen und auf
            //  sein Versprechen hinweisen!
            case HAT_HOCHHEBEN_GEFORDERT:
                sc.feelingsComp().setMood(ANGESPANNT);

                return n.add(
                        // STORY Weitere Alternativen!
                        neuerSatz(PARAGRAPH, "Plötzlich sitzt "
                                        + getReactorDescription().nom()
                                        + " neben dir auf der Bank. „Denk an dein "
                                        + "Versprechen“, quakt er dir zu, "
                                        + "„Lass uns aus einem Tellerlein essen!“ Du bist ganz "
                                        + "erschrocken – was für eine "
                                        + "abstoßende Vorstellung!",
                                secs(30)));
            default:
                return n.add(neuerSatz("Hier sitzt "
                        + getReactorDescription().nom(), noTime()));
        }

    }

    @Override
    public AvTimeSpan onNehmen(final IHasStoringPlaceGO room,
                               final ILocatableGO taken,
                               final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    @ParametersAreNonnullByDefault
    public AvTimeSpan onEssen(final IHasStoringPlaceGO room,
                              final StoryState currentStoryState) {
        if (!room.is(SCHLOSS_VORHALLE_TISCH_BEIM_FEST)) {
            // Wenn der Spieler nicht im Schloss isst, ist es dem Frosch egal
            return noTime();
        }

        final GameObject schlossfest = load(db, SCHLOSSFEST);
        if (!(schlossfest instanceof IHasStateGO) ||
                !((IHasStateGO) schlossfest).stateComp().hasState(BEGONNEN)) {
            // Wenn der Spieler nicht auf dem Schlossfest isst, ist es dem
            // Frosch egal

            return noTime();
        }

        if (getReactor().stateComp().hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && getReactor().locationComp().hasLocation(SPIELER_CHARAKTER)) {
            return froschprinzHuepftAusTascheUndWillMitessen(room);
        }

        if (getReactor().stateComp().hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)) {
            // STORY Der Frosch könnte dahergeplatscht kommen und den Spieler ansprechen
            //  (wenn der Frosch schon - heimlich - im Schloss ist; ist Weg dauert eine Weile)
            return noTime();
        }

        if (getReactor().stateComp().hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return froschprinzHatHochhebenGefordertUndWillMitessen();
        }

        return noTime();
    }

    private AvTimeSpan froschprinzHuepftAusTascheUndWillMitessen(final IHasStoringPlaceGO room) {
        getReactor().locationComp().setLocation(room);
        getReactor().stateComp().setState(HAT_HOCHHEBEN_GEFORDERT);
        getReactor().talkingComp().setTalkingTo(sc);
        sc.feelingsComp().setMood(ANGESPANNT);

        return n.add(neuerSatz(
                "Auf einmal ruckelt es unangenehm in deiner Tasche, und eh du dich's versiehst "
                        + "hüpft der garstige Frosch heraus. Patsch! – sitzt er neben dir auf der "
                        + "Holzbank und drängt sich nass an deinen Oberschenkel. "
                        + "„Heb mich herauf!“ ruft er "
                        + "„weißt du nicht, was du zu mir gesagt bei dem kühlen "
                        + "Brunnenwasser? Heb mich herauf!“",
                secs(25))
                .beendet(PARAGRAPH));
    }

    private AvTimeSpan froschprinzHatHochhebenGefordertUndWillMitessen() {
        getReactor().talkingComp().setTalkingTo(sc);
        sc.feelingsComp().setMood(ANGESPANNT);

        return n.addAlt(
                neuerSatz(PARAGRAPH,
                        "„Heb mich auf den Tisch“, ruft der Frosch, „wie sollen wir "
                                + "zwei sonst zusammmen essen?“ Dir klopft das Herz",
                        secs(15))
                        .undWartest()
                        .dann(),
                neuerSatz("„Versprechen muss man halten!“, ruft der Frosch",
                        secs(15))
                        .beendet(PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Der Frosch lässt seine lange, schleimige Zunge vorschnellen. "
                                + "Hat er „Mitessen!“ gequakt?",
                        secs(15))
                        .beendet(PARAGRAPH));
    }

    @Override
    @ParametersAreNonnullByDefault
    public AvTimeSpan onAblegen(final IHasStoringPlaceGO room,
                                final IGameObject abgelegt,
                                final StoryState currentStoryState) {
        return noTime();
    }

    @Override
    public AvTimeSpan onHochwerfen(final IHasStoringPlaceGO room,
                                   final ILocatableGO object,
                                   final StoryState currentStoryState) {
        if (!room.is(IM_WALD_BEIM_BRUNNEN) || getReactor().stateComp()
                .hasState(UNAUFFAELLIG)) {
            return noTime();
        }

        final boolean scHatObjektAufgefangen =
                object.locationComp().hasLocation(SPIELER_CHARAKTER);

        if (getReactor().stateComp().hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                HAT_NACH_BELOHNUNG_GEFRAGT,
                HAT_FORDERUNG_GESTELLT)) {
            if (!scHatObjektAufgefangen) {
                // Der Spieler hat ein weiteres Objekt in den Brunnen fallen
                // lassen, obwohl er noch mit dem Frosch verhandelt.
                return n.add(neuerSatz(StructuralElement.PARAGRAPH,
                        "Ob der Frosch gerade seine glitschige Nase gerümpft hat?",
                        secs(3))
                        .beendet(PARAGRAPH)
                );
            }
            return noTime();
        }

        if (!object.is(GOLDENE_KUGEL)) {
            return noTime();
        }

        if (scHatObjektAufgefangen) {
            return noTime();
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.
        return n.add(neuerSatz(
                capitalize(
                        getReactorDescription(true).nom()) +
                        " schaut dich vorwurfsvoll und etwas hochnäsig an",
                secs(5)));
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now,
                                   final StoryState currentStoryState) {
        AvTimeSpan timeElapsed = noTime();

        if (!now.isBefore(FROSCHPRINZ_LAEUFT_FRUEHESTENS_ZUM_SCHLOSSFEST_DATE_TIME)
                && getReactor().stateComp()
                .hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)) {
            timeElapsed = timeElapsed.plus(
                    froschprinz_laeuft_zum_schlossfest_los());
        }

        return timeElapsed;
    }

    private AvTimeSpan froschprinz_laeuft_zum_schlossfest_los() {
        // TODO Find all equals() warnings and fix the code.

        getReactor().locationComp().unsetLocation();
        getReactor().stateComp().setState(AUF_DEM_WEG_ZUM_SCHLOSSFEST);

        // STORY Irgendwann (x Stunden danach?!) taucht der Frosch beim
        //   Spieler am Tisch im Schlossfest auf.
        //   Dazu müsste man an Creatures Zeitpunkte speichern können z.B.
        //   statusDateTime
        if (sc.hasSameLocationAs(getReactor())) {
            return n.add(neuerSatz(PARAGRAPH,
                    "Plitsch platsch, plitsch platsch hüpft der Frosch davon",
                    secs(5))
                    .beendet(PARAGRAPH));
        }

        return noTime();
    }
}
