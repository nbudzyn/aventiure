package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobjects.GameObjects;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.DescriptionComp;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IEssenReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.StateComp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.FroschprinzTalkingComp;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.StructuralElement;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjects.loadSC;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.WARTET_AUF_SC_BEIM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

public class FroschprinzReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, IEssenReactions, ITimePassedReactions {
    private static final AvTimeSpan WEGZEIT_FROSCH_BRUNNEN_ZUM_SCHLOSSFEST = hours(6);

    private static final AvDateTime FROSCH_LAEUFT_FRUEHESTENS_ZUM_SCHLOSSFEST =
            SCHLOSSFEST_BEGINN_DATE_TIME.minus(
                    WEGZEIT_FROSCH_BRUNNEN_ZUM_SCHLOSSFEST
                            // Der Frosch plant etwas Sicherheit ein
                            .plus(hours(6)));

    private final DescriptionComp descriptionComp;
    private final FroschprinzTalkingComp talkingComp;
    private final StateComp stateComp;
    private final LocationComp locationComp;

    public FroschprinzReactionsComp(final AvDatabase db,
                                    final DescriptionComp descriptionComp,
                                    final FroschprinzTalkingComp talkingComp,
                                    final StateComp stateComp,
                                    final LocationComp locationComp) {
        super(FROSCHPRINZ, db);
        this.descriptionComp = descriptionComp;
        this.talkingComp = talkingComp;
        this.stateComp = stateComp;
        this.locationComp = locationComp;
    }

    @Override
    public AvTimeSpan onLeave(final ILocatableGO locatable,
                              final ILocationGO from,
                              @Nullable final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCLeave(from, to);
        }

        return noTime();
    }

    private AvTimeSpan onSCLeave(final ILocationGO from,
                                 @Nullable final ILocationGO to) {
        if (!locationComp.hasLocation(from)) {
            // Spieler lässt den Frosch nicht zurück
            return noTime();
        }

        if (stateComp.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && !from.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            return n.add(
                    neuerSatz(" „Warte, warte“, ruft der Frosch, „nimm mich mit, "
                            + "ich kann nicht so "
                            + "laufen wie du.“ Aber was hilft ihm, dass er dir "
                            + "sein „Quak, quak!“ so laut nachschreit, "
                            + "als er kann, du hörst nicht darauf", noTime())
                            .undWartest());
        }
        // STORY Der Frosch ruft bereits bei "schlägst dich in die Wildnis hinter dem Brunnen",
        //  nicht erst nach "wie zu klein geratene Äpfel"

        return noTime();
    }

    @Override
    public AvTimeSpan onEnter(final ILocatableGO locatable,
                              @Nullable final ILocationGO from,
                              final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            return onSCEnter(from, to);
        }

        if (locatable.is(GOLDENE_KUGEL)) {
            return onGoldeneKugelEnter(from, to);
        }

        return noTime();
    }

    private AvTimeSpan onSCEnter(@Nullable final ILocationGO from,
                                 final ILocationGO to) {
        if (!locationComp.hasLocation(to)) {
            // Spieler hat nicht den Raum betreten, wo der Frosch sitzt.
            return noTime();
        }

        switch (stateComp.getState()) {
            case UNAUFFAELLIG:
                // STORY Bei einem Status dazwischen könnte der Froschprinz den SC ansprechen und auf
                //  sein Versprechen hinweisen!
            case WARTET_AUF_SC_BEIM_SCHLOSSFEST:
                return noTime();
            case HAT_HOCHHEBEN_GEFORDERT:
                loadSC(db).feelingsComp().setMood(ANGESPANNT);

                // TODO Wenn der Frosch nur rekursiv enthalten ist (Frosch sitzt auf dem Tisch),
                //  dann hier prüfen und ggf. beschreiben (vgl. AblegenAction)
                return n.addAlt(
                        neuerSatz(PARAGRAPH, "Plötzlich sitzt "
                                        + getFroschprinzDescription().nom()
                                        + " neben dir auf der Bank. „Denk an dein "
                                        + "Versprechen“, quakt er dir zu, "
                                        + "„Lass uns aus einem Tellerlein essen!“ Du bist ganz "
                                        + "erschrocken – was für eine "
                                        + "abstoßende Vorstellung!",
                                secs(30)),
                        neuerSatz("Da stößt es schon von der Seite an dein Bein. "
                                        + "Du drehst dich "
                                        + "hastig weg und dein Herz klopft vor schlechtem "
                                        + "Gewissen, als "
                                        + "der Frosch „Heb mich herauf, heb mich herauf!“ quakt",
                                secs(20))
                                .beendet(PARAGRAPH));
            default:
                // TODO Wenn der Frosch nur rekursiv enthalten ist (Frosch sitzt auf dem Tisch),
                //  dann beschreiben (vgl. AblegenAction)

                return n.add(neuerSatz("Hier sitzt "
                        + getFroschprinzDescription().nom(), noTime()));
        }
    }

    private AvTimeSpan onGoldeneKugelEnter(@Nullable final ILocationGO from,
                                           final ILocationGO to) {
        if (from == null || !from.is(SPIELER_CHARAKTER)) {
            return noTime();
        }

        // Die goldene Kugel hat sich vom SC her irgendwohin bewegt.
        if (!to.is(UNTEN_IM_BRUNNEN)) {
            return noTime();
        }

        // Der SC hat die goldene Kugel hochgeworfen und in den Brunnen fallen lassen.

        if (!to.is(IM_WALD_BEIM_BRUNNEN) || stateComp.hasState(UNAUFFAELLIG)) {
            return noTime();
        }

        if (stateComp.hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                HAT_NACH_BELOHNUNG_GEFRAGT,
                HAT_FORDERUNG_GESTELLT)) {
            // Der Spieler hat die goldene Kugel in den Brunnen fallen
            // lassen, obwohl er noch mit dem Frosch verhandelt.
            return n.add(neuerSatz(StructuralElement.PARAGRAPH,
                    "Ob der Frosch gerade seine glitschige Nase gerümpft hat?",
                    secs(3))
                    .beendet(PARAGRAPH)
            );
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.
        return n.add(neuerSatz(
                capitalize(
                        getFroschprinzDescription(true).

                                nom()) +
                        " schaut dich vorwurfsvoll und etwas hochnäsig an",
                secs(5)));
    }

    @Override
    public AvTimeSpan onEssen(final IGameObject gameObject) {
        if (!gameObject.is(SPIELER_CHARAKTER)) {
            // Wenn nicht der Spieler ist, ist es dem Frosch egal
            return noTime();
        }

        final SpielerCharakter sc = (SpielerCharakter) gameObject;

        if (!sc.locationComp().hasLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            // Wenn der Spieler nicht im Schloss isst, ist es dem Frosch egal
            return noTime();
        }

        if (!schlossfestHatBegonnen()) {
            // Wenn der Spieler nicht auf dem Schlossfest isst, ist es dem
            // Frosch egal

            return noTime();
        }

        if (stateComp.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && locationComp.hasLocation(SPIELER_CHARAKTER)) {
            return froschprinzHuepftAusTascheUndWillMitessen(sc);
        }

        if (stateComp.hasState(WARTET_AUF_SC_BEIM_SCHLOSSFEST)) {
            return froschprinzSitztAufEinmalAufDerBankUndWillMitessen(sc);
        }

        if (stateComp.hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return froschprinzHatHochhebenGefordertUndWillMitessen(sc);
        }

        return noTime();
    }

    // STORY  "Auf einmal spürst du etwas Feuchtes an deinem rechten Bein – oh nein, der garstige Frosch! „Heb mich herauf!“ ruft er „weißt du nicht, was du zu mir gesagt bei dem kühlen Brunnenwasser? Heb mich herauf!“"

    private AvTimeSpan froschprinzHuepftAusTascheUndWillMitessen(
            final SpielerCharakter sc) {
        return locationComp.narrateAndSetLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
                () -> {
                    final AvTimeSpan timeSpan = n.add(neuerSatz(
                            "Auf einmal ruckelt es unangenehm in deiner Tasche, und eh "
                                    + "du dich's versiehst "
                                    + "hüpft der garstige Frosch heraus. Patsch! – sitzt er neben "
                                    + "dir auf der "
                                    + "Holzbank und drängt sich nass an deinen Oberschenkel. "
                                    + "„Heb mich herauf!“ ruft er "
                                    + "„weißt du nicht, was du zu mir gesagt bei dem kühlen "
                                    + "Brunnenwasser? Heb mich herauf!“",
                            secs(25))
                            .beendet(PARAGRAPH));

                    setFroschHatHochhebenGefordert(sc);

                    return timeSpan;
                }
        );
    }

    private AvTimeSpan froschprinzSitztAufEinmalAufDerBankUndWillMitessen(
            final SpielerCharakter sc) {
        final AvTimeSpan timeSpan = n.add(du(
                "spürst", "auf einmal etwas Feuchtes an deinem rechten Bein – oh "
                        + "nein, der "
                        + "garstige Frosch! „Heb mich herauf!“ ruft er „weißt du nicht, was du "
                        + "zu mir gesagt bei dem kühlen Brunnenwasser? Heb mich herauf!“",
                "auf einmal",
                secs(20))
                .beendet(PARAGRAPH));

        setFroschHatHochhebenGefordert(sc);

        return timeSpan;
    }

    private void setFroschHatHochhebenGefordert(final SpielerCharakter sc) {
        talkingComp.setTalkingTo(sc);
        sc.feelingsComp().setMood(ANGESPANNT);
        stateComp.setState(HAT_HOCHHEBEN_GEFORDERT);
    }

    private AvTimeSpan froschprinzHatHochhebenGefordertUndWillMitessen(
            final SpielerCharakter sc) {
        talkingComp.setTalkingTo(sc);
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
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        switch (stateComp.getState()) {
            case ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS:
                if (now.isEqualOrAfter(FROSCH_LAEUFT_FRUEHESTENS_ZUM_SCHLOSSFEST)) {
                    return froschprinzLaueftZumSchlossfestLos();
                }
                return noTime();
            case AUF_DEM_WEG_ZUM_SCHLOSSFEST:
                if (schlossfestHatBegonnen() &&
                        now.isEqualOrAfter(
                                stateComp.getStateDateTime() // Als der Frosch losgelaufen ist
                                        .plus(WEGZEIT_FROSCH_BRUNNEN_ZUM_SCHLOSSFEST))) {
                    return froschprinzAufSchlossfestAngekommen();
                }
                return noTime();
        }

        return noTime();
    }

    private AvTimeSpan froschprinzLaueftZumSchlossfestLos() {
        // TODO Find all equals() warnings and fix the code.

        AvTimeSpan timeElapsed = noTime();

        @Nullable final ILocationGO scLocation = loadSC(db).locationComp().getLocation();

        if ((scLocation != null && locationComp.hasLocation(scLocation)) ||
                locationComp.hasLocation(SPIELER_CHARAKTER)) {
            timeElapsed = timeElapsed.plus(n.add(neuerSatz(PARAGRAPH,
                    "Plitsch platsch, plitsch platsch hüpft der Frosch davon",
                    secs(5))
                    .beendet(PARAGRAPH)));
        }

        timeElapsed = timeElapsed.plus(locationComp.narrateAndUnsetLocation());

        stateComp.setState(AUF_DEM_WEG_ZUM_SCHLOSSFEST);

        return timeElapsed;
    }

    private AvTimeSpan froschprinzAufSchlossfestAngekommen() {
        return locationComp.narrateAndSetLocation(
                SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
                () -> {
                    stateComp.setState(WARTET_AUF_SC_BEIM_SCHLOSSFEST);
                    return noTime();
                }
        );
    }

    /**
     * Gibt eine Nominalphrase zurück, die den Froschprinzen beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler den Froschprinzen schon kennt oder nicht.
     */
    private Nominalphrase getFroschprinzDescription() {
        return getFroschprinzDescription(false);
    }

    /**
     * Gibt eine Nominalphrase zurück, die den Froschprinzen beschreibt.
     * Die Phrase kann unterschiedlich sein, je nachdem,
     * ob der Spieler den Froschprinzen schon kennt oder nicht.
     *
     * @param shortIfKnown <i>Falls der Spieler(-charakter)</i> den
     *                     Froschprinzen schon kennt, wird eher eine
     *                     kürzere Beschreibung gewählt
     */
    private Nominalphrase getFroschprinzDescription(final boolean shortIfKnown) {
        return descriptionComp.getDescription(
                loadSC(db).memoryComp().isKnown(FROSCHPRINZ), shortIfKnown);
    }

    private boolean schlossfestHatBegonnen() {
        return ((IHasStateGO) GameObjects.load(db, SCHLOSSFEST))
                .stateComp().hasState(BEGONNEN);
    }
}
