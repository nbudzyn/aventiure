package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.base.GameObjectId;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.description.AbstractDescriptionComp;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.time.AvTimeSpan;
import de.nb.aventiure2.german.base.Nominalphrase;

import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.VOR_DEM_ALTEN_TURM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.SINGEND;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.noTime;
import static de.nb.aventiure2.data.world.time.AvTimeSpan.secs;
import static de.nb.aventiure2.german.base.AllgDescription.neuerSatz;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;

/**
 * Component for {@link World#RAPUNZEL}: Der Spieler
 * kann mit Rapunzel im Gespräch sein (dann auch umgekehrt).
 */
public class RapunzelTalkingComp extends AbstractTalkingComp {
    private final AbstractDescriptionComp descriptionComp;
    private final RapunzelStateComp stateComp;

    public RapunzelTalkingComp(final AvDatabase db,
                               final World world,
                               final AbstractDescriptionComp descriptionComp,
                               final RapunzelStateComp stateComp) {
        super(RAPUNZEL, db, world);
        this.descriptionComp = descriptionComp;
        this.stateComp = stateComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        return ImmutableList.of();
    }

    public AvTimeSpan reactToRapunzelruf(final GameObjectId rufer) {
        if (stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            return noTime();
        }

        AvTimeSpan extraTime = noTime();

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
            final Nominalphrase desc = getDescription(true);

            if (stateComp.hasState(SINGEND)) {
                extraTime = extraTime.plus(n.add(
                        neuerSatz(SENTENCE,
                                "Sofort hört der Gesang auf – und gleich darauf fallen "
                                        + "aus dem kleinen "
                                        + "Fenster oben im Turm lange, goldene Haarzöpfe herab, sicher "
                                        + "zwanzig Ellen tief bis auf den Boden. ", secs(30))));
            } else {
                extraTime = extraTime.plus(n.add(
                        neuerSatz(SENTENCE, "Gleich darauf fallen aus dem kleinen "
                                + "Fenster oben im Turm lange, goldene Haarzöpfe herab, sicher "
                                + "zwanzig Ellen tief bis auf den Boden. ", secs(30))));
            }
        }

        stateComp.setState(HAARE_VOM_TURM_HERUNTERGELASSEN);

        return extraTime;
    }
}
