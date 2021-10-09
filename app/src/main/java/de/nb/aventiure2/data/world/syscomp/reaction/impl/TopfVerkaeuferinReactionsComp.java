package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;

/**
 * Reaktionen der Topf-Verkäuferin - z.B. darauf, dass der Markt öffnet oder schließt.
 */
@SuppressWarnings("UnnecessaryReturnStatement")
public class TopfVerkaeuferinReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions, ITimePassedReactions {
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
        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(from, to);
            return;
        }
    }

    private void onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, BAUERNMARKT)) {
            onSCEnter_Bauernmarkt(from, to);
            return;
        }
    }

    private void onSCEnter_Bauernmarkt(@Nullable final ILocationGO scFrom, final ILocationGO scTo) {
        if (!world.hasSameVisibleOuterMostLocationAsSC(getGameObjectId())
                // SC und Topf-Verkäuferin sind nicht am gleichen Ort
                || isVorScVerborgen()
                || LocationSystem.isOrHasRecursiveLocation(scTo, scFrom)
                || LocationSystem.isOrHasRecursiveLocation(scFrom, scTo)
            // Der Spieler ist nur im selben Raum auf einen Tisch gestiegen,
            // wieder vom Tisch herabgestiegen o.Ä.
        ) {
            // Die Topf-Verkäuferin nicht (erneut) beschreiben.
            return;
        }

        final AltDescriptionsBuilder alt = alt();

        alt.add(neuerSatz(anaph(false).nomK()
                , "hat Töpfe und irdenes Geschirr vor sich stehen").schonLaenger());

        if (loadSC().memoryComp().isKnown(TOPF_VERKAEUFERIN)) {
            alt.add(
                    neuerSatz(anaph(false).nomK(), "klappert mit ihren Töpfen"),
                    neuerSatz(anaph(false).nomK(), "hat wohl gerade ein Schüsselchen",
                            "verkauft"),
                    neuerSatz(anaph().nomK(), "sortiert ihre irdenen Näpfe und Töpfe"));
        }

        n.narrateAlt(alt, secs(30));

        world.narrateAndUpgradeScKnownAndAssumedState(getGameObjectId());
    }

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        // FIXME Orte und ggf. assumptions aktualiseren

        // FIXME Neben ihr baut eine ... vor sich auf.

        // FIXME eine schöne junge Frau hat Töpfe und irdenes
        //  Geschirr vor
        //  sich stehen

        // FIXME eine junge Frau mit fein geschnittenem Gesicht
        //   hat Töpfe und irdenes Geschirr vor sich stehen

        // FIXME  - "Die schöne junge Frau klappert mit ihren Töpfen"

        // FIXME "Die junge Frau mit den feinen Gesichtszügen hat wohl gerade ein Schüsselchen /
        //  Tellerchen / irdenes
        //  Schälchen verkauft"

        // FIXME Du siehst auch, wie ...  dann geht sie ihrer Wege...

        // FIXME "Die schöne junge Frau stapelt alle ihre Töpfe und Schächen in ein Tuch,
        //  bindet es zusammen und geht ihrer Wege"

        // FIXME "..verlässt den Markt"
    }
}
