package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.reaction.impl.MusVerkaeuferinReactionsComp.Counter.BESCHREIBUNG_MUS_VERKAEUFERIN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.MARKTZEIT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MARKT;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.MUS;
import static de.nb.aventiure2.german.base.Nominalphrase.npArtikellos;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.VERKAUFEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.VERLASSEN;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvDateTime;
import de.nb.aventiure2.data.time.AvTime;
import de.nb.aventiure2.data.time.AvTimeInterval;
import de.nb.aventiure2.data.world.base.Change;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.location.ILocatableGO;
import de.nb.aventiure2.data.world.syscomp.location.LocationComp;
import de.nb.aventiure2.data.world.syscomp.location.LocationSystem;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractDescribableReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.IMovementReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.NumerusGenus;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;

/**
 * Reaktionen der Mus-Verkäuferin - z.B. darauf, dass der Markt öffnet oder schließt.
 */
@SuppressWarnings("UnnecessaryReturnStatement")
public class MusVerkaeuferinReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions, ITimePassedReactions {
    private final LocationComp locationComp;

    @SuppressWarnings({"unused", "RedundantSuppression"})
    public enum Counter {
        BESCHREIBUNG_MUS_VERKAEUFERIN
    }

    private static final AvTimeInterval ZEIT_AUF_MARKT =
            AvTimeInterval.fromExclusiveToInclusive(
                    MARKTZEIT.getStartExclusive().rotate(mins(15)),
                    MARKTZEIT.getEndInclusive().rotateMinus(mins(5)));

    public MusVerkaeuferinReactionsComp(final CounterDao counterDao,
                                        final Narrator n,
                                        final World world,
                                        final LocationComp locationComp) {
        super(MUS_VERKAEUFERIN, counterDao, n, world);
        this.locationComp = locationComp;
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
            n.narrate(neuerSatz("„Mus feil!“, ruft", anaph(textContext, possessivDescriptionVorgabe,
                    false).nomK())
                    .timed(NO_TIME)
                    .schonLaenger()
                    .withCounterIdIncrementedIfTextIsNarrated(BESCHREIBUNG_MUS_VERKAEUFERIN));

            world.narrateAndDoReactions().onRuf(getGameObjectId(), Ruftyp.MUS_FEIL);
        } else {
            final AltDescriptionsBuilder alt = alt();
            // "Die dicke Bäuerin verkauft Mus"
            alt.add(VERKAUFEN.mit(npArtikellos(MUS)).alsSatzMitSubjekt(anaph(textContext,
                    possessivDescriptionVorgabe, false)));

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
        final AvTime time = change.getNachher().getTime();

        if (time.isWithin(ZEIT_AUF_MARKT) && locationComp.hasNoLocation()) {
            betrittDenMarkt();
        } else if (!time.isWithin(ZEIT_AUF_MARKT)
                && locationComp.hasRecursiveLocation(BAUERNMARKT)) {
            verlaesstDenMarkt();
        }
    }

    private void betrittDenMarkt() {
        if (world.hasSameVisibleOuterMostLocationAsSC(getGameObjectId())) {
            final SubstantivischePhrase anaph = anaph(textContext, possessivDescriptionVorgabe,
                    false);
            n.narrate(neuerSatz(anaph.nomK(),
                    "setzt sich und baut",
                    anaph.possArt().vor(NumerusGenus.PL_MFN).akkStr(), // "ihre"
                    "Waren auf").timed(NO_TIME));
        }

        locationComp.narrateAndSetLocation(BAUERNMARKT);
    }

    private void verlaesstDenMarkt() {
        if (world.hasSameVisibleOuterMostLocationAsSC(getGameObjectId())) {
            final SubstantivischePhrase anaph = anaph(textContext, possessivDescriptionVorgabe);

            n.narrateAlt(NO_TIME,
                    neuerSatz(VERLASSEN.mit(MARKT).alsSatzMitSubjekt(anaph(textContext,
                            possessivDescriptionVorgabe))),
                    neuerSatz(anaph, "kramt",
                            anaph.possArt().vor(NumerusGenus.PL_MFN).akkStr(), // "ihre"
                            "Siebensachen zusammen und geht")
            );
        }

        locationComp.narrateAndUnsetLocation();
    }
}
