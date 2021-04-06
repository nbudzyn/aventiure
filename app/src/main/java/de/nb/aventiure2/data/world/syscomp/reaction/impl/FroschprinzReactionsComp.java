package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import com.google.common.annotations.VisibleForTesting;

import javax.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.gameobject.player.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IEssenReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzState;
import de.nb.aventiure2.data.world.syscomp.state.impl.FroschprinzStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.talking.impl.FroschprinzTalkingComp;
import de.nb.aventiure2.german.base.EinzelneSubstantivischePhrase;
import de.nb.aventiure2.german.base.StructuralElement;
import de.nb.aventiure2.german.base.SubstantivischePhrase;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.ANGESPANNT;
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
import static de.nb.aventiure2.german.base.NumerusGenus.M;
import static de.nb.aventiure2.german.base.StructuralElement.CHAPTER;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.description.DescriptionBuilder.satzanschluss;

@SuppressWarnings("UnnecessaryReturnStatement")
public class FroschprinzReactionsComp
        extends AbstractDescribableReactionsComp
        implements IMovementReactions, IEssenReactions, ITimePassedReactions {
    private static final AvTimeSpan WEGZEIT_PRINZ_TISCH_DURCH_VORHALLE = mins(1);
    private static final AvTimeSpan ZEIT_FUER_ABFAHRT_PRINZ_MIT_WAGEN = mins(10);

    private final FroschprinzStateComp stateComp;
    private final LocationComp locationComp;
    private final FroschprinzTalkingComp talkingComp;

    public FroschprinzReactionsComp(final AvDatabase db,
                                    final Narrator n,
                                    final World world,
                                    final FroschprinzStateComp stateComp,
                                    final LocationComp locationComp,
                                    final FroschprinzTalkingComp talkingComp) {
        super(FROSCHPRINZ, db.counterDao(), n, world);
        this.stateComp = stateComp;
        this.locationComp = locationComp;
        this.talkingComp = talkingComp;
    }

    @Override
    public void onLeave(final ILocatableGO locatable,
                        final ILocationGO from,
                        @Nullable final ILocationGO to) {
        talkingComp.updateSchonBegruesstMitSCOnLeave(locatable, from, to);

        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCLeave(from, to);
            return;
        }
    }

    private void onSCLeave(final ILocationGO from,
                           @Nullable final ILocationGO to) {
        if (locationComp.hasRecursiveLocation(SPIELER_CHARAKTER)) {
            // Spieler nimmt den Frosch mit
            onSCLeaveMitFroschprinz(from, to);
            return;
        }

        if (!locationComp.hasSameOuterMostLocationAs(from)
                || locationComp.hasSameOuterMostLocationAs(to)) {
            // Spieler lässt den Frosch nicht zurück

            return;
        }

        if (stateComp.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && !from.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            n.narrateAlt(NO_TIME,
                    neuerSatz("„Warte, warte“, ruft dir der Frosch noch nach, „nimm mich mit, "
                            + "ich kann nicht so "
                            + "laufen wie du.“ Aber was hilft ihm, dass er "
                            + "sein „Quak, quak!“ so laut schreit, "
                            + "als er kann, du hörst nicht darauf"
                    )
                            .undWartest(),
                    neuerSatz("„Halt!“, ruft der Frosch dir nach, „nimm mich mit!“")
            );
            world.narrateAndDoReactions().onRuf(
                    FROSCHPRINZ, Ruftyp.WARTE_NIMM_MICH_MIT);
            return;
        }
    }

    private void onSCLeaveMitFroschprinz(final ILocationGO from,
                                         @Nullable final ILocationGO to) {
        if (!from.is(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            return;
        }

        if (!schlossfestHatBegonnen()) {
            return;
        }

        if (!stateComp.hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return;
        }

        locationComp.narrateAndSetLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST);

        n.narrate(
                neuerSatz("Da springt dir der Frosch aus der Hand – weg ist er!", PARAGRAPH)
                        .timed(secs(3)));
    }

    @Override
    public boolean verbirgtSichVorEintreffendemSC() {
        final FroschprinzState state = stateComp.getState();
        return state.equals(UNAUFFAELLIG)
                || state.equals(WARTET_AUF_SC_BEIM_SCHLOSSFEST);
    }

    @Override
    public void onEnter(final ILocatableGO locatable,
                        @Nullable final ILocationGO from,
                        final ILocationGO to) {
        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(from, to);
            return;
        }

        // Die Goldene Kugel hat einen anderen Ort erreicht -
        // oder ein Container, der die Goldene Kugel (ggf. rekursiv) enthält,
        // hat einen anderen Ort erreicht
        if (world.isOrHasRecursiveLocation(GOLDENE_KUGEL, locatable)) {
            onGoldeneKugelRecEnter(from, to);
            return;
        }

        if (locatable.is(FROSCHPRINZ)) {
            onFroschprinzEnter(from, to);
            return;
        }
    }

    private void onSCEnter(@Nullable final ILocationGO from,
                           final ILocationGO to) {
        if (locationComp.hasRecursiveLocation(SPIELER_CHARAKTER) ||
                !locationComp.hasLocation(to)) {
            // Spieler hat nicht die Location betreten, in dem sich der Froschprinz befindet
            return;
        }

        final EinzelneSubstantivischePhrase desc = getDescription();
        switch (stateComp.getState()) {
            case UNAUFFAELLIG:
            case WARTET_AUF_SC_BEIM_SCHLOSSFEST:
                return;
            case HAT_HOCHHEBEN_GEFORDERT:
                loadSC().feelingsComp().requestMood(ANGESPANNT);

                // STORY Wenn der Frosch nur rekursiv enthalten ist (Frosch sitzt
                //  in einer Schale auf der Bank, dann hier prüfen und ggf. beschreiben
                //  (vgl. AblegenAction)
                n.narrateAlt(
                        neuerSatz(PARAGRAPH, "Plötzlich sitzt",
                                desc.nomStr(), // Gerät in Vergessenheit...
                                "neben dir auf der Bank. „Denk an dein",
                                "Versprechen“, quakt",
                                desc.persPron().nomStr(), // Gerät in Vergessenheit...
                                "dir zu,",
                                "„Lass uns aus einem Tellerlein essen!“ Du bist ganz",
                                "erschrocken – was für eine",
                                "abstoßende Vorstellung!")
                                .timed(secs(30)),
                        neuerSatz("Da stößt es schon von der Seite an dein Bein.",
                                "Du drehst dich",
                                "hastig weg und dein Herz klopft vor schlechtem",
                                "Gewissen, als",
                                "der Frosch „Heb mich herauf, heb mich herauf!“ quakt", PARAGRAPH)
                                .timed(secs(20)));
                return;
            case BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN:
                loadSC().feelingsComp().requestMood(ANGESPANNT);

                n.narrateAlt(
                        neuerSatz(PARAGRAPH, "Auf einmal sitzt",
                                desc.nomStr(), // Gerät in Vergessenheit
                                "bei dir auf dem Tisch. „Auf, füll deine",
                                "Schale, wir wollen zusammen essen“, quakt",
                                desc.persPron().nomStr(), // Gerät in Vergessenheit
                                "dich an. Es schauert dich bei dem Gedanken", PARAGRAPH)
                                .timed(secs(10)),
                        neuerSatz(PARAGRAPH, "Platsch – da springt auf einmal",
                                desc.nomK(),
                                "vor dich auf den Tisch. Gerade noch, dass "
                                        + desc.persPron().nomK(),
                                "dir nicht in die Essensschale gehüpft ist. Dir läuft",
                                "ein Schauer über den Rücken, als",
                                // IDEA Nur Bezugsobjekt und Kasus übergeben und
                                //  Text / Anaphern automatisch wählen lassen?
                                //  Abhängigkeiten gut bedenken.
                                desc.persPron().nomK(),
                                "fordert: „Nicht länger gezögert – nun lass uns zusammen",
                                "essen!“", PARAGRAPH)
                                .timed(secs(10))
                );
                return;
            case ZURUECKVERWANDELT_IN_VORHALLE:
            case ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN:
                onSCEnterPrinzLocation(from, to);
                return;
            default:
                // STORY Wenn der Frosch nur rekursiv enthalten ist (Frosch sitzt auf dem Tisch),
                //  dann beschreiben (vgl. BewegenAction)
                n.narrate(neuerSatz("Hier sitzt", desc.nomK()).timed(NO_TIME));
                return;
        }
    }

    private void onSCEnterPrinzLocation(
            @Nullable final ILocationGO from, final ILocationGO toAndPrinzLocation) {
        if (world.isOrHasRecursiveLocation(from, SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST) &&
                toAndPrinzLocation.is(SCHLOSS_VORHALLE)) {
            prinzVerlaesstSchlossVorhalle();
            return;
        }

        if (from != null && from.is(SCHLOSS_VORHALLE) &&
                world.isOrHasRecursiveLocation(toAndPrinzLocation, DRAUSSEN_VOR_DEM_SCHLOSS)) {
            prinzFaehrtMitWagenDavon();
            return;
        }

        final EinzelneSubstantivischePhrase desc = getDescription();

        // STORY Wenn der Prinz nur rekursiv enthalten ist (Prinz sitzt auf einem Stuhl),
        //  dann genauer beschreiben (vgl. BewegenAction)
        n.narrate(du("siehst", getDescription().akkK()).schonLaenger()
                .timed(NO_TIME));
    }

    private void prinzVerlaesstSchlossVorhalle() {
        if (n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            n.narrate(
                    satzanschluss(", aber die Menge hat dich schon von dem",
                            "jungen Königssohn getrennt")
                            .timed(secs(15))
                            .phorikKandidat(M, FROSCHPRINZ));

            prinzDraussenVorDemSchlossAngekommen();
            return;
        }

        n.narrate(
                neuerSatz("In der Menge ist der junge Königssohn nicht mehr zu "
                        + "erkennen")
                        .timed(secs(15))
                        .phorikKandidat(M, FROSCHPRINZ));

        prinzDraussenVorDemSchlossAngekommen();
    }

    private void prinzFaehrtMitWagenDavon() {
        n.narrate(
                du("siehst",
                        "noch einen Wagen davonfahren, mit acht weißen Pferden bespannt, "
                                + "jedes mit weißen Straußfedern auf dem Kopf", CHAPTER)
                        .schonLaenger()
                        .timed(mins(2)));

        // TODO Nach der Prinzabfahrt. Klares Lob, aber auch Hinweis auf einsame Stimme
        //  auf Turm oder so (wenn man sie schon kennt und Rapunzel noch im Turm ist....)
        //  Einfaches allgemeines Konzept: Immer wenn man eine Geschichte abgeschlossen
        //  hat, erhält man zwingend ein Lob ("Du bist sehr zufrieden mit dir, dass du..."
        //  und einen Tipp!
        // "Vor dem Schloss sind viele Leute zzsammengekimmen.
        //  Ein einfach gekleideter Mann spricht dich von der Seite an: Ein Prinz azs einen
        //  fernen Land!
        // Ich habe erzählen hören, er sei verzaubert gewesen. Ein Unfug, wenn Ihr mich fragt!
        // Aber sein Vatwr wird überglücklich sein, dass er wieder aufgetaucht ist!
        // Du bist kurz in Gedanken. Als du dich ihm zuwwnden Willsr, ist der Mann schon in der
        // Menge verscgwunden.

        locationComp.narrateAndUnsetLocation();
    }

    /**
     * Die Goldene Kugel hat <code>to</code> erreicht - oder ein Container, der die
     * Goldene Kugel (ggf. rekursiv) enthält, hat <code>to</code> erreicht.
     */
    private void onGoldeneKugelRecEnter(@Nullable final ILocationGO from,
                                        final ILocationGO to) {
        if (!world.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
            // auch nicht vom Spieler oder aus einer Tasche des Spielers o.Ä.

            return;
        }

        // Die goldene Kugel hat sich vom SC her irgendwohin bewegt.
        if (!to.is(UNTEN_IM_BRUNNEN)) {
            return;
        }

        // Der SC hat die goldene Kugel hochgeworfen und in den Brunnen fallen lassen.
        if (!to.is(IM_WALD_BEIM_BRUNNEN) || stateComp.hasState(UNAUFFAELLIG)) {
            return;
        }

        if (stateComp.hasState(HAT_SC_HILFSBEREIT_ANGESPROCHEN,
                HAT_NACH_BELOHNUNG_GEFRAGT,
                HAT_FORDERUNG_GESTELLT)) {
            // Der Spieler hat die goldene Kugel in den Brunnen fallen
            // lassen, obwohl er noch mit dem Frosch verhandelt.
            n.narrate(neuerSatz(StructuralElement.PARAGRAPH,
                    "Ob",
                    getDescription(true).nomK(),
                    "gerade seine glitschige Nase gerümpft hat?", PARAGRAPH)
                    .timed(secs(3)));
            return;
        }

        // Der Spieler hat die goldene Kugel letztlich in den Brunnen
        // fallen lassen, NACHDEM der Frosch schon Dinge hochgeholt hat.
        // Dann ist die Kugel jetzt WEG - PECH.
        final EinzelneSubstantivischePhrase froschprinzDesc = getDescription(true);
        n.narrate(neuerSatz(froschprinzDesc.nomK(),
                "schaut dich vorwurfsvoll und etwas hochnäsig an")
                .timed(secs(5)));
    }

    private void onFroschprinzEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (to.is(SCHLOSS_VORHALLE_LANGER_TISCH_BEIM_FEST)) {
            // Der Froschprinz hat es auf den Tisch beim Schlossfest geschafft!
            onFroschprinzEnterTischBeimSchlossfest();
            return;
        }

        if (
            // Der Froschprinz ist aus dem Nichts erschienen.
                from == null ||
                        // oder der Froschprinz kam zumindest nicht vom Spieler, auch nicht aus
                        // einer Tasche o.Ä.
                        !world.isOrHasRecursiveLocation(from, SPIELER_CHARAKTER)) {
            return;
        }

        // Der Froschprinz war vorher beim SC.
        if (!stateComp.hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            return;
        }

        final EinzelneSubstantivischePhrase froschprinzDesc = getDescription(true);
        n.narrateAlt(secs(5),
                neuerSatz(froschprinzDesc.nomK(), "quakt erbost"),
                neuerSatz("Entrüstet quakt", froschprinzDesc.nomK())
        );
    }

    private void onFroschprinzEnterTischBeimSchlossfest() {
        // Ist der Spieler auch da?
        if (!loadSC().locationComp()
                .hasRecursiveLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            stateComp.narrateAndSetState(BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN);
            return;
        }

        final SubstantivischePhrase anaph = anaph();

        stateComp.narrateAndSetState(BEIM_SCHLOSSFEST_AUF_TISCH_WILL_ZUSAMMEN_ESSEN);

        n.narrate(neuerSatz("Wie",
                anaph.nomK(),
                "nun da sitzt, glotzt",
                anaph.nomK(),
                "dich mit großen Glubschaugen an und spricht: „Nun füll deine",
                "Holzschale auf, wir wollen zusammen essen.“")
                .timed(secs(10)));
    }

    @Override
    public void onEssen(final IGameObject gameObject) {
        if (!gameObject.is(SPIELER_CHARAKTER)) {
            // Wenn nicht der Spieler isst, ist es dem Frosch egal
            return;
        }

        onSCEssen();
    }

    private void onSCEssen() {
        if (!loadSC().locationComp().hasRecursiveLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)) {
            // Wenn der Spieler nicht im Schloss isst, ist es dem Frosch egal
            return;
        }

        if (!schlossfestHatBegonnen()) {
            // Wenn der Spieler nicht auf dem Schlossfest isst, ist es dem
            // Frosch egal

            return;
        }

        onSCEssenBeimSchlossfest();
    }

    private void onSCEssenBeimSchlossfest() {
        if (stateComp.hasState(ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS)
                && locationComp.hasRecursiveLocation(SPIELER_CHARAKTER)) {
            froschprinzHuepftAusTascheUndWillMitessen();
            return;
        }

        if (stateComp.hasState(WARTET_AUF_SC_BEIM_SCHLOSSFEST)) {
            froschprinzSitztAufEinmalAufDerBankUndWillMitessen();
            return;
        }

        if (stateComp.hasState(HAT_HOCHHEBEN_GEFORDERT)) {
            froschprinzHatHochhebenGefordertUndWillMitessen();
            return;
        }
    }

    private void froschprinzHuepftAusTascheUndWillMitessen() {
        locationComp.narrateAndSetLocation(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
                () -> {
                    n.narrate(neuerSatz("Auf einmal ruckelt es unangenehm in deiner Tasche, und eh "
                            + "du dich's versiehst "
                            + "hüpft der garstige Frosch heraus. Patsch! – sitzt er neben "
                            + "dir auf der "
                            + "Holzbank und drängt sich nass an deinen Oberschenkel. "
                            + "„Heb mich herauf!“ ruft er "
                            + "„weißt du nicht, was du zu mir gesagt bei dem kühlen "
                            + "Brunnenwasser? Heb mich herauf!“", PARAGRAPH)
                            .timed(secs(25)));

                    narrateAndDoFroschHatHochhebenGefordert();
                }
        );
    }

    private void froschprinzSitztAufEinmalAufDerBankUndWillMitessen() {
        n.narrate(du("spürst",
                "auf einmal etwas Feuchtes an deinem rechten Bein – o "
                        + "nein, der "
                        + "garstige Frosch! „Heb mich herauf!“, ruft er, „weißt du nicht, was du "
                        + "zu mir gesagt bei dem kühlen Brunnenwasser? Heb mich herauf!“",
                PARAGRAPH)
                .mitVorfeldSatzglied("auf einmal")
                .timed(secs(20)));

        narrateAndDoFroschHatHochhebenGefordert();
    }

    private void narrateAndDoFroschHatHochhebenGefordert() {
        loadSC().feelingsComp().requestMood(ANGESPANNT);
        stateComp.narrateAndSetState(HAT_HOCHHEBEN_GEFORDERT);
        loadSC().mentalModelComp().setAssumedLocationToActual(FROSCHPRINZ);
    }

    private void froschprinzHatHochhebenGefordertUndWillMitessen() {
        final SpielerCharakter sc = loadSC();
        sc.feelingsComp().requestMood(ANGESPANNT);

        n.narrateAlt(secs(15),
                neuerSatz(PARAGRAPH,
                        "„Heb mich auf den Tisch“, ruft der Frosch, „wie sollen wir "
                                + "zwei sonst zusammmen essen?“ Dir klopft das Herz")
                        .undWartest()
                        .dann(),
                neuerSatz("„Versprechen muss man halten!“, ruft der Frosch", PARAGRAPH),
                neuerSatz(PARAGRAPH,
                        "Der Frosch lässt seine lange, schleimige Zunge vorschnellen. "
                                + "Hat er „Mitessen!“ gequakt?", PARAGRAPH));
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        switch (stateComp.getState()) {
            case ERWARTET_VON_SC_EINLOESUNG_SEINES_VERSPRECHENS:
                if (((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST))
                        .stateComp().hasState(BEGONNEN)) {
                    froschprinzLaueftZumSchlossfestLos();
                    return;
                }
                return;
            case ZURUECKVERWANDELT_IN_VORHALLE:
                if (endTime.isEqualOrAfter(stateComp.getStateDateTime()
                        // als der Prinz aufgestanden ist
                        .plus(WEGZEIT_PRINZ_TISCH_DURCH_VORHALLE))) {
                    prinzDraussenVorDemSchlossAngekommen();
                    return;
                }
                return;
            case ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN:
                if (endTime.isEqualOrAfter(stateComp.getStateDateTime()
                        // als der Prinz die Vorhalle verlassen hat
                        .plus(ZEIT_FUER_ABFAHRT_PRINZ_MIT_WAGEN))) {
                    locationComp.narrateAndUnsetLocation();
                    return;
                }
                return;
        }
    }

    @VisibleForTesting
    void froschprinzLaueftZumSchlossfestLos() {
        if (locationComp.hasSameVisibleOuterMostLocationAs(SPIELER_CHARAKTER)) {
            n.narrate(
                    neuerSatz(PARAGRAPH, "Plitsch platsch, plitsch platsch",
                            "hüpft der Frosch davon", PARAGRAPH)
                            .timed(secs(5)));
        }
        locationComp.narrateAndSetLocation(
                SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST,
                () -> stateComp.narrateAndSetState(WARTET_AUF_SC_BEIM_SCHLOSSFEST)
        );
    }

    private void prinzDraussenVorDemSchlossAngekommen() {
        locationComp.narrateAndSetLocation(
                DRAUSSEN_VOR_DEM_SCHLOSS,
                () -> stateComp.narrateAndSetState(ZURUECKVERWANDELT_SCHLOSS_VORHALLE_VERLASSEN)
        );
    }


    private boolean schlossfestHatBegonnen() {
        return ((IHasStateGO<SchlossfestState>) world.load(SCHLOSSFEST))
                .stateComp().hasState(BEGONNEN);
    }
}
