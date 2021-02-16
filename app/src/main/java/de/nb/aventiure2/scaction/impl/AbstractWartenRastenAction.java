package de.nb.aventiure2.scaction.impl;

import androidx.annotation.Nullable;

import de.nb.aventiure2.data.narration.Narrator;
import de.nb.aventiure2.data.time.AvTimeSpan;
import de.nb.aventiure2.data.time.TimeTaker;
import de.nb.aventiure2.data.world.counter.CounterDao;
import de.nb.aventiure2.data.world.gameobject.*;
import de.nb.aventiure2.data.world.syscomp.feelings.FeelingIntensity;
import de.nb.aventiure2.data.world.syscomp.spatialconnection.CardinalDirection;
import de.nb.aventiure2.scaction.AbstractScAction;
import de.nb.aventiure2.scaction.stepcount.SCActionStepCountDao;

import static de.nb.aventiure2.scaction.impl.AbstractWartenRastenAction.Counter.WARTEN_ODER_RASTEN_IN_FOLGE;

public abstract class AbstractWartenRastenAction extends AbstractScAction {
    @SuppressWarnings({"unused", "RedundantSuppression"})
    enum Counter {
        WARTEN_ODER_RASTEN_IN_FOLGE
    }

    protected final CounterDao counterDao;

    AbstractWartenRastenAction(
            final CounterDao counterDao, final SCActionStepCountDao scActionStepCountDao,
            final TimeTaker timeTaker,
            final Narrator n, final World world) {
        super(scActionStepCountDao, timeTaker, n, world);
        this.counterDao = counterDao;
    }

    @Override
    public String getType() {
        return "actionRastenWarten";
    }

    @Nullable
    @Override
    public CardinalDirection getCardinalDirection() {
        return null;
    }

    boolean automatischesEinschlafen() {
        if (counterDao.get(WARTEN_ODER_RASTEN_IN_FOLGE) >= 3) {
            return sc.feelingsComp().getMuedigkeit() >= FeelingIntensity.MERKLICH;
        }

        return sc.feelingsComp().getMuedigkeit() >= FeelingIntensity.STARK;
    }

    void narrateAndDoSchlafen() {
        final AvTimeSpan schlafdauer = sc.feelingsComp().calcSchlafdauerMensch();

        narrateAndDoEinschlafen(schlafdauer);
        sc.feelingsComp().narrateAndDoAufwachenSC(schlafdauer, true);
    }

    protected abstract void narrateAndDoEinschlafen(AvTimeSpan schlafdauer);
}
