package de.nb.aventiure2.data.world.syscomp.talking.impl;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.world.gameobject.World;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelStateComp;
import de.nb.aventiure2.data.world.syscomp.talking.AbstractTalkingComp;
import de.nb.aventiure2.data.world.time.AvTimeSpan;

import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL;
import static de.nb.aventiure2.data.world.gameobject.World.RAPUNZEL_HAAR_TRICK;
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
    private final RapunzelStateComp stateComp;

    public RapunzelTalkingComp(final AvDatabase db,
                               final World world,
                               final RapunzelStateComp stateComp) {
        super(RAPUNZEL, db, world);
        this.stateComp = stateComp;
    }

    @Override
    protected Iterable<SCTalkAction> getSCTalkActionsWithoutCheckingConditions() {
        if (!loadSC().memoryComp().isKnown(RAPUNZEL_HAAR_TRICK)) {
            return ImmutableList.of();
        }

        return ImmutableList.of(
                // STORY Von VOR_DEM_ALTEN_TURM kann der SC
                //  nach OBEN_IM_ALTEN_TURM rufen.
                //  Das ist sehr speziell: Eine RedenAction
                //  über die Grenzen eines Raums hinaus!
                //  Idee: Eine upper-most location kann sagen,
                //  welche anderen Locations auch für RedenActions
                //  zur Verfügung stehen.
        );
    }

    public AvTimeSpan reactToRapunzelruf() {
        if (stateComp.hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
            return noTime();
        }

        AvTimeSpan extraTime = noTime();

        if (loadSC().locationComp().hasRecursiveLocation(VOR_DEM_ALTEN_TURM)) {
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

        return extraTime.plus(
                stateComp.narrateAndSetState(HAARE_VOM_TURM_HERUNTERGELASSEN));
    }
}
