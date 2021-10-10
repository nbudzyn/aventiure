package de.nb.aventiure2.scaction.impl;

import static com.google.common.collect.ImmutableList.builder;
import static de.nb.aventiure2.data.time.AvTimeSpan.secs;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp.LASS_DEIN_HAAR_HERUNTER;
import static de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState.HAARE_VOM_TURM_HERUNTERGELASSEN;
import static de.nb.aventiure2.german.base.Konstituentenfolge.joinToKonstituentenfolge;
import static de.nb.aventiure2.german.base.NebenordnendeEinteiligeKonjunktionImLinkenAussenfeld.UND;
import static de.nb.aventiure2.german.base.Numerus.SG;
import static de.nb.aventiure2.german.base.Person.P2;
import static de.nb.aventiure2.german.base.StructuralElement.SENTENCE;
import static de.nb.aventiure2.german.base.StructuralElement.WORD;
import static de.nb.aventiure2.german.description.DescriptionBuilder.du;
import static de.nb.aventiure2.german.description.DescriptionUmformulierer.drueckeAus;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.VERSTEHT_SICH_VON_SELBST;
import static de.nb.aventiure2.german.description.Kohaerenzrelation.WIEDERHOLUNG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.memory.Action;
import de.nb.aventiure2.data.world.syscomp.reaction.interfaces.Ruftyp;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.data.world.syscomp.state.IHasStateGO;
import de.nb.aventiure2.data.world.syscomp.state.impl.RapunzelState;
import de.nb.aventiure2.data.world.syscomp.storingplace.ILocationGO;
import de.nb.aventiure2.german.description.DescriptionBuilder;
import de.nb.aventiure2.german.description.Kohaerenzrelation;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

/**
 * Der Spielercharakter ruft / stößt einen Ruf aus.
 */
public class RufenAction extends AbstractScAction {
    private final Ruftyp ruftyp;

    public static ImmutableList<AbstractScAction> buildActions(
            final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world,
            @NonNull final ILocationGO location) {
        final ImmutableList.Builder<AbstractScAction> res = builder();

        if (world.loadSC().memoryComp().isKnown(RAPUNZELRUF)) {
            if (world.isOrHasRecursiveLocation(location, VOR_DEM_ALTEN_TURM) &&
                    !world.<IHasStateGO<RapunzelState>>loadRequired(RAPUNZEL).stateComp()
                            .hasState(HAARE_VOM_TURM_HERUNTERGELASSEN)) {
                res.add(new RufenAction(scActionStepCountDao, timeTaker, n, world,
                        LASS_DEIN_HAAR_HERUNTER));
            }
        }

        return res.build();
    }

    private RufenAction(final SCActionStepCountDao scActionStepCountDao,
                        final TimeTaker timeTaker, final Narrator n, final World world,
                        final Ruftyp ruftyp) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.ruftyp = ruftyp;
    }

    @Override
    public String getType() {
        return "actionRedenRufen";
    }

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    @Override
    @NonNull
    public String getName() {
        return joinToKonstituentenfolge(
                SENTENCE,
                ruftyp.getName().getInfinitiv(P2, SG))
                .joinToString();
    }

    @Override
    public void narrateAndDo() {
        final Kohaerenzrelation kohaerenzrelation =
                sc.memoryComp().getLastAction().is(Action.Type.RUFEN) ?
                        WIEDERHOLUNG : VERSTEHT_SICH_VON_SELBST;

        if (kohaerenzrelation != VERSTEHT_SICH_VON_SELBST
                || n.allowsAdditionalDuSatzreihengliedOhneSubjekt()) {
            n.narrateAlt(drueckeAus(kohaerenzrelation, false,
                    du(ruftyp.getName())),
                    secs(30));
        } else {
            n.narrate(DescriptionBuilder.satz(WORD, ruftyp.getName()
                    .alsSatzMitSubjekt(duSc())
                    .mitAnschlusswort(UND))
                    .timed(secs(30)));
        }

        world.narrateAndDoReactions().onRuf(sc, Ruftyp.LASS_DEIN_HAAR_HERUNTER);

        sc.memoryComp().setLastAction(buildMemorizedAction());
    }

    @Override
    protected boolean isDefinitivWiederholung() {
        // Der Ruf-Typ wird derzeit nicht in der Aktion gespeichert (es gibt kein
        // Feld passenden Typs dafür, er hat ja keine GameObjectId).
        return false;
    }

    @Override
    protected boolean isDefinitivFortsetzung() {
        return false;
    }

    @Override
    protected boolean isDefinitivDiskontinuitaet() {
        return false;
    }

    @NonNull
    private static Action buildMemorizedAction() {
        return new Action(Action.Type.RUFEN);
    }
}
