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
// FIXME Man sollte die speziellen ReactionComps etc. besser zu Welt verschieben, so
//  dass alles, was mit der Korbflechterin zu tun hat, in einem Verzeichnis zu liegen kommt.
//  (Das ist ein Schritt zur Trennung von Welt und Framework).
public class KorbflechterinReactionsComp extends AbstractDescribableReactionsComp
        implements IMovementReactions {
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
        // FIXME  eine alte Frau flicht Körbe

        // FIXME "Die Frau / Alte / Korbflechterin ist fleißig dabei, einen Korb zu flechten"

        // FIXME "Die Korbflechterin hat ihren Stand gut verschnürt und abgedeckt"

        // FIXME "Auch die dicke Bäuerin ist nicht mehr da. Die Menschen haben sich
        //  verlaufen, und
        //  du stehst allein zwischen den leeren Bänken und Ständen"

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
    }
}
