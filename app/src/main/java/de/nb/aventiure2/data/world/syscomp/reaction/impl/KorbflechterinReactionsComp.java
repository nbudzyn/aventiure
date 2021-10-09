package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.KOERBE;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.description.AltDescriptionsBuilder.alt;
import static de.nb.aventiure2.german.description.DescriptionBuilder.neuerSatz;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.FLECHTEN;

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
// FIXME Man sollte die speziellen ReactionComps etc. besser zu Welt verschieben, so
//  dass alles, was mit der Korbflechterin zu tun hat, in einem Verzeichnis zu liegen kommt.
//  (Das ist ein Schritt zur Trennung von Welt und Framework).
public class KorbflechterinReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions, ITimePassedReactions {
    public KorbflechterinReactionsComp(final CounterDao counterDao,
                                       final Narrator n,
                                       final World world) {
        super(KORBFLECHTERIN, counterDao, n, world);
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

        alt.add(FLECHTEN.mit(np(INDEF, KOERBE)).alsSatzMitSubjekt(anaph(false)));

        if (loadSC().memoryComp().isKnown(KORBFLECHTERIN)) {
            alt.add(neuerSatz(anaph(false).nomK(),
                    "ist fleißig dabei, einen Korb zu flechten"));
        }

        n.narrateAlt(alt, secs(30));

        world.narrateAndUpgradeScKnownAndAssumedState(getGameObjectId());
    }

    // FIXME (?) "Die Korbflechterin hat ihren Stand gut verschnürt und abgedeckt"

    // FIXME "Der Frau bei ihrer Arbeit zusehen"

    // FIXME "Du wendest dich der alten Frau zu. Sie arbeitet an einem Korb. Du siehst ihr
    //  genau dabei zu: Sie verdreht immer drei Stränge von Binsen zu einem
    //  Seil, das legt sie dann immer wieder im Kreis, bis daraus ein großes Korb entsteht.

    // FIXME -> "Dich der alten Frau / alten Korbflechterin zuwenden"
    //  Gespräch
    //  - Man könnte sie fragen - sie hat kein Seil mehr, das lang genug ist?!
    //  - man sieht ihr zu (beobachtet sie bei ihrer Tätigkeit) und lernt es dabei?!
    //  ("du schaust ihr genau dabei zu. So schwer sieht
    //   es eigentlich gar nicht aus. Man dreht drei Binsenhalme zusammen... und dann
    //   nimmt man
    //   wieder...  Mh-hm,  gut zu wissen!")

    // FIXME "Du siehst der alten Frau weiter bei der Arbeit zu."
    //  "Es ist ein Freude, der fleißigen Alten zuzusehen."
    //  "Eine langwierige Arbeit!"

    // FIXME "Du schaust, was es sonst noch auf dem Markt zu sehen gibt"

    // FIXME "Dann wendest du dich wieder der alten Korbflechterin zu"....

    @Override
    public void onTimePassed(final Change<AvDateTime> change) {
        // FIXME Orte und ggf. assumptions aktualiseren

        // FIXME "Die Korbflechterin deckt ihren Stand ab und verschnürt alles gut"

        // FIXME  eine alte Frau flicht Körbe

        // FIXME "Die Frau / Alte / Korbflechterin ist fleißig dabei, einen Korb zu flechten"

        // FIXME "Die Korbflechterin hat ihren Stand gut verschnürt und abgedeckt"

        // FIXME "Auch die dicke Bäuerin ist nicht mehr da. Die Menschen haben sich
        //  verlaufen, und
        //  du stehst allein zwischen den leeren Bänken und Ständen"

        // FIXME "..verlässt den Markt"
    }
}
