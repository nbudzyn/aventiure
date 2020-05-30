package de.nb.aventiure2.data.world.syscomp.reaction.impl;

import androidx.annotation.NonNull;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.AbstractReactionsComp;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.ITimePassedReactions;
import de.nb.aventiure2.data.world.time.AvDateTime;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobjects.GameObjects.SPIELER_CHARAKTER;
import static de.nb.aventiure2.data.world.syscomp.feelings.Hunger.HUNGRIG;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.DuDescription.du;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * "Automatische" Reaktionen des Spielercharakters, z.B. darauf, dass Zeit vergeht.
 * (Z.B.: Spielercharakter wird hungrig.)
 */
public class ScAutomaticReactionsComp
        extends AbstractReactionsComp
        implements ITimePassedReactions {
    private final FeelingsComp feelingsComp;

    public ScAutomaticReactionsComp(final AvDatabase db, final FeelingsComp feelingsComp) {
        super(SPIELER_CHARAKTER, db);
        this.feelingsComp = feelingsComp;
    }

    @Override
    public AvTimeSpan onTimePassed(final AvDateTime lastTime, final AvDateTime now) {
        AvTimeSpan timeElapsed = noTime();

        final AvDateTime wiederHungrigAb = feelingsComp.getWiederHungrigAb();
        if (!now.isBefore(wiederHungrigAb)) {
            feelingsComp.setHunger(HUNGRIG);

            if (lastTime.isBefore(wiederHungrigAb)) {
                timeElapsed = timeElapsed.plus(scWirdHungrig());
            }
        }

        return timeElapsed;
    }

    @NonNull
    private AvTimeSpan scWirdHungrig() {
        return n.addAlt(
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
                neuerSatz(SENTENCE, "Dir fällt auf, dass du Hunger hast",
                        noTime())
                        .komma(),
                du("empfindest", "wieder leichten Hunger",
                        noTime())
                        .undWartest()
        );
    }
}
