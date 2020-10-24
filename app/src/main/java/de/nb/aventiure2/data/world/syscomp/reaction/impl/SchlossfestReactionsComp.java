package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.time.*;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;

/**
 * "Reaktionen" des Schlossfestess, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Das Schlossfest beginnt.)
 */
public class SchlossfestReactionsComp
        extends AbstractReactionsComp
        implements ITimePassedReactions {
    private final AbstractStateComp<SchlossfestState> stateComp;

    public SchlossfestReactionsComp(final AvDatabase db, final World world,
                                    final AbstractStateComp<SchlossfestState> stateComp) {
        super(SCHLOSSFEST, db, world);
        this.stateComp = stateComp;
    }

    @Override
    public void onTimePassed(final AvDateTime startTime, final AvDateTime endTime) {
        if (SCHLOSSFEST_BEGINN_DATE_TIME.isWithin(startTime, endTime)) {
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
        db.counterDao().incAndGet(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN);

        n.narrate(
                neuerSatz(PARAGRAPH, "Dir fällt auf, dass Handwerker dabei sind, überall "
                                + "im Schlossgarten kleine bunte Pagoden aufzubauen. Du schaust eine Weile "
                                + "zu, und wie es scheint, beginnen von überallher Menschen zu "
                                + "strömen. Aus dem Schloss weht dich der Geruch von Gebratenem an.",
                        mins(30)));
    }
}
