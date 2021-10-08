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
 * Reaktionen der Topf-Verkäuferin - z.B. darauf, dass der Markt öffnet oder schließt.
 */
public class TopfVerkaeuferinReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions {
    public TopfVerkaeuferinReactionsComp(final CounterDao counterDao,
                                         final Narrator n,
                                         final World world) {
        super(TOPF_VERKAEUFERIN, counterDao, n, world);
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
        // FIXME eine schöne junge Frau hat Töpfe und irdenes
        //  Geschirr vor
        //  sich stehen
        // FIXME eine junge Frau mit fein geschnittenem Gesicht
        //   hat Töpfe und irdenes Geschirr vor sich stehen

        // FIXME  - "Die schöne junge Frau klappert mit ihren Töpfen"
        // FIXME "Die junge Frau mit den feinen Gesichtszügen hat wohl gerade ein Schüsselchen /
        //  Tellerchen / irdenes
        //  Schälchen verkauft"
    }
}
