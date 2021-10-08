package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.MusVerkaeuferinReactionsComp.Counter.BESCHREIBUNG_MUS_VERKAEUFERIN;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MUS;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.VERKAUFEN;

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
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;

/**
 * Reaktionen der Mus-Verkäuferin - z.B. darauf, dass der Markt öffnet oder schließt.
 */
@SuppressWarnings("UnnecessaryReturnStatement")
public class MusVerkaeuferinReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions, ITimePassedReactions {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    public enum Counter {
        BESCHREIBUNG_MUS_VERKAEUFERIN;
    }

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
        if (locatable.is(SPIELER_CHARAKTER)) {
            onSCEnter(from, to);
            return;
        }
    }

    private void onSCEnter(@Nullable final ILocationGO from, final ILocationGO to) {
        if (world.isOrHasRecursiveLocation(to, VOR_DEM_ALTEN_TURM)) {
            onSCEnter_Bauernmarkt(from, to);
            return;
        }
    }

    private void onSCEnter_Bauernmarkt(@Nullable final ILocationGO scFrom, final ILocationGO scTo) {
        if (!world.hasSameVisibleOuterMostLocationAsSC(getGameObjectId())
                // SC und Mus-Verkäuferin sind nicht am gleichen Ort
                || isVorScVerborgen()
                || LocationSystem.isOrHasRecursiveLocation(scTo, scFrom)
                || LocationSystem.isOrHasRecursiveLocation(scFrom, scTo)
            // Der Spieler ist nur im selben Raum auf einen Tisch gestiegen,
            // wieder vom Tisch herabgestiegen o.Ä.
        ) {
            // Die Mus-Verkäuferin nicht (erneut) beschreiben.
            return;
        }

        if (counterDao.get(BESCHREIBUNG_MUS_VERKAEUFERIN) == 1) {
            n.narrate(neuerSatz("„Mus feil“, ruft", anaph(false).nomK())
                    .timed(NO_TIME)
                    .schonLaenger()
                    .withCounterIdIncrementedIfTextIsNarrated(BESCHREIBUNG_MUS_VERKAEUFERIN));

            world.narrateAndDoReactions().onRuf(getGameObjectId(), Ruftyp.MUS_FEIL);
        } else {
            final AltDescriptionsBuilder alt = alt();
            // "Die dicke Bäuerin verkauft Mus"
            alt.add(VERKAUFEN.mit(MUS).alsSatzMitSubjekt(anaph(false)));

            if (loadSC().memoryComp().isKnown(MUS_VERKAEUFERIN)) {
                alt.add(
                        neuerSatz("der Duft von süßem Mus steigt dir in die Nase", PARAGRAPH)
                                .schonLaenger(),
                        neuerSatz("der Geruch von süßem Mus kitzelt dir die Nase", PARAGRAPH)
                                .schonLaenger(),
                        neuerSatz("wieder steigt dir der Geruch von dem süßem Mus",
                                "in die Nase", PARAGRAPH).schonLaenger());
            }

            n.narrateAlt(alt, NO_TIME, BESCHREIBUNG_MUS_VERKAEUFERIN);
        }

        world.narrateAndUpgradeScKnownAndAssumedState(getGameObjectId());
    }

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        // FIXME         locationComp.narrateAndSetLocation(BAUERNMARKT);
        // FIXME Orte und ggf. assumptions aktualisieren

        // FIXME "GEGEN MORGEN belebt sich der Platz: Eine Bäuerin setzt sich und baut vor sich
        //  ... einige Leute kommen daher und schauen sich um"

        // FIXME "Auch die dicke Bäuerin ist nicht mehr da.
        // FIXME Wenn der Markt verlassen ist: Assumption über dicke Bäuerin
        //  etc. korrigieren.

        // FIXME Wenn der Markt verlassen wird: Assumption über dicke Bäuerin
        //  etc. korrigieren.

        // FIXME "..verlässt den Markt"

        // FIXME Eine dicke Bäuerin verkauft Mus

        // FIXME "Allmählich wird es SPÄTER NACHMITTAG(?) und die Bäuerin kramt ihre
        //  Siebensachen zusammen und geht"

        // FIXME "Auch die dicke Bäuerin ist nicht mehr da."

    }
}
