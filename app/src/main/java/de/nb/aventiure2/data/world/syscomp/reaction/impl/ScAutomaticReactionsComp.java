package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.time.*;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.HUNGRIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.description.DuDescriptionBuilder.du;

/**
 * "Automatische" Reaktionen des Spielercharakters, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Spielercharakter wird hungrig.)
 */
public class ScAutomaticReactionsComp
        extends AbstractReactionsComp
        implements ITimePassedReactions {
    private final FeelingsComp feelingsComp;

    public ScAutomaticReactionsComp(final AvDatabase db, final World world,
                                    final FeelingsComp feelingsComp) {
        super(SPIELER_CHARAKTER, db, world);
        this.feelingsComp = feelingsComp;
    }

    @Override
    public void onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        final AvDateTime wiederHungrigAb = feelingsComp.getWiederHungrigAb();
        if (now.isEqualOrAfter(wiederHungrigAb)) {
            feelingsComp.setHunger(HUNGRIG);

            if (lastTime.isBefore(wiederHungrigAb)) {
                scWirdHungrig();
            }
        }
    }

    private void scWirdHungrig() {
        n.narrateAlt(
                du(PARAGRAPH, "fühlst", "dich allmählich etwas hungrig",
                        noTime())
                        .undWartest(),
                neuerSatz("Wann hast du eigentlich zuletzt etwas gegessen? Das "
                                + "muss schon eine Weile her sein.",
                        noTime()),
                du(PARAGRAPH, "bekommst", "so langsam Hunger",
                        "so langsam",
                        noTime()),
                neuerSatz(PARAGRAPH, "Allmählich überkommt dich der Hunger",
                        noTime()),
                neuerSatz(PARAGRAPH, "Allmählich regt sich wieder der Hunger",
                        noTime())
                        .undWartest(),
                neuerSatz("Dir fällt auf, dass du Hunger hast",
                        noTime())
                        .komma(),
                du("empfindest", "wieder leichten Hunger",
                        noTime())
                        .undWartest()
        );
    }
}
