package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.NO_TIME;
import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.SchlossfestState.MARKTZEIT;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KOERBE;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.NumerusGenus.F;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.FLECHTEN;

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
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.base.SubstantivischePhrase;
import de.nb.aventiure2.german.description.AltDescriptionsBuilder;

/**
 * Reaktionen der Topf-Verkäuferin - z.B. darauf, dass der Markt öffnet oder schließt.
 */
// FIXME Man sollte die speziellen ReactionComps etc. besser zu Welt verschieben, so
//  dass alles, was mit der Korbflechterin zu tun hat, in einem Verzeichnis zu liegen kommt.
//  (Das ist ein Schritt zur Trennung von Welt und Framework).
@SuppressWarnings("UnnecessaryReturnStatement")
public class KorbflechterinReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions, ITimePassedReactions {
    private final LocationComp locationComp;

    private static final AvTimeInterval ZEIT_AUF_MARKT =
            AvTimeInterval.fromExclusiveToInclusive(
                    MARKTZEIT.getStartExclusive().rotate(mins(50)),
                    MARKTZEIT.getEndInclusive().rotateMinus(mins(40)));

    public KorbflechterinReactionsComp(final CounterDao counterDao,
                                       final Narrator n,
                                       final World world,
                                       final LocationComp locationComp) {
        super(KORBFLECHTERIN, counterDao, n, world);
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
                // SC und Korbflechterin sind nicht am gleichen Ort
                || isVorScVerborgen()
                || LocationSystem.isOrHasRecursiveLocation(scTo, scFrom)
                || LocationSystem.isOrHasRecursiveLocation(scFrom, scTo)
            // Der Spieler ist nur im selben Raum auf einen Tisch gestiegen,
            // wieder vom Tisch herabgestiegen o.Ä.
        ) {
            // Die Korbflechterin nicht (erneut) beschreiben.
            return;
        }

        final AltDescriptionsBuilder alt = alt();

        alt.add(FLECHTEN.mit(np(INDEF, KOERBE)).alsSatzMitSubjekt(anaph(textContext,
                possessivDescriptionVorgabe, false)));

        if (loadSC().memoryComp().isKnown(KORBFLECHTERIN)) {
            alt.add(neuerSatz(anaph(textContext, possessivDescriptionVorgabe, false).nomK(),
                    "ist fleißig dabei, einen Korb zu flechten"));
        }

        n.narrateAlt(alt, secs(30));

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
            final AltDescriptionsBuilder alt = alt();

            final SubstantivischePhrase anaph = anaph(textContext, possessivDescriptionVorgabe,
                    false);

            if (!loadSC().memoryComp().isKnown(KORBFLECHTERIN)) {
                alt.add(neuerSatz(anaph.nomK(),
                        "kommt dazu, offenbar eine Korblechterin")
                        .phorikKandidat(F, KORBFLECHTERIN));
            } else {
                alt.add(neuerSatz("Auch die alte Korbflechterin ist wieder",
                        "auf dem Markt")
                        .schonLaenger()
                        .phorikKandidat(F, KORBFLECHTERIN));
            }

            alt.add(neuerSatz(anaph.nomK(),
                    "findet einen Platz und beginnt einen Korb zu flechten"));

            n.narrateAlt(alt, NO_TIME);
        }

        locationComp.narrateAndSetLocation(BAUERNMARKT);
    }


    private void verlaesstDenMarkt() {
        if (world.hasSameVisibleOuterMostLocationAsSC(getGameObjectId())) {
            final AltDescriptionsBuilder alt = alt();

            final SubstantivischePhrase anaph = anaph(textContext, possessivDescriptionVorgabe);

            alt.add(neuerSatz(anaph.nomK(),
                    "deckt ihren Stand ab und verschnürt alles gut"));

            if (loadSC().memoryComp().isKnown(KORBFLECHTERIN)) {
                alt.add(neuerSatz("Die Korbflechterin räumt ihren Stand auf, dann",
                        "verlässt sie den Markt")
                        .schonLaenger()
                        .phorikKandidat(F, KORBFLECHTERIN));
            }
        }

        locationComp.narrateAndUnsetLocation();
    }

}
