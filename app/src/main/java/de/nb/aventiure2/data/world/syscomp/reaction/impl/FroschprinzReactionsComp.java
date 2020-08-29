package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import org.jetbrains.annotations.Contract;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.gameobject.player.SpielerCharakter;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IEssenReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.FROSCHPRINZ;
import static de.nb.aventiure2.data.world.gameobject.World.GOLDENE_KUGEL;
import static de.nb.aventiure2.data.world.gameobject.World.IM_WALD_BEIM_BRUNNEN;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.gameobject.World.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.gameobject.World.UNTEN_IM_BRUNNEN;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.AUF_DEM_WEG_ZUM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_FORDERUNG_GESTELLT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_HOCHHEBEN_GEFORDERT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_NACH_BELOHNUNG_GEFRAGT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.HAT_SC_HILFSBEREIT_ANGESPROCHEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.UNAUFFAELLIG;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.WARTET_AUF_SC_BEIM_SCHLOSSFEST;
import static de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState.ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
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
        extends AbstractDescribableReactionsComp
        implements IMovementReactions, IEssenReactions, ITimePassedReactions {
    private static final AvTimeSpan WEGZEIT_FROSCH_BRUNNEN_ZUM_SCHLOSSFEST = hours(6);
    private static final AvTimeSpan WEGZEIT_PRINZ_TISCH_DURCH_VORHALLE = mins(1);
    private static final AvTimeSpan ZEIT_FUER_ABFAHRT_PRINZ_MIT_WAGEN = mins(10);

    private static final AvDateTime FROSCH_LAEUFT_FRUEHESTENS_ZUM_SCHLOSSFEST =
            SCHLOSSFEST_BEGINN_DATE_TIME.minus(
                    WEGZEIT_FROSCH_BRUNNEN_ZUM_SCHLOSSFEST
                            // Der Frosch plant etwas Sicherheit ein
                            .plus(hours(6)));

    private final FroschprinzStateComp stateComp;
    private final LocationComp locationComp;

    public FroschprinzReactionsComp(final AvDatabase db,
                                    final World world,
                                    final FroschprinzStateComp stateComp,
                                    final LocationComp locationComp) {
        super(FROSCHPRINZ, db, world);
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
            return n.addAlt(
                    neuerSatz("„Warte, warte“, ruft dir der Frosch noch nach, „nimm mich mit, "
                            + "ich kann nicht so "
                            + "laufen wie du.“ Aber was hilft ihm, dass er "
                            + "sein „Quak, quak!“ so laut schreit, "
                            + "als er kann, du hörst nicht darauf", noTime())
                            .undWartest(),
                    neuerSatz("„Halt!“, ruft der Frosch dir nach, „nimm mich mit!“",
                            noTime())
            );
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

        // Die Goldene Kugel hat einen anderen Ort erreicht -
        // oder ein Container, der die Goldene Kugel (ggf. rekursiv) enthält,
        // hat einen anderen Ort erreicht
        if (world.isOrHasRecursiveLocation(GOLDENE_KUGEL, locatable)) {
            return onGoldeneKugelRecEnter(from, to);
        }

        if (locatable.is(FROSCHPRINZ)) {
            return onFroschprinzEnter(from, to);
        }

        return noTime();
    }


    private AvTimeSpan onSCEnter(@Nullable final ILocationGO from,
                                 final ILocationGO to) {
        if (locationComp.hasRecursiveLocation(SPIELER_CHARAKTER) ||
                !locationComp.hasLocation(to)) {
            // Spieler hat nicht die Location betreten, in dem sich der Froschprinz befindet
            return noTime();
        }

        final Nominalphrase desc = getDescription();
        switch (stateComp.getState()) {
            case UNAUFFAELLIG:
            case WARTET_AUF_SC_BEIM_SCHLOSSFEST:
                return noTime();
            case HAT_HOCHHEBEN_GEFORDERT:
                loadSC().feelingsComp().setMood(ANGESPANNT);

                // TODO Wenn der Frosch nur rekursiv enthalten ist (Frosch sitzt auf
                //  in einer Schale auf der Bank, dann hier prüfen und ggf. beschreiben
                //  (vgl. AblegenAction)
                return n.addAlt(
                        neuerSatz(PARAGRAPH, "Plötzlich sitzt "
                                        + desc.nom()
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
                                        + desc.nom()
                                        + " bei dir auf dem Tisch. „Auf, füll deine "
                                        + "Schale, wir wollen zusammen essen“, quakt " +
                                        desc.persPron().nom() +
                                        " dich an. Es schauert dich bei dem Gedanken",
                                secs(10))
                                .beendet(PARAGRAPH),
                        neuerSatz(PARAGRAPH, "Platsch – da springt auf einmal "
                                        + desc.nom()
                                        + " vor dich auf den Tisch. Gerade noch, dass er "
                                        + "dir nicht in die Essensschale gehüpft ist. Dir läuft "
                                        + "ein Schauer über den Rücken, als "
                                        + desc.persPron().nom()
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
                        + desc.nom(), noTime())
                        .phorikKandidat(desc, FROSCHPRINZ));
        }
    }

    private AvTimeSpan onSCEnterPrinzLocation(
            @Nullable final ILocationGO from, final ILocationGO toAndPrinzLocation) {
        if (world.isOrHasRecursiveLocation(from, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                toAndPrinzLocation.is(SCHLOSS_VORHALLE)) {
            return prinzVerlaesstSchlossVorhalle();
        }

        if (from != null && from.is(SCHLOSS_VORHALLE) &&
                world.isOrHasRecursiveLocation(toAndPrinzLocation, DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return prinzFaehrtMitWagenDavon();
        }

        final Nominalphrase desc = getDescription();

        // TODO Wenn der Prinz nur rekursiv enthalten ist (Prinz sitzt auf einem Stuhl),
        //  dann genauer beschreiben (vgl. BewegenAction)
        return n.add(du("siehst", getDescription().akk(), noTime())
                .phorikKandidat(desc, FROSCHPRINZ));
    }

    private AvTimeSpan prinzVerlaesstSchlossVorhalle() {
        if (n.requireNarration().allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
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

    /**
     * Die Goldene Kugel hat <code>to</code> erreicht - oder ein Container, der die
     * Goldene Kugel (ggf. rekursiv) enthält, hat <code>to</code> erreicht.
     */
    @Contract("null, _ -> !null")
    private AvTimeSpan onGoldeneKugelRecEnter(@Nullable final ILocationGO from,
                                              final ILocationGO to) {
        if (!world.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
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
        final Nominalphrase froschprinzDesc = getDescription(true);
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
                        !world.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
            return noTime();
        }

        // Der Froschprinz war vorher beim SC.
        if (!stateComp.hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return noTime();
        }

        final Nominalphrase froschprinzDesc = getDescription(true);
        return n.addAlt(
                neuerSatz(froschprinzDesc.nom() + " quakt erbost",
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
            return stateComp.narrateAndSetState(BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN);
        }

        final SubstantivischePhrase froschDescOderAnapher =
                getAnaphPersPronWennMglSonstShortDescription();

        final AvTimeSpan timeElapsed =
                stateComp.narrateAndSetState(BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN);

        return timeElapsed.plus(
                n.add(neuerSatz(
                        "Wie " +
                                froschDescOderAnapher.nom() +
                                " nun da sitzt, glotzt " +
                                froschDescOderAnapher.nom() +
                                " dich mit großen Glubschaugen an und spricht: „Nun füll deine "
                                + "Holzschale auf, wir wollen zusammen essen.“",
                        secs(10))
                        .phorikKandidat(froschDescOderAnapher, FROSCHPRINZ)));
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

                    return timeSpan.plus(
                            narrateAndDoFroschHatHochhebenGefordert());
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

        return timeSpan.plus(
                narrateAndDoFroschHatHochhebenGefordert());
    }

    private AvTimeSpan narrateAndDoFroschHatHochhebenGefordert() {
        loadSC().feelingsComp().setMood(ANGESPANNT);
        return stateComp.narrateAndSetState(HAT_HOCHHEBEN_GEFORDERT);
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

        return timeElapsed.plus(
                stateComp.narrateAndSetState(AUF_DEM_WEG_ZUM_SCHLOSSFEST));
    }

    private AvTimeSpan froschprinzAufSchlossfestAngekommen() {
        return locationComp.narrateAndSetLocation(
                SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
                () -> stateComp.narrateAndSetState(WARTET_AUF_SC_BEIM_SCHLOSSFEST)
        );
    }

    private AvTimeSpan prinzDraussenVorDemSchlossAngekommen() {
        return locationComp.narrateAndSetLocation(
                DRAUSSEN_VOR_DEM_SCHLOSS,
                () -> stateComp
                        .narrateAndSetState(ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)
        );
    }


    private boolean schlossfestHatBegonnen() {
        return ((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST))
                .stateComp().hasState(BEGONNEN);
    }
}
