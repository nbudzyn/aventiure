package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

/**
 * "Reaktionen" des Schlossfestess, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Das Schlossfest beginnt.)
 */
public class SchlossfestReactionsComp
        extends AbstractReactionsComp
        implements ITimePassedReactions {
    private final AbstractStateComp<SchlossfestState> stateComp;

    public SchlossfestReactionsComp(final AvDatabase db, final Narrator n,
                                    final World world,
                                    final AbstractStateComp<SchlossfestState> stateComp) {
        super(SCHLOSSFEST, n, world);
        this.stateComp = stateComp;
    }

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        if (SCHLOSSFEST_BEGINN_DATE_TIME.isWithin(change)) {
            schlossfestBeginnt();
        }
    }

    private void schlossfestBeginnt() {
        stateComp.narrateAndSetState(BEGONNEN);
        ((ILocatableGO) world.load(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST))
                .locationComp().narrateAndSetLocation(SCHLOSS_VORHALLE);

        final @Nullable IGameObject currentRoom = loadSC().locationComp().getLocation();

        if (currentRoom == null) {
            return;
        }

        loadSC().feelingsComp().requestMood(NEUTRAL);

        if (!currentRoom.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return;  // Passiert nebenher und braucht KEINE zusätzliche Zeit
        }

        // Der Spieler weiß jetzt, dass das Schlossfest läuft
        loadSC().mentalModelComp().setAssumptionsToActual(SCHLOSSFEST);

        n.narrate(
                neuerSatz(PARAGRAPH, "Dir fällt auf, dass Handwerker dabei sind, überall "
                        + "im Schlossgarten kleine bunte Pagoden aufzubauen. Du schaust eine Weile "
                        + "zu, und wie es scheint, beginnen von überallher Menschen zu "
                        + "strömen. Aus dem Schloss weht dich der Geruch von Gebratenem an.")
                        .timed(mins(30)));
    }
}
