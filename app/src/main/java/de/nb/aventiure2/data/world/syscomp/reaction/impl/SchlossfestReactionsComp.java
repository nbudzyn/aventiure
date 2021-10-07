package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BETRUEBT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NOCH_NICHT_BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.VERWUESTET;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.STURM;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IWetterChangedReactions;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterData;
import de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke;

/**
 * "Reaktionen" des Schlossfestess, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Das Schlossfest beginnt.)
 */
public class SchlossfestReactionsComp
        extends AbstractReactionsComp
        implements IWetterChangedReactions, ITimePassedReactions {
    private final AbstractStateComp<SchlossfestState> stateComp;

    public SchlossfestReactionsComp(final Narrator n,
                                    final World world,
                                    final AbstractStateComp<SchlossfestState> stateComp) {
        super(SCHLOSSFEST, n, world);
        this.stateComp = stateComp;
    }

    @Override
    public void onWetterChanged(final ImmutableList<WetterData> wetterSteps) {
        checkArgument(wetterSteps.size() >= 2,
                "At least two wetter steps necessary: old and new");

        switch (stateComp.getState()) {
            case NOCH_NICHT_BEGONNEN:
                break;
            case BEGONNEN:
                if (WetterData.contains(wetterSteps, STURM)) {
                    stateComp.narrateAndSetState(VERWUESTET);

                    if (loadSC().locationComp()
                            .hasVisiblyRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
                        if (wetterSteps.get(wetterSteps.size() - 1)
                                .getWindstaerkeUnterOffenemHimmel()
                                .compareTo(Windstaerke.STURM) >= 0) {
                            // Wetter hat zu Sturm gewechselt.
                            scErlebtSchlossfestWirdVonSturmVerwuestet();
                        } else {
                            // Wetter hatte zu Sturm gewechselt, aber der Sturm ist schon wieder
                            // vorbei.
                            scErlebtSchlossfestWurdeZwischenzeitlichVomSturmVerwuestet();
                        }
                    }
                }
                break;
            case VERWUESTET:
                // fall-through
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN:
                // fall-through
            case NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN:
                break;
            default:
                throw new IllegalStateException("Unexpected state: " + stateComp.getState());
        }
    }

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        final AvTime time = change.getNachher().getTime();

        if (change.getNachher().isEqualOrAfter(SCHLOSSFEST_BEGINN_DATE_TIME)
                && stateComp.hasState(NOCH_NICHT_BEGONNEN)) {
            schlossfestBeginnt();
        } else if (
            // Wenn das Schlossfest verwüstet ist...
                stateComp.hasState(VERWUESTET)
                        // ... und der SC weiß das auch schon...
                        && loadSC().mentalModelComp().hasAssumedState(getGameObjectId(), VERWUESTET)
                        // ... und der SC ist ein Stück weit weg, ...
                        && loadSC().locationComp().hasLocation(ABZWEIG_IM_WALD)) {
            // dann wird das Schlossfest gerichtet und der Markt baut sich auf.
            stateComp.narrateAndSetState(SchlossfestState.getMarkt(time));
        } else if (stateComp.hasState(NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN)
                && SchlossfestState.getMarkt(time)
                != NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN) {
            marktOeffnet();
        } else if (stateComp.hasState(NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN)
                && SchlossfestState.getMarkt(time)
                != NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN) {
            marktSchliesst();
        }
    }

    private void marktOeffnet() {
        stateComp.narrateAndSetState(NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_OFFEN);

        if (loadSC().locationComp().hasVisiblyRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            scErlebtDraussenVorDemSchloss_MarktOeffnet();
        }

        // FIXME Texte für Bauernmarkt, Assumptions aktualisieren

        // FIXME "GEGEN MORGEN belebt sich der Platz: Eine Bäuerin setzt sich und baut vor sich
        //  ... einige Leute kommen daher und schauen sich um"

        // FIXME "Wie es scheint, beginnt jetzt der Markt"
    }

    private void scErlebtDraussenVorDemSchloss_MarktOeffnet() {
        n.narrate(neuerSatz(PARAGRAPH, "Auf dem Markt wird es lebendig: Händler",
                "bauen ihre Stände auf, die ersten Leute schauen sich dort um")
                .timed(mins(5))); // extra-Zeit fürs Zuschauen

        loadSC().mentalModelComp().setAssumptionsToActual(SCHLOSSFEST);
    }

    private void marktSchliesst() {
        stateComp.narrateAndSetState(NACH_VERWUESTUNG_WIEDER_GERICHTET_MARKTSTAENDE_GESCHLOSSEN);

        if (loadSC().locationComp().hasVisiblyRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            scErlebtDraussenVorDemSchloss_MarktSchliesst();
        }

        // FIXME Texte für Bauernmarkt, Assumptions aktualisieren

        // FIXME "Die Korbflechterin deckt ihren Stand ab und verschnürt alles gut"

        // FIXME "Allmählich wird es SPÄTER NACHMITTAG(?) und die Bäuerin kramt ihre
        //  Siebensachen zusammen und geht"

        // FIXME "Die schöne junge Frau stapelt alle ihre Töpfe und Schächen in ein Tuch,
        //  bindet es zusammen und geht ihrer Wege"

        // FIXME "Auch die dicke Bäuerin ist nicht mehr da. Die Menschen haben sich
        //  verlaufen, und
        //  du stehst allein zwischen den leeren Bänken und Ständen"

        // FIXME "Die Standbesitzer verlassen den Markt"
    }

    private void scErlebtDraussenVorDemSchloss_MarktSchliesst() {
        n.narrateAlt(mins(10), // extra-Zeit fürs Zuschauen
                neuerSatz(PARAGRAPH, "Auf dem Markt lösen die Händler ihre Stände auf",
                        SENTENCE,
                        "die Menschen verlaufen sich"),
                neuerSatz(PARAGRAPH, "Auf dem Markt kramen die Händler",
                        "ihre Siebensachen zusammen",
                        SENTENCE,
                        "bald ist der kleine Bauernmarkt wie leer gefegt"));

        loadSC().mentalModelComp().setAssumptionsToActual(SCHLOSSFEST);
    }

    private void schlossfestBeginnt() {
        stateComp.narrateAndSetState(BEGONNEN);
        world.<ILocatableGO>loadRequired(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)
                .locationComp().narrateAndSetLocation(SCHLOSS_VORHALLE);

        if (loadSC().locationComp().hasVisiblyRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            scErlebtSchlossfestBeginnt();
        }
    }

    private void scErlebtSchlossfestBeginnt() {
        n.narrate(
                neuerSatz(PARAGRAPH, "Dir fällt auf, dass Handwerker dabei sind, überall "
                        + "im Schlossgarten kleine bunte Pagoden aufzubauen. Du schaust eine "
                        + "Weile "
                        + "zu, und wie es scheint, beginnen von überallher Menschen zu "
                        + "strömen. Aus dem Schloss weht dich der Geruch von Gebratenem an.")
                        .timed(mins(30))); // extra-Zeit fürs Zuschauen

        loadSC().feelingsComp().requestMood(NEUTRAL);

        // Der Spieler weiß jetzt, dass das Schlossfest läuft
        loadSC().mentalModelComp().setAssumptionsToActual(SCHLOSSFEST);
    }

    private void scErlebtSchlossfestWirdVonSturmVerwuestet() {
        n.narrate(du(PARAGRAPH, "musst",
                "erschüttert miterleben, wie der Sturm viele der kleinen",
                "farbigen Pagoden überall im Schlossgarten umwirft oder",
                "ihnen die Dächer abreißt. Marktleute verlassen ihre Stände",
                "oder versuchen sie an windgeschützten Plätzen zu verzurren.",
                "Viele Menschen fliehen ins Schloss", PARAGRAPH)
                .mitVorfeldSatzglied("erschüttert")
                .schonLaenger()
                .timed(mins(5))); // extra-Zeit fürs Zuschauen

        loadSC().feelingsComp().requestMoodMax(BETRUEBT);
        setScAssumedStateToActual();
    }

    private void scErlebtSchlossfestWurdeZwischenzeitlichVomSturmVerwuestet() {
        n.narrate(neuerSatz(PARAGRAPH,
                "Der Sturm hat im Schlossgarten heftig gewütet, viele der Pagoden sind",
                "umgeworfen oder ihre Dächer abgerissen. Einzelne Marktstände sind",
                "ausgeräumt oder stehen aufwendig verzurrt an windgeschützten Plätzen. –",
                "Aus dem Schloss aber hört man immer noch die Menschenmenge", PARAGRAPH)
                .schonLaenger()
                .timed(NO_TIME));

        loadSC().feelingsComp().requestMoodMax(BETRUEBT);
        setScAssumedStateToActual();
    }

    private void setScAssumedStateToActual() {
        loadSC().mentalModelComp().setAssumedStateToActual(SCHLOSSFEST);
    }
}
