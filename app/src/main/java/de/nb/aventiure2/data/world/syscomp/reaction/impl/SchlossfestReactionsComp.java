package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.BETRUEBT;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.NOCH_NICHT_BEGONNEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.VERWUESTET;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.STURM;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
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
                // IDEA: Das Schlossfest könnte auch einen State WIEDER_AUFGEBAUT
                //  erhalten.
                break;
            default:
                throw new IllegalStateException("Unexpected state: " + stateComp.getState());
        }
    }

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        if (change.getNachher().isEqualOrAfter(SCHLOSSFEST_BEGINN_DATE_TIME)
                && stateComp.hasState(NOCH_NICHT_BEGONNEN)) {
            schlossfestBeginnt();
        }
    }

    private void schlossfestBeginnt() {
        if (loadSC().locationComp().hasVisiblyRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
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

        stateComp.narrateAndSetState(BEGONNEN);
        world.<ILocatableGO>load(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST)
                .locationComp().narrateAndSetLocation(SCHLOSS_VORHALLE);
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
