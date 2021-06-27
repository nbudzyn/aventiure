package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IWetterChangedReactions;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.data.world.syscomp.wetter.WetterData;

import static com.google.common.base.Preconditions.checkArgument;
import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.AM_BAUM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.AUF_DEM_BODEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.GESAMMELT;
import static de.nb.aventiure2.data.world.syscomp.wetter.windstaerke.Windstaerke.SCHWERER_STURM;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * "Reaktionen" des Holzes, das der Sturm von den Bäumen bricht und aus dem
 * der SC dann eine Strickleiter baut (z.B: Sturm bricht das Holz von den Bäumen).
 */
public class HolzFuerStrickleiterReactionsComp
        extends AbstractReactionsComp
        implements IWetterChangedReactions, IMovementReactions {
    private final AbstractStateComp<HolzFuerStrickleiterState> stateComp;
    private final LocationComp locationComp;

    public HolzFuerStrickleiterReactionsComp(
            final Narrator n,
            final World world,
            final AbstractStateComp<HolzFuerStrickleiterState> stateComp,
            final LocationComp locationComp) {
        super(HOLZ_FUER_STRICKLEITER, n, world);
        this.stateComp = stateComp;
        this.locationComp = locationComp;
    }

    @Override
    public void onWetterChanged(final ImmutableList<WetterData> wetterSteps) {
        checkArgument(wetterSteps.size() >= 2,
                "At least two wetter steps necessary: old and new");

        if (!stateComp.hasState(AM_BAUM)) {
            return;
        }

        if (!WetterData.contains(wetterSteps, SCHWERER_STURM)) {
            return;
        }

        locationComp.narrateAndSetLocation(DRAUSSEN_VOR_DEM_SCHLOSS,
                () -> stateComp.narrateAndSetState(AUF_DEM_BODEN));

        if (loadSC().locationComp()
                .hasVisiblyRecursiveLocation(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            if (wetterSteps.get(wetterSteps.size() - 1).getWindstaerkeUnterOffenemHimmel()
                    .compareTo(SCHWERER_STURM) >= 0) {
                // Wetter hat zu schwerem Sturm gewechselt.
                scErlebtAesteWerdenVonDenBaeumenGebrochen();
            } else {
                // Wetter hatte zu schwerem Sturm gewechselt, aber der schwere Sturm ist
                // schon wieder vorbei.
                scErlebtAesteWurdenZwischenzeitlichVonDenBaeumenGebrchen();
            }
        }

        // FIXME Danach sollte sich das Wetter wieder normalisieren
        //  (Planwetter -> null).
    }

    private void scErlebtAesteWerdenVonDenBaeumenGebrochen() {
        n.narrate(neuerSatz(PARAGRAPH,
                "Der Sturm bricht einiges Holz von den Bäumen, große und kleine Äste")
                .schonLaenger()
                .timed(NO_TIME));

        setScKnownAndAssumedStateToActual();
    }

    private void scErlebtAesteWurdenZwischenzeitlichVonDenBaeumenGebrchen() {
        n.narrate(neuerSatz(PARAGRAPH,
                "Der Sturm hat einiges Holz von den Bäumen gebrochen, große und kleine Äste")
                .schonLaenger()
                .timed(NO_TIME));

        setScKnownAndAssumedStateToActual();
    }


    @Override
    public void onLeave(final ILocatableGO locatable, final ILocationGO from,
                        @Nullable final ILocationGO to) {
    }

    @Override
    public boolean isVorScVerborgen() {
        return false;
    }

    @Override
    public void onEnter(final ILocatableGO locatable, @Nullable final ILocationGO from,
                        final ILocationGO to) {
        if (locatable.is(getGameObjectId())) {
            onHolzEnter(to);
        }
    }

    private void onHolzEnter(final ILocationGO to) {
        if (!stateComp.hasState(AUF_DEM_BODEN)) {
            return;
        }

        if (to.isOrHasRecursiveLocation(world.loadSC())) {
            stateComp.narrateAndSetState(GESAMMELT);
        }
    }

    private void setScKnownAndAssumedStateToActual() {
        world.narrateAndUpgradeScKnownAndAssumedState(HOLZ_FUER_STRICKLEITER);
    }
}
