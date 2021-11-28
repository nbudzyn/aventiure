package de.nb.aventiure2.data.world.syscomp.state.impl;

import static de.nb.aventiure2.data.time.AvTimeSpan.mins;
import static de.nb.aventiure2.data.world.gameobject.World.*;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.AM_BAUM;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.GESAMMELT;
import static de.nb.aventiure2.data.world.syscomp.state.impl.HolzFuerStrickleiterState.IN_STUECKE_GEBROCHEN;
import static de.nb.aventiure2.german.adjektiv.AdjektivOhneErgaenzungen.HANDLICH;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.ETWAS;
import static de.nb.aventiure2.german.base.ArtikelwortFlexionsspalte.Typ.INDEF;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.STUECKE;
import static de.nb.aventiure2.german.base.NomenFlexionsspalte.ZEIT;
import static de.nb.aventiure2.german.base.Nominalphrase.np;
import static de.nb.aventiure2.german.base.PraepositionMitKasus.IN_AKK;
import static de.nb.aventiure2.german.praedikat.ReflVerbSubjObj.SICH_NEHMEN;
import static de.nb.aventiure2.german.praedikat.VerbSubjObj.BRECHEN;
import static de.nb.aventiure2.german.string.GermanStringUtil.capitalize;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import de.nb.aventiure2.data.database.AvDatabase;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.state.AbstractStateComp;
import de.nb.aventiure2.data.world.syscomp.state.StateModification;
import de.nb.aventiure2.german.description.DescriptionBuilder;
import de.nb.aventiure2.german.description.TimedDescription;
import de.nb.aventiure2.german.praedikat.AdvAngabeSkopusVerbAllg;
import de.nb.aventiure2.german.praedikat.ZweiPraedikateOhneLeerstellenSem;

public class HolzFuerStrickleiterStateComp extends AbstractStateComp<HolzFuerStrickleiterState> {
    public HolzFuerStrickleiterStateComp(final AvDatabase db,
                                         final TimeTaker timeTaker,
                                         final World world) {
        super(HOLZ_FUER_STRICKLEITER, db, timeTaker, world,
                AM_BAUM);
    }

    @Override
    public ImmutableList<StateModification<HolzFuerStrickleiterState>> getScStateModificationData() {
        if (hasState(GESAMMELT)) {
            return ImmutableList.of(
                    new StateModification<>(capitalize(
                            BRECHEN.mit(getDescription(textContext, possessivDescriptionVorgabe,
                                    true))
                                    .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                            IN_AKK.mit(np(INDEF, STUECKE))))
                                    .getInfinitiv(textContext, duSc()).joinToString()),
                            this::altTimedDescriptionsAndDo_holzZerbrechen,
                            IN_STUECKE_GEBROCHEN)
            );
        }

        return ImmutableList.of();
    }

    private ImmutableCollection<TimedDescription<?>>
    altTimedDescriptionsAndDo_holzZerbrechen() {
        return ImmutableList.of(
                DescriptionBuilder.du(
                        new ZweiPraedikateOhneLeerstellenSem(
                                SICH_NEHMEN.mit(np(ETWAS, ZEIT)),
                                BRECHEN.mit(
                                        getDescription(textContext, possessivDescriptionVorgabe))
                                        .mitAdvAngabe(new AdvAngabeSkopusVerbAllg(
                                                IN_AKK.mit(np(INDEF, HANDLICH, STUECKE))))))
                        .timed(mins(10))
                        .dann());
    }
}
