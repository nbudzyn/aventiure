package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.IGameObject;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN;
import static de.nb.aventiure2.data.world.gameobject.World.DRAUSSEN_VOR_DEM_SCHLOSS;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSSFEST_BEGINN_DATE_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE;
import static de.nb.aventiure2.data.world.gameobject.World.SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST;
import static de.nb.aventiure2.data.world.syscomp.feelings.Mood.NEUTRAL;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.BEGONNEN;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;

/**
 * "Reaktionen" des Schlossfestess, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Das Schlossfest beginnt.)
 */
public class SchlossfestReactionsComp
        extends AbstractReactionsComp
        implements ITimePassedReactions {
    private final AbstractStateComp<SchlossfestState> stateComp;

    public SchlossfestReactionsComp(final AvDatabase db, final World world,
                                    final AbstractStateComp stateComp) {
        super(SCHLOSSFEST, db, world);
        this.stateComp = stateComp;
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan timeElapsed = noTime();

        if (SCHLOSSFEST_BEGINN_DATE_TIME.isWithin(lastTime, now)) {
            timeElapsed = timeElapsed.plus(schlossfestBeginnt());
        }

        return timeElapsed;
    }

    private AvTimeSpan schlossfestBeginnt() {
        stateComp.setState(BEGONNEN);
        ((ILocatableGO) world.load(SCHLOSS_VORHALLE_AM_TISCH_BEIM_FEST))
                .locationComp().narrateAndSetLocation(SCHLOSS_VORHALLE);

        final @Nullable IGameObject currentRoom = loadSC().locationComp().getLocation();

        if (currentRoom == null) {
            return noTime();
        }

        loadSC().feelingsComp().setMood(NEUTRAL);

        if (!currentRoom.is(DRAUSSEN_VOR_DEM_SCHLOSS)) {
            return noTime();  // Passiert nebenher und braucht KEINE zusätzliche Zeit
        }

        // Der Spieler weiß jetzt, dass das Schlossfest läuft
        db.counterDao().incAndGet(COUNTER_ID_VOR_DEM_SCHLOSS_SCHLOSSFEST_KNOWN);

        return n.add(
                neuerSatz(PARAGRAPH, "Dir fällt auf, dass Handwerker dabei sind, überall "
                                + "im Schlossgarten kleine bunte Pagoden aufzubauen. Du schaust eine Weile "
                                + "zu, und wie es scheint, beginnen von überallher Menschen zu "
                                + "strömen. Aus dem Schloss weht dich der Geruch von Gebratenem an.",
                        mins(30)));
    }
}
