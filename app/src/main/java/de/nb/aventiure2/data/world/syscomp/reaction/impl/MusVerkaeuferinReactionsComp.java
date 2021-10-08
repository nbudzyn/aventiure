package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static de.nb.aventiure2.data.world.gameobject.World.*;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;

/**
 * Reaktionen der Mus-Verkäuferin - z.B. darauf, dass der Markt öffnet oder schließt.
 */
public class MusVerkaeuferinReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions {
    public MusVerkaeuferinReactionsComp(final CounterDao counterDao,
                                        final Narrator n,
                                        final World world) {
        super(MUS_VERKAEUFERIN, counterDao, n, world);
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
        // FIXME Eine dicke Bäuerin verkauft Mus

        // FIXME - "Der Duft von süßem Mus steigt dir in Nase"
        //  "Der Geruch von dem süßen Mus kitzelt dir die Nase"
        //  "Mus feil", ruft
        //   die
        //  dicke Bauersfrau. Die schöne junge Frau sortiert ihre irdenen Näpfe und Töpfe.
        //  "Wieder steigt dir der Geruch von dem süßen Mus in die Nase" (Hunger?!)
    }

    // FIXME "Auch die dicke Bäuerin ist nicht mehr da."
}
