package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.reaction.impl.RapunzelReactionsComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;

import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.*;
import static de.nb.aventiure2.german.base.StructuralElement.PARAGRAPH;
import static de.nb.aventiure2.german.description.AllgDescription.neuerSatz;

/**
 * Component for {@link World#RAPUNZEL}: Der Spieler
 * kann mit Rapunzel im Gespräch sein (dann auch umgekehrt).
 */
public class RapunzelTalkingComp extends AbstractTalkingComp {
    private final RapunzelStateComp stateComp;
    private final RapunzelReactionsComp reactionsComp;

    public RapunzelTalkingComp(final AvDatabase db,
                               final World world,
                               final RapunzelStateComp stateComp,
                               final RapunzelReactionsComp reactionsComp) {
        super(RAPUNZEL, db, world);
        this.stateComp = stateComp;
        this.reactionsComp = reactionsComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        return ImmutableList.of(
                SCTalkAction.entrySt(
                        () -> !haareSindHeruntergelassen(),
                        this::haareHerunterlassenBitte)
        );
    }

    private boolean haareSindHeruntergelassen() {
        return stateComp.hasState(RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN);
    }

    private void haareHerunterlassenBitte() {
        n.addAlt(
                // STORY Nur, wenn SC und Rapunzel sich noch nicht gut kennen
                neuerSatz(PARAGRAPH,
                        "„Ich wollte euch nicht belästigen“, sprichst du "
                                + getAnaphPersPronWennMglSonstShortDescription().akk()
                                + " an, "
                                + "„lasst mich wieder hinunter und ich lasse euch euren Frieden.“",
                        secs(10))
                        .beendet(PARAGRAPH)
                //  STORY "Oh, ich wünschte, ihr könntet noch einen Moment bleiben!"
                //   antwortet RAPUNZEL.
        );

        haareHerunterlassen();
    }

    private void haareHerunterlassen() {
        reactionsComp.rapunzelLaesstHaareZumAbstiegHerunter();

        unsetTalkingTo();
    }
}
