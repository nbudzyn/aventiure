package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobjects.GameObjectService;
import de.nb.aventiure2.data.world.gameobjects.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.description.impl.FroschprinzDescriptionComp;
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
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobjects.GameObjectService.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.AUF_DEM_WEG_ZUM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.WARTET_AUF_SC_BEIM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.GameObjectState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.hours;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.AllgDescription.satzanschluss;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.GermanUtil.capitalize;
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

public class FroschprinzReactionsComp
        extends AbstractReactionsComp
        implements IMovementReactions, IEssenReactions, ITimePassedReactions {
    private static final AvTimeSpan WEGZEIT_FROSCH_BRUNNEN_ZUM_SCHLOSSFEST = hours(6);
    private static final AvTimeSpan WEGZEIT_PRINZ_TISCH_DURCH_VORHALLE = mins(1);
    private static final AvTimeSpan ZEIT_FUER_ABFAHRT_PRINZ_MIT_WAGEN = mins(10);

    private static final AvDateTime FROSCH_LAEUFT_FRUEHESTENS_ZUM_SCHLOSSFEST =
            SCHLOSSFEST_BEGINN_DATE_TIME.minus(
                    WEGZEIT_FROSCH_BRUNNEN_ZUM_SCHLOSSFEST
                            // Der Frosch plant etwas Sicherheit ein
                            .plus(hours(6)));

    private final FroschprinzDescriptionComp descriptionComp;
    private final FroschprinzTalkingComp talkingComp;
    private final StateComp stateComp;
    private final LocationComp locationComp;

    public FroschprinzReactionsComp(final AvDatabase db,
                                    final GameObjectService gos,
                                    final FroschprinzDescriptionComp descriptionComp,
                                    final FroschprinzTalkingComp talkingComp,
                                    final StateComp stateComp,
                                    final LocationComp locationComp) {
        super(FROSCHPRINZ, db, gos);
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
        if (locationComp.hasRecursiveLocation(SPIELER_CHARAKTER) ||
                !locationComp.hasRecursiveLocation(from)) {
            // Spieler lässt den Frosch nicht zurück

            if (locationComp.hasRecursiveLocation(SPIELER_CHARAKTER)) {
                // Spieler nimmt den Frosch mit
                return onSCLeaveMitFroschprinz(from, to);
            }

            return noTime();
        }

        if (stateComp.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && !from.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            return n.add(
                    neuerSatz("„Warte, warte“, ruft der Frosch, „nimm mich mit, "
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

    private AvTimeSpan onSCLeaveMitFroschprinz(final ILocationGO from,
                                               @Nullable final ILocationGO to) {
        if (!from.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            return noTime();
        }

        if (!schlossfestHatBegonnen()) {
            return noTime();
        }

        if (!stateComp.hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return noTime();
        }

        final AvTimeSpan timeElapsed =
                locationComp.narrateAndSetLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST);

        return timeElapsed.plus(n.add(
                neuerSatz("Da springt dir der Frosch "
                        + "aus der Hand – weg ist er!", secs(3))
                        .beendet(PARAGRAPH)));
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

        if (locatable.is(FROSCHPRINZ)) {
            return onFroschprinzEnter(from, to);
        }

        return noTime();
    }


    private AvTimeSpan onSCEnter(@Nullable final ILocationGO from,
                                 final ILocationGO to) {
        if (locationComp.hasRecursiveLocation(SPIELER_CHARAKTER) ||
                !locationComp.hasRecursiveLocation(to)) {
            // Spieler hat nicht den Raum betreten, in dem sich der Froschprinz befindet
            return noTime();
        }

        final Nominalphrase froschprinzDesc = getFroschprinzDescription();
        switch (stateComp.getState()) {
            case UNAUFFAELLIG:
                // STORY Bei einem Status dazwischen könnte der Froschprinz den SC ansprechen und auf
                //  sein Versprechen hinweisen!
            case WARTET_AUF_SC_BEIM_SCHLOSSFEST:
                return noTime();
            case HAT_HOCHHEBEN_GEFORDERT:
                loadSC().feelingsComp().setMood(ANGESPANNT);

                // TODO Wenn der Frosch nur rekursiv enthalten ist (Frosch sitzt auf dem Tisch),
                //  dann hier prüfen und ggf. beschreiben (vgl. AblegenAction)
                return n.addAlt(
                        neuerSatz(PARAGRAPH, "Plötzlich sitzt "
                                        + froschprinzDesc.nom()
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
            case BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN:
                loadSC().feelingsComp().setMood(ANGESPANNT);

                return n.addAlt(
                        neuerSatz(PARAGRAPH, "Auf einmal sitzt "
                                        + froschprinzDesc.nom()
                                        + " bei dir auf dem Tisch. „Auf, füll deine "
                                        + "Schale, wir wollen zusammen essen“, quakt " +
                                        froschprinzDesc.persPron().nom() +
                                        " dich an. Es schauert dich bei dem Gedanken",
                                secs(10))
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH, "Platsch – da springt auf einmal "
                                        + froschprinzDesc.nom()
                                        + " vor dich auf den Tisch. Gerade noch, dass er "
                                        + "dir nicht in die Essensschale gehüpft ist. Dir läuft "
                                        + "ein Schauer über den Rücken, als "
                                        + froschprinzDesc.persPron().nom()
                                        + " fordert: „Nicht länger gezögert – nun lass uns zusammen "
                                        + "essen!“",
                                secs(10))
                                .beendet(PARAGRAPH)
                );
            case ZURUECKVERWANDELT_IN_VORHALLE:
            case ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN:
                return onSCEnterPrinzLocation(from, to);
            default:
                // TODO Wenn der Frosch nur rekursiv enthalten ist (Frosch sitzt auf dem Tisch),
                //  dann beschreiben (vgl. BewegenAction)
                return n.add(neuerSatz("Hier sitzt "
                        + froschprinzDesc.nom(), noTime())
                        .phorikKandidat(froschprinzDesc, FROSCHPRINZ));
        }
    }

    private AvTimeSpan onSCEnterPrinzLocation(
            @Nullable final ILocationGO from, final ILocationGO toAndPrinzLocation) {
        if (gos.isOrHasRecursiveLocation(from, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                gos.isOrHasRecursiveLocation(toAndPrinzLocation, SCHLOSS_VORHALLE)) {
            return prinzVerlaesstSchlossVorhalle();
        }

        if (gos.isOrHasRecursiveLocation(from, SCHLOSS_VORHALLE) &&
                gos.isOrHasRecursiveLocation(toAndPrinzLocation, DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return prinzFaehrtMitWagenDavon();
        }

        final Nominalphrase froschprinzDesc = getFroschprinzDescription();

        // TODO Wenn der Prinz nur rekursiv enthalten ist (Prinz sitzt auf einem Stuhl),
        //  dann genauer beschreiben (vgl. BewegenAction)
        return n.add(du("siehst "
                + getFroschprinzDescription().akk(), noTime())
                .phorikKandidat(froschprinzDesc, FROSCHPRINZ));
    }

    private AvTimeSpan prinzVerlaesstSchlossVorhalle() {
        if (n.getStoryState().allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            final AvTimeSpan timeSpan = n.add(
                    satzanschluss(", aber die Menge hat dich schon von dem "
                            + "jungen Königssohn getrennt", secs(15))
                            .phorikKandidat(M, FROSCHPRINZ));

            return timeSpan.plus(prinzDraussenVorDemSchlossAngekommen());
        }

        final AvTimeSpan timeSpan = n.add(
                neuerSatz("In der Menge ist der junge Königssohn nicht mehr zu "
                        + "erkennen", secs(15))
                        .phorikKandidat(M, FROSCHPRINZ));

        return timeSpan.plus(prinzDraussenVorDemSchlossAngekommen());
    }

    private AvTimeSpan prinzFaehrtMitWagenDavon() {
        final AvTimeSpan timeSpan = n.add(
                // TODO Danach stehst du vom Tisch auf und drängst dich durch das
                //  Eingangstor. DANN siehst du noch einen Wagen davonfahren... - das DANN
                //  ergibt keinen Sinn. Wie kann man das sinnvoll verhindern?
                //  Du drängst dich durch das Eingangstor und siehst noch... ergibt
                //  total Sinn. Muss man möglicherweise bei du-Sätzen festlegen,
                //  Ob sie neue Handlung beginnen - und, falls nicht, dann verbieten?

                du("siehst",
                        "noch einen Wagen davonfahren, mit acht weißen Pferden bespannt, "
                                + "jedes mit weißen Straußfedern auf dem Kopf", mins(2))
                        .beendet(CHAPTER));

        return timeSpan.plus(locationComp.narrateAndUnsetLocation());
    }

    @Contract("null, _ -> !null")
    private AvTimeSpan onGoldeneKugelEnter(@Nullable final ILocationGO from,
                                           final ILocationGO to) {
        if (!gos.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
            // auch nicht vom Spieler oder aus einer Tasche des Spielers o.Ä.

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
        final Nominalphrase froschprinzDesc = getFroschprinzDescription(true);
        return n.add(neuerSatz(
                capitalize(
                        froschprinzDesc.nom()) +
                        " schaut dich vorwurfsvoll und etwas hochnäsig an",
                secs(5))
                .phorikKandidat(froschprinzDesc, FROSCHPRINZ));
    }

    private AvTimeSpan onFroschprinzEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (to.is(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST)) {
            // Der Froschprinz hat es auf den Tisch beim Schlossfest geschafft!
            return onFroschprinzEnterTischBeimSchlossfest();
        }

        if (
            // Der Froschprinz ist aus dem Nichts erschienen.
                from == null ||
                        // oder der Froschprinz kam zumindest nicht vom Spieler, auch nicht aus
                        // einer Tasche o.Ä.
                        !gos.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
            return noTime();
        }

        // Der Froschprinz war vorher beim SC.
        if (!stateComp.hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return noTime();
        }

        final Nominalphrase froschprinzDesc = getFroschprinzDescription(true);
        return n.addAlt(
                neuerSatz(capitalize(froschprinzDesc.nom()) + " quakt erbost",
                        secs(5))
                        .phorikKandidat(froschprinzDesc, FROSCHPRINZ),
                neuerSatz("Entrüstet quakt " + froschprinzDesc.nom(),
                        secs(5))
                        .phorikKandidat(froschprinzDesc, FROSCHPRINZ)
        );
    }

    private AvTimeSpan onFroschprinzEnterTischBeimSchlossfest() {
        // Ist der Spieler auch da?
        if (!loadSC().locationComp()
                .hasRecursiveLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            stateComp.setState(BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN);

            return noTime();
        }

        final SubstantivischePhrase froschDescOderAnapher =
                getAnaphPersPronWennMglSonstShortDescription(FROSCHPRINZ);

        stateComp.setState(BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN);

        return n.add(neuerSatz(
                "Wie " +
                        froschDescOderAnapher.nom() +
                        " nun da sitzt, glotzt " +
                        froschDescOderAnapher.nom() +
                        " dich mit großen Glubschaugen an und spricht: „Nun füll deine "
                        + "Holzschale auf, wir wollen zusammen essen.“",
                secs(10))
                .phorikKandidat(froschDescOderAnapher, FROSCHPRINZ));
    }

    @Override
    public AvTimeSpan onEssen(final IGameObject gameObject) {
        if (!gameObject.is(SPIELER_CHARAKTER)) {
            // Wenn nicht der Spieler isst, ist es dem Frosch egal
            return noTime();
        }

        return onSCEssen();
    }

    private AvTimeSpan onSCEssen() {
        if (!loadSC().locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            // Wenn der Spieler nicht im Schloss isst, ist es dem Frosch egal
            return noTime();
        }

        if (!schlossfestHatBegonnen()) {
            // Wenn der Spieler nicht auf dem Schlossfest isst, ist es dem
            // Frosch egal

            return noTime();
        }

        return onSCEssenBeimSchlossfest();
    }

    private AvTimeSpan onSCEssenBeimSchlossfest() {
        if (stateComp.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && locationComp.hasRecursiveLocation(SPIELER_CHARAKTER)) {
            return froschprinzHuepftAusTascheUndWillMitessen();
        }

        if (stateComp.hasState(WARTET_AUF_SC_BEIM_SCHLOSSFEST)) {
            return froschprinzSitztAufEinmalAufDerBankUndWillMitessen();
        }

        if (stateComp.hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return froschprinzHatHochhebenGefordertUndWillMitessen();
        }

        return noTime();
    }

    private AvTimeSpan froschprinzHuepftAusTascheUndWillMitessen() {
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

                    setFroschHatHochhebenGefordert();

                    return timeSpan;
                }
        );
    }

    private AvTimeSpan froschprinzSitztAufEinmalAufDerBankUndWillMitessen() {
        final AvTimeSpan timeSpan = n.add(du(
                "spürst", "auf einmal etwas Feuchtes an deinem rechten Bein – oh "
                        + "nein, der "
                        + "garstige Frosch! „Heb mich herauf!“, ruft er, „weißt du nicht, was du "
                        + "zu mir gesagt bei dem kühlen Brunnenwasser? Heb mich herauf!“",
                "auf einmal",
                secs(20))
                .beendet(PARAGRAPH));

        setFroschHatHochhebenGefordert();

        return timeSpan;
    }

    private void setFroschHatHochhebenGefordert() {
        loadSC().feelingsComp().setMood(ANGESPANNT);
        stateComp.setState(HAT_HOCHHEBEN_GEFORDERT);
    }

    private AvTimeSpan froschprinzHatHochhebenGefordertUndWillMitessen() {
        final SpielerCharakter sc = loadSC();
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
            case ZURUECKVERWANDELT_IN_VORHALLE:
                if (now.isEqualOrAfter(stateComp.getStateDateTime()
                        // als der Prinz aufgestanden ist
                        .plus(WEGZEIT_PRINZ_TISCH_DURCH_VORHALLE))) {
                    return prinzDraussenVorDemSchlossAngekommen();
                }
                return noTime();
            case ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN:
                if (now.isEqualOrAfter(stateComp.getStateDateTime()
                        // als der Prinz die Vorhalle verlassen hat
                        .plus(ZEIT_FUER_ABFAHRT_PRINZ_MIT_WAGEN))) {
                    return locationComp.narrateAndUnsetLocation();
                }
                return noTime();
        }

        return noTime();
    }

    private AvTimeSpan froschprinzLaueftZumSchlossfestLos() {
        // TODO Find all equals() warnings and fix the code.

        AvTimeSpan timeElapsed = noTime();

        @Nullable final ILocationGO scLocation = loadSC().locationComp().getLocation();

        if ((scLocation != null && locationComp.hasSameUpperMostLocationAs(SPIELER_CHARAKTER))) {
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

    private AvTimeSpan prinzDraussenVorDemSchlossAngekommen() {
        return locationComp.narrateAndSetLocation(
                DRAUSSEN_VOR_DEM_SCHLOSS,
                () -> {
                    stateComp.setState(ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN);
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
                loadSC().memoryComp().isKnown(FROSCHPRINZ), shortIfKnown);
    }

    private boolean schlossfestHatBegonnen() {
        return ((IHasStateGO) gos.load(SCHLOSSFEST))
                .stateComp().hasState(BEGONNEN);
    }
}
